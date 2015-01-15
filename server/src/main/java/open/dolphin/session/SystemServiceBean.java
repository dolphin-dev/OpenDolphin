package open.dolphin.session;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import open.dolphin.infomodel.*;
import open.dolphin.msg.OidSender;
import open.stamp.seed.CopyStampTreeBuilder;
import open.stamp.seed.CopyStampTreeDirector;
//import org.jboss.ejb3.annotation.ResourceAdapter;

/**
 *
 * @author kazushi, Minagawa, Digital Globe, Inc.
 */
@Named
@Stateless
//s.oh^ 2014/02/21 Claim送信方法の変更
//@ResourceAdapter("hornetq-ra.rar")
//s.oh$
public class SystemServiceBean {

    //private static final boolean DolphinPro = true;

    //private static final String BASE_OID = "1.3.6.1.4.1.9414.3.";               // 3.xx
    //private static final String DEMO_FACILITY_ID = "1.3.6.1.4.1.9414.2.1";

    private static final String BASE_OID = "1.3.6.1.4.1.9414.71.";
    private static final String DEMO_FACILITY_ID = "1.3.6.1.4.1.9414.70.1";  //70.1

    private static final String QUERY_NEXT_FID = "select nextval('facility_num') as n";
    private static final String QUERY_FACILITY_BY_FID = "from FacilityModel f where f.facilityId=:fid";
    private static final String FID = "fid";
    private static final String PK = "pk";

    private static final String ASP_TESTER = "ASP_TESTER";
    private static final int MAX_DEMO_PATIENTS = 5;
    private static final String ID_PREFIX = "D_";
    private static final String QUERY_PATIENT_BY_FID = "from PatientModel p where p.facilityId=:fid order by p.patientId";
    private static final String QUERY_HEALTH_INSURANCE_BY_PATIENT_PK = "from HealthInsuranceModel h where h.patient.id=:pk";

    @PersistenceContext
    private EntityManager em;
    
//s.oh^ 2014/02/21 Claim送信方法の変更
    //@Resource(mappedName = "java:/JmsXA")
    //private ConnectionFactory connectionFactory;
    //
    //@Resource(mappedName = "java:/queue/dolphin")
    //private javax.jms.Queue queue;
//s.oh$
    

