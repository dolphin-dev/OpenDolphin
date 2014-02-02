/*
 * MenuBarDirector.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
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
package open.dolphin.plugin.helper;

import java.io.*;
import java.net.*;
import java.util.*;


import org.jdom.*;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;


/**
 * Director to build MenuBar from XML source.
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class MenuBarDirector {
    
    static final int CMP_MENU_BAR           = 0;
    static final int CMP_MENU               = 1;
    static final int CMP_SUB_MENU           = 2;
    static final int CMP_MENU_ITEM          = 3;
    static final int CMP_ACTION_ITEM        = 4;
    static final int CMP_STYLE_ITEM         = 5;
    
    MenuBarBuilder builder;
    
    
    /**
     * Creates new MenuBarDirector
     */
    public MenuBarDirector(MenuBarBuilder builder) {
        this.builder = builder;
    }
    
    public void build(URL url) {
        
        SAXBuilder docBuilder = new SAXBuilder();
        
        try {
            Document doc = docBuilder.build(url);
            Element root = doc.getRootElement();
            
            parseChildren(root);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }
    
    public void parseChildren(Element current) {
        
        int cmpType = startElement(current.getName(), current);
        
        if (cmpType == CMP_MENU_ITEM || cmpType == CMP_ACTION_ITEM || cmpType == CMP_STYLE_ITEM) {
            return;
        }
        
        List children = current.getChildren();
        Iterator iterator = children.iterator();
        
        while (iterator.hasNext()) {
            Element child = (Element) iterator.next();
            parseChildren(child);
        }
        
        builder.buildEnd(cmpType);
    }
    
    public int startElement(String eName, Element e) {
        
        if (eName.equals("menuItem")) {
            
            builder.buildMenuItem(e.getAttributeValue("key"),
                    e.getChildTextTrim("text"),
                    e.getChildTextTrim("accelerator"),
                    e.getChildTextTrim("shiftMask"),
                    e.getChildTextTrim("enabled"));
            
            return CMP_MENU_ITEM;
            
            
        } else if (eName.equals("actionItem")) {
            
            builder.buildActionItem(e.getAttributeValue("name"),
                    e.getAttributeValue("type"),
                    e.getAttributeValue("group"),
                    e.getChildTextTrim("text"),
                    e.getChildTextTrim("icon"),
                    e.getChildTextTrim("accelerator"),
                    e.getChildTextTrim("shiftMask"),
                    e.getChildTextTrim("toolTip"),
                    e.getChildTextTrim("enabled"));
            
            return CMP_ACTION_ITEM;
            
        } else if (eName.equals("styleItem")) {
            
            builder.buildStyleItem(e.getAttributeValue("name"),
                    e.getAttributeValue("type"),
                    e.getAttributeValue("group"),
                    e.getChildTextTrim("text"),
                    e.getChildTextTrim("icon"),
                    e.getChildTextTrim("accelerator"),
                    e.getChildTextTrim("shiftMask"),
                    e.getChildTextTrim("toolTip"),
                    e.getChildTextTrim("enabled"));
            
            return CMP_STYLE_ITEM;
            
            
        } else if (eName.equals("menu")) {
            builder.buildMenu(e.getAttributeValue("name"),
                    e.getAttributeValue("text"),
                    e.getAttributeValue("mnemonic"));
            return CMP_MENU;
            
        } else if (eName.equals("subMenu")) {
            builder.buildSubMenu(e.getAttributeValue("name"),
                    e.getAttributeValue("text"),
                    e.getChildTextTrim("enabled"));
            return CMP_SUB_MENU;
            
        } else if (eName.equals("menubar")) {
            builder.buildMenuBar();
            return CMP_MENU_BAR;
        }
        
        return -1;
    }
}