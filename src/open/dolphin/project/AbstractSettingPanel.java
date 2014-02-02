/*
 * SettingPanel.java
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
package open.dolphin.project;

import javax.swing.*;

/**
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public abstract class AbstractSettingPanel extends JPanel implements SettingVisitor {
    
    public static final int TT_GET = 0;
    public static final int TT_SET = 1;
        
    /** Creates a new instance of SettingPanel */
    public AbstractSettingPanel() {
    }
    
    public void visit(ProjectStub stub) {
        
        int mode = stub.getMode();
        
        switch(mode) {
            case TT_SET:
                setValues(stub);
                break;
                
            case TT_GET:
                getValues(stub);
                break;
                
            default:
                //assert false : mode;
        }
    }
        
    public boolean isOk() {
        return true;
    }
    
    protected abstract void getValues(ProjectStub stub);
    
    protected abstract void setValues(ProjectStub stub);
    
}
