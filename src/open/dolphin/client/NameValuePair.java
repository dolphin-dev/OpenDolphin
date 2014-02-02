package open.dolphin.client;

/**
 * @author Kazushi Minagawa Digital Globe, Inc.
 *
 */
public class NameValuePair {
    
    private String value;
    private String name;
    
    public static int getIndex(NameValuePair test, NameValuePair[] cnArray) {
        int index = 0;
        for (int i = 0; i < cnArray.length; i++) {
            if (test.equals(cnArray[i])) {
                index = i;
                break;
            }
        }
        return index;
    }
    
    public static int getIndex(String test, NameValuePair[] cnArray) {
        int index = 0;
        for (int i = 0; i < cnArray.length; i++) {
            if (test.equals(cnArray[i].getValue())) {
                index = i;
                break;
            }
        }
        return index;
    }
    
    public NameValuePair() {
    }
    
    public NameValuePair(String name, String value) {
        this();
        setName(name);
        setValue(value);
    }
    
    public void setValue(String code) {
        this.value = code;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    @Override
    public int hashCode() {
        return value.hashCode() + 15;
    }
    
    @Override
    public boolean equals(Object other) {
        if (other != null && getClass() == other.getClass()) {
            String otherValue = ((NameValuePair)other).getValue();
            return value.equals(otherValue);
        }
        return false;
    }
    
    public int compareTo(Object other) {
        if (other != null && getClass() == other.getClass()) {
            String otherValue = ((NameValuePair)other).getValue();
            return value.compareTo(otherValue);
        }
        return -1;
    }
}
