package open.dolphin.adm10.converter;

import open.dolphin.infomodel.ChartEventModel;

/**
 *
 * @author kazushi Minagawa
 */
public class IChartEvent implements java.io.Serializable {
    
    private String issuerUUID;
    
    private int eventType;
    
    private long pvtPk;
    
    private int state;
    
    private String memo;
    
    private String ownerUUID;
    
    private String facilityId;
    
    private long ptPk;

    public String getIssuerUUID() {
        return issuerUUID;
    }

    public void setIssuerUUID(String issuerUUID) {
        this.issuerUUID = issuerUUID;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public long getPvtPk() {
        return pvtPk;
    }

    public void setPvtPk(long pvtPk) {
        this.pvtPk = pvtPk;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getOwnerUUID() {
        return ownerUUID;
    }

    public void setOwnerUUID(String ownerUUID) {
        this.ownerUUID = ownerUUID;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public long getPtPk() {
        return ptPk;
    }

    public void setPtPk(long ptPk) {
        this.ptPk = ptPk;
    }
    
    public ChartEventModel toModel() {
        ChartEventModel ret = new ChartEventModel();
        ret.setEventType(this.getEventType());
        ret.setFacilityId(this.getFacilityId());
        ret.setIssuerUUID(this.getIssuerUUID());
        ret.setMemo(this.getMemo());
        ret.setOwnerUUID(this.getOwnerUUID());
        ret.setPtPk(this.getPtPk());
        ret.setPvtPk(this.getPvtPk());
        ret.setState(this.getState());        
        return ret;
    }
}
