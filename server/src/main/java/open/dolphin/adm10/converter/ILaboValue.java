/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.adm10.converter;

/**
 *
 * @author kazushi
 */
public class ILaboValue implements java.io.Serializable {
    
    private String sampleDate;
    private String value;
    private String outFlag;
    private String comment1;
    private String comment2;

    public String getSampleDate() {
        return sampleDate;
    }

    public void setSampleDate(String sampleDate) {
        this.sampleDate = sampleDate;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getOutFlag() {
        return outFlag;
    }

    public void setOutFlag(String outFlag) {
        this.outFlag = outFlag;
    }

    public String getComment1() {
        return comment1;
    }

    public void setComment1(String comment1) {
        this.comment1 = comment1;
    }

    public String getComment2() {
        return comment2;
    }

    public void setComment2(String comment2) {
        this.comment2 = comment2;
    }
}
