/*
 * AQueryResultProcessor.java
 *
 * Created on 2001/11/19, 22:05
 */

package jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans;

/**
 *
 * @author  Junzo SATO
 * @version 
 */
import java.io.*;
//import cryptix.util.mime.*;

import javax.xml.parsers.* ;

import open.dolphin.client.*;
import open.dolphin.util.MMLDate;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.*;
import jp.ac.kumamoto_u.kuh.fc.jsato.math_mml.*;

import org.apache.soap.encoding.soapenc.Base64;
//import starlight.util.*;

import java.util.*;

import jp.ac.kumamoto_u.kuh.fc.jsato.*;
public class AQueryResultProcessor {

    //==============================================================
    // if the processor receives loginResult message, 
    // loginSucceeded is set in handleLoginResult method.
    boolean loginSucceeded = false;
    synchronized public boolean didLoginSucceed() {
        return loginSucceeded;
    }
    //==============================================================
    // Succeeded MmlModuleItems as a result of mmlQuery are stored in this vector.
    private Vector succeededItems = null;
    synchronized public Vector getSucceededItems() {
        return succeededItems;
    }
    //==============================================================
    boolean resultSucceeded = false;
    synchronized public boolean didResultSucceed() {
        return resultSucceeded;
    }
    //==============================================================
    private Vector extRefInContent = new Vector();
    synchronized public Vector getExtRefInContent() {
        return extRefInContent;
    }
    //==============================================================
    
    public String getResultString() {
        String resultString = "";        
        // <loginResult> or <?mmlResult
        // In case of sending loginRequest or <?mmlQuery
        // query result processor would receive loginResult or <?mmlResult
        // this result is set to resultString.
        if (didLoginSucceed() || didResultSucceed()) {
            resultString = "succeeded";
        } else {
            resultString = "failed";
        }
        return resultString;
    }
    
    Mml resultMmlObject = null;
    public Mml getResultMmlObject() {
        return resultMmlObject;
    }
    //============================================
    
    public static void main(String args[]) {
        AQueryResultProcessor p = new AQueryResultProcessor();
        p.processMmlString(FileUtils.fromFile("/DOLPHIN_PROJECT_MATERIALS/mmlQuery/LoginRequest.xml"));    
        p.processMmlString(FileUtils.fromFile("/DOLPHIN_PROJECT_MATERIALS/mmlQuery/LoginResult.xml"));        
        p.processMmlString(FileUtils.fromFile("/DOLPHIN_PROJECT_MATERIALS/mmlQuery/mmlAddendum.xml"));        
        p.processMmlString(FileUtils.fromFile("/DOLPHIN_PROJECT_MATERIALS/mmlQuery/mmlResult_list.xml"));        
        p.processMmlString(FileUtils.fromFile("/DOLPHIN_PROJECT_MATERIALS/mmlQuery/listResult.xml"));
        
        /*
        FileUtils.toFile(
            new String("/DOLPHIN_PROJECT_MATERIALS/dummy/RCV" + MMLDate.getDateTime() + ".txt").replaceAll(":", ""),
            "How lovely you are! :-)"
        );
         */
    }

    /** Creates new AQueryResultProcessor */
    public AQueryResultProcessor() {

    }
    
    MMLDirector dr = null;
    MMLVisitor v = null;
    
