/*
 * AClientSocket.java
 *
 * Created on 2001/11/18, 18:54
 */

package jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans;

/**
 *
 * @author Junzo SATO
 * @version 
 */

import java.net.*;

public class AClientSocket extends java.net.Socket {

    private java.io.InputStream in;
    private java.io.OutputStream out;

    /** Creates new AClientSocket */
    public AClientSocket() {
        super();
    }

    /** Creates new CustomSocket */
    public AClientSocket(String host, int port) throws java.net.UnknownHostException, java.io.IOException {
        super(host, port);
    }

    public java.io.InputStream getInputStream() throws java.io.IOException {
        if (in == null) {
            try {
                if (super.getInputStream() == null) {}
            } catch (java.net.SocketException e) {
                return null;
            }
            
            java.io.BufferedInputStream buffered = new java.io.BufferedInputStream(super.getInputStream());
            in = new AClientInputStream(buffered);
        }
        return in;
    }

    public java.io.OutputStream getOutputStream() throws java.io.IOException {
        if (out == null) {
            try {
                if (super.getOutputStream() == null) {}
            } catch (java.net.SocketException e) {
                return null;
            }
            
            java.io.BufferedOutputStream buffered = new java.io.BufferedOutputStream(super.getOutputStream());
            out = new AClientOutputStream(buffered);
        }
        return out;
    }

    public synchronized void close() throws java.io.IOException {
        java.io.OutputStream out = getOutputStream();
        if (out != null) out.flush();
        super.close();
    }

}
