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

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 診断履歴クラス。
 *
 * @author Kazushi Minagawa, Digital Globe,Inc.
 */
@Entity
@Table(name = "d_diagnosis")
public class RegisteredDiagnosisModel extends KarteEntryBean {
    
    private static final long serialVersionUID = 8449675831667704574L;
    
    // 疾患名
    @Column(nullable=false)
    private String diagnosis;
    
    // 疾患コード
    private String diagnosisCode;
    
    // 疾患コード体系名
    private String diagnosisCodeSystem;
    
    // 病名分類モデル
    @Embedded
    private DiagnosisCategoryModel diagnosisCategoryModel;
    
    // 転帰モデル
    @Embedded
    private DiagnosisOutcomeModel diagnosisOutcomeModel;
    
    // 疾患の初診日
    private String firstEncounterDate;
    
    // 関連健康保険情報
    private String relatedHealthInsurance;
    
//    // 疾患開始日
//    private String startDate;
//    
//    // 疾患終了日
//    private String endDate;
    
//    private String firstConfirmDate;
//    
//    private String confirmDate;
    
    @Transient
    private PatientLiteModel patientLiteModel;
    
    @Transient
    private UserLiteModel userLiteModel;
    
    
    /**
     * Creates new RegisteredDiagnosisModule
     */
    public RegisteredDiagnosisModel() {
    }
    
    /**
     * 有効なモデルかどうかを返す。
     * @return 有効なモデルの時 true
     */
    public boolean isValidMML() {
        return getDiagnosis() != null ? true : false;
    }
    
    /**
     * 疾患名を返す。
     * @return  疾患名
     */
    public String getDiagnosis() {
        return diagnosis;
    }
    
