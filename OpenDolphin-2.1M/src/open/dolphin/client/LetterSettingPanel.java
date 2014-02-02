package open.dolphin.client;

import javax.swing.ButtonGroup;
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
        setUI(view);
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

        public void populate(ProjectStub stub) {
            setTitle(stub.getString("letter.atesaki.title"));
            setIncludeGreetings(stub.getBoolean("letter.greetings.include"));
            setPrintName(stub.getBoolean("plain.print.patinet.name"));
        }

        public void restore(ProjectStub stub) {
            stub.setString("letter.atesaki.title", getTitle());
            stub.setBoolean("letter.greetings.include", isIncludeGreetings());
            stub.setBoolean("plain.print.patinet.name", isPrintName());
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
    }
}
