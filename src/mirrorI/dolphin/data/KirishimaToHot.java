/*
 * KirishimaToHot.java
 *
 * Created on 2003/04/01, 17:54
 */

package mirrorI.dolphin.data;

import java.util.*;
import netscape.ldap.*;
import java.sql.*;

import open.dolphin.infomodel.*;

/**
 *
 * @author  kazm
 */
public class KirishimaToHot {
    
    private LDAPConnection oldLd;
    private LDAPConnection newLd;
    private Connection conn;
    
    /** Creates a new instance of KirishimaToHot */
    public KirishimaToHot(String oldHost, String newHost) {
        
        oldLd = getLDAPConnection(oldHost);
        newLd = getLDAPConnection(newHost);
        conn = getPostgresConnection(newHost);
    }
    
    private ArrayList getUserProfile() {
        /*
        uid           => 利用者ID
        userPassword  => パスワード
        sn            => 姓
        givenName     => 名
        cn            => 氏名(sn & ' ' & givenName),
        licenseCode   => 職種(MML0026)
        facilityId    => 医療機関コード(ORCA医療機関コード)
        departmentId  => 診療科(MML0028)
        authority     => LASに対する権限(admin:管理者,user:一般利用者)
        mail          => メールアドレス
        description   => その他情報
        objectClass   => 'DolphinUser'
        */
        String base = "ou=DolphinUsers,o=Dolphin";
        String filter = "(objectclass=*)";
        String[] attrs = new String[]{"uid","userPassword","sn","givenName","cn","licenseCode","facilityId","departmentId","authority","mail","description"};        
               
        ArrayList result = null;
        UserProfileEntry profile = null;
        String val = null;
                
        try {
            LDAPSearchResults res = oldLd.search(base, LDAPConnection.SCOPE_ONE, filter, attrs, false);
            
            while (res != null && res.hasMoreElements()) {

                try {
                    LDAPEntry entry = res.next();
                    Enumeration enumVals;

                    LDAPAttribute attr = entry.getAttribute("uid");
                    if (attr != null) {
                        enumVals = attr.getStringValues();
                        if ( (enumVals != null) && enumVals.hasMoreElements() ) {

                            val = (String)enumVals.nextElement();
                            System.out.println(val);
                            //if (val.equals("lasmanager")) {
                                //;

                            //} else {
                                profile = new UserProfileEntry();
                                //profile.setUserId("433010100001");
                                profile.setUserId(val);
                                
                                if (result == null) {
                                    result = new ArrayList();
                                }
                                result.add(profile);
                            //}
                        }
                    }

                    attr = entry.getAttribute("userPassword");
                    if (attr != null) {
                        enumVals = attr.getStringValues();
                        if ( (enumVals != null) && enumVals.hasMoreElements() ) {
                            val = (String)enumVals.nextElement();
                            System.out.println(val);
                            profile.setPasswd(val);
                        }
                    }

                    attr = entry.getAttribute("sn");
                    if (attr != null) {
                        enumVals = attr.getStringValues();
                        if ( (enumVals != null) && enumVals.hasMoreElements() ) {
                            val = (String)enumVals.nextElement();
                            System.out.println(val);
                            profile.setSirName(val);
                        }
                    }

                    attr = entry.getAttribute("givenName");
                    if (attr != null) {
                        enumVals = attr.getStringValues();
                        if ( (enumVals != null) && enumVals.hasMoreElements() ) {
                            val = (String)enumVals.nextElement();
                            System.out.println(val);
                            profile.setGivenName(val);
                        }
                    }

                    attr = entry.getAttribute("cn");
                    if (attr != null) {
                        enumVals = attr.getStringValues();
                        if ( (enumVals != null) && enumVals.hasMoreElements() ) {
                            val = (String)enumVals.nextElement();
                            System.out.println(val);
                            profile.setCommonName(val);
                        }
                    }

                    attr = entry.getAttribute("licenseCode");
                    if (attr != null) {
                        enumVals = attr.getStringValues();
                        if ( (enumVals != null) && enumVals.hasMoreElements() ) {
                            val = (String)enumVals.nextElement();
                            System.out.println(val);
                            profile.setLicenseCode(val);
                        }
                    }

                    attr = entry.getAttribute("facilityId");
                    if (attr != null) {
                        enumVals = attr.getStringValues();
                        if ( (enumVals != null) && enumVals.hasMoreElements() ) {
                            val = (String)enumVals.nextElement();
                            System.out.println(val);
                            profile.setFacilityId(val);
                        }
                    }

                    attr = entry.getAttribute("departmentId");
                    if (attr != null) {
                        enumVals = attr.getStringValues();
                        if ( (enumVals != null) && enumVals.hasMoreElements() ) {
                            val = (String)enumVals.nextElement();
                            System.out.println(val);
                            profile.setDepartmentId(val);
                        }
                    }

                    attr = entry.getAttribute("authority");
                    if (attr != null) {
                        enumVals = attr.getStringValues();
                        if ( (enumVals != null) && enumVals.hasMoreElements() ) {
                            val = (String)enumVals.nextElement();
                            System.out.println(val);
                            profile.setAuthority(val);
                        }
                    }

                    attr = entry.getAttribute("mail");
                    if (attr != null) {
                        enumVals = attr.getStringValues();
                        if ( (enumVals != null) && enumVals.hasMoreElements() ) {
                            val = (String)enumVals.nextElement();
                            System.out.println(val);
                            profile.setMail(val);
                        }
                    }

                    attr = entry.getAttribute("description");
                    if (attr != null) {
                        enumVals = attr.getStringValues();
                        if ( (enumVals != null) && enumVals.hasMoreElements() ) {
                            val = (String)enumVals.nextElement();
                            System.out.println(val);
                            profile.setDescription(val);
                        }
                    }
                    
                } catch (LDAPException e1) {
                    System.out.println("LDAPException while reading user profile: " + e1.toString());
                    profile = null;
                    result = null;
                } catch (NullPointerException e2) {
                    System.out.println("NullPointerException while reading user profile: " + e2.toString());
                    profile = null;
                    result = null;
                }
            }
            
        } catch (LDAPException e3) {
            System.out.println("LDAPException while reading user profile: " + e3.toString());
            profile = null;
            result = null;
        }
        
        return result;
    }
    
