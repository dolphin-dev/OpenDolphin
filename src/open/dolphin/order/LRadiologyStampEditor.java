/*
 * RadiologyStampEditor.java
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

import javax.swing.*;
import javax.swing.border.*;

import open.dolphin.client.*;

import java.awt.*;

/**
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class LRadiologyStampEditor extends StampModelEditor  {
    
    private static final long serialVersionUID = 2467212598346800512L;
    
    private RadItemTablePanel testTable;
    private MasterSetPanel masterPanel;
    
    /**
     * Creates new InjectionStampEditor
     */
    public LRadiologyStampEditor(IStampEditorDialog context, MasterSetPanel masterPanel) {
        setContext(context);
        this.masterPanel = masterPanel;
        initComponent();
    }
    
    public void start() {
        ClaimConst.ClaimSpec spec = ClaimConst.ClaimSpec.RADIOLOGY;
        masterPanel.setSearchClass(spec.getSearchCode());
        masterPanel.setRadLocationEnabled(true);
        masterPanel.startTest(testTable);
    }
    
    private void initComponent() {
        
        // 放射線のCLAIM 仕様を得る
        ClaimConst.ClaimSpec spec = ClaimConst.ClaimSpec.RADIOLOGY;
        
        // セットテーブルを生成し CLAIM パラメータを設定する
        testTable = new RadItemTablePanel(this);
        testTable.setOrderName(spec.getName());
        testTable.setClassCode(spec.getClassCode());
        testTable.setClassCodeId(ClaimConst.CLASS_CODE_ID);
        testTable.setSubClassCodeId(ClaimConst.SUBCLASS_CODE_ID);
        
//        // 放射線メソッドのリストボックスとセットテーブルをリスナ関係にする
//        RadiologyMethod method = new RadiologyMethod();
//        method.addPropertyChangeListener(RadiologyMethod.RADIOLOGY_MEYTHOD_PROP, testTable);
        
        // タイトルを設定しレイアウトする
        setTitle(spec.getName());
        Border b = BorderFactory.createEtchedBorder();
        testTable.setBorder(BorderFactory.createTitledBorder(b, spec.getName()));
        
        this.setLayout(new BorderLayout());
        //this.add(method, BorderLayout.WEST);
        this.add(testTable, BorderLayout.CENTER);
        
        //setPreferredSize(GUIConst.DEFAULT_STAMP_EDITOR_SIZE);
    }
    
    public Object getValue() {
        return testTable.getValue();
    }
    
    public void setValue(Object val) {
        testTable.setValue(val);
    }
    
    public void dispose() {
        masterPanel.stopTest(testTable);
    }
}