    public void readMmlString(String dataString) {
        //System.out.println("\nPARSING STRING");
        //System.out.println(dataString);
        
        try {
            // create parser
            SAXParserFactory saxFactory = SAXParserFactory.newInstance();
            // we need to detect namespaces by SAX2.
            // this setting should be called explicitely for jdk1.4 or later
            saxFactory.setNamespaceAware(true);
            SAXParser parser = saxFactory.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            if (reader == null) {
                System.out.println("XMLReader is null.");
                return;
            }
            
            if ( dr != null ) {
                dr.releaseDirector();
                dr = null;
            }
            dr = new MMLDirector();
            
            reader.setContentHandler(dr);
            // parse xml
            BufferedReader br = new BufferedReader(new StringReader(dataString));
            if (br == null) {
                System.out.println("BufferedReader is null.");
                return;
            }
            reader.parse(new InputSource(br));
            
            // create new file
            //printlnStatus("Parsing done...\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
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
            MMLObject obj = (MMLObject)dr.getMMLBuilder().getMmlTree().firstElement();
            if (obj == null) {
                System.out.println("*** COULDN'T GET OBJECT");
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
    
    public void handleMml(Mml mmlObj) {
        System.out.println("PROCESSING MML");
        //new MmlProcessor().processMml(mmlObj);
        resultMmlObject = mmlObj;
        
        // extRef in the content
        if (dr.getMMLBuilder().extRefInContent != null && 
            dr.getMMLBuilder().extRefInContent.size() > 0) {
                
            System.out.println(((Vector)dr.getMMLBuilder().extRefInContent).size());
            // get deep copy of the external references vector
            
            //******************************************************************
            /*
            Enumeration en = ((Vector)dr.getMMLBuilder().extRefInContent).elements();
            while (en.hasMoreElements()) {
                extRefInContent.addElement(en.nextElement());
            }
            */
            for (int k = 0; k < ((Vector)dr.getMMLBuilder().extRefInContent).size(); ++k) {
                System.out.println(
                    "]]] " + ((mmlCmextRef)((Vector)dr.getMMLBuilder().extRefInContent).elementAt(k)).getMmlCmhref() + "[[["
                );
                extRefInContent.addElement(((Vector)dr.getMMLBuilder().extRefInContent).elementAt(k));
            }
            //******************************************************************
        }
        
        System.out.println("END PROCESSING MML");
    }
    
    public void handleLoginRequest(loginRequest requestObj) {
        System.out.println("PROCESSING LOGIN REQUEST");         
        if (requestObj == null) return;

        String id = "";
        userId userIdObj = requestObj.getUserId();
        if (userIdObj != null) {
            if (userIdObj.getText() != null) {
                id = userIdObj.getText().trim();
            }
        }
        System.out.println("User ID: " + id);

        String passwd = "";
        userPassword passwdObj = requestObj.getUserPassword();
        if (passwdObj != null) {
            if (passwdObj.getText() != null) {
                passwd = passwdObj.getText().trim();
            }
        }
        System.out.println("Password: " + passwd);
    }
    
    public void handleLoginResult(loginResult resultObj) {
        System.out.println("PROCESSING LOGIN RESULT");
        if (resultObj == null) return;

        String resCode = "";
        resultCode resCodeObj = resultObj.getResultCode();
        if (resCodeObj != null) {
            if (resCodeObj.getText() != null) {
                resCode = resCodeObj.getText().trim();
            }
        }
        System.out.println("Result Code: " + resCode);

        String resStr = "";
        resultString resStrObj = resultObj.getResultString();
        if (resStrObj != null) {
            if (resStrObj.getText() != null) {
                resStr = resStrObj.getText().trim();
            }
        }
        System.out.println("Result String: " + resStr);

        //==============================================================
        if (resCode.equals("00") && resStr.equals("LDAP_SUCCESS")) {
            loginSucceeded = true;
            System.out.println("*** LOGIN SUCCEEDED :)");
        } else {
            loginSucceeded = false;
            System.out.println("*** LOGIN FAILED :(");
        }
        //==============================================================
    }
    
    public void handleMmlAddendum(MmlAddendum addendumObj) {
        System.out.println("PROCESSING MML ADDENDUM");
        if (addendumObj == null) return;

        // contentName
        String name = "";
        if (addendumObj.getContentName() != null) {
            name = addendumObj.getContentName();
        }
        System.out.println("Content Name: " + name);

        // contentEncoding
        String encoding = "";
        if (addendumObj.getContentEncoding() != null) {
            encoding = addendumObj.getContentEncoding();
        }
        System.out.println("Content Encoding: " + encoding);

        // contentType
        String type = "";
        if (addendumObj.getContentType() != null) {
            type = addendumObj.getContentType();
        }
        System.out.println("Content Type: " + type);

        // medicalRole
        String role = "";
        if (addendumObj.getMedicalRole() != null) {
            role = addendumObj.getMedicalRole();
        }
        System.out.println("Medical Role: " + role);

        // title
        String title = "";
        if (addendumObj.getTitle() != null) {
            title = addendumObj.getTitle();
        }
        System.out.println("Title: " + title);

        // body
        String data = "";
        if (addendumObj.getText() != null) {
            data = addendumObj.getText();
            // if the body of the element contains large string without 
            // any CR or LF, SAX parser will fail to load correct string
        }
        System.out.println("Data: " + data);

        ////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////
        try {
            byte[] decode = Base64.decode(data);
            // DEBUG
            //File f = new File("/DOLPHIN_PROJECT_MATERIALS/mmlQuery/decode/" + name);
            File f = new File(ClientContext.getUserDirectory() + "/" + name);
            
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(decode);
            fos.flush();
            fos.close();
            System.out.println("DECODED DATA WAS STORED IN : " + f.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        ////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////
    }
    
    public void handleMmlTree() {
        // get root object
        MMLObject obj = (MMLObject)dr.getMMLBuilder().getMmlTree().firstElement();
        if (obj == null) {
            System.out.println("*** COULDN'T GET MML OBJECT");
            return; 
        }

        //System.out.println(obj.getQName());

        // dispatch root element of the tree to the appropriate handler
        if (obj.getQName().equals("Mml")) {
            handleMml((Mml)obj);
        } else if (obj.getQName().equals("loginRequest")) {
            handleLoginRequest((loginRequest)obj);
        } else if (obj.getQName().equals("loginResult")) {
            handleLoginResult((loginResult)obj);        
        } else if (obj.getQName().equals("MmlAddendum")) {
            handleMmlAddendum((MmlAddendum)obj);
        }
    }
    
    public void processMmlString(String dataString) {
        //System.out.println("processMmlString");
        if (dataString == null) {
            // show message dialog here....
            System.out.println("********* dataString is null *********");
            return;
        }
        
        /*
        System.out.println(
            "RECEIVED DATA ******************************************\n" + 
            dataString + 
            "********************************************************" );
         */
        
        //***********************************************************************************
        // DEBUG         
        /*
        FileUtils.toFile(
            new String("/DOLPHIN_PROJECT_MATERIALS/dummy/" + MMLDate.getDateTime() + "RCV.txt").replaceAll(":", ""),
            dataString
        );
         */
         
        
        FileUtils.toFile(
            ClientContext.getUserDirectory() + "/" + new String(MMLDate.getDateTime() + "RCV.txt").replaceAll(":", ""),
            dataString
        );
        
        try { Thread.sleep(1000); } catch (Exception e) {}
        //***********************************************************************************
        
        //===================================
        loginSucceeded = false;
        resultSucceeded = false;
        
        if (extRefInContent != null) {
            extRefInContent.removeAllElements();
            extRefInContent = new Vector();
        }
        //===================================
        
        // call MML parser
        readMmlString(dataString);
        
        /////////////////////////////////////////////////////
        /////////////////////////////////////////////////////
        // get information about processing instructions
        Vector v = dr.getMMLBuilder().getMmlInstruction();
        
        // init succeededItems
        if (succeededItems != null) {
            succeededItems.removeAllElements();
        }
        succeededItems = new Vector();
        
        // parse instructions found in the result
        if (v != null && v.size() > 0) {
            for (int k = 0; k < v.size(); ++k) {
                String key = (String)v.elementAt(k);
                parse_Instruction(key, k);
            }
        }
        /////////////////////////////////////////////////////
        /////////////////////////////////////////////////////
        
        handleMmlTree();
        
        // DEBUG ===========
        //System.out.println("DEBUG START -------");
        //System.out.println(toStringMML());
        //System.out.println("DEBUG END ---------");
        // =================
    }
    
    //==========================================================================
    
    public void parse_mmlResult(String data) {
        // replace single quote with double quote
        data = data.replaceAll("\"", "'");
        data = data.replaceAll(" ", "");
        data = data.replaceAll("\r", "");
        data = data.replaceAll("\n", "");
        //System.out.println(data);
        
        // status
        int n1 = data.indexOf("status");
        if (n1 >= 0) {
            // status='success' or 'failed'
            String s = data.substring(n1 + 6 + 1 + 1, data.length());
            s = s.substring(0, s.indexOf("'"));
            System.out.println("status: " + s);
            //================================================
            if (s.equals("success")) {
                resultSucceeded = true;
                System.out.println("*** mmlResult SUCCEEDED:)");
            } else {
                resultSucceeded = false;
                System.out.println("*** mmlResult FAILED:(");
            }
            //================================================
        }
        
        // continue
        int n2 = data.indexOf("continue");
        if (n2 >= 0) {
            String s = data.substring(n2 + 8 + 1 + 1, data.length());
            s = s.substring(0, s.indexOf("'"));
            // true or false
            System.out.println("continue: " + s);
        }
        
        // partNo
        int n3 = data.indexOf("partNo");
        if (n3 >= 0) {
            String s = data.substring(n3 + 6 + 1 + 1, data.length());
            s = s.substring(0, s.indexOf("'"));
            // 0, 1, 2, ...
            System.out.println("partNo: " + s);
        }
        
        // reqId
        int n4 = data.indexOf("reqId");
        if (n4 >= 0) {
            String s = data.substring(n4 + 5 + 1 + 1, data.length());
            s = s.substring(0, s.indexOf("'"));
            System.out.println("reqId: " + s);
        } else {
            //----------------------------------------------------------------
            // because some program is likely to mistype reqId to reqid, 
            // we should be patient and generous for that:-)
            n4 = -1;
            n4 = data.indexOf("reqid");
            if (n4 >= 0) {
                String s = data.substring(n4 + 5 + 1 + 1, data.length());
                s = s.substring(0, s.indexOf("'"));
                System.out.println("reqId was mistyped as reqid: " + s);
            }
            //----------------------------------------------------------------
        }
        
        // errorReason
        int n5 = data.indexOf("errorReason");
        if (n5 >= 0) {
            String s = data.substring(n5 + 11 + 1 + 1, data.length());
            s = s.substring(0, s.indexOf("'"));
            // SYSTEMERROR, 
            // HEADERINVALID, 
            // NOTSUPPORTED, 
            // REQUESTDENIED
            System.out.println("errorReason: " + s);
        }
    }
    
    public void parse_mmlItemResult(String data, int index) {
        //==========================================================
        // GET PARENT OBJECT ANYWAY:-)
        Vector ht = dr.getMMLBuilder().getMmlInstructionTable();
        MMLObject obj = null;
        if (ht != null && ht.size() > 0) {
            obj = (MMLObject)ht.elementAt(index);
        }
        //==========================================================
        
        // replace single quote with double quote
        data = data.replaceAll("\"", "'");
        data = data.replaceAll(" ", "");
        data = data.replaceAll("\r", "");
        data = data.replaceAll("\n", "");
        //System.out.println(data);
        
        // status
        int n1 = data.indexOf("status");
        if (n1 >= 0) {
            // status='success' or 'failed'
            String s = data.substring(n1 + 6 + 1 + 1, data.length());
            s = s.substring(0, s.indexOf("'"));
            System.out.println("status: " + s);
            
            if (s.equals("success")){
                succeededItems.addElement(obj);
            }
        }
        
        // reqId
        int n4 = data.indexOf("reqId");
        if (n4 >= 0) {
            String s = data.substring(n4 + 5 + 1 + 1, data.length());
            s = s.substring(0, s.indexOf("'"));
            System.out.println("reqId: " + s);
        } else {
            //----------------------------------------------------------------
            // because some program is likely to mistype reqId to reqid, 
            // we should be patient and generous for that:-)
            n4 = -1;
            n4 = data.indexOf("reqid");
            if (n4 >= 0) {
                String s = data.substring(n4 + 5 + 1 + 1, data.length());
                s = s.substring(0, s.indexOf("'"));
                System.out.println("reqId was mistyped as reqid: " + s);
            }
            //----------------------------------------------------------------
        }
        
        // errorReason
        int n5 = data.indexOf("errorReason");
        if (n5 >= 0) {
            String s = data.substring(n5 + 11 + 1 + 1, data.length());
            s = s.substring(0, s.indexOf("'"));
            // SYSTEMERROR, 
            // HEADERINVALID,
            // NOTHINGDATA, 
            // ACCESSDENIED, 
            // NOTSUPPORTED
            System.out.println("errorReason: " + s);
        }
    }
    
    public void parse_Instruction(String s, int index) {
        // it is assumed that the string s has the form of "command<>data" like
        // "mmlResult<>status=\"success\"...."
        System.out.println("parse_Instruction: " + s + "_____index: " + index);
        
        int i = s.indexOf("<>");
        if (i < 0) return;
        
        String command = s.substring(0, i);
        String data = s.substring(i + 2, s.length());
        
        if (command.equals("mmlResult")) {
            System.out.println("Parsing <?" + command);
            parse_mmlResult(data);
        } else if (command.equals("mmlItemResult")) {
            System.out.println("Parsing <?" + command);
            parse_mmlItemResult(data, index);// to get parent MmlModuleItem, the index is passed too.
        }
    }
}
