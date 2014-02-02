/*
 * SaveStampTreeTask.java
 * Copyright (C) 2005 Digital Globe, Inc. All rights reserved.
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

import open.dolphin.delegater.StampDelegater;
import open.dolphin.infomodel.IStampTreeModel;
import open.dolphin.plugin.helper.AbstractLongTask;

/**
 * SaveStampTreeTask
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class SaveStampTreeTask extends AbstractLongTask {
    
    private IStampTreeModel treeModel;
    private StampDelegater sdl;
    private String[] messages = new String[]{
        "スタンプツリーを保存しています...",
        "スタンプツリーを保存しました",
        "スタンプツリーの保存ができません"
    };
    
    public SaveStampTreeTask(IStampTreeModel treeModel, StampDelegater sdl) {
        this.treeModel = treeModel;
        this.sdl = sdl;
    }
    
    public void run() {
        setMessage(messages[0]);
        long putCode = sdl.putTree(treeModel);
        if (sdl.isNoError()) {
            setResult(true);
            setMessage(messages[1]);
        } else {
            setResult(false);
            setMessage(messages[2] + "(" + putCode + ")。");
        }
    }
}
