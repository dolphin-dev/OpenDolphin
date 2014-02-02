package open.dolphin.infomodel;

/**
 * AddressModel
 * 
 * 
 * @author Minagawa,kazushi
 */
public class AddressModel extends InfoModel {

    private String addressType;
    private String addressTypeDesc;
    private String addressTypeCodeSys;
    private String countryCode;
    private String zipCode;
    private String address;

    /**
     * 国コードを設定する。
     * 
     * @param countryCode
     *            国コード
     */
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    /**
     * 国コードを返す。
     * 
     * @return 国コード
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * 郵便番号を設定する。
     * 
     * @param zipCode
     *            郵便番号
     */
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    /**
     * 郵便番号を返す。
     * 
     * @return 郵便番号
     */
    public String getZipCode() {
        return zipCode;
    }

    /**
     * 住所を設定する。
     * 
     * @param address
     *            住所
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * 住所を返す。
     * 
     * @return 住所
     */
    public String getAddress() {
        return address;
    }

    /**
     * 住所区分を設定する。
     * 
     * @param addressType
     *            住所区分
     */
    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    /**
     * 住所区分を返す。
     * 
     * @return 住所区分
     */
    public String getAddressType() {
        return addressType;
    }

    /**
     * 住所区分説明を設定する。
     * 
     * @param addressTypeDesc
     *            住所区分説明
     */
    public void setAddressTypeDesc(String addressTypeDesc) {
        this.addressTypeDesc = addressTypeDesc;
    }

    /**
     * 住所区分説明を返す。
     * 
     * @return 住所区分説明
     */
    public String getAddressTypeDesc() {
        return addressTypeDesc;
    }

    /**
     * 住所区分体系を設定する。
     * 
     * @param addressTypeCodeSys
     *            住所区分体系
     */
    public void setAddressTypeCodeSys(String addressTypeCodeSys) {
        this.addressTypeCodeSys = addressTypeCodeSys;
    }

    /**
     * 住所区分体系を返す。
     * 
     * @return 住所区分体系
     */
    public String getAddressTypeCodeSys() {
        return addressTypeCodeSys;
    }
}
