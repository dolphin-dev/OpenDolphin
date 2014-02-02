/*
 * TextPaneDumpBuilder.java
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
import java.io.*;
import java.util.*;

import javax.swing.JLabel;
import javax.swing.text.*;

/**
 *
 * @author  kazm
 */
public class TextPaneDumpBuilder {
    
    // Control flags to dump.
    static final int TT_PARAGRAPH   = 0;
    static final int TT_CONTENT     = 1;
    static final int TT_ICON        = 2;
    static final int TT_COMPONENT   = 3;
    
    /** Creates a new instance of TextPaneDumpBuilder */
    public TextPaneDumpBuilder() {
    }
    
    public String build(DefaultStyledDocument doc) {
        
        StringWriter sw = new StringWriter();
        BufferedWriter w = new BufferedWriter(sw);
        
        try {
            w.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
            w.write("<progressCourseModule>\n");
            javax.swing.text.Element root = (javax.swing.text.Element)doc.getDefaultRootElement();
            writeElemnt(root, w);
            w.write("</progressCourseModule>\n");
            
            w.flush();
            w.close();
            
        } catch (Exception e) {
            System.out.println(e);
        }
        
        return sw.toString();
    }
    
    void writeElemnt(javax.swing.text.Element e, Writer w) throws IOException, BadLocationException {
        
        String name = e.getName();
        int start = e.getStartOffset();
        int end = e.getEndOffset();
        AttributeSet a = e.getAttributes();
        int elementType = -1;
        
        if (name.equals(AbstractDocument.ParagraphElementName)) {
            startParagraph(w, start, end, a);
            elementType = TT_PARAGRAPH;
            
        } else if (name.equals(AbstractDocument.ContentElementName)) {
            startContent(w, start, end, e, a);
            elementType = TT_CONTENT;
            
        } else if (name.equals("icon")) {
            elementType = TT_ICON;
            startIcon(w, start, end, e, a);
            
        } else if (name.equals("component")) {
            elementType = TT_COMPONENT;
            startComponent(w, start, end, e, a);
        }
                
        int children = e.getElementCount();
        for (int i = 0; i < children; i++) {
            writeElemnt(e.getElement(i), w);
        }
        
        // このメソッドの出口で endXXX をコールする
        switch (elementType) {
         
            case TT_PARAGRAPH:
                endParagraph(w);
                break;
                
            case TT_CONTENT:
                endContent(w);
                break;
                
            case TT_ICON:
                endIcon(w);
                break;
                
            case TT_COMPONENT:
                endComponent(w);
                break;
        }
    }
    
    void startParagraph(Writer w, int start, int end, AttributeSet a) throws IOException {
        
        // 論理スタイル
        String name = (String)a.getAttribute(StyleConstants.NameAttribute);
        
        indent(w, 1);
        w.write("<paragraph");
        w.write(" start=");
        w.write(addQuote(start));
        w.write(" end=");
        w.write(addQuote(end));
        
        if (name != null) {
            w.write(" logicalStyle=");
            w.write(addQuote(name));
        }
        
        w.write(">\n");
        
            
        /*Enumeration enum = a.getAttributeNames();
        
        while (enum.hasMoreElements()) {
            Object o = enum.nextElement();
            String ename = o.toString();
            String value = null;
            
            if (ename.equals("resolver")) {
                Style s = (Style)a.getAttribute(o);
                Color c = (Color)s.getAttribute(StyleConstants.Foreground);
                value = c.toString();
            } else {
                value = a.getAttribute(ename).toString();
            }
            
            indent(w, 2);
            w.write("<");
            w.write(ename);
            w.write(">");
            w.write(value);
            w.write("</");
            w.write(ename);
            w.write(">\n");
        }*/
    }
    
    void endParagraph(Writer w) throws IOException {
        indent(w, 1);
        w.write("</paragraph>\n");
    }
    
