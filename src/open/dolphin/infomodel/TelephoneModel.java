package open.dolphin.infomodel;

/**
 * TelephoneModel
 * 
 * @author Minagawa,Kazushi
 * 
 */
public class TelephoneModel  extends InfoModel {

	private static final long serialVersionUID = -3520256828672499135L;

	private String telephoneType;

	private String telephoneTypeDesc;

	private String telephoneTypeCodeSys;

	private String country;

	private String area;

	private String city;

	private String number;

	// Ç≥Ç∑Ç™Ç…ïsóvÇ≈ÇÕÅH
	private String extension;

	private String memo;

	public TelephoneModel() {
	}

	/**
	 * @param telephoneClass
	 *            The telephoneClass to set.
	 */
	public void setTelephoneType(String telephoneClass) {
		this.telephoneType = telephoneClass;
	}

	/**
	 * @return Returns the telephoneClass.
	 */
	public String getTelephoneType() {
		return telephoneType;
	}

	/**
	 * @param telephoneClassDesc
	 *            The telephoneClassDesc to set.
	 */
	public void setTelephoneTypeDesc(String telephoneClassDesc) {
		this.telephoneTypeDesc = telephoneClassDesc;
	}

	/**
	 * @return Returns the telephoneClassDesc.
	 */
	public String getTelephoneTypeDesc() {
		return telephoneTypeDesc;
	}

	/**
	 * @param telephoneClassCodeSys
	 *            The telephoneClassCodeSys to set.
	 */
	public void setTelephoneTypeCodeSys(String telephoneClassCodeSys) {
		this.telephoneTypeCodeSys = telephoneClassCodeSys;
	}

	/**
	 * @return Returns the telephoneClassCodeSys.
	 */
	public String getTelephoneTypeCodeSys() {
		return telephoneTypeCodeSys;
	}

	/**
	 * @param country
	 *            The country to set.
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @return Returns the country.
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param area
	 *            The area to set.
	 */
	public void setArea(String area) {
		this.area = area;
	}

	/**
	 * @return Returns the area.
	 */
	public String getArea() {
		return area;
	}

	/**
	 * @param city
	 *            The city to set.
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return Returns the city.
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param number
	 *            The number to set.
	 */
	public void setNumber(String number) {
		this.number = number;
	}

	/**
	 * @return Returns the number.
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * @param extension
	 *            The extension to set.
	 */
	public void setExtension(String extension) {
		this.extension = extension;
	}

	/**
	 * @return Returns the extension.
	 */
	public String getExtension() {
		return extension;
	}

	/**
	 * @param memo
	 *            The memo to set.
	 */
	public void setMemo(String memo) {
		this.memo = memo;
	}

	/**
	 * @return Returns the memo.
	 */
	public String getMemo() {
		return memo;
	}
}
