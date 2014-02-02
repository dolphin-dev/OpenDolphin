package open.dolphin.session;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.jms.*;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import open.dolphin.infomodel.*;
import open.stamp.seed.CopyStampTreeBuilder;
import open.stamp.seed.CopyStampTreeDirector;
import org.jboss.ejb3.annotation.ResourceAdapter;

/**
 *
 * @author kazushi, Minagawa, Digital Globe, Inc.
 */
@Named
@Stateless
@ResourceAdapter("hornetq-ra.rar")
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
    
    @Resource(mappedName = "java:/JmsXA")
    private ConnectionFactory connectionFactory;
    
    @Resource(mappedName = "java:/queue/dolphin")
    private javax.jms.Queue queue;
    

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
        
        // MailでOIDを通知するためMessageDrivenBeanに渡す
        Connection conn = null;
        try {
            conn = connectionFactory.createConnection();
            Session session = conn.createSession(false, QueueSession.AUTO_ACKNOWLEDGE);

            AccountSummary account = new AccountSummary();
            account.setMemberType(ASP_TESTER);
            account.setFacilityAddress(user.getFacilityModel().getAddress());
            account.setFacilityId(user.getFacilityModel().getFacilityId());
            account.setFacilityName(user.getFacilityModel().getFacilityName());
            account.setFacilityTelephone(user.getFacilityModel().getTelephone());
            account.setFacilityZipCode(user.getFacilityModel().getZipCode());
            account.setUserEmail(user.getEmail());
            account.setUserName(user.getCommonName());
            account.setUserId(user.idAsLocal());

            ObjectMessage msg = session.createObjectMessage(account);
            MessageProducer producer = session.createProducer(queue);
            producer.send(msg);
            
        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw new RuntimeException(e.getMessage());

        } 
        finally {
            if(conn != null)
            {
                try
                {
                conn.close();
                }
                catch (JMSException e)
                { 
                }
            }
        }
    }
}
