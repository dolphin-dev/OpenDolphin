package open.dolphin.project;

import java.beans.PropertyEditorSupport;

/**
 *
 * @author Kazushi Minagawa
 */
public class SpinnerEditorSupport extends PropertyEditorSupport {
    
    private int value;
    private int minValue;
    private int maxValue;
    private int stepValue;
    
    @Override
    public Object getValue() {
        return value;
    }
    
    @Override
    public void setValue(Object o) {
        value = (Integer)o;
    }
    
    @Override
    public String getAsText() {
        return String.valueOf(value);
    }
    
    @Override
    public void setAsText(String text) {
        value = Integer.parseInt(text);
    }

    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public int getStepValue() {
        return stepValue;
    }

    public void setStepValue(int stepValue) {
        this.stepValue = stepValue;
    }
}
