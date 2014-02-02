package open.dolphin.session;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import open.dolphin.infomodel.*;
import org.apache.log4j.Logger;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
@Stateless
public class PVTServiceBean implements PVTServiceBeanLocal {

    private static final String QUERY_PATIENT_BY_FID_PID        = "from PatientModel p where p.facilityId=:fid and p.patientId=:pid";
    private static final String QUERY_PVT_BY_FID_PID_DATE       = "from PatientVisitModel p where p.facilityId=:fid and p.pvtDate like :date and p.patient.patientId=:pid";
    private static final String QUERY_PVT_BY_FID_DATE           = "from PatientVisitModel p where p.facilityId=:fid and p.pvtDate like :date order by p.pvtDate";
    private static final String QUERY_PVT_BY_FID_DID_DATE       = "from PatientVisitModel p where p.facilityId=:fid and p.pvtDate like :date and (doctorId=:did or doctorId=:unassigned) order by p.pvtDate";
    private static final String QUERY_INSURANCE_BY_PATIENT_ID   = "from HealthInsuranceModel h where h.patient.id=:id";
    private static final String QUERY_KARTE_BY_PATIENT_ID       = "from KarteBean k where k.patient.id=:id";
    private static final String QUERY_APPO_BY_KARTE_ID_DATE     = "from AppointmentModel a where a.karte.id=:id and a.date=:date";
    private static final String QUERY_PVT_BY_PK                 = "from PatientVisitModel p where p.id=:id";
    
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
     * Š³Ò—ˆ‰@î•ñ‚ğ“o˜^‚·‚éB
     * @param spec —ˆ‰@î•ñ‚ğ•Û‚·‚é DTO ƒIƒuƒWƒFƒNƒg
     * @return “o˜^ŒÂ”
     */
    @Override
    public int addPvt(PatientVisitModel pvt) {

        PatientModel patient = pvt.getPatientModel();
        String fid = pvt.getFacilityId();

        //--------------------------------------------
        // “ñd“o˜^‚ğƒ`ƒFƒbƒN‚·‚é
        //--------------------------------------------
        try {
            List<PatientVisitModel> list = (List<PatientVisitModel>)em
                    .createQuery(QUERY_PVT_BY_FID_PID_DATE)
                    .setParameter(FID, fid)
                    .setParameter(DATE, pvt.getPvtDate()+PERCENT)
                    .setParameter(PID, patient.getPatientId())
                    .getResultList();
            if (!list.isEmpty()) {
                for (PatientVisitModel doubleEntry : list) {
                    em.remove(doubleEntry);
                }
            }

        } catch (Exception te) {
            Logger.getLogger("org.jboss.logging.util.OnlyOnceErrorHandler").warn(te.getMessage());
            return 0;
        }

        // Šù‘¶‚ÌŠ³Ò‚©‚Ç‚¤‚©’²‚×‚é
        try {
            PatientModel exist = (PatientModel) em
                    .createQuery(QUERY_PATIENT_BY_FID_PID)
                    .setParameter(FID, fid)
                    .setParameter(PID, patient.getPatientId())
                    .getSingleResult();

            //-----------------------------
            // Œ’N•ÛŒ¯î•ñ‚ğXV‚·‚é
            //-----------------------------
            Collection<HealthInsuranceModel> ins = patient.getHealthInsurances();
            if (ins != null && ins.size() > 0) {

                // Œ’N•ÛŒ¯‚ğXV‚·‚é
                Collection old = em.createQuery(QUERY_INSURANCE_BY_PATIENT_ID)
                .setParameter(ID, exist.getId())
                .getResultList();

                // Œ»İ‚Ì•ÛŒ¯î•ñ‚ğíœ‚·‚é
                for (Iterator iter = old.iterator(); iter.hasNext(); ) {
                    HealthInsuranceModel model = (HealthInsuranceModel) iter.next();
                    em.remove(model);
                }

                // V‚µ‚¢Œ’N•ÛŒ¯î•ñ‚ğ“o˜^‚·‚é
                Collection<HealthInsuranceModel> newOne = patient.getHealthInsurances();
                for (HealthInsuranceModel model : newOne) {
                    model.setPatient(exist);
                    em.persist(model);
                }
            }

            // –¼‘O‚ğXV‚·‚é 2007-04-12
            exist.setFamilyName(patient.getFamilyName());
            exist.setGivenName(patient.getGivenName());
            exist.setFullName(patient.getFullName());
            exist.setKanaFamilyName(patient.getKanaFamilyName());
            exist.setKanaGivenName(patient.getKanaGivenName());
            exist.setKanaName(patient.getKanaName());
            exist.setRomanFamilyName(patient.getRomanFamilyName());
            exist.setRomanGivenName(patient.getRomanGivenName());
            exist.setRomanName(patient.getRomanName());

            // «•Ê
            exist.setGender(patient.getGender());
            exist.setGenderDesc(patient.getGenderDesc());
            exist.setGenderCodeSys(patient.getGenderCodeSys());

            // Birthday
            exist.setBirthday(patient.getBirthday());

            // ZŠA“d˜b‚ğXV‚·‚é
            exist.setSimpleAddressModel(patient.getSimpleAddressModel());
            exist.setTelephone(patient.getTelephone());
            //exist.setMobilePhone(patient.getMobilePhone());

            // PatientVisit ‚Æ‚ÌŠÖŒW‚ğİ’è‚·‚é
            pvt.setPatientModel(exist);

        } catch (NoResultException e) {
            // V‹KŠ³Ò‚Å‚ ‚ê‚Î“o˜^‚·‚é
            // Š³Ò‘®«‚Í cascade=PERSIST ‚Å©“®“I‚É•Û‘¶‚³‚ê‚é
            em.persist(patient);

            // ‚±‚ÌŠ³Ò‚ÌƒJƒ‹ƒe‚ğ¶¬‚·‚é
            KarteBean karte = new KarteBean();
            karte.setPatientModel(patient);
            karte.setCreated(new Date());
            em.persist(karte);
        }

        // —ˆ‰@î•ñ‚ğ“o˜^‚·‚é
        // CLAIM ‚Ìd—l‚É‚æ‚èŠ³Òî•ñ‚Ì‚İ‚ğ“o˜^‚µA—ˆ‰@î•ñ‚Í‚È‚¢ê‡‚ª‚ ‚é
        // ‚»‚ê‚ğ pvtDate ‚Ì‘®«‚Å”»’f‚µ‚Ä‚¢‚é
        if (pvt.getPvtDate() != null) {
            em.persist(pvt);
        }

        return 1;
    }

