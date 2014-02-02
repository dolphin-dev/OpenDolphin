/*
 * AreaDataBean.java
 *
 * Created on 2001/12/15, 21:08
 */

package jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans;

import jp.ac.kumamoto_u.kuh.fc.jsato.math_mml.*;
import jp.ac.kumamoto_u.kuh.fc.jsato.xslt.*;
import javax.swing.*;

import open.dolphin.client.*;
import open.dolphin.infomodel.ID;
import open.dolphin.plugin.*;
import open.dolphin.project.*;
import open.dolphin.util.*;

//-------------------------------------

import java.io.*;
import java.util.*;

import java.awt.event.*;
import java.awt.*;
//-------------------------------------

import java.util.Calendar;
import java.util.GregorianCalendar;
import jp.ac.kumamoto_u.kuh.fc.jsato.*;
/**
 *
 * @author  Junzo SATO
 */
public class AreaDataBean extends javax.swing.JPanel {
    // to avoid sharing socket, we simply prepare BackGround object for each instance.
    
    private BackGround BackGround = null;
    
    private String patientId;
    private boolean isLocalId;
    private IChartContext context;
    
    String fromDate = null;
    String toDate = null;
    
    /** Creates new form AreaDataBean */
    public AreaDataBean() {
        initComponents();
        
        // set term by some months before today...
        fromDate = getDate(-3);// the day before one year from today.
        toDate = getDate(0);// today
        
        tfFromDate.setText(fromDate);
        tfToDate.setText(toDate);        
    }

    public AreaDataBean(IChartContext context) {
        super();
        initComponents();
        
        this.context = context;
        
        if (Project.getLocalCode() == Project.KUMAMOTO) {
            isLocalId = true;
        } else {
            isLocalId = false;
        }
        
        ID id = Project.getMasterId(context.getPatient().getId());
        // 2003-09-02 Minagawa
        if (id != null) {
            patientId = id.getId();
        } else {
            patientId = null;
        }
        
        //jLabel1.setText(patientId);
        //jLabel2.setText(String.valueOf(isLocalId));

        // set term by some months before today...
        fromDate = getDate(-3);// the day before one year from today.
        toDate = getDate(0);// today
        
        tfFromDate.setText(fromDate);
        tfToDate.setText(toDate);
        
        // 2003-09-02 Minagawa
        if (patientId == null) {
            btnLogin.setEnabled(false);
            lblStatus.setText("患者IDが登録されていません");
        }
    }
    
    public String getDate(int monthOffset) {
        GregorianCalendar gc = new GregorianCalendar();
        //
        gc.add(Calendar.MONTH, monthOffset);
        //
        int year = gc.get(Calendar.YEAR);
        int month = gc.get(Calendar.MONTH) + 1;
        int day = gc.get(Calendar.DAY_OF_MONTH);
        
        String date = String.valueOf(year);
        date = date + "-";
        if (month < 10) {
            date = date + "0" + String.valueOf(month);
        } else {
            date = date + String.valueOf(month);
        }
        date = date + "-";
        if (day < 10) {
            date = date + "0" + String.valueOf(day);
        } else {
            date = date + String.valueOf(day);            
        }
        return date;
    }

