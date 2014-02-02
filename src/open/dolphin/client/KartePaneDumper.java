/*
 * KartePaneDumper.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
 * Copyright (C) 2003,2004 Digital Globe, Inc. All rights reserved.
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

import javax.swing.text.*;

import open.dolphin.infomodel.Module;
import open.dolphin.infomodel.Schema;


/**
 * KartePane の dumper
 * 
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class KartePaneDumper {
    
    // Control flags to dump.
    static final String XML_HEADER  = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
    static final String START_ROOT_TAG  = "<kartePane>";
	static final String END_ROOT_TAG    = "</kartePane>";
    static final int TT_PARAGRAPH   = 0;
    static final int TT_CONTENT     = 1;
    static final int TT_ICON        = 2;
    static final int TT_COMPONENT   = 3;
    
	private int freeTopPos;
    private ArrayList moduleList;
    private ArrayList schemaList;
    private String spec;
    
    private boolean startFree;
    private StringBuffer freeBuffer;

    
    /** Creates a new instance of TextPaneDumpBuilder */
    public KartePaneDumper() {
    }
    
	public void setTopFreePos(int val) {
		freeTopPos = val;
	}
	
	public String getSpec() {
		return spec;
	}
    
	public Module[] getModule() {
        
		Module[] ret = null;
		if ( (moduleList != null) && (moduleList.size() > 0)) {
			Object[] o = moduleList.toArray();
			ret = new Module[o.length];
			for (int i = 0; i < o.length; i++) {
				ret[i] = (Module)o[i];
			}
		}  
		return ret;
	}
    
	public Schema[] getSchema() {
        
		Schema[] schemas = null;
		
		if ( (schemaList != null) && (schemaList.size() > 0)) {
			int len = schemaList.size();
			schemas = new Schema[len];
			
			for (int i = 0; i < len; i++) {
				schemas[i] = (Schema)schemaList.get(i);
			}
		} 
		return schemas;
	}
    
    public void dump(DefaultStyledDocument doc) {
        
        StringWriter sw = new StringWriter();
        BufferedWriter w = new BufferedWriter(sw);
        
        try {
            //w.write(XML_HEADER);
            w.write(START_ROOT_TAG);
            javax.swing.text.Element root = (javax.swing.text.Element)doc.getDefaultRootElement();
            writeElemnt(root, w);
            w.write(END_ROOT_TAG);
            
            w.flush();
            w.close();
            
			spec = sw.toString();
            
        } catch (Exception e) {
            System.out.println("Exception while dumping the text pane: " + e.toString());
            e.printStackTrace();
        }
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
    	       
    	if ( (start >= freeTopPos) && (! startFree) ) {
    		startFree = true;
    		freeBuffer = new StringBuffer();
    		System.out.println("start free text area");      
    	}
    	
    	if (startFree) {
    	
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
	        
	        w.write(">");
    	}
    }
    
    void endParagraph(Writer w) throws IOException {
    	
		if (startFree) {
			indent(w, 1);
			w.write("</paragraph>");			
		}
    }
    
    void startContent(Writer w, int start, int end, javax.swing.text.Element e, AttributeSet a) 
    throws IOException, BadLocationException {
        
		if (!startFree) {
			return;
		}
        
        indent(w, 2);
        w.write("<content");
        w.write(" start=");
        w.write(addQuote(start));
        w.write(" end=");
        w.write(addQuote(end));
        w.write(">");
        
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
            w.write(">");
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
        
        w.write("</text>");
    }
    
    void endContent(Writer w) throws IOException {
		if (startFree) {
			indent(w, 2);
			w.write("</content>");
		}
    }
    
    void startComponent(Writer w, int start, int end, javax.swing.text.Element e, AttributeSet a) throws IOException {
		
		if (startFree) {
			
			indent(w, 2);
			w.write("<component");
			w.write(" start=");
			w.write(addQuote(start));
			w.write(" end=");
			w.write(addQuote(end));
			w.write(">");
		}
		
        Enumeration enum = a.getAttributeNames();
        
        while (enum.hasMoreElements()) {
            
            Object o = enum.nextElement();
            String ename = o.toString();
            String value = null;
            
            // $ename 
            if (ename.startsWith("$")) {
                continue;
            } else if (ename.equals("component")) {
            	IComponentHolder ih = (IComponentHolder)a.getAttribute(o);
            	int cType = ih.getContentType();
            	
            	switch (cType) {
            		
            		case IComponentHolder.TT_STAMP:
            			if (moduleList == null) {
							moduleList = new ArrayList();
            			}
            			StampHolder sh = (StampHolder)ih;
						moduleList.add((Module)sh.getStamp());
            			value = String.valueOf(moduleList.size() - 1);
            			break;
            			
            		case IComponentHolder.TT_IMAGE:
						if (schemaList == null) {
							schemaList = new ArrayList();
						}
						SchemaHolder ch = (SchemaHolder)ih;
						schemaList.add(ch.getSchema());
						value = String.valueOf(schemaList.size() - 1);            		
            		    break;	
            	}
            	
            	
            } else {
                value = a.getAttribute(o).toString();
            }
            
            if (startFree) {
				indent(w, 3);
				w.write("<");
				w.write(ename);
				w.write(">");
				w.write(value);
				//indent(w, 2);
				w.write("</");
				w.write(ename);
				w.write(">");            	
            }
        }
    }
    
    void endComponent(Writer w) throws IOException {
		if (startFree) {
			indent(w,2);
			w.write("</component>");
		}
    } 
    
    void startIcon(Writer w, int start, int end, javax.swing.text.Element e, AttributeSet a) throws IOException {
		
		if (startFree) {
	        indent(w, 2);
	        w.write("<icon");
	        w.write(" start=");
	        w.write(addQuote(start));
	        w.write(" end=");
	        w.write(addQuote(end));
	        w.write(">");
		}
        
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
            
            if (startFree) {
	            indent(w, 3);
	            w.write("<");
	            w.write(ename);
	            w.write(">");
	            w.write(value);
	            //indent(w, 2);
	            w.write("</");
	            w.write(ename);
	            w.write(">");
            }
        }        
    }
    
    void endIcon(Writer w) throws IOException {
		if (startFree) {
			indent(w,2);
			w.write("</icon>");
		}
    }
    
    /*void indent(Writer w, int depth) throws IOException {
        for (int i = 0; i < depth; i++) {
            w.write("    ");
        }
    }*/
    
	void indent(Writer w, int depth) throws IOException {
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