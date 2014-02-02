
package cancer.document;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import open.dolphin.client.ClientContext;
import open.dolphin.client.KartePane;
import open.dolphin.infomodel.SchemaModel;
import org.apache.log4j.Logger;

import org.jdom.*;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;

/**
 * KarteRenderer_2
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class PhysioPaneRenderer {
    
    private static final String COMPONENT_ELEMENT_NAME = "component";
    
    private static final String SCHEMA_HOLDER = "schemaHolder";
    
    private static final int TT_SECTION = 0;
    
    private static final int TT_PARAGRAPH = 1;
    
    private static final int TT_CONTENT = 2;
    
    private static final int TT_ICON = 3;
    
    private static final int TT_COMPONENT = 4;
    
    private static final int TT_PROGRESS_COURSE = 5;
    
    private static final String SECTION_NAME = "section";
    
    private static final String PARAGRAPH_NAME = "paragraph";
    
    private static final String CONTENT_NAME = "content";
    
    private static final String COMPONENT_NAME = "component";
    
    private static final String ICON_NAME = "icon";
    
    private static final String ALIGNMENT_NAME = "Alignment";
    
    private static final String FOREGROUND_NAME = "foreground";
    
    private static final String SIZE_NAME = "size";
    
    private static final String BOLD_NAME = "bold";
    
    private static final String ITALIC_NAME = "italic";
    
    private static final String UNDERLINE_NAME = "underline";
    
    private static final String TEXT_NAME = "text";
    
    private static final String NAME_NAME = "name";
    
    private static final String LOGICAL_STYLE_NAME = "logicalStyle";
    
    private static final String PROGRESS_COURSE_NAME = "kartePane";
    
    private static final String[] REPLACES = new String[] { "<", ">", "&", "'" ,"\""};
    
    private static final String[] MATCHES = new String[] { "&lt;", "&gt;", "&amp;", "&apos;", "&quot;" };
    
    private KartePane physioPane;
    
    boolean logicalStyle;
    
    private List<SchemaModel> image;
    
    private Logger logger;
    
    /** Creates a new instance of TextPaneRestoreBuilder */
    public PhysioPaneRenderer(KartePane physioPane) {
        this.physioPane = physioPane;
        logger = ClientContext.getBootLogger();
    }
    
    /**
     * TextPane Dump の XML を解析する。
     * @param xml TextPane Dump の XML
     */
    public void renderPane(String xml, List<SchemaModel> image) {
        
        if (xml == null || xml.equals("")) {
            return;
        }
        
        this.image = image;
        
        debug(xml);
        
        SAXBuilder docBuilder = new SAXBuilder();
        
        try {
            StringReader sr = new StringReader(xml);
            Document doc = docBuilder.build(new BufferedReader(sr));
            org.jdom.Element root = (org.jdom.Element) doc.getRootElement();
            
            writeChildren(root);
        }
        // indicates a well-formedness error
        catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 子要素をパースする。
     * @param current 要素
     */
    private void writeChildren(org.jdom.Element current) {
        
        int eType = -1;
        String eName = current.getName();
        
        if (eName.equals(PARAGRAPH_NAME)) {
            eType = TT_PARAGRAPH;
            startParagraph(current.getAttributeValue(LOGICAL_STYLE_NAME),
                    current.getAttributeValue(ALIGNMENT_NAME));
            
        } else if (eName.equals(CONTENT_NAME) && (current.getChild(TEXT_NAME) != null)) {
            eType = TT_CONTENT;
            startContent(current.getAttributeValue(FOREGROUND_NAME), 
                    current.getAttributeValue(SIZE_NAME), 
                    current.getAttributeValue(BOLD_NAME), 
                    current.getAttributeValue(ITALIC_NAME), 
                    current.getAttributeValue(UNDERLINE_NAME), 
                    current.getChildText(TEXT_NAME));
            
        } else if (eName.equals(COMPONENT_NAME)) {
            eType = TT_COMPONENT;
            startComponent(current.getAttributeValue(NAME_NAME), // compoenet=number
                    current.getAttributeValue(COMPONENT_ELEMENT_NAME));
            
        } else if (eName.equals(ICON_NAME)) {
            eType = TT_ICON;
            startIcon(current);
            
        } else if (eName.equals(PROGRESS_COURSE_NAME)) {
            eType = TT_PROGRESS_COURSE;
            startProgressCourse();
            
        } else if (eName.equals(SECTION_NAME)) {
            eType = TT_SECTION;
            startSection();
            
        } else {
            debug("Other element:" + eName);
        }
        
        // 子を探索するのはパラグフとトップ要素のみ
        if (eType == TT_PARAGRAPH || eType == TT_PROGRESS_COURSE
                || eType == TT_SECTION) {
            
            java.util.List children = (java.util.List) current.getChildren();
            Iterator iterator = children.iterator();
            
            while (iterator.hasNext()) {
                org.jdom.Element child = (org.jdom.Element) iterator.next();
                writeChildren(child);
            }
        }
        
        switch (eType) {
            
            case TT_PARAGRAPH:
                endParagraph();
                break;
                
            case TT_CONTENT:
                endContent();
                break;
                
            case TT_ICON:
                endIcon();
                break;
                
            case TT_COMPONENT:
                endComponent();
                break;
                
            case TT_PROGRESS_COURSE:
                endProgressCourse();
                break;
                
            case TT_SECTION:
                endSection();
                break;
        }
    }
    
    private void startSection() {
    }
    
    private void endSection() {
    }
    
    private void startProgressCourse() {
    }
    
    private void endProgressCourse() {
    }
    
    private void startParagraph(String lStyle, String alignStr) {
        
        // if (lStyle != null) {
        physioPane.setLogicalStyle("default");
        logicalStyle = true;
        // }
        
        if (alignStr != null) {
            DefaultStyledDocument doc = (DefaultStyledDocument) physioPane
                    .getTextPane().getDocument();
            Style style0 = doc.getStyle("default");
            Style style = doc.addStyle("alignment", style0);
            if (alignStr.equals("0")) {
                StyleConstants.setAlignment(style, StyleConstants.ALIGN_LEFT);
            } else if (alignStr.equals("1")) {
                StyleConstants.setAlignment(style, StyleConstants.ALIGN_CENTER);
            } else if (alignStr.equals("2")) {
                StyleConstants.setAlignment(style, StyleConstants.ALIGN_RIGHT);
            }
            physioPane.setLogicalStyle("alignment");
            logicalStyle = true;
        }
    }
    
    private void endParagraph() {
        //thePane.makeParagraph(); // trim() の廃止で廃止
        if (logicalStyle) {
            physioPane.clearLogicalStyle();
            logicalStyle = false;
        }
    }
    
    private void startContent(String foreground, String size, String bold,
            String italic, String underline, String text) {
        
        // 特殊文字を戻す
        for (int i = 0; i < REPLACES.length; i++) {
            text = text.replaceAll(MATCHES[i], REPLACES[i]);
        }
        
        // このコンテントに設定する AttributeSet
        MutableAttributeSet atts = new SimpleAttributeSet();
        //StyleContext sc = StyleContext.getDefaultStyleContext();
        //AttributeSet atts = sc.getEmptySet();
        
        
        // foreground 属性を設定する
        if (foreground != null) {
            StringTokenizer stk = new StringTokenizer(foreground, ",");
            if (stk.hasMoreTokens()) {
                int r = Integer.parseInt(stk.nextToken());
                int g = Integer.parseInt(stk.nextToken());
                int b = Integer.parseInt(stk.nextToken());
                //atts = sc.addAttribute(atts, StyleConstants.Foreground, new Color(r, g, b));
                StyleConstants.setForeground(atts, new Color(r, g, b));
            }
        }
        
        // size 属性を設定する
        if (size != null) {
            //atts = sc.addAttribute(atts, StyleConstants.FontSize, Integer.parseInt(size));
            StyleConstants.setFontSize(atts, Integer.parseInt(size));
        }
        
        // bold 属性を設定する
        if (bold != null) {
            //atts = sc.addAttribute(atts, StyleConstants.Bold, Boolean.valueOf(bold).booleanValue());
            StyleConstants.setBold(atts, Boolean.valueOf(bold).booleanValue());
        }
        
        // italic 属性を設定する
        if (italic != null) {
            //atts = sc.addAttribute(atts, StyleConstants.Italic, Boolean.valueOf(italic).booleanValue());
            StyleConstants.setItalic(atts, Boolean.valueOf(italic)
            .booleanValue());
        }
        
        // underline 属性を設定する
        if (underline != null) {
            //atts = sc.addAttribute(atts, StyleConstants.Underline, Boolean.valueOf(underline).booleanValue());
            StyleConstants.setUnderline(atts, Boolean.valueOf(underline)
            .booleanValue());
        }
        
        // テキストを挿入する
        physioPane.insertFreeString(text, atts);
    }
    
    private void endContent() {
    }
    
    private void startComponent(String name, String number) {
        
        debug("Entering startComponent");
        debug("Name = " + name);
        debug("Number = " + number);
        
        try {
            if (name != null && name.equals(SCHEMA_HOLDER)) {
                int index = Integer.parseInt(number);
                physioPane.flowSchema(image.get(index));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void endComponent() {
    }
    
    private void startIcon(org.jdom.Element current) {
        
        String name = current.getChildTextTrim("name");
        
        if (name != null) {
            debug(name);
        }
    }
    
    private void endIcon() {
    }
    
    private void debug(String msg) {
        logger.debug(msg);
        logger.debug(msg);
    }
}