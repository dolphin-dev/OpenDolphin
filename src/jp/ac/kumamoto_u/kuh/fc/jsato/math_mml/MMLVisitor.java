/*
 * MMLVisitor.java
 *
 * Created on 2001/09/29, 19:09
 */

package jp.ac.kumamoto_u.kuh.fc.jsato.math_mml;

import java.io.*;
/**
 *
 * @author	Junzo SATO
 * @version
 */

/* Typical use of MMLVIsitor is like the followings.
 * Where in writeMML(path), v is the MMLVisitor object.
 *
public void writeMML(String path) {
    try {
        printlnStatus("Creating xml...");
        Writer w = new FileWriter(path);
        PrintWriter pw = new PrintWriter(w);
        // create visitor
        v = new MMLVisitor(pw);

        // get root object
        MMLObject obj = (MMLObject)MMLBuilder.getMmlTree().firstElement();
        if (obj == null) {
            printlnStatus("*** COULDN'T GET OBJECT");
        } else {
            v.visitMMLObject(obj);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}
*/
/*
public String toStringMML() {
    String resultString = "";
    try {
        StringWriter sw = null;
        BufferedWriter bw = null;
        PrintWriter pw = null;
        sw = new StringWriter();
        bw = new BufferedWriter(sw);
        pw = new PrintWriter(bw);

        // create visitor
        v = new MMLVisitor(pw);

        // get root object
        MMLObject obj = (MMLObject)MMLBuilder.getMmlTree().firstElement();
        if (obj == null) {
            printlnStatus("*** COULDN'T GET OBJECT");
        } else {
            v.visitMMLObject(obj);
        }

        pw.flush();
        bw.flush();
        sw.flush();

        resultString = sw.toString();

        pw.close();
        bw.close();
        sw.close();
        bw = null;
        sw = null;

        return resultString;

    } catch (Exception e) {
        e.printStackTrace();
        return resultString;
    }
}
*/

public class MMLVisitor {

    /** Holds value of property pw. */
    private PrintWriter pw;
    
    // flag to wirte <?xml line at top of the instance when prints object
    private boolean writeXMLInstruction = true;
    
    /** Creates new MMLVisitor */
    public MMLVisitor(PrintWriter pw) {
        this.pw = pw;
    }

    public MMLVisitor(PrintWriter pw, boolean writeXMLInstruction) {
        this.pw = pw;
        this.writeXMLInstruction = writeXMLInstruction;
    }
    //---------------------------------
    // this is a cheap solution for adding tab spacing:-)
    private int tabIndex = -1;
    
    public void goDown() {
        tabIndex++;
    }
    
    public void goUp() {
        tabIndex--;
    }
    
    public String getTabPadding() {
        String t = "\t";
        String s = "";
        //System.out.println("tabIndex = " + String.valueOf(tabIndex));
        for (int i = 0; i < tabIndex; ++i) {
            s = t + s;
        }
        return s;
    }
    //---------------------------------------
    private boolean ignoreTab = false;
    
    public boolean getIgnoreTab() {
         return ignoreTab;
    }
    
    public void setIgnoreTab( boolean ignoreTab ) {
        this.ignoreTab = ignoreTab;
    }
    //---------------------------------------
    
    // This method is used by the Mml.printObject().
    public void printNamespaces(PrintWriter pw) {
        // this method addes declaration of namespaces to the Mml tag...
        // though it is better to print stored name spaces loaded by the parser,
        // namespaces are hardcoded here for convenience...( sorry, I'm busy...)
        pw.print( " " + "xmlns:xhtml='http://www.w3.org/1999/xhtml'" );
        pw.print( " " + "xmlns:mmlCm='http://www.medxml.net/MML/SharedComponent/Common/1.0'" );
        pw.print( " " + "xmlns:mmlNm='http://www.medxml.net/MML/SharedComponent/Name/1.0'" );
        pw.print( " " + "xmlns:mmlFc='http://www.medxml.net/MML/SharedComponent/Facility/1.0'" );
        pw.print( " " + "xmlns:mmlDp='http://www.medxml.net/MML/SharedComponent/Department/1.0'" );
        pw.print( " " + "xmlns:mmlAd='http://www.medxml.net/MML/SharedComponent/Address/1.0'" );
        pw.print( " " + "xmlns:mmlPh='http://www.medxml.net/MML/SharedComponent/Phone/1.0'" );
        pw.print( " " + "xmlns:mmlPsi='http://www.medxml.net/MML/SharedComponent/PersonalizedInfo/1.0'" );
        pw.print( " " + "xmlns:mmlCi='http://www.medxml.net/MML/SharedComponent/CreatorInfo/1.0'" );
        pw.print( " " + "xmlns:mmlPi='http://www.medxml.net/MML/ContentModule/PatientInfo/1.0'" );
        pw.print( " " + "xmlns:mmlBc='http://www.medxml.net/MML/ContentModule/BaseClinic/1.0'" );
        pw.print( " " + "xmlns:mmlFcl='http://www.medxml.net/MML/ContentModule/FirstClinic/1.0'" );
        pw.print( " " + "xmlns:mmlHi='http://www.medxml.net/MML/ContentModule/HealthInsurance/1.1'" );
        pw.print( " " + "xmlns:mmlLs='http://www.medxml.net/MML/ContentModule/Lifestyle/1.0'" );
        pw.print( " " + "xmlns:mmlPc='http://www.medxml.net/MML/ContentModule/ProgressCourse/1.0'" );
        pw.print( " " + "xmlns:mmlRd='http://www.medxml.net/MML/ContentModule/RegisteredDiagnosis/1.0'" );
        pw.print( " " + "xmlns:mmlSg='http://www.medxml.net/MML/ContentModule/Surgery/1.0'" );
        pw.print( " " + "xmlns:mmlSm='http://www.medxml.net/MML/ContentModule/Summary/1.0'" );
        pw.print( " " + "xmlns:mmlLb='http://www.medxml.net/MML/ContentModule/test/1.0'" );
        pw.print( " " + "xmlns:mmlRp='http://www.medxml.net/MML/ContentModule/report/1.0'" );
        pw.print( " " + "xmlns:mmlRe='http://www.medxml.net/MML/ContentModule/Referral/1.0'" );
        pw.print( " " + "xmlns:mmlSc='http://www.medxml.net/MML/SharedComponent/Security/1.0'" );
        pw.print( " " + "xmlns:claim='http://www.medxml.net/claim/claimModule/2.1'" );
        pw.print( " " + "xmlns:claimA='http://www.medxml.net/claim/claimAmountModule/2.1'" );
    }
    
    public void visitMMLObject(MMLObject obj) {
        try {
            ignoreTab = false;
            
            if (writeXMLInstruction == true) {
                pw.println("<?xml version='1.0' encoding='Shift_JIS'?>");
            }
            
            // instructions
            
            // comment
            
            // tree
            obj.printObject(pw, this);
            pw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /** Getter for property pw.
     * @return Value of property pw.
     */
    public PrintWriter getPw() {
        return pw;
    }
    
    /** Setter for property pw.
     * @param pw New value of property pw.
     */
    public void setPw(PrintWriter pw) {
        this.pw = pw;
    }
}