    private boolean storeProfile(ArrayList list) {
        
        boolean result = false;
        
        if (list == null || list.size() == 0) {
            return result;
        }
        
        int userCount = list.size();
        
        for (int i = 0; i < userCount; i++) {
            
            UserProfileEntry profile = (UserProfileEntry)list.get(i);
            
            if (profile.getUserId().equals("lasmanager")) {
                continue;
            }
            
            try {
                /*
                    uid           => 利用者ID
                    userPassword  => パスワード
                    sn            => 姓
                    givenName     => 名
                    cn            => 氏名(sn & ' ' & givenName),
                    //licenceCode   => 職種(MML0026)
                    licenseCode   => 職種(MML0026)
                    facilityId    => 医療機関コード(ORCA医療機関コード)
                    departmentId  => 診療科(MML0028)
                    authority     => LASに対する権限(admin:管理者,user:一般利用者)
                    mail          => メールアドレス
                    description   => その他情報
                    objectClass   => 'DolphinUser'
                */
                LDAPAttributeSet attrs = new LDAPAttributeSet();
                attrs.add(new LDAPAttribute("objectclass", new String[]{"DolphinUser"}));
                attrs.add(new LDAPAttribute("uid", profile.getUserId()));
                attrs.add(new LDAPAttribute("userPassword", profile.getPasswd()));

                String val = profile.getSirName();
                if (val != null) {
                    attrs.add(new LDAPAttribute("sn", val));
                }

                val = profile.getGivenName();
                if (val != null) {
                    attrs.add(new LDAPAttribute("givenName", val));
                }

                val = profile.getCommonName();
                if (val != null) {
                    attrs.add(new LDAPAttribute("cn", val));
                }

                val = profile.getLicenseCode();
                if (val != null) {
                    attrs.add(new LDAPAttribute("licenseCode", val));
                }

                val = profile.getFacilityId();
                if (val != null) {
                    attrs.add(new LDAPAttribute("facilityId", val));
                }

                val = profile.getDepartmentId();
                if (val != null) {
                    attrs.add(new LDAPAttribute("departmentId", val));
                }

                val = profile.getAuthority();
                if (val != null) {
                    attrs.add(new LDAPAttribute("authority", val));
                }

                val = profile.getMail();
                if (val != null) {
                    attrs.add(new LDAPAttribute("mail", val));
                }

                val = profile.getDescription();
                if (val != null) {
                    attrs.add(new LDAPAttribute("description", val));
                }

                // DN
                StringBuffer buf = new StringBuffer();
                buf.append("uid=");
                buf.append(profile.getUserId());
                buf.append(",ou=DolphinUsers,o=Dolphin");
                String dn = buf.toString();
                System.out.println("adding entry " + dn);
                LDAPEntry entry = new LDAPEntry(dn, attrs);

                newLd.add(entry);
                result = true;

            } catch (NullPointerException ne) {
                System.out.println("NullPointerException while writing the user profile: " + ne.toString());

            } catch (LDAPException le) {
                System.out.println("LDAPException while writing the user profile: " + le.toString());
            } 
        }
        
        return result;
    }
    
