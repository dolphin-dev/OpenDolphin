package open.dolphin.session;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import open.dolphin.infomodel.*;
import open.dolphin.mbean.ServletContextHolder;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
@Named
@Stateless
public class PVTServiceBean {

    private static final String QUERY_PATIENT_BY_FID_PID        = "from PatientModel p where p.facilityId=:fid and p.patientId=:pid";
    private static final String QUERY_PVT_BY_FID_PID_DATE       = "from PatientVisitModel p where p.facilityId=:fid and p.pvtDate like :date and p.patient.patientId=:pid";
    private static final String QUERY_PVT_BY_FID_DATE           = "from PatientVisitModel p where p.facilityId=:fid and p.pvtDate like :date order by p.pvtDate";
    private static final String QUERY_PVT_BY_FID_DID_DATE       = "from PatientVisitModel p where p.facilityId=:fid and p.pvtDate like :date and (doctorId=:did or doctorId=:unassigned) order by p.pvtDate";
    private static final String QUERY_INSURANCE_BY_PATIENT_ID   = "from HealthInsuranceModel h where h.patient.id=:id";
    private static final String QUERY_KARTE_BY_PATIENT_ID       = "from KarteBean k where k.patient.id=:id";
    private static final String QUERY_APPO_BY_KARTE_ID_DATE     = "from AppointmentModel a where a.karte.id=:id and a.date=:date";
    private static final String QUERY_PVT_BY_PK                 = "from PatientVisitModel p where p.id=:id";
//masuda^    
    private static final String QUERY_KARTE_ID_BY_PATIENT_ID    = "select k.id from KarteBean k where k.patient.id = :id";
    
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
    
    @Inject
    private ChartEventServiceBean eventServiceBean;
    
