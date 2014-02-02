/*
 * Created on 2004/04/10
 *
 * Copyright (C) 2003 Digital Globe, Inc. All rights reserved. 
 * 
 */
package open.dolphin.client;

import java.net.URL;
import java.net.URLClassLoader;

import open.dolphin.infomodel.InfoModel;

/**
 * @author Kazushi Minagawa, Digital Globe, Inc.
 *
 */
public class URLClassLoaderTest {
		
	public static void main(String[] args) {
		
		try {
			String spec = "jar:file:/D:/develop/openDolphin/bin/gcp/TJN-324.jar!/";
			URL[] url = new URL[]{new URL(spec)};
			URLClassLoader classLoader = new URLClassLoader(url);
			Class c = classLoader.loadClass("gcp.ObjectiveNotes");
			InfoModel model = (InfoModel)c.newInstance();
			System.out.println(model.isValidModel());
			
		} catch (Exception e) {
			System.out.println(e);
		}
		
		System.exit(1);
	}
}
