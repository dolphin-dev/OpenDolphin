package open.dolphin.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import open.dolphin.helper.UserDocumentHelper;
import open.dolphin.infomodel.DocumentModel;
import open.dolphin.letter.KartePDFMaker;
import open.dolphin.project.Project;

/**
 *
 * @author kazushi Minagawa
 */
public class KartePDFSender implements IKarteSender {
    
    private Chart context;
    private boolean send;
    private String pathToOutput;

    @Override
    public Chart getContext() {
        return context;
    }

    @Override
    public void setContext(Chart context) {
        this.context = context;
    }

    @Override
    public void prepare(DocumentModel data) {
        send = Project.getBoolean(Project.KARTE_PDF_SEND_AT_SAVE);
        if (!send) {
            return;
        }
        // 出力先を取得
        String outputDir = Project.getString(Project.KARTE_PDF_SEND_DIRECTORY);
        String title = "カルテ";
        String ext = ".pdf";
        String ptName = context.getPatient().getFullName();
        ptName = ptName.replace(" ", "").replace("　", "");
        Date d = new Date();
        pathToOutput = UserDocumentHelper.createPathToDocument(outputDir, title, ext, ptName, d);
    }

    @Override
    public void send(DocumentModel data) {
        if (send) {
            List<DocumentModel> list = new ArrayList<DocumentModel>(1);
            list.add(data);
            KartePDFMaker maker = new KartePDFMaker();
            maker.setContext(getContext());
            maker.setDocumentList(list);
            maker.setAscending(true);
            maker.makePDF(pathToOutput);
        }
    }
}
