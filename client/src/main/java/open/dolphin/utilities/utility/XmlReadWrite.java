/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.utilities.utility;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import open.dolphin.utilities.common.XML;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * XMLライブラリクラス
 * @author S.Oh@Life Sciences Computing Corporation.
 */
public class XmlReadWrite extends XML {
    /**
     * コンストラクタ
     */
    public XmlReadWrite() {
        super();
    }
    
    /**
     * XMLの解析
     * @param xml XMLデータ
     * @return 成功/失敗
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException 
     */
    public boolean analize(String xml, String charset) throws ParserConfigurationException, SAXException, IOException {
        boolean ret = analizeXML(xml, charset);
        if(ret) {
            ret = getRoot().hasChildNodes();
        }
        return ret;
    }
    
    /**
     * ルートの取得
     * @return 要素
     */
    public Element getRoot() {
        return getRootElement();
    }
    
    /**
     * 要素の数の取得
     * @param ele 要素
     * @return 要素数
     */
    public int getEleNum(Element ele) {
        return getElementNum(ele);
    }
    
    /**
     * 要素の取得
     * @param parent 親要素
     * @param idx 要素インデックス
     * @return 要素
     */
    public Element getEle(Element parent, int idx) {
        return getElement(parent, idx);
    }
    
    /**
     * 要素値の取得
     * @param ele 要素
     * @return 要素値
     */
    public String getEleVal(Element ele) {
        return getElementValue(ele);
    }

    /**
     * 属性値の取得
     * @param ele 要素
     * @param atrb 属性
     * @return 属性値
     */
    public String getAtrbValue(Element ele, String atrb) {
        return getAttributeValue(ele, atrb);
    }
    
    /**
     * XMLの作成
     * @param root ルート
     * @throws ParserConfigurationException 
     */
    public void create(String root) throws ParserConfigurationException {
        createXML(root);
    }
    
    /**
     * XMLの保存
     * @param path ファイルパス
     * @param encoding エンコード
     * @throws TransformerConfigurationException
     * @throws TransformerException 
     */
    public void save(String path, String encoding) throws TransformerConfigurationException, TransformerException {
        saveXML(path, encoding);
    }
    
    /**
     * 要素の追加
     * @param parentElement 親要素
     * @param child 子の要素文字列
     * @param text 子の要素値
     */
    public void addElement(Element parentElement, String child, String text) {
        appendChildElement(parentElement, child, text);
    }
    
    /**
     * デバッグ情報の有無設定
     * @param dbg デバッグ情報の有無
     */
    public void debug(boolean dbg) {
        setDebug(dbg);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    }
}
