/*
 * StampHolder2.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
 * Copyright (C) 2004-2005 Digital Globe, Inc. All rights reserved.
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
import javax.swing.text.*;
import java.beans.*;
import java.io.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;

import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.ModuleModel;

import org.apache.velocity.app.Velocity;
import org.apache.velocity.VelocityContext;

/**
 * KartePane に Component　として挿入されるスタンプを保持スルクラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class StampHolder extends ComponentHolder implements IComponentHolder{
    
    private static final long serialVersionUID = -115789645956065719L;
    
    private static final char[] MATCHIES = {'０','１','２','３','４','５','６','７','８','９','　','ｍ','ｇ'};
    private static final char[] REPLACES = {'0','1','2','3','4','5','6','7','8','9',' ','m','g'};
    private static final Color FOREGROUND = new Color(20, 20, 140);
    private static final Color BACKGROUND = Color.white;
    private static final Color SELECTED_BORDER = new Color(255, 0, 153);
    
    private ModuleModel stamp;
    private StampRenderingHints hints;
    private KartePane kartePane;
    private Position start;
    private Position end;
    private boolean selected;
    
    private Color foreGround = FOREGROUND;
    private Color background = BACKGROUND;
    private Color selectedBorder = SELECTED_BORDER;
    
    /** Creates new StampHolder2 */
    public StampHolder(KartePane kartePane, ModuleModel stamp) {
        super();
        this.kartePane = kartePane;
        setHints(new StampRenderingHints());
        setForeground(foreGround);
        setBackground(background);
        setBorder(BorderFactory.createLineBorder(kartePane.getTextPane().getBackground()));
        setStamp(stamp);
    }
    
    /**
     * Focusされた場合のメニュー制御とボーダーを表示する。
     */
    public void focusGained(FocusEvent e) {
        //System.out.println("stamp gained");
        ChartMediator mediator = kartePane.getMediator();
        mediator.setCurrentComponent(this);
        mediator.getAction(GUIConst.ACTION_CUT).setEnabled(false);
        mediator.getAction(GUIConst.ACTION_COPY).setEnabled(false);
        mediator.getAction(GUIConst.ACTION_PASTE).setEnabled(false);
        mediator.getAction(GUIConst.ACTION_UNDO).setEnabled(false);
        mediator.getAction(GUIConst.ACTION_REDO).setEnabled(false);
        mediator.getAction(GUIConst.ACTION_INSERT_TEXT).setEnabled(false);
        mediator.getAction(GUIConst.ACTION_INSERT_SCHEMA).setEnabled(false);
        mediator.getAction(GUIConst.ACTION_INSERT_STAMP).setEnabled(false);
        mediator.enableMenus(new String[]{GUIConst.ACTION_COPY});
        if (kartePane.getTextPane().isEditable()) {
            mediator.enableMenus(new String[]{GUIConst.ACTION_CUT});
        } else {
            mediator.disableMenus(new String[]{GUIConst.ACTION_CUT});
        }
        mediator.disableMenus(new String[]{GUIConst.ACTION_PASTE});
        setSelected(true);
    }
    
    /**
     * Focusがはずれた場合のメニュー制御とボーダーの非表示を行う。
     */
    public void focusLost(FocusEvent e) {
        //System.out.println("stamp lost");
        //ChartMediator mediator = kartePane.getMediator();
        //String[] menus = new String[]{"cut", "copy", "paste"};
        //mediator.disableMenus(menus);
        setSelected(false);
    }
    
    /**
     * Popupメニューを表示する。
     */
    public void mabeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            JPopupMenu popup = new JPopupMenu();
            // popup時にStampHolderがFocusLostになるため
            popup.setFocusable(false);
            ChartMediator mediator = kartePane.getMediator();
            popup.add(mediator.getAction(GUIConst.ACTION_CUT));
            popup.add(mediator.getAction(GUIConst.ACTION_COPY));
            popup.add(mediator.getAction(GUIConst.ACTION_PASTE));
            popup.show(e.getComponent(), e.getX(), e.getY());
        }
    }
    
    /**
     * このスタンプホルダのKartePaneを返す。
     */
    public KartePane getKartePane() {
        return kartePane;
    }
    
    /**
     * スタンプホルダのコンテントタイプを返す。
     */
    public int getContentType() {
        return IComponentHolder.TT_STAMP;
    }
    
    /**
     * このホルダのモデルを返す。
     * @return
     */
    public ModuleModel getStamp() {
        return stamp;
    }
    
    /**
     * このホルダのモデルを設定する。
     * @param stamp
     */
    public void setStamp(ModuleModel stamp) {
        this.stamp = stamp;
        setMyText();
    }
    
    public StampRenderingHints getHints() {
        return hints;
    }
    
    public void setHints(StampRenderingHints hints) {
        this.hints = hints;
    }
    
    /**
     * 選択されているかどうかを返す。
     * @return 選択されている時 true
     */
    public boolean isSelected() {
        return selected;
    }
    
    /**
     * 選択属性を設定する。
     * @param selected 選択の時 true
     */
    public void setSelected(boolean selected) {
        boolean old = this.selected;
        this.selected = selected;
        if (old != this.selected) {
            if (this.selected) {
                this.setBorder(BorderFactory.createLineBorder(selectedBorder));
            } else {
                this.setBorder(BorderFactory.createLineBorder(kartePane.getTextPane().getBackground()));
            }
        }
    }
    
    /**
     * KartePane でこのスタンプがダブルクリックされた時コールされる。
     * StampEditor を開いてこのスタンプを編集する。
     */
    public void edit() {
        
        if (kartePane.getTextPane().isEditable()) {
            String category = stamp.getModuleInfo().getEntity();
            StampEditorDialog stampEditor = new StampEditorDialog(category,stamp);
            stampEditor.addPropertyChangeListener(StampEditorDialog.VALUE_PROP, this);
            stampEditor.start();
            
        } else {
            Toolkit.getDefaultToolkit().beep();
            return;
        }
    }
    
    /**
     * エディタで編集した値を受け取り内容を表示する。
     */
    public void propertyChange(PropertyChangeEvent e) {
        
        ModuleModel newStamp = (ModuleModel) e.getNewValue();
        
        if (newStamp != null) {
            // スタンプを置き換える
            importStamp(newStamp);
        }
    }
    
    /**
     * スタンプの内容を置き換える。
     * @param newStamp
     */
    public void importStamp(ModuleModel newStamp) {
        setStamp(newStamp);
        kartePane.getTextPane().validate();
        kartePane.getTextPane().repaint();
    }
    
    /**
     * TextPane内での開始と終了ポジションを保存する。
     */
    public void setEntry(Position start, Position end) {
        this.start = start;
        this.end = end;
    }
    
    /**
     * 開始ポジションを返す。
     */
    public int getStartPos() {
        return start.getOffset();
    }
    
    /**
     * 終了ポジションを返す。
     */
    public int getEndPos() {
        return end.getOffset();
    }
    
    /**
     * Velocity を利用してスタンプの内容を表示する。
     */
    private void setMyText() {
        
        try {
            IInfoModel model = getStamp().getModel();
            VelocityContext context = ClientContext.getVelocityContext();
            context.put("model", model);
            context.put("hints", getHints());
            context.put("stampName", getStamp().getModuleInfo().getStampName());
            
            // このスタンプのテンプレートファイルを得る
            String templateFile = getStamp().getModel().getClass().getName() + ".vm";
            
            // Merge する
            StringWriter sw = new StringWriter();
            BufferedWriter bw = new BufferedWriter(sw);
            InputStream instream = ClientContext.getTemplateAsStream(templateFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(instream, "SHIFT_JIS"));
            Velocity.evaluate(context, bw, "stmpHolder", reader);
            bw.flush();
            bw.close();
            reader.close();
            
            // 全角数字とスペースを直す
            String text = sw.toString();
            for (int i = 0; i < MATCHIES.length; i++) {
                text = text.replace(MATCHIES[i], REPLACES[i]);
            }
            this.setText(text);
            
            // カルテペインへ展開された時広がるのを防ぐ
            this.setMaximumSize(this.getPreferredSize());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}