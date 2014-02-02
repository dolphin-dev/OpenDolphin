/*
 * StampTreeEntry.java
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

/**
 * @author Kazushi Minagawa, Digital Globe, Inc.
 *
 */
public class StampTreeEntry {
	
	private String userId;
	
	private String id;
	
	private boolean use;
	
	private int number;
	
	private String treeXml;

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return userId;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setUse(boolean use) {
		this.use = use;
	}

	public boolean isUse() {
		return use;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getNumber() {
		return number;
	}

	public void setTreeXml(String treeXml) {
		this.treeXml = treeXml;
	}

	public String getTreeXml() {
		return treeXml;
	}
}
