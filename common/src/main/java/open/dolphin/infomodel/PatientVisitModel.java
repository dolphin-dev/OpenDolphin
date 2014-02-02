package open.dolphin.infomodel;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

/**
 * PatientVisitModel
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
@Entity
@Table(name = "d_patient_visit")
public class PatientVisitModel extends InfoModel implements java.io.Serializable {
    
//masuda^   ChartImplから引っ越し
    // PVT state
    public static final int BIT_OPEN = 0;
    public static final int BIT_SAVE_CLAIM = 1;
    public static final int BIT_MODIFY_CLAIM = 2;
    public static final int BIT_TREATMENT = 3;
    public static final int BIT_HURRY = 4;
    public static final int BIT_GO_OUT = 5;
    public static final int BIT_CANCEL = 6;
    public static final int BIT_UNFINISHED = 8;
//masuda$

    public static final DataFlavor PVT_FLAVOR =
            new DataFlavor(open.dolphin.infomodel.PatientVisitModel.class, "Patient Visit");
    
    public static DataFlavor flavors[] = {PVT_FLAVOR};
    
    // PK
    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    
    // 患者オブジェクト
    @ManyToOne
    @JoinColumn(name="patient_id", nullable=false)
    private PatientModel patient;
    
    // 施設ID Dolphin Table で使用
    @Column(nullable=false)
    private String facilityId;
    
    // 受付リスト上の番号 
    @Transient
    private int number;
    
    // 来院時間 
    @Column(nullable=false)
    private String pvtDate;
    
    // 予約 
    @Transient
    private String appointment;
    
    // 診療科 
    private String department;
    
    // 終了フラグ 
    private int status;
    
    // 健康保険GUID 2006-05-01 
    private String insuranceUid;

    //----------------------------------------------
    // 2.0 で追加
    private String deptCode;        // 診療科コード
    private String deptName;        // 診療科名
    private String doctorId;        // ORCAでの担当医コード
    private String doctorName;      // 担当医名
    private String jmariNumber;     // JMARI code
    private String firstInsurance;  // 受け付けた健康保険
    private String memo;

    @Transient
    private String watingTime;      // 待ち時間
    //----------------------------------------------
//minagawa^ 予定カルテ    (予定カルテ対応)
    @Transient
    private boolean fromSchedule;
    
    @Transient
    private Date lastDocDate;
//minagawa$    
    
    /**
     * PatientVisitModelオブジェクトを生成する。
     */
    public PatientVisitModel() {
    }
//pns^  pvt model の項目を増やす
    @Transient
    private Integer byomei = 0;         // 今まで付いた病名の総数
    @Transient
    private Integer byomeiToday = 0;    // 今日付いた病名の数

    // 病名の総数
    public void setByomeiCount(int bc) {
        byomei = bc;
    }
    public int getByomeiCount() {
        return byomei;
    }
    // 今日付いた病名の数
    public void setByomeiCountToday(int bct) {
        byomeiToday = bct;
    }
    public int getByomeiCountToday() {
        return byomeiToday;
    }
    // 初診かどうか（今日付いた病名がすべての病名かどうか）
    public boolean isShoshin() {
        return (byomei == byomeiToday);
    }
    // 病名がついているかどうか
    public boolean hasByomei() {
        return (getByomeiCount() != 0);
    }
//pns$    
    
//masuda^
    public boolean getStateBit(int stateBit) {
        return (status & (1 << stateBit)) != 0;
    }
    public void setStateBit(int stateBit, boolean flag) {
        if (flag) {
            status |= (1 << stateBit);
        } else {
            status &= ~(1 << stateBit);
        }
    }
