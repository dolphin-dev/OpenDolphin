/*
 * AClientClientSocketFactory.java
 *
 * Created on 2001/11/18, 18:54
 */

package jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans;

/**
 *
 * @author  Junzo SATO
 * @version 
 */
public class AClientClientSocketFactory implements java.rmi.server.RMIClientSocketFactory, java.io.Serializable {

    /** Creates new AClientClientSocketFactory */
    public AClientClientSocketFactory() {
        super();
    }

    /** Create a client socket connected to the specified host and port.
     * @param host - the host name
     * @param port - the port number
     * @return a socket connected to the specified host and port.
     * @exception IOException if an I/O error occurs during socket creation.
     */
    public java.net.Socket createSocket(java.lang.String host, int port)
    throws java.io.IOException {
        return new AClientSocket(host, port);
    }

    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public int hashCode() {
        return super.hashCode();
    }
}
