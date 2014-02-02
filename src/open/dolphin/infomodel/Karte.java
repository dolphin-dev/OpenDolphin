/*
 * Karte.java
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
package open.dolphin.infomodel;

import java.util.ArrayList;


/**
 *  2号カルテドキュメントのモデル
 * 
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class Karte extends MedicalDocument {
	
	Module[] module;
	Schema[] schema;
	
	public Karte() {
		getDocInfo().setDocType("karte");
	}
	
	public void setModule(Module[] module) {
		this.module = module;
	}
		
	public Module[] getModule() {
		return module;
	}
	
	public void addModule(Module[] value) {
		if (module == null) {
			module = new Module[value.length];
			System.arraycopy(value, 0, module, 0, value.length);
			return;
		}
		int len = module.length;
		Module[] dest = new Module[len + value.length];
		System.arraycopy(module, 0, dest, 0, len);
		System.arraycopy(value, 0, dest, len, value.length);
		module = dest;
	}
	
	public void addModule(Module value) {
		if (module == null) {
			module = new Module[1];
			module[0] = value;
			return;
		}
		int len = module.length;
		Module[] dest = new Module[len + 1];
		System.arraycopy(module, 0, dest, 0, len);
		module = dest;
		module[len] = value;
	}	

	public void setSchema(Schema[] schema) {
		this.schema = schema;
	}

	public Schema[] getSchema() {
		return schema;
	}
	
	public void addSchema(Schema value) {
		if (schema == null) {
			schema = new Schema[1];
			schema[0] = value;
			return;
		}
		int len = schema.length;
		Schema[] dest = new Schema[len + 1];
		System.arraycopy(schema, 0, dest, 0, len);
		schema = dest;
		schema[len] = value;
	}	
	
	public Module getModule(String entityName) {
		
		if (module == null) {
			return null;
		}
		
		Module ret = null;
		
		for (int i = 0; i < module.length; i++) {
			
			if (module[i].getModuleInfo().getEntity().equals(entityName)) {
				ret = module[i];
				break;
			}
		}
		
		return ret;
	}
	
	public ModuleInfo[] getModuleInfo(String entityName) {
		
		if (module == null) {
			return null;
		}
		
		ModuleInfo[] ret = null;
		ArrayList list = new ArrayList(2);
		
		for (int i = 0; i < module.length; i++) {
			
			if (module[i].getModuleInfo().getEntity().equals(entityName)) {
				list.add(module[i].getModuleInfo());
			}
		}
		
		int cnt = list.size();
		if (cnt > 0) {
			ret = new ModuleInfo[cnt];
			for (int i = 0; i < cnt; i++) {
				ret[i] = (ModuleInfo)list.get(i);
			}
		}
		return ret;
	}
}
