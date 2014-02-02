package open.dolphin.client;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class UpdateObject {

    // JAR ファイル名
    private String name;

    // Path
    private String localPath;

    // クライアントマシンに保存されている lastModified
    private long localLast;

    // 更新サーバーに置かれている lastModified
    private long remoteLast;

    // コンテント長
    private int contentLength;

    // バイトデータ
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

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
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
