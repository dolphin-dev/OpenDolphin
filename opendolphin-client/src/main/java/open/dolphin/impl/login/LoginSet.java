package open.dolphin.impl.login;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class LoginSet {

    private String name;

    private String facilityId;

    private String baseURI;

    private String jmariCode;

    private String claimAddress;

    private int claimPort;

    private String userId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public String getBaseURI() {
        return baseURI;
    }

    public void setBaseURI(String baseURI) {
        this.baseURI = baseURI;
    }

    public String getJmariCode() {
        return jmariCode;
    }

    public void setJmariCode(String jmariCode) {
        this.jmariCode = jmariCode;
    }

    public String getClaimAddress() {
        return claimAddress;
    }

    public void setClaimAddress(String orcaAddress) {
        this.claimAddress = orcaAddress;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getClaimPort() {
        return claimPort;
    }

    public void setClaimPort(int claimPort) {
        this.claimPort = claimPort;
    }

    @Override
    public String toString() {
        return name;
    }

        /**
     * ハッシュ値を返す。
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * 文書IDで eqaul かどうかを返す。
     *
     * @return equal の時 true
     */
    @Override
    public boolean equals(Object other) {
        if (other != null && getClass() == other.getClass()) {
            return getName().equals(((LoginSet) other).getName());
        }
        return false;
    }
}
