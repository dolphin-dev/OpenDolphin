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

/**
 * Diagnosis editor.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class LDiagnosisEditor extends StampModelEditor  {
    
    private static final String EDITOR_NAME = "傷病名";
    
    /** 傷病名編集テーブル */
    private DiagnosisTablePanel diagnosisTable;
    
    /** マスターセットパネル */
    private MasterSetPanel masterPanel;
    
    
    /** 
     * Creates new DiagnosisEditor 
     */
    public LDiagnosisEditor(IStampEditorDialog context, MasterSetPanel masterPanel) {
        setContext(context);
        this.masterPanel = masterPanel;
        initComponent();
    }
    
    /**
     * エディタを開始する。
     */
    public void start() {
        masterPanel.startDiagnosis(diagnosisTable);
    }
    
    /**
     * Componentを初期化する。
     */
    private void initComponent() {
        
        setTitle(EDITOR_NAME);
        
        //
        // 傷病名編集テーブル
        // マスターセットパネル
        // を生成しレイアウトする
        //
        diagnosisTable = new DiagnosisTablePanel(this);
        Border b = BorderFactory.createEtchedBorder();
        diagnosisTable.setBorder(BorderFactory.createTitledBorder(b, EDITOR_NAME));
        
        setLayout(new BorderLayout(0, GUIConst.DEFAULT_CMP_V_SPACE));
        add(diagnosisTable, BorderLayout.CENTER);
    }
    
    /**
     * 編集した傷病名を返す。
     * @return RegisteredDiagnosisModel
     */
    public Object getValue() {
        return diagnosisTable.getValue();
    }
    
    /**
     * 編集する傷病名を設定する。
     * @param val RegisteredDiagnosisModel
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