    void startContent(Writer w, int start, int end, javax.swing.text.Element e, AttributeSet a) 
    throws IOException, BadLocationException {
        
        indent(w, 2);
        w.write("<content");
        w.write(" start=");
        w.write(addQuote(start));
        w.write(" end=");
        w.write(addQuote(end));
        w.write(">\n");
        
        Enumeration enum = a.getAttributeNames();
        
        while (enum.hasMoreElements()) {
            
            Object o = enum.nextElement();
            String ename = o.toString();
            Object value = a.getAttribute(o);
            
            indent(w, 3);
            w.write("<");
            w.write(ename);
            w.write(">");
            
            if (ename.equals("foreground")) {
                Color c = (Color)a.getAttribute(StyleConstants.Foreground);
                w.write(String.valueOf(c.getRed()));
                w.write(",");
                w.write(String.valueOf(c.getGreen()));
                w.write(",");
                w.write(String.valueOf(c.getBlue()));
            
            } else if (ename.equals("size")) {
                w.write(value.toString());
                
            } else if (ename.equals("bold")) {
                w.write(value.toString());
            
            } else if (ename.equals("italic")) {
                w.write(value.toString());
            
            } else if (ename.equals("underline")) {
                w.write(value.toString());
            }
            
            //indent(w, 2);
            w.write("</");
            w.write(ename);
            w.write(">\n");
        }
        
        indent(w, 3);
        w.write("<text>");
        int len = end - start;
        String text = e.getDocument().getText(start, len).trim();
        
        text.replaceAll("<", "&lt;");
        text.replaceAll(">", "&gt;");
        text.replaceAll("&", "&amp;");
        
        w.write(text);
        //indent(w, 2);
        
        w.write("</text>\n");
    }
    
    void endContent(Writer w) throws IOException {
        indent(w, 2);
        w.write("</content>\n");
    }
    
    void startComponent(Writer w, int start, int end, javax.swing.text.Element e, AttributeSet a) throws IOException {
        
        indent(w, 2);
        w.write("<component");
        w.write(" start=");
        w.write(addQuote(start));
        w.write(" end=");
        w.write(addQuote(end));
        w.write(">\n");
        
        Enumeration enum = a.getAttributeNames();
        
        while (enum.hasMoreElements()) {
            
            Object o = enum.nextElement();
            String ename = o.toString();
            String value = null;
            
            // $ename 
            if (ename.startsWith("$")) {
                continue;
            } else if (ename.equals("component")) {
            	JLabel l = (JLabel)a.getAttribute(o);
            	value = l.getText();
            	// 上記によって Stamp と schema の区別が可能
            	// それぞれ別の ArrayList へ追加できる。
            	// またここにリストアするための ID 等の情報を入れることができる
                //value = a.getAttribute(o).getClass().getName();
            } else {
                value = a.getAttribute(o).toString();
            }
            
            indent(w, 3);
            w.write("<");
            w.write(ename);
            w.write(">");
            w.write(value);
            //indent(w, 2);
            w.write("</");
            w.write(ename);
            w.write(">\n");
        }
    }
    
    void endComponent(Writer w) throws IOException {
        indent(w,2);
        w.write("</component>\n");
    } 
    
    void startIcon(Writer w, int start, int end, javax.swing.text.Element e, AttributeSet a) throws IOException {
        
        indent(w, 2);
        w.write("<icon");
        w.write(" start=");
        w.write(addQuote(start));
        w.write(" end=");
        w.write(addQuote(end));
        w.write(">\n");
        
        Enumeration enum = a.getAttributeNames();
        
        while (enum.hasMoreElements()) {
            
            Object o = enum.nextElement();
            String ename = o.toString();
            String value = null;
            
            // $ename 
            if (ename.startsWith("$")) {
                continue;
            } else if (ename.equals("icon")) {
                value = a.getAttribute(o).getClass().getName();
            } else {
                value = a.getAttribute(o).toString();
            }
            
            indent(w, 3);
            w.write("<");
            w.write(ename);
            w.write(">");
            w.write(value);
            //indent(w, 2);
            w.write("</");
            w.write(ename);
            w.write(">\n");
        }        
    }
    
    void endIcon(Writer w) throws IOException {
        indent(w,2);
        w.write("</icon>\n");
    }
    
    void indent(Writer w, int depth) throws IOException {
        for (int i = 0; i < depth; i++) {
            w.write("    ");
        }
    }
    
    String addQuote(String str) {
        StringBuffer buf = new StringBuffer();
        buf.append("\"");
        buf.append(str);
        buf.append("\"");
        return buf.toString();
    }
    
    String addQuote(int str) {
        StringBuffer buf = new StringBuffer();
        buf.append("\"");
        buf.append(str);
        buf.append("\"");
        return buf.toString();
    }   
}