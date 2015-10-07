package open.dolphin.adm10.converter;

import open.dolphin.infomodel.ProgressCourse;

/**
 * ProgressCourse
 * 
 * @author Kazushi Minagawa, Digital Globe, Inc.
 *
 */
public class IProgressCourse implements java.io.Serializable {

    private String freeText;
    
    public void setFreeText(String freeText) {
        this.freeText = freeText;
    }

    public String getFreeText() {
        return freeText;
    }
    
    public void fromModel(ProgressCourse model) {
        this.freeText = model.getFreeText();
    }
    
    public ProgressCourse toModel() {
        if (getFreeText()!=null && getFreeText().length()>0) {
            ProgressCourse ret = new ProgressCourse();
            ret.setFreeText(this.getFreeText());
            return ret;
        }
        return null;
    }
    
    public void toSoaSpec() {
        if (freeText!=null) {
            StringBuilder sb = new StringBuilder();
            sb.append("<section>");
            sb.append("<paragraph>");
            sb.append("<content>");
            sb.append("<text>");
            sb.append(freeText);
            sb.append("</text>");
            sb.append("</content>");
            sb.append("</paragraph>");
            sb.append("</section>");
            freeText = sb.toString();
        }
    }
}
