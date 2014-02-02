/*
 * MMLErrorHandler.java
 *
 * Created on 2002/03/31, 19:25
 */

package jp.ac.kumamoto_u.kuh.fc.jsato.math_mml_three;

import org.xml.sax.*;
import jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.*;

import java.io.*;

/**
 *
 * @author  Junzo SATO
 * @version 
 */
public class MMLErrorHandler implements ErrorHandler {
    StatusBean bean = null;
    PrintWriter pw = null;
    public void setPrintWriter(PrintWriter pw) {
        this.pw = pw;
    }
    public PrintWriter getPrintWriter() {
        return pw;
    }
    
    public void printlnStatus(String s) {
        if (pw != null) {
            pw.println(s);
            pw.println("<br/>");// because we use this PrintWriter to generate html output, <br/> is added.
            pw.flush();
        } else {
            if (bean != null) {
                bean.printlnStatus(s);
            } else {
                System.out.println(s);
            }
        }
    }
    
    /** Creates new MMLErrorHandler */
    public MMLErrorHandler() {
    }

    public MMLErrorHandler(StatusBean bean) {
        this.bean = bean;
    }

    public void error(SAXParseException e) {
        printlnStatus("Line: " + e.getLineNumber() + " SAX Error: " + e.getMessage());
    }
    
    public void warning(SAXParseException e) {
        printlnStatus("Line: " + e.getLineNumber() + " SAX Warning: " + e.getMessage());
    }
    
    public void fatalError(SAXParseException e) {
        printlnStatus("Line: " + e.getLineNumber() + " SAX Fatal Error: " + e.getMessage());
    }
}
