/*
 * DlAction.java
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

import javax.swing.*;
import java.awt.event.*;

/**
 *
 * @author  kazm
 */
public class DlAction extends AbstractAction {
    
    IRoutingTarget target;
    
    /** Creates a new instance of DlAction */
    public DlAction(String text, IRoutingTarget target) {
        super(text);
        this.target = target;
    }
    
    public DlAction(String text, Icon icon, IRoutingTarget target) {
        super(text, icon);
        this.target = target;
    }
    
    public void actionPerformed(ActionEvent e) {
        target.actionRouted((Action)this);
    }
}