    private String generateMml() {
        return "<?xml version='1.0' encoding='UTF-8'?>\n" + 
            "<Mml version=\"2.3\" createDate=\"" + MMLDate.getDateTime() + "\"" + 
                " " + "xmlns:xhtml=\"http://www.w3.org/1999/xhtml\"" + "\n" + 
                " " + "xmlns:mmlCm=\"http://www.medxml.net/MML/SharedComponent/Common/1.0\"" + "\n" + 
                " " + "xmlns:mmlNm=\"http://www.medxml.net/MML/SharedComponent/Name/1.0\"" + "\n" + 
                " " + "xmlns:mmlFc=\"http://www.medxml.net/MML/SharedComponent/Facility/1.0\"" + "\n" + 
                " " + "xmlns:mmlDp=\"http://www.medxml.net/MML/SharedComponent/Department/1.0\"" + "\n" + 
                " " + "xmlns:mmlAd=\"http://www.medxml.net/MML/SharedComponent/Address/1.0\"" + "\n" + 
                " " + "xmlns:mmlPh=\"http://www.medxml.net/MML/SharedComponent/Phone/1.0\"" + "\n" + 
                " " + "xmlns:mmlPsi=\"http://www.medxml.net/MML/SharedComponent/PersonalizedInfo/1.0\"" + "\n" + 
                " " + "xmlns:mmlCi=\"http://www.medxml.net/MML/SharedComponent/CreatorInfo/1.0\"" + "\n" + 
                " " + "xmlns:mmlPi=\"http://www.medxml.net/MML/ContentModule/PatientInfo/1.0\"" + "\n" + 
                " " + "xmlns:mmlBc=\"http://www.medxml.net/MML/ContentModule/BaseClinic/1.0\"" + "\n" + 
                " " + "xmlns:mmlFcl=\"http://www.medxml.net/MML/ContentModule/FirstClinic/1.0\"" + "\n" + 
                " " + "xmlns:mmlHi=\"http://www.medxml.net/MML/ContentModule/HealthInsurance/1.1\"" + "\n" + 
                " " + "xmlns:mmlLs=\"http://www.medxml.net/MML/ContentModule/Lifestyle/1.0\"" + "\n" + 
                " " + "xmlns:mmlPc=\"http://www.medxml.net/MML/ContentModule/ProgressCourse/1.0\"" + "\n" + 
                " " + "xmlns:mmlRd=\"http://www.medxml.net/MML/ContentModule/RegisteredDiagnosis/1.0\"" + "\n" + 
                " " + "xmlns:mmlSg=\"http://www.medxml.net/MML/ContentModule/Surgery/1.0\"" + "\n" + 
                " " + "xmlns:mmlSm=\"http://www.medxml.net/MML/ContentModule/Summary/1.0\"" + "\n" + 
                " " + "xmlns:mmlLb=\"http://www.medxml.net/MML/ContentModule/test/1.0\"" + "\n" + 
                " " + "xmlns:mmlRp=\"http://www.medxml.net/MML/ContentModule/report/1.0\"" + "\n" + 
                " " + "xmlns:mmlRe=\"http://www.medxml.net/MML/ContentModule/Referral/1.0\"" + "\n" + 
                " " + "xmlns:mmlSc=\"http://www.medxml.net/MML/SharedComponent/Security/1.0\"" + "\n" + 
                " " + "xmlns:claim=\"http://www.medxml.net/claim/claimModule/2.1\"" + "\n" + 
                " " + "xmlns:claimA=\"http://www.medxml.net/claim/claimAmountModule/2.1\"" + ">\n";
    }
    
