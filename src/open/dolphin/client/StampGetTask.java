/*
 * StampTask.java
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

import java.util.List;

import open.dolphin.delegater.StampDelegater;
import open.dolphin.infomodel.ModuleInfoBean;
import open.dolphin.infomodel.StampModel;

/**
 * StampTask
 * 
 * @author Kazushi Minagawa
 */
public class StampGetTask extends AbstractInfiniteTask {
    
	private List<StampModel> result;
	private List<ModuleInfoBean> stampIdList;
	private StampDelegater sdl;
	private int putCode;
	
	
	public StampGetTask(List<ModuleInfoBean> stampIdList, StampDelegater sdl, int taskLength) {
		this.stampIdList = stampIdList;
		this.sdl = sdl;
		setTaskLength(taskLength);
	}
	
	public int getResult() {
		return putCode;
	}
	
	public List<StampModel> getModelList() {
		return result;
	}

	
	protected void doTask() {
		result = sdl.getStamp(stampIdList);
		setDone(true);
	}
}