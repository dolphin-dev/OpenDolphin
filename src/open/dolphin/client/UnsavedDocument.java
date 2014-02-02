/*
 * UnsavedDocument.java
 *
 * Created on 2007/01/15, 20:38
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package open.dolphin.client;

import javax.swing.JCheckBox;

/**
 *
 * @author Minagawa, Kazushi
 */
public class UnsavedDocument {
    
    private int index;
    
    private IChartDocument doc;
    
    private JCheckBox checkBox;
    
    
    /** Creates a new instance of UnsavedDocument */
    public UnsavedDocument() {
    }
    
    public UnsavedDocument(int index, IChartDocument doc) {
        this();
        this.setIndex(index);
        this.setDoc(doc);
        this.checkBox = new JCheckBox(doc.getTitle());
        this.checkBox.setSelected(true);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public IChartDocument getDoc() {
        return doc;
    }

    public void setDoc(IChartDocument doc) {
        this.doc = doc;
    }
    
    public boolean isNeedSave() {
        return checkBox.isSelected();
    }
    
    public JCheckBox getCheckBox() {
        return checkBox;
    }
    
    public String toString() {
        return doc.getTitle();
    }
}
