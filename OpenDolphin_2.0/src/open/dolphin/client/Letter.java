package open.dolphin.client;

import open.dolphin.infomodel.LetterModule;

/**
 * 新規文書インターフェイス。
 * 
 * @author Kazushi Minagawa. Digital Globe, Inc.
 */
public interface Letter extends NChartDocument {

    public void modelToView(LetterModule model);

    public void viewToModel();

    public void setEditables(boolean b);

    public void setListeners();

    public boolean letterIsDirty();

    public void makePDF();
    
}
