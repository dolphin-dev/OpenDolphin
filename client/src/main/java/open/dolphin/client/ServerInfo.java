package open.dolphin.client;


/**
 * ServerInfo
 * 
 * @author Minagawa,Kazushi
 */
public class ServerInfo {

    private String facilityId;
    private String adminId;

    public ServerInfo() {
    }
    /**
	 * @param facilityId The facilityId to set.
	 */

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }
    /**
	 * @return Returns the facilityId.
	 */

    public String getFacilityId() {
        return facilityId;
    }
    /**
	 * @param adminId The adminId to set.
	 */

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }
    /**
	 * @return Returns the adminId.
	 */

    public String getAdminId() {
        return adminId;
    }
}
