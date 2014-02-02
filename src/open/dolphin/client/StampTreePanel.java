/*
 * StampTreePanel.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
 * Copyright (C) 2003 Digital Globe, Inc. All rights reserved.
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

import java.awt.*;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import open.dolphin.infomodel.IInfoModel;

import open.dolphin.infomodel.ModuleInfoBean;

/**
 * StampTreePanel
 *
 * @author  Kazushi Minagawa
 */
public class StampTreePanel extends JPanel implements TreeSelectionListener {
    
    private static final long serialVersionUID = -268963413379453444L;
    
    protected StampTree stampTree;
    protected JTextArea infoArea;
    
    /** Creates a new instance of StampTreePanel */
    public StampTreePanel(StampTree tree) {
        
        this.stampTree = tree;
        JScrollPane scroller = new JScrollPane(stampTree);
        this.setLayout(new BorderLayout());
        this.add(scroller, BorderLayout.CENTER);
        
        String treeEntity = stampTree.getEntity();
        if (treeEntity != null && (!treeEntity.equals(IInfoModel.ENTITY_TEXT))) {
            infoArea = new JTextArea();
            infoArea.setMargin(new Insets(3,2,3,2));
            infoArea.setLineWrap(true);
            infoArea.setPreferredSize(new Dimension(250, 40));
            Font font = GUIFactory.createSmallFont();
            infoArea.setFont(font);
            this.add(infoArea, BorderLayout.SOUTH);
            tree.addTreeSelectionListener(this);
        }
    }
    
    /**
     * このパネルのStampTreeを返す。
     * @return StampTree
     */
    public StampTree getTree() {
        return stampTree;
    }
    
    /**
     * スタンプツリーで選択されたスタンプの情報を表示する。
     */
    public void valueChanged(TreeSelectionEvent e) {
        StampTree tree = (StampTree) e.getSource();
        StampTreeNode node =(StampTreeNode) tree.getLastSelectedPathComponent();
        if (node != null) {
            if (node.getUserObject() instanceof ModuleInfoBean) {
                ModuleInfoBean info = (ModuleInfoBean) node.getUserObject();
                infoArea.setText(info.getStampMemo());
            } else {
                infoArea.setText("");
            }
        } else {
            infoArea.setText("");
        }
    }
}
