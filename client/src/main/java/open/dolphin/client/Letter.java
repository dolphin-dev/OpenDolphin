package open.dolphin.client;

import open.dolphin.infomodel.LetterModule;

/**
 * 新規文書インターフェイス。
 * 
 * @author Kazushi Minagawa. Digital Globe, Inc.
 */
public interface Letter extends NChartDocument {

    public void modelToView(LetterModule model);

//minagawa^ LSC 1.4 bug fix 文書の印刷日付 2013/06/24
    //public void viewToModel();
    public void viewToModel(boolean print);
//minagawa$    

    public void setEditables(boolean b);

    public void setListeners();

    public boolean letterIsDirty();

    public void makePDF();
    
}
