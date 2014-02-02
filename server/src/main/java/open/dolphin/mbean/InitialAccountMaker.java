package open.dolphin.mbean;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.Date;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import open.dolphin.infomodel.AddressModel;
import open.dolphin.infomodel.DepartmentModel;
import open.dolphin.infomodel.FacilityModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.KarteBean;
import open.dolphin.infomodel.LicenseModel;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.infomodel.RoleModel;
import open.dolphin.infomodel.TelephoneModel;
import open.dolphin.infomodel.UserModel;

/**
 * Updator
 * 
 * @author masuda, Masuda Naika
 */
@Startup
@Singleton
public class InitialAccountMaker {

// minagawa^ DolphinProサポートで運用するためかなり勝ってに変更    
    private static final String MEMBER_TYPE = "FACILITY_USER";
    private static final String DEFAULT_FACILITY_OID = "1.3.6.1.4.1.9414.10.1";
    private static final String DEFAULT_FACILITY_NAME = "クリニック";
    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASS_MD5 = "21232f297a57a5a743894a0e4a801fc3";    // admin
    private static final String ADMIN_SIR_NAME = "オープン";
    private static final String ADMIN_GIVEN_NAME = "ドルフィン";

    private static final boolean DEVELOPMENT = true;
    
    @PersistenceContext
    private EntityManager em;
    
    @Resource(mappedName="java:jboss/datasources/OrcaDS")
    private DataSource ds;
    
    @PostConstruct
    public void init() {
        start();
    }
    
    private void start() {
        
        long userCount = (Long) em.createQuery("select count(*) from UserModel").getSingleResult();
        long facilityCount = (Long) em.createQuery("select count(*) from FacilityModel").getSingleResult();
        
        // ユーザーも施設情報もない場合のみ初期ユーザーと施設情報を登録する
        if (userCount == 0 && facilityCount == 0) {
            addFacilityAdmin();
            if (DEVELOPMENT) {
                addDemoPatient();
            }
        }
        
        Properties config = new Properties();
        
        // コンフィグファイルをチェックする
        StringBuilder sb = new StringBuilder();
        sb.append(System.getProperty("jboss.home.dir"));
        sb.append(File.separator);
        sb.append("custom.properties");
        File f = new File(sb.toString());
        
        try {
            // 読み込む
            FileInputStream fin = new FileInputStream(f);
            InputStreamReader r = new InputStreamReader(fin, "JISAutoDetect");
            config.load(r);
            r.close();
            
            String conn = config.getProperty("claim.conn");
            String addr = config.getProperty("claim.host");
            if (conn!=null && conn.equals("server") && addr!=null) {
                Connection con = ds.getConnection();
                con.close();
            }
        } catch (Exception e) {
        }
    }  

   private void addFacilityAdmin() {
        
        Date date = new Date();
        
        // 施設情報
        FacilityModel facility = new FacilityModel();
        facility.setFacilityId(DEFAULT_FACILITY_OID);
        facility.setFacilityName(DEFAULT_FACILITY_NAME);
        facility.setMemberType(MEMBER_TYPE);
        facility.setZipCode("");
        facility.setAddress("");
        facility.setTelephone("");
        facility.setUrl("");
        facility.setRegisteredDate(date);
        
        // 永続化する
        em.persist(facility);        
        
        // ユーザー情報
        UserModel admin = new UserModel();
        admin.setFacilityModel(facility);
        admin.setUserId(DEFAULT_FACILITY_OID + IInfoModel.COMPOSITE_KEY_MAKER + ADMIN_USER);
        admin.setPassword(ADMIN_PASS_MD5);
        admin.setSirName(ADMIN_SIR_NAME);
        admin.setGivenName(ADMIN_GIVEN_NAME);
        admin.setCommonName(admin.getSirName() + " " + admin.getGivenName());
        admin.setEmail("");
        admin.setMemberType(MEMBER_TYPE);
        admin.setRegisteredDate(date);       
        
        LicenseModel license = new LicenseModel();
        license.setLicense("doctor");
        license.setLicenseDesc("医師");
        license.setLicenseCodeSys("MML0026");
        admin.setLicenseModel(license);
        
        DepartmentModel depart = new DepartmentModel();
        depart.setDepartment("01");
        depart.setDepartmentDesc("内科");
        depart.setDepartmentCodeSys("MML0028");
        admin.setDepartmentModel(depart);

        // add roles
        String[] roles = {IInfoModel.ADMIN_ROLE, IInfoModel.USER_ROLE};
        for (String role : roles) {
            RoleModel roleModel = new RoleModel();
            roleModel.setRole(role);
            roleModel.setUserModel(admin);
            roleModel.setUserId(admin.getUserId());
            admin.addRole(roleModel);
        }
        
        // 永続化する
        em.persist(admin);
    }
    
    private void addDemoPatient() {
        
        PatientModel pm = new PatientModel();
        pm.setFacilityId(DEFAULT_FACILITY_OID);
        pm.setPatientId("D_000001");
        pm.setKanaFamilyName("トクガワ");
        pm.setKanaGivenName("ヨシムネ");
        pm.setKanaName(pm.getKanaFamilyName() + " " + pm.getKanaGivenName());
        pm.setFamilyName("徳川");
        pm.setGivenName("吉宗");
        pm.setFullName(pm.getFamilyName() + " " + pm.getGivenName());
        pm.setGender(IInfoModel.MALE);
        pm.setGenderDesc(IInfoModel.MALE_DISP);
        pm.setBirthday("1684-11-17");
        
        AddressModel am = new AddressModel();
        am.setZipCode("640-8156");
        am.setAddress("和歌山市七番丁２３番地");
        pm.addAddress(am);
        
        TelephoneModel tm = new TelephoneModel();
        tm.setArea("073");
        tm.setCity("435");
        tm.setNumber("1044");
        pm.addTelephone(tm);
        
        em.persist(pm);
        
        KarteBean karte = new KarteBean();
        karte.setPatientModel(pm);
        karte.setCreated(new Date());
        em.persist(karte);
    }
//minagawa$    
}
