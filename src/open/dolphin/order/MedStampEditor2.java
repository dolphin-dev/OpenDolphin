/*
 * MedStampEditor2.java
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
package open.dolphin.order;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import open.dolphin.client.*;

/**
 * 処方スタンプエディタ。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class MedStampEditor2 extends StampModelEditor  {
    
    private static final String ADMIN_TITLE_BORDER = "用 法";
    private static final String MEDICINE_TABLETITLE_BORDER    = "処方セット";
    
    private AdminPanel adminPanel;
    private MedicineTablePanel medicineTable;
    private MasterTabPanel masterPanel;
        
    /** Creates new MedStampEditor2 */
    public MedStampEditor2() {

        //this.title = "Hippocrates: 処方エディタ";
        
        // Admin table
        adminPanel = new AdminPanel();
        
        // Medicine table
        medicineTable = new MedicineTablePanel();
        Border b = BorderFactory.createEtchedBorder();
        medicineTable.setBorder(BorderFactory.createTitledBorder(b, MEDICINE_TABLETITLE_BORDER));
        
        //MasterTabPanel masterPanel = MasterTabPanel.getInstance();
        masterPanel = new MasterTabPanel();
        masterPanel.startMedicine(medicineTable);
        
        // Connects
        adminPanel.addPropertyChangeListener(AdminPanel.ADMIN_PROP, medicineTable);
        medicineTable.setParent(this);
        
        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));
        top.add(adminPanel);
        top.add(medicineTable);
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(top);
        add(masterPanel);
        setPreferredSize(new Dimension(970, 620));
    }
    
    public Object getValue() {
        return medicineTable.getValue();
    }
    
    public void setValue(Object val) {
        medicineTable.setValue(val);
    }
    
    public void dispose() {
        //MasterTabPanel masterPanel = MasterTabPanel.getInstance();
        masterPanel.stopMedicine(medicineTable);
    }
}