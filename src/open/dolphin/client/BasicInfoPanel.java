/*
 * BasicInfoPanel.java
 * Copyright (C) 2004 Digital Globe, Inc. All rights reserved.
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

import javax.swing.*;

import open.dolphin.infomodel.Allergy;
import open.dolphin.infomodel.BaseClinicModule;
import open.dolphin.infomodel.BloodType;
import open.dolphin.infomodel.Infection;
import open.dolphin.infomodel.LifestyleModule;
import open.dolphin.infomodel.Patient;


import java.awt.*;

/**
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class BasicInfoPanel extends JPanel {
    
    private int width = 190; //200;
    private int height = 50;
    private Font font = new Font("dialog", Font.PLAIN, 12);
    private Color background = ClientContext.getColor("stickPaper.color.background");
    private Color foreground = ClientContext.getColor("stickPaper.color.foreground");
    private String tooltipText = "ååâtå^ÅAÉAÉåÉãÉMìôÇÃäÓëbèÓïÒ";
    private Insets margin = new Insets(3,3,2,2);
    private String pid;
    private String name;
    private String sex;
    private String birthday;
    private String age;
    
    private LifestyleModule lifestyleModule;
    private BaseClinicModule baseClinicModule;
    
    private JTextArea infoArea;
    
    public BasicInfoPanel(Patient patient) {
        
        super(new BorderLayout());
        
        pid = patient.getId();
        name = patient.getName();
        sex = patient.getGender();
        birthday = patient.getBirthday();
        age = patient.getAge();
        
        infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setLineWrap(true);
        infoArea.setFont(font);
        infoArea.setMargin(margin);
        infoArea.setBackground(background);
        infoArea.setForeground(foreground);
        infoArea.setToolTipText(tooltipText);
        
        JScrollPane scroller = new JScrollPane(infoArea,
                                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.add(scroller, BorderLayout.CENTER);
        this.setPreferredSize(new Dimension(width, height));
    }
    
    public String getPid() {
        return pid;
    }
    
    public void setPid(String val) {
        pid = val;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String val) {
        name = val;
    }
    
    public String getSex() {
        return sex;
    }
    
    public void setSex(String val) {
        sex = val;
    }  
    
    public String getBirthday() {
        return birthday;
    }
    
    public void setBirthday(String val) {
        birthday = val;
    } 
    
    public String getAge() {
        return age;
    }
    
    public void setAge(String val) {
        age = val;
    }  
    
    public BaseClinicModule getBaseClinicModule() {
        return baseClinicModule;
    }
    
    public void setBaseClinicModule(BaseClinicModule val) {
        baseClinicModule = val;
    }
    
    public LifestyleModule getLifestyleModule() {
        return lifestyleModule;
    }
    
    public void setLifestyleModule(LifestyleModule val) {
        lifestyleModule = val;
    }    
    
    public void fetch() {
    }
    
    public void display() {
        
        infoArea.setText("");
        
        StringBuffer buf = new StringBuffer();
        buf.append(name);
        
        buf.append(" ");
        
        if (sex.equals("íjê´") || sex.equals("male")) {
            buf.append("M");
        } else if (sex.equals("èóê´") || sex.equals("female")) {
            buf.append("F");
        } else {
            //buf.append("U");
            buf.append(sex);
        }
        
        buf.append(" ");
        
        buf.append(age);
        buf.append(" (");
        buf.append(birthday);
        buf.append(") ");
        buf.append("\n");
        
        if (baseClinicModule != null) {
            
            BloodType blood = baseClinicModule.getBloodType();
            if (blood != null) {
                //buf.append("ååâtå^: ");
                String val = blood.getAbo();
                if (val != null) {
                    buf.append(val);
                    //buf.append(" ");
                }
                val = blood.getRhod();
                if (val != null) {
                    buf.append(" (");
                    buf.append(val);
                    buf.append(")");
                }
                val = blood.getMemo();
                if (val != null) {
                    buf.append(" ");
                    buf.append(val);
                    //buf.append(")");
                }
                buf.append("\n");
            }
            
            Allergy[] allergy = baseClinicModule.getAllergy();
            if (allergy != null) {
                                
                for (int i = 0; i < allergy.length; i++) {
                    
                    String val = allergy[i].getFactor();
                    //buf.append("ÅE");
                    if (i != 0) {
                        buf.append(" / ");
                    }
                    buf.append(val);
                    
                    val = allergy[i].getSeverity();
                    if (val != null) {
                        buf.append("(");
                        buf.append(val);
                        buf.append(")");
                    }
                    
                    /*val = items[i].getIdentifiedDate();
                    if (val != null) {
                        buf.append(" ");
                        buf.append(val);
                        buf.append(" ìØíË");
                    }
                    
                    val = items[i].getMemo();
                    if (val != null) {
                        buf.append(" ");
                        buf.append(val);
                    } */
                    
                    //buf.append("\n");
                }
                buf.append("\n");
            }
            
            Infection[] infection = baseClinicModule.getInfection();
            if (infection != null) {
                
                //buf.append("ä¥êıè«: \n");
                
                for (int i = 0; i < infection.length; i++) {
                    
                    String val = infection[i].getFactor();
                    //buf.append(" ÅE");
                    if (i != 0) {
                        buf.append(" / ");
                    }
                    buf.append(val);
                    
                    val = infection[i].getExamValue();
                    if (val != null) {
                        buf.append("(");
                        buf.append(val);
                        buf.append(")");
                    }
                    
                    /*val = items[i].getIdentifiedDate();
                    if (val != null) {
                        buf.append(" ");
                        buf.append(val);
                        buf.append(" ìØíË");
                    }
                    
                    val = items[i].getMemo();
                    if (val != null) {
                        buf.append(" ");
                        buf.append(val);
                    } 
                    
                    buf.append("\n");*/
                }
                buf.append("\n");
            }              
        }
        
        if (lifestyleModule != null) {
            String val = lifestyleModule.getTobacco();
            if (val != null) {
                buf.append("ÇΩÇŒÇ±(");
                buf.append(val);
                buf.append(")");
            }
            val = lifestyleModule.getAlcohol();
            if (val != null) {
                buf.append(" à˘é(");
                buf.append(val);
                buf.append(")");
            }
            val = lifestyleModule.getOccupation();
            if (val != null) {
                buf.append(" êEã∆(");
                buf.append(val);
                buf.append(")");
            }
            val = lifestyleModule.getOther();
            if (val != null) {
                buf.append(" ÇªÇÃëº(");
                buf.append(val);
                buf.append(")");
            }
            buf.append("\n");
        }
        
        infoArea.setText(buf.toString());
    }
}