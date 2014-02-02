/*
 * MedStampEditor2.java
 * Copyright (C) 2007 Dolphin Project. All rights reserved.
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

import java.awt.BorderLayout;
import javax.swing.*;
import javax.swing.border.*;

import open.dolphin.client.*;

/**
 * 処方スタンプエディタ。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class LMedStampEditor2 extends StampModelEditor  {
    
    private static final long serialVersionUID = 3721140728191931803L;
    
    private static final String MEDICINE_TABLETITLE_BORDER    = "処方セット";
    private static final String EDITOR_NAME = "処方";
    
    private MedicineTablePanel medicineTable;
    private MasterSetPanel masterPanel;
    
    /** Creates new MedStampEditor2 */
    public LMedStampEditor2(IStampEditorDialog context, MasterSetPanel masterPanel) {
        setContext(context);
        this.masterPanel = masterPanel;
        initComponent();
    }
    
    public void start() {
        masterPanel.startMedicine(medicineTable);
    }
    
    private void initComponent() {
        
        setTitle(EDITOR_NAME);
        
        // Medicine table
        medicineTable = new MedicineTablePanel(this);
        Border b = BorderFactory.createEtchedBorder();
        medicineTable.setBorder(BorderFactory.createTitledBorder(b, MEDICINE_TABLETITLE_BORDER));
        
        // Connects
        medicineTable.setParent(this);
        
        this.setLayout(new BorderLayout());
        this.add(medicineTable, BorderLayout.CENTER);
        
        //setPreferredSize(GUIConst.DEFAULT_STAMP_EDITOR_SIZE);
    }
    
    public Object getValue() {
        return medicineTable.getValue();
    }
    
    public void setValue(Object val) {
        medicineTable.setValue(val);
    }
    
    public void dispose() {
        masterPanel.stopMedicine(medicineTable);
    }
}