    private String generateToc() {
        return "\t" +  "<toc>\n" + 
            "\t\t" + "<tocItem>" + "http://www.medxml.net/MML/SharedComponent/Common/1.0"               + "</tocItem>\n" + 
            "\t\t" + "<tocItem>" + "http://www.medxml.net/MML/SharedComponent/Name/1.0"                 + "</tocItem>\n" + 
            "\t\t" + "<tocItem>" + "http://www.medxml.net/MML/SharedComponent/Facility/1.0"             + "</tocItem>\n" + 
            "\t\t" + "<tocItem>" + "http://www.medxml.net/MML/SharedComponent/Department/1.0"           + "</tocItem>\n" + 
            "\t\t" + "<tocItem>" + "http://www.medxml.net/MML/SharedComponent/Address/1.0"              + "</tocItem>\n" + 
            "\t\t" + "<tocItem>" + "http://www.medxml.net/MML/SharedComponent/Phone/1.0"                + "</tocItem>\n" + 
            "\t\t" + "<tocItem>" + "http://www.medxml.net/MML/SharedComponent/PersonalizedInfo/1.0"     + "</tocItem>\n" + 
            "\t\t" + "<tocItem>" + "http://www.medxml.net/MML/SharedComponent/CreatorInfo/1.0"          + "</tocItem>\n" + 
            "\t\t" + "<tocItem>" + "http://www.medxml.net/MML/ContentModule/PatientInfo/1.0"            + "</tocItem>\n" + 
            "\t\t" + "<tocItem>" + "http://www.medxml.net/MML/ContentModule/BaseClinic/1.0"             + "</tocItem>\n" + 
            "\t\t" + "<tocItem>" + "http://www.medxml.net/MML/ContentModule/FirstClinic/1.0"            + "</tocItem>\n" + 
            "\t\t" + "<tocItem>" + "http://www.medxml.net/MML/ContentModule/HealthInsurance/1.1"        + "</tocItem>\n" + 
            "\t\t" + "<tocItem>" + "http://www.medxml.net/MML/ContentModule/Lifestyle/1.0"              + "</tocItem>\n" + 
            "\t\t" + "<tocItem>" + "http://www.medxml.net/MML/ContentModule/ProgressCourse/1.0"         + "</tocItem>\n" + 
            "\t\t" + "<tocItem>" + "http://www.medxml.net/MML/ContentModule/RegisteredDiagnosis/1.0"    + "</tocItem>\n" + 
            "\t\t" + "<tocItem>" + "http://www.medxml.net/MML/ContentModule/Surgery/1.0"                + "</tocItem>\n" + 
            "\t\t" + "<tocItem>" + "http://www.medxml.net/MML/ContentModule/Summary/1.0"                + "</tocItem>\n" + 
            "\t\t" + "<tocItem>" + "http://www.medxml.net/MML/ContentModule/test/1.0"                   + "</tocItem>\n" + 
            "\t\t" + "<tocItem>" + "http://www.medxml.net/MML/ContentModule/report/1.0"                 + "</tocItem>\n" + 
            "\t\t" + "<tocItem>" + "http://www.medxml.net/MML/ContentModule/Referral/1.0"               + "</tocItem>\n" + 
            "\t\t" + "<tocItem>" + "http://www.medxml.net/MML/SharedComponent/Security/1.0"             + "</tocItem>\n" + 
            "\t\t" + "<tocItem>" + "http://www.medxml.net/claim/claimModule/2.1"                        + "</tocItem>\n" + 
            "\t\t" + "<tocItem>" + "http://www.medxml.net/claim/claimAmountModule/2.1"                  + "</tocItem>\n" + 
            "\t" + "</toc>\n";
    }
    
    public String setupMmlQueryTypeList() {
        String mmlString = generateMml();
        
        //==================================================================================================
        // don't use double quote for mmlQuery instruction:-)
        mmlString = mmlString + "<?mmlQuery type=\"list\" startDate=\"" + fromDate + "\" endDate=\"" + toDate + "\" reqId=\"" + Project.createUUID() + "\"?>\n";
        //==================================================================================================

        mmlString = mmlString + generateHeader();
        
        //----------------------------------------------------------------------
        // MmlBody
        mmlString = mmlString + "<MmlBody>\n";
        mmlString = mmlString + 
            "\t" + "<MmlModuleItem type=\"patientInfo\"/>\n" + 
            "\t" + "<MmlModuleItem type=\"healthInsurance\"/>\n" + 
            "\t" + "<MmlModuleItem type=\"registeredDiagnosis\"/>\n" + 
            "\t" + "<MmlModuleItem type=\"lifestyle\"/>\n" + 
            "\t" + "<MmlModuleItem type=\"baseClinic\"/>\n" + 
            "\t" + "<MmlModuleItem type=\"firstClinic\"/>\n" + 
            "\t" + "<MmlModuleItem type=\"progressCourse\"/>\n" + 
            "\t" + "<MmlModuleItem type=\"surgery\"/>\n" + 
            "\t" + "<MmlModuleItem type=\"summary\"/>\n" + 
            // we need not to share any CLAIM modules
            //"\t" + "<MmlModuleItem type=\"claim\"/>\n" + 
            //"\t" + "<MmlModuleItem type=\"claimAmount\"/>\n" + 
            "\t" + "<MmlModuleItem type=\"referral\"/>\n" + 
            "\t" + "<MmlModuleItem type=\"test\"/>\n" + 
            "\t" + "<MmlModuleItem type=\"report\"/>\n";
        mmlString = mmlString + "</MmlBody>\n";
        //----------------------------------------------------------------------
        
        mmlString = mmlString + "</Mml>";
        
        return mmlString;
    }

