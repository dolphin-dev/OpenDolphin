/*
 * KartePane.java
 * Copyright(C) 2002 Dolphin Project. All rights reserved.
 * Copyright (C) 2003-2004 Digital Globe, Inc. All rights reserved.
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
import javax.swing.event.*;
import javax.swing.text.*;

import open.dolphin.dao.*;
import open.dolphin.infomodel.ExtRef;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.Module;
import open.dolphin.infomodel.ModuleInfo;
import open.dolphin.infomodel.Schema;
import open.dolphin.project.*;

import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.beans.*;
import java.awt.im.InputSubset;
// Junzo SATO
import jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.*;

/**
 * Karte Pane  
 *
 * @author  Kazushi Minagawa, Digital Globe, inc.
 */
public final class KartePane extends JTextPane implements DropTargetListener, PropertyChangeListener {
    
    private static final int TITLE_LENGTH       = 15;
    private static final int COPY_CAPACITY      = 10;
    private static final int STAMP_CAPACITY     = 10;
    private static final int TT_NONE_SELECTION  = 0;
    private static final int TT_TEXT_SELECTION  = 1;
    private static final int TT_STAMP_SELECTION = 2;
    private static final int TT_IMAGE_SELECTION = 3;
     
    private static final DataFlavor SUPPORT_FLAVOR = 
                            StampTreeTransferable.stampTreeNodeFlavor;    
    
    private static int stampId;
    private SelectionController selectionController;
    private ArrayList stampSelectionList;
    private ArrayList imageSelectionList;
    private String myRole;
    private KartePane myPartner;
    private boolean dirty;
    private KarteEditor parent;
    private int initialLength;
    private ChartMediator mediator;
    private String docId;
    private DropTarget dropTarget;
    private Color uneditableColor = new Color(227, 250, 207);
    private StampEditorDialog stampEditor;
    

