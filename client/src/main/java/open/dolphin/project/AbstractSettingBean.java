package open.dolphin.project;

import open.dolphin.util.ZenkakuUtils;

/**
 *
 * @author Kazushi Minagawa
 */
public abstract class AbstractSettingBean implements SettingBean {

    @Override
    public abstract String[] propertyOrder();

    @Override
    public boolean isTagProperty(String property) {
        return false;
    }

    @Override
    public String[] getTags(String property) {
        return null;
    }

    @Override
    public boolean isDirectoryProperty(String property) {
        return false;
    }

    @Override
    public boolean isSpinnerProperty(String property) {
        return false;
    }
    
    @Override
    public int[] getSpinnerSpec(String property) {
        return null;
    }
    
    @Override
    public boolean isDecimalProperty(String property) {
        return false;
    }
    
    @Override
    public boolean isValidBean() {
        return false;
    }

    @Override
    public abstract void populate();

    @Override
    public abstract void store();
    
    protected String arrayValueFromBoolean(boolean b, String[] tags) {
        if (tags!=null && tags.length==2) {
            return b ? tags[0] : tags[1];
        }
        return null;
    }
    
    protected int findIndex(String value, String[] array) {
        if (value==null || array==null) {
            return 0;
        }
        int index=0;
        for (String test : array) {
            if (test.equals(value)) {
                break;
            } 
            index++;
        }
        return (index>=0 && index<array.length) ? index : 0;
    }
    
    protected boolean notEmpty(String test) {
        return (test!=null && !"".equals(test));
    }
    
    protected String decimalStringByCheck(String test) {
        String ret;
        try {
            Float.parseFloat(test);
            ret = ZenkakuUtils.toHalfNumber(test);
        } catch (Exception e) {
            ret = "0";
        }
        return ret;
    }
}