    /**
     * 施設と管理者情報を登録する。
     *
     * @param user 施設管理者
     */
    public void addFacilityAdmin(UserModel user) {

        // シーケンサから次の施設番号を得る
        java.math.BigInteger nextId = (java.math.BigInteger)em.createNativeQuery(QUERY_NEXT_FID).getSingleResult();
        Long nextFnum = new Long(nextId.longValue());

        // 施設OIDを生成する
        StringBuilder sb = new StringBuilder();
        sb.append(BASE_OID).append(String.valueOf(nextFnum));
        String fid = sb.toString();

        // OIDをセットし施設レコードを生成する
        FacilityModel facility = user.getFacilityModel();
        facility.setFacilityId(fid);
        try {
            em.createQuery(QUERY_FACILITY_BY_FID)
            .setParameter(FID, fid)
            .getSingleResult();

            // すでに存在している場合は例外をスローする
            throw new EntityExistsException();

        } catch (NoResultException e) {
            // 当たり前
        }

        // 永続化する
        // このメソッドで facility が管理された状態になる
        em.persist(facility);

        // fid:uid
        sb = new StringBuilder();
        sb.append(fid);
        sb.append(IInfoModel.COMPOSITE_KEY_MAKER);
        sb.append(user.getUserId());
        user.setUserId(sb.toString());

        // role
        Collection<RoleModel> roles = user.getRoles();
        if (roles != null) {
            for (RoleModel role : roles) {
                role.setUserModel(user);
                role.setUserId(user.getUserId());
            }
        }

        // 永続化する
        // Role には User から CascadeType.ALL が設定されている
        em.persist(user);

        //-----------------------------------
        // 評価ユーザなのでデモ用の患者を生成する
        //-----------------------------------
        Collection demoPatients = em.createQuery(QUERY_PATIENT_BY_FID)
                                    .setParameter(FID, DEMO_FACILITY_ID)
                                    .setFirstResult(1)
                                    .setMaxResults(MAX_DEMO_PATIENTS)
                                    .getResultList();

        for (Iterator iter = demoPatients.iterator(); iter.hasNext(); ) {

            PatientModel demoPatient = (PatientModel) iter.next();
            PatientModel copyPatient = new PatientModel();
            copyPatient.setFacilityId(fid);
            copyPatient.setPatientId(ID_PREFIX + demoPatient.getPatientId());
            copyPatient.setFamilyName(demoPatient.getFamilyName());
            copyPatient.setGivenName(demoPatient.getGivenName());
            copyPatient.setFullName(demoPatient.getFullName());
            copyPatient.setKanaFamilyName(demoPatient.getKanaFamilyName());
            copyPatient.setKanaGivenName(demoPatient.getKanaGivenName());
            copyPatient.setKanaName(demoPatient.getKanaName());
            copyPatient.setGender(demoPatient.getGender());
            copyPatient.setGenderDesc(demoPatient.getGenderDesc());
            copyPatient.setBirthday(demoPatient.getBirthday());
            copyPatient.setSimpleAddressModel(demoPatient.getSimpleAddressModel());
            copyPatient.setTelephone(demoPatient.getTelephone());

            // 健康保険を設定する
            Collection demoInsurances = em.createQuery(QUERY_HEALTH_INSURANCE_BY_PATIENT_PK)
                                          .setParameter(PK, demoPatient.getId()).getResultList();

            for (Iterator iter2 = demoInsurances.iterator(); iter2.hasNext(); ) {
                HealthInsuranceModel demoInsurance = (HealthInsuranceModel) iter2.next();
                HealthInsuranceModel copyInsurance = new HealthInsuranceModel();
                copyInsurance.setBeanBytes(demoInsurance.getBeanBytes());
                copyInsurance.setPatient(copyPatient);
                copyPatient.addHealthInsurance(copyInsurance);
            }

            // 永続化する
            em.persist(copyPatient);

            // カルテを生成する
            KarteBean karte = new KarteBean();
            karte.setPatientModel(copyPatient);
            karte.setCreated(new Date());
            em.persist(karte);
        }
        
        //----------------------------------------
        // StampTreeを生成する
        //----------------------------------------
        try {
            // admin の StampTreeModel を取得する
            UserModel admin = (UserModel)
                em.createQuery("from UserModel u where u.userId=:uid")
                  .setParameter("uid", "1.3.6.1.4.1.9414.70.1:lsc_admin")
                  .getSingleResult();
            List<StampTreeModel> list = (List<StampTreeModel>)
                em.createQuery("from StampTreeModel s where s.user.id=:userPK")
                  .setParameter("userPK", admin.getId())
                  .getResultList();
            StampTreeModel st = list.remove(0);
            
            // 上記StampTreeModelのtreeXmlをコピーする
            InputStream is = new ByteArrayInputStream(st.getTreeBytes());
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            CopyStampTreeBuilder builder = new CopyStampTreeBuilder();
            CopyStampTreeDirector director = new CopyStampTreeDirector(builder);
            director.build(br);
            br.close();
            
            // copyした treeXml & bytes
            String copiedTreeXml = builder.getStampTreeXML();
            byte[] treeBytes = copiedTreeXml.getBytes("UTF-8");
            
            // copyした treeXml を登録ユーザーのTreeとして永続化する
            StampTreeModel copyTree = new StampTreeModel();
            copyTree.setTreeBytes(treeBytes);
            copyTree.setUserModel(user);
            copyTree.setName("個人用");
            copyTree.setDescription("個人用のスタンプセットです");
            copyTree.setPartyName(user.getFacilityModel().getFacilityName());
            if (user.getFacilityModel().getUrl()!=null) {
                copyTree.setUrl(user.getFacilityModel().getUrl());
            }
            em.persist(copyTree);
            
            // copy Treeに関連づけされているStampの実態を永続化する
            List<StampModel> stampToPersist = builder.getStampModelToPersist();
            List<String> seedStampIdList = builder.getSeedStampList();
            
            for (int i=0; i<stampToPersist.size();i++) {
                String id = seedStampIdList.get(i);
                StampModel seed = (StampModel)em.find(StampModel.class, id);
                StampModel persist = stampToPersist.get(i);
                persist.setStampBytes(seed.getStampBytes());
                persist.setUserId(user.getId());
                em.persist(persist);
            }
            
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        
//s.oh^ 2014/02/21 Claim送信方法の変更
        //// MailでOIDを通知するためMessageDrivenBeanに渡す
        //Connection conn = null;
        //try {
        //    conn = connectionFactory.createConnection();
        //    Session session = conn.createSession(false, QueueSession.AUTO_ACKNOWLEDGE);
        //
        //    AccountSummary account = new AccountSummary();
        //    account.setMemberType(ASP_TESTER);
        //    account.setFacilityAddress(user.getFacilityModel().getAddress());
        //    account.setFacilityId(user.getFacilityModel().getFacilityId());
        //    account.setFacilityName(user.getFacilityModel().getFacilityName());
        //    account.setFacilityTelephone(user.getFacilityModel().getTelephone());
        //    account.setFacilityZipCode(user.getFacilityModel().getZipCode());
        //    account.setUserEmail(user.getEmail());
        //    account.setUserName(user.getCommonName());
        //    account.setUserId(user.idAsLocal());
        //
        //    ObjectMessage msg = session.createObjectMessage(account);
        //    MessageProducer producer = session.createProducer(queue);
        //    producer.send(msg);
        //    
        //} catch (Exception e) {
        //    e.printStackTrace(System.err);
        //    throw new RuntimeException(e.getMessage());
        //
        //} 
        //finally {
        //    if(conn != null)
        //    {
        //        try
        //        {
        //        conn.close();
        //        }
        //        catch (JMSException e)
        //        { 
        //        }
        //    }
        //}
//s.oh$
    }
    
//s.oh^ 2014/07/08 クラウド0対応
    /**
     * カルテ枚数等、全件数をカウントする
     * @param fid  医療機関 OID
     * @return 
     */
    public ActivityModel countTotalActivities(String fid) {
        
        ActivityModel am = new ActivityModel();
        
        // ユーザー数
        StringBuilder sb = new StringBuilder();
        sb.append("select count(u.id) from UserModel u where u.userId like :fid and u.memberType!=:memberType");
        String sql = sb.toString();
        Object obj = em.createQuery(sql)
                .setParameter("fid", fid+":%")
                .setParameter("memberType", "EXPIRED")
                .getSingleResult();
        long count = (long)obj;
        am.setNumOfUsers(count);
        
        // 全患者数
        sb = new StringBuilder();
        sb.append("select count(p.id) from PatientModel p where p.facilityId=:fid");
        sql = sb.toString();
        obj = em.createQuery(sql)
                .setParameter("fid", fid)
                .getSingleResult();
        count = (long)obj;
        am.setNumOfPatients(count);
        
        // 延べ来院患者
        sb = new StringBuilder();
        sb.append("select count(p.id) from PatientVisitModel p where p.facilityId=:fid and p.status!=:status");
        sql = sb.toString();
        obj = em.createQuery(sql)
                .setParameter("fid", fid)
                .setParameter("status", 6)
                .getSingleResult();
        count = (long)obj;
        am.setNumOfPatientVisits(count);
        
        // 全カルテ数
        sb = new StringBuilder();
        sb.append("select count(d.id) from DocumentModel d where d.creator.userId like :fid and d.status='F'");
        sql = sb.toString();
        obj = em.createQuery(sql)
                .setParameter("fid", fid+":%")
                .getSingleResult();
        count = (long)obj;
        am.setNumOfKarte(count);
        
        // 全画像数
        sb = new StringBuilder();
        sb.append("select count(s.id) from SchemaModel s where s.creator.userId like :fid and s.status='F'");
        sql = sb.toString();
        obj = em.createQuery(sql)
                .setParameter("fid", fid+":%")
                .getSingleResult();
        count = (long)obj;
        am.setNumOfImages(count);
        
        // 添付文書数
        sb = new StringBuilder();
        sb.append("select count(a.id) from AttachmentModel a where a.creator.userId like :fid and a.status='F'");
        sql = sb.toString();
        obj = em.createQuery(sql)
                .setParameter("fid", fid+":%")
                .getSingleResult();
        count = (long)obj;
        am.setNumOfAttachments(count);
        
        // 病名数 RegisteredDiagnosisModel
        sb = new StringBuilder();
        sb.append("select count(r.id) from RegisteredDiagnosisModel r where r.creator.userId like :fid");
        sql = sb.toString();
        obj = em.createQuery(sql)
                .setParameter("fid", fid+":%")
                .getSingleResult();
        count = (long)obj;
        am.setNumOfDiagnosis(count);
        
        // 紹介状数
        sb = new StringBuilder();
        sb.append("select count(l.id) from LetterModule l where l.creator.userId like :fid and l.status='F'");
        sql = sb.toString();
        obj = em.createQuery(sql)
                .setParameter("fid", fid+":%")
                .getSingleResult();
        count = (long)obj;
        am.setNumOfLetters(count);
        
        // 検査数
        sb = new StringBuilder();
        sb.append("select count(l.id) from NLaboModule l where l.patientId like :fid");
        sql = sb.toString();
        obj = em.createQuery(sql)
                .setParameter("fid", fid+":%")
                .getSingleResult();
        count = (long)obj;
        am.setNumOfLabTests(count);
        
        // 医療機関情報
        sb = new StringBuilder();
        sb.append("from FacilityModel f where f.facilityId=:fid");
        sql = sb.toString();
        FacilityModel fm = (FacilityModel)em.createQuery(sql)
                .setParameter("fid", fid)
                .getSingleResult();
        am.setFacilityId(fm.getFacilityId());
        am.setFacilityName(fm.getFacilityName());
        am.setFacilityZip(fm.getZipCode());
        am.setFacilityAddress(fm.getAddress());
        am.setFacilityTelephone(fm.getTelephone());
        am.setFacilityFacimile(fm.getFacsimile());
        
        // DB size
        sb = new StringBuilder();
        sb.append("select pg_size_pretty(pg_database_size('dolphin'))");
        sql = sb.toString();
        obj = em.createNativeQuery(sql).getSingleResult();
        am.setDbSize(obj.toString());
        
        // bind address
        am.setBindAddress(this.getBindAddress());
        
        return am;
    }
    
    /**
     * 対象期間のレコード件数をカウントする
     * @param fid   医療機関OID
     * @param from  集計開始日
     * @param to    集計終了日
     * @return 
     */
    public ActivityModel countActivities(String fid, Date from, Date to) {
        
        ActivityModel am = new ActivityModel();
        am.setFromDate(from);
        am.setToDate(to);
        
        // 対象期間の新規患者
        StringBuilder sb = new StringBuilder();
        sb.append("select count(p.id) from PatientModel p, KarteBean k where p.id=k.patient.id and p.facilityId=:fid and k.created between :fromDate and :toDate");
        String sql = sb.toString();
        Object obj = em.createQuery(sql)
                .setParameter("fid", fid)
                .setParameter("fromDate", from)
                .setParameter("toDate", to)
                .getSingleResult();
        long count = (long)obj;
        am.setNumOfPatients(count);
        
        // 対象期間の来院数
        sb = new StringBuilder();
        sb.append("select count(p.id) from PatientVisitModel p where p.facilityId=:fid and p.pvtDate between :fromDate and :toDate and p.status!=:status");
        sql = sb.toString();
        obj = em.createQuery(sql)
                .setParameter("fid", fid)
                .setParameter("fromDate", pvtDateFromDate(from))
                .setParameter("toDate", pvtDateFromDate(to))
                .setParameter("status", 6)
                .getSingleResult();
        count = (long)obj;
        am.setNumOfPatientVisits(count);
        
        // 対象期間のカルテ枚数
        sb = new StringBuilder();
        sb.append("select count(d.id) from DocumentModel d where d.creator.userId like :fid and d.started between :fromDate and :toDate and d.status='F'");
        sql = sb.toString();
        obj = em.createQuery(sql)
                .setParameter("fid", fid+":%")
                .setParameter("fromDate", from)
                .setParameter("toDate", to)
                .getSingleResult();
        count = (long)obj;
        am.setNumOfKarte(count);
        
        // 対象期間画像数
        sb = new StringBuilder();
        sb.append("select count(s.id) from SchemaModel s where s.creator.userId like :fid and s.started between :fromDate and :toDate and s.status='F'");
        sql = sb.toString();
        obj = em.createQuery(sql)
                .setParameter("fid", fid+":%")
                .setParameter("fromDate", from)
                .setParameter("toDate", to)
                .getSingleResult();
        count = (long)obj;
        am.setNumOfImages(count);
        
        // 対象期間添付文書数
        sb = new StringBuilder();
        sb.append("select count(a.id) from AttachmentModel a where a.creator.userId like :fid and a.started between :fromDate and :toDate and a.status='F'");
        sql = sb.toString();
        obj = em.createQuery(sql)
                .setParameter("fid", fid+":%")
                .setParameter("fromDate", from)
                .setParameter("toDate", to)
                .getSingleResult();
        count = (long)obj;
        am.setNumOfAttachments(count);
        
        // 対象期間病名数
        sb = new StringBuilder();
        sb.append("select count(r.id) from RegisteredDiagnosisModel r where r.creator.userId like :fid and r.started between :fromDate and :toDate");
        sql = sb.toString();
        obj = em.createQuery(sql)
                .setParameter("fid", fid+":%")
                .setParameter("fromDate", from)
                .setParameter("toDate", to)
                .getSingleResult();
        count = (long)obj;
        am.setNumOfDiagnosis(count);
        
        // 対象期間の紹介状数
        sb = new StringBuilder();
        sb.append("select count(l.id) from LetterModule l where l.creator.userId like :fid and l.started between :fromDate and :toDate and l.status='F'");
        sql = sb.toString();
        obj = em.createQuery(sql)
                .setParameter("fid", fid+":%")
                .setParameter("fromDate", from)
                .setParameter("toDate", to)
                .getSingleResult();
        count = (long)obj;
        am.setNumOfLetters(count);
        
        // 対象期間の検査数
        sb = new StringBuilder();
        sb.append("select count(l.id) from NLaboModule l where l.patientId like :fid and l.sampleDate between :fromDate and :toDate");
        sql = sb.toString();
        obj = em.createQuery(sql)
                .setParameter("fid", fid+":%")
                .setParameter("fromDate", sampleDateFromDate(from))
                .setParameter("toDate", sampleDateFromDate(to))
                .getSingleResult();
        count = (long)obj;
        am.setNumOfLabTests(count);
        
        return am;
    }
    
    public void mailActivities(ActivityModel[] ams) {
        
        ActivityModel am = ams[0];
        ActivityModel total = ams[1];
        
        // log
        log("開始日時", am.getFromDate().toString());
        log("終了日時", am.getToDate().toString());
        log("医療機関ID", total.getFacilityId());
        log("医療機関名", total.getFacilityName());
        log("郵便番号", total.getFacilityZip());
        log("住所", total.getFacilityAddress());
        log("電話", total.getFacilityTelephone());
        log("FAX", total.getFacilityFacimile());
        log("利用者数", am.getNumOfUsers());
        log("患者数", am.getNumOfPatients(), total.getNumOfPatients());
        log("来院数", am.getNumOfPatientVisits(),total.getNumOfPatientVisits());
        log("病名数", am.getNumOfDiagnosis(),total.getNumOfDiagnosis());
        log("カルテ枚数", am.getNumOfKarte(),total.getNumOfKarte());
        log("画像数", am.getNumOfImages(),total.getNumOfImages());
        log("添付文書数", am.getNumOfAttachments(),total.getNumOfAttachments());
        log("紹介状数", am.getNumOfLetters(),total.getNumOfLetters());
        log("検査数", am.getNumOfLabTests(),total.getNumOfLabTests());
        log("データベース容量", total.getDbSize());
        log("IP アドレス", total.getBindAddress());
        
        // MailでOIDを通知するためMessageDrivenBeanに渡す
        //Connection conn = null;
        //try {
        //    conn = connectionFactory.createConnection();
        //    Session session = conn.createSession(false, QueueSession.AUTO_ACKNOWLEDGE);
        //    ObjectMessage msg = session.createObjectMessage(ams);
        //    MessageProducer producer = session.createProducer(queue);
        //    producer.send(msg);
        //
        //} catch (JMSException e) {
        //    e.printStackTrace(System.err);
        //    throw new RuntimeException(e.getMessage());
        //
        //} 
        //finally {
        //    if(conn != null)
        //    {
        //        try
        //        {
        //        conn.close();
        //        }
        //        catch (JMSException e)
        //        { 
        //        }
        //    }
        //}
        Logger.getLogger("open.dolphin").info("ActivityModel message has received. Reporting will start(Not Que).");
        OidSender sender = new OidSender();
        try {
            sender.sendActivity(ams);
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            Logger.getLogger("open.dolphin").warning("ActivityModel message send error : " + ex.getMessage());
        }
    }
    
    public void sendMonthlyActivities(int year, int month) {
        
        // 対象月の１日
        GregorianCalendar gcFrom = new GregorianCalendar(year, month, 1);
        Date fromDate = gcFrom.getTime();
        
        // 対象月の最後
        GregorianCalendar gcTo = new GregorianCalendar(year, month, gcFrom.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
        Date toDate = gcTo.getTime();
        
        List<FacilityModel> list = (List<FacilityModel>)em.createQuery("from FacilityModel f").getResultList();
        for (FacilityModel fm : list) {
            
            ActivityModel total = this.countTotalActivities(fm.getFacilityId());
            total.setFlag("T");
            
            ActivityModel target = this.countActivities(fm.getFacilityId(), fromDate, toDate);
            target.setFlag("M");
            target.setFromDate(fromDate);
            target.setToDate(toDate);
            
            this.mailActivities(new ActivityModel[]{target, total});
        }
    }
    
    private String pvtDateFromDate(Date d) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return sdf.format(d);
    }
    
    private String sampleDateFromDate(Date d) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(d);
    }
    
    private String getBindAddress() {
        String test = System.getProperty("jboss.bind.address");
        if (test==null) {
            try {
                InetAddress ip = InetAddress.getLocalHost();
                if (ip!=null) {
                    test = ip.toString();
                }
            } catch (UnknownHostException ex) {
                Logger.getLogger("open.dolphin").log(Level.SEVERE, null, ex);
            }
        }
        return test;
    }
    
    private void log(String name, String value) { 
        Logger.getLogger("open.dolphin").log(Level.INFO, "{0}={1}", new Object[]{name, value});
    }
    
    private void log(String msg, long count) { 
        Logger.getLogger("open.dolphin").log(Level.INFO, "{0}={1}", new Object[]{msg, count});
    }
    
    private void log(String msg, long count, long total) { 
        Logger.getLogger("open.dolphin").log(Level.INFO, "{0}={1}/{2}", new Object[]{msg, count, total});
    }
//s.oh$
}