    private void updateMasterFlag() {
        
        PreparedStatement psmt = null;
        boolean result = false;
        
        int count = DataMapping.masterBase.length;
        String[] attrs = new String[]{"mmlMasterItemCode"};
        
        for (int i = 0; i < count; i++) {
            
            String base = DataMapping.masterBase[i];
            System.out.println(base);
            String objectclass = DataMapping.masterObjClass[i];
            String table = DataMapping.masterTable[i];
            
            StringBuffer buf = new StringBuffer();
            buf.append("(&(objectclass=");
            buf.append(objectclass);
            buf.append(")(mmlFreqFlag=*))");
            String filter = buf.toString();
            System.out.println(filter);
            
            buf = new StringBuffer();
            buf.append("update ");
            buf.append(table);
            buf.append(" set freqflag='1' where code=?");
            String sql = buf.toString();
            System.out.println(sql);
            
            try {
                //conPostgre.setAutoCommit(false);
                psmt = conn.prepareStatement(sql);
                
            } catch (SQLException e) {
                System.out.println(e);
            }
            
            try {
                LDAPSearchResults res = oldLd.search(base, LDAPConnection.SCOPE_ONE, filter,attrs, false);
                
                while(res != null && res.hasMoreElements()) {
            
                    LDAPEntry entry = res.next();
                    Enumeration enumVals;

                    LDAPAttribute attr = entry.getAttribute("mmlMasterItemCode");
                    if (attr != null) {
                        enumVals = attr.getStringValues();
                        if ( (enumVals != null) && enumVals.hasMoreElements() ) {
                            
                            String code = (String)enumVals.nextElement();
                            System.out.println(code);
                            psmt.setString(1, code);
                            psmt.executeUpdate();
                        }
                    }
                }
                
            } catch (LDAPException e1) {
                System.out.println("LDAPException while updating master flags: " + e1.toString());
                
            } catch (SQLException e2) {
                System.out.println("SQLException while updating master flags: " + e2.toString());
                
            } catch (NullPointerException e3) {
                System.out.println("NullPointerException while updating master flags: " + e3.toString());
            }
            
            try {
                conn.commit();
                psmt.close();
            } catch (Exception e4) {
                System.out.println(e4);
            }
            result = true;
        }
        
        if (result) {
            System.out.println("Update Master Flag end succesfully");
        
        } else {
            System.out.println("Update Master Flag end unsuccesfully");
        }
    }
    
    public void update() {
        
        //UserProfileEntry profile = getUserProfile();
        //storeProfile(profile);
        
        ArrayList list = getUserProfile();
        storeProfile(list);
        
        DataConversion2 conv = new DataConversion2();
        conv.setUsers(list);
        //conv.setCreatorId(profile.getUserId());
        //conv.setDepartment(DataMapping.getDepartmentName(profile.getDepartmentId()));
        conv.doDataTransfer(oldLd, conn);
        
        updateMasterFlag();
        
        closeLDAPConnection(oldLd);
        closeLDAPConnection(newLd);
        closeConnection(conn);
    }
    
    private LDAPConnection getLDAPConnection(String host) {
        
        int port = 389;
        String bindDN = "cn=Manager,o=Dolphin";
        String passwd = "secret";
        LDAPConnection ld = new LDAPConnection();
        try {
            ld.connect(host, port, bindDN, passwd);
            System.out.println("Got ldap connection");
        }
        catch (LDAPException e) {
            System.out.println(e);
            System.exit(1);
        }
        return ld;
    }
    
    private void closeLDAPConnection(LDAPConnection ld) {
        
        if (ld == null) {
            return;
        }
        try {
            ld.disconnect();
        } catch (Exception e) {
        }
    }
    
    private Connection getPostgresConnection(String host) {

        try {
            Class.forName("org.postgresql.Driver");
			  
        } catch (ClassNotFoundException cnfe) {
            System.out.println("Couldn't find the driver!");
            System.exit(1); 
        }
       
        Connection con = null;
        String port = "5432";
        String db = "dolphin";
        String user = "dolphin";
        String passwd ="";
        
        StringBuffer buf = new StringBuffer();
        buf.append("jdbc:postgresql://");
        buf.append(host);
        buf.append(":");
        buf.append(port);
        buf.append("/");
        buf.append(db);
        String url = buf.toString();

        try {
            con = DriverManager.getConnection(url, user, passwd);
            System.out.println("Got postgresql connection");

        } catch (SQLException e) {
            System.out.println(e);
            System.exit(1);
        }
        return con;
    }
    
    private void closeConnection(Connection conn) {
        if (conn == null) {
            return;
        }
        try {
            conn.close();
        } catch (Exception e) {
        }
    }
    
    public static void main(String[] args) {
        
        if (args.length < 2) {
            System.out.println("Usage: KirishimaToHot oldIPAddress newIPAddress");
        }
        
        KirishimaToHot kh = new KirishimaToHot(args[0], args[1]);
        //KirishimaToHot kh = new KirishimaToHot("172.168.158.2", "172.168.158.2");
        kh.update();
    }
}