    /**
     * {İ‚ÌŠ³Ò—ˆ‰@î•ñ‚ğæ“¾‚·‚éB
     * @param spec ŒŸõd—lDTOƒIƒuƒWƒFƒNƒg
     * @return —ˆ‰@î•ñ‚ÌCollection
     */
    @Override
    public List<PatientVisitModel> getPvt(String fid, String date, int firstResult, String appoDateFrom, String appoDateTo) {

        if (!date.endsWith(PERCENT)) {
            date += PERCENT;
        }
        
        // PatientVisitModel‚ğ{İID‚ÅŒŸõ‚·‚é
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

        // —ˆ‰@î•ñ‚ÆŠ³Ò‚Í ManyToOne ‚ÌŠÖŒW‚Å‚ ‚é
        for (int i = 0; i < len; i++) {
            //for (int i = firstResult; i < len; i++) {
            
            PatientVisitModel pvt = result.get(i);
            PatientModel patient = pvt.getPatientModel();

            // Š³Ò‚ÌŒ’N•ÛŒ¯‚ğæ“¾‚·‚é
            List<HealthInsuranceModel> insurances = (List<HealthInsuranceModel>)em.createQuery(QUERY_INSURANCE_BY_PATIENT_ID)
            .setParameter(ID, patient.getId()).getResultList();
            patient.setHealthInsurances(insurances);

            // —\–ñ‚ğŒŸõ‚·‚é
            if (searchAppo) {
                KarteBean karte = (KarteBean)em.createQuery(QUERY_KARTE_BY_PATIENT_ID)
                .setParameter(ID, patient.getId())
                .getSingleResult();
                // ƒJƒ‹ƒe‚Ì PK ‚ğ“¾‚é
                long karteId = karte.getId();

                List c = em.createQuery(QUERY_APPO_BY_KARTE_ID_DATE)
                .setParameter(ID, karteId)
                .setParameter(DATE, theDate)
                .getResultList();
                //System.err.println("appo size = " + c.size());
                if (c != null && c.size() > 0) {
                    // “–“ú‚Ì—\–ñ‚ÅÅ‰‚Ì‚à‚Ì
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

        // PatientVisitModel‚ğ{İID‚ÅŒŸõ‚·‚é
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

        // —ˆ‰@î•ñ‚ÆŠ³Ò‚Í ManyToOne ‚ÌŠÖŒW‚Å‚ ‚é
        for (int i = 0; i < len; i++) {
            //for (int i = firstResult; i < len; i++) {

            PatientVisitModel pvt = result.get(i);
            PatientModel patient = pvt.getPatientModel();

            // Š³Ò‚ÌŒ’N•ÛŒ¯‚ğæ“¾‚·‚é
            List<HealthInsuranceModel> insurances = (List<HealthInsuranceModel>)em.createQuery(QUERY_INSURANCE_BY_PATIENT_ID)
            .setParameter(ID, patient.getId()).getResultList();
            patient.setHealthInsurances(insurances);

            // —\–ñ‚ğŒŸõ‚·‚é
            if (searchAppo) {
                KarteBean karte = (KarteBean)em.createQuery(QUERY_KARTE_BY_PATIENT_ID)
                .setParameter(ID, patient.getId())
                .getSingleResult();
                // ƒJƒ‹ƒe‚Ì PK ‚ğ“¾‚é
                long karteId = karte.getId();

                List c = em.createQuery(QUERY_APPO_BY_KARTE_ID_DATE)
                .setParameter(ID, karteId)
                .setParameter(DATE, theDate)
                .getResultList();
                //System.err.println("appo size = " + c.size());
                if (c != null && c.size() > 0) {
                    // “–“ú‚Ì—\–ñ‚ÅÅ‰‚Ì‚à‚Ì
                    AppointmentModel appo = (AppointmentModel) c.get(0);
                    pvt.setAppointment(appo.getName());
                }
            }
        }

        return result;
    }

    /**
     * ó•tî•ñ‚ğíœ‚·‚éB
     * @param id ó•tƒŒƒR[ƒh
     * @return íœŒ”
     */
    @Override
    public int removePvt(long id) {
        PatientVisitModel exist = (PatientVisitModel) em.find(PatientVisitModel.class, new Long(id));
        em.remove(exist);
        return 1;
    }

    /**
     * f@I—¹î•ñ‚ğ‘‚«‚ŞB
     * @param pk ƒŒƒR[ƒhID
     * @param state f@I—¹‚Ì 1
     */
    @Override
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

        // •Û‘¶iCLAIM‘—Mj==2 (bit=1)
        // C³‘—M == 4 (bit=2)
        if (state == 2 || state == 4) {
            exist.setState(state);
            em.flush();
            return 1;
        }

        int curState = exist.getState();
        boolean red = ((curState & (1<<BIT_SAVE_CLAIM))!=0);
        boolean yellow = ((curState & (1<<BIT_MODIFY_CLAIM))!=0);
        boolean cancel = ((curState & (1<<BIT_CANCEL))!=0);

        // •Û‘¶ | C³ | ƒLƒƒƒ“ƒZƒ‹ --> •ÏX•s‰Â
        if (red || yellow || cancel) {
            return 0;
        }

        exist.setState(state);
        em.flush();
        return 1;
    }

    /**
     * ƒƒ‚‚ğXV‚·‚éB
     * @param pk ƒŒƒR[ƒhID
     * @param memo ƒƒ‚
     * @return 1
     */
    @Override
    public int updateMemo(long pk, String memo) {
        PatientVisitModel exist = (PatientVisitModel) em.find(PatientVisitModel.class, new Long(pk));
        exist.setMemo(memo);
        return 1;
    }
}
