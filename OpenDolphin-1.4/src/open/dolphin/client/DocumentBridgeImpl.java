package open.dolphin.client;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JScrollPane;
import open.dolphin.infomodel.DocInfoModel;
import open.dolphin.infomodel.IInfoModel;

/**
 * 参照タブ画面を提供する Bridge クラス。このクラスの scroller へ
 * カルテ、紹介状等のどきゅめんとが表示される。
 * 
 * @author kazushi Minagawa, Digital Globe, Inc.
 */
public class DocumentBridgeImpl extends AbstractChartDocument 
    implements PropertyChangeListener, DocumentBridger {
    
    private static final String TITLE = "参 照";
        
    // 文書表示クラスのインターフェイス
    private DocumentViewer curViwer;
    
    // Scroller  
    private JScrollPane scroller;
    
    public DocumentBridgeImpl() {
        setTitle(TITLE);
    }
    

    @Override
    public void start() {
        
        scroller = new JScrollPane();
        getUI().setLayout(new BorderLayout());
        getUI().add(scroller, BorderLayout.CENTER);
        
        // 文書履歴のプロパティ通知をリッスンする
        DocumentHistory h = getContext().getDocumentHistory();
        h.addPropertyChangeListener(DocumentHistory.DOCUMENT_TYPE, this);
        h.addPropertyChangeListener(DocumentHistory.HITORY_UPDATED, this);
        h.addPropertyChangeListener(DocumentHistory.SELECTED_HISTORIES, this);
        
        curViwer = new KarteDocumentViewer();
        curViwer.setContext(getContext());
        curViwer.start();
            
        enter();
    }

    @Override
    public void stop() {  
        if (curViwer != null) {
            curViwer.stop();
        }
    }
    
    @Override
    public void enter() {
        if (curViwer != null) {
            // これによりメニューは viwer で制御される
            curViwer.enter();
        } else {
            super.enter();
        }
    }
    
    /**
     * Bridge 機能を提供する。選択された文書のタイプに応じてビューへブリッジする。
     * @param docs 表示する文書の DocInfo 配列
     */
    public void showDocuments(DocInfoModel[] docs) {
        
        if (docs == null || docs.length == 0) {
            return;
        }
        
        if (curViwer != null) {
            //getContext().showDocument(0);
            curViwer.showDocuments(docs, scroller);
            //getContext().showDocument(0);
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        
        ChartMediator med = this.getContext().getChartMediator();
        med.setCurKarteComposit(null);
        
        String prop = evt.getPropertyName();
        
        if (prop.equals(DocumentHistory.DOCUMENT_TYPE)) {
            
            String docType = (String) evt.getNewValue();
            
            if (docType.equals(IInfoModel.DOCTYPE_LETTER)) {
                curViwer = new LetterViewer();
                
            } else if (docType.equals(IInfoModel.DOCTYPE_LETTER_REPLY)) {
                curViwer = new LetterReplyViewer();
                
            } else if (docType.equals(IInfoModel.DOCTYPE_LETTER_REPLY2)) {
                curViwer = new LetterReplyViewer2();
                
            } else {
                curViwer = new KarteDocumentViewer();
            }
            
            curViwer.setContext(getContext());
            curViwer.start();
            
        } else if (prop.equals(DocumentHistory.HITORY_UPDATED)) {
            // 文書履歴の抽出期間が変更された場合
            if (curViwer != null) {
                curViwer.historyPeriodChanged();
            }
            this.scroller.setViewportView(null);
            
        } else if (prop.equals(DocumentHistory.SELECTED_HISTORIES)) {
            
            // 文書履歴の選択が変更された場合
            DocInfoModel[] selectedHistoroes = (DocInfoModel[]) evt.getNewValue();
            this.showDocuments(selectedHistoroes);
        }
    }
    
    public KarteViewer getBaseKarte() {
        if (curViwer != null && curViwer instanceof KarteDocumentViewer) {
            return ((KarteDocumentViewer) curViwer).getBaseKarte();
        }
        return null;
    }
}
