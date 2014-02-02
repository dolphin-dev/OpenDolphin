package open.dolphin.infomodel;

import java.io.Serializable;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class LabTestValueObject implements Serializable {

    private String value;

    private String out;

    private String comment1;

    private String comment2;

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the out
     */
    public String getOut() {
        return out;
    }

    /**
     * @param out the out to set
     */
    public void setOut(String out) {
        this.out = out;
    }

    /**
     * @return the comment1
     */
    public String getComment1() {
        return comment1;
    }

    /**
     * @param comment1 the comment1 to set
     */
    public void setComment1(String comment1) {
        this.comment1 = comment1;
    }

    /**
     * @return the comment2
     */
    public String getComment2() {
        return comment2;
    }

    /**
     * @param comment2 the comment2 to set
     */
    public void setComment2(String comment2) {
        this.comment2 = comment2;
    }

    public String concatComment() {

        StringBuilder sb = new StringBuilder();
        if (getComment1() != null) {
            sb.append(getComment1());
            sb.append(" ");
        }

        if (getComment2() != null) {
            sb.append(getComment2());
        }

        return sb.toString();
    }

}
