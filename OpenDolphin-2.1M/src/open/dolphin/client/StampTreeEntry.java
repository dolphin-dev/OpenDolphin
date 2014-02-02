package open.dolphin.client;

/**
 * @author Kazushi Minagawa, Digital Globe, Inc.
 *
 */
public class StampTreeEntry {

    private String userId;
    private String id;
    private boolean use;
    private int number;
    private String treeXml;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setUse(boolean use) {
        this.use = use;
    }

    public boolean isUse() {
        return use;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public void setTreeXml(String treeXml) {
        this.treeXml = treeXml;
    }

    public String getTreeXml() {
        return treeXml;
    }
}
