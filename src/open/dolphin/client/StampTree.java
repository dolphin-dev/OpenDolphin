/*
 * StampTree.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
 * Copyright (C) 2001,2003,2004 Digital Globe, Inc. All rights reserved.
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
import javax.swing.tree.*;
import javax.swing.event.*;

import open.dolphin.dao.*;
import open.dolphin.exception.DolphinException;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.Module;
import open.dolphin.infomodel.ModuleInfo;
import open.dolphin.infomodel.RegisteredDiagnosisModule;
import open.dolphin.infomodel.TextStamp;
import open.dolphin.project.*;
import open.dolphin.util.*;

import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.rmi.server.*;

/**
 * StampTree 
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class StampTree extends JTree 
implements TreeModelListener, DragGestureListener, DropTargetListener, DragSourceListener {
                     
    private static final int TOOLTIP_LENGTH = 35;
    private static final ImageIcon ASP_ICON = new ImageIcon(open.dolphin.client.StampTree.class.getResource("/open/dolphin/resources/images/WebComponent16.gif"));
    private static final ImageIcon LOCAL_ICON = new ImageIcon(open.dolphin.client.StampTree.class.getResource("/open/dolphin/resources/images/Bean16.gif"));
    
    /** この Tree が編集可能かどうか */
    private boolean editable;
            
    private DragSource dragSource;
    
    /** Reference to the StampBox */
    private StampBoxService stampBox;
    
        
    /**
     * Tree model からこのクラスを生成する
     */
    public StampTree(TreeModel model) {
        
        super(model);

        this.putClientProperty("JTree.lineStyle", "Angled");           // 水平及び垂直線を使用する
        this.setEditable(false);                                       // ノード名を編集不可にする
        this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);  // Single Selection にする
        this.setRootVisible(false);
        
        // Replace the default CellRenderer
        final TreeCellRenderer oldRenderer = this.getCellRenderer();
        TreeCellRenderer r = new TreeCellRenderer() {
            
            public Component getTreeCellRendererComponent(JTree tree,
                Object value,
                boolean selected,
                boolean expanded,
                boolean leaf,
                int row,
                boolean hasFocus) {
                    
                    Component c = oldRenderer.getTreeCellRendererComponent(tree,
                        value,selected,expanded,leaf,row,hasFocus);
                    if (leaf && c instanceof JLabel) {
                        JLabel l = (JLabel)c;
                        Object o = ((StampTreeNode)value).getUserObject();
                        if (o instanceof ModuleInfo) {
                            
                            // 固有のアイコンを設定する              
                            if ( ((ModuleInfo)o).isASP() ) {
                                l.setIcon(ASP_ICON);
                            }
                            else {
                                l.setIcon(LOCAL_ICON);
                            }
                            
                            // ToolTips を設定する
                            l.setToolTipText(((ModuleInfo)o).getMemo());
                        }
                    }
                    return c;
            }
        };
        this.setCellRenderer(r);
            
        // Listens TreeModelEvent
        model.addTreeModelListener(this);
        
        // Enable ToolTips
        enableToolTips(true);
        
        // DragEnabled
        dragSource = new DragSource();
        dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, this);
        
        // Make tree dropTarget
        new DropTarget(this, this);
    }
    
    public boolean isEdiatble() {
    	return editable;
    }
    
    public void setEdiatble(boolean b) {
    	editable = b;
    }
    
    /**
     * Enable or disable tooltip
     */
    public void enableToolTips(boolean state) {
    
        ToolTipManager mgr = ToolTipManager.sharedInstance();
        if (state) {
            // Enable tooltips
            mgr.registerComponent(this);
        
        } else {
            mgr.unregisterComponent(this);
        }
    }
    
    /**
     * Set StampBox reference
     */ 
    public void setStampBox(StampBoxService ref) {
        stampBox = ref;
    }
    
    /**
     * RootNode の名前を返す
     */
    public String getRootName() {
        StampTreeNode node = (StampTreeNode)this.getModel().getRoot();
        return node.toString();
    }
    
    /**
     * 選択されているノードを返す
     */
    protected StampTreeNode getSelectedNode() {
        return (StampTreeNode) this.getLastSelectedPathComponent();   
    }
    
    /**
     * Drop 位置のノードを返す
     */
    protected StampTreeNode getNode(Point p) {
        TreePath path = this.getPathForLocation(p.x, p.y);
        return (path != null) ? (StampTreeNode)path.getLastPathComponent() : null;
    }
    
    //////////////   Drag Support //////////////////
    
    public void dragGestureRecognized(DragGestureEvent event) {
        
        StampTreeNode dragNode = getSelectedNode();
        
        if (dragNode == null) {
            return;
        }
        
		dragNode.setTreeId(getRootName());
        
        Transferable t = new StampTreeTransferable(dragNode);
        Cursor cursor = DragSource.DefaultCopyDrop;

        //begin the drag
        dragSource.startDrag(event, cursor, t, this);
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
    
    //////////// Drop Support ////////////////
        
    public void drop(DropTargetDropEvent e) {
        
        if (! isDropAcceptable(e)) {
            e.rejectDrop();
            setDropTargetBorder(false);
            return;
        }
        
        // Transferable を取得する
        final Transferable tr = e.getTransferable();

        // Drop 位置を得る
        final Point loc = e.getLocation();
        
        // Force copy
        e.acceptDrop(DnDConstants.ACTION_COPY);
        e.getDropTargetContext().dropComplete(true);
        setDropTargetBorder(false);
            
        boolean ok = doDrop(tr, loc);
    }
    
    public boolean isDragAcceptable(DropTargetDragEvent evt) {
    	if (!editable) {
    		return false;
    	}
        return (evt.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) != 0;
    }
    
    public boolean isDropAcceptable(DropTargetDropEvent evt) {
		if (!editable) {
			return false;
		}
        return (evt.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) != 0;
    }        

    /** DropTaregetListener interface method */
    public void dragEnter(DropTargetDragEvent e) {
        if (! isDragAcceptable(e)) {
            e.rejectDrag();
        }
    }

    /** DropTaregetListener interface method */
    public void dragExit(DropTargetEvent e) {
        setDropTargetBorder(false);
    }

    /** DropTaregetListener interface method */
    public void dragOver(DropTargetDragEvent e) { 
        if (isDragAcceptable(e)) {
            setDropTargetBorder(true);
        }
    }

    /** DropTaregetListener interface method */
    public void dropActionChanged(DropTargetDragEvent e) {
        if (! isDragAcceptable(e)) {
            e.rejectDrag();
        }
    }
    
    private void setDropTargetBorder(final boolean b) {
        Color c = b ? DesignFactory.getDropOkColor() : this.getBackground();
        this.setBorder(BorderFactory.createLineBorder(c, 2));
    }
    
    /**
     * ノードを Drop する
     */
    protected boolean doDrop(Transferable tr, Point loc) {
                     
        int state = -1;
        
        // StampTreeNode
        if (tr.isDataFlavorSupported(StampTreeTransferable.stampTreeNodeFlavor)) {
            state = 0;
            
        // OrderList    
        } else if (tr.isDataFlavorSupported(OrderListTransferable.orderListFlavor)) {
            state = 1;
            
        // Text    
        } else if (tr.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            state = 2;
        
        // InforModel    
        } else if (tr.isDataFlavorSupported(InfoModelTransferable.infoModelFlavor)) {
            state = 3;
            
        } else {
            return false;
        }
        
        boolean ret = false;
        
        switch (state) {
            
            case 0:
                // TreeNode dropped
                try {
                	StampTreeNode dropNode = (StampTreeNode)tr.getTransferData(StampTreeTransferable.stampTreeNodeFlavor);
					if (dropNode.getTreeId().equals(getRootName())) {
						ret = moveNode(dropNode, loc);
					} else {
						ModuleInfo info =(ModuleInfo)dropNode.getUserObject();
						info.setEditable(true);
						ret = addNode(dropNode, loc);
					}
                } catch (Exception ufe) {
                	debug(ufe.toString());
                }  
                break;
                
            case 1:
                // Stamp dropped
                if (editable) {
                    ret = addStamp(tr, loc);
                
                }
                break;
                
            case 2:
                // Text dropped
                if (editable) {
                    ret = addTextStamp(tr, loc);
               
                }
                break;
                
            case 3:
                // RegisteredDiagnosis
                if (editable) {
                	ret = addDiagnosis(tr, loc);
                }
                break;
        }
        
        return ret;
    }
    
    /**
     * TreeNode の移動を行う。
     */
    protected boolean moveNode(StampTreeNode dropNode, Point loc) {  

        // Drop 位置のノードを取得
        StampTreeNode destinationNode = getNode(loc);
        
        if (destinationNode == null) {
            //それがヌルの場合はリターン
            return false;
            
        } else if (destinationNode == getSelectedNode()) {
            // Drag Node に同じの場合はリターン
            return false;
        }
        
        boolean ret = false;
        
        // Drop(Drg)Node の 元（旧）の親
        StampTreeNode orgNode = getSelectedNode();
        StampTreeNode oldParent = (StampTreeNode)orgNode.getParent();

        // Move Action の場合は旧の親からノードを切り離す
        orgNode.removeFromParent();

        /**
         * Drop 位置がフォルダであれば dropNode をそのフォルダに add
         * Drop 位置が葉ノードであれば dropNode をその親に insert
         */
        StampTreeNode newParent = null;

        if (! destinationNode.isLeaf()) {
            newParent = destinationNode;
            newParent.add(dropNode);
            
        } else {
            // 親を得る
            newParent = (StampTreeNode)destinationNode.getParent();

            // Drop 位置のインデックスを得る
            int index = newParent.getIndex(destinationNode);

            // そこへ挿入する
            newParent.insert(dropNode, index);
        }

        //expand nodes appropriately - this probably isnt the best way...
        DefaultTreeModel model = (DefaultTreeModel)this.getModel();
        model.reload(oldParent);
        model.reload(newParent);
        TreePath parentPath = new TreePath(newParent.getPath());
        this.expandPath(parentPath);
        parentPath = new TreePath(oldParent.getPath());
        this.expandPath(parentPath);

        // ここまで来たら成功
        ret = true;
        
        return ret;
    }
    
    /**
     * 他の StampTree から Drop された　TreeNode を自分に加える。
     */
	private boolean addNode(StampTreeNode dropNode, Point loc) {  

		// Drop 位置のノードを取得
		StampTreeNode destinationNode = getNode(loc);
        
		if (destinationNode == null) {
			//それがヌルの場合は root 直下に追加する
			destinationNode = (StampTreeNode)this.getModel().getRoot();
		}
        
		boolean ret = false;
        
		/**
		 * Drop 位置がフォルダであれば dropNode をそのフォルダに add
		 * Drop 位置が葉ノードであれば dropNode をその親に insert
		 */
		StampTreeNode newParent = null;

		if (! destinationNode.isLeaf()) {
			newParent = destinationNode;
			newParent.add(dropNode);
            
		} else {
			// 親を得る
			newParent = (StampTreeNode)destinationNode.getParent();

			// Drop 位置のインデックスを得る
			int index = newParent.getIndex(destinationNode);

			// そこへ挿入する
			newParent.insert(dropNode, index);
		}

		//expand nodes appropriately - this probably isnt the best way...
		DefaultTreeModel model = (DefaultTreeModel)this.getModel();
		model.reload(newParent);
		TreePath parentPath = new TreePath(newParent.getPath());
		this.expandPath(parentPath);

		// ここまで来たら成功
		ret = true;
        
		return ret;
	}    
    
    /**
     * KartePane から　Drag & Drop されたスタンプを永続化する
     */
    protected boolean addStamp(Transferable tr, Point loc) {
        
        boolean ret = false;
        
        try {
            // スタンプは PTrainTrasnferable
            OrderList list = (OrderList)tr.getTransferData(OrderListTransferable.orderListFlavor);
            final Module stamp = list.orderList[0];
            ModuleInfo org = stamp.getModuleInfo();
            
            // データベースへ Stamp のデータモデルを永続化する
            String stampId = Project.createUUID();
            String userId = Project.getUserId();
            String category = org.getEntity();
            
            final SqlStampDao dao = (SqlStampDao)SqlDaoFactory.create(this, "dao.stamp");
            
            if (! dao.addStamp(userId, category, stampId, (IInfoModel)stamp.getModel())) {
                        
                throw new DolphinException("Unable to save the stamp");
            }
            
            // 新しい StampInfo を生成する
            ModuleInfo info = new ModuleInfo();
            //info.setName(org.getName() + "-copy");        // オリジナル名-copy
            info.setName(org.getName());                    // オリジナル名-copy
            info.setEntity(org.getEntity());  				// Entity
            info.setRole(org.getRole());                    // Role
            info.setMemo(constractToolTip(stamp));          // Tooltip                        
            info.setStampId(stampId);                       // Stamp ID
                       
            // StampInfo から新しい StampTreeNode を生成する
            StampTreeNode node = new StampTreeNode(info);
            
            // それをターゲットの StampTreeに加える
            // 格納する StampTree を取得する
            final StampTree theTree = stampBox.getStampTree(category);
            boolean notMe = (theTree != this) ? true : false;
            DefaultTreeModel model = (DefaultTreeModel)theTree.getModel();
            StampTreeNode root = (StampTreeNode)model.getRoot();
            root.add(node);
            model.reload(root);            
            
            // メッセージを表示する
            if (notMe) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        StringBuffer buf = new StringBuffer();
                        buf.append("スタンプは ");
                        buf.append(theTree.getRootName());
                        buf.append(" に格納しました。");
                        JOptionPane.showMessageDialog(null,
                                             buf.toString(),
                                             "Dolphin: スタンプ登録",
                                             JOptionPane.INFORMATION_MESSAGE);
                    }
                });
            }
            
            ret = true;
            
        } catch (IOException ie) {
            //ClientContext.getLogger().warning("IOException at addStamp: " + ie.toString());
			debug(ie.toString());
        
        } catch (UnsupportedFlavorException ue) {
            //ClientContext.getLogger().warning("UnsupportedFlavorException at addStamp: " + ue.toString());
			debug(ue.toString());
        
        } catch (DolphinException de) {
            //ClientContext.getLogger().warning("DolphinException at addStamp: " + de.toString());
			debug(de.toString());
        }
        
        return ret;
    }
    
    /**
     * Diagnosis Table から　Drag & Drop されたRegisteredDiagnosisをスタンプ化する
     */
    protected boolean addDiagnosis(Transferable tr, Point loc) {
        
        boolean ret = false;
        
        try {
            // スタンプは InfoModelTrasnferable
            RegisteredDiagnosisModule rd = (RegisteredDiagnosisModule)tr.getTransferData(InfoModelTransferable.infoModelFlavor);
            
            // 日付をクリア
            rd.setFirstEncounterDate(null);
            rd.setEndDate(null);
            
            final Module stamp = new Module();
            stamp.setModel(rd);
            
            // データベースへ Stamp のデータモデルを永続化する
            String stampId = Project.createUUID();
            String userId = Project.getUserId();
            String category = "diagnosis";
            
            final SqlStampDao dao = (SqlStampDao)SqlDaoFactory.create(this, "dao.stamp");
            
            if (! dao.addStamp(userId, category, stampId, (IInfoModel)stamp.getModel())) {
                        
                throw new DolphinException("Unable to save the stamp");
            }
            
            // 新しい StampInfo を生成する
            ModuleInfo info = new ModuleInfo();
            info.setName(rd.getDiagnosis());                // 傷病名
            info.setEntity(category);                		// カテゴリ
            info.setRole("diagnosis");                      // Role
            
            StringBuffer buf = new StringBuffer();
            buf.append(rd.getDiagnosis());
            String cd = rd.getDiagnosisCode();
            if (cd != null) {
                buf.append("(");
                buf.append(cd);
                buf.append(")");   // Tooltip  
            } 
            info.setMemo(buf.toString());
            info.setStampId(stampId);                       // Stamp ID
                       
            // StampInfo から新しい StampTreeNode を生成する
            StampTreeNode node = new StampTreeNode(info);
            
            // それをターゲットの StampTreeに加える
            // 格納する StampTree を取得する
            final StampTree theTree = stampBox.getStampTree(category);
            boolean notMe = (theTree != this) ? true : false;
            DefaultTreeModel model = (DefaultTreeModel)theTree.getModel();
            StampTreeNode root = (StampTreeNode)model.getRoot();
            root.add(node);
            model.reload(root);            
            
            // メッセージを表示する
            if (notMe) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        StringBuffer buf = new StringBuffer();
                        buf.append("スタンプは ");
                        buf.append(theTree.getRootName());
                        buf.append(" に格納しました。");
                        JOptionPane.showMessageDialog(null,
                                             buf.toString(),
                                             "Dolphin: スタンプ登録",
                                             JOptionPane.INFORMATION_MESSAGE);
                    }
                });
            }
            
            ret = true;
            
        } catch (IOException ie) {
            //ClientContext.getLogger().warning("IOException at addStamp: " + ie.toString());
			debug(ie.toString());
        
        } catch (UnsupportedFlavorException ue) {
            //ClientContext.getLogger().warning("UnsupportedFlavorException at addStamp: " + ue.toString());
			debug(ue.toString());
        
        } catch (DolphinException de) {
            //ClientContext.getLogger().warning("DolphinException at addStamp: " + de.toString());
			debug(de.toString());
        }
        
        return ret;
    }    
    
    /**
     * テキストスタンプを登録
     */
    protected boolean addTextStamp(Transferable tr, Point loc) {
        
        boolean ret = false;
        
        try {
            // スタンプは PTrainTrasnferable
            String text = (String)tr.getTransferData(DataFlavor.stringFlavor);
            
            final TextStamp stamp = new TextStamp();
            stamp.setText(text);
            //DolphinContext.getLogger().warning(stamp.toString());
            
            // データベースへ Stamp のデータモデルを永続化する
            String stampId = Project.createUUID();
            String userId = Project.getUserId();
            
            final SqlStampDao dao = (SqlStampDao)SqlDaoFactory.create(this, "dao.stamp");
            
            if (! dao.addStamp(userId, "text", stampId, (IInfoModel)stamp)) {
                throw new DolphinException("Unable to save the stamp");
            }
            
            // 新しい StampInfo を生成する
            ModuleInfo info = new ModuleInfo();
            int len = text.length() > 16 ? 16 : text.length();
            String name = text.substring(0, len);
            len = name.indexOf("\n");
            if (len > 0 ) {
                name = name.substring(0, len);
            }
            info.setName(name);                             // 
            info.setEntity("text");                  		// カテゴリ
            info.setRole("text");                           // Role
            info.setMemo(text);                             // Tooltip                        
            info.setStampId(stampId);                       // Stamp ID
                       
            // StampInfo から新しい StampTreeNode を生成する
            StampTreeNode node = new StampTreeNode(info);
            
            // それをターゲットの StampTreeに加える
            // 格納する StampTree を取得する
            String category = info.getEntity();
            final StampTree theTree = stampBox.getStampTree(category);
            boolean notMe = (theTree != this) ? true : false;
            DefaultTreeModel model = (DefaultTreeModel)theTree.getModel();
            StampTreeNode root = (StampTreeNode)model.getRoot();
            root.add(node);
            model.reload(root);            
            
            // メッセージを表示する
            if (notMe) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        StringBuffer buf = new StringBuffer();
                        buf.append("スタンプは ");
                        buf.append(theTree.getRootName());
                        buf.append(" に格納しました。");
                        JOptionPane.showMessageDialog(null,
                                             buf.toString(),
                                             "Dolphin: スタンプ登録",
                                             JOptionPane.INFORMATION_MESSAGE);
                    }
                });
            }
            
            ret = true;
            
        } catch (IOException ie) {
            //ClientContext.getLogger().warning("IOException at addStamp: " + ie.toString());
			debug(ie.toString());
        
        } catch (UnsupportedFlavorException ue) {
            //ClientContext.getLogger().warning("UnsupportedFlavorException at addStamp: " + ue.toString());
			debug(ue.toString());
        
        } catch (DolphinException de) {
            //ClientContext.getLogger().warning("DolphinException at addStamp: " + de.toString());
			debug(de.toString());
        }
        return ret;
    }    
    
    /**
     * ASP Stamp として保存する。ASP_TOOLモードの時のみ動作する。
     */
    protected boolean addAspStamp(Transferable tr, Point loc) {
        
        boolean ret = false;
        
        try {
            OrderList list = (OrderList)tr.getTransferData(OrderListTransferable.orderListFlavor);
            final Module stamp = list.orderList[0];
            ModuleInfo org = stamp.getModuleInfo();
            
            // データベースへ Stamp のデータモデルを永続化する
            UID docId = new UID();
            final String uid = docId.toString();            
            final AspStampModelDao dao = (AspStampModelDao)StampDaoFactory.createAspDao(this, "dao.aspStampModel");
            
            Runnable r = new Runnable() {
                public void run() {
                    if (! dao.save(uid, (IInfoModel)stamp.getModel())) {
                        //ClientContext.getLogger().warning("Failed to save the stamp");
						debug("Failed to save the stamp");
                    }
                }
            };
            Thread t = new Thread(r);
            t.start();
            
            // 新しい StampInfo を生成する
            ModuleInfo info = new ModuleInfo();
            info.setName(org.getName() + "-asp");           // オリジナル名-copy
            info.setEntity(org.getEntity());  // カテゴリ
            info.setRole(org.getRole());                    // Role
            info.setMemo(constractToolTip(stamp));          // Tooltip                        
            info.setStampId(uid);                           // Stamp ID
            info.setASP(true);                              // ASP stamp
                       
            // StampInfo から新しい StampTreeNode を生成する
            StampTreeNode node = new StampTreeNode(info);
            
            DefaultTreeModel model = (DefaultTreeModel)this.getModel();
            StampTreeNode root = (StampTreeNode)model.getRoot();
            root.add(node);
            model.reload(root);
            
            ret = true;
            
        } catch (IOException ie) {
            //ClientContext.getLogger().warning("IOException at addStamp: " + ie.toString());
			debug(ie.toString());
        
        } catch (UnsupportedFlavorException ue) {
            //ClientContext.getLogger().warning("UnsupportedFlavorException at addStamp: " + ue.toString());
			debug(ue.toString());
        }
        
        return ret;
    }    
    
    protected String constractToolTip(Module stamp) {
        
        String ret = null;
        
        try {
            StringBuffer buf = new StringBuffer();
            BufferedReader reader = new BufferedReader(new StringReader(stamp.getModel().toString()));
            String line;
            while(true) {
                line = reader.readLine();
                
                if (line == null) {
                    break;
                }
                
                buf.append(line);
                
                if (buf.length() < TOOLTIP_LENGTH) {
                    buf.append(",");
                }
                else {
                    break;
                }
            }
            reader.close();
            if (buf.length() > TOOLTIP_LENGTH ) {
                buf.setLength(TOOLTIP_LENGTH);
            }
            buf.append("...");
            ret = buf.toString();
        
        } catch(IOException e) {
            //ClientContext.getLogger().warning(e.toString());
			debug(e.toString());
        }
        
        return ret;
    }

    //////////////  PopupMenu サポート //////////////
    
    /**
     * ノードの名前を変更する
     */
    public void renameNode () {
    	
    	if (!editable) {
    		return;
    	}

        // Root へのパスを取得する
        StampTreeNode node = getSelectedNode();
        TreeNode[] nodes = node.getPath();
        TreePath path = new TreePath(nodes);
        
        // 編集を開始する
        this.setEditable(true); 
        this.startEditingAtPath(path);
        //this.setEditable (false); は TreeModelListener で行う
    }
    
    /**
     * ノードを削除する
     */
    public void deleteNode () {
    	
		if (!editable) {
			return;
		}
       
        // Gets the target node
        StampTreeNode theNode = getSelectedNode();
        
        // Removes template editors contained by the target node
        Enumeration e = theNode.preorderEnumeration();
        
        while(e.hasMoreElements()) {
            StampTreeNode node = (StampTreeNode)e.nextElement();
            if (node.isLeaf()) {
            	
                ModuleInfo info = (ModuleInfo)node.getUserObject();
                
                //String treeId = node.getTreeId();
                //boolean myNode = (treeId != null && treeId.equals(this.getId())) ? true : false;
                String stampId = info.getStampId();
                String category = info.getEntity();
                String userId = Project.getUserId();
                
                // 永続化されているモデルを削除する
                if (editable) {
                    
                    SqlStampDao dao = (SqlStampDao)SqlDaoFactory.create(this, "dao.stamp");
                    
                    boolean result = dao.removeStamp(userId, category, stampId);
                }
                //else if (mode == ASP_TOOL) {
                    //AspStampModelDao dao = (AspStampModelDao)StampDaoFactory.createAspDao(this, "dao.aspStampModel");
                    //dao.remove(stampId);
                //}
            }
        }
        // Removes from parent
        StampTreeNode parent = (StampTreeNode) theNode.getParent();
        parent.remove(theNode);
            
        // Tells the model
        DefaultTreeModel model = (DefaultTreeModel)getModel();
        model.reload(parent);
    }
    
    /**
     * 新規のフォルダノードを追加する
     */
    public void createNewFolder () {
    	
		if (!editable) {
			return;
		}
       
        // 選択されたノードを得る
        StampTreeNode node = getSelectedNode();
        StampTreeNode parent = null;

        // ノードが葉なら親へ追加し、フォルダなら自分へ追加する
        parent = (node.isLeaf () == true) ? (StampTreeNode) node.getParent() : node;

        // 新規ノードを生成し親へ追加する
        StampTreeNode newChild = new StampTreeNode("新規フォルダ");
        parent.add (newChild);
        
        // モデルへ通知する
        DefaultTreeModel model = (DefaultTreeModel) getModel();
        model.reload(parent);
        
        TreePath parentPath = new TreePath(parent.getPath());
        expandPath(parentPath);
    }
    
    ///////////// TreeModelListener ////////////////
    
    public void treeNodesChanged(TreeModelEvent e) {        
        this.setEditable(false);
    }
    
    public void treeNodesInserted(TreeModelEvent e) {        
    }
 
    public void treeNodesRemoved(TreeModelEvent e) {       
    }
    
    public void treeStructureChanged(TreeModelEvent e) {        
    }
    
	private void debug(String msg) {
		if (ClientContext.isDebug()) {
			System.out.println(msg);
		}
	}
}