    @Inject
    private ServletContextHolder contextHolder;
    
    
   /**
     * 患者来院情報を登録する。
     * @param PatientVisitModel 来院情報を保持するPatientVisitModel
     * @return 登録個数
     */
    public int addPvt(PatientVisitModel pvt) {

        // CLAIM 送信の場合 facilityID がデータベースに登録されているものと異なる場合がある
        // 施設IDを認証にパスしたユーザの施設IDに設定する。
        String fid = pvt.getFacilityId();
        PatientModel patient = pvt.getPatientModel();
        pvt.setFacilityId(fid);
        patient.setFacilityId(fid);
        
        // 1.4との互換性のためdepartmentにも設定する
        StringBuilder sb = new StringBuilder();
        sb.append(pvt.getDeptName()).append(",");
        sb.append(pvt.getDeptCode()).append(",");
        sb.append(pvt.getDoctorName()).append(",");
        sb.append(pvt.getDoctorId()).append(",");
        sb.append(pvt.getJmariNumber()).append(",");
        pvt.setDepartment(sb.toString());

        // 既存の患者かどうか調べる
        try {
            // 既存の患者かどうか調べる。なければNoResultException
            PatientModel exist = (PatientModel) 
                    em.createQuery(QUERY_PATIENT_BY_FID_PID)
                    .setParameter(FID, fid)
                    .setParameter(PID, patient.getPatientId())
                    .getSingleResult();

            //-----------------------------
            // 健康保険情報を更新する
            //-----------------------------
            @SuppressWarnings("unchecked")
            List<HealthInsuranceModel> old =
                    em.createQuery(QUERY_INSURANCE_BY_PATIENT_ID)
                    .setParameter(ID, exist.getId())
                    .getResultList();
            
            // ORCAからpvtに乗ってやってきた保険情報を取得する。検索などからPVT登録したものには乗っかっていない
            List<HealthInsuranceModel> newOne = patient.getHealthInsurances();

            if (newOne != null && !newOne.isEmpty()) {
                // 現在の保険情報を削除する
                for (HealthInsuranceModel model : old) {
                    em.remove(model);
                }

                // 新しい健康保険情報を登録する
                for (HealthInsuranceModel model : newOne) {
                    model.setPatient(exist);
                    em.persist(model);
                }
                // 健康保険を新しいものに更新する
                exist.setHealthInsurances(newOne);
            } else {
                // pvtに保険情報が乗っかっていない場合は古いのを使う
                exist.setHealthInsurances(old);
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

            // PatientModelを新しい情報に更新する
            em.merge(exist);
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

        // ここからPVT登録処理
        
        // CLAIM の仕様により患者情報のみを登録し、来院情報はない場合がある
        // 来院情報を登録する。pvtDate == nullなら患者登録のみ
        if (pvt.getPvtDate() == null) {
            return 0;   // 追加０個、終了
        }
        
//minagawa^ 予約: ORCAで未来日受付の場合、persistしてリターン(予定カルテ対応)
        if (!isToday(pvt.getPvtDate())) {
            Logger.getLogger("open.dolphin").log(Level.INFO, "scheduled PVT: {0}", pvt.getPvtDate());
            // 2重登録をチェックする
            int index = pvt.getPvtDate().indexOf("T");
            String test = pvt.getPvtDate().substring(0, index);
            List<PatientVisitModel> list = (List<PatientVisitModel>)em
            .createQuery(QUERY_PVT_BY_FID_PID_DATE)
            .setParameter(FID, fid)
            .setParameter(DATE, test+PERCENT)
            .setParameter(PID, patient.getPatientId())
            .getResultList();
        
            if (list.isEmpty()) {
                // 受付がない場合
                em.persist(pvt);

            } else {
                // 最初のレコードを後から来たデータで上書きする
                PatientVisitModel target = list.get(0);
                target.setDepartment(pvt.getDepartment());
                target.setDeptCode(pvt.getDeptCode());
                target.setDeptName(pvt.getDeptName());
                target.setDoctorId(pvt.getDoctorId());
                target.setDoctorName(pvt.getDoctorName());
                target.setFirstInsurance(pvt.getFirstInsurance());
                target.setInsuranceUid(pvt.getInsuranceUid());
                target.setJmariNumber(pvt.getJmariNumber());
                // transient及び値が変更されないもの
                //target.setAppointment(pvt.getAppointment());
                //target.setFacilityId(pvt.getFacilityId());
                //target.setMemo(pvt.getMemo());
                //target.setNumber(pvt.getNumber());
                //target.setPatientModel(pvt.getPatientModel());
                //target.setPvtDate(pvt.getPvtDate());
                //target.setState(pvt.getState());
                //target.setWatingTime(pvt.getWatingTime());
            }
            return 1;
        }
//minagawa$

        // これ以降は今日の受付で排他制御がかかる
        
        // カルテの PK を得る
        long karteId = (Long)
                em.createQuery(QUERY_KARTE_ID_BY_PATIENT_ID)
                .setParameter(ID, pvt.getPatientModel().getId())
                .getSingleResult();
        // 予約を検索する
        @SuppressWarnings("unchecked")
        List<AppointmentModel> c =
                em.createQuery(QUERY_APPO_BY_KARTE_ID_DATE)
                .setParameter(ID, karteId)
                .setParameter(DATE, contextHolder.getToday().getTime())
                .getResultList();
        if (c != null && !c.isEmpty()) {
            AppointmentModel appo = c.get(0);
            pvt.setAppointment(appo.getName());
        }

        // 受付嬢にORCAの受付ボタンを連打されたとき用ｗ 復活！！
        List<PatientVisitModel> pvtList = eventServiceBean.getPvtList(fid);
        for (int i = 0; i < pvtList.size(); ++i) {
            PatientVisitModel test = pvtList.get(i);
            // pvt時刻が同じでキャンセルでないものは更新(merge)する
            if (test.getPvtDate().equals(pvt.getPvtDate()) 
                    && (test.getState() & (1<< PatientVisitModel.BIT_CANCEL)) ==0) {
                pvt.setId(test.getId());    // pvtId, state, ownerUUID, byomeiCountは既存のものを使う
                pvt.setState(test.getState());
                pvt.getPatientModel().setOwnerUUID(test.getPatientModel().getOwnerUUID());
                pvt.setByomeiCount(test.getByomeiCount());
                pvt.setByomeiCountToday(test.getByomeiCountToday());
                // データベースを更新
                em.merge(pvt);
                // 新しいもので置き換える
                pvtList.set(i, pvt);
                // クライアントに通知
                String uuid = contextHolder.getServerUUID();
                ChartEventModel msg = new ChartEventModel(uuid);
                msg.setParamFromPvt(pvt);
                msg.setPatientVisitModel(pvt);
                msg.setEventType(ChartEventModel.PVT_MERGE);
                eventServiceBean.notifyEvent(msg);
                return 0;   // 追加０個
            }
        }
        // 同じ時刻のPVTがないならばPVTをデータベースに登録(persist)する
        eventServiceBean.setByomeiCount(karteId, pvt);   // 病名数をカウントする
        em.persist(pvt);
        // pvtListに追加
        pvtList.add(pvt);    
        // クライアントに通知
        String uuid = contextHolder.getServerUUID();
        ChartEventModel msg = new ChartEventModel(uuid);
        msg.setParamFromPvt(pvt);
        msg.setPatientVisitModel(pvt);
        msg.setEventType(ChartEventModel.PVT_ADD);
        eventServiceBean.notifyEvent(msg);
        
        return 1;   // 追加１個
    } 
    
    /**
     * 引数の日付が今日かどうかを返す。
     * (予定カルテ対応)
     * @param mmlDate yyyy-MM-ddTHH:mm:ss
     * @return 今日の時 true
     */
    private boolean isToday(String mmlDate) {
        
        try {
            int index = mmlDate.indexOf("T");
            String test = mmlDate.substring(0, index);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String today = sdf.format(new Date());
            return test.equals(today);
        } catch (Exception e) {
        }
        
        return false;
    }

//    /**
//     * 患者来院情報を登録する。
//     * @param spec 来院情報を保持する DTO オブジェクト
//     * @return 登録個数
//     */
//    
//    public int addPvt(PatientVisitModel pvt) {
//
//        PatientModel patient = pvt.getPatientModel();
//        String fid = pvt.getFacilityId();
//        
//        // 2012-07
//        // ORCAの受付で、受付する保険や担当医を間違え、キャンセルなしに再受付した場合の処理を変更。
//        // 前のレコードを削除して新規に受付のレコードを生成すると、クライアントプログラムで削除した受付レコードを
//        // 保持し、カルテ保存後もステータスが更新されないケースがあった。
//        // 再受付の場合は最初のレコードを後からきたデータで上書きするようにした。
//
////        //--------------------------------------------
////        // 二重登録をチェックする
////        //--------------------------------------------
////        try {
////            List<PatientVisitModel> list = (List<PatientVisitModel>)em
////                    .createQuery(QUERY_PVT_BY_FID_PID_DATE)
////                    .setParameter(FID, fid)
////                    .setParameter(DATE, pvt.getPvtDate()+PERCENT)
////                    .setParameter(PID, patient.getPatientId())
////                    .getResultList();
////            if (!list.isEmpty()) {
////                for (PatientVisitModel doubleEntry : list) {
////                    em.remove(doubleEntry);
////                }
////            }
////
////        } catch (Exception te) {
////            return 0;
////        }
//
//        // 既存の患者かどうか調べる
//        try {
//            PatientModel exist = (PatientModel) em
//                    .createQuery(QUERY_PATIENT_BY_FID_PID)
//                    .setParameter(FID, fid)
//                    .setParameter(PID, patient.getPatientId())
//                    .getSingleResult();
//
//            //-----------------------------
//            // 健康保険情報を更新する
//            //-----------------------------
//            Collection<HealthInsuranceModel> ins = patient.getHealthInsurances();
//            if (ins != null && ins.size() > 0) {
//
//                // 健康保険を更新する
//                Collection old = em.createQuery(QUERY_INSURANCE_BY_PATIENT_ID)
//                .setParameter(ID, exist.getId())
//                .getResultList();
//
//                // 現在の保険情報を削除する
//                for (Iterator iter = old.iterator(); iter.hasNext(); ) {
//                    HealthInsuranceModel model = (HealthInsuranceModel) iter.next();
//                    em.remove(model);
//                }
//
//                // 新しい健康保険情報を登録する
//                Collection<HealthInsuranceModel> newOne = patient.getHealthInsurances();
//                for (HealthInsuranceModel model : newOne) {
//                    model.setPatient(exist);
//                    em.persist(model);
//                }
//            }
//
//            // 名前を更新する 2007-04-12
//            exist.setFamilyName(patient.getFamilyName());
//            exist.setGivenName(patient.getGivenName());
//            exist.setFullName(patient.getFullName());
//            exist.setKanaFamilyName(patient.getKanaFamilyName());
//            exist.setKanaGivenName(patient.getKanaGivenName());
//            exist.setKanaName(patient.getKanaName());
//            exist.setRomanFamilyName(patient.getRomanFamilyName());
//            exist.setRomanGivenName(patient.getRomanGivenName());
//            exist.setRomanName(patient.getRomanName());
//
//            // 性別
//            exist.setGender(patient.getGender());
//            exist.setGenderDesc(patient.getGenderDesc());
//            exist.setGenderCodeSys(patient.getGenderCodeSys());
//
//            // Birthday
//            exist.setBirthday(patient.getBirthday());
//
//            // 住所、電話を更新する
//            exist.setSimpleAddressModel(patient.getSimpleAddressModel());
//            exist.setTelephone(patient.getTelephone());
//            //exist.setMobilePhone(patient.getMobilePhone());
//
//            // PatientVisit との関係を設定する
//            pvt.setPatientModel(exist);
//
//        } catch (NoResultException e) {
//            // 新規患者であれば登録する
//            // 患者属性は cascade=PERSIST で自動的に保存される
//            em.persist(patient);
//
//            // この患者のカルテを生成する
//            KarteBean karte = new KarteBean();
//            karte.setPatientModel(patient);
//            karte.setCreated(new Date());
//            em.persist(karte);
//        }
////
////        // 来院情報を登録する
////        // CLAIM の仕様により患者情報のみを登録し、来院情報はない場合がある
////        // それを pvtDate の属性で判断している
////        if (pvt.getPvtDate() != null) {
////            em.persist(pvt);
////        }
//        
//        // 来院情報を登録する
//        // CLAIM の仕様により患者情報のみを登録し、来院情報はない場合がある
//        // それを pvtDate の属性で判断している
//        if (pvt.getPvtDate()==null) {
//            return 1;
//        }
//        
//        //------------------------------------------
//        // 既に同一患者同一時刻で受け付けがあるか ?
//        //------------------------------------------
//        List<PatientVisitModel> list = (List<PatientVisitModel>)em
//                    .createQuery(QUERY_PVT_BY_FID_PID_DATE)
//                    .setParameter(FID, fid)
//                    .setParameter(DATE, pvt.getPvtDate()+PERCENT)
//                    .setParameter(PID, patient.getPatientId())
//                    .getResultList();
//        
//        if (list.isEmpty()) {
//            // 受付がない場合
//            em.persist(pvt);
//            
//        } else {
//            // 最初のレコードを後から来たデータで上書きする
//            PatientVisitModel target = list.get(0);
//            target.setDepartment(pvt.getDepartment());
//            target.setDeptCode(pvt.getDeptCode());
//            target.setDeptName(pvt.getDeptName());
//            target.setDoctorId(pvt.getDoctorId());
//            target.setDoctorName(pvt.getDoctorName());
//            target.setFirstInsurance(pvt.getFirstInsurance());
//            target.setInsuranceUid(pvt.getInsuranceUid());
//            target.setJmariNumber(pvt.getJmariNumber());
//            // transient及び値が変更されないもの
//            //target.setAppointment(pvt.getAppointment());
//            //target.setFacilityId(pvt.getFacilityId());
//            //target.setMemo(pvt.getMemo());
//            //target.setNumber(pvt.getNumber());
//            //target.setPatientModel(pvt.getPatientModel());
//            //target.setPvtDate(pvt.getPvtDate());
//            //target.setState(pvt.getState());
//            //target.setWatingTime(pvt.getWatingTime());
//        }
//        
//        return 1;
//    }

    /**
     * 施設の患者来院情報を取得する。
     * @param spec 検索仕様DTOオブジェクト
     * @return 来院情報のCollection
     */
    
    public List<PatientVisitModel> getPvt(String fid, String date, int firstResult, String appoDateFrom, String appoDateTo) {

        if (!date.endsWith(PERCENT)) {
            date += PERCENT;
        }
        
        // PatientVisitModelを施設IDで検索する
        List<PatientVisitModel> result =
                (List<PatientVisitModel>) em.createQuery(QUERY_PVT_BY_FID_DATE)
                              .setParameter(FID, fid)
                              .setParameter(DATE, date+PERCENT)
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
                              .setParameter(DATE, date+PERCENT)
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
     * @param pvtPk, fid
     * @return 削除件数
     */
    public int removePvt(long id, String fid) {
        
        try {
            // データベースから削除
            PatientVisitModel exist = em.find(PatientVisitModel.class, id);
            // WatingListから開いていないとexist = nullなので。
            if (exist != null) {
                em.remove(exist);
            }

            // pvtListから削除
            List<PatientVisitModel> pvtList = eventServiceBean.getPvtList(fid);
            PatientVisitModel toRemove = null;
            for (PatientVisitModel model : pvtList) {
                if (model.getId() == id) {
                    toRemove = model;
                    break;
                }
            }
            if (toRemove != null) {
                pvtList.remove(toRemove);
                return 1;
            }
        } catch (Exception e) {
        }
        return 0;
    }

    /**
     * 受付情報を削除する。
     * @param id 受付レコード
     * @return 削除件数
     */
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
    
    public int updatePvtState(long pk, int state) {
        
        //PatientVisitModel exist = (PatientVisitModel) em.find(PatientVisitModel.class, new Long(pk));
        List<PatientVisitModel> list =  em
                .createQuery(QUERY_PVT_BY_PK)
                .setParameter(ID, pk)
                .getResultList();
        
        if (list.isEmpty()) {
            return 0;
        }
        
        PatientVisitModel exist = list.get(0);

        // 保存（CLAIM送信）==2 (bit=1)
        // 修正送信 == 4 (bit=2)
        if (state == 2 || state == 4) {
            exist.setState(state);
            em.flush();
            return 1;
        }

        int curState = exist.getState();
        boolean red = ((curState & (1<<BIT_SAVE_CLAIM))!=0);
        boolean yellow = ((curState & (1<<BIT_MODIFY_CLAIM))!=0);
        boolean cancel = ((curState & (1<<BIT_CANCEL))!=0);

        // 保存 | 修正 | キャンセル --> 変更不可
        if (red || yellow || cancel) {
            return 0;
        }

        exist.setState(state);
        em.flush();
        return 1;
    }

    /**
     * メモを更新する。
     * @param pk レコードID
     * @param memo メモ
     * @return 1
     */
    
    public int updateMemo(long pk, String memo) {
        PatientVisitModel exist = (PatientVisitModel) em.find(PatientVisitModel.class, new Long(pk));
        exist.setMemo(memo);
        return 1;
    }
}
