/*
 * DolphinContextStub.java
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

import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.*;
import java.util.prefs.*;

import javax.swing.ImageIcon;
import javax.swing.UIManager;

import open.dolphin.exception.PluginException;
import open.dolphin.plugin.*;
import open.dolphin.project.*;
import open.dolphin.util.Enviroment;

import org.apache.velocity.app.Velocity;
import org.apache.velocity.VelocityContext;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * Dolphin Client のコンテキストクラス。 
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class ClientContextStub extends Enviroment {
    
	private final String DOLPHIN_FILE       = "dolphin.xml";
    private final String RESOURCE_LOCATION  = "/open/dolphin/resources/";
	private final String TEMPLATE_LOCATION  = "/open/dolphin/resources/templates/";
	private final String IMAGE_LOCATION     = "/open/dolphin/resources/images/";
	private final String SCHEMA_LOCATION    = "/open/dolphin/resources/schema/";
	private final String VELOCITY_PROP_FILE = "velocity.properties";
	private final String PROJECT_FILE 		= "project.prop";
	private final String RESOURCE_FILE 		= "Dolphin.sjis.properties";
	private final String RESOURCE_ENCODING 	= "SHIFT_JIS";
    
	private final String STRING_DELIM = ",";
	private final int ARRAY_CAPACITY  = 20;
	private final int TT_VALUE        = 0;
	private final int TT_DELIM        = 1;
	private final String NULL_VALUE   = "";
           
	private Logger logger;
	private final String LOG_FILE  = "dolphin%g.log";
	private final int LOG_CAPACITY = 100000;  // 100K
	private final int LOG_ROTATION = 1;       // 1
	
	private Preferences prefs;
	private VelocityContext velocityContext;
    
	private HashMap serviceRegistry;
	private Vector curServices;
    
	private final String EXIT_PROP = "exitProp";
	private boolean exit;
	
	private boolean DEBUG = true;

	/** Creates new DolphinContext */
	public ClientContextStub() {
    	
		// インストールされているディレクトリパスを取得する
		
        
		// ロガーを取得する
		logger = Logger.getLogger("open.dolphin");

		if (!DEBUG) {
	
			logger.setUseParentHandlers(false);
    
			try{
				StringBuffer buf = new StringBuffer();
				buf.append(getUserDirectory());
				buf.append(File.separator);
				buf.append(LOG_FILE);
				Handler fileHandler = new FileHandler(buf.toString(), LOG_CAPACITY, LOG_ROTATION);
				fileHandler.setFormatter(new SimpleFormatter());
				logger.addHandler(fileHandler);
    
			} catch (java.io.IOException ex){
				System.out.println("IOException while adding the handler: " + ex.toString());
				ex.printStackTrace();
			}

		} else {
			logger.setUseParentHandlers(true);
			logger.setLevel(Level.ALL);
			Logger parent = logger.getParent();

			// ログ Handler の Level を設定する
			Handler[] handlers = parent.getHandlers();
			for(int i = 0 ; i < handlers.length ; i++){
				if(handlers[i] instanceof java.util.logging.ConsoleHandler){
					handlers[i].setLevel(Level.ALL);
					break;
				}
			}
		}
	  	        
		// Velocity を初期化する
		try {
			//Velocity.init(installedDir + File.separator + VELOCITY_PROP_FILE);
			Velocity.init();
			velocityContext = new VelocityContext();
			logger.info("Velocity を初期化しました");
			
		} catch(Exception e) {
			logger.warning("Exception while initializing the Velocity: " + e );
			System.exit(1);
		}
        
		//プレファレンスを取得する
		//prefs = Preferences.systemNodeForPackage(this.getClass());
		prefs = Preferences.userNodeForPackage(this.getClass());
         
		// リソースを読み込む        
		loadResources();
        
		// プラグインホルダ
		serviceRegistry = new HashMap();
		curServices = new Vector();
		
		// プラグインを読み込む
		loadPlugins();
        
		// デフォルトの UI フォントを変更する
		setUIFonts();
		
	}
        
	////////////////////////////////////////////////////////////////////////////
	
	public void loadPlugins() {
    	    	
		try {
			InputStream in = getResourceAsStream(DOLPHIN_FILE);
			BufferedReader r = new BufferedReader(new InputStreamReader(in, "SHIFT_JIS"));
			SAXBuilder docBuilder = new SAXBuilder();
			Document doc = docBuilder.build(r);
			
			// mainWindow plugins
			Element root = doc.getRootElement().getChild("mainWindow");
			parseMainWindowPlugin(root);
			
			// chart plugins
			root = doc.getRootElement().getChild("chart");
			parseChartPlugin(root);
			
			// chart plugins
			//root = doc.getRootElement().getChild("karteEditor");
			//parseKarteEditorPlugin(root);
			
			// StampEditor
			root = doc.getRootElement().getChild("stampBox");
			parseStampEditor(root);
			
			// DataAccess
			root = doc.getRootElement().getChild("dataAccess");
			parseDao(root);
			
			logger.info("プラグインを読み込みました");							
    		
		} catch (Exception e) {
			logger.warning("Exception while loading plugins: " + e.toString());
			e.printStackTrace();
			System.exit(1);
		}
	}
    
	private void parseMainWindowPlugin(Element mainWindow) {
    	
		List children = mainWindow.getChildren();
		Iterator iterator = children.iterator();
		Element child = null;
		String ename = null;
		ServiceProxy proxy = null;
		String id = null;
		String name = null;
		String title = null;
		String icon = null;
		String selectedIcon = null;
		String className = null;
		int index = 0;
		String startUp = null;
		String visible = null;
		boolean bStartUp = false;
		boolean bVisible = false;

		while (iterator.hasNext()) {
			
			child = (Element)iterator.next();
			ename = child.getName();
		    
			if (ename.equals("plugin")) {
				name = child.getChildTextTrim("name");
				title = child.getChildTextTrim("title");
				icon = child.getChildTextTrim("icon");
				selectedIcon = child.getChildTextTrim("selectedIcon");
				className = child.getChildTextTrim("class");
				startUp = child.getChildTextTrim("startUp");
				bStartUp = startUp != null ? Boolean.valueOf(startUp).booleanValue() : false;
				visible = child.getChildTextTrim("visible");
				bVisible = visible != null ? Boolean.valueOf(visible).booleanValue() : false;
				id = "mainWindow." + name;
				
				proxy = new ServiceProxy(id,
										 title,
										 getImageIcon(icon),
										 getImageIcon(selectedIcon),
										 className,
				                         bStartUp,
										 bVisible);
				serviceRegistry.put(id, proxy);
			}
		}
	}
	
	private void parseChartPlugin(Element chart) {
    	
		List children = chart.getChildren();
		Iterator iterator = children.iterator();
		Element child = null;
		String ename = null;
		String name = null;
		String title = null;
		String className = null;
		String key = null;
		String value = null;
		StringBuffer buf = new StringBuffer();
		int index = 0;

		while (iterator.hasNext()) {
			
			child = (Element)iterator.next();
			ename = child.getName();
		    
			if (ename.equals("plugin")) {
				name = child.getChildTextTrim("name");
				title = child.getChildTextTrim("title");
				className = child.getChildTextTrim("class");
				key = "chart." + name;
				value= title + "," + className;
				if (index != 0) {
					buf.append(",");
				}
				buf.append(key);
				getHashMap().put(key, value);
				index++;
			}
		}
		
		getHashMap().put("chart.documents", buf.toString());
	}
	
	private void parseStampEditor(Element stampBox) {
    	
		List children = stampBox.getChildren();
		Iterator iterator = children.iterator();
		Element child = null;
		String ename = null;
		String entity = null;
		String className = null;
		String key = null;

		while (iterator.hasNext()) {
			
			child = (Element)iterator.next();
			ename = child.getName();
		    
			if (ename.equals("stampEditor")) {
				entity = child.getChildTextTrim("entity");
				className = child.getChildTextTrim("class");
				key = "stampEditor." + entity;
				getHashMap().put(key, className);
			}
		}
	}
	
	private void parseDao(Element dataAccess) {
    	
		List children = dataAccess.getChildren();
		Iterator iterator = children.iterator();
		Element child = null;
		String ename = null;
		String name = null;
		String className = null;
		String key = null;

		while (iterator.hasNext()) {
			
			child = (Element)iterator.next();
			ename = child.getName();
		    
			if (ename.equals("dao")) {
				name = child.getChildTextTrim("name");
				className = child.getChildTextTrim("class");
				
				if (name.equals("dao")) {
					key = name;
				} else {
					key = "dao." + name;
				}
				
				getHashMap().put(key, className);
			}
		}
	}			
	
	public void loadServices() {
		String[] services = getStringArray("service.default");
		int len = services.length;
		String[] params;
		String key;
		ServiceProxy proxy;
		for (int i = 0; i < len; i++) {
			key = services[i];
			params = getStringArray(key);
			proxy = new ServiceProxy(key,                                        	// id
									 params[0],                                  	// name
									 getImageIcon(params[1]),                 		// Icon
									 getImageIcon(params[2]),                 		// SelectedIcon
									 params[3],                                  	// Class Name
									 Boolean.valueOf(params[4]).booleanValue(),  	// Startup prop
									 Boolean.valueOf(params[5]).booleanValue()   	// Visible prop
									 );
			serviceRegistry.put(key, proxy);
		}  
	}
    
	public IPlugin getPlugin(String id) {
        
		ServiceProxy proxy = (ServiceProxy)serviceRegistry.get(id);
        
		if (proxy == null) {
			logger.warning(id + " is not registered");
			return null;
		}
            
		IPlugin service = proxy.getService();
		if (service != null) {
			return service;
		}
        
		service = (IPlugin)ClassFactory.create(proxy.getClassName());
		service.setName(id);
		service.setTitle(proxy.getServiceName());
		proxy.setService(service);
		curServices.add(service);
        
		try {
			service.init();
			service.start();
			logger.info(id + " started");
		} catch (PluginException e) {
			logger.warning("Exception while initializing and starting the plugin " + id);
			curServices.remove(service);
			service = null;
		}

		return service;
	}
    
	public void remove(IPlugin service) {
        
		String id = service.getName();
		curServices.remove(service);
        
		if (service instanceof IChartContext) {
			logger.info("Chart " + service.getTitle() + " end");
			return;
		}
        
		ServiceProxy proxy = (ServiceProxy)serviceRegistry.get(id);
		if (proxy != null) {
			proxy.setService(null);        
			logger.info(id + " closed");
		}
	} 
        
	public String[] getServiceNames() {
        
		ArrayList allServices = ServiceProxy.getAllServices();
		int size = allServices.size();
		String[] ret = new String[size];
		for (int i = 0; i < size; i++) {
			ret[i] = ((ServiceProxy)allServices.get(i)).getServiceName();
		}
		return ret;
	} 
    
	public Object[] getServiceProxies() {
		ArrayList serviceList = ServiceProxy.getAllServices();
		return serviceList != null ? serviceList.toArray() : null;
	}     
    
	public void addCurrentService(IPlugin s) {
		curServices.add(s);
	}
    
	public void removeCurrentService(IPlugin s) {
		curServices.remove(s);
	}
    
	////////////////////////////////////////////////////////////////////////////
    
	public VelocityContext getVelocityContext() {
		return velocityContext;
	}
    
	public Logger getLogger() {
		return logger;
	}
    
	public Preferences getPreferences() {
		return prefs;
	}
	
	public boolean isDebug() {
		return DEBUG;
	}
    
	////////////////////////////////////////////////////////////////////////////
    
	public String getUserDirectory() {
		return System.getProperty("user.dir");
	}
    
	public String getVersion() {
		return getString("version");
	}
	
	public String getWindowTitle(String title) {
		StringBuffer buf = new StringBuffer();
		buf.append(title);
		buf.append("-");
		buf.append(getString("application.title"));
		return buf.toString();
	}
    
	public URL getResource(String name) {
		return this.getClass().getResource(RESOURCE_LOCATION + name);
	}
    
	public URL getImageResource(String name) {
		return this.getClass().getResource(IMAGE_LOCATION + name);
	}
    
	public InputStream getResourceAsStream(String name) {
		return this.getClass().getResourceAsStream(RESOURCE_LOCATION + name);
	}
	
	public InputStream getTemplateAsStream(String name) {
		return this.getClass().getResourceAsStream(TEMPLATE_LOCATION + name);
	}	    
           
	public ImageIcon getImageIcon(String name) {
		return new ImageIcon(getImageResource(name));
	}
    
	public ImageIcon getSchemaIcon(String name) {        
		return new ImageIcon(this.getClass().getResource(SCHEMA_LOCATION + name));
	}
    
	////////////////////////////////////////////////////////////////////////////
   
	public boolean getExit() {
		return exit;
	}
    
	public void setExit(boolean b) {
        
		if (! b) {
			return;
		}
        
		Vector v = null;
		synchronized (curServices) {
			v = (Vector)curServices.clone();
		}
        
		int size = v.size();
		PropertyChangeEvent e = new PropertyChangeEvent(this, EXIT_PROP, new Boolean(false), new Boolean(true));
		IMainWindowPlugin service = null;    
        
		// Ask all services if exit ok
		try {       
			for (int i = 0; i < size; i++) {
				service = (IMainWindowPlugin)v.get(i);
				if (service != null) {
					service.vetoableChange(e);
				}
			}
			// Change the property
			exit = true;
		}
		catch (PropertyVetoException ve) {
			logger.info("PropertyVetoException at setExit: " + e.toString());
		}
        
		if (exit) {
			for (int i = 0; i < size; i++) {
				service = (IMainWindowPlugin)v.get(i);
				if (service != null) {
					service.propertyChange(e);
				}
			}
		}
	}
            
	public ProjectStub loadProject() {
        
		ProjectStub projectStub = new ProjectStub();
        
		// Project 情報保存ファイルを得る
		String theFile = getString("application.projectFile");
		String userDir = System.getProperty("user.dir");
		System.out.println(userDir);
		File f = new File(userDir + File.separator + theFile);
		
		if (f.exists()) {
			// プロジェクトファイルが存在
			try {
				URL url = f.toURL();
				System.out.println(url);
				projectStub.load(url);
				projectStub.setValid(true);
			} catch (Exception e) {
				System.out.println(e.toString());	
			}
		} else {
			// リソースからデフォルトのプロジェクトファイルを読み込む
			projectStub.load(getResourceAsStream("project.prop"));
			projectStub.setValid(false);
		}
	
		if (projectStub.getErrorMessage() == null) {
			logger.info("プロジェクトファイルを読み込みました");		
		} else {
			logger.info("プロジェクトファイルの読み込みができません: " + projectStub.getErrorMessage());
			projectStub.setValid(false);	
		}
		
		return projectStub;
	}   
	
	public void storeProject(ProjectStub projectStub) {
		
		String theFile = getString("application.projectFile");
		String userDir = System.getProperty("user.dir");
		System.out.println(userDir);
		File f = new File(userDir + File.separator + theFile);
		
		projectStub.store(f);
		
		if (projectStub.getErrorMessage() != null) {
			logger.warning("プロジェクトファイルの保存ができません: " + projectStub.getErrorMessage());
		} else {
			logger.info("プロジェクトファイルを保存しました");
		}
	}
	
	/////////////////////////////////////////////////////////
    
	/**
	 * Windows のデフォルトフォントを設定する。
	 */
	private void setUIFonts() { 
    	
		String osName = System.getProperty("os.name");
		
		if (osName.startsWith("Windows")) {
			Font font = new Font("Dialog", Font.PLAIN, 12);
			UIManager.put("Label.font", font);
			UIManager.put("Button.font", font);
			UIManager.put("ToggleButton.font", font);
			UIManager.put("Menu.font", font);
			UIManager.put("MenuItem.font", font);
			UIManager.put("CheckBox.font", font);
			UIManager.put("CheckBoxMenuItem.font", font);
			UIManager.put("RadioButton.font", font);
			UIManager.put("RadioButtonMenuItem.font", font);
			UIManager.put("ToolBar.font", font);
			UIManager.put("ComboBox.font", font);
			UIManager.put("TabbedPane.font", font);
			UIManager.put("TitledBorder.font", font);
			UIManager.put("List.font", font);
	        
			logger.info("デフォルトの UI フォントを変更しました");
		}
	}
    
	private void loadResources() {
    	
		try {
			load(new InputStreamReader(getResourceAsStream(RESOURCE_FILE), RESOURCE_ENCODING));
			
			if (getErrorMessage() != null) {
				logger.warning("リソースファイルの読み込みができません: " + getErrorMessage());
				System.exit(1);
			} else {
				logger.info("リソースファイルを読み込みました: " + RESOURCE_FILE);
			}
			
		} catch (Exception e) {
			logger.warning("Exception while reading the resources: " + e.toString());
			System.exit(1);
		}
	}

}