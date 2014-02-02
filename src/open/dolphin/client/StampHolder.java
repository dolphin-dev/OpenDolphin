/*
 * StampHolder2.java
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
import java.beans.*;
import java.io.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;

import open.dolphin.exception.*;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.Module;

import org.apache.velocity.app.Velocity;
import org.apache.velocity.VelocityContext;

/**
 * KartePane に Component　として挿入されるスタンプを保持スルクラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class StampHolder extends JLabel 
implements DragGestureListener, DragSourceListener, PropertyChangeListener,IComponentHolder {

    private static final Color FOREGROUND = new Color(20, 20, 140); //Color.blue;
    private static final Color BACKGROUND = Color.white;
    private static final int MAX_LINE_LENGTH = 20;
    private static final String ITEM_DELIM = ",";
    private static final String HEAD_MARK = "　";  // 全角スペース
	private static final Color SELECTED_BORDER = new Color(255, 0, 153); //Color.magenta;
	private static final Color DESELECTED_BORDER = Color.white;

	private int id;
	private Module stamp;
	private KartePane kartePane;
	private Position start;
	private Position end;   
	private boolean selected;
		
	private Color foreGround = FOREGROUND;
	private Color background = BACKGROUND;
	private Color selectedBorder = SELECTED_BORDER;
	private Color deSelectedBorder = DESELECTED_BORDER;
	private DragSource dragSource;
	//private boolean DEBUG = true;

    /** Creates new StampHolder2 */
    public StampHolder(KartePane kartePane, int id, Module stamp) {
    	
        this.kartePane = kartePane;
        this.id = id;
        this.stamp = stamp;

        setForeground(foreGround);
        setBackground(background);
        setBorder(BorderFactory.createLineBorder(kartePane.getBackground()));
        dragSource = new DragSource();
        dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, this);
        setMyText();
    }
    
    public int getContentType() {
        return IComponentHolder.TT_STAMP;
    }
    
    public int getId() {
        return id;
    }
    
    public Module getStamp() {
    	return stamp;
    }
    
    public boolean isSelected() {
        return selected;
    }
    
    public boolean toggleSelection() {
    	
    	//selected = selected ? false : true;
		// 2004-02-14 DnD のためトグル選択を止める
		selected = selected ? true : true;
    	Color c = selected ? selectedBorder : kartePane.getBackground();
		this.setBorder(BorderFactory.createLineBorder(c));
    	
    	return selected;
    }

    public void setSelected(boolean b) {
    	
        if (b) {
            this.setBorder(BorderFactory.createLineBorder(selectedBorder));
            selected = true;
        }
        else if (selected) {
            this.setBorder(BorderFactory.createLineBorder(kartePane.getBackground()));
            selected = false;
        }
    }

	/**
	 * KartePane でこのスタンプがダブルクリックされた時コールされる。
	 * StampEditor を開いてこのスタンプを編集する。 
	 */
    public void edit(boolean editable) {
        
        if (! editable) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }
		
		// Stamp のエディタは StampInfo のカテゴリ属性にマッピングされている
        StampEditorDialog stampEditor = null;
        String category = stamp.getModuleInfo().getEntity();
        
        try {
            stampEditor = new StampEditorDialog(category);
            stampEditor.addPropertyChangeListener("value", this);
            stampEditor.setValue(stamp);
                        
        } catch (DolphinException e) {
            System.out.println("DolphinException while opening the stampEditor: " + e.toString());
            e.printStackTrace();
			stampEditor = null;
        }
         
        if (stampEditor == null) return;
        
        Thread t = new Thread((Runnable)stampEditor);
        t.start();        
    }

	/**
	 * StampEditor で編集が終了すると通知される。
	 */
    public void propertyChange(PropertyChangeEvent e) {

        Module newStamp = (Module)e.getNewValue();
        if (newStamp == null) {
        	// キャンセルの時
            return;
        }
  
        // スタンプを置き換える
        stamp = newStamp;
        
        // もとのスタンプを KartePane から削除し、新しいものに入れ替える
        // 2002-5-15 remove が KartePane 経由だと正常な結果にならない
        // ケースがある
        int startOffset = start.getOffset();	// KartePane でのスタート位置
        int endOffset = end.getOffset();        // KartePane での終了位置
        KarteStyledDocument doc = (KarteStyledDocument)kartePane.getDocument();
        //doc.removeStamp(startOffset, endOffset - startOffset + 1);
        doc.removeStamp(startOffset, 3);   // TODO 要調査
        setMyText();
        doc.insertStamp(start, this);
        //setMyText();
    }

    public void setEntry(Position start, Position end) {
        this.start = start;
        this.end = end;
    }

    public int getStartPos() {
        return start.getOffset();
    }
    
    public int getEndPos() {
        return end.getOffset();
    }
    
    public void display() {
        setMyText();
    }
    
    /**
     * Velocity を利用してスタンプの内容を表示する。
     */
	private void setMyText() {
        
		IInfoModel model = stamp.getModel();
                
		try {
			VelocityContext context = ClientContext.getVelocityContext();
			context.put("model", model);
			context.put("stampName", stamp.getModuleInfo().getName());
			
			// このスタンプのテンプレートファイルを得る
			String templateFile = stamp.getModel().getClass().getName() + ".vm";
			debug(templateFile);
            
			// Merge する
			StringWriter sw = new StringWriter();
			BufferedWriter bw = new BufferedWriter(sw);
			InputStream instream = ClientContext.getTemplateAsStream(templateFile);
			BufferedReader reader = new BufferedReader(new InputStreamReader(instream, "SHIFT_JIS"));
			Velocity.evaluate(context, bw, "stmpHolder", reader);
			bw.flush();
			bw.close();
			reader.close();
			this.setText(sw.toString());
        
		} catch (Exception e) {
			System.out.println("Execption while setting the stamp text: " + e.toString());
			e.printStackTrace();
		}
	}
	        
    public void dragGestureRecognized(DragGestureEvent event) {
    	         
        Transferable tr = kartePane.getStampTrain();
                
        if (tr != null) {
            try {
                OrderList list = (OrderList)tr.getTransferData(OrderListTransferable.orderListFlavor);
                if (list.orderList.length == 1) {
                    Cursor cursor = DragSource.DefaultCopyDrop;
                    dragSource.startDrag(event, cursor, tr, this);
                }
            }
            catch (UnsupportedFlavorException e) {
                System.out.println("DEBUG UnsupportedException while getting the transfer data: " 
                                    + e.toString());
            }
            catch (IOException ie) {
                System.out.println("IOException while getting the transfer data: " 
                                    + ie.toString());
            }
        }
    }

    public void dragDropEnd(DragSourceDropEvent event) { 
    }

    public void dragEnter(DragSourceDragEvent event) {
    }

    public void dragOver(DragSourceDragEvent event) {
    }
    
    public void dragExit(DragSourceEvent event) {
    }    

    public void dropActionChanged ( DragSourceDragEvent event) {
    }    
    
    private void debug(String msg) {
    	if (ClientContext.isDebug()) {
    		System.out.println(msg);
    	}
    }
}