package open.dolphin.letter;

import javax.swing.JScrollPane;
import open.dolphin.client.Chart;
import open.dolphin.client.DocumentViewer;
import open.dolphin.delegater.LetterDelegater;
import open.dolphin.helper.DBTask;
import open.dolphin.infomodel.DocInfoModel;
import open.dolphin.infomodel.LetterModule;

/**
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class Reply1Viewer extends Reply1Impl implements DocumentViewer {

    @Override
    public void start() {
        stateMgr = new LetterStateMgr(this);
        this.enter();
    }

    @Override
    public void historyPeriodChanged() {
        stateMgr.processEmptyEvent();
    }

    @Override
    public void showDocuments(DocInfoModel[] docs, JScrollPane scroller) {

        if (docs == null || docs.length == 0) {
            stateMgr.processEmptyEvent();
            return;
        }

        DocInfoModel docInfo = docs[0];
        long pk = docInfo.getDocPk();

        if (pk == 0L) {
            return;
        }

        LetterGetTask task = new LetterGetTask(getContext(), pk, scroller);

        task.execute();
    }

    class LetterGetTask extends DBTask<LetterModule, Void> {

        private long letterPk;
        private JScrollPane scroller;

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
            scroller.setViewportView(view);
            stateMgr.processCleanEvent();
        }
    }
}
