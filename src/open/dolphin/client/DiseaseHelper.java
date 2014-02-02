/*
 * DiseaseHelper.java
 * Created on 2004/02/21
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

import open.dolphin.infomodel.Creator;
import open.dolphin.infomodel.DocInfo;
import open.dolphin.infomodel.InfoModel;
import open.dolphin.infomodel.RegisteredDiagnosisModule;

/**
 * @author Kazushi Minagawa, Digital Globe, Inc.
 *
 */
public class DiseaseHelper extends InfoModel {
	
	private String patientId;
	private String confirmDate;
	private Creator creator;
	private DocInfo[] docInfo;
	private RegisteredDiagnosisModule[] registeredDiagnosisModule;
	private String groupId;

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setCreator(Creator creator) {
		this.creator = creator;
	}

	public Creator getCreator() {
		return creator;
	}

	public void setDocInfo(DocInfo[] docInfo) {
		this.docInfo = docInfo;
	}

	public DocInfo[] getDocInfo() {
		return docInfo;
	}

	public void setRegisteredDiagnosisModule(RegisteredDiagnosisModule[] module) {
		this.registeredDiagnosisModule = module;
	}

	public RegisteredDiagnosisModule[] getModule() {
		return registeredDiagnosisModule;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setConfirmDate(String confirmDate) {
		this.confirmDate = confirmDate;
	}

	public String getConfirmDate() {
		return confirmDate;
	}
}
