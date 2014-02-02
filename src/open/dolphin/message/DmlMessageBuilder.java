/*
 * DmlMessageBuilder.java
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
package open.dolphin.message;

import java.lang.reflect.*;
import java.io.*;

import open.dolphin.infomodel.IInfoModel;

/**
 * DML Message builder.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class DmlMessageBuilder  {
    
    private static final int INDENT = 4;
    
    private int indent = INDENT;
    
    /** Creates a new instance of XMLBuilder */
    public DmlMessageBuilder() {
    }
    
    public int getIndent() {
    	return indent;
    }
    
    public void setIndent(int indent) {
    	this.indent = indent;
    }
    
    public String build(IInfoModel model) {
        
        StringWriter sw = null;
        
        try {
            sw = new StringWriter();
            BufferedWriter writer = new BufferedWriter(sw);
            writeObject(writer, model, 0, null);
            writer.flush();
            writer.close();
            
        } catch (Exception e) {
        	e.printStackTrace();
            System.out.println(e);
        }
        
        return sw.toString();
    }
    
	private void writeObject(BufferedWriter writer, IInfoModel model, int depth, String curProperty) {
        
		try {
            
			Class theClass = model.getClass();
			String className = classToElementName(theClass);
			Method[] methods = theClass.getMethods();

			boolean writeClass = isWriteClassName(className, curProperty);
            
			if (writeClass) {
				writer.write(indentStr(depth));
				writer.write(addStartTag(className));
				writer.write(crStr());
			} else {
				depth--;
			}
                        
			String property = null;
			Object value = null;

			for (int i = 0; i < methods.length; i++) {

				if ( isProperty(methods[i]) ) {

					value = methods[i].invoke(model, null);

					if (value != null) {
                        
						property = methodToPropertyName(methods[i].getName());
                        
						if (value instanceof String) {
							writer.write(indentStr(depth + 1));
							writer.write(addStartTag(property));
							writer.write((String)value);
							writer.write(addEndTag(property));
							writer.write(crStr());
                        
						} else if (value instanceof String[]) {

							String[] o = (String[])value;
							for (int k = 0; k < o.length; k++) {
								if (o[k] == null) {
									continue;
								}
								writer.write(indentStr(depth + 1));
								writer.write(addStartTag(property));
								writer.write((String)o[k]);
								writer.write(addEndTag(property));
								writer.write(crStr());
							}  
                        
						} else if (value instanceof IInfoModel) {
							writer.write(indentStr(depth + 1));
							writer.write(addStartTag(property));
							writer.write(crStr());// ‰üs‚µ‚Ä‚©‚ç
							depth += 2;
							writeObject(writer, (IInfoModel)value, depth, property);
							depth -= 2;
							writer.write(indentStr(depth + 1));
							writer.write(addEndTag(property));
							writer.write(crStr());
                        
						} else if (value instanceof IInfoModel[]) {
                            
							IInfoModel[] o = (IInfoModel[])value;
							for (int k = 0; k < o.length; k++) {
								if (o[k] == null) {
									continue;
								}
								writer.write(indentStr(depth + 1));
								writer.write(addStartTag(property));
								writer.write(crStr()); // ‰üs‚µ‚Ä‚©‚ç

								depth += 2;
								writeObject(writer, o[k], depth, property);
								depth -= 2;
								writer.write(indentStr(depth + 1));
								writer.write(addEndTag(property));
								writer.write(crStr());
							}
						}
					}
				}
			}

			if (writeClass) {
				writer.write(indentStr(depth));
				writer.write(addEndTag(className));
				writer.write(crStr());
			}
        
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}
    
    private boolean isWriteClassName(String className, String curProperty) {
        return ( curProperty != null && curProperty.equals(className) ) ? false : true;
    }
    
    private boolean isProperty(Method method) {
        
        String methodName = method.getName();
            
        if (methodName.startsWith("get") && (! methodName.equals("getClass")) ) {

            Class[] paramClass = method.getParameterTypes();

            if (paramClass.length == 0) {

                return true;
            }   
        }
        
        return false;
    }
    
    private String methodToPropertyName(String m) {
        StringBuffer buf = new StringBuffer();
        buf.append(m.substring(3,4).toLowerCase());
        buf.append(m.substring(4));
        return buf.toString();
    }
    
    private String classToElementName(Class c) {
        String className = c.getName();
        int index = className.lastIndexOf('.');
        className = index > 0 ? className.substring(index + 1) : className;
        return className.substring(0,1).toLowerCase() + className.substring(1);
    }
        
    private String addStartTag(String tag) {
        StringBuffer buf = new StringBuffer();
        buf.append("<");
        buf.append(tag);
        buf.append(">");
        return buf.toString();
    }
    
    private String addEndTag(String tag) {
        StringBuffer buf = new StringBuffer();
        buf.append("</");
        buf.append(tag);
        buf.append(">");
        return buf.toString();
    }  
    
    private String indentStr(int depth) {
        if (depth == 0) {
            return "";
        }
        int n = depth * indent;
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < n; i++) {
            buf.append(" ");
        }
        return buf.toString();
    }
    
    private String crStr() {
        return "\n";
    }
}