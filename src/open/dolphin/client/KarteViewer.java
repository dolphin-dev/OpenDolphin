/*
 * KarteEditor2.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
 * Copyright (C) 2003-2005 Digital Globe, Inc. All rights reserved.
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.print.PageFormat;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;

import open.dolphin.infomodel.DocInfoModel;
import open.dolphin.infomodel.DocumentModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.ModelUtils;

/**
 * 2号カルテクラス。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class KarteViewer extends DefaultChartDocument implements Comparable {
    
    
    /** 選択されている時のボーダ色 */
    private static final Color SELECTED_COLOR = new Color(255, 0, 153);
    
    /** 選択された状態のボーダ */
    private static final Border SELECTED_BORDER = BorderFactory.createLineBorder(SELECTED_COLOR);
    
    /** 選択されていない時のボーダ色 */
    private static final Color NOT_SELECTED_COLOR = new Color(227, 250, 207);
    
    /** 選択されていない状態のボーダ */
    private static final Border NOT_SELECTED_BORDER = BorderFactory.createLineBorder(NOT_SELECTED_COLOR);
    
    /** タイムスタンプの foreground カラー */
    private static final Color TIMESTAMP_FORE = Color.BLUE;
    
    /** タイムスタンプのフォントサイズ */
    private static final int TIMESTAMP_FONT_SIZE = 14;
    
    /** タイムスタンプフォント */
    private static final Font TIMESTAMP_FONT = new Font("Dialog", Font.PLAIN, TIMESTAMP_FONT_SIZE);
    
    /** タイムスタンプパネル FlowLayout のマージン */
    private static final int TIMESTAMP_SPACING = 7;
    
    /** 仮保存中のドキュメントを表す文字 */
    private static final String UNDER_TMP_SAVE = " - 仮保存中";
    
    //
    // インスタンス変数
    //
    
    /** この view のモデル */
    private DocumentModel model;
    
    /** タイムスタンプラベル */
    private JLabel timeStampLabel;
    
    /** SOA Pane */
    private KartePane soaPane;
    
    /** P Pane */
    private KartePane pPane;
    
    /** 2号カルテパネル */
    private Panel2 panel2;
    
    /** タイムスタンプの foreground カラー */
    private Color timeStampFore = TIMESTAMP_FORE;
    
    /** タイムスタンプのフォント */
    private Font timeStampFont = TIMESTAMP_FONT;
    
    private int timeStampSpacing = TIMESTAMP_SPACING;
    
    private boolean avoidEnter;
    
    /** 選択されているかどうかのフラグ */
    private boolean selected;
    
    /**
     * Creates new KarteViewer
     */
    public KarteViewer() {
    }
    
    
    public void setAvoidEnter(boolean b) {
        avoidEnter = b;
    }
    
    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    // Junzo SATO
    public void printPanel2(final PageFormat format) {
        String name = getContext().getPatient().getFullName();
        panel2.printPanel(format, 1, false, name);
    }
    
    public void printPanel2(final PageFormat format, final int copies,
            final boolean useDialog) {
        String name = getContext().getPatient().getFullName();
        panel2.printPanel(format, copies, useDialog, name);
    }
    
    public void print() {
        PageFormat pageFormat = getContext().getContext().getPageFormat();
        this.printPanel2(pageFormat);
    }
    
    //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
        
    /**
     * ドキュメントの長さを view のピクセル数にして返す。
     * @return modelToView(ドキュメントの長さ)
     */
    public int getActualHeight() {
        try {
            JTextPane pane = soaPane.getTextPane();
            int pos = pane.getDocument().getLength();
            Rectangle r = pane.modelToView(pos);
            int hsoa = r.y;
            
            pane = pPane.getTextPane();
            pos = pane.getDocument().getLength();
            r = pane.modelToView(pos);
            int hp = r.y;
            
            return Math.max(hsoa, hp);
            
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
        return 0;
    }
    
    public void adjustSize() {
        int h = getActualHeight();
        soaPane.getTextPane().setPreferredSize(new Dimension(KartePane.PANE_WIDTH, h));
        pPane.getTextPane().setPreferredSize(new Dimension(KartePane.PANE_WIDTH, h));
    }
    
    /**
     * SOA Pane を返す。
     * @return soaPane
     */
    public KartePane getSOAPane() {
        return soaPane;
    }
    
    /**
     * P Pane を返す。
     * @return pPane
     */
    public KartePane getPPane() {
        return pPane;
    }
    
    /**
     * コンテナからコールされる enter() メソッドで
     * メニューを制御する。
     */
    public void enter() {
        
        if (avoidEnter) {
            return;
        }
        super.enter();
        
        // ReadOnly 属性
        boolean canEdit = getContext().isReadOnly() ? false : true;
        
        // 仮保存かどうか
        boolean tmp = model.getDocInfo().getStatus().equals(IInfoModel.STATUS_TMP) ? true : false;
        
        // 新規カルテ作成が可能な条件
        boolean newOk = canEdit && (!tmp) ? true : false;
        
        ChartMediator mediator = getContext().getChartMediator();
        mediator.getAction(GUIConst.ACTION_NEW_KARTE).setEnabled(newOk); // 新規カルテ
        mediator.getAction(GUIConst.ACTION_PRINT).setEnabled(true); // 印刷
        mediator.getAction(GUIConst.ACTION_MODIFY_KARTE).setEnabled(canEdit); // 修正
    }
    
    /**
     * 初期化する。GUI を構築する。
     */
    public void initialize() {
        
        // TimeStampLabel を生成する
        timeStampLabel = new JLabel("TimeStamp");
        timeStampLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timeStampLabel.setForeground(timeStampFore);
        timeStampLabel.setFont(timeStampFont);
        
        // SOA Pane を生成する
        soaPane = new KartePane();
        if (model != null) {
            // Schema 画像にファイル名を付けるのために必要
            String docId = model.getDocInfo().getDocId();
            soaPane.setDocId(docId);
        }
        
        // P Pane を生成する
        pPane = new KartePane();
        
        // 互いに関連ずける
        soaPane.setRole(IInfoModel.ROLE_SOA, pPane);
        pPane.setRole(IInfoModel.ROLE_P, soaPane);
        
        //
        // 2号カルテを生成する
        //
        panel2 = new Panel2();
        panel2.setLayout(new BorderLayout());
        
        JPanel flowPanel = new JPanel();
        flowPanel.setLayout(new BoxLayout(flowPanel, BoxLayout.X_AXIS));
        flowPanel.add(soaPane.getTextPane());
        flowPanel.add(Box.createHorizontalStrut(KartePane.PANE_DIVIDER_WIDTH));
        flowPanel.add(pPane.getTextPane());        
        
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, timeStampSpacing));
        timePanel.add(timeStampLabel);
        //timePanel.setBorder(BorderFactory.createLineBorder(Color.BLUE));
        panel2.add(timePanel, BorderLayout.NORTH);
        panel2.add(flowPanel, BorderLayout.CENTER);
        
        setUI(panel2);
    }
    
    /**
     * プログラムを開始する。
     */
    public void start() {
        //
        // Creates GUI
        //
        this.initialize();
        
        //
        // Display Model
        //
        if (this.getModel() != null) {
            //
            // 確定日を分かりやすい表現に変える
            //
            String timeStamp = ModelUtils.getDateAsFormatString(
                    model.getDocInfo().getFirstConfirmDate(), 
                    IInfoModel.KARTE_DATE_FORMAT);
            
            if (model.getDocInfo().getStatus().equals(IInfoModel.STATUS_TMP)) {
                StringBuilder sb = new StringBuilder();
                sb.append(timeStamp);
                sb.append(UNDER_TMP_SAVE);
                timeStamp = sb.toString();
            }
            timeStampLabel.setText(timeStamp);
            KarteRenderer_2 renderer = new KarteRenderer_2(soaPane, pPane);
            renderer.render(model);
        }
        
        //
        // モデル表示後にリスナ等を設定する
        //
        ChartMediator mediator = getContext().getChartMediator();
        soaPane.init(false, mediator);
        pPane.init(false, mediator);
        enter();
    }
    
    public void stop() {
        soaPane.clear();
        pPane.clear();
    }
    
    /**
     * 表示するモデルを設定する。
     * @param model 表示するDocumentModel
     */
    public void setModel(DocumentModel model) {
        this.model = model;
    }
    
    /**
     * 表示するモデルを返す。
     * @return 表示するDocumentModel
     */
    public DocumentModel getModel() {
        return model;
    }
    
    /**
     * 選択状態を設定する。
     * 選択状態によりViewのボーダの色を変える。
     * @param selected 選択された時 true
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
        if (selected) {
            getUI().setBorder(SELECTED_BORDER);
        } else {
            getUI().setBorder(NOT_SELECTED_BORDER);
        }
    }
    
    /**
     * 選択されているかどうかを返す。
     * @return 選択されている時 true
     */
    public boolean isSelected() {
        return selected;
    }
    
    
    /*public boolean copyStamp() {
            return pPane.copyStamp();
    }*/
    
    public int hashCode() {
        return getModel().getDocInfo().getDocId().hashCode() + 72;
    }
    
    public boolean equals(Object other) {
        if (other != null && other.getClass() == this.getClass()) {
            DocInfoModel otheInfo = ((KarteViewer) other).getModel()
            .getDocInfo();
            return getModel().getDocInfo().equals(otheInfo);
        }
        return false;
    }
    
    public int compareTo(Object other) {
        if (other != null && other.getClass() == this.getClass()) {
            DocInfoModel otheInfo = ((KarteViewer) other).getModel()
            .getDocInfo();
            return getModel().getDocInfo().compareTo(otheInfo);
        }
        return -1;
    }
}