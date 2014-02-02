/*
 * AClientInputStream.java
 *
 * Created on 2001/11/18, 18:54
 */

package jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans;

/**
 *
 * @author Junzo SATO
 * @version 
 */
public class AClientInputStream extends java.io.DataInputStream/*java.io.FilterInputStream*/ {

    /** Creates new AClientInputStream */
    public AClientInputStream(java.io.InputStream in) {
        super(in);
    }

    /*
     public int read() throws java.io.IOException {
        return in.read();
    }

    public int read(byte[] b) throws java.io.IOException {
        return in.read(b);
    }

    public int read(byte[] b, int off, int len) throws java.io.IOException {
        return in.read(b, off, len);
    }
     */
}
