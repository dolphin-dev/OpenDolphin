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
import javax.swing.JPanel;

import open.dolphin.exception.PluginException;
import open.dolphin.plugin.*;

/**
 * チャートドキュメントのルートクラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class DefaultChartDocument extends JPanel implements IChartDocument {
        
    protected String title;
    
    protected IChartContext context;

    /** Creates new DefaultChartDocument */
    public DefaultChartDocument() {
    }
    
    public JFrame getParentFrame() {
        return ((ChartPlugin)context);
    }
    
    public IChartContext getChartContext() {
        return context;
    }
        
    public void setChartContext(IChartContext ctx) {
        context = ctx;
    }
    
    public String getTitle() {
        return title;
    } 
        
    public void setTitle(String title) {
        this.title = title;
    }
    
    public void init() throws PluginException {
    }
    
    public void start() {
    }
    
    public void stop() {
    }
    
    public void enter() {
        ((ChartPlugin)context).getStatusPanel().setMessage("");
    }    
    
    public void save() {
    }
    
    public boolean isDirty() {
        return false;
    }
    
    public boolean isReadOnly() {
        return context.isReadOnly();
    }
    
    protected void controlMenu() {
                
        ChartMediator mediator = ((ChartPlugin)context).getChartMediator();
        
        // 2003-10-30 licenseCode による制御
        boolean canEdit = isReadOnly() ? false : true;
        mediator.newKarteAction.setEnabled(canEdit);            // 新規カルテ
        mediator.copyNewKarteAction.setEnabled(false);          // コピー新規
        mediator.newReferralAction.setEnabled(false);           // Until release
        mediator.saveKarteAction.setEnabled(false);             // 保存
        mediator.printAction.setEnabled(false);                 // 印刷
        mediator.modifyKarteAction.setEnabled(false);           // 修正        
    }
    
    protected void debug(String msg) {
    	if (ClientContext.isDebug()) {
    		System.out.println(msg);    
    	}
    }
}