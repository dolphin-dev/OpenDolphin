package open.dolphin.client;

/**
 *
 * @author kazm
 */
public class UpdateObject {
    
    private String name;
    private long localLast = 0L;
    private long remoteLast = 0L;
    private int contentLength;
    private byte[] bytes;
    
    public UpdateObject(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getLocalLast() {
        return localLast;
    }

    public void setLocalLast(long localLast) {
        this.localLast = localLast;
    }

    public long getRemoteLast() {
        return remoteLast;
    }

    public void setRemoteLast(long remoteLast) {
        this.remoteLast = remoteLast;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public boolean isNew() {
        return remoteLast > localLast ? true : false;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }
}
