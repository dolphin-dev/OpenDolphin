
/*
 * Created on 2004/02/03
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
 * Copyright (C) 2003 Digital Globe, Inc. All rights reserved.
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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.StringTokenizer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * PatientVisitModel
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
@Entity
@Table(name = "d_patient_visit")
public class PatientVisitModel extends InfoModel  {
    
    private static final long serialVersionUID = 7049490761810599245L;
    
    public static final DataFlavor PVT_FLAVOR =
            new DataFlavor(open.dolphin.infomodel.PatientVisitModel.class, "Patient Visit");
    
    public static DataFlavor flavors[] = {PVT_FLAVOR};
    
    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    
    /** 患者 */
    @ManyToOne
    @JoinColumn(name="patient_id", nullable=false)
    private PatientModel patient;
    
    /** 施設ID  */
    @Column(nullable=false)
    private String facilityId;
    
    /** 受付リスト上の番号 */
    @Transient
    private int number;
    
    /** 来院時間 */
    @Column(nullable=false)
    private String pvtDate;
    
    /** 予約 */
    @Transient
    private String appointment;
    
    /** 診療科 */
    private String department;
    
    /** 終了フラグ */
    private int status;
    
    /** 健康保険GUID 2006-05-01 */
    private String insuranceUid;
    
    /**
     * PatientVisitModelオブジェクトを生成する。
     */
    public PatientVisitModel() {
    }
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    /**
     * 患者モデルを返す。
     * @return 患者モデル
     */
    public PatientModel getPatient() {
        return patient;
    }
    
    /**
     * 患者モデルを設定する。
     * @param patientModel
     */
    public void setPatient(PatientModel patientModel) {
        this.patient = patientModel;
    }
    
    /**
     * 施設IDを返す。
     * @return 施設ID
     */
    public String getFacilityId() {
        return facilityId;
    }
    
    /**
     * 施設IDを設定する。
     * @param facilityId 施設ID
     */
    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }
    
    
    /**
     * リスト番号を設定する。
     * @param number リスト番号
     */
    public void setNumber(int number) {
        this.number = number;
    }
    
    /**
     * リスト番号を返す。
     * @return リスト番号
     */
    public int getNumber() {
        return number;
    }
    
    /**
     * 来院日時を設定する。
     * @param time 来院日時 yyyy-MM-ddTHH:mmss
     */
    public void setPvtDate(String time) {
        this.pvtDate = time;
    }
    
    /**
     * 来院日時を返す。
     * @return 来院日時 yyyy-MM-ddTHH:mmss
     */
    public String getPvtDate() {
        return pvtDate;
    }
    
    /**
     * 来院日時の日付部分を返す。
     * @return 来院日時の日付部分
     */
    public String getPvtDateTrimTime() {
        return ModelUtils.trimTime(pvtDate);
    }
    
    /**
     * 来院日時の時間部分を返す。
     * @return 来院日時の時間部分
     */
    public String getPvtDateTrimDate() {
        return ModelUtils.trimDate(pvtDate);
    }
    
    public String getAppointment() {
        return appointment;
    }
    
    /**
     * 予約があるかどうかを設定する。
     * @param appointment 予約がある時 true
     */
    public void setAppointment(String appointment) {
        this.appointment = appointment;
    }
    
    /**
     * 受付診療科を設定する。
     * @param department 受付診療科
     */
    public void setDepartment(String department) {
        this.department = department;
    }
    
    /**
     * 受付診療科を返す。
     * @return 受付診療科名
     */
    public String getDepartment() {
        // 1.3 までの暫定
        String[] tokens = tokenizeDept(department);
        return tokens[0];
    }
    
    /**
     * 受付診療科コードを返す。
     * @return 受付診療科
     */
    public String getDepartmentCode() {
        // 1.3 までの暫定
        String[] tokens = tokenizeDept(department);
        return tokens[1];
    }
    
    /**
     * 担当医を返す。
     * @return 担当医名
     */
    public String getAssignedDoctorName() {
        // 1.3 までの暫定
        String[] tokens = tokenizeDept(department);
        return tokens[2];
    }
    
    /**
     * 担当医IDを返す。
     * @return 担当医ID
     */
    public String getAssignedDoctorId() {
        // 1.3 までの暫定
        String[] tokens = tokenizeDept(department);
        return tokens[3];
    }
    
    /**
     * JMARI コードを返す。
     * @return JMARI コード
     */
    public String getJmariCode() {
        // 1.3 までの暫定
        String[] tokens = tokenizeDept(department);
        return tokens[4];
    }
    
    public String getDeptNoTokenize() {
        return department;
    }
    
    /**
     * department を , で分解する
     */
    private String[] tokenizeDept(String dept) {
        
        // 診療科名、コード、担当医名、担当医コード、JMARI コード
        // を格納する配列を生成する
        String[] ret = new String[5];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = null;
        }
        
        if (dept != null) {
            int index = 0; 
            try {
                StringTokenizer st = new StringTokenizer(dept, ",");
                while (st.hasMoreTokens()) {
                    ret[index++] = st.nextToken();
                }
            } catch (Exception e) { 
                e.printStackTrace();
            }
        }
        
        return ret;
    }
    
    /**
     * カルテの状態を設定する。
     * @param state カルテの状態
     */
    public void setState(int state) {
        this.status = state;
    }
    
    /**
     * カルテの状態を返す。
     * @return カルテの状態
     */
    public int getState() {
        return status;
    }
    
    public Integer getStateInteger() {
        return new Integer(status);
    }
    
    /**
     * 患者IDを返す。
     * @return 患者ID
     */
    public String getPatientId() {
        return getPatient().getPatientId();
    }
    
    /**
     * 患者氏名を返す。
     * @return 患者氏名
     */
    public String getPatientName() {
        return getPatient().getFullName();
    }
    
    /**
     * 患者性別説明を返す。
     * @return 性別説明
     */
    public String getPatientGenderDesc() {
        return ModelUtils.getGenderDesc(getPatient().getGender());
    }
    
    /**
     * 患者の年齢と生年月日の表示を返す。
     * @return 患者の年齢と生年月日
     */
    public String getPatientAgeBirthday() {
        return ModelUtils.getAgeBirthday(getPatient().getBirthday());
    }
    
    /**
     * 患者の生年月日の表示を返す。
     * @return 患者の年齢と生年月日
     */
    public String getPatientBirthday() {
        return getPatient().getBirthday();
    }
    
    public String getPatientAge() {
        return ModelUtils.getAge(getPatient().getBirthday());
    }
    
    /**
     * 健康保険情報モジュールのUUIDを設定する。
     * @param insuranceUid 健康保険情報モジュールのUUID
     */
    public void setInsuranceUid(String insuranceUid) {
        this.insuranceUid = insuranceUid;
    }
    
    /**
     * 健康保険情報モジュールのUUIDを返す。
     * @return 健康保険情報モジュールのUUID
     */
    public String getInsuranceUid() {
        return insuranceUid;
    }
    
    /////////////////// Transferable 処理 //////////////////////////
    
    public boolean isDataFlavorSupported(DataFlavor df) {
        return df.equals(PVT_FLAVOR);
    }
    
    public Object getTransferData(DataFlavor df) throws UnsupportedFlavorException, IOException {
        if (df.equals(PVT_FLAVOR)) {
            return this;
        } else throw new UnsupportedFlavorException(df);
    }
    
    public DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
