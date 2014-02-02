/*
 * NumberCellEditor.java
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
package open.dolphin.order;

import javax.swing.text.*;
import javax.swing.*;

import open.dolphin.table.*;

/**
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class NumberCellEditor extends IMECellEditor {
    
    private boolean hasPeriod = false;
    
    /** Creates a new instance of NumberCellEditor */
    public NumberCellEditor(final JTextField tf) {
       
        // ClickCountToStart = 1, IME = off
        super(tf, 1, false);
        
        tf.setDocument(new NumberDocument());
    }
    
    protected final class NumberDocument extends PlainDocument {
        
        public NumberDocument() {
        }
        
        public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
            
            /*int len = str.length();            
            
            for (int i = 0; i < len; i++) {
                
                 char ch = str.charAt(i);
                 
                if ( ch == '.' ) {
                    if (hasPeriod) {
                        return;
                    }
                    else {
                        hasPeriod = true;
                    }
                }
                else if (! Character.isDigit(ch) ) {
                    return;
                }

            }*/
            
            super.insertString(offset, str, a);
        }
        
        public void remove(int offset, int len) throws BadLocationException {
            
            /*String str = getText(offset, len);
            if (str.indexOf('.') >= 0) {
                hasPeriod = false;
            }*/
            super.remove(offset, len);
        }
    }
}