    /**
     * 疾患名を設定する。
     * @param diagnosis  疾患名
     */
    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }
    
    /**
     * 疾患コードを返す。
     * @return 疾患コード
     */
    public String getDiagnosisCode() {
        return diagnosisCode;
    }
    
    /**
     * 疾患コードを設定する。
     * @param diagnosisCode 疾患コード
     */
    public void setDiagnosisCode(String diagnosisCode) {
        this.diagnosisCode = diagnosisCode;
    }
    
    /**
     * 疾患コード体系名を返す。
     * @return 疾患コード体系名
     */
    public String getDiagnosisCodeSystem() {
        return diagnosisCodeSystem;
    }
    
    /**
     * 疾患コード体系名を設定する。
     * @param diagnosisCodeSystem 疾患コード体系名
     */
    public void setDiagnosisCodeSystem(String diagnosisCodeSystem) {
        this.diagnosisCodeSystem = diagnosisCodeSystem;
    }
    
    /**
     * 分類名を返す。
     * @return 分類名
     */
    public String getCategory() {
        return diagnosisCategoryModel != null ? diagnosisCategoryModel.getDiagnosisCategory() : null;
    }
    
    /**
     * 分類名を設定する。
     * @param category 分類名
     */
    public void setCategory(String category) {
        if (diagnosisCategoryModel == null) {
            diagnosisCategoryModel = new DiagnosisCategoryModel();
        }
        this.diagnosisCategoryModel.setDiagnosisCategory(category);
    }
    
    /**
     * 分類説明を返す。
     * @return 分類説明
     */
    public String getCategoryDesc() {
        return diagnosisCategoryModel != null ? diagnosisCategoryModel.getDiagnosisCategoryDesc() : null;
    }
    
    /**
     * 分類説明を設定する。
     * @param categoryDesc 分類説明
     */
    public void setCategoryDesc(String categoryDesc) {
        if (diagnosisCategoryModel == null) {
            diagnosisCategoryModel = new DiagnosisCategoryModel();
        }
        this.diagnosisCategoryModel.setDiagnosisCategoryDesc(categoryDesc);
    }
    
    /**
     * 分類体系名を返す。
     * @return 分類体系名
     */
    public String getCategoryCodeSys() {
        return diagnosisCategoryModel != null ? diagnosisCategoryModel.getDiagnosisCategoryCodeSys() : null;
    }
    
    /**
     * 分類体系名を設定する。
     * @param categoryTable 分類体系名
     */
    public void setCategoryCodeSys(String categoryTable) {
        if (diagnosisCategoryModel == null) {
            diagnosisCategoryModel = new DiagnosisCategoryModel();
        }
        this.diagnosisCategoryModel.setDiagnosisCategoryCodeSys(categoryTable);
    }
    
    /**
     * 疾患初診日を返す。
     * @return 疾患初診日
     */
    public String getFirstEncounterDate() {
        return firstEncounterDate;
    }
    
    /**
     * 疾患初診日を設定する。
     * @param firstEncounterDate 疾患初診日
     */
    public void setFirstEncounterDate(String firstEncounterDate) {
        this.firstEncounterDate = firstEncounterDate;
    }
    
    /**
     * 疾患開始日を返す。
     * @return 疾患開始日
     */
    public String getStartDate() {
        if (getStarted() != null) {
            return ModelUtils.getDateAsString(getStarted());
        }
        return null;
    }
    
    /**
     * 疾患開始日を設定する。
     * @param startDate 疾患開始日
     */
    public void setStartDate(String startDate) {
        if (startDate != null) {
            int index = startDate.indexOf('T');
            if (index < 0) {
                startDate += "T00:00:00";
            }
            //System.out.println(startDate);
            setStarted(ModelUtils.getDateTimeAsObject(startDate));
        }
    }
    
    /**
     * 疾患終了日を返す。
     * @return 疾患終了日
     */
    public String getEndDate() {
        if (getEnded() != null) {
            return ModelUtils.getDateAsString(getEnded());
        }
        return null;
    }
    
    /**
     * 疾患終了日を設定する。
     * @param endDate 疾患終了日
     */
    public void setEndDate(String endDate) {
        if (endDate != null) {
            int index = endDate.indexOf('T');
            if (index < 0) {
                endDate += "T00:00:00";
            }
            setEnded(ModelUtils.getDateTimeAsObject(endDate));
        }
    }
    
    /**
     * 転帰を返す。
     * @return 転帰
     */
    public String getOutcome() {
        return diagnosisOutcomeModel != null ? diagnosisOutcomeModel.getOutcome() : null;
    }
    
    /**
     * 転帰を設定する。
     * @param outcome 転帰
     */
    public void setOutcome(String outcome) {
        if (diagnosisOutcomeModel == null) {
            diagnosisOutcomeModel = new DiagnosisOutcomeModel();
        }
        this.diagnosisOutcomeModel.setOutcome(outcome);
    }
    
    /**
     * 転帰説明を返す。
     * @return 転帰説明
     */
    public String getOutcomeDesc() {
        return diagnosisOutcomeModel != null ? diagnosisOutcomeModel.getOutcomeDesc() : null;
    }
    
    /**
     * 転帰説明を設定する。
     * @param outcomeDesc 転帰説明を設定
     */
    public void setOutcomeDesc(String outcomeDesc) {
        if (diagnosisOutcomeModel == null) {
            diagnosisOutcomeModel = new DiagnosisOutcomeModel();
        }
        this.diagnosisOutcomeModel.setOutcomeDesc(outcomeDesc);
    }
    
    /**
     * 転帰体系を返す。
     * @return 転帰体系
     */
    public String getOutcomeCodeSys() {
        return diagnosisOutcomeModel != null ? diagnosisOutcomeModel.getOutcomeCodeSys() : null;
    }
    
    /**
     * 転帰体系を設定する。
     * @param outcomeTable
     */
    public void setOutcomeCodeSys(String outcomeTable) {
        if (diagnosisOutcomeModel == null) {
            diagnosisOutcomeModel = new DiagnosisOutcomeModel();
        }
        this.diagnosisOutcomeModel.setOutcomeCodeSys(outcomeTable);
    }
    
    /**
     * 関連健康保険情報を返す。
     * @return 関連健康保険情報
     */
    public String getRelatedHealthInsurance() {
        return relatedHealthInsurance;
    }
    
    /**
     * 関連健康保険情報を設定する。
     * @param relatedHealthInsurance 関連健康保険情報
     */
    public void setRelatedHealthInsurance(String relatedHealthInsurance) {
        this.relatedHealthInsurance = relatedHealthInsurance;
    }
    
    /**
     * カテゴリモデルを返す。
     * @return カテゴリモデル
     */
    public DiagnosisCategoryModel getDiagnosisCategoryModel() {
        return diagnosisCategoryModel;
    }
    
    /**
     * カテゴリモデルを設定する。
     * @param diagnosisOutcomeModel カテゴリモデル
     */
    public void setDiagnosisCategoryModel(
            DiagnosisCategoryModel diagnosisCategoryModel) {
        this.diagnosisCategoryModel = diagnosisCategoryModel;
    }
    
    /**
     * 転帰モデルを設定する。
     * @return 転帰モデル
     */
    public DiagnosisOutcomeModel getDiagnosisOutcomeModel() {
        return diagnosisOutcomeModel;
    }
    
    /**
     * 転帰モデルを返す。
     * @param diagnosisOutcomeModel 転帰モデル
     */
    public void setDiagnosisOutcomeModel(
            DiagnosisOutcomeModel diagnosisOutcomeModel) {
        this.diagnosisOutcomeModel = diagnosisOutcomeModel;
    }
    
//    public String getConfirmDate() {
//        return confirmDate;
//    }
//    
//    public void setConfirmDate(String confirmDate) {
//        this.confirmDate = confirmDate;
//    }
//    
//    public String getFirstConfirmDate() {
//        return firstConfirmDate;
//    }
//    
//    public void setFirstConfirmDate(String firstConfirmDate) {
//        this.firstConfirmDate = firstConfirmDate;
//    }
    
    public PatientLiteModel getPatientLiteModel() {
        return patientLiteModel;
    }
    
    public void setPatientLiteModel(PatientLiteModel patientLiteModel) {
        this.patientLiteModel = patientLiteModel;
    }
    
    public UserLiteModel getUserLiteModel() {
        return userLiteModel;
    }
    
    public void setUserLiteModel(UserLiteModel userLiteModel) {
        this.userLiteModel = userLiteModel;
    }
}