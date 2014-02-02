/*
 * DiagnosisEditor.java
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

import javax.swing.*;
import javax.swing.border.Border;

import open.dolphin.client.*;
import open.dolphin.client.GUIConst;

import java.awt.*;
import java.util.*;

/**
 * 傷病名エディタクラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class DiagnosisEditor extends StampModelEditor  {
    
    /** エディタ名 */
    private static final String EDITOR_NAME = "傷病名";
    
    /** 傷病名編集テーブル */
    private DiagnosisTablePanel diagnosisTable;
    
    /** マスターセットパネル */
    private MasterTabPanel masterPanel;
    
    /** 
     * Creates new DiagnosisEditor 
     */
    public DiagnosisEditor() {
    }
    
    /**
     * プログラムを開始する。
     */
    public void start() {
        
        setTitle(EDITOR_NAME);
        
        //
        // 傷病名編集テーブルを生成する
        //
        diagnosisTable = new DiagnosisTablePanel(this);
        Border b = BorderFactory.createEtchedBorder();
        diagnosisTable.setBorder(BorderFactory.createTitledBorder(b, EDITOR_NAME));
        
        //
        // 傷病名で使用するマスタのセットを生成する
        //
        EnumSet<ClaimConst.MasterSet> set = EnumSet.of(
                ClaimConst.MasterSet.DIAGNOSIS);
        
        //
        // マスターセットを生成する
        //
        masterPanel = new MasterTabPanel(set);
        masterPanel.startDiagnosis(diagnosisTable);
        
        //
        // 全体をレイアウトする
        //
        setLayout(new BorderLayout(0, GUIConst.DEFAULT_CMP_V_SPACE));
        add(diagnosisTable, BorderLayout.NORTH);
        add(masterPanel, BorderLayout.CENTER);
        setPreferredSize(GUIConst.DEFAULT_STAMP_EDITOR_SIZE);
    }
    
    /**
     * 編集した傷病名を返す。
     * @return 編集した RegisteredDiagnosisModel
     */
    public Object getValue() {
        return diagnosisTable.getValue();
    }
    
    /**
     * 編集する傷病名を設定する。
     * @param val 編集する RegisteredDiagnosisModel
     */
    public void setValue(Object val) {
        diagnosisTable.setValue((Object[])val);
    }
    
    /**
     * リソースを解放する。
     */
    public void dispose() {
        masterPanel.stopDiagnosis(diagnosisTable);
    }
}