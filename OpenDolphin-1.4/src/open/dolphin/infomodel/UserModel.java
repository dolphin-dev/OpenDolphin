package open.dolphin.infomodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * UserModel
 *
 * @author Minagawa,Kazushi
 *
 */
@Entity
@Table(name="d_users")
public class UserModel extends InfoModel  {
    
    private static final long serialVersionUID = 1646664434908470285L;
    
    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    
    /** composite businnes key */
    @Column(nullable=false, unique=true)
    private String userId;
    
    @Column(nullable=false)
    private String password;
    
    private String sirName;
    
    private String givenName;
    
    @Column(nullable=false)
    private String commonName;
    
    @Embedded
    private LicenseModel licenseModel;
    
    @Embedded
    private DepartmentModel departmentValue;
    
    @Column(nullable=false)
    private String memberType;
    
    private String memo;
    
    @Column(nullable=false)
    @Temporal(value = TemporalType.DATE)
    private Date registeredDate;
    
    @Column(nullable=false)
    private String email;
    
    @ManyToOne
    @JoinColumn(name="facility_id", nullable=false)
    private FacilityModel facility;
    
    @OneToMany(mappedBy="user", cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    private java.util.Collection<RoleModel> roles;
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    /**
     * UserModelオブジェクトを生成する。
     */
    public UserModel(){
    }
    
    /**
     * ユーザIDを設定する。
     * @param userId ユーザID
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    /**
     * ユーザIDを返す。
     * @return ユーザID
     */
    public String getUserId() {
        return userId;
    }
    
    /**
     * 施設IDを除いたIDを返す。
     * @return 施設IDを除いたID
     */
    public String idAsLocal() {
        int index = userId.indexOf(COMPOSITE_KEY_MAKER);
        return userId.substring(index+1);
    }
    
    /**
     * パスワードを設定する。
     * @param password パスワード
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    /**
     * パスワードを返す。
     * @return パスワード
     */
    public String getPassword() {
        return password;
    }
    
    /**
     * SirName を設定する。
     * @param sirName SirName
     */
    public void setSirName(String sirName) {
        this.sirName = sirName;
    }
    
    /**
     * SirName を返す。
     * @return SirName
     */
    public String getSirName() {
        return sirName;
    }
    
    /**
     * GivenName を設定する。
     * @param givenName GivenName
     */
    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }
    
    /**
     * GivenName を返す。
     * @return GivenName
     */
    public String getGivenName() {
        return givenName;
    }
    
    /**
     * フルネームを設定する。
     * @param commonName フルネーム
     */
    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }
    
    /**
     * フルネームを返す。
     * @return フルネーム
     */
    public String getCommonName() {
        return commonName;
    }
    
    /**
     * 医療資格モデルを設定する。
     * @param licenseValue 医療資格モデル
     */
    public void setLicenseModel(LicenseModel licenseValue) {
        this.licenseModel = licenseValue;
    }
    
    /**
     * 医療資格モデルを返す。
     * @return 医療資格モデル
     */
    public LicenseModel getLicenseModel() {
        return licenseModel;
    }
    
    /**
     * 施設モデルを設定する。
     * @param facilityValue 施設モデル
     */
    public void setFacilityModel(FacilityModel facility) {
        this.facility = facility;
    }
    
    /**
     * 施設モデルを返す。
     * @return 施設モデル
     */
    public FacilityModel getFacilityModel() {
        return facility;
    }
    
    /**
     * 診療科モデルを設定する。
     * @param departmentValue 診療科モデル
     */
    public void setDepartmentModel(DepartmentModel departmentValue) {
        this.departmentValue = departmentValue;
    }
    
    /**
     * 診療科モデルを返す。
     * @return 診療科モデル
     */
    public DepartmentModel getDepartmentModel() {
        return departmentValue;
    }
    
    /**
     * ユーザロールを設定する。
     * @param roles ユーザロール
     */
    public void setRoles(Collection<RoleModel> roles) {
        this.roles = roles;
    }
    
    /**
     * ユーザロールを返す。
     * @return ユーザロール
     */
    public Collection<RoleModel> getRoles() {
        return roles;
    }
    
    /**
     * ユーザロールを追加する。
     * @param value ユーザロール
     */
    public void addRole(RoleModel value) {
        
        if (roles == null) {
            roles = new ArrayList<RoleModel>(1);
        }
        roles.add(value);
    }
    
    /**
     * 簡易ユーザ情報を返す。
     * @return 簡易ユーザ情報
     */
    public UserLiteModel getLiteModel() {
        
        UserLiteModel model = new UserLiteModel();
        model.setUserId(getUserId());
        model.setCommonName(getCommonName());
        LicenseModel lm = new LicenseModel();
        lm.setLicense(getLicenseModel().getLicense());
        lm.setLicenseDesc(getLicenseModel().getLicenseDesc());
        lm.setLicenseCodeSys(getLicenseModel().getLicenseCodeSys());
        model.setLicenseModel(lm);
        return model;
    }
    
    /**
     * メンバータイプを設定する。
     * @param memberType メンバータイプ
     */
    public void setMemberType(String memberType) {
        this.memberType = memberType;
    }
    
    /**
     * メンバータイプを返す。
     * @return メンバータイプ
     */
    public String getMemberType() {
        return memberType;
    }
    
    /**
     * このユーザのメモを設定する。
     * @param memo メモ
     */
    public void setMemo(String memo) {
        this.memo = memo;
    }
    
    /**
     * このユーザのメモを返す。
     * @return メモ
     */
    public String getMemo() {
        return memo;
    }
    
    /**
     * 登録日を設定する。
     * @param registeredDate 登録日
     */
    public void setRegisteredDate(Date registeredDate) {
        this.registeredDate = registeredDate;
    }
    
    /**
     * 登録日を返す。
     * @return 登録日
     */
    public Date getRegisteredDate() {
        return registeredDate;
    }
    
    /**
     * 電子メールを設定する。
     * @param email 電子メール
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     * 電子メールを返す。
     * @return  電子メール
     */
    public String getEmail() {
        return email;
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
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final UserModel other = (UserModel) obj;
        if (id != other.id)
            return false;
        return true;
    }
}
