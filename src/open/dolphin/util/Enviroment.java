/*
 * Enviroment.java
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
package open.dolphin.util;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * @author Kazushi Minagawa, Digital Globe, Inc.
 *
 */
public class Enviroment {
	
	private final int ARRAY_CAPACITY  = 20;
	private final int TT_VALUE        = 0;
	private final int TT_DELIM        = 1;
	
	private HashMap hashMap;
	
	private String enviromentFile;
	
	private String delimitater = ",";
	
	private String encoding;
	
	private String errorMessage;
	
	public Enviroment() {
		hashMap = new HashMap();
	}
	
	public void load() {
		
		if (getEncoding() == null) {
			load(enviromentFile);
		} else {
			try {
				load(new InputStreamReader(new FileInputStream(enviromentFile),encoding));
			}catch (Exception e) {
				processError(e.toString());
			}
		}
	}
	
	public void load(String fileSpec) {
		load(new File(fileSpec));
	}
	
	public void load(File f) {
		try {
			load(new InputStreamReader(new FileInputStream(f)));
		} catch (Exception e) {
			processError(e.toString());
		}
	}
	
	public void load(URL url) {
		try {
			load(new InputStreamReader(url.openStream()));
		} catch (Exception e) {
			processError(e.toString());
		}
	}
	
	public void load(InputStream in) {
		try {
			load(new InputStreamReader(in));
		} catch (Exception e) {
			processError(e.toString());
		}		
	}
	
	public void load(InputStreamReader r) {
		
		try {
			load(new BufferedReader(r));
		} catch (Exception e) {
			processError(e.toString());
		}
	}
	
	public void load(BufferedReader reader) {
				
		try {			
			String line = null;
			String key = null;
			String val = null;
			int index = 0;
			
			while ((line = reader.readLine()) != null) {
				
				if (line.startsWith("#") || line.startsWith("!")) {
					continue;
				}
				
				index = line.indexOf("=");
				if (index > 0) {
					key = line.substring(0, index).trim();
					val = line.substring(index + 1).trim();
					putString(key, val); 
				}
			}
			
		} catch (Exception e) {
			processError(e.toString());
		}
		
		closeReader(reader);
	}
	
