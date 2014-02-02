/*
 * AClientOutputStream.java
 *
 * Created on 2001/11/18, 18:54
 */

package jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans;

/**
 *
 * @author Junzo SATO
 * @version 
 */

public class AClientOutputStream extends java.io.DataOutputStream/*java.io.FilterOutputStream*/ {
    /** Creates new AClientOutputStream */
    public AClientOutputStream(java.io.OutputStream out) {
        super(out);
    }

    /*
    public void write(int b) throws java.io.IOException {
        out.write(b);
    }

    public void write(byte[] b) throws java.io.IOException {
        out.write(b);
    }

    public void write(byte[] b, int off, int len) throws java.io.IOException {
        out.write(b, off, len);
    }
     */
}
