package open.dolphin.infomodel;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class SampleDateComparator implements java.util.Comparator {

    @Override
    public int compare(Object o1, Object o2) {

        NLaboModule m1 = (NLaboModule) o1;
        NLaboModule m2 = (NLaboModule) o2;
        
        int result = m1.getSampleDate().compareTo(m2.getSampleDate());
        
        if (result==0) {
            String key1 = m1.getModuleKey();
            String key2 = m2.getModuleKey();
            if (key1!=null && key2!=null) {
                return key1.compareTo(key2);
            }
        } 
        
        return m1.getSampleDate().compareTo(m2.getSampleDate());
    }
}
