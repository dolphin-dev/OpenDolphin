package open.dolphin.infomodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class LabTestRowObject implements Serializable, Comparable {

    private String labCode;

    private String groupCode;

    private String parentCode;

    private String itemCode;

    private String normalValue;

    private String itemName;

    private String unit;

    private List<LabTestValueObject> values;

    public String getLabCode() {
        return labCode;
    }

    public void setLabCode(String labCode) {
        this.labCode = labCode;
    }

    /**
     * @return the groupCode
     */
    public String getGroupCode() {
        return groupCode;
    }

    /**
     * @param groupCode the groupCode to set
     */
    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    /**
     * @return the parentCode
     */
    public String getParentCode() {
        return parentCode;
    }

    /**
     * @param parentCode the parentCode to set
     */
    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    /**
     * @return the itemCode
     */
    public String getItemCode() {
        return itemCode;
    }

    /**
     * @param itemCode the itemCode to set
     */
    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    /**
     * @return the normalValue
     */
    public String getNormalValue() {
        return normalValue;
    }

    /**
     * @param normalValue the normalValue to set
     */
    public void setNormalValue(String normalValue) {
        this.normalValue = normalValue;
    }

    /**
     * @return the itemName
     */
    public String getItemName() {
        return itemName;
    }

    /**
     * @param itemName the itemName to set
     */
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    /**
     * @return the unit
     */
    public String getUnit() {
        return unit;
    }

    /**
     * @param unit the unit to set
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String nameWithUnit() {

        StringBuilder sb = new StringBuilder();
        sb.append(getItemName());
        if (getUnit()!=null) {
            sb.append("(");
            sb.append(getUnit());
            sb.append(")");
        }
        return sb.toString();
    }

    /**
     * @return the values
     */
    public List<LabTestValueObject> getValues() {
        return values;
    }

    /**
     * @param values the values to set
     */
    public void setValues(List<LabTestValueObject> values) {
        this.values = values;
    }

    public void addLabTestValueObjectAt(int index, LabTestValueObject value) {

        if (values == null) {
            values = new ArrayList<LabTestValueObject>(5);
            for (int i = 0; i < 5; i++) {
                values.add(null);
            }
        }
        values.add(index, value);
    }

    public LabTestValueObject getLabTestValueObjectAt(int index) {

        if (getValues() == null || index < 0 || index > getValues().size() -1) {
            return null;
        }

        return getValues().get(index);
    }

    @Override
    public int compareTo(Object o) {

        if (o != null && getClass() == o.getClass()) {

            LabTestRowObject other = (LabTestRowObject) o;

            StringBuilder sb = new StringBuilder();
            sb.append(getLabCode());
            sb.append(getGroupCode());
            sb.append(getParentCode());
            sb.append(getItemCode());
            String str1 = sb.toString();

            sb = new StringBuilder();
            sb.append(other.getLabCode());
            sb.append(other.getGroupCode());
            sb.append(other.getParentCode());
            sb.append(other.getItemCode());
            String str2 = sb.toString();

            return str1.compareTo(str2);
        }
        
        return -1;
    }

    public String toClipboard() {

        if (values==null || values.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(itemName);

        for (LabTestValueObject val : values) {
            if (val != null && val.getValue() != null) {
                sb.append(",").append(val.getValue());
                if (val.getOut()!=null) {
                    sb.append(",").append(val.getOut());
                }
                if (unit!=null) {
                    sb.append(",").append(unit);
                }
                sb.append(",").append(val.getSampleDate());
            }
        }

        return sb.toString();
    }

    public String toClipboardLatest() {

        if (values==null || values.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(itemName);

        int last = values.size() -1;
        LabTestValueObject test = null;
        for (int i=last; i > -1; i--) {
            test = values.get(i);
            if (test!=null && test.getValue()!=null) {
                break;
            }
        }

        if (test == null) {
            return null;
        }

        sb.append(",").append(test.getValue());
        if (test.getOut()!=null) {
            sb.append(",").append(test.getOut());
        }
        if (unit!=null) {
            sb.append(",").append(unit);
        }
        sb.append(",").append(test.getSampleDate());

        return sb.toString();
    }
}
