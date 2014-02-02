package open.dolphin.infomodel;

/**
 * DiseaseEntry
 * 
 * @author  Minagawa, Kazushi
 */
public final class DiseaseEntry extends MasterEntry {

    private static final long serialVersionUID = 9088599523647351403L;
	
    private String icdTen;

    /** Creates a new instance of DeseaseEntry */
    public DiseaseEntry() {
    }

    public String getIcdTen() {
        return icdTen;
    }

    public void setIcdTen(String val) {
        icdTen = val;
    }
    
    @Override
    public boolean isInUse() {
        if (disUseDate != null) {
            return refDate.compareTo(disUseDate) <=0 ? true : false;
        }
        return false;
    }
}
