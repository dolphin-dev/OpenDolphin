/*
 * IMECellEditor.java
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
package open.dolphin.table;

import java.awt.im.InputSubset;
import javax.swing.*;
import java.awt.event.*;

/**
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class IMECellEditor extends DefaultCellEditor {
    
    /** Creates a new instance of DTableCellEditor */
    public IMECellEditor(final JTextField tf, final int clickCount, final boolean on) {
        
        super(tf);
        
        int clickCountToStart = clickCount == 1 ? 1 : 2;
        setClickCountToStart(clickCountToStart);
        
        if (on) {

            // IME ‚ðON‚É‚·‚é Windows ‚Ì‚Ý‚É—LŒø
            tf.addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent event) {
                    tf.getInputContext().setCharacterSubsets(new Character.Subset[] {InputSubset.KANJI});
                    //tf.getInputContext().setCharacterSubsets(null);
                }
                public void focusLosted(FocusEvent event) {
                   tf.getInputContext().setCharacterSubsets(null);
                }
            });
        
        } else {
            // IME ‚ðOFF‚É‚·‚é Windows ‚Ì‚Ý‚É—LŒø
            tf.addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent event) {
                    //tf.getInputContext().setCharacterSubsets(new Character.Subset[] {InputSubset.KANJI});
                    tf.getInputContext().setCharacterSubsets(null);
                }
                public void focusLosted(FocusEvent event) {
                   tf.getInputContext().setCharacterSubsets(null);
                }
            });
        }
    }  
}
