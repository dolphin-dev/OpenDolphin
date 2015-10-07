/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.adm10.converter;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author kazushi
 */
public class ILaboGraphItem {
    
    private String itemCode;
    private String itemName;
    private String normalValue;
    private String unit;
    private List<ILaboValue> results;

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getNormalValue() {
        return normalValue;
    }

    public void setNormalValue(String normalValue) {
        this.normalValue = normalValue;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public List<ILaboValue> getResults() {
        return results;
    }

    public void setResults(List<ILaboValue> results) {
        this.results = results;
    }
    
    public void addValue(ILaboValue value) {
        if (results==null) {
            results = new ArrayList();
        }
        results.add(value);
    }
}
