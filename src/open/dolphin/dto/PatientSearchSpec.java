package open.dolphin.dto;


/**
 * PatientSearchSpec
 * 
 * @author Minagawa,Kazushi
 */
public class PatientSearchSpec extends DolphinDTO {
	
	private static final long serialVersionUID = -3192512318678902328L;
	
	public static final int ALL_SEARCH 		= 0;
	public static final int ID_SEARCH 		= 1;
	public static final int NAME_SEARCH 		= 2;
	public static final int KANA_SEARCH 		= 3;
	public static final int ROMAN_SEARCH 		= 4;
	public static final int TELEPHONE_SEARCH 	= 5;
	public static final int ZIPCODE_SEARCH          = 6;
	public static final int ADDRESS_SEARCH          = 7;
	public static final int EMAIL_SEARCH 		= 8;
	public static final int OTHERID_SEARCH          = 9;
	public static final int DIGIT_SEARCH            = 10;
        public static final int DATE_SEARCH             = 11;
	
	private int code;
	private String patientId;
	private String name;
	private String kana;
	private String roman;
	private String telephone;
	private String zipCode;
	private String address;
	private String email;
	private String otherId;
	private String otherIdClass;
	private String otherIdCodeSys;
	private String digit;
        

	/**
	 * @param code The code to set.
	 */
	public void setCode(int code) {
		this.code = code;
	}

	/**
	 * @return Returns the code.
	 */
	public int getCode() {
		return code;
	}

	/**
	 * @param patientId The patientId to set.
	 */
	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	/**
	 * @return Returns the patientId.
	 */
	public String getPatientId() {
		return patientId;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param kana The kana to set.
	 */
	public void setKana(String kana) {
		this.kana = kana;
	}

	/**
	 * @return Returns the kana.
	 */
	public String getKana() {
		return kana;
	}

	/**
	 * @param roman The roman to set.
	 */
	public void setRoman(String roman) {
		this.roman = roman;
	}

	/**
	 * @return Returns the roman.
	 */
	public String getRoman() {
		return roman;
	}

	/**
	 * @param telephone The telephone to set.
	 */
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	/**
	 * @return Returns the telephone.
	 */
	public String getTelephone() {
		return telephone;
	}

	/**
	 * @param zipCode The zipCode to set.
	 */
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	/**
	 * @return Returns the zipCode.
	 */
	public String getZipCode() {
		return zipCode;
	}

	/**
	 * @param address The address to set.
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return Returns the address.
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param email The email to set.
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return Returns the email.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param otherId The otherId to set.
	 */
	public void setOtherId(String otherId) {
		this.otherId = otherId;
	}

	/**
	 * @return Returns the otherId.
	 */
	public String getOtherId() {
		return otherId;
	}

	/**
	 * @param otherIdClass The otherIdClass to set.
	 */
	public void setOtherIdClass(String otherIdClass) {
		this.otherIdClass = otherIdClass;
	}

	/**
	 * @return Returns the otherIdClass.
	 */
	public String getOtherIdClass() {
		return otherIdClass;
	}

	/**
	 * @param otherIdCodeSys The otherIdCodeSys to set.
	 */
	public void setOtherIdCodeSys(String otherIdCodeSys) {
		this.otherIdCodeSys = otherIdCodeSys;
	}

	/**
	 * @return Returns the otherIdCodeSys.
	 */
	public String getOtherIdCodeSys() {
		return otherIdCodeSys;
	}

	/**
	 * @param digit The digit to set.
	 */
	public void setDigit(String digit) {
		this.digit = digit;
	}

	/**
	 * @return Returns the digit.
	 */
	public String getDigit() {
		return digit;
	}
}
