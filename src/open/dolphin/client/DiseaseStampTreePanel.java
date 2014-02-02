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


/**
 * StampTreePanel
 *
 * @author  Kazushi Minagawa
 */
public class DiseaseStampTreePanel extends StampTreePanel {
    
	private static final long serialVersionUID = -6382383999375891508L;

	/** Creates a new instance of StampTreePanel */
    public DiseaseStampTreePanel(StampTree tree) {
    	super(tree);
    }
    
    /**
     * スタンプエディタをオープンしスタンプを作成する。
     */
    /*protected void openEditor() {
		String treeEntity = stampTree.getEntity();
		StampEditorDialog stampEditor = new StampEditorDialog(treeEntity, null, false);
		stampEditor.addPropertyChangeListener(StampEditorDialog.VALUE_PROP, this);
		stampEditor.start();
    }*/
    
    /**
     * スタンプエディタで作成したスタンプをこのツリーに保存する。
     */
    /*@SuppressWarnings("unchecked")
	public void propertyChange(PropertyChangeEvent e) {
		ArrayList list = (ArrayList) e.getNewValue();
		if (list == null || list.size() == 0) {
			return;
		}
		stampTree.addDiagnosis(list);
    }*/
}
