package open.dolphin.infomodel;

/**
 * ProgressCourse
 * 
 * @author Kazushi Minagawa, Digital Globe, Inc.
 *
 */
public class ProgressCourse extends InfoModel {

    private static final long serialVersionUID = -8741238869253717241L;
    String freeText;

    public void setFreeText(String freeText) {
        this.freeText = freeText;
    }

    public String getFreeText() {
        return freeText;
    }
}