	public void store() {
		if (getEncoding() == null) {
			store(enviromentFile);
		} else {
			try {
				store(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(enviromentFile), encoding)));
			} catch (Exception e) {
				processError(e.toString());
			}
		}
	}
	
	public void store(String fileSpec) {
		try {
			store(new BufferedWriter(new FileWriter(fileSpec)));
		} catch (Exception e) {
			processError(e.toString());
		}
	}
	
	public void store(File f) {
		try {
			store(new BufferedWriter(new FileWriter(f)));
		} catch (Exception e) {
			processError(e.toString());
		}
	}	
	
	public void store(BufferedWriter writer) {
				
		Iterator iter = hashMap.keySet().iterator();
		String key = null;
		
		try {			
			while (iter.hasNext()) {
				key = (String)iter.next();
				writer.write(key);
				writer.write("=");
				writer.write(getString(key));
				writer.write("\n");
			}
			writer.flush();
			
		} catch (Exception e) {
			processError(e.toString());		
		}
		
		closeWriter(writer);
	}
	
	public String getString(String key) {
		return (String)getHashMap().get(key);
	}
	
	public void putString(String key, String val) {
		getHashMap().put(key, val);
	}
	
	public String[] getStringArray(String key) {
		
		String line = getString(key);
		
		if (line == null) {
			return null;
		}
		
		String[] ret = new String[ARRAY_CAPACITY];
		int count = 0;
        
		StringTokenizer st = new StringTokenizer(line, delimitater, true);
		int state = TT_VALUE;
        
		while (st.hasMoreTokens()) {
            
			if ( (count % ARRAY_CAPACITY) == 0 ) {
				String[] dest = new String[count + ARRAY_CAPACITY];
				System.arraycopy(ret, 0, dest, 0, count);
				ret = dest;
			}

			String token = st.nextToken();

			switch (state) {

				case TT_VALUE:
					if (token.equals(delimitater)) {
						token = null;

					} else {
						state = TT_DELIM;
					}
					ret[count] = token;
					count++;
					break;

				case TT_DELIM:
					state = TT_VALUE;
					break;
			}
		}

		String[] ret2 = new String[count];
		System.arraycopy(ret, 0, ret2, 0, count);

		return ret2;
	}
	
	public void putStringArray(String key, String[] val) {
		if (val == null) {
			putString(key, null);
			return;
		}
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < val.length; i++) {
			if (i != 0) {
				buf.append(getDelimitater());
			}
			buf.append(val[i]);
		}
		putString(key, buf.toString());
	}
	
	public int getInt(String key, int ret) {
		
		String val = getString(key);
		if (val == null) {
			return ret;
		}
				
		try {
			ret = Integer.parseInt(val);
			
		} catch (Exception e) {
			processError(e.toString());
		}
		
		return ret;
	}
	
	public void putInt(String key, int val) {
		putString(key, String.valueOf(val));
	}
	
	public int[] getIntArray(String key) {
		
		String[] st = getStringArray(key);
		
		if (st == null) {
			return null;
		}
		
		int[] ret = new int[st.length];
		
		try {
			for (int i = 0; i < st.length; i++) {
				ret[i] = Integer.parseInt(st[i]);
			}
		} catch (Exception e) {
			ret = null;
			processError(e.toString());
		}
		
		return ret;
	}
	
	public void putIntArray(String key, int[] val) {
		if (val == null) {
			putString(key, null);
			return;
		}
		String[] st = new String[val.length];
		for (int i = 0; i < val.length; i++) {
			st[i] = String.valueOf(val[i]);
		}
		putStringArray(key, st);
	}
	
	public long getLong(String key, long ret) {
		
		String val = getString(key);
		
		if (val == null) {
			return ret;
		}

		try {
			ret = Long.parseLong(val);
	
		} catch (Exception e) {
			processError(e.toString());
		}

		return ret;
	}
	
	public void putLong(String key, long val) {
		putString(key, String.valueOf(val));
	}	
	
	public long[] getLongArray(String key) {
		
		String[] st = getStringArray(key);
		
		if (st == null) {
			return null;
		}
		
		long[] ret = new long[st.length];
		
		try {
			for (int i = 0; i < st.length; i++) {
				ret[i] = Long.parseLong(st[i]);
			}
		} catch (Exception e) {
			ret = null;
			processError(e.toString());
		}
		
		return ret;
	}
	
	public void putLongArray(String key, long[] val) {
		if (val == null) {
			putString(key, null);
			return;
		}
		String[] st = new String[val.length];
		for (int i = 0; i < val.length; i++) {
			st[i] = String.valueOf(val[i]);
		}
		putStringArray(key, st);
	}	
	
	public boolean getBoolean(String key, boolean ret) {
		
		String val = getString(key);
		if (val == null) {
			return ret;
		}
				
		try {
			ret = Boolean.valueOf(val).booleanValue();
			
		} catch (Exception e) {
			processError(e.toString());
		}
		
		return ret;
	}
	
	public void putBoolean(String key, boolean val) {
		putString(key, String.valueOf(val));
	}	
	
	public boolean[] getBooleanArray(String key) {
		
		String[] st = getStringArray(key);
		
		if (st == null) {
			return null;
		}
		
		boolean[] ret = new boolean[st.length];
		
		try {
			for (int i = 0; i < st.length; i++) {
				ret[i] = Boolean.valueOf(st[i]).booleanValue();
			}
		} catch (Exception e) {
			ret = null;
			processError(e.toString());
		}
		
		return ret;
	}
	
	public void putBooleanArray(String key, boolean[] val) {
		if (val == null) {
			putString(key, null);
			return;
		}
		String[] st = new String[val.length];
		for (int i = 0; i < val.length; i++) {
			st[i] = String.valueOf(val[i]);
		}
		putStringArray(key, st);
	}	
	
	public Color getColor(String key) {
		
		int[] val = getIntArray(key);
		
		if (val == null) {
			return null;
		}
		
		Color ret = null;
		try {
			ret = new Color(val[0], val[1], val[2]);
			
		} catch (Exception e) {
			processError(e.toString());
		}
		
		return ret;
	}
	
	public void putColor(String key, Color val) {
		if (val == null) {
			putString(key, null);
			return;
		}
		
		String[] st = new String[3];
		st[0] = String.valueOf(val.getRed());
		st[1] = String.valueOf(val.getGreen());
		st[2] = String.valueOf(val.getBlue());
		
		putStringArray(key, st);
	}
	
	public Color[] getColorArray(String key) {
		
		int[] val = getIntArray(key);
		
		if (val == null) {
			return null;
		}
		
		int cnt = val.length / 3;
		Color[] ret = new Color[cnt];
		
		try {
			for (int i = 0; i < cnt; i++) {
				int k = i * 3;
				ret[i] = new Color(val[k], val[k+1], val[k+2]);
			}
		} catch (Exception e) {
			ret = null;
			processError(e.toString());
		}
		
		return ret;
	}
	
	public String toString() {
    	
		StringBuffer buf = new StringBuffer();
		Iterator iter = hashMap.keySet().iterator();
		String key = null;
		while (iter.hasNext()) {
			key = (String)iter.next();
			buf.append(key);
			buf.append("=");
			buf.append(getString(key));
			buf.append("\n");
		}
    	        
		return buf.toString();
	}	
	
	private void processError(String msg) {
		System.out.println(msg);
		setErrorMessage(msg);					
	}

	public void setHashMap(HashMap hashMap) {
		this.hashMap = hashMap;
	}

	public HashMap getHashMap() {
		return hashMap;
	}

	public void setEnviromentFile(String enviromentFile) {
		this.enviromentFile = enviromentFile;
	}

	public String getEnviromentFile() {
		return enviromentFile;
	}

	public void setDelimitater(String delimitater) {
		this.delimitater = delimitater;
	}

	public String getDelimitater() {
		return delimitater;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
		
	private void closeReader(Reader r) {
		
		if (r != null) {
			try {
				r.close();
				r = null;
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}
	
	private void closeWriter(Writer w) {
		
		if (w != null) {
			try {
				w.close();
				w = null;
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}	
}
