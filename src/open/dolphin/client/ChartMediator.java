/*
 * ChartMediator.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
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
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.undo.*;
import java.util.*;
import java.awt.event.*;

/**
 * Mediator class to control Karte Window Menu.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class ChartMediator 
implements CaretListener, UndoableEditListener, IRoutingTarget, ActionListener {
    
    private ChartPlugin chartService;
    
    // Actions
    public Action newKarteAction;
    public Action copyNewKarteAction;
    public Action newReferralAction;
    public Action saveKarteAction;
    public Action printerSetupAction;
    public Action printAction;
    public Action closeAction;

    public Action modifyKarteAction;  // Modify
    public Action undoAction;
    public Action redoAction;
    public Action cutAction;
    public Action copyAction;
    public Action pasteAction;
    public Action deleteAction;
    
    public Action resetStyleAction;
    public Action redAction;
    public Action blueAction;
    public Action greenAction;
    public Action s10Action;
    public Action s12Action;
    public Action s14Action;
    public Action s16Action;
    public Action s18Action;
    public Action s20Action;
    public Action s24Action;
    public Action boldAction;
    public Action italicAction;
    public Action underlineAction;
    
    public Action insertImageAction;
    
    // 選択が起こっている KartePane
    private KartePane curPane;
    
    // Undo Manager
    private UndoManager undoManager = new UndoManager();
    
    /**
     * Creates new M3C 
     */
    public ChartMediator(ChartPlugin s) {     
        super();
        this.chartService = s;
    }
    
    private void printNull(Action a, String n){
        if (a == null) {
            System.out.println(n + " is null");
        }
    }
    
    public void registerActions(HashMap map) {
        
        newKarteAction = (Action)map.get("newKarteAction");
        printNull(newKarteAction, "newKarteAction");
        
        copyNewKarteAction = (Action)map.get("copyNewKarteAction");
        printNull(copyNewKarteAction, "copyNewKarteAction");
        
        newReferralAction = (Action)map.get("newReferralAction");
        printNull(newReferralAction, "newReferralAction");
        
        saveKarteAction = (Action)map.get("saveKarteAction");
        printNull(saveKarteAction, "saveKarteAction");
        
        printAction = (Action)map.get("printAction");
        printNull(printAction, "printAction");
        
        printerSetupAction = (Action)map.get("printerSetupAction");
        printNull(printerSetupAction, "printerSetupAction");
        
        closeAction = (Action)map.get("closeAction");
        printNull(closeAction, "closeAction");

        modifyKarteAction = (Action)map.get("modifyKarteAction");
        printNull(modifyKarteAction, "modifyKarteAction");
        
        undoAction = (Action)map.get("undoAction");
        printNull(undoAction, "undoAction");
        
        redoAction = (Action)map.get("redoAction");
        printNull(redoAction, "redoAction");
        
        cutAction = (Action)map.get("cutAction");
        printNull(cutAction, "cutAction");
        
        copyAction = (Action)map.get("copyAction");
        printNull(copyAction, "copyAction");
        
        pasteAction = (Action)map.get("pasteAction");
        printNull(pasteAction, "pasteAction");
        
        deleteAction = (Action)map.get("deleteAction");
        printNull(deleteAction, "deleteAction");
    
        resetStyleAction = (DlAction)map.get("resetStyleAction");
        printNull(resetStyleAction, "resetStyleAction");
        
        redAction = (Action)map.get("redAction");
        printNull(redAction, "redAction");
        
        blueAction = (Action)map.get("blueAction");
        printNull(blueAction, "blueAction");
        
        greenAction = (Action)map.get("greenAction");
        printNull(greenAction, "greenAction");
        
        s10Action = (Action)map.get("s10Action");
        printNull(s10Action, "s10Action");
        
        s12Action = (Action)map.get("s12Action");
        printNull(s12Action, "s12Action");
        
        s14Action = (Action)map.get("s14Action");
        printNull(s14Action, "s14Action");
        
        s16Action = (Action)map.get("s16Action");
        printNull(s16Action, "size16Action");
        
        s18Action = (Action)map.get("s18Action");
        printNull(s18Action, "size18Action");
        
        s20Action = (Action)map.get("s20Action");
        printNull(s20Action, "size20Action");
        
        s24Action = (Action)map.get("s24Action");
        printNull(s24Action, "size24Action");
        
        boldAction = (Action)map.get("boldAction");
        printNull(boldAction, "boldAction");
        
        italicAction = (Action)map.get("italicAction");
        printNull(italicAction, "italicAction");
        
        underlineAction = (Action)map.get("underlineAction");
        printNull(underlineAction, "underlineAction");
    
        insertImageAction = (Action)map.get("insertImageAction");
        printNull(insertImageAction, "insertImageAction");
    }
    
    
    public void actionRouted(Action source) {
        
        if (source == newKarteAction) {
            chartService.newKarte();
            return;
        }
        
        if (source == copyNewKarteAction) {
            chartService.copyNewKarte();
            return;
        } 
        
        if (source == saveKarteAction) {
            chartService.save();
            return;
        }
        
        if (source == printerSetupAction) {
            chartService.printerSetup();
            return;
        }
        
        if (source == printAction) {
            chartService.print();
            return;
        }
        
        if (source == closeAction) {
            chartService.processWindowClosing();
            return;
        }        
        
        if (source == modifyKarteAction) {
            chartService.modifyKarte();
            return;
        } 
        
        if (source == undoAction) {
            undo();
            return;
        }
        
        if (source == redoAction) {
            redo();
            return;
        } 
        
        if (source == cutAction) {
            cut();
            return;
        }
        
        if (source == copyAction) {
            copy();
            return;
        } 
        
        if (source == pasteAction) {
            paste();
            return;
        }
        
        if (source == deleteAction) {
            delete();
            return;
        }
        
        if (source == resetStyleAction) {
            resetStyle();
            return;
        }        

        if (source == insertImageAction) {
            chartService.insertImage();
            return;
        }       
    }
    
    public void actionPerformed(ActionEvent e) {
        
    }
    
    /**
     * KartePane から選択通知を受け、編集・スタイル・挿入メニューを制御する
     * @aparam e 選択イベント
     */
    public void selected(SelectionEvent e) {
        //System.out.println("Selection changed");
        KartePane newPane = (KartePane)e.getSource();
        
        // 相手ペインの選択を解除する
        if ((curPane != null) && (newPane != curPane)) {
            curPane.clearAllSelection();
        }
        
        // 通知されたペインについて毎回メニューを制御する 
        adjustEditMenu(newPane);
        adjustStyleMenu(newPane);
        adjustInsertMenu(newPane);
        
        // Current Pane に保存する
        curPane = newPane;
    }
    
    /**
     * KartePane から CaretEvent を受け、編集・スタイル・挿入メニューを制御する
     * @aparam e CaretEvent 
     */
    public void caretUpdate(CaretEvent e) {
        
        KartePane newPane = (KartePane)e.getSource();
        
        if (e.getDot() != e.getMark()) {
            
            // テキスト選択が起こったのでスタンプとイメージ選択を解除する
            newPane.diSelectStamp();
        }
        if ((curPane != null) && (newPane != curPane)) {
            
            // 相手ペインの選択を解除する
            curPane.clearAllSelection();
        }
        
        // 通知されたペインについて毎回メニューを制御する 
        adjustEditMenu(newPane);
        adjustStyleMenu(newPane);
        adjustInsertMenu(newPane);
        
        // Current Pane に保存する
        curPane = newPane;
    }
    
    private void adjustEditMenu(KartePane kartePane) {
        
        boolean editable = kartePane.isEditable();
        boolean selected = kartePane.hasSelection() ? true : false;
        copyAction.setEnabled(selected);

        if (editable && selected) {
            cutAction.setEnabled(true);
            deleteAction.setEnabled(true);
        }
        else {
            cutAction.setEnabled(false);
            deleteAction.setEnabled(false);
        }
        
        adjustPasteMenu(kartePane);
    }
    
    private void adjustPasteMenu(KartePane kartePane) {
     
        if (! kartePane.isEditable()) {
            pasteAction.setEnabled(false);
            return;
        }
        boolean b = kartePane.canPaste();
        pasteAction.setEnabled(b);
    }
    
    private void adjustStyleMenu(KartePane kartePane) {
    
        boolean b = kartePane.isEditable() ? true : false;
        //boolean b2 = kartePane.hasTextSelection() ? true : false;
        //boolean b = ( b1 && b2 ) ? true : false;
        
        resetStyleAction.setEnabled(b);
        redAction.setEnabled(b);
        blueAction.setEnabled(b);
        greenAction.setEnabled(b);
        s10Action.setEnabled(b);
        s12Action.setEnabled(b);
        s14Action.setEnabled(b);
        s16Action.setEnabled(b);
        s18Action.setEnabled(b);
        s20Action.setEnabled(b);
        s24Action.setEnabled(b);
        boldAction.setEnabled(b);
        italicAction.setEnabled(b);
        underlineAction.setEnabled(b);
    }
    
    private void adjustInsertMenu(KartePane kartePane) {
        boolean b = kartePane.isEditable() ? true : false;
        insertImageAction.setEnabled(b);
    }
    
    ///////////////////////////////////////////////////////////////////////////
    
    public void cut() {
        curPane.doCut();
    }
    
    public void copy() {
         curPane.doCopy();
    }
    
    public void paste() {
         curPane.doPaste();
    }
    
    public void delete() {
         curPane.doDelete();
    }
    
    public void resetStyle() {
        if (curPane != null) {
            curPane.setCharacterAttributes(SimpleAttributeSet.EMPTY, true);
        }
    }
        
    ///////////////////////////////////////////////////////////////////////////
    
    public void setUndoManager(UndoManager undo) {
        undoManager = undo;
        
        if (undoManager == null) {
            undoAction.setEnabled(false);
            redoAction.setEnabled(false);
        }
        else {
            updateUndoAction();
	    updateRedoAction();
        }
    }
    
    public void undoableEditHappened(UndoableEditEvent e) {
        undoManager.addEdit(e.getEdit());
        updateUndoAction();
        updateRedoAction();
    }
    
    public void undo() {
        try {
            undoManager.undo();
	    
        } catch (CannotUndoException ex) {
             System.out.println("Unable to undo: " + ex);
             //ex.printStackTrace();
        }
        updateUndoAction();
        updateRedoAction();
    }
    
    public void redo() {
        try {
            undoManager.redo();
        } catch (CannotRedoException ex) {
            System.out.println("Unable to redo: " + ex);
            //ex.printStackTrace();
        }
	updateRedoAction();
        updateUndoAction();
    }
    
    private void updateUndoAction() {

        if(undoManager.canUndo()) {
            undoAction.setEnabled(true);
        }
	else {
            undoAction.setEnabled(false);
        }
    }
    
    private void updateRedoAction() {

        if(undoManager.canRedo()) {
            redoAction.setEnabled(true);
        }
	else {
            redoAction.setEnabled(false);
        }
    }    
}