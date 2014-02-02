/*
 * RegisteredDiagnosisModule.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
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


/**
 * 診断履歴クラス。
 *
 * @author  Kazushi Minagawa, Digital Globe,Inc.
 */
public class RegisteredDiagnosisModule extends InfoModel {
   
   private // 疾患名
   String diagnosis;
   
   private // 疾患コード
   String diagnosisCode;
   
   private // 疾患コード体系名
   String diagnosisCodeSystem;
   
   private // 診断名の分類
   String category;
   
   private // 分類テーブル
   String categoryTable;
   
   private // 開始日
   String startDate;
   
   private // 終了日
   String endDate;
   
   private // 転帰
   String outcome;
   
   private // 転帰テーブル名
   String outcomeTable;
   
   private // 疾患の初診日
   String firstEncounterDate;
   
   private // 関連健康保険情報
   String relatedHealthInsurance;
   
   /** Creates new RegisteredDiagnosisModule */
   public RegisteredDiagnosisModule() {
   }
      
   // isValid?
   public boolean isValidMML() {
      return getDiagnosis() != null ? true : false;
   }

	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}
	
	public String getDiagnosis() {
		return diagnosis;
	}
	
	public void setDiagnosisCode(String diagnosisCode) {
		this.diagnosisCode = diagnosisCode;
	}
	
	public String getDiagnosisCode() {
		return diagnosisCode;
	}
	
	public void setDiagnosisCodeSystem(String diagnosisCodeSystem) {
		this.diagnosisCodeSystem = diagnosisCodeSystem;
	}
	
	public String getDiagnosisCodeSystem() {
		return diagnosisCodeSystem;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
	public String getCategory() {
		return category;
	}
	
	public void setCategoryTable(String categoryTable) {
		this.categoryTable = categoryTable;
	}
	
	public String getCategoryTable() {
		return categoryTable;
	}
	
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	
	public String getStartDate() {
		return startDate;
	}
	
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	
	public String getEndDate() {
		return endDate;
	}
	
	public void setOutcome(String outcome) {
		this.outcome = outcome;
	}
	
	public String getOutcome() {
		return outcome;
	}
	
	public void setOutcomeTable(String outcomeTable) {
		this.outcomeTable = outcomeTable;
	}
	
	public String getOutcomeTable() {
		return outcomeTable;
	}
	
	public void setFirstEncounterDate(String firstEncounterDate) {
		this.firstEncounterDate = firstEncounterDate;
	}
	
	public String getFirstEncounterDate() {
		return firstEncounterDate;
	}
	
	public void setRelatedHealthInsurance(String relatedHealthInsurance) {
		this.relatedHealthInsurance = relatedHealthInsurance;
	}
	
	public String getRelatedHealthInsurance() {
		return relatedHealthInsurance;
	}
}