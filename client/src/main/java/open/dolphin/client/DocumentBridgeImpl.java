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

    // Handle Class
    private String handleClass;
    
    public DocumentBridgeImpl() {
        setTitle(TITLE);
    }
    

    @Override
    public void start() {
        
        scroller = new JScrollPane();
        scroller.getVerticalScrollBar().setUnitIncrement(16);
        getUI().setLayout(new BorderLayout());
        getUI().add(scroller, BorderLayout.CENTER);

        //----------------------------------------
        // 文書履歴のプロパティ通知をリッスンする
        //----------------------------------------
        DocumentHistory h = getContext().getDocumentHistory();
        // 文書種別
        h.addPropertyChangeListener(DocumentHistory.DOCUMENT_TYPE, this);
        // 抽出期間
        h.addPropertyChangeListener(DocumentHistory.HITORY_UPDATED, this);
        // 選択
        h.addPropertyChangeListener(DocumentHistory.SELECTED_HISTORIES, this);
        
        curViwer = createKarteDocumentViewer();
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
    @Override
    public void showDocuments(DocInfoModel[] docs) {
        
        if (docs != null && docs.length > 0) {
            
            String hClass = docs[0].getHandleClass();

            if (hClass!=null && (!hClass.equals(handleClass))) {
                curViwer = null;
                handleClass = hClass;
                try {
                    curViwer = createLetterModuleViewer(handleClass);
                    curViwer.setContext(getContext());
                    curViwer.start();
                } catch (Throwable e) {
                    e.printStackTrace(System.err);
                }
            }

            if (curViwer != null) {
                curViwer.showDocuments(docs, scroller);
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        
        ChartMediator med = this.getContext().getChartMediator();
        med.setCurKarteTransferHandler(null);
        
        String prop = evt.getPropertyName();
        
        if (prop.equals(DocumentHistory.DOCUMENT_TYPE)) {

            // 文書種別が変更された場合
            String docType = (String) evt.getNewValue();
//minagawa^ LSC Test            
//            if (docType.equals(IInfoModel.DOCTYPE_LETTER)) {
//                // 紹介状
//                curViwer = null;
//                handleClass = null;
//                
//            } else if (docType.equals(IInfoModel.DOCTYPE_LETTER_REPLY)) {
//                // 紹介状返書
//                curViwer = null;
//                handleClass = null;
//                
//            } else if (docType.equals(IInfoModel.DOCTYPE_LETTER_REPLY2)) {
//                // 紹介状返書2
//                curViwer = null;
//                handleClass = null;
//                
//            } else if (docType.equals(IInfoModel.DOCTYPE_LETTER_PLUGIN)) {
//                // 紹介状 plugin type
//                curViwer = null;
//                handleClass = null;
//                
//            } else {
//                // カルテ文書
//                curViwer = createKarteDocumentViewer();
//            }  
            // showDocumentsがコールされるまでどのクラスがインスタンス化されるか不明
            // 紹介状等の履歴が空の場合 showDocumentがコールされないので
            // メニューを制御するためここでenterする
            if (docType.equals(IInfoModel.DOCTYPE_LETTER) ||
                docType.equals(IInfoModel.DOCTYPE_LETTER_REPLY) ||
                docType.equals(IInfoModel.DOCTYPE_LETTER_REPLY2) ||
                docType.equals(IInfoModel.DOCTYPE_LETTER_PLUGIN)) {
                curViwer = null;
                handleClass = null;
                this.enter();
//minagawa$                
            } else {
                // カルテ文書
                curViwer = createKarteDocumentViewer();
            }

            if (curViwer!=null) {
                curViwer.setContext(getContext());
                curViwer.start();
            }
            
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

    private KarteDocumentViewer createKarteDocumentViewer() {
        if (curViwer != null) {
            curViwer = null;
        }
        return new KarteDocumentViewer();
    }

    private DocumentViewer createLetterModuleViewer(String handleClass)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        if (curViwer != null) {
            curViwer = null;
        }
        DocumentViewer doc = (DocumentViewer) Class.forName(
                    handleClass).newInstance();
        return doc;
    }
}
