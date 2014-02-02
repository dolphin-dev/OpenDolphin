/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.utilities.common;

import java.io.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 * XMLクラス
 * @author S.Oh@Life Sciences Computing Corporation.
 */
public class XML extends AbstractCommonFunc {
    private Element rootElement;
    private Document xmlDocument;
    
    /**
     * コンストラクタ
     */
    protected XML() {
        super();
        Init();
    }
    
    /**
     * 初期化
     */
    void Init() {
        rootElement = null;
        xmlDocument = null;
    }

    /**
     * XMLデータの展開
     * @param xml XMLデータ
     * @throws ParserConfigurationException
     * @throws SAXException 
     * @throws IOException
     */
    protected boolean analizeXML(String xml, String charset) throws ParserConfigurationException, SAXException, IOException {
        // ドキュメントビルダーファクトリの生成
        DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
        // ドキュメントビルダーの生成
        DocumentBuilder builder = dbfactory.newDocumentBuilder();
        // パースを実行してDocumentオブジェクトを取得
        InputStream is = null;
        if(charset == null || charset.length() <= 0) {
            is = new ByteArrayInputStream(xml.getBytes());
        }else{
            is = new ByteArrayInputStream(xml.getBytes(charset));
        }
        xmlDocument = builder.parse(is);

        // ルート要素の取得
        rootElement = xmlDocument.getDocumentElement();
        
        return xmlDocument.hasChildNodes();
    }
    
    /**
     * XMLファイルの展開
     * @param xml XMLファイル
     * @throws ParserConfigurationException
     * @throws SAXException 
     * @throws IOException
     */
    protected boolean analizeXML(FileInputStream xml) throws ParserConfigurationException, SAXException, IOException {
        // ドキュメントビルダーファクトリの生成
        DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
        // ドキュメントビルダーの生成
        DocumentBuilder builder = dbfactory.newDocumentBuilder();
        // パースを実行してDocumentオブジェクトを取得
        xmlDocument = builder.parse(new BufferedInputStream(xml));
        xmlDocument.normalize();

        // ルート要素の取得
        rootElement = xmlDocument.getDocumentElement();
        
        if(getDebug()) System.out.println("ルート: " + rootElement.getTagName());

        return xmlDocument.hasChildNodes();
    }
    
    /**
     * ルート要素の取得
     * @return 要素
     */
    protected Element getRootElement() {
        return rootElement;
    }
    
    /**
     * 要素の数の取得
     * @param element 要素
     * @param tag 要素値
     * @return 要素数
     */
    protected int getElementNum(Element element, String tag) {
        NodeList list = rootElement.getElementsByTagName(tag);
        return list.getLength();
    }
    
    /**
     * 要素の数の取得
     * @param element 要素
     * @return 要素数
     */
    protected int getElementNum(Element element) {
        NodeList list = rootElement.getChildNodes();
        return list.getLength();
    }

    /**
     * 指定した属性から要素を取得
     * @param parentElement 親要素
     * @param tag 要素値
     * @param attribute 属性
     * @param data 属性値
     * @return 要素
     */
    protected Element getElement(Element parentElement, String tag, String attribute, String data) {
        Element element = null;
        // 各ノードリストの取得
        NodeList list = parentElement.getElementsByTagName(tag);
        for(int i = 0; i < list.getLength(); i++) {
            element = (Element)list.item(i);
            String val = element.getAttribute(attribute);
            if(val.equals(data)) {
                break;
            }
        }
        return element;
    }

    /**
     * 指定したインデックスの要素を取得
     * @param parentElement 親要素
     * @param tag 要素値
     * @param int 要素インデックス
     * @return 要素
     */
    protected Element getElement(Element parentElement, String tag, int idx) {
        NodeList list = parentElement.getElementsByTagName(tag);
        Element element = (Element)list.item(idx);
        return element;
    }

    /**
     * 指定したインデックスの要素を取得
     * @param parentElement 親要素
     * @param int 要素インデックス
     * @return 要素
     */
    protected Element getElement(Element parentElement, int idx) {
        NodeList list = parentElement.getChildNodes();
        Element element = (Element)list.item(idx);
        return element;
    }
    
    /**
     * 要素値の取得
     * @param element 要素
     * @return 要素値
     */
    protected String getElementValue(Element element) {
        Node node = element.getFirstChild();
        if(node != null && node.getNodeType() == Node.TEXT_NODE) {
            return node.getNodeValue();
        }
        return "";
    }
    
    /**
     * 属性値の取得
     * @param element 要素
     * @param attribute 属性
     * @return 属性値
     */
    protected String getAttributeValue(Element element, String attribute) {
        String ret = "";
        if(element != null && element.hasAttribute(attribute)) {
            ret = element.getAttribute(attribute);
        }
        return ret;
    }
    
    /**
     * 全要素の取得
     * @param node ノード
     */
    protected void getAllElement(Node node) {
        Node child = node.getFirstChild();
        while(child != null) {
            if(getDebug()) {
                System.out.println(node.getNodeName() + " ");
                System.out.println(node.getNodeType() + " ");
                System.out.println(node.getNodeValue() + " ");
            }
            
            // 子ノードの取得
            getAllElement(child);
            // 次のノードの取得
            child = child.getNextSibling();
        }
    }
    
    /**
     * 全属性の取得
     * @param element 要素
     */
    protected void getAllAttribut(Element element) {
        NamedNodeMap attrs = element.getAttributes();
        if(attrs != null) {
            for(int i = 0; i < attrs.getLength(); i++) {
                Node attr = attrs.item(i);
                if(getDebug()) {
                    System.out.println(attr.getNodeName());
                    System.out.println(attr.getNodeValue());
                }
            }
        }
    }
    
    /**
     * XMLの作成
     * @param root ルート
     * @throws ParserConfigurationException 
     */
    protected void createXML(String root) throws ParserConfigurationException {
        DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docbuilder = dbfactory.newDocumentBuilder();
        xmlDocument = docbuilder.newDocument();
        
        // ルート要素の追加
        rootElement = xmlDocument.createElement(root);
        xmlDocument.appendChild(rootElement);
    }
    
    /**
     * 子要素の追加
     * @param parentElement 親要素
     * @param child 子の要素文字列
     * @param text 子の要素値
     * @return 要素
     */
    protected Element appendChildElement(Element parentElement, String child, String text) {
        // 子要素の追加
        Element element = xmlDocument.createElement(child);
        parentElement.appendChild(element);
        // 値の設定
        if(text != null && text.length() > 0) {
            Text textContent = xmlDocument.createTextNode(text);
            element.appendChild(textContent);
        }
        
        return element;
    }
    
    /**
     * 子属性の追加
     * @param element 要素
     * @param name 属性名
     * @param val 属性値
     */
    protected void setChildAttibute(Element element, String name, String val) {
        // 属性の追加
        element.setAttribute(name, val);
    }
    
    /**
     * XMLファイルの保存
     * @param filePath ファイルパス
     * @param encoding エンコード
     * @throws TransformerConfigurationException
     * @throws TransformerException 
     */
    protected void saveXML(String filePath, String encoding) throws TransformerConfigurationException, TransformerException {
        // DOMオブジェクトを出力
        TransformerFactory tfactory = TransformerFactory.newInstance();
        Transformer transformer = tfactory.newTransformer();
        if(encoding != null && encoding.length() > 0) {
            transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
        }
        File outfile = new File(filePath);
        xmlDocument.setXmlStandalone(false);
        transformer.transform(new DOMSource(xmlDocument), new StreamResult(outfile));
    }
}
