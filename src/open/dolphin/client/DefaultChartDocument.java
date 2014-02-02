/*
 * DefaultChartDocument.java
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

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

/**
 * チャートドキュメントのルートクラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class DefaultChartDocument implements IChartDocument {
    
    
    private static final String[] CHART_MENUS = {
        GUIConst.ACTION_NEW_KARTE, GUIConst.ACTION_SAVE, GUIConst.ACTION_PRINT, GUIConst.ACTION_MODIFY_KARTE,
        GUIConst.ACTION_ASCENDING, GUIConst.ACTION_DESCENDING, GUIConst.ACTION_SHOW_MODIFIED,
        GUIConst.ACTION_INSERT_TEXT, GUIConst.ACTION_INSERT_SCHEMA, GUIConst.ACTION_INSERT_STAMP, GUIConst.ACTION_SELECT_INSURANCE
    };
    
    private IChart chartContext;
    private String title;
    private JPanel ui;
    private boolean dirty;
    
    
    /** Creates new DefaultChartDocument */
    public DefaultChartDocument() {
        setUI(new JPanel());
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public IChart getContext() {
        return chartContext;
    }
    
    public void setContext(IChart chart) {
        this.chartContext = chart;
    }
    
    public void initialize() {}
    
    public void start(){}
    
    public void stop(){}
    
    public void enter() {
        chartContext.getStatusPanel().setMessage("");
        getContext().getChartMediator().addChain(this);
        disableMenus();
    }
    
    public JPanel getUI() {
        return ui;
    }
    
    public void setUI(JPanel ui) {
        this.ui = ui;
    }
    
    public void save() {}
    
    public void print() {}
    
    public boolean isDirty() {
        return dirty;
    }
    
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
    
    public boolean isReadOnly() {
        return chartContext.isReadOnly();
    }
    
    public JFrame getParentFrame() {
        return chartContext.getFrame();
    }
    
    public Logger getLogger(String category) {
        return ClientContext.getLogger(category);
    }
    
    public void disableMenus() {
        // このウインドウに関連する全てのメニューをdisableにする
        ChartMediator mediator = getContext().getChartMediator();
        mediator.disableMenus(CHART_MENUS);
    }
    
    /**
     * 共通の警告表示を行う。
     * @param message
     */
    protected void warning(String title, String message) {
        JFrame parent = getContext().getFrame();
        JOptionPane.showMessageDialog(parent, message, ClientContext.getFrameTitle(title), JOptionPane.WARNING_MESSAGE);
    }
    
}