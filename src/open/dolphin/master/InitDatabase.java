package open.dolphin.master;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import open.dolphin.infomodel.DepartmentModel;
import open.dolphin.infomodel.FacilityModel;
import open.dolphin.infomodel.LicenseModel;
import open.dolphin.infomodel.UserModel;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.jboss.security.Util;

import open.dolphin.ejb.RemoteSystemService;
import open.dolphin.infomodel.RadiologyMethodValue;

/**
 * Dolphin用のマスタを登録する。
 *
 * @author Minagawa,Kazushi
 */
public class InitDatabase {
    
    private static final String MEMBER_TYPE = "FACILITY_USER";
    private static final String DEFAULT_FACILITY_OID = "1.3.6.1.4.1.9414.10.1";
    private static final String PROFIEL_RESOURCE = "/open/dolphin/master/profiel.txt";
    
    // マスタデータファイル
    private static final String ADMIN_RESOURCE 		= "/open/dolphin/master/admin-data-sjis.txt";
    private static final String ADMIN_COMMENT_RESOURCE 	= "/open/dolphin/master/admin-coment-data-sjis.txt";
    private static final String RAD_METHOD_RESOURCE 	= "/open/dolphin/master/radiology-method-data-sjis.txt";
    
    // マスタデータファイルの仕様
    private final int ARRAY_CAPACITY  	= 20;
    private final int TT_VALUE        	= 0;
    private final int TT_DELIM        	= 1;
    private String delimitater 		= "\t";
    private static final String ENCODING = "SHIFT_JIS";
    
    // SystemService to use
    private RemoteSystemService service;
    
    private Logger logger;
    
    /**
     * DolphinMasterMakerObject を生成する。
     */
    public InitDatabase(String userId, String password) {
        
        logger = Logger.getLogger(this.getClass());
        BasicConfigurator.configure();
        
        addDatabaseAdmin(userId, password);
            
        addDolphinMaster();
        
        logger.info("データベースを初期化しました。");
    }
    
    public void addDatabaseAdmin(String userId, String password) {
        
        Hashtable<String, String> prop = new Hashtable<String, String>();
        
        try {
            InputStream in = this.getClass().getResourceAsStream(PROFIEL_RESOURCE);
            InputStreamReader ir = new InputStreamReader(in, ENCODING);
            BufferedReader reader = new BufferedReader(ir);
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#") || line.startsWith("!")) {
                    continue;
                }
                int index = line.indexOf("=");
                if (index < 0) {
                    continue;
                }
                String key = line.substring(0, index);
                String value = line.substring(index+1);
                if (value != null) {
                    prop.put(key, value);
                }
            }
            
            logger.info("管理者情報ファイルを読み込みました。");
            
            // RMI Connection
            String host = prop.get("host.address");
            if (host == null || host.equals("")) {
                host = "localhost";
            }
            Properties props = new Properties();
            props.setProperty("java.naming.factory.initial","org.jnp.interfaces.NamingContextFactory");
            props.setProperty("java.naming.provider.url","jnp://" + host + ":1099");
            props.setProperty("java.naming.factory.url.pkgs","org.jboss.namingrg.jnp.interfaces");
            InitialContext ctx = new InitialContext(props);
            
            this.service = (RemoteSystemService) ctx.lookup("openDolphin/RemoteSystemService");
            
            logger.info("Host Service を取得しました。");
            
            // Admin Model
            FacilityModel facility = new FacilityModel();
            UserModel admin = new UserModel();
            admin.setFacilityModel(facility);

            // 施設OID
            facility.setFacilityId(DEFAULT_FACILITY_OID);

            facility.setFacilityName(prop.get("facility.name"));
            facility.setZipCode(prop.get("facility.zipcode"));
            facility.setAddress(prop.get("facility.address"));
            facility.setTelephone(prop.get("facility.telephone"));
            facility.setUrl(prop.get("facility.url"));
            Date date = new Date();
            facility.setRegisteredDate(date);
            facility.setMemberType(MEMBER_TYPE);

            //admin.setUserId(prop.get("admin.id"));
            //admin.setPassword(prop.get("admin.password"));
            if (userId == null || userId.equals("")) {
                admin.setUserId(prop.get("admin.login.id"));
            } else {
                admin.setUserId(userId);
            }
            if (password == null || password.equals("")) {
                admin.setPassword(prop.get("admin.login.password"));
            } else {
                admin.setPassword(password);
            }
            String Algorithm = "MD5";
            String encoding = "hex";
            String charset = null;
            String hashPass = Util.createPasswordHash(Algorithm, encoding, charset, admin.getUserId(), admin.getPassword());
            admin.setPassword(hashPass);
            admin.setSirName(prop.get("admin.sir.name"));
            admin.setGivenName(prop.get("admin.given.name"));
            admin.setCommonName(admin.getSirName() + " " + admin.getGivenName());

