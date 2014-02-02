/*
 * StampLibraryTree.java
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
import javax.swing.tree.*;

import open.dolphin.infomodel.Module;
import open.dolphin.infomodel.ModuleInfo;

import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.*;
import java.io.*;

/**
 * StampLibrartyTree. 
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class StampLibraryTree extends JTree 
implements DragGestureListener,DragSourceListener {
                     
    private static final int TOOLTIP_LENGTH = 35;
    private static final ImageIcon ASP_ICON = new ImageIcon(open.dolphin.client.StampTree.class.getResource("/open/dolphin/resources/images/WebComponent16.gif"));
    private static final ImageIcon LOCAL_ICON = new ImageIcon(open.dolphin.client.StampTree.class.getResource("/open/dolphin/resources/images/Bean16.gif"));
                
    private DragSource dragSource;
            
    /**
     * Tree model からこのクラスを生成する
     */
    public StampLibraryTree(TreeModel model) {
        
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
                    
        // Enable ToolTips
        enableToolTips(true);
        
        // DragEnabled
        dragSource = new DragSource();
        dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, this);

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
        
    //////////////   Drag Support //////////////////
    
    public void dragGestureRecognized(DragGestureEvent event) {
        
        StampTreeNode dragNode = getSelectedNode();
        
        if (dragNode == null) {
            return;
        }
        
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
    
	protected void debug(String msg) {
		if (ClientContext.isDebug()) {
			System.out.println(msg);
		}
	}    
}