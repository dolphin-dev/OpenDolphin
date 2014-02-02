package open.dolphin.infomodel;

/**
 * ProgressCourse
 * 
 * @author Kazushi Minagawa, Digital Globe, Inc.
 *
 */
public class ProgressCourse extends InfoModel {

    String freeText;

    public void setFreeText(String freeText) {
        this.freeText = freeText;
    }

    public String getFreeText() {
        return freeText;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        ProgressCourse ret = new ProgressCourse();
        ret.setFreeText(this.getFreeText());
        return ret;
    }
}
