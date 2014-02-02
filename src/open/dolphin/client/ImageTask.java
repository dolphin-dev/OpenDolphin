/*
 * ModuleTask.java
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

import open.dolphin.delegater.DocumentDelegater;
import open.dolphin.dto.ImageSearchSpec;
import open.dolphin.infomodel.SchemaModel;

/**
 * モジュールと予約を検索するタスク。
 * 
 * @author Kazushi Minagawa, Digital Globe, Inc.
 *
 */
public class ImageTask extends AbstractInfiniteTask {
	
    private SchemaModel schemaModel;
	private List images;
	private ImageSearchSpec spec;
	private DocumentDelegater ddl;
    
	public ImageTask(ImageSearchSpec spec, DocumentDelegater ddl, int taskLength) {
		this.spec = spec;
		this.ddl = ddl;
		setTaskLength(taskLength);
	}
    
	public List getImageList() {
		return images;
	}
	
	public SchemaModel getImage() {
		return schemaModel;
	}

	protected void doTask() {
        
		if (spec.getCode() == ImageSearchSpec.ID_SEARCH) {
			schemaModel = ddl.getImage(spec.getId());
		} else {
			images = ddl.getImageList(spec);
		}
		setDone(true);
	}
}