            // 医療資格
            LicenseModel license = new LicenseModel();
            license.setLicense("doctor");
            license.setLicenseDesc("医師");
            license.setLicenseCodeSys("MML0026");
            admin.setLicenseModel(license);

            // 診療科
            DepartmentModel dept = new DepartmentModel();
            dept.setDepartment("01");
            dept.setDepartmentDesc("内科");
            dept.setDepartmentCodeSys("MML0028");
            admin.setDepartmentModel(dept);

            // Email
            String email = prop.get("admin.email");
            if (email == null || email.equals("")) {
                admin.setEmail(prop.get("someone@some-clinic.jp"));
            } else {
                admin.setEmail(email);
            }

            // MemberTpe
            admin.setMemberType(MEMBER_TYPE);

            // 登録日
            admin.setRegisteredDate(date);

            // 登録
            service.addFacilityAdmin(admin);

            logger.info("管理者を登録しました。");
            
            
        } catch (IOException e) {
            logger.fatal("管理者情報ファイルを読み込めません。");
            e.printStackTrace();
            System.exit(1);
        } catch (NamingException ne) {
            logger.fatal("ホストに接続できません。");
            ne.printStackTrace();
            System.exit(1);
        } catch (Exception ee) {
            logger.fatal("管理者情報の登録に失敗しました。");
            ee.printStackTrace();
            System.exit(1);
        }
            
    }
    
    
    
    /**
     * マスタを登録する。
     */
    public void addDolphinMaster() {
        try {
            addRdMethod(RAD_METHOD_RESOURCE);
        } catch (IOException e) {
            logger.fatal("マスターファイルを読み込めません。");
            e.printStackTrace();
            System.exit(1);
            
        } catch (Exception ee) {
            logger.fatal("マスターファイルの登録に失敗しました。");
            ee.printStackTrace();
            System.exit(1);
        }
        
    }
    
    
    /**
     * 放射線メソッドマスタを登録する。
     * @param name 放射線メソッドマスタリソース名
     */
    private void addRdMethod(String name) throws IOException {
        
        //try {
            InputStream in = this.getClass().getResourceAsStream(name);
            InputStreamReader ir = new InputStreamReader(in, ENCODING);
            BufferedReader reader = new BufferedReader(ir);
            
            String line = null;
            ArrayList<RadiologyMethodValue> list = null;
            int cnt = 0;
            
            while ( (line = reader.readLine()) != null ) {
                
                String[] data = getStringArray(line);
                
                if (data != null) {
                    
                    RadiologyMethodValue av = new RadiologyMethodValue();
                    av.setHierarchyCode1(format(data[0]));
                    av.setHierarchyCode2(format(data[1]));
                    av.setMethodName(format(data[2]));
                    
                    if (list == null) {
                        list = new ArrayList<RadiologyMethodValue>();
                    }
                    list.add(av);
                    
                    cnt++;
                }
            }
            
            if (list == null) {
                return;
            }
            
            service.putRadMethodMaster(list);
            logger.info("放射線メソッドマスタを登録しました。");
            
        //} catch (Exception e) {
           // e.printStackTrace();
        //}
    }
    
    /**
     * 文字を整形する。
     */
    private String format(String d) {
        if (d == null) {
            return null;
        } else if (d.equals("\\N")) {
            return null;
        } else {
            return d;
        }
    }
    
    /**
     * リソースファイルから読み込んだタブ区切りの１行をパースし、 String 配列のデータにして返す。
     * @param line　パースするライン
     * @return データ配列
     */
    private String[] getStringArray(String line) {
        
        if (line == null) {
            return null;
        }
        
        String[] ret = new String[ARRAY_CAPACITY];
        int count = 0;
        
        StringTokenizer st = new StringTokenizer(line, delimitater, true);
        int state = TT_VALUE;
        
        while (st.hasMoreTokens()) {
            
            if ( (count % ARRAY_CAPACITY) == 0 ) {
                String[] dest = new String[count + ARRAY_CAPACITY];
                System.arraycopy(ret, 0, dest, 0, count);
                ret = dest;
            }
            
            String token = st.nextToken();
            
            switch (state) {
                
                case TT_VALUE:
                    if (token.equals(delimitater)) {
                        token = null;
                        
                    } else {
                        state = TT_DELIM;
                    }
                    ret[count] = token;
                    count++;
                    break;
                    
                case TT_DELIM:
                    state = TT_VALUE;
                    break;
            }
        }
        
        String[] ret2 = new String[count];
        System.arraycopy(ret, 0, ret2, 0, count);
        
        return ret2;
    }
    
    public static void main(String[] args) {
        if (args.length > 0) {
            new InitDatabase(args[0], args[1]);
        } else {
            new InitDatabase(null, null);
        }
        System.exit(0);
    }
}
