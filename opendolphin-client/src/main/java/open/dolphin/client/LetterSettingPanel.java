package open.dolphin.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import open.dolphin.project.Project;
import open.dolphin.project.ProjectStub;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class LetterSettingPanel extends AbstractSettingPanel {
    
    private static final String ID = "letterSetting";
    private static final String TITLE = "紹介状等";
    private static final String ICON = "mail_16.gif";

    private LetterSettingView view;
    private LetterSettingModel model;

    public LetterSettingPanel() {
        this.setId(ID);
        this.setTitle(TITLE);
        this.setIcon(ICON);
    }

    private void initComponents() {
        view = new LetterSettingView();
        ButtonGroup bg = new ButtonGroup();
        bg.add(view.getOnkikaRadio());
        bg.add(view.getOnjishiTitleRadio());
        bg.add(view.getNoTitleRadio());
        bg = new ButtonGroup();
        bg.add(view.getPrintRadio());
        bg.add(view.getNoPrintRadio());
        
        // PDF
        view.getPdfBtn().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                choosePDFDirectory();
            }
        });
        
        setUI(view);
    }
    
    private void choosePDFDirectory() {

        try {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            String baseDir = view.getPdfFld().getText().trim();
            if (baseDir != null && (!baseDir.equals(""))) {
                File f = new File(baseDir);
                chooser.setSelectedFile(f);
            }
            int returnVal = chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                view.getPdfFld().setText(chooser.getSelectedFile().getPath());
            }

        } catch (Exception ex) {
            logger.warn(ex);
        }
    }

    private void bindModelToView() {

        // 敬称
        String title = model.getTitle();
        if (title.equals("御机下")) {
            view.getOnkikaRadio().setSelected(true);
        } else if (title.equals("御侍史")) {
            view.getOnjishiTitleRadio().setSelected(true);
        } else {
            view.getNoTitleRadio().setSelected(true);
        }

        // 挨拶
        view.getGreetingsChk().setSelected(model.isIncludeGreetings());

        // 患者氏名印刷
        if (model.isPrintName()) {
            view.getPrintRadio().setSelected(true);
        } else {
            view.getNoPrintRadio().setSelected(true);
        }
        
        // PDF dir
        if (model.getPdfDirectory()!=null) {
            view.getPdfFld().setText(model.getPdfDirectory());
        }
    }

    private void bindViewToModel() {

        // 敬称
        if (view.getOnkikaRadio().isSelected()) {
            model.setTitle("御机下");
        } else if (view.getOnjishiTitleRadio().isSelected()) {
            model.setTitle("御侍史");
        } else {
            model.setTitle("無し");
        }

        // 挨拶
        model.setIncludeGreetings(view.getGreetingsChk().isSelected());

        // 患者氏名印刷
        if (view.getPrintRadio().isSelected()) {
            model.setPrintName(true);
        } else {
            model.setPrintName(false);
        }
        
        // PDF directory
        String dir = view.getPdfFld().getText().trim();
        if (dir==null || dir.equals("")) {
            dir = ClientContext.getPDFDirectory();
        }
        model.setPdfDirectory(dir);
    }

    @Override
    public void start() {
        model = new LetterSettingModel();
        model.populate(getProjectStub());
        initComponents();
        bindModelToView();
    }

    @Override
    public void save() {
        bindViewToModel();
        model.restore(getProjectStub());
    }

    class LetterSettingModel {

        private String title;
        private boolean includeGreetings;
        private boolean printName;
        private String pdfDirectory;

        public void populate(ProjectStub stub) {
            setTitle(stub.getString("letter.atesaki.title"));
            setIncludeGreetings(stub.getBoolean("letter.greetings.include"));
            setPrintName(stub.getBoolean("plain.print.patinet.name"));
            String test = stub.getString(Project.LOCATION_PDF);
            if (test==null || test.equals("")) {
                test = ClientContext.getPDFDirectory();
            }
            setPdfDirectory(test);
        }

        public void restore(ProjectStub stub) {
            stub.setString("letter.atesaki.title", getTitle());
            stub.setBoolean("letter.greetings.include", isIncludeGreetings());
            stub.setBoolean("plain.print.patinet.name", isPrintName());
            stub.setString(Project.LOCATION_PDF, getPdfDirectory());
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public boolean isIncludeGreetings() {
            return includeGreetings;
        }

        public void setIncludeGreetings(boolean includeGreetings) {
            this.includeGreetings = includeGreetings;
        }

        public boolean isPrintName() {
            return printName;
        }

        public void setPrintName(boolean printName) {
            this.printName = printName;
        }
        
        public String getPdfDirectory() {
            return pdfDirectory;
        }

        public void setPdfDirectory(String dir) {
            this.pdfDirectory = dir;
        }
    }
}