    private String generateHeader() {
        String s = "<MmlHeader>\n";
        
        // CreatorInfo
        StringWriter sw = null;
        BufferedWriter bw = null;
        String strCreatorInfo = "";
        try {
            sw = new StringWriter();
            bw = new BufferedWriter(sw);
            //Project.getCreatorInfo().writeMML(bw,0);
            bw.flush();
            sw.flush();
            strCreatorInfo = sw.toString();
            bw.close();
            sw.close();
            bw = null;
            sw = null;
        } catch (Exception e) {
            
        }
        
        s = s + strCreatorInfo;
        
        // masterId
        ID id = Project.getMasterId(context.getPatient().getId());
        
        String strMasterId = "";
        try {
            sw = new StringWriter();
            bw = new BufferedWriter(sw);
            //id.writeMML(bw,0);
            bw.flush();
            sw.flush();
            strMasterId = "\t" + "<masterId>\n" + 
                          "\t\t" + sw.toString() + 
                          "\t" + "</masterId>\n";
            bw.close();
            sw.close();
            bw = null;
            sw = null;
        } catch (Exception e) {
            
        }        
        
        s = s + strMasterId;
        
        // toc
        s = s + generateToc();
        
        s = s + "</MmlHeader>\n";
        
        return s;
    }
    
