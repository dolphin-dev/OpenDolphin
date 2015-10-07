package open.dolphin.project;

/**
 *
 * @author Kazushi Minagawa
 */
public interface SettingBean {
    
    public String[] propertyOrder();
    
    public boolean isTagProperty(String property);
    
    public String[] getTags(String property);
    
    public boolean isDirectoryProperty(String property);
    
    public boolean isSpinnerProperty(String property);
    
    public int[] getSpinnerSpec(String property);
    
    public boolean isDecimalProperty(String property);
    
    // Return true if bean has valid data set
    public boolean isValidBean();
    
    // Set beans' value from the setting file
    public void populate();
    
    // Store propertis to the setting file
    public void store();
    
}