//masuda$
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public PatientModel getPatientModel() {
        return patient;
    }
    
    public void setPatientModel(PatientModel patientModel) {
        this.patient = patientModel;
    }
    
    public String getFacilityId() {
        return facilityId;
    }
    
    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }
    
    public void setNumber(int number) {
        this.number = number;
    }
    
    public int getNumber() {
        return number;
    }
    
    public void setPvtDate(String time) {
        this.pvtDate = time;
    }
    
    public String getPvtDate() {
        return pvtDate;
    }
    
    public String getPvtDateTrimTime() {
        return ModelUtils.trimTime(pvtDate);
    }
    
    public String getPvtDateTrimDate() {
        return ModelUtils.trimDate(pvtDate);
    }
    
    public String getAppointment() {
        return appointment;
    }
    
    public void setAppointment(String appointment) {
        this.appointment = appointment;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public String getDepartment() {
//        // 1.3 までの暫定
//        String[] tokens = tokenizeDept(department);
//        return tokens[0];

        // 2.0 から
        return department;
    }
    
    /**
     * department を , で分解する
     */
    private String[] tokenizeDept(String dept) {
        
        // 診療科名、コード、担当医名、担当医コード、JMARI コード
        // を格納する配列を生成する
        String[] ret = new String[5];
        Arrays.fill(ret, null);
        
        if (dept != null) {
            try {
                String[] params = dept.split(",");
                System.arraycopy(params, 0, ret, 0, params.length);
            } catch (Exception e) { 
                e.printStackTrace(System.err);
            }
        }
        
        return ret;
    }
    
    public void setState(int state) {
        this.status = state;
    }
    
    public int getState() {
        return status;
    }
    
    public Integer getStateInteger() {
        return new Integer(status);
    }
    
    public String getPatientId() {
        return getPatientModel().getPatientId();
    }
    
    public String getPatientName() {
        return getPatientModel().getFullName();
    }
    
    public String getPatientGenderDesc() {
        return ModelUtils.getGenderDesc(getPatientModel().getGender());
    }
    
    public String getPatientAgeBirthday() {
        return ModelUtils.getAgeBirthday(getPatientModel().getBirthday());
    }
    
    public String getPatientBirthday() {
        return getPatientModel().getBirthday();
    }

    public void setInsuranceUid(String insuranceUid) {
        this.insuranceUid = insuranceUid;
    }
    
    public String getInsuranceUid() {
        return insuranceUid;
    }

    //-----------------------------------------------------
    public String getDeptName() {
        if (deptName!=null) {
            return deptName;
        }
        // 2.0 以前のレコード
        String[] tokens = tokenizeDept(department);
        return tokens[0];
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getDeptCode() {
        if (deptCode!=null) {
            return deptCode;
        }
        // 2.0 以前のレコード
        String[] tokens = tokenizeDept(department);
        return tokens[1];
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public String getDoctorName() {
        if (doctorName!=null) {
            return doctorName;
        }
        // 2.0 以前のレコード
        String[] tokens = tokenizeDept(department);
        return tokens[2];
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorId() {
        if (doctorId!=null) {
            return doctorId;
        }
        // 2.0 以前のレコード
        String[] tokens = tokenizeDept(department);
        return tokens[3];
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }
    
    public String getJmariNumber() {
        if (jmariNumber!=null) {
            return jmariNumber;
        }
        // 2.0 以前のレコード
        String[] tokens = tokenizeDept(department);
        return tokens[4];
    }

    public void setJmariNumber(String jmariNumber) {
        this.jmariNumber = jmariNumber;
    }

    public String getFirstInsurance() {
        return firstInsurance;
    }

    public void setFirstInsurance(String firstInsurance) {
        this.firstInsurance = firstInsurance;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
//minagawa^ 予定カルテ    (予定カルテ対応)
    public boolean isFromSchedule() {
        return fromSchedule;
    }
    
    public void setFromSchedule(boolean b) {
        fromSchedule = b;
    }
    
    public Date getLastDocDate() {
        return lastDocDate;
    }
    
    public void setLastDocDate(Date d) {
        lastDocDate = d;
    }
//minagawa$    

    //---------------------------------------------------
    // 受け付けた保険を返す
    //---------------------------------------------------
    public String getHealthInsuranceInfo() {

        String uuid = getInsuranceUid();
        if (uuid == null) {
            return null;
        }

        List<PVTHealthInsuranceModel> list = getPatientModel().getPvtHealthInsurances();
        if (list == null || list.isEmpty()) {
            return null;
        }

        StringBuilder info = new StringBuilder();
        for (PVTHealthInsuranceModel pm : list) {
            if (pm.getGUID()!=null && uuid.equals(pm.getGUID())) {

                info.append(pm.getInsuranceClass());
                PVTPublicInsuranceItemModel[] pbs = pm.getPVTPublicInsuranceItem();
                if (pbs!=null) {
                    for (PVTPublicInsuranceItemModel pb : pbs) {
                        info.append(":");
                        info.append(pb.getProviderName());
                    }
                }

                break;
            }
        }

        return info.length()>0 ? info.toString() : null;
    }


    //---------------------------------------------------
    //              待ち時間表示
    //---------------------------------------------------
    public String getWatingTime() {
        return watingTime;
    }

    public void setWatingTime(String watingTime) {
        this.watingTime = watingTime;
    }

    //---------------------------------------------------
    // 診療科、担当医、JMARI コードの情報を返す。 2.0
    //---------------------------------------------------
    public String getDeptDoctorJmariInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append(getDeptName()).append(",");
        sb.append(getDeptCode()).append(",");
        sb.append(getDoctorName()).append(",");
        sb.append(getDoctorId()).append(",");
        sb.append(getJmariNumber());
        return sb.toString();
    }

    //---------------------------------------------------
    //              Transferable 処理
    //---------------------------------------------------
    public boolean isDataFlavorSupported(DataFlavor df) {
        return df.equals(PVT_FLAVOR);
    }

    public Object getTransferData(DataFlavor df) throws UnsupportedFlavorException, IOException {
        if (df.equals(PVT_FLAVOR)) {
            return this;
        } else {
            throw new UnsupportedFlavorException(df);
        }
    }

    public DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + (int) (id ^ (id >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PatientVisitModel other = (PatientVisitModel) obj;
        if (id != other.id) {
            return false;
        }
        return true;
    }
}
