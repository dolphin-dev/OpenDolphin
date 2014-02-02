/*
 * GUIDirector.java
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
package open.dolphin.util;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import org.jdom.*;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class GUIDirector {
    
    static final int CMP_PANEL              = 0;
    static final int CMP_TABPANEL           = 1;
    static final int CMP_SCROLLER_PANEL     = 2;
    static final int CMP_TABBED_PANE        = 3;
    static final int CMP_SCROLLER           = 4;
    static final int CMP_BUTTONS            = 5;
    static final int CMP_CHECK_BOXES        = 6;
    static final int CMP_RADIO_BUTTONS      = 7;
    static final int CMP_LABEL              = 8;
    static final int CMP_TEXT_FIELD         = 9;
    static final int CMP_TEXT_AREA          = 10;
    static final int CMP_BUTTON             = 11;
    static final int CMP_CHECK_BOX          = 12;
    static final int CMP_RADIO_BUTTON       = 13;
    static final int CMP_COMBO_BOX          = 14;
    static final int CMP_PROGRESS_BAR       = 15;
    static final int CMP_H_GAP              = 16;
    static final int CMP_V_GAP              = 17;
    static final int CMP_H_GLUE             = 18;
    static final int CMP_V_GLUE             = 19;
    static final int CMP_OBJECT_TABLE       = 20;
    static final int CMP_CLASS              = 21;
    
    GUIBuilder builder;
    HashMap hashMap;
    
    /** Creates a new instance of JDOMReader */
    public GUIDirector() {
    }
   
    public GUIDirector(GUIBuilder builder, HashMap hashMap) {
        this();
        this.builder = builder;
        this.hashMap = hashMap;
    }
        
    public JPanel getTopPanel() {
        return builder.getTopPanel();
    }
    
    public void build(URL url) {
        
        builder.buildStart(hashMap);

        SAXBuilder docBuilder = new SAXBuilder();

        try {
            Document doc = docBuilder.build(url);
            Element root = doc.getRootElement();

            parseChildren(root);      
        }
        // indicates a well-formedness error
        catch (JDOMException e) { 
            System.out.println(url + " is not well-formed.");
            System.out.println(e.getMessage());
        }  
        catch (IOException e) { 
            System.out.println(e);
        }  
    }
    
    public void parseChildren(Element current) {
   
        int cmpType = startElement(current.getName(), current);
        
        List children = current.getChildren();
        Iterator iterator = children.iterator();
        
        while (iterator.hasNext()) {
            Element child = (Element) iterator.next();
            parseChildren(child);
        }
        
        builder.buildEnd(cmpType);
    }
    
    public int startElement(String eName, Element e)  {
                
        //----------------------------------------------------------------------
            
        if (eName.equals("panel")) {
            
            builder.buildPanel(e.getAttributeValue("layout"), 
                               e.getAttributeValue("titleBorder"),
                               e.getAttributeValue("insets"),
                               e);
            
            return CMP_PANEL;
        }
        
        
        if (eName.equals("tabPanel")) {
            
            builder.buildTabPanel(e.getAttributeValue("tabTitle"), 
                                  e.getAttributeValue("layout"), 
                                  e.getAttributeValue("titleBorder"),
                                  e.getAttributeValue("insets"));
            return CMP_TABPANEL;
        }
        
        
        if (eName.equals("scrollerPanel")) {
            
            builder.buildScrollerPanel(e.getAttributeValue("layout"));
            return CMP_SCROLLER_PANEL;
        }
        
        
        if (eName.equals("tabbedPane")) {
            
            builder.buildTabbedPane(e.getAttributeValue("key"), 
                                    e.getAttributeValue("tabPlacement"), 
                                    e.getAttributeValue("tabLayoutPolicy"));
            return CMP_TABBED_PANE;
        }
        
        
        if (eName.equals("scroller")) {
            
            builder.buildScroller(e.getAttributeValue("key"), 
                                  e.getAttributeValue("vsbPolicy"), 
                                  e.getAttributeValue("hsbPolicy"));
            return CMP_SCROLLER;
        }
        
        
        if (eName.equals("buttons")) {
            
            builder.buildButtons(e.getAttributeValue("layout"), 
                                 e.getAttributeValue("titleBorder"),
                                 e.getAttributeValue("insets"));
            return CMP_BUTTONS;
        }
        
        
        if (eName.equals("checkBoxes")) {
            
            builder.buildCheckBoxes(e.getAttributeValue("layout"), 
                                    e.getAttributeValue("titleBorder"),
                                    e.getAttributeValue("insets"));
            return CMP_CHECK_BOXES;
        }        
        
        
        if (eName.equals("radioButtons")) {
            
            builder.buildRadioButtons(e.getAttributeValue("layout"), 
                                      e.getAttributeValue("titleBorder"),
                                      e.getAttributeValue("insets"));
            return CMP_RADIO_BUTTONS;
        }
        
        //----------------------------------------------------------------------
        
        if (eName.equals("label")) {
            
            builder.buildLabel(e.getAttributeValue("key"), 
                               e.getAttributeValue("text"), 
                               e.getAttributeValue("icon"),
                               e.getAttributeValue("align"), 
                               e.getAttributeValue("hTextPos"), 
                               e.getAttributeValue("vTextPos"),
                               e.getAttributeValue("toolTip"));
            return CMP_LABEL;
        }
        
          
        if (eName.equals("textField")) {
            
            builder.buildTextField(e.getAttributeValue("key"), 
                                   e.getAttributeValue("label"), 
                                   e.getAttributeValue("value"),
                                   e.getAttributeValue("maxSize"));
            return CMP_TEXT_FIELD;
        }
        
        
        if (eName.equals("textArea")) {
            
            builder.buildTextArea(e.getAttributeValue("key"), 
                                   e.getAttributeValue("label"), 
                                   e.getAttributeValue("value"),
                                   e.getAttributeValue("insets"));
            return CMP_TEXT_AREA;
        }
        
   
        if (eName.equals("button")) {
            
            builder.buildButton(e.getAttributeValue("key"), 
                                e.getAttributeValue("text"), 
                                e.getAttributeValue("icon"),
                                e.getAttributeValue("mnemonic"), 
                                e.getAttributeValue("enabled"),
                                e.getAttributeValue("hTextPos"), 
                                e.getAttributeValue("vTextPos"),
                                e.getAttributeValue("toolTip"));
            return CMP_BUTTON;
        }
        
   
        if (eName.equals("checkBox")) {
            
            builder.buildCheckBox(e.getAttributeValue("key"), 
                                  e.getAttributeValue("text"));
            return CMP_CHECK_BOX;
        }
            
        

        if (eName.equals("radioButton")) {
            
            builder.buildRadioButton(e.getAttributeValue("key"), 
                                     e.getAttributeValue("text"));
            return CMP_RADIO_BUTTON;
        }
        
        
        if (eName.equals("comboBox")) {
            
            builder.buildComboBox(e.getAttributeValue("key"), 
                                  e.getAttributeValue("label"), 
                                  e.getAttributeValue("items"));
            return CMP_COMBO_BOX;
        }
        
        
        if (eName.equals("progressBar")) {
            
            builder.buildProgressBar(e.getAttributeValue("key"), 
                                     e.getAttributeValue("borderPainted"), 
                                     e.getAttributeValue("min"), 
                                     e.getAttributeValue("max"));
            return CMP_PROGRESS_BAR;
        }
        
            
        if (eName.equals("hGap")) {
            builder.buildHGap(e.getAttributeValue("width"));
            return CMP_H_GAP;
        }
        
        
        if (eName.equals("vGap")) {
            builder.buildVGap(e.getAttributeValue("height"));
            return CMP_V_GAP;
        }
        
        
        if (eName.equals("hGlue")) {
            builder.buildHGlue();
            return CMP_H_GLUE;
        }
        
        
        if (eName.equals("vGlue")) {
            builder.buildVGlue();
            return CMP_V_GLUE;
        }
        
        
        //================================
        
        if (eName.equals("objectTable")) {
            
            builder.buildObjectTable(e.getAttributeValue("key"), 
                                     e.getAttributeValue("columnNames"), 
                                     e.getAttributeValue("startNumRows"));
            return CMP_OBJECT_TABLE;
        }
        
        
        if (eName.equals("class")) {
            
            builder.buildObject(e.getAttributeValue("key"), 
                                e.getAttributeValue("name"));
            return CMP_CLASS;
        }
        
        return -1;
    }    
}
