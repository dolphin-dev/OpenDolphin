package open.dolphin.infomodel;


/**
 * AddressModel
 * 
 * 
 * @author Minagawa,kazushi
 */
public class AddressModel extends InfoModel {

	private static final long serialVersionUID = 4602230572833538876L;

	private String addressType;

	private String addressTypeDesc;

	private String addressTypeCodeSys;

	private String countryCode;

	private String zipCode;

	private String address;

	/**
	 * ‘ƒR[ƒh‚ğİ’è‚·‚éB
	 * 
	 * @param countryCode
	 *            ‘ƒR[ƒh
	 */
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	/**
	 * ‘ƒR[ƒh‚ğ•Ô‚·B
	 * 
	 * @return ‘ƒR[ƒh
	 */
	public String getCountryCode() {
		return countryCode;
	}

	/**
	 * —X•Ö”Ô†‚ğİ’è‚·‚éB
	 * 
	 * @param zipCode
	 *            —X•Ö”Ô†
	 */
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	/**
	 * —X•Ö”Ô†‚ğ•Ô‚·B
	 * 
	 * @return —X•Ö”Ô†
	 */
	public String getZipCode() {
		return zipCode;
	}

	/**
	 * ZŠ‚ğİ’è‚·‚éB
	 * 
	 * @param address
	 *            ZŠ
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * ZŠ‚ğ•Ô‚·B
	 * 
	 * @return ZŠ
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * ZŠ‹æ•ª‚ğİ’è‚·‚éB
	 * 
	 * @param addressType
	 *            ZŠ‹æ•ª
	 */
	public void setAddressType(String addressType) {
		this.addressType = addressType;
	}

	/**
	 * ZŠ‹æ•ª‚ğ•Ô‚·B
	 * 
	 * @return ZŠ‹æ•ª
	 */
	public String getAddressType() {
		return addressType;
	}

	/**
	 * ZŠ‹æ•ªà–¾‚ğİ’è‚·‚éB
	 * 
	 * @param addressTypeDesc
	 *            ZŠ‹æ•ªà–¾
	 */
	public void setAddressTypeDesc(String addressTypeDesc) {
		this.addressTypeDesc = addressTypeDesc;
	}

	/**
	 * ZŠ‹æ•ªà–¾‚ğ•Ô‚·B
	 * 
	 * @return ZŠ‹æ•ªà–¾
	 */
	public String getAddressTypeDesc() {
		return addressTypeDesc;
	}

	/**
	 * ZŠ‹æ•ª‘ÌŒn‚ğİ’è‚·‚éB
	 * 
	 * @param addressTypeCodeSys
	 *            ZŠ‹æ•ª‘ÌŒn
	 */
	public void setAddressTypeCodeSys(String addressTypeCodeSys) {
		this.addressTypeCodeSys = addressTypeCodeSys;
	}

	/**
	 * ZŠ‹æ•ª‘ÌŒn‚ğ•Ô‚·B
	 * 
	 * @return ZŠ‹æ•ª‘ÌŒn
	 */
	public String getAddressTypeCodeSys() {
		return addressTypeCodeSys;
	}
}
