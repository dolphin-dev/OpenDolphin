/*
 * AspStampBox.java
 * Copyright (C) 2006 Digital Globe, Inc. All rights reserved.
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

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;

/**
 * AspStampBox
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class AspStampBox extends AbstractStampBox {
    
    private static final long serialVersionUID = 342707175199862030L;

	/** Creates new StampBoxPlugin */
    public AspStampBox() {
    }
    
    protected void buildStampBox() {
        
        try {
            // Build stampTree
            BufferedReader reader = new BufferedReader(new StringReader(stampTreeModel.getTreeXml()));
            ASpStampTreeBuilder builder = new ASpStampTreeBuilder();
            StampTreeDirector director = new StampTreeDirector(builder);
            List<StampTree> aspTrees = director.build(reader);
            reader.close();
            stampTreeModel.setTreeXml(null);
            
            // StampTreeに設定するポップアップメニューとトランスファーハンドラーを生成する
            //AspStampTreePopupAdapter popAdapter = new AspStampTreePopupAdapter();
            AspStampTreeTransferHandler transferHandler = new AspStampTreeTransferHandler();
            
            // StampBox(TabbedPane) へリスト順に格納する
            for (StampTree stampTree : aspTrees) {
                //stampTree.addMouseListener(popAdapter);
                stampTree.setTransferHandler(transferHandler);
                stampTree.setAsp(true);
                stampTree.setStampBox(getContext());
                StampTreePanel treePanel = new StampTreePanel(stampTree);
                this.addTab(stampTree.getTreeName(), treePanel);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}