/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.utilities.utility;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

/**
 * FCR連携
 * @author S.Oh@Life Sciences Computing Corporation.
 */
public class FCRLink {
    private static final String FILE_NAME = "Ekarte.xml";
    private static final String ELEMENT_ROOT = "imageinfo";
    private static final String ELEMENT_PATIENTID = "patientid";
    private static final String ELEMENT_STUDYDATE = "studydate";
    private static final String ELEMENT_MODE = "mode";
    private static final String ELEMENT_STARTSTUDYDATE = "startstudydate";
    private static final String ELEMENT_ENDSTUDYDATE = "endstudydate";
    private static final String MODE_IMAGE = "0";
    private static final String MODE_LIST = "1";
    private static final String XML_ENCODE = "Shift_JIS";
    
    private String fullPath;
    
    /**
     * コンストラクタ
     * @param path 出力パス
     */
    public FCRLink(String path) {
        StringBuilder sb = new StringBuilder();
        sb.append(path);
        if(path.endsWith(File.separator) == false) {
            sb.append(File.separator).append(FILE_NAME);
        }else{
            sb.append(FILE_NAME);
        }
        fullPath = sb.toString();
    }
    
    /**
     * リストの表示
     * @param pID 患者ID
     * @throws ParserConfigurationException
     * @throws TransformerConfigurationException
     * @throws TransformerException 
     */
    public void linkList(String pID) throws ParserConfigurationException, TransformerConfigurationException, TransformerException {
        //outputXmlForFCR(pID, "", MODE_LIST);
        outputTextForFCR(pID, "", MODE_LIST);
    }
    
    /**
     * 画像の表示
     * @param pID 患者ID
     * @throws ParserConfigurationException
     * @throws TransformerConfigurationException
     * @throws TransformerException 
     */
    public void linkImage(String pID) throws ParserConfigurationException, TransformerConfigurationException, TransformerException {
        //outputXmlForFCR(pID, "", MODE_IMAGE);
        outputTextForFCR(pID, "", MODE_IMAGE);
    }
    
    /**
     * 全画像の表示
     * @param pID 患者ID
     * @param date 日付
     * @throws ParserConfigurationException
     * @throws TransformerConfigurationException
     * @throws TransformerException 
     */
    public void linkImage(String pID, String date) throws ParserConfigurationException, TransformerConfigurationException, TransformerException {
        //outputXmlForFCR(pID, date, MODE_IMAGE);
        outputTextForFCR(pID, date, MODE_IMAGE);
    }
    
    /**
     * XMLファイルの作成
     * @param pID 患者ID
     * @param date 日付
     * @param mode モード
     * @throws ParserConfigurationException
     * @throws TransformerConfigurationException
     * @throws TransformerException 
     */
    private void outputXmlForFCR(String pID, String date, String mode) throws ParserConfigurationException, TransformerConfigurationException, TransformerException {
        XmlReadWrite xml = new XmlReadWrite();
        xml.create(ELEMENT_ROOT);
        xml.addElement(xml.getRoot(), ELEMENT_PATIENTID, pID);
        xml.addElement(xml.getRoot(), ELEMENT_STUDYDATE, date);
        xml.addElement(xml.getRoot(), ELEMENT_MODE, mode);
        xml.addElement(xml.getRoot(), ELEMENT_STARTSTUDYDATE, "");
        xml.addElement(xml.getRoot(), ELEMENT_ENDSTUDYDATE, "");
        xml.save(fullPath, XML_ENCODE);
    }
    
    /**
     * XMLファイルの作成
     * @param pID 患者ID
     * @param date 日付
     * @param mode モード
     */
    private void outputTextForFCR(String pID, String date, String mode) {
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        try {
            PrintWriter pw = null;
            StringBuilder sb = new StringBuilder();
            File file = new File(fullPath);
            fos = new FileOutputStream(file);
            osw = new OutputStreamWriter(fos, XML_ENCODE);
            pw = new PrintWriter(osw);
            sb.append("<?xml version=\"1.0\" encoding=\"").append(XML_ENCODE).append("\"?>");
            sb.append("<imageinfo>");
            sb.append("<patientid>").append(pID).append("</patientid>");
            if(date == null || date.length() <= 0) {
                sb.append("<studydate/>");
            }else{
                sb.append("<studydate>").append(date).append("</studydate>");
            }
            sb.append("<mode>").append(mode).append("</mode>");
            sb.append("<startstudydate/>");
            sb.append("<endstudydate/>");
            sb.append("</imageinfo>");
            pw.write(sb.toString());
            pw.close();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(FCRLink.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FCRLink.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fos.close();
            } catch (IOException ex) {
                Logger.getLogger(FCRLink.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                osw.close();
            } catch (IOException ex) {
                Logger.getLogger(FCRLink.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