    public String setupMmlQueryTypePatient() {
        String mmlString = generateMml();
        
        //==================================================================================================
        // don't use double quote for mmlQuery instruction:-)
        mmlString = mmlString + "<?mmlQuery type=\"patient\" reqId=\"" + Project.createUUID() + "\"?>\n";
        //==================================================================================================

        mmlString = mmlString + generateHeader();
        
        //----------------------------------------------------------------------
        // MmlBody
        mmlString = mmlString + "<MmlBody>\n";
        mmlString = mmlString + 
            "\t" + "<MmlModuleItem type=\"patientInfo\"/>\n" + 
            "\t" + "<MmlModuleItem type=\"healthInsurance\"/>\n" + 
            "\t" + "<MmlModuleItem type=\"registeredDiagnosis\"/>\n" + 
            "\t" + "<MmlModuleItem type=\"lifestyle\"/>\n" + 
            "\t" + "<MmlModuleItem type=\"baseClinic\"/>\n" + 
            "\t" + "<MmlModuleItem type=\"firstClinic\"/>\n" + 
            "\t" + "<MmlModuleItem type=\"progressCourse\"/>\n" + 
            "\t" + "<MmlModuleItem type=\"surgery\"/>\n" + 
            "\t" + "<MmlModuleItem type=\"summary\"/>\n" + 
            "\t" + "<MmlModuleItem type=\"claim\"/>\n" + 
            "\t" + "<MmlModuleItem type=\"claimAmount\"/>\n" + 
            "\t" + "<MmlModuleItem type=\"referral\"/>\n" + 
            "\t" + "<MmlModuleItem type=\"test\"/>\n" + 
            "\t" + "<MmlModuleItem type=\"report\"/>\n";
        mmlString = mmlString + "</MmlBody>\n";
        //----------------------------------------------------------------------
        
        mmlString = mmlString + "</Mml>";
        
        return mmlString;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        tfFromDate = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        tfToDate = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        lblStatus = new javax.swing.JLabel();
        btnLogin = new javax.swing.JButton();
        jProgressBar1 = new javax.swing.JProgressBar();
        jScrollPane1 = new javax.swing.JScrollPane();

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        setEnabled(false);
        jPanel1.setMaximumSize(new java.awt.Dimension(32767, 36));
        jPanel1.setMinimumSize(new java.awt.Dimension(10, 36));
        jPanel1.setPreferredSize(new java.awt.Dimension(10, 36));
        jLabel1.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel1.setText("\u6587\u66f8\u78ba\u5b9a\u65e5(CCYY-MM-DD)\u3067");
        jPanel1.add(jLabel1);

        tfFromDate.setText("2001-01-01");
        tfFromDate.setPreferredSize(new java.awt.Dimension(80, 24));
        jPanel1.add(tfFromDate);

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel2.setText("\u304b\u3089");
        jPanel1.add(jLabel2);

        tfToDate.setText("2002-01-01");
        tfToDate.setPreferredSize(new java.awt.Dimension(80, 24));
        jPanel1.add(tfToDate);

        jLabel3.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel3.setText("\u307e\u3067");
        jPanel1.add(jLabel3);

        add(jPanel1);

        jPanel2.setMaximumSize(new java.awt.Dimension(32767, 36));
        lblStatus.setText("\u5171\u6709\u30ab\u30eb\u30c6\u53c2\u7167\u306e\u6e96\u5099\u304c\u51fa\u6765\u3066\u3044\u307e\u3059\u3002");
        lblStatus.setMinimumSize(new java.awt.Dimension(300, 16));
        lblStatus.setPreferredSize(new java.awt.Dimension(300, 16));
        jPanel2.add(lblStatus);

        btnLogin.setText("\u53c2\u7167");
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });

        jPanel2.add(btnLogin);

        jPanel2.add(jProgressBar1);

        add(jPanel2);

        add(jScrollPane1);

    }//GEN-END:initComponents

    String bodyRequest = null;
    private void prepareForBodyRequest(Mml mmlObj) {
        // at now, mmlObj contains only succeeded list of module items.
        // each item has docInfo only.
        // we should construct mmlQuery string to request content of the item.
        if (mmlObj == null) return;
        
        //======================================================================
        // for the simplest implementation, all items are requested within one message
        //MmlHeader h = mmlObj.getMmlHeader();
        MmlBody b = mmlObj.getMmlBody();
        
        ////////////////////////////////
        ////////////////////////////////
        //
        // GROUP BY groupId
        // ORDER BY confirmDate
        System.out.println("*************************** START SORTING *************************");
        MmlProcessor p = new MmlProcessor();
        //p.processModules(p.getModules(mmlObj));
        
        // sort modules
        Vector resV = p.sortModules(p.getModules(mmlObj));
        // set result modules
        mmlObj.getMmlBody().setMmlModuleItem(resV);
        
        System.out.println("*************************** END SORTING ***************************");
        //
        ////////////////////////////////
        ////////////////////////////////
        String bodyString = toStringMML(b);
        
        bodyRequest = generateMml() +  
            "<?mmlQuery type=\"patient\" reqId=\"" + Project.createUUID() + "\"?>\n" + 
            generateHeader() + 
            bodyString +
            "</Mml>";
        
        System.out.println("BODY REQUEST ******************\n" + bodyRequest);
        //======================================================================
    }

    public String toStringMML(MMLObject obj) {
        return toStringMML(obj, false);// <?xml is not written
    }
    
    public String toStringMML(MMLObject obj, boolean writeXMLInstruction) {
        if (obj == null) return "";
        
        String resultString = "";
        try {
            StringWriter sw = null;
            BufferedWriter bw = null;
            PrintWriter pw = null;
            sw = new StringWriter();
            bw = new BufferedWriter(sw);
            pw = new PrintWriter(bw);

            // create visitor
            MMLVisitor v = new MMLVisitor(pw, writeXMLInstruction);
            v.visitMMLObject(obj);

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
    
    //***********************************************
    AreaDataTask task = null;
    javax.swing.Timer timer = null;
    final int TIMER_INTERVAL = 200;
    //***********************************************
    
    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed
        // Add your handling code here:

        fromDate = tfFromDate.getText();
        toDate = tfToDate.getText();
        
        lblStatus.setText("サーバーへ接続中...");
        
        task = new AreaDataTask(this);
        
        jProgressBar1.setMinimum(0);
        jProgressBar1.setMaximum(task.getLengthOfTask());
        jProgressBar1.setValue(jProgressBar1.getMinimum());
        
        timer = new javax.swing.Timer(TIMER_INTERVAL, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jProgressBar1.setValue(task.getCurrent());
                lblStatus.setText(task.getMessage());
                if (task.done()) {
                    Toolkit.getDefaultToolkit().beep();
                    timer.stop();       
                    
                    // disconnect -------------
                    BackGround.LogOut();
                    BackGround = null;
                    //-------------------------
                    
                    // stop indeterminate progress bar
                    jProgressBar1.setIndeterminate(false);
                    jProgressBar1.setValue(jProgressBar1.getMinimum());               
                    btnLogin.setEnabled(true);
                    
                    // check the result
                    /*
                     if (task.getMessage().equals("statMessage = "ドキュメント取得に成功しました。")) {
                        lblStatus.setText("結果を作成しています...");
                    }
                     */
                }
            }
        });
        
        btnLogin.setEnabled(false);
        // start indeterminate progress bar
        jProgressBar1.setIndeterminate(true);
        if (BackGround == null) {
            BackGround = new BackGround();
        }
        task.go();
        timer.start();
    }//GEN-LAST:event_btnLoginActionPerformed

    public boolean doLogin() {
        if (true == BackGround.RequestLogin()) {
            //btnList.setEnabled(true);
            return true;
        } else {
            return false;
        }
    }
    
    public boolean doList() {
        BackGround.SendQuery(setupMmlQueryTypeList());
        // blocking by waiting for the result
        if (BackGround.questionObj.getResultString().equals("succeeded")) {
            //btnBody.setEnabled(true);
            
            // get Mml object
            Mml mmlObj = BackGround.questionObj.getResultMmlObject();
            //System.out.println("RESULT MML OBJECT: " + mmlObj);
            
            //System.out.println("================= BEFORE ==================");
            //new MmlProcessor().processMml(mmlObj);
            //System.out.println("==================================================");
            
            // get succeeded MmlModuleItems
            Vector items = BackGround.questionObj.getSucceededItems();
            //if (items != null && items.size() > 0) {
            //    System.out.println("NUM OF ITEMS: " + items.size());               
            //}
            //System.out.println("================= ITEMS ==================");
            //new MmlProcessor().processModules(items);
            //System.out.println("==================================================");
            
            //===============================================
            // GENERATE BODY REQUEST USING mmlObj and items. 
            //===============================================
            
            // replace module items with succeeded items
            mmlObj.getMmlBody().setMmlModuleItem(items);
            // never forget to let the parent know about new child:-)
            
            // DEBUG
            //System.out.println("================= AFTER ==================");
            //new MmlProcessor().processMml(mmlObj);
            //System.out.println("==================================================");
            
            prepareForBodyRequest(mmlObj);
            
            return true;
        } else {
            return false;
        }
    }
    
    private String ResolvExtRef(String htmlString, Vector v) {
        if (htmlString == null) return null;
        if (v == null || v.size() <= 0) return htmlString;

        String keyword = "[外部参照あり]";
        String s = htmlString;
        StringBuffer sb = new StringBuffer(s);
        int start = -1, end = -1;
        int cnt = -1;
        while (true) {
            start = s.indexOf(keyword);
            System.out.println("indexOf(keyword): " + start);
            if (start < 0) break;
            end = start + keyword.length();

            cnt++;
            mmlCmextRef ref = (mmlCmextRef)v.elementAt(cnt);
            if (ref == null) {
                sb.replace(start, end, "[外部参照]");
                s = sb.toString();
                start = -1;
                end = -1;
                continue;
            }
            
            String title = " ";
            if ( ref.getMmlCmtitle() != null ) {
                title = ref.getMmlCmtitle();
                System.out.println(":-) *** title: " + title);
            }
            
            String href = " ";
            if ( ref.getMmlCmhref() != null ) {
                href = ref.getMmlCmhref();
                System.out.println(":-) *** href: " + href);
            }
            
            String type = " ";
            if ( ref.getMmlCmcontentType() != null ) {
                type = ref.getMmlCmcontentType();
                System.out.println(":-) *** contentType: " + type);
            }

            if (type.indexOf("image") >= 0) {
                // target would be an image file
                
                // generate html which contains <img src=.
                String pathname = ClientContext.getUserDirectory() + "/" + href + ".htm";
                FileUtils.toFile(
                    pathname, 
                    "<html><body><img src=" + href + "/></body></html>"
                );
                // specify file url by installed directory
                // DEBUG
                //System.out.println("--- Replace from " + start + " to " + end);
                //System.out.println("<a href=\"file:///c:/dolphin/" + href + ".htm"  + "\">" + "Ref: " + title + "</a>");
                //
                //sb.replace(start, end, "<a href=\"file:///c:/dolphin/" + href + ".htm"  + "\">" + "Ref: " + title + "</a>");
                sb.replace(start, end, "<a href=\"file:///" + ClientContext.getUserDirectory() + "/" + href + ".htm"  + "\">" + "Ref: " + title + "</a>");

            } else {
                // specify file url by installed directory
                // DEBUG
                //System.out.println("--- Replace from " + start + " to " + end);
                //System.out.println("<a href=\"file:///c:/dolphin/" + href + ".htm"  + "\">" + "Ref: " + title + "</a>");
                //
                //sb.replace(start, end, "<a href=\"file:///c:/dolphin/" + href + "\">" + "Ref: " + title + "</a>");
                sb.replace(start, end, "<a href=\"file:///" + ClientContext.getUserDirectory() + "/" + href + "\">" + "Ref: " + title + "</a>");
            }
            
            s = sb.toString();
            start = -1;
            end = -1;
        }

        /*
        System.out.println("---------------- extRef in content: " + v.size());
        for (int i=0; i < v.size(); ++i) {
            mmlCmextRef ref = (mmlCmextRef)v.elementAt(i);
            if (ref == null) continue;

            String type = " ";
            if ( ref.getMmlCmcontentType() != null ) {
                type = ref.getMmlCmcontentType();
                System.out.println(":-)*** contentType: " + type);
            }

            String role = " ";
            if ( ref.getMmlCmmedicalRole() != null ) {
                role = ref.getMmlCmmedicalRole();
                System.out.println(":-)*** medicalRole: " + role);
            }

            String title = " ";
            if ( ref.getMmlCmtitle() != null ) {
                title = ref.getMmlCmtitle();
                System.out.println(":-)*** title: " + title);
            }

            String href = " ";
            if ( ref.getMmlCmhref() != null ) {
                href = ref.getMmlCmhref();
                System.out.println(":-)*** href: " + href);
            }                
        }
        */
        
        return s;
    }
    
    public boolean doBody() {
        // DEBUG
        // send a fake message
        //BackGround.SendQuery(FileUtils.fromFile("/mmlQuery/bodyRequest.xml"));
        
        BackGround.SendQuery(bodyRequest);
        
        // blocking by waiting for the result
        if (BackGround.questionObj.getResultString() != null && 
            BackGround.questionObj.getResultString().equals("succeeded")) {
            
            // get result as Mml object
            Mml mmlObj = BackGround.questionObj.getResultMmlObject();
            if (mmlObj == null) {
                return false;
            }
             
            //------
            // NOTE:
            // because modules in the body could contain failed objects,
            // we should take only succeeded ones to handle the result.
            //
            // get succeeded modules
            Vector items = BackGround.questionObj.getSucceededItems();
            if (items == null) {
                return false;
            }
            
            // replace modules in the body with above succeeded items
            mmlObj.getMmlBody().setMmlModuleItem(items);
            //
            // get external references in contents
            Vector v = BackGround.questionObj.getExtRefInContent();
            /*
            if (v != null) {
                System.out.println("---------------- extRef in content: " + v.size());
                MmlProcessor mp = new MmlProcessor();
                mp.processExtRefs(v);
            }
             */
            
            // get informations about extRefs from items.
            //MmlProcessor mp = new MmlProcessor();
            //mp.processModules(items);
            //Vector v = mp.getAllExtRefs();
            
            /*
            if (v != null && v.size() > 0) {
                System.out.println("---------------- extRef in content: " + v.size());
                for (int i=0; i < v.size(); ++i) {
                    mmlCmextRef ref = (mmlCmextRef)v.elementAt(i);
                    if (ref == null) continue;
                    
                    String type = " ";
                    if ( ref.getMmlCmcontentType() != null ) {
                        type = ref.getMmlCmcontentType();
                        System.out.println(":-)*** contentType: " + type);
                    }

                    String role = " ";
                    if ( ref.getMmlCmmedicalRole() != null ) {
                        role = ref.getMmlCmmedicalRole();
                        System.out.println(":-)*** medicalRole: " + role);
                    }

                    String title = " ";
                    if ( ref.getMmlCmtitle() != null ) {
                        title = ref.getMmlCmtitle();
                        System.out.println(":-)*** title: " + title);
                    }

                    String href = " ";
                    if ( ref.getMmlCmhref() != null ) {
                        href = ref.getMmlCmhref();
                        System.out.println(":-)*** href: " + href);
                    }                
                }
            }
             */
            
            //------
            // get xmlString
            String xmlString = toStringMML(mmlObj, true);
            
            //====================================================
            // DEBUG
            //FileUtils.toFile("/dummy/RCV.xml", xmlString);
            FileUtils.toFile(
                ClientContext.getUserDirectory() + "/" + new String(MMLDate.getDateTime() + "XML.txt").replaceAll(":", ""),
                xmlString
            );
            try { Thread.sleep(1000); } catch (Exception e) {}
            
            //====================================================
            // generate html
            // JEditorPane ep = XSLT.createHTMLPane(xmlString);
            // 
            // instead of generating html view from result html string,
            // we store it to the local directory.
            JEditorPane ep = new JEditorPane();
            //
            String htmlString = XSLT.toHTML(xmlString);
            htmlString = XSLT.getBodyContent(htmlString);
            //
            String name = new String(MMLDate.getDateTime() + ".htm").replaceAll(":", "");
            String pathname = ClientContext.getUserDirectory() + "/" + name;
            //==================================================
            // To handle extRefs in source xml and result html,
            // more adjustment is required.
            
            /*
            // DEBUG
            System.out.println("*************************************************");
            System.out.println("Vector v.size: " + v.size());
            for (int k = 0; k < v.size(); ++k) {
                mmlCmextRef er = (mmlCmextRef)v.elementAt(k);
                System.out.println(k + ": " + er.getMmlCmhref());
            }
            System.out.println("*************************************************");
            */
            
            htmlString = ResolvExtRef(htmlString, v);

            //
            //==================================================
            FileUtils.toFile(
                pathname,
                "<html>" + htmlString + "</html>"
            );
            
            /*
            FileUtils.toFile(
                new String("/dummy/HTML" + MMLDate.getDateTime() + ".htm").replaceAll(":", ""),
                htmlString
            );
             */
            //
            try {
                Thread.sleep(1000);
                HyperlinkHandler hh = new HyperlinkHandler(ep);
                //hh.ReadFile(new File(pathname));
                hh.ReadFileNoThreaded(new File(pathname));
                //XSLT.showInFrame(ep);
                jScrollPane1.setViewportView(ep);
            } catch (Exception e) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }
    
    // 変数宣言 - 編集不可//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField tfToDate;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField tfFromDate;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton btnLogin;
    private javax.swing.JProgressBar jProgressBar1;
    // 変数宣言の終わり//GEN-END:variables

}
