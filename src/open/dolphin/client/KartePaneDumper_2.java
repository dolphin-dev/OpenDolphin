package open.dolphin.client;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyleConstants;
import open.dolphin.infomodel.ModuleModel;
import open.dolphin.infomodel.SchemaModel;
import org.apache.log4j.Logger;

/**
 * KartePane の dumper
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class KartePaneDumper_2 {
    
    private static final String[] MATCHES = new String[] { "<", ">", "&", "'","\""};
    
    private static final String[] REPLACES = new String[] { "&lt;", "&gt;", "&amp;" ,"&apos;", "&quot;"};
    
    private ArrayList<ModuleModel> moduleList;
    
    private ArrayList<SchemaModel> schemaList;
    
    private String spec;
    
    private Logger logger;
    
    /** Creates a new instance of TextPaneDumpBuilder */
    public KartePaneDumper_2() {
        logger = ClientContext.getBootLogger();
    }
    
    /**
     * ダンプした Document の XML 定義を返す。
     *
     * @return Documentの内容を XML で表したもの
     */
    public String getSpec() {
        logger.debug(spec);
        return spec;
    }
    
    /**
     * ダンプした Documentに含まれている ModuleModelを返す。
     *
     * @return
     */
    public ModuleModel[] getModule() {
        
        ModuleModel[] ret = null;
        
        if ((moduleList != null) && (moduleList.size() > 0)) {
            ret = moduleList.toArray(new ModuleModel[moduleList.size()]);
        }
        return ret;
    }
    
    /**
     * ダンプした Documentに含まれている SchemaModel を返す。
     *
     * @return
     */
    public SchemaModel[] getSchema() {
        
        SchemaModel[] schemas = null;
        
        if ((schemaList != null) && (schemaList.size() > 0)) {
            
            schemas = schemaList.toArray(new SchemaModel[schemaList.size()]);
        }
        return schemas;
    }
    
    /**
     * 引数の Document をダンプする。
     *
     * @param doc ダンプするドキュメント
     */
    public void dump(DefaultStyledDocument doc) {
        
        StringWriter sw = new StringWriter();
        BufferedWriter writer = new BufferedWriter(sw);
        
        try {
            // ルート要素から再帰的にダンプする
            javax.swing.text.Element root = (javax.swing.text.Element) doc.getDefaultRootElement();
            writeElemnt(root, writer);
            
            // 出力バッファーをフラッシュしペインのXML定義を生成する
            writer.flush();
            writer.close();
            spec = sw.toString();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 要素を再帰的にダンプする。
     * @param element 要素
     * @param writer	出力ライター
     * @throws IOException
     * @throws BadLocationException
     */
    private void writeElemnt(javax.swing.text.Element element, Writer writer)
    throws IOException, BadLocationException {
        
        // 要素の開始及び終了のオフセット値を保存する
        int start = element.getStartOffset();
        int end = element.getEndOffset();
        logger.debug("start = " + start);
        logger.debug("end = " + end);
        
        // このエレメントの属性セットを得る
        AttributeSet atts = element.getAttributes().copyAttributes();
        
        // 属性値の文字列表現
        String asString = "";
        
        // 属性を調べる
        if (atts != null) {
            
            StringBuilder retBuffer = new StringBuilder();
            
            // 全ての属性を列挙する
            Enumeration names = atts.getAttributeNames();
            
            while (names.hasMoreElements()) {
                
                // 属性の名前を得る
                Object nextName = names.nextElement();
                
                if (nextName != StyleConstants.ResolveAttribute) {
                    
                    logger.debug("attribute name = " + nextName.toString());
                    
                    // $enameは除外する
                    if (nextName.toString().startsWith("$")) {
                        continue;
                    }
                    
                    // 属性= の形を準備する
                    retBuffer.append(" ");
                    retBuffer.append(nextName);
                    retBuffer.append("=");
                    
                    // foreground 属性の場合は再構築の際に利用しやすい形に分解する
                    if (nextName.toString().equals("foreground")) {
                        Color c = (Color) atts.getAttribute(StyleConstants.Foreground);
                        logger.debug("color = " + c.toString());
                        StringBuilder buf = new StringBuilder();
                        buf.append(String.valueOf(c.getRed()));
                        buf.append(",");
                        buf.append(String.valueOf(c.getGreen()));
                        buf.append(",");
                        buf.append(String.valueOf(c.getBlue()));
                        retBuffer.append(addQuote(buf.toString()));
                        
                    } else {
                        // 属性セットから名前をキーにして属性オブジェクトを取得する
                        Object attObject = atts.getAttribute(nextName);
                        logger.debug("attribute object = " + attObject.toString());
                        
                        if (attObject instanceof StampHolder) {
                            // スタンプの場合
                            if (moduleList == null) {
                                moduleList = new ArrayList<ModuleModel>();
                            }
                            StampHolder sh = (StampHolder) attObject;
                            moduleList.add((ModuleModel) sh.getStamp());
                            String value = String.valueOf(moduleList.size() - 1); // ペインに出現する順番をこの属性の値とする
                            retBuffer.append(addQuote(value));
                            
                        } else if (attObject instanceof SchemaHolder) {
                            // シュェーマの場合
                            if (schemaList == null) {
                                schemaList = new ArrayList<SchemaModel>();
                            }
                            SchemaHolder ch = (SchemaHolder) attObject;
                            schemaList.add(ch.getSchema());
                            String value = String.valueOf(schemaList.size() - 1); // ペインに出現する順番をこの属性の値とする
                            retBuffer.append(addQuote(value));
                            
                        } else {
                            // それ以外の属性についてはそのまま記録する
                            retBuffer.append(addQuote(attObject.toString()));
                        }
                    }
                }
            }
            asString = retBuffer.toString();
        }
        
        // <要素名 start="xx" end="xx" + asString>
        writer.write("<");
        writer.write(element.getName());
        writer.write(" start=");
        writer.write(addQuote(start));
        writer.write(" end=");
        writer.write(addQuote(end));
        writer.write(asString);
        writer.write(">");
        
        // content要素の場合はテキストを抽出する
        if (element.getName().equals("content")) {
            writer.write("<text>");
            int len = end - start;
            String text = element.getDocument().getText(start, len);
            logger.debug("text = " + text);
            
            // 特定の文字列を置換する
            for (int i = 0; i < REPLACES.length; i++) {
                text = text.replaceAll(MATCHES[i], REPLACES[i]);
            }
            writer.write(text);
            writer.write("</text>");

        }
        
        // 子要素について再帰する
        int children = element.getElementCount();
        for (int i = 0; i < children; i++) {
            writeElemnt(element.getElement(i), writer);
        }
        
        // この属性を終了する
        // </属性名>
        writer.write("</");
        writer.write(element.getName());
        writer.write(">");
    }
    
    private String addQuote(String str) {
        StringBuilder buf = new StringBuilder();
        buf.append("\"");
        buf.append(str);
        buf.append("\"");
        return buf.toString();
    }
    
    private String addQuote(int str) {
        StringBuilder buf = new StringBuilder();
        buf.append("\"");
        buf.append(str);
        buf.append("\"");
        return buf.toString();
    }
}