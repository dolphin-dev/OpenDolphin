package open.dolphin.client;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.Security;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.naming.InitialContext;
import javax.security.auth.login.LoginContext;

import org.jboss.security.auth.callback.UsernamePasswordHandler;

import open.dolphin.ejb.RemoteSystemService;
import open.dolphin.infomodel.AdminComentValue;
import open.dolphin.infomodel.AdminValue;
import open.dolphin.infomodel.RadiologyMethodValue;

/**
 * Dolphin用のマスタを登録する。
 *
 * @author Minagawa,Kazushi
 */
public class DolphinMasterMaker {
    
    // マスタデータファイル
    private static final String ADMIN_RESOURCE 		= "/open/dolphin/master/admin-data-sjis.txt";
    private static final String ADMIN_COMMENT_RESOURCE 	= "/open/dolphin/master/admin-coment-data-sjis.txt";
    private static final String RAD_METHOD_RESOURCE 	= "/open/dolphin/master/radiology-method-data-sjis.txt";
    
    // マスタデータファイルの仕様
    private final int ARRAY_CAPACITY  	= 20;
    private final int TT_VALUE        	= 0;
    private final int TT_DELIM        	= 1;
    private String delimitater 			= "\t";
    private static final String ENCODING = "SHIFT_JIS";
    
    // SystemService to use
    private RemoteSystemService service;
    
    public void deleteExpiredAccount() {
        
        try {

            //service.deleteExpiredAccount();
            
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * DolphinMasterMakerObject を生成する。
     */
    public DolphinMasterMaker(String host) {
        
        try {
            // SECURITY
            String loginConfig = "./security/dolphin.login.config";
            System.setProperty("java.security.auth.login.config", loginConfig);
            System.out.println("ログイン構成ファイルを設定しました: " + loginConfig);
            // System Properties を設定する
            Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            System.out.println("Security Provider を追加しました: com.sun.net.ssl.internal.ssl.Provider");

            // SSL trust store
            String trustStore = "./security/dolphin.trustStore";
            System.setProperty("javax.net.ssl.trustStore", trustStore);
            System.out.println("trustStoreを設定しました: " + trustStore);
            
            String qid = "minagawa";
            String password = "hanagui+";
            String securityDomain = "openDolphinSysAd";
            
            UsernamePasswordHandler h = new UsernamePasswordHandler(qid, password.toCharArray());
            LoginContext lc = new LoginContext(securityDomain, h);
            lc.login();
            
            Properties props = new Properties();
            props.setProperty("java.naming.factory.initial","org.jnp.interfaces.NamingContextFactory");
            props.setProperty("java.naming.provider.url","jnp://" + host + ":1099");
            props.setProperty("java.naming.factory.url.pkgs","org.jboss.namingrg.jnp.interfaces");
            InitialContext ctx = new InitialContext(props);
            
            this.service = (RemoteSystemService)ctx.lookup("openDolphin/RemoteSystemService");
            
            System.out.println("Service を取得しました");
            
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * マスタを登録する。
     */
    public void addDolphinMaster() {
        
        addAdmin(ADMIN_RESOURCE);
        addAdminComent(ADMIN_COMMENT_RESOURCE);
        addRdMethod(RAD_METHOD_RESOURCE);
        
    }
    
    /**
     * 用法マスタを登録する。
     * @param name 用法マスタファイルリソース名
     */
    private void addAdmin(String name) {
        
        try {
            InputStream in = this.getClass().getResourceAsStream(name);
            InputStreamReader ir = new InputStreamReader(in, ENCODING);
            BufferedReader reader = new BufferedReader(ir);
            
            String line = null;
            ArrayList<AdminValue> list = null;
            int cnt = 0;
            
            while ( (line = reader.readLine()) != null) {
                
                String[] data = getStringArray(line);
                
                if (data != null) {
                    
                    AdminValue av = new AdminValue();
                    av.setHierarchyCode1(format(data[0]));
                    av.setHierarchyCode2(format(data[1]));
                    av.setName(format(data[2]));
                    av.setCode(format(data[3]));
                    av.setClaimClassCode(format(data[4]));
                    av.setNumberCode(format(data[5]));
                    av.setDisplayName(format(data[6]));
                    
                    if (list == null) {
                        list = new ArrayList<AdminValue>();
                    }
                    list.add(av);
                    
                    cnt++;
                }
            }
            
            if (list == null) {
                return;
            }
            
            service.putAdminMaster(list);
            System.out.println("Admin マスタを登録しました");
            
        } catch (Exception e) {
            e.printStackTrace();;
        }
    }
    
    /**
     * 用法コメントマスタを登録する。
     * @param name 用法コメントマスタファイルリソース名
     */
    private void addAdminComent(String name) {
        
        try {
            InputStream in = this.getClass().getResourceAsStream(name);
            InputStreamReader ir = new InputStreamReader(in, ENCODING);
            BufferedReader reader = new BufferedReader(ir);
            
            String line = null;
            ArrayList<AdminComentValue> list = null;
            int cnt = 0;
            
            while ( (line = reader.readLine()) != null ) {
                
                String[] data = getStringArray(line);
                
                if (data != null) {
                    
                    AdminComentValue av = new AdminComentValue();
                    av.setAdminComent(format(data[0]));
                    
                    if (list == null) {
                        list = new ArrayList<AdminComentValue>();
                    }
                    list.add(av);
                    
                    cnt++;
                }
            }
            
            if (list == null) {
                return;
            }
            
            service.putAdminComentMaster(list);
            System.out.println("Admin コメントマスタを登録しました");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 放射線メソッドマスタを登録する。
     * @param name 放射線メソッドマスタリソース名
     */
    private void addRdMethod(String name) {
        
        try {
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
            System.out.println("放射線メソッドマスタを登録しました");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
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
//        if (args.length != 1) {
//            System.exit(1);
//        }
//        String host = args[0];
        //String host = "172.168.158.1";
        String host = "210.153.124.60";
        //String host = "localhost";
        //new DolphinMasterMaker(host).deleteExpiredAccount();
        new DolphinMasterMaker(host).addDolphinMaster();
        System.exit(0);
    }
    
}
