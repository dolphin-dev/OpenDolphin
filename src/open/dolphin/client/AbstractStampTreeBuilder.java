/*
 * DefaultStampTreeBuilder.java
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

import java.util.List;

import open.dolphin.infomodel.IInfoModel;


/**
 * AbstractStampTreeBuilder
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public abstract class AbstractStampTreeBuilder {
    
    /** Creates new DefaultStampTreeBuilder */
    public AbstractStampTreeBuilder() {
    }
    
    public abstract List<StampTree> getProduct();
    
    public abstract void buildStart();
    
    public abstract void buildRoot(String name, String entity);
    
    public abstract void buildNode(String name);
    
    public abstract void buildStampInfo(String name, String role, String entity, String editable, String memo, String id);
    
    public abstract void buildNodeEnd();
    
    public abstract void buildRootEnd();
    
    public abstract void buildEnd();
    
    protected static String getEntity(String rootName) {
        
        String ret = null;
        
        if (rootName == null) {
            return ret;
        }
        
        for (int i = 0; i < IInfoModel.STAMP_ENTITIES.length; i++) {
            if (IInfoModel.STAMP_NAMES[i].equals(rootName)) {
                ret = IInfoModel.STAMP_ENTITIES[i];
                break;
            }
        }
        
        return ret;
    }
}