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

import java.awt.BorderLayout;
import java.util.EnumSet;

import javax.swing.*;
import javax.swing.border.*;

import open.dolphin.client.*;
import open.dolphin.client.GUIConst;

/**
 * 処方スタンプエディタ。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class MedStampEditor2 extends StampModelEditor  {
    
    private static final long serialVersionUID = 3721140728191931803L;
    
    private static final String MEDICINE_TABLETITLE_BORDER    = "処方セット";
    private static final String EDITOR_NAME = "処方";
    
    /** 処方セット作成パネル */
    private MedicineTablePanel medicineTable;
    
    /** マスタセットパネル */
    private MasterTabPanel masterPanel;
    
    /** Creates new MedStampEditor2 */
    public MedStampEditor2() {
    }
    
    /**
     * プログラムを開始する。
     */
    public void start() {
        
        setTitle(EDITOR_NAME);
        
        // Medicine table
        medicineTable = new MedicineTablePanel(this);
        Border b = BorderFactory.createEtchedBorder();
        medicineTable.setBorder(BorderFactory.createTitledBorder(b, MEDICINE_TABLETITLE_BORDER));
        
        //
        // 処方で使用するマスタを指定し、マスタセットパネルを生成する
        //
        EnumSet<ClaimConst.MasterSet> set = EnumSet.of(
                ClaimConst.MasterSet.MEDICAL_SUPPLY,
                ClaimConst.MasterSet.ADMINISTRATION,
                ClaimConst.MasterSet.INJECTION_MEDICINE,
                ClaimConst.MasterSet.TOOL_MATERIAL);
        masterPanel = new MasterTabPanel(set);
        
        //
        // 処方作成であることを通知する
        //
        masterPanel.startMedicine(medicineTable);
        
        //
        // Connects
        //
        medicineTable.setParent(this);
        
        //
        // 上にスタンプのセットパネル、下にマスタのセットパネルを配置する
        // 全てのスタンプエディタに共通
        //
        JPanel top = new JPanel(new BorderLayout());
        top.add(medicineTable, BorderLayout.CENTER);
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(top);
        add(masterPanel);
        setPreferredSize(GUIConst.DEFAULT_STAMP_EDITOR_SIZE);
    }
    
    /**
     * 作成したスタンプを返す。
     * @return 作成したスタンプ
     */
    public Object getValue() {
        return medicineTable.getValue();
    }
    
    /**
     * 編集するスタンプを設定する。
     * @param val 編集するスタンプ
     */
    public void setValue(Object val) {
        System.err.println("setValue");
        medicineTable.setValue(val);
        System.err.println("setValue1");
    }
    
    /**
     * プログラムを終了する。
     */
    public void dispose() {
        masterPanel.stopMedicine(medicineTable);
    }
}