    /** Creates new KartePane2 */
    public KartePane(boolean editable, ChartMediator mediator) {
        
        this.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent event) {
                getInputContext().setCharacterSubsets(new Character.Subset[] {InputSubset.KANJI});
            }
            public void focusLosted(FocusEvent event) {
                getInputContext().setCharacterSubsets(null);
            }
        });
    
        this.mediator = mediator;
        
        // Assign key stroke
        Keymap keymap = getKeymap();
        KeyStroke keystroke = KeyStroke.getKeyStroke(KeyEvent.VK_X,KeyEvent.CTRL_MASK);
        keymap.addActionForKeyStroke(keystroke, mediator.cutAction);
        
        keystroke = KeyStroke.getKeyStroke(KeyEvent.VK_C,KeyEvent.CTRL_MASK);
        keymap.addActionForKeyStroke(keystroke, mediator.copyAction);
        
        keystroke = KeyStroke.getKeyStroke(KeyEvent.VK_V,KeyEvent.CTRL_MASK);
        keymap.addActionForKeyStroke(keystroke, mediator.pasteAction);
        
        keystroke = KeyStroke.getKeyStroke(KeyEvent.VK_Z,KeyEvent.CTRL_MASK);
        keymap.addActionForKeyStroke(keystroke, mediator.undoAction);
        
        keystroke = KeyStroke.getKeyStroke(KeyEvent.VK_Y,KeyEvent.CTRL_MASK);
        keymap.addActionForKeyStroke(keystroke, mediator.redoAction);
        
        selectionController = new SelectionController();
                
        addCaretListener(mediator);
        
        setEditableProp(editable);
        
        this.setDragEnabled(true);
    }
    
    public void setEditableProp(boolean b) {
        if( b && (dropTarget == null)) {
            dropTarget = new DropTarget(this, this);
            getDocument().addUndoableEditListener(mediator);
        }
        else {
            setBackgroundUneditable();
        }
        setEditable(b);
    }
    
    public void setBackgroundUneditable() {
        setBackground(uneditableColor);
        setOpaque(true);
    }
    
    public void setRole(String role, KartePane partner) {
        myRole = role;
        myPartner = partner;
    }
    
    public void setParent(KarteEditor parent) {
        this.parent = parent;
    }
    
    public void setDocId(String val) {
        docId = val;
    }
    
    public boolean isDirty() {
        return isEditable() ? dirty : false;
    }    
    
    public String getTitle() {
        String text = null;
        try {
            KarteStyledDocument doc = (KarteStyledDocument)getDocument();
            int len = doc.getLength();
            int freeTop = doc.getFreeTop();
            int freeLen = len - freeTop;
            freeLen = freeLen < TITLE_LENGTH ? freeLen : TITLE_LENGTH;
            text = getText(freeTop, freeLen).trim();
        }
        catch (Exception e) {
        	System.out.println("Exception while getting the documednt title: " + e.toString());
        	e.printStackTrace();
        }
        return text;
    }    
    
    /**
     * このペインのコンテンツをクリアする。
     */
    public void init() {
    	
        KarteStyledDocument doc = new KarteStyledDocument();
		this.setDocument(doc);
        doc.setParent(this);
        
        if (stampSelectionList != null) {
            stampSelectionList.clear();
        }
        if (imageSelectionList != null) {
            imageSelectionList.clear();
        }
    }
    
    public void setTimestamp(String val) {
        
        final KarteStyledDocument doc = (KarteStyledDocument)getDocument();
        doc.setTimestamp(val);
        
        // Dirty 判定用の DocumentListener をつける
        if (isEditable()) {
            
            // スタンプ挿入後が初期長になる
            initialLength = doc.getLength();
            
            doc.addDocumentListener(new DocumentListener() {
                
                public void insertUpdate(DocumentEvent e) {
                    //boolean newDirty = true;
                    boolean newDirty = doc.getLength() > initialLength ? true : false;
                    if (newDirty != dirty) {
                        dirty = newDirty;
                        
                        // KarteEditor へ通知
                        parent.setDirty(dirty);
                    }
                }
                
                public void removeUpdate(DocumentEvent e) {
                    boolean newDirty = doc.getLength() > initialLength ? true : false;
                    if (newDirty != dirty) {
                        dirty = newDirty;
                        
                        // KarteEditor へ通知
                        parent.setDirty(dirty);
                    }
                }
                
                public void changedUpdate(DocumentEvent e) {
                }
            });
        }
    }    
    
    ////////////////////////////////////////////////
    
	public void setLogicalStyle(String str) {
		((KarteStyledDocument)this.getDocument()).setLogicalStyle(str);
	}
    
	public void clearLogicalStyle() {
		((KarteStyledDocument)this.getDocument()).clearLogicalStyle();
	}
	
	public void makeParagraph() {
		((KarteStyledDocument)this.getDocument()).makeParagraph();
	}	
    	
	public void insertFreeString(String s, AttributeSet a) {
		((KarteStyledDocument)this.getDocument()).insertFreeString(s, a);
	}
	
	////////////////////////////////////////////////          
    
    /*
     * このペインに Stamp を挿入する。
     */
    public void stamp(Module s) {
        if (s == null) {
            return;
        }
        
        // StampHolder に格納しコンポーネントとして挿入する
        StampHolder h = new StampHolder(this, stampId++, s);
        h.addMouseListener(selectionController);
        ((KarteStyledDocument)this.getDocument()).stamp(h);
    }
        
    public void stampSchema(Schema o) {
        if (o == null) {
            return;
        }
        SchemaHolder h = new SchemaHolder(this, stampId, o);
        h.addMouseListener(selectionController);
        ((KarteStyledDocument)this.getDocument()).stampSchema(h);
    }
    
    /*
     * このペインに TextStamp を挿入する。
     */
    public void insertTextStamp(String s) {
        ((KarteStyledDocument)this.getDocument()).insertTextStamp(s);
    }
           
    ////////////////////////////////////////////////
        
    public boolean isDragAcceptable(DropTargetDragEvent evt) {
        return (isEditable() && ((evt.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) != 0))
               ? true
               : false;
    }
    
    public boolean isDropAcceptable(DropTargetDropEvent evt) {
        return (isEditable() && ((evt.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) != 0))
               ? true
               : false;
    }    
    
    public void dragEnter(DropTargetDragEvent evt) {
        if (! isDragAcceptable(evt)) {
            evt.rejectDrag();
        }
    }
   
    public void dragOver(DropTargetDragEvent evt) {
        if (isDragAcceptable(evt)) {
            // Called from the event dispatch
            parent.setDropTargetBorder(true);
        }
    }
   
    public void dragExit(DropTargetEvent evt) {
        parent.setDropTargetBorder(false);
    }
   
    public void dropActionChanged(DropTargetDragEvent evt) {
        if (! isDragAcceptable(evt)) {
            evt.rejectDrag();
        }
    }
    
    public void drop(DropTargetDropEvent event) {
        
        if (! isDropAcceptable(event)) {
            event.rejectDrop();
            parent.setDropTargetBorder(false);
            Toolkit.getDefaultToolkit().beep();
            event.getDropTargetContext().dropComplete(true);
            return;
        }
        
        // Get Transferable
        final Transferable tr = event.getTransferable();
        
        // Drop 位置を得る
        final Point loc = event.getLocation();
        
        // Called from the event dispatch
        event.acceptDrop(DnDConstants.ACTION_COPY);
        event.getDropTargetContext().dropComplete(true);
        parent.setDropTargetBorder(false);
        
        // Do actual drop
        Runnable r = new Runnable() {
            public void run() {
                boolean ok = doDrop(tr, loc);
            }
        };
        Thread t = new Thread(r);
        t.start();
    }
    
    //////////////////////////////////////////////////////////
    
    private boolean doDrop(Transferable tr, Point loc) {
        
        int flavor = -1;
        
        // サポートしている　Data Flavor か
        if (tr.isDataFlavorSupported(SUPPORT_FLAVOR)) { 
            flavor = 0;
            
        } else if (tr.isDataFlavorSupported(SchemaListTransferable.schemaListFlavor)) {
            flavor = 1;
        }
        //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
        // Junzo SATO
        else if (tr.isDataFlavorSupported(ImageSelection.imageFlavor)) {
            flavor = 2;
        }
        //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
        else {
            return false;
        }
                
        boolean ret = false;
        
        switch (flavor) {
        	
            case 0: 
                ModuleInfo stampInfo = null;

                try {
                    StampTreeNode node = (StampTreeNode)tr.getTransferData(SUPPORT_FLAVOR);
                    stampInfo = (ModuleInfo)node.getStampInfo();
                    ret = true;
                }
                catch (Exception e) {
                    System.out.println("Exception while getting the stampInfo: " + e.toString());
                    e.printStackTrace();
                    break;
                }
                
                // TextStamp
                String role = stampInfo.getRole();
                if (role.equals("text")) {
                    if (myRole.equals("soa")) {
                        stampInfoDropped(stampInfo);
                    }
                    else {
                        myPartner.stampInfoDropped(stampInfo);
                    }
                }
                // SOA / P
                else if ( myRole.equals(role) ) {
                    stampInfoDropped(stampInfo);
                }
                else {
                    myPartner.stampInfoDropped(stampInfo);
                }
                break;
                
            case 1:     
                if (myRole.equals("soa")) {
                    try {
                        SchemaList list = (SchemaList)tr.getTransferData(SchemaListTransferable.schemaListFlavor);
                        Schema s = list.schemaList[0];
                        int pos = viewToModel(loc);
                        dndSchema(s, pos);
                        
                    } catch (Exception sce) {
                        System.out.println("Exception while getting the Schema: " + sce.toString());
                        sce.printStackTrace();
                    }
                    
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
                ret = true;
                break;
            //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
            // Junzo SATO
            case 2:
                Image trImg = null;
                try {
                    trImg = (Image)tr.getTransferData(ImageSelection.imageFlavor);
                    ret = true;
                }
                catch (Exception e) {
                    System.out.println("Exception while getting the image: " + e.toString());
                    e.printStackTrace();
                    break;
                }
                
                if (myRole.equals("soa")) {
                    myInsertImage(trImg);
                }
                else {
                    myPartner.myInsertImage(trImg);
                }
                break;
            //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX         
        }
        return ret;
    }   
    
    private void startAnimation() {
        
        SwingUtilities.invokeLater(new Runnable() {
                    
            public void run() {
                ChartPlugin context = (ChartPlugin)parent.context;
                StatusPanel sp = context.getStatusPanel();
                sp.start("スタンプを取得しています...");
            }
        });
    }
    
    private void stopAnimation() {
        
        SwingUtilities.invokeLater(new Runnable() {
                    
            public void run() {
                ChartPlugin context = (ChartPlugin)parent.context;
                StatusPanel sp = context.getStatusPanel();
                sp.stop("");                    
            }
        });
    }    
    
    /*
     * 永続化されているスタンプを取得してこのペインに展開する。
     */
    private void applySerializedStamp(final ModuleInfo stampInfo) {
        
        startAnimation();

        String rdn = stampInfo.getStampId();
        IInfoModel model = null;
        if (stampInfo.isASP()) {
            AspStampModelDao dao = (AspStampModelDao)StampDaoFactory.createAspDao(KartePane.this, "dao.aspStampModel");
            model = (IInfoModel)dao.get(rdn);
            
        } else { 
        	// Database から Stamp Model を取得する
            SqlStampDao dao = (SqlStampDao)SqlDaoFactory.create(KartePane.this, "dao.stamp");
            String category = stampInfo.getEntity();
            String userId = Project.getUserId();
            model = (IInfoModel)dao.getStamp(userId, category, rdn);
        }

        if (model != null) {
            final Module stamp = new Module();
            stamp.setModel(model);
            stamp.setModuleInfo(stampInfo);
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    stamp(stamp);
                }
            });
        }

        stopAnimation();
    }
    
    /*
     * TextStamp をこのペインに挿入する。
     */
    private void applyTextStamp(final ModuleInfo stampInfo) {
        
        startAnimation();

        String rdn = stampInfo.getStampId();
        IInfoModel model = null;
        
        if (stampInfo.isASP()) {
            AspStampModelDao dao = (AspStampModelDao)StampDaoFactory.createAspDao(KartePane.this, "dao.aspStampModel");
            model = (IInfoModel)dao.get(rdn);
        
        } else {
            SqlStampDao dao = (SqlStampDao)SqlDaoFactory.create(KartePane.this, "dao.stamp");
            String category = stampInfo.getEntity();
            String userId = Project.getUserId();
            model = (IInfoModel)dao.getStamp(userId, category, rdn);
        }

        if (model != null) {
            final String str = model.toString();       // toString()!!
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    insertTextStamp(str);
                }
            });
        }

        stopAnimation();
    }    
    
    /*
     * StampInfo が Drop された時の処理を行なう。
     */    
    private void stampInfoDropped(ModuleInfo stampInfo) {
        
        final String entity = stampInfo.getEntity();
        
        // 病名の場合は２号カルテペインには展開しない
        if (entity.equals("diagnosis")) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        
        // Text スタンプを処理する
        if (entity.equals("text")) {
            applyTextStamp(stampInfo);
            return;
        }
        
        // データベースに保存されているスタンプを処理する
        if (stampInfo.isSerialized()) {
            applySerializedStamp(stampInfo);
            return;
        }
        
        // StampEditor を起動する
        try{
            stampEditor = new StampEditorDialog(entity);
            // Stamp Model の受け手をこのペインに設定する
            stampEditor.addPropertyChangeListener("value", KartePane.this);
            
            // Stamp object を生成しエディタをセットする
            Module stamp = new Module();
            stamp.setModuleInfo(stampInfo);
            stampEditor.setValue(stamp);
            
        } catch (Exception e) {
        	System.out.println("Exception while opening the stamp editor: " + e.toString());
        	e.printStackTrace();
            stampEditor = null;
        }

        if (stampEditor == null) {
            System.out.println("Can not get the StampEditor: " + entity);
            return;
        }

		// Event dispatch から起動
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                stampEditor.run();
            }
        });
    }    
        
    /*
     * Schema が DnD された場合、シェーマエディタを開いて編集する。
     */    
    public void myInsertImage(Image trImg) {
        try {
             //open schema editor
             //editSchemaImage(trImg);
            
            ImageIcon org = new ImageIcon(trImg);
            Schema schema = new Schema();
            schema.setIcon(org);
            
            // IInfoModel として ExtRef を保持している
            ExtRef ref = new ExtRef();
            ref.setContentType("image/jpeg");
            ref.setTitle("Schema Image");
            schema.setModel(ref);
            
            String fileName = docId + "-" + stampId + ".jpg";
            schema.setFileName(fileName);
            ref.setHref(fileName);
            
            final SchemaEditorDialog dlg = new SchemaEditorDialog((Frame)null, true, schema, true);
            dlg.addPropertyChangeListener(KartePane.this);
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    dlg.run();
                }
           });
        }
        catch (Exception e) {
			System.out.println("Exception while opening the schema editor: " + e.toString());
            e.printStackTrace();
        }
   }   
    
    /*
     * StampEditor の編集が終了するとここへ通知される。
     */
    public void propertyChange(PropertyChangeEvent e) {
        
        String prop = e.getPropertyName();
        
        if (prop.equals("imageProp")) {
            
            Schema schema = (Schema)e.getNewValue();
            
            if (schema == null) {
                return;
            }
            
            // 編集されたシェーマをこのペインに挿入する
            //SchemaHolder sh = new SchemaHolder(this, stampId++, schema);
            //sh.addMouseListener(selectionController);
            //((KarteStyledDocument)getDocument()).stampSchema(sh);
			stampSchema(schema);
        
        } else if (prop.equals("value")) {
            
            Object o = e.getNewValue();

            if (o == null) {
            	// Canceld
                return;
            }

			// 編集された Stamp をこのペインに挿入する
            Module stamp = (Module)o;
            stamp(stamp);
        }
    }
    
    private void dndSchema(Schema schema, int pos) {
        removeImage();
        SchemaHolder sh = new SchemaHolder(this, stampId++, schema);
        sh.addMouseListener(selectionController);
        ((KarteStyledDocument)getDocument()).dndSchema(sh, pos);
    }
    
    public void doCopy() {
     
        switch (getCurrentSelection()) {
            case TT_TEXT_SELECTION:
                this.copy();
                break;
                
            case TT_STAMP_SELECTION:
                copyStamp();
                break;
                
            case TT_IMAGE_SELECTION:
                copyImage();
                break;
        }
    }
    
    public void doCut() {
        
        switch (getCurrentSelection()) {
            case TT_TEXT_SELECTION:
                this.cut();
                break;
                
            case TT_STAMP_SELECTION:
                cutStamp();
                break;
                
            case TT_IMAGE_SELECTION:
                cutImage();
                break;
        }
    }
     
    public void doDelete() {
         
        switch (getCurrentSelection()) {
            case TT_TEXT_SELECTION:
                this.replaceSelection("");
                break;
                
            case TT_STAMP_SELECTION:
                removeStamp(); 
                break;
                
            case TT_IMAGE_SELECTION:
                removeImage();
                break;
        }
    }
    
    public void doPaste() {
        
        Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        
        if (t == null) {
            return;
        }

        if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            this.paste();
            return;
        }
                
        if ( t.isDataFlavorSupported(StampListTransferable.stampListFlavor) ||
             t.isDataFlavorSupported(OrderListTransferable.orderListFlavor) ) {
            pasteStamp();
            return;
        }
        
        if (t.isDataFlavorSupported(SchemaListTransferable.schemaListFlavor)) {
            pasteImage();
            return;
        }
    }    
    
    public boolean hasSelection() {    
        return (getCurrentSelection() != TT_NONE_SELECTION) ? true : false;
    }
    
    public boolean hasTextSelection() {    
        return (getCurrentSelection() != TT_TEXT_SELECTION) ? true : false;
    }    
    
    public boolean canPaste() {
        
        boolean ret = false;
        Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        if ( t == null ) {
            return false;
        }
        
        if ( t.isDataFlavorSupported(DataFlavor.stringFlavor) ) {
            return true;
        }
        
        if (myRole.equals("p")) {
            if (t.isDataFlavorSupported(OrderListTransferable.orderListFlavor)) {
                ret = true;
            }
        }
        else {
            if ( t.isDataFlavorSupported(StampListTransferable.stampListFlavor) || 
                 t.isDataFlavorSupported(SchemaListTransferable.schemaListFlavor) ) {
                ret = true;
            }
        }
        return ret;
    }    
    
    private void cutStamp() {
    
        Transferable t = getStampTrain();
        if (t != null) {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(t, null);
            removeStamp();
        }  
    } 
    
    private void cutImage() {
    
        Transferable t = getImageTrain();
        if (t != null) {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(t, null);
            removeImage();
        }  
    } 
    
    public boolean copyStamp() {
        Transferable t = getStampTrain();
        if (t != null) {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(t, null);
            return true;
        }
        else {
            return false;
        }
    }
    
    private void copyImage() {
        
        Transferable t = getImageTrain();
        if (t != null) {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(t, null);
        }
    }
        
    public Transferable getStampTrain() {
        
        if ( (stampSelectionList == null) || (stampSelectionList.size() == 0) ) {
            return null;
        }
        
        Transferable t = null;
        
        try {
            int size = stampSelectionList.size();
            
            Module[] copyList = new Module[size];
            for (int i = 0; i < size; i++) {
                StampHolder sh = (StampHolder)stampSelectionList.get(i);
                copyList[i] = sh.getStamp();             
            }
            
            if (myRole.equals("p")) {
                OrderList list = new OrderList();
                list.orderList = copyList;
                t = new OrderListTransferable(list);
            }
            else {
                StampList list = new StampList();
                list.setStampList(copyList);
                t = new StampListTransferable(list);
            }
        }
        catch (Exception e) {
            t = null;
        }
        return t;
    }    
    
    public Transferable getImageTrain() {
        
        if ( (imageSelectionList == null) || (imageSelectionList.size() == 0) ) {
            return null;
        }
        
        Transferable t = null;
        
        try {
            int size = imageSelectionList.size();
            Schema[] copyList = new Schema[size];
            for (int i = 0; i < size; i++) {
                SchemaHolder sh = (SchemaHolder)imageSelectionList.get(i);
                copyList[i] = sh.schema;
            }
            SchemaList list = new SchemaList();
            list.schemaList = copyList;
            t = new SchemaListTransferable(list);
        }
        catch (Exception e) {
            t = null;
        }
        return t;
    }     
    
    public void pasteStamp() {

        Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        if (t == null) {
            return;
        }

        try {
            Module[] list = null;
            if (myRole.equals("p")) {
                if (t.isDataFlavorSupported(OrderListTransferable.orderListFlavor)) {
                    OrderList o = (OrderList)t.getTransferData(OrderListTransferable.orderListFlavor);
                    list = o.orderList;
                }
            }
            else {
                if (t.isDataFlavorSupported(StampListTransferable.stampListFlavor)) {
                    StampList o = (StampList)t.getTransferData(StampListTransferable.stampListFlavor);
                    list = o.getStampList();
                }
            }
                
            if (list != null) {
                int len = list.length;
                for(int i = 0; i < len; i++) {
                    stamp((Module)list[i]);
                }
            }
        }
        catch (UnsupportedFlavorException e) {
        } 
        catch (IOException e) {
        }
    } 
    
    private void pasteImage() {
        
        Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        if (t == null) {
            return;
        }

        try {
            Schema[] list = null;
            if (t.isDataFlavorSupported(SchemaListTransferable.schemaListFlavor)) {
                SchemaList o = (SchemaList)t.getTransferData(SchemaListTransferable.schemaListFlavor);
                list = o.schemaList;
            }
                
            if (list != null) {
                int size = list.length;
                for(int i = 0; i < size; i++) {
                    stampSchema((Schema)list[i]);
                }
            }
        }
        catch (UnsupportedFlavorException e) {
        } 
        catch (IOException e) {
        }
    }     
    
    public void removeStamp() {
        int len = stampSelectionList.size();
        StampHolder ta;
        for (int i = len -1; i > -1; i--) {
            ta = (StampHolder)stampSelectionList.remove(i);
            ta.setSelected(false);
            int start = ta.getStartPos();
            int end = ta.getEndPos();
            ((KarteStyledDocument)getDocument()).removeStamp(start, 3); // TODO 3
        }
    }   
    
    private void removeImage() {
        int len = imageSelectionList.size();
        SchemaHolder ta;
        for (int i = len -1; i > -1; i--) {
            ta = (SchemaHolder)imageSelectionList.remove(i);
            ta.setSelected(false);
            int start = ta.getStartPos();
            int end = ta.getEndPos();
            ((KarteStyledDocument)getDocument()).removeStamp(start, 2);
        }
    }  
    
    public int getCurrentSelection() {
    
        int selection = TT_NONE_SELECTION;
        
        if ( (stampSelectionList != null) && (stampSelectionList.size() > 0)) {
            selection = TT_STAMP_SELECTION;
        }
        else if ( (imageSelectionList != null) && (imageSelectionList.size() > 0)) {
            selection = TT_IMAGE_SELECTION;
        }
        else if (getSelectionStart() != getSelectionEnd()) {
            selection = TT_TEXT_SELECTION;
        }
        return selection;
    }
    
    public void notifySelection() {
        SelectionEvent se = new SelectionEvent(this);
        mediator.selected(se);
    }
    
    public void clearAllSelection() {
        selectionController.clearAllSelection();
    }
    
    public void diSelectStamp() {
        selectionController.clearSelection(stampSelectionList);
        selectionController.clearSelection(imageSelectionList);
    }
    
    protected final class SelectionController extends MouseAdapter {
        
        public SelectionController() {
            super();
        }
        
        public void mousePressed(MouseEvent e) {
        	
			IComponentHolder ch = (IComponentHolder)e.getSource();
			
			boolean diselectOther = false;
    
			// スタンプ選択の場合
			if (ch.getContentType() == IComponentHolder.TT_STAMP) {
				if (stampSelectionList == null) {
					stampSelectionList = new ArrayList(10);
				}
        
				// 選択制御及び　テキストとイメージ選択をクリアするかどうか
				diselectOther = controlSelection(stampSelectionList, ch, e.isShiftDown());
        
				// 排他制御
				if (diselectOther) {
					clearTextSelection();
					clearSelection(imageSelectionList);
				}
			}
			else {
				// イメージ選択の場合
				if (imageSelectionList == null) {
					imageSelectionList = new ArrayList(10);
				}
        
				// 選択制御及び　テキストとスタンプ選択をクリアするかどうか
				diselectOther = controlSelection(imageSelectionList, ch, e.isShiftDown());
        
				// 排他制御
				if (diselectOther) {
					clearTextSelection();
					clearSelection(stampSelectionList);
				}
			}
    
			// 選択イベントを通知する
			notifySelection(); 
        }
        
        public void mouseClicked(MouseEvent e) {
        	
            IComponentHolder ch = (IComponentHolder)e.getSource();
            
            // ダブルクリックで編集
            if (e.getClickCount() == 2) {
                ch.edit(isEditable());
                return;
            }
            
            /*
            // シングルクリック
            boolean diselectOther = false;
            
            // スタンプ選択の場合
            if (ch.getContentType() == IComponentHolder.TT_STAMP) {
                if (stampSelectionList == null) {
                    stampSelectionList = new ArrayList(10);
                }
                
                // 選択制御及び　テキストとイメージ選択をクリアするかどうか
                diselectOther = controlSelection(stampSelectionList, ch, e.isShiftDown());
                
                // 排他制御
                if (diselectOther) {
                    clearTextSelection();
                    clearSelection(imageSelectionList);
                }
            }
            else {
                // イメージ選択の場合
                if (imageSelectionList == null) {
                    imageSelectionList = new ArrayList(10);
                }
                
                // 選択制御及び　テキストとスタンプ選択をクリアするかどうか
                diselectOther = controlSelection(imageSelectionList, ch, e.isShiftDown());
                
                // 排他制御
                if (diselectOther) {
                    clearTextSelection();
                    clearSelection(stampSelectionList);
                }
            }
            
            // 選択イベントを通知する
            notifySelection(); */
        }
        
        // Cursor 調整
        public void mouseEntered(MouseEvent e) {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
        public void mouseExited(MouseEvent e) {
            setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        }    
        
        // 選択のコントロール
        private boolean controlSelection(ArrayList list, IComponentHolder sh, boolean isShiftDown) {
            
            // 相手ペイン・スタンプとイメージ間の選択クリアフラグ
            boolean diselectOther = false;
            
            // 今選択リストが空であれば、他の選択はクリア
            if (list.size() == 0 ) {
                diselectOther = true;
            }

            if (isShiftDown) {
                
                if (sh.toggleSelection()) {
                    list.add(sh);
                }
                else {
                    int id = sh.getId();
                    int len = list.size();
                    int index;
                    IComponentHolder h = null;
                    for (index = 0; index < len; index++) {
                        h = (IComponentHolder)list.get(index);
                        if (h.getId() == id) {
                            break;
                        }
                    }
                    list.remove(index);
                }
            }
            else {
                int len = list.size();
                int id = sh.getId();
                IComponentHolder h = null;
                for (int i = len -1; i > -1; i--) {
                    h = (IComponentHolder)list.remove(i);
                    if (id != h.getId()) {
                        h.setSelected(false);
                    }
                }
                if (sh.toggleSelection()) {
                    list.add(sh);
                }
            }
            
            return diselectOther;
       }
              
       public void clearAllSelection() {
           clearTextSelection();
           clearSelection(stampSelectionList);
           clearSelection(imageSelectionList);
       }
       
       private void clearTextSelection() {
            if (getSelectionStart() != getSelectionEnd()) {
                int pos = getCaretPosition();
                select(pos,pos);
            }
       }
       
       private void clearSelection(ArrayList list) {
           if ((list != null) && (list.size() > 0 )) {
                int len = list.size();
                IComponentHolder sh;
                for (int i = len -1; i > -1 ; i--) {
                    sh = (IComponentHolder)list.remove(i);
                    sh.setSelected(false);
                }
           }
       }
    }
}