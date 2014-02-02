package open.dolphin.session;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import open.dolphin.infomodel.AppointmentModel;
import open.dolphin.infomodel.HealthInsuranceModel;
import open.dolphin.infomodel.KarteBean;
import open.dolphin.infomodel.ModelUtils;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.infomodel.PatientVisitModel;
//import org.apache.log4j.Logger;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
@Stateless
public class PVTServiceBean implements PVTServiceBeanLocal {

    private static final String QUERY_PATIENT_BY_FID_PID        = "from PatientModel p where p.facilityId=:fid and p.patientId=:pid";
    private static final String QUERY_PVT_BY_FID_PID_DATE       = "from PatientVisitModel p where p.facilityId=:fid and p.pvtDate >= :date and p.patient.patientId=:pid";
    private static final String QUERY_PVT_BY_FID_DATE           = "from PatientVisitModel p where p.facilityId=:fid and p.pvtDate >= :date order by p.pvtDate";
    private static final String QUERY_PVT_BY_FID_DID_DATE       = "from PatientVisitModel p where p.facilityId=:fid and p.pvtDate >= :date and (doctorId=:did or doctorId=:unassigned) order by p.pvtDate";
    private static final String QUERY_INSURANCE_BY_PATIENT_ID   = "from HealthInsuranceModel h where h.patient.id=:id";
    private static final String QUERY_KARTE_BY_PATIENT_ID       = "from KarteBean k where k.patient.id=:id";
    private static final String QUERY_APPO_BY_KARTE_ID_DATE     = "from AppointmentModel a where a.karte.id=:id and a.date=:date";

    private static final String FID = "fid";
    private static final String PID = "pid";
    private static final String DID = "did";
    private static final String UNASSIGNED = "unassigned";
    private static final String ID = "id";
    private static final String DATE = "date";
    private static final String PERCENT = "%";
    private static final int BIT_SAVE_CLAIM     = 1;
    private static final int BIT_MODIFY_CLAIM   = 2;
    private static final int BIT_CANCEL         = 6;

    @PersistenceContext
    private EntityManager em;

    /**
     * 患者来院情報を登録する。
     * @param spec 来院情報を保持する DTO オブジェクト
     * @return 登録個数
     */
    @Override
    public int addPvt(PatientVisitModel pvt) {

        PatientModel patient = pvt.getPatientModel();
        String fid = pvt.getFacilityId();

        //--------------------------------------------
        // 二重登録をチェックする
        //--------------------------------------------
        try {
            List<PatientVisitModel> list = (List<PatientVisitModel>)em
                    .createQuery(QUERY_PVT_BY_FID_PID_DATE)
                    .setParameter(FID, fid)
                    .setParameter(DATE, pvt.getPvtDate())
                    .setParameter(PID, patient.getPatientId())
                    .getResultList();
            if (!list.isEmpty()) {
                for (PatientVisitModel doubleEntry : list) {
                    em.remove(doubleEntry);
                }
            }

        } catch (Exception te) {
            Logger.getLogger("open.dolphin").fine(te.getMessage());
            return 0;
        }

        // 既存の患者かどうか調べる
        try {
            PatientModel exist = (PatientModel) em
                    .createQuery(QUERY_PATIENT_BY_FID_PID)
                    .setParameter(FID, fid)
                    .setParameter(PID, patient.getPatientId())
                    .getSingleResult();

            //-----------------------------
            // 健康保険情報を更新する
            //-----------------------------
            Collection<HealthInsuranceModel> ins = patient.getHealthInsurances();
            if (ins != null && ins.size() > 0) {

                // 健康保険を更新する
                Collection old = em.createQuery(QUERY_INSURANCE_BY_PATIENT_ID)
                .setParameter(ID, exist.getId())
                .getResultList();

                // 現在の保険情報を削除する
                for (Iterator iter = old.iterator(); iter.hasNext(); ) {
                    HealthInsuranceModel model = (HealthInsuranceModel) iter.next();
                    em.remove(model);
                }

                // 新しい健康保険情報を登録する
                Collection<HealthInsuranceModel> newOne = patient.getHealthInsurances();
                for (HealthInsuranceModel model : newOne) {
                    model.setPatient(exist);
                    em.persist(model);
                }
            }

            // 名前を更新する 2007-04-12
            exist.setFamilyName(patient.getFamilyName());
            exist.setGivenName(patient.getGivenName());
            exist.setFullName(patient.getFullName());
            exist.setKanaFamilyName(patient.getKanaFamilyName());
            exist.setKanaGivenName(patient.getKanaGivenName());
            exist.setKanaName(patient.getKanaName());
            exist.setRomanFamilyName(patient.getRomanFamilyName());
            exist.setRomanGivenName(patient.getRomanGivenName());
            exist.setRomanName(patient.getRomanName());

            // 性別
            exist.setGender(patient.getGender());
            exist.setGenderDesc(patient.getGenderDesc());
            exist.setGenderCodeSys(patient.getGenderCodeSys());

            // Birthday
            exist.setBirthday(patient.getBirthday());

            // 住所、電話を更新する
            exist.setSimpleAddressModel(patient.getSimpleAddressModel());
            exist.setTelephone(patient.getTelephone());
            //exist.setMobilePhone(patient.getMobilePhone());

            // PatientVisit との関係を設定する
            pvt.setPatientModel(exist);

        } catch (NoResultException e) {
            // 新規患者であれば登録する
            // 患者属性は cascade=PERSIST で自動的に保存される
            em.persist(patient);

            // この患者のカルテを生成する
            KarteBean karte = new KarteBean();
            karte.setPatientModel(patient);
            karte.setCreated(new Date());
            em.persist(karte);
        }

        // 来院情報を登録する
        // CLAIM の仕様により患者情報のみを登録し、来院情報はない場合がある
        // それを pvtDate の属性で判断している
        if (pvt.getPvtDate() != null) {
            em.persist(pvt);
        }

        return 1;
    }

    /**
     * 施設の患者来院情報を取得する。
     * @param spec 検索仕様DTOオブジェクト
     * @return 来院情報のCollection
     */
    @Override
    public List<PatientVisitModel> getPvt(String fid, String date, int firstResult, String appoDateFrom, String appoDateTo) {

        if (!date.endsWith(PERCENT)) {
            date += PERCENT;
        }
        
        // PatientVisitModelを施設IDで検索する
        List<PatientVisitModel> result =
                (List<PatientVisitModel>) em.createQuery(QUERY_PVT_BY_FID_DATE)
                              .setParameter(FID, fid)
                              .setParameter(DATE, date)
                              .setFirstResult(firstResult)
                              .getResultList();

        int len = result.size();

        if (len == 0) {
            return result;
        }

        int index = date.indexOf(PERCENT);
        Date theDate = ModelUtils.getDateAsObject(date.substring(0, index));

        boolean searchAppo = (appoDateFrom != null && appoDateTo != null) ? true : false;

        // 来院情報と患者は ManyToOne の関係である
        for (int i = 0; i < len; i++) {
            //for (int i = firstResult; i < len; i++) {
            
            PatientVisitModel pvt = result.get(i);
            PatientModel patient = pvt.getPatientModel();

            // 患者の健康保険を取得する
            List<HealthInsuranceModel> insurances = (List<HealthInsuranceModel>)em.createQuery(QUERY_INSURANCE_BY_PATIENT_ID)
            .setParameter(ID, patient.getId()).getResultList();
            patient.setHealthInsurances(insurances);

            // 予約を検索する
            if (searchAppo) {
                KarteBean karte = (KarteBean)em.createQuery(QUERY_KARTE_BY_PATIENT_ID)
                .setParameter(ID, patient.getId())
                .getSingleResult();
                // カルテの PK を得る
                long karteId = karte.getId();

                List c = em.createQuery(QUERY_APPO_BY_KARTE_ID_DATE)
                .setParameter(ID, karteId)
                .setParameter(DATE, theDate)
                .getResultList();
                //System.err.println("appo size = " + c.size());
                if (c != null && c.size() > 0) {
                    // 当日の予約で最初のもの
                    AppointmentModel appo = (AppointmentModel) c.get(0);
                    pvt.setAppointment(appo.getName());
                }
            }
        }

        return result;
    }

    @Override
    public List<PatientVisitModel> getPvt(String fid, String did, String unassigned, String date, int firstResult, String appoDateFrom, String appoDateTo) {

        if (!date.endsWith(PERCENT)) {
            date += PERCENT;
        }

        // PatientVisitModelを施設IDで検索する
        List<PatientVisitModel> result =
                (List<PatientVisitModel>) em.createQuery(QUERY_PVT_BY_FID_DID_DATE)
                              .setParameter(FID, fid)
                              .setParameter(DID, did)
                              .setParameter(UNASSIGNED, unassigned)
                              .setParameter(DATE, date)
                              .setFirstResult(firstResult)
                              .getResultList();

        int len = result.size();

        if (len == 0) {
            return result;
        }

        int index = date.indexOf(PERCENT);
        Date theDate = ModelUtils.getDateAsObject(date.substring(0, index));

        boolean searchAppo = (appoDateFrom != null && appoDateTo != null) ? true : false;

        // 来院情報と患者は ManyToOne の関係である
        for (int i = 0; i < len; i++) {
            //for (int i = firstResult; i < len; i++) {

            PatientVisitModel pvt = result.get(i);
            PatientModel patient = pvt.getPatientModel();

            // 患者の健康保険を取得する
            List<HealthInsuranceModel> insurances = (List<HealthInsuranceModel>)em.createQuery(QUERY_INSURANCE_BY_PATIENT_ID)
            .setParameter(ID, patient.getId()).getResultList();
            patient.setHealthInsurances(insurances);

            // 予約を検索する
            if (searchAppo) {
                KarteBean karte = (KarteBean)em.createQuery(QUERY_KARTE_BY_PATIENT_ID)
                .setParameter(ID, patient.getId())
                .getSingleResult();
                // カルテの PK を得る
                long karteId = karte.getId();

                List c = em.createQuery(QUERY_APPO_BY_KARTE_ID_DATE)
                .setParameter(ID, karteId)
                .setParameter(DATE, theDate)
                .getResultList();
                //System.err.println("appo size = " + c.size());
                if (c != null && c.size() > 0) {
                    // 当日の予約で最初のもの
                    AppointmentModel appo = (AppointmentModel) c.get(0);
                    pvt.setAppointment(appo.getName());
                }
            }
        }

        return result;
    }

    /**
     * 受付情報を削除する。
     * @param id 受付レコード
     * @return 削除件数
     */
    @Override
    public int removePvt(long id) {
        PatientVisitModel exist = (PatientVisitModel) em.find(PatientVisitModel.class, new Long(id));
        em.remove(exist);
        return 1;
    }

    /**
     * 診察終了情報を書き込む。
     * @param pk レコードID
     * @param state 診察終了の時 1
     */
    @Override
    public int updatePvtState(long pk, int state) {

        PatientVisitModel exist = (PatientVisitModel) em.find(PatientVisitModel.class, new Long(pk));

        if (state == 2 || state == 4) {
            exist.setState(state);
            return 1;
        }

        int curState = exist.getState();
        boolean red = ((curState & (1<<BIT_SAVE_CLAIM))!=0);
        boolean yellow = ((curState & (1<<BIT_MODIFY_CLAIM))!=0);
        boolean cancel = ((curState & (1<<BIT_CANCEL))!=0);
        
        if (red || yellow || cancel) {
            // 変更不可
            return 0;
        }

        exist.setState(state);
        return 1;
    }

    /**
     * メモを更新する。
     * @param pk レコードID
     * @param memo メモ
     * @return 1
     */
    @Override
    public int updateMemo(long pk, String memo) {
        PatientVisitModel exist = (PatientVisitModel) em.find(PatientVisitModel.class, new Long(pk));
        exist.setMemo(memo);
        return 1;
    }
}
