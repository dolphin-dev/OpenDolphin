/*
 * ConnectingToLDAP.java
 *
 * Delivered on 2003/01/31
 *
 * Last updated on 2003/02/28
 */

package mirrorI.dolphin.data;
import java.util.*;
import netscape.ldap.*;


/**
 * This class gives LDAP connection for data conversion
 *
 * @author  Aniruddha, Mirror-I
 */
public final class ConnectingToLDAP {

    private static ConnectingToLDAP instance = new ConnectingToLDAP();

    private Hashtable env;

    public static ConnectingToLDAP getInstance() {
        return instance;
    }

    public Hashtable getEnviroment() {
        return env;
    }

    public void setEnviroment(Hashtable h) {
        env = h;

    }

    public synchronized LDAPConnection acquireLDAPConnection() {

        return createConnection();

    }

    public synchronized void releaseLDAPConnection(LDAPConnection ld) {

	    disconnect(ld);

    }

    private LDAPConnection createConnection() {

        if (env == null) {
            env = new Hashtable(4, 0.75f);
            env.put("host", "localhost");
            env.put("port", new Integer(389));
            env.put("authid", "uid=directorymanager,ou=Managers,o=Dolphin");
            env.put("authpw", "secret");
        }

        LDAPConnection ld = null;

        try {
            String host = (String)env.get("host");
            Integer i = (Integer)env.get("port");
            int port = i.intValue();
            String authId = (String)env.get("authid");
            String authPasswd = (String)env.get("authpw");
            ld = new LDAPConnection();
            ld.connect(host, port, authId, authPasswd);
        }
        catch (Exception e) {
            ld = null;
			System.out.println("Exception while calling createConnection");
			System.out.println("Exception details:"  + e );
        }
        return ld;
    }

    private void disconnect(LDAPConnection ld) {
        try {
            if(ld != null) {
            	ld.disconnect();
            	ld = null;
			}
        }
        catch (LDAPException e) {
        }
    }

}