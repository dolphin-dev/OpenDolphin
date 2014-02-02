/*
 * ChartDocumentProxy.java
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


import java.awt.Component;

import open.dolphin.exception.PluginException;
import open.dolphin.plugin.*;

/**
 * チャートドキュメントのプロキシクラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class DocumentProxy implements IChartDocument {

	private String name;
    private String title;
    private IChartDocument document;
    private String className;

    /** Creates new ChartDocumentProxy */
    public DocumentProxy(String name, String title, String className) {
    	this.name = name;
        this.title = title;
        this.className = className;
    }
    
	/** Creates new ChartDocumentProxy */
	public DocumentProxy(String title, String className) {
		this(null, title,className);
	}    
    
    public String getName() {
    	return name;
    }
    
    public void setName(String name) {
    	this.name = name;
    }

    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public Component getUI() {
        return (Component)getDocument();
    }
    
    /////////////////////////////////
    public IChartContext getChartContext() {
        return getDocument().getChartContext();
    }

    public void setChartContext(IChartContext val) {
        getDocument().setChartContext(val);
    }
    
    public void init() throws PluginException {
		getDocument().init();
    }

    public void start() {
        getDocument().start();
    }
    
    public void stop() {
		getDocument().stop();
    }
    
    public void enter() {
        getDocument().enter();
    }    
    
    public void save() {
        getDocument().save();
    }
        
    public boolean isDirty() {
        return getDocument().isDirty();
    }
    
    /////////// Factory /////////////
    private IChartDocument getDocument() {
        if (document == null) {
            document = (IChartDocument)ClassFactory.create(className);
            document.setTitle(getTitle());
        }
        return document;
    }
}