package open.dolphin.infomodel;

import java.util.Date;

/**
 * 
 * @author kazushi Minagawa.
 */
public class PostSchedule {
    
    private long pvtPK;
    
    private long ptPK;
    
    private Date scheduleDate;
    
    private long phPK;
    
    private boolean sendClaim;

    public long getPvtPK() {
        return pvtPK;
    }

    public void setPvtPK(long pvtPK) {
        this.pvtPK = pvtPK;
    }

    public long getPtPK() {
        return ptPK;
    }

    public void setPtPK(long ptPK) {
        this.ptPK = ptPK;
    }

    public Date getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(Date scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public long getPhPK() {
        return phPK;
    }

    public void setPhPK(long phPK) {
        this.phPK = phPK;
    }

    public boolean getSendClaim() {
        return sendClaim;
    }

    public void setSendClaim(boolean sendClaim) {
        this.sendClaim = sendClaim;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("pvtPK=").append(pvtPK).append(",");
        sb.append("pvPK=").append(ptPK).append(",");
        sb.append("scheduleDate=").append(scheduleDate).append(",");
        sb.append("phPK=").append(phPK).append(",");
        sb.append("sendClaim=").append(sendClaim);
        return sb.toString();
    }
}
