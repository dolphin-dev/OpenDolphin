/*
 * TextAreaTracer.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
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
package open.dolphin.server;

import javax.swing.*;

/**
 * 
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class TextAreaTracer extends JTextArea implements Trace {
    
    private boolean debug;

    /** Creates new TextAreaTracer */
    public TextAreaTracer() {
    }

    public void clear() {
        this.setText("");
    }
    
    public void error(String message) {
        this.append("ERROR: " + message );
        this.append("\n");
    }
    
    public void setDebug(boolean debug) {
        this.debug = debug;
    }
    
    public void debug(String message) {
        if( debug ) {  // only print if debug is true
            this.append("DEBUG: " + message );
            this.append("\n");
        }
    }
}
