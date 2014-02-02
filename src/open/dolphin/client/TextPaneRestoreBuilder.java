/*
 * TextPaneRestoreBuilder.java
 * Copyright (C) 2003 Digital Globe, Inc. All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *	
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *	
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package open.dolphin.client;

import java.awt.*;
//import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import org.jdom.*;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author  kazm
 */
public class TextPaneRestoreBuilder {
    
    static final int TT_PARAGRAPH           = 0;
    static final int TT_CONTENT             = 1;
    static final int TT_ICON                = 2;
    static final int TT_COMPONENT           = 3;
    static final int TT_PROGRESS_COURSE     = 4;
    
    static final String PARAGRAPH_NAME  = "paragraph";
    static final String CONTENT_NAME    = "content";
    static final String COMPONENT_NAME  = "component";
    static final String ICON_NAME       = "icon";
    static final String FOREGROUND_NAME = "foreground";
    static final String SIZE_NAME       = "size";
    static final String BOLD_NAME       = "bold";
    static final String ITALIC_NAME     = "italic";
    static final String UNDERLINE_NAME  = "underline";
    static final String TEXT_NAME       = "text";
    static final String LOGICAL_STYLE_NAME = "logicalStyle";
    static final String PROGRESS_COURSE_NAME = "progressCourseModule";
    
    JTextPaneDump textPane;
    boolean logicalStyle;
    
    /** Creates a new instance of TextPaneRestoreBuilder */
    public TextPaneRestoreBuilder() {
    }
    
    void build(JTextPaneDump textPane, String xml) { 
        
        this.textPane = textPane;

        SAXBuilder docBuilder = new SAXBuilder();

        try {
            StringReader sr = new StringReader(xml);
            Document doc = docBuilder.build(new BufferedReader(sr));
            org.jdom.Element root = (org.jdom.Element)doc.getRootElement();

            writeChildren(root);      
        }
        // indicates a well-formedness error
        catch (JDOMException e) { 
            //System.out.println(url + " is not well-formed.");
            System.out.println(e.getMessage());
        }  
        catch (IOException e) { 
            System.out.println(e);
        }  
    }
    
    void writeChildren(org.jdom.Element current) {
   
        int eType = -1;
        String eName = current.getName();
        //System.out.println(eName);
        
        if (eName.equals(PARAGRAPH_NAME)) {
            eType = TT_PARAGRAPH;
            startParagraph(current);
        
        } else if (eName.equals(CONTENT_NAME)) {
            eType = TT_CONTENT;
            startContent(current);
            
        } else if (eName.equals(COMPONENT_NAME)) {
            eType = TT_COMPONENT;
            startComponent(current);
        
        } else if (eName.equals(ICON_NAME)) {
            eType = TT_ICON;
            startIcon(current);
        
        } else if (eName.equals(PROGRESS_COURSE_NAME)) {
            eType = TT_PROGRESS_COURSE;
            startProgressCourse();
        
        } else {
            System.out.println("Other element:" + eName);
        }
        
        // 子を探索するのはパラグフとトップ要素のみ
        if (eType == TT_PARAGRAPH || eType == TT_PROGRESS_COURSE) {
        
            java.util.List children = (java.util.List)current.getChildren();
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
        }
    }
    
    void startProgressCourse() {
        // 
    }
    
    void endProgressCourse() {
        // 
    }    
    
    void startParagraph(org.jdom.Element current) {
        
        String logical = current.getAttributeValue(LOGICAL_STYLE_NAME);
        
        if (logical != null) {
            textPane.setLogicalStyle(logical);
            logicalStyle = true;
        }
    }
    
    void endParagraph() {
        textPane.makeParagraph();
        if (logicalStyle) {
            textPane.clearLogicalStyle();
            logicalStyle = false;
        }
    }
    
    void startContent(org.jdom.Element current) {
        
        // text 要素が null の場合はリターン
        if (current.getChild(TEXT_NAME) == null) {
            return;
        }
        
        // text データの長さがゼロの場合はリターン
        String text = current.getChildTextTrim(TEXT_NAME);
        if (text.length() == 0) {
            return;
        }
        
        // 特殊文字を戻す
        text.replaceAll("&lt;", "<");
        text.replaceAll("&gt;", ">");
        text.replaceAll("&amp;","&");
        
        // このコンテントに設定する AttributeSet
        MutableAttributeSet atts = new SimpleAttributeSet();
        
        // Foreground 属性を設定する
        if (current.getChild(FOREGROUND_NAME) != null) {
            
            String rgb = current.getChildTextTrim(FOREGROUND_NAME);
            StringTokenizer stk = new StringTokenizer(rgb,",");
            if (stk.hasMoreTokens()) {
                int r = Integer.parseInt(stk.nextToken());
                int g = Integer.parseInt(stk.nextToken());
                int b = Integer.parseInt(stk.nextToken());
                StyleConstants.setForeground(atts, new Color(r,g,b));
            }
        }
        
        // Size 属性を設定する
        if (current.getChild(SIZE_NAME) != null) {
            String size = current.getChildTextTrim(SIZE_NAME);
            StyleConstants.setFontSize(atts, Integer.parseInt(size));
        }
        
        // Bold 属性を設定する
        if (current.getChild(BOLD_NAME) != null) {
            String bold = current.getChildTextTrim(BOLD_NAME);
            StyleConstants.setBold(atts, Boolean.valueOf(bold).booleanValue());
        }
        
        // Italic 属性を設定する
        if (current.getChild(ITALIC_NAME) != null) {
            String italic = current.getChildTextTrim(ITALIC_NAME);
            StyleConstants.setItalic(atts, Boolean.valueOf(italic).booleanValue());
        }
        
        // Underline 属性を設定する
        if (current.getChild(UNDERLINE_NAME) != null) {
            String underline = current.getChildTextTrim(UNDERLINE_NAME);
            StyleConstants.setUnderline(atts, Boolean.valueOf(underline).booleanValue());
        }
        
        // テキストを挿入する
        textPane.insertString(text, atts);
    }
    
    void endContent() {
    }
    
    void startComponent(org.jdom.Element current) {
        
        String name = current.getChildTextTrim("name");
        if (name != null && name.equals("stampHolder")) {
            JLabel l = new JLabel("Stamp");
            textPane.insertStamp(l);
        }
    }
    
    void endComponent() {
    }
    
    void startIcon(org.jdom.Element current) {
       
        String name = current.getChildTextTrim("name");
        if (name != null) {
            textPane.insertIcon(name);
        }      
    }
    
    void endIcon() {
    }    
}