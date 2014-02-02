package open.dolphin.project;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import open.dolphin.client.ClientContext;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class LetterSettingPanel extends AbstractSettingPanel {
    
    private static final String ID = "letterSetting";
    private static final String TITLE = "紹介状等";
//minagawa^ Icon Server    
    //private static final String ICON = "mail_16.gif";
    private static final String ICON = "icon_letter_settings_small";
//minagawa$    
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
        
        bg = new ButtonGroup();
        bg.add(view.getFontSizeSmallRadio());
        bg.add(view.getFontSizeLargeRadio());
        
//s.oh^ 2013/11/26 文書の電話出力対応
        bg = new ButtonGroup();
        bg.add(view.getTelephoneRadio());
        bg.add(view.getNoTelephoneRadio());
//s.oh$
        
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
        
        // 診断書文字サイズ
        if (model.getFontSize().equals("small")) {
            view.getFontSizeSmallRadio().setSelected(true);
        } else if (model.getFontSize().equals("large")) {
             view.getFontSizeLargeRadio().setSelected(true);
        }
        
//s.oh^ 2013/11/26 文書の電話出力対応
        if(model.isTelephoneOutputPdf()) {
            view.getTelephoneRadio().setSelected(true);
        }else{
            view.getNoTelephoneRadio().setSelected(true);
        }
//s.oh$
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
        
        // 診断書フォントサイズ
        if (view.getFontSizeSmallRadio().isSelected()) {
            model.setFontSize("small");
        } else if (view.getFontSizeLargeRadio().isSelected()) {
            model.setFontSize("large");
        }
        
//s.oh^ 2013/11/26 文書の電話出力対応
        if(view.getTelephoneRadio().isSelected()) {
            model.setTelephoneOutputPdf(true);
        }else{
            model.setTelephoneOutputPdf(false);
        }
//s.oh$
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
        private String fontSize;
//s.oh^ 2013/11/26 文書の電話出力対応
        private boolean telephoneOutputPdf;
//s.oh$

        public void populate(ProjectStub stub) {
            setTitle(stub.getString(Project.LETTER_ATESAKI_TITLE));
            setIncludeGreetings(stub.getBoolean(Project.LETTER_INCLUDE_GREETINGS));
            setPrintName(stub.getBoolean(Project.PLAIN_PRINT_PATIENT_NAME));
            String test = stub.getString(Project.LOCATION_PDF);
            if (test==null || test.equals("")) {
                test = ClientContext.getPDFDirectory();
            }
            setPdfDirectory(test);
            setFontSize(stub.getString(Project.SHINDANSYO_FONT_SIZE));
//s.oh^ 2013/11/26 文書の電話出力対応
            setTelephoneOutputPdf(stub.getBoolean(Project.LETTER_TELEPHONE_OUTPUTPDF, true));
//s.oh$
        }

        public void restore(ProjectStub stub) {
            // 宛先敬称
            stub.setString(Project.LETTER_ATESAKI_TITLE, getTitle());
            
            // PDF印刷時に挨拶文を含めるかどうか
            stub.setBoolean(Project.LETTER_INCLUDE_GREETINGS, isIncludeGreetings());
            
            // PLAIN文書印刷で患者氏名を印刷するかどうか
            stub.setBoolean(Project.PLAIN_PRINT_PATIENT_NAME, isPrintName());
            
            // PDFの出力ディレクトリー
            stub.setString(Project.LOCATION_PDF, getPdfDirectory());
            
            // 診断書のフォントサイズ
            stub.setString(Project.SHINDANSYO_FONT_SIZE, getFontSize());
            
//s.oh^ 2013/11/26 文書の電話出力対応
            stub.setBoolean(Project.LETTER_TELEPHONE_OUTPUTPDF, isTelephoneOutputPdf());
//s.oh
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

        public String getFontSize() {
            return fontSize;
        }

        public void setFontSize(String fontSize) {
            this.fontSize = fontSize;
        }   
        
//s.oh^ 2013/11/26 文書の電話出力対応
        public boolean isTelephoneOutputPdf() {
            return telephoneOutputPdf;
        }
        
        public void setTelephoneOutputPdf(boolean output) {
            this.telephoneOutputPdf = output;
        }
//s.oh$
    }
}
