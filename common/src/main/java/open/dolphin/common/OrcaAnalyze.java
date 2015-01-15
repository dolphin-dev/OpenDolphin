/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package open.dolphin.common;

import com.sun.org.apache.xpath.internal.XPathAPI;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.SAXException;

/**
 *
 * @author S.Oh@Life Sciences Computing Corporation.
 */
public class OrcaAnalyze {
    /**
     * コンストラクタ
     */
    public OrcaAnalyze() {
        
    }
    
    public void analisisSampleXml(String statement) {
        DocumentBuilderFactory factory;
        DocumentBuilder builder;
        Document doc;
        
        try {
            factory = DocumentBuilderFactory.newInstance();
            builder = factory.newDocumentBuilder();
            doc = builder.parse(new ByteArrayInputStream(statement.getBytes("UTF-8")));
            
            Node pNode = XPathAPI.selectSingleNode(doc, "/xmlio2/patientinfores/Patient_Information");
            if(pNode != null) {
                Node node;
                // 患者ID
                node = XPathAPI.selectSingleNode(pNode, "Patient_ID");
                String pid = (node != null) ? node.getFirstChild().getNodeValue() : null;
                
                // 保険情報
                Node hNode;
                NodeIterator ite = XPathAPI.selectNodeIterator(pNode, "HealthInsurance_Information/HealthInsurance_Information_child");
                while((hNode = ite.nextNode()) != null) {
                    // 保険の種類
                    node = XPathAPI.selectSingleNode(hNode, "InsuranceProvider_Class");
                    String insuranceProviderClass = (node != null) ? node.getFirstChild().getNodeValue() : null;
                }
            }
        } catch (SAXException ex) {
            Logger.getLogger(OrcaAnalyze.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OrcaAnalyze.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(OrcaAnalyze.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(OrcaAnalyze.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
