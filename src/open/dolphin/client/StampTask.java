/*
 * StampTask.java
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

import java.util.ArrayList;
import java.util.List;

import open.dolphin.delegater.StampDelegater;
import open.dolphin.infomodel.StampModel;
import open.dolphin.infomodel.StampTreeModel;

/**
 * StampTask
 *
 * @author Kazushi Minagawa
 */
public class StampTask extends AbstractInfiniteTask {
    
    private int mode;
    private ArrayList<StampModel> list;
    private StampTreeModel treeModel;
    private StampModel stampModel;
    private List<String> deleteList;
    private String stampId;
    private StampModel readStamp;
    private StampDelegater sdl;
    
    
    public StampTask(ArrayList<StampModel> list, StampDelegater sdl, int taskLength) {
        this.list = list;
        this.sdl = sdl;
        setTaskLength(taskLength);
        mode = 0;
    }
    
    public StampTask(StampModel stampModel, StampDelegater sdl, int taskLength) {
        this.stampModel = stampModel;
        this.sdl = sdl;
        setTaskLength(taskLength);
        mode = 1;
    }
    
    public StampTask(StampTreeModel treeModel, StampDelegater sdl, int taskLength) {
        this.treeModel = treeModel;
        this.sdl = sdl;
        setTaskLength(taskLength);
        mode = 2;
    }
    
    public StampTask(List<String> deleteList, StampDelegater sdl, int taskLength) {
        this.deleteList = deleteList;
        this.sdl = sdl;
        setTaskLength(taskLength);
        mode = 3;
    }
    
    public StampTask(String stampId, StampDelegater sdl, int taskLength) {
        this.stampId = stampId;
        this.sdl = sdl;
        setTaskLength(taskLength);
        mode = 4;
    }
    
    public StampModel getStampModel() {
        return readStamp;
    }
    
    
    protected void doTask() {
        
        if (mode == 0) {
            sdl.putStamp(list);
            
        } else if (mode == 1) {
            sdl.putStamp(stampModel);
            
        } else if (mode == 2) {
            sdl.putTree(treeModel);
            
        } else if (mode == 3) {
            sdl.removeStamp(deleteList);
            
        } else if (mode == 4) {
            readStamp = sdl.getStamp(stampId);
            
        } else {
            throw new RuntimeException("Illegal mode: " + mode);
        }
        setDone(true);
    }
}
