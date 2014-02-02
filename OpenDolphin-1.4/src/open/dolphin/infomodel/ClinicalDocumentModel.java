/*
 * ClinicalDocumentModel.java
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
 * ClinicalDocumentModel
 * 
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class ClinicalDocumentModel extends InfoModel {

	private static final long serialVersionUID = 570360867001362278L;

	private PatientLiteModel patientLiteModel;

	private UserLiteModel creatorLiteModel;

	private DocInfoModel docInfo;

	private ModuleModel[] moduleModel;

	/** 
	 * Creates a new instance of ClinicalDocumentModel 
	 */
	public ClinicalDocumentModel() {
		docInfo = new DocInfoModel();
	}

	/**
	 * 文書情報を返す。
	 * @return 文書情報
	 */
	public DocInfoModel getDocInfoModel() {
		return docInfo;
	}

	/**
	 * 文書情報を設定する。
	 * @param docInfo 文書情報
	 */
	public void setDocInfoModel(DocInfoModel docInfo) {
		this.docInfo = docInfo;
	}

	/**
	 * モジュールモデルを設定する。
	 * @param module モジュールモデルの配列
	 */
	public void setModuleModel(ModuleModel[] module) {
		this.moduleModel = module;
	}

	/**
	 * モジュールモデルを返す。
	 * @return モジュールモデルの配列
	 */
	public ModuleModel[] getModuleModel() {
		return moduleModel;
	}

	/**
	 * モジュールモデルの配列を追加する。
	 * @param moules モジュールモデルの配列
	 */
	public void addModule(ModuleModel[] moules) {
		if (moduleModel == null) {
			moduleModel = new ModuleModel[moules.length];
			System.arraycopy(moules, 0, moduleModel, 0, moules.length);
			return;
		}
		int len = moduleModel.length;
		ModuleModel[] dest = new ModuleModel[len + moules.length];
		System.arraycopy(moduleModel, 0, dest, 0, len);
		System.arraycopy(moules, 0, dest, len, moules.length);
		moduleModel = dest;
	}

	/**
	 * モジュールモデルを追加する。
	 * @param value モジュールモデル
	 */
	public void addModule(ModuleModel value) {
		if (moduleModel == null) {
			moduleModel = new ModuleModel[1];
			moduleModel[0] = value;
			return;
		}
		int len = moduleModel.length;
		ModuleModel[] dest = new ModuleModel[len + 1];
		System.arraycopy(moduleModel, 0, dest, 0, len);
		moduleModel = dest;
		moduleModel[len] = value;
	}

	/**
	 * 引数のエンティティを持つモジュールモデルを返す。
	 * @param entityName エンティティの名前
	 * @return 該当するモジュールモデル
	 */
	public ModuleModel getModule(String entityName) {

		if (moduleModel != null) {

			ModuleModel ret = null;
	
			for (ModuleModel model : moduleModel) {
				if (model.getModuleInfo().getEntity().equals(entityName)) {
					ret = model;
					break;
				}
			}
			return ret;
		}
		
		return null;
	}

	/**
	 * 引数のエンティティ名を持つモジュール情報を返す。
	 * @param entityName エンティティの名前
	 * @return モジュール情報
	 */
	public ModuleInfoBean[] getModuleInfo(String entityName) {

		if (moduleModel != null) {
			
			ArrayList<ModuleInfoBean> list = new ArrayList<ModuleInfoBean>(2);
			
			for (ModuleModel model : moduleModel) {
	
				if (model.getModuleInfo().getEntity().equals(entityName)) {
					list.add(model.getModuleInfo());
				}
			}
			
			if (list.size() > 0) {
				return  (ModuleInfoBean[])list.toArray(new ModuleInfoBean[list.size()]);
			}
		}
		
		return null;
	}

	/**
	 * 患者モデルを設定する。
	 * @param patientLiteModel 患者モデル
	 */
	public void setPatientLiteModel(PatientLiteModel patientLiteModel) {
		this.patientLiteModel = patientLiteModel;
	}

	/**
	 * 患者モデルを返す。
	 * @return 患者モデル
	 */
	public PatientLiteModel getPatientLiteModel() {
		return patientLiteModel;
	}

	/**
	 * 記載者モデルを設定する。
	 * @param creatorLiteModel 記載者モデル
	 */
	public void setCreatorLiteModel(UserLiteModel creatorLiteModel) {
		this.creatorLiteModel = creatorLiteModel;
	}

	/**
	 * 記載者モデルを返す。
	 * @return 記載者モデル
	 */
	public UserLiteModel getCreatorLiteModel() {
		return creatorLiteModel;
	}
}