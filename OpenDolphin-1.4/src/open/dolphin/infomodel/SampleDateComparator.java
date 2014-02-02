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

        return m1.getSampleDate().compareTo(m2.getSampleDate());
    }
}
