package open.dolphin.converter;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class KeyObjectPair {

    private String key;

    private Object object;

    public KeyObjectPair() {
    }

    public KeyObjectPair(String key, Object object) {
        this();
        this.key = key;
        this.object = object;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
