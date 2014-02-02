package open.dolphin.client;

import javax.swing.JCheckBox;

/**
 *
 * @author Minagawa, Kazushi. Digital Globe, Inc.
 */
public final class UnsavedDocument {

    private int index;
    private ChartDocument doc;
    private JCheckBox checkBox;

    /** Creates a new instance of UnsavedDocument */
    public UnsavedDocument() {
    }

    public UnsavedDocument(int index, ChartDocument doc) {
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

    public ChartDocument getDoc() {
        return doc;
    }

    public void setDoc(ChartDocument doc) {
        this.doc = doc;
    }

    public boolean isNeedSave() {
        return checkBox.isSelected();
    }

    public JCheckBox getCheckBox() {
        return checkBox;
    }

    @Override
    public String toString() {
        return doc.getTitle();
    }
}
