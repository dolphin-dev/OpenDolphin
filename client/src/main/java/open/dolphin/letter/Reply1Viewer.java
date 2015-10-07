package open.dolphin.letter;

import java.awt.FlowLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import open.dolphin.client.Chart;
import open.dolphin.client.ChartImpl;
import open.dolphin.client.ClientContext;
import open.dolphin.client.DocumentViewer;
import open.dolphin.delegater.LetterDelegater;
import open.dolphin.helper.DBTask;
import open.dolphin.infomodel.DocInfoModel;
import open.dolphin.infomodel.LetterModule;

/**
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class Reply1Viewer extends Reply1Impl implements DocumentViewer {

    private long docPK;
    
    @Override
    public void start() {
        stateMgr = new LetterStateMgr(this);
        this.enter();
    }

    @Override
    public void historyPeriodChanged() {
        stateMgr.processEmptyEvent();
        getContext().showDocument(0);
    }

    @Override
    public void showDocuments(DocInfoModel[] docs, JScrollPane scroller) {

        if (docs == null || docs.length == 0) {
            stateMgr.processEmptyEvent();
            return;
        }

        DocInfoModel docInfo = docs[0];
        docPK = docInfo.getDocPk();

        if (docPK == 0L) {
            return;
        }

        LetterGetTask task = new LetterGetTask(getContext(), docPK, scroller);

        task.execute();
    }
    
    public void modifyKarte() {
        if (docPK==0L) {
            return;
        }
        
        DBTask task = new DBTask<LetterModule,Void>(getContext()) {

            @Override
            protected Object doInBackground() throws Exception {
                LetterDelegater ddl = new LetterDelegater();
                LetterModule letter = ddl.getLetter(docPK);
                return letter;
            }
            
            @Override
            protected void succeeded(LetterModule lm) {
                if (lm==null) {
                    return;
                }
                Reply1Impl editor = new Reply1Impl();
                editor.setModel(lm);
                editor.setModify(true);
                editor.setContext(getContext());
                editor.start();
                ChartImpl chart = (ChartImpl)getContext();
                StringBuilder sb = new StringBuilder();
                sb.append(ClientContext.getMyBundle(Reply1Viewer.class).getString("modify")).append("(").append(editor.getTitle()).append(")");
                chart.addChartDocument(editor, sb.toString());
            }
        };
        task.execute();      
    }
    
//s.oh^ 2014/04/03 文書の複製
    public void copyDocument() {
        if (docPK==0L) {
            return;
        }
        
        DBTask task = new DBTask<LetterModule,Void>(getContext()) {

            @Override
            protected Object doInBackground() throws Exception {
                LetterDelegater ddl = new LetterDelegater();
                LetterModule letter = ddl.getLetter(docPK);
                letter.setId(0);
                letter.setLinkId(0);
                return letter;
            }
            
            @Override
            protected void succeeded(LetterModule lm) {
                if (lm==null) {
                    return;
                }
                Reply1Impl editor = new Reply1Impl();
                editor.setModel(lm);
                editor.setModify(true);
                editor.setContext(getContext());
                editor.start();
                ChartImpl chart = (ChartImpl)getContext();
                StringBuilder sb = new StringBuilder();
                sb.append(ClientContext.getMyBundle(Reply1Viewer.class).getString("dupuricate")).append("(").append(editor.getTitle()).append(")");
                chart.addChartDocument(editor, sb.toString());
            }
        };
        task.execute();
    }
//s.oh$

    class LetterGetTask extends DBTask<LetterModule, Void> {

        private final long letterPk;
        private final JScrollPane scroller;

        public LetterGetTask(Chart app, long letterPk, JScrollPane scroller) {
            super(app);
            this.letterPk = letterPk;
            this.scroller = scroller;
        }

        @Override
        protected LetterModule doInBackground() throws Exception {
            LetterDelegater ddl = new LetterDelegater();
            LetterModule letter = ddl.getLetter(letterPk);
            return letter;
        }

        @Override
        protected void succeeded(LetterModule letter) {
            model = letter;
            modelToView(model);
            setEditables(false);            
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
            p.add(view);
            scroller.setViewportView(p);  
            stateMgr.processCleanEvent();
            getContext().showDocument(0);
        }
    }
}
