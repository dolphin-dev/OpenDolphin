/*
 * DefaultMenuBarBuilder.java
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
package open.dolphin.util;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import open.dolphin.client.*;


/**
 * MenuBar and ToolBar builder.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class DefaultMenuBarBuilder {
    
    static final String[] ACCELERATOR_STRING = {
        "VK_N", "VK_S", "VK_P", "VK_W", "VK_E", "VK_Z", "VK_Y", "VK_X", "VK_C", "VK_V"
    };
    
    static final int[] ACCELERATOR_INT = {
        KeyEvent.VK_N, KeyEvent.VK_S, KeyEvent.VK_P, KeyEvent.VK_W, KeyEvent.VK_E,
        KeyEvent.VK_Z, KeyEvent.VK_Y, KeyEvent.VK_X, KeyEvent.VK_C, KeyEvent.VK_V
    };

    HashMap actionTable;
    ActionListener actionListener;
    IRoutingTarget target;
    
    /** Products of this builder */
    JMenuBar menuBar;
    JPanel toolPanel;
    
    LinkedList menuList;
    LinkedList toolList;
    boolean hasToolBar;
    
    boolean DEBUG;

    /** 
     * Creates new MenuBarBuilder 
     */
    public DefaultMenuBarBuilder() {
    }
    
    public void setActionListener(ActionListener l) {
        actionListener = l;
    }
    
    public void setRoutingTarget(IRoutingTarget t) {
        target = t;
    }
    
    public void setActionTable(HashMap table) {
        actionTable = table;
    }
    
    /** 
     * Returns JMenuBar product
     */
    public JMenuBar getJMenuBar() {
        return menuBar;
    }
    
    /**
     * Returns ToolBar panel product
     */
    public JPanel getToolPanel() {
        return toolPanel;
    }
    
    /**
     * MenuBar Çê∂ê¨Ç∑ÇÈ
     */
    public void buildMenuBar() {
        debugString("buildMenuBar");
        menuBar = new JMenuBar();
        menuList = new LinkedList();
    }
    
    /**
     * Menu Çê∂ê¨Çµ MenuBar Ç÷í«â¡Ç∑ÇÈ
     */
    public void buildMenu(String text, String mnemonic) {
        debugString("buildMenu: " + text);
        JMenu menu = new JMenu(text);
        if (mnemonic != null) {
            menu.setMnemonic(mnemonic.charAt(0));
        }
        menuBar.add(menu);
        menuList.addFirst(menu);
        hasToolBar = false;
    }
    
    /**
     * DlAction Çê∂ê¨Çµ Menu, ToolBar Ç÷í«â¡Ç∑ÇÈ
     */
    public void buildActionItem(String key,
                                String text,
                                String iconSpec,
                                String accelerator,        // Accelerator
                                String toolTip,
                                String enabled) {                      
     

        debugString("buildActionItem: " + text);
        // Icon Ç™Ç»Ç¢èÍçá
        if (iconSpec == null) {
            buildActionItem_2(key, text, accelerator, enabled);
            return;
        }
        
        // Text & Icon ÇÃ Action Çê∂ê¨Ç∑ÇÈ
        ImageIcon icon = ClientContext.getImageIcon(iconSpec);
        DlAction action = new DlAction(text, icon, target);
        if (key != null) {
            actionTable.put(key, action);
        }
        
        if (! hasToolBar) {
            JToolBar toolBar = new JToolBar();
            toolBar.setMargin(new Insets(5,5,5,5));
            if (toolPanel == null) {
                toolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                toolList = new LinkedList();
            }
            toolPanel.add(toolBar);
            toolList.addFirst(toolBar);
            hasToolBar = true;
        }
        JMenu menu = getCurrentMenu();
        JToolBar toolBar = getCurrentToolBar();
        
        JMenuItem menuItem = menu.add(action);
        menuItem.setText(text);
        menuItem.setIcon(null);
        
        JButton btn = toolBar.add(action);        
        btn.setText("");
        btn.setIcon(icon);
        btn.setToolTipText(toolTip);
        
        setAcceleEnabled(menuItem, action, accelerator, enabled);
    }
    
    /**
     * Text ÇÃÇ›ÇÃ DlAction Çê∂ê¨ÇµÅAMenu Ç÷í«â¡Ç∑ÇÈ
     */
    void buildActionItem_2(String key, String text, String accelerator, String enabled) {
        
        debugString("buildActionItem_2: " + text);
        
        DlAction action = new DlAction(text,target);
        if (key != null) {
            actionTable.put(key, action);
        }
        
        JMenu menu = getCurrentMenu();
        JMenuItem menuItem = menu.add(action);
        menuItem.setText(text);
        menuItem.setIcon(null);
        setAcceleEnabled(menuItem, action, accelerator, enabled);
    }  
    
    /**
     * MenuItem Çê∂ê¨Çµ Menu Ç÷í«â¡Ç∑ÇÈ
     */
    public void buildMenuItem(String key, String text, String accelerator, String enabled) {
        
        debugString("buildMenuItem:" + text);
        
        JMenu menu = getCurrentMenu();
        
        if (text.equals("-")) {
            menu.addSeparator();
            return;
        }
        
        JMenuItem menuItem = new JMenuItem(text);
        if (key != null) {
            actionTable.put(key, menuItem);
        }
        menu.add(menuItem);
        menuItem.addActionListener(actionListener);
        setAcceleEnabled(menuItem, accelerator, enabled);
    } 
    
    /**
     * StyledEditorAction Çê∂ê¨ÇµMenuÇ÷í«â¡Ç∑ÇÈ
     */
    public void buildStyleItem(String key, String text, String enabled) {
        
        debugString("buildStyleItem: " + text);
        
        JMenu menu = getCurrentMenu();
        
        AbstractAction action = null;
        if (key != null) {
            action = (AbstractAction)actionTable.get(key);
        } else {
            debugString("buildStyleItem: key = null");
        }
        if (action != null) {
            JMenuItem menuItem = menu.add(action);
            menuItem.setText(text);
            menuItem.setIcon(null);
            setAcceleEnabled(menuItem, action, null, enabled);
        } else {
            debugString("buildStyleItem: action = null");
        }
    }    
    
    JMenu getCurrentMenu() {
        return (JMenu)menuList.getFirst();
    }
    
    JToolBar getCurrentToolBar() {
        return (JToolBar)toolList.getFirst();
    }
       
    void setAcceleEnabled(JMenuItem menuItem, Action action, String accelerator, String enabled) {
        
        debugString("setAcceleEnabled: " + accelerator);
        
        if (accelerator != null) {
            int i = acceleStringToInt(accelerator);
            menuItem.setAccelerator(KeyStroke.getKeyStroke(i,InputEvent.CTRL_MASK,false));
        }
        if (enabled != null) {
            action.setEnabled(Boolean.valueOf(enabled).booleanValue());
        }
    }
    
    void setAcceleEnabled(JMenuItem menuItem, String accelerator, String enabled) {
        
        debugString("setAcceleEnabled: " + accelerator);
        
        if (accelerator != null) {
            int i = acceleStringToInt(accelerator);
            menuItem.setAccelerator(KeyStroke.getKeyStroke(i, InputEvent.CTRL_MASK, false));
        }
        if (enabled != null) {
            menuItem.setEnabled(Boolean.valueOf(enabled).booleanValue());
        }        
    }
        
    public void buildEnd(int cmpType) {
        
        debugString("buildEnd");
        
        if (cmpType == MenuBarDirector.CMP_MENU) {
            menuList.removeFirst();
            if (hasToolBar) {
                toolList.removeFirst();
            }
        }
    }
        
    void debugString(String msg) {
        if (DEBUG) {
            System.out.println(msg);
        }
    }
    
    int acceleStringToInt(String str) {
        
        int ret = 0;
        
        for (int i = 0; i < ACCELERATOR_STRING.length; i++) {
            
            if (ACCELERATOR_STRING[i].equals(str)) {
                ret = ACCELERATOR_INT[i];
                break;
            }
        }
        
        return ret;
    }
}