package open.dolphin.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.MouseListener;
import java.awt.print.PageFormat;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;
import open.dolphin.infomodel.DocInfoModel;
import open.dolphin.infomodel.DocumentModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.ModelUtils;
import open.dolphin.letter.KartePDFImpl2;
import open.dolphin.project.Project;

/**
 * シングルドキュメントのビュワークラス。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class KarteViewer extends AbstractChartDocument implements Comparable {
    
    // タイムスタンプの foreground カラー 
    private static final Color TIMESTAMP_FORE = new Color(0,51,153);
    
    // タイムスタンプのフォントサイズ 
    private static final int TIMESTAMP_FONT_SIZE = 14;
    
    // タイムスタンプフォント
    private static final Font TIMESTAMP_FONT = new Font("Dialog", Font.PLAIN, TIMESTAMP_FONT_SIZE);
//s.oh^ 不具合修正
    private static final Font TIMESTAMP_MSFONT = new Font("MS UI Gothic", Font.PLAIN, TIMESTAMP_FONT_SIZE);
//s.oh$
    
    // タイムスタンプパネル FlowLayout のマージン 
    private static final int TIMESTAMP_SPACING = 7;
    
    // 仮保存中のドキュメントを表す文字 
    protected static final String UNDER_TMP_SAVE = " - 仮保存中";
    
    // 選択されている時のボーダ色、1.3の赤
    private static final Color SELECTED_COLOR = Color.GRAY;//Color.ORANGE;//new Color(255, 0, 153);
    
    // 選択された状態のボーダ
    private static final Border SELECTED_BORDER = BorderFactory.createLineBorder(SELECTED_COLOR);
    //private static final Border SELECTED_BORDER = BorderFactory.createLineBorder((Color)UIManager.get("Table.selectionBackground"));
    
    // 選択されていない時のボーダ色
    private static final Color NOT_SELECTED_COLOR = new Color(0, 0, 0, 0);  // 透明
    
    // 選択されていない状態のボーダ
    protected static final Border NOT_SELECTED_BORDER = BorderFactory.createLineBorder(NOT_SELECTED_COLOR);
    
    //-------------------------------------------
    // インスタンス変数
    //-------------------------------------------

    // この view のモデル 
    protected DocumentModel model;
    
    // タイムスタンプラベル
    protected JLabel timeStampLabel;
    
    // SOA Pane 
    protected KartePane soaPane;

    // P Pane
    protected KartePane pPane;
    
    // 2号カルテパネル
    protected Panel2 panel2;
    
    // タイムスタンプの foreground カラー
    protected Color timeStampFore = TIMESTAMP_FORE;
    
    // タイムスタンプのフォント 
//s.oh^ 不具合修正
    //protected Font timeStampFont = TIMESTAMP_FONT;
    protected Font timeStampFont = null;
//s.oh$
    
    protected int timeStampSpacing = TIMESTAMP_SPACING;
    
    protected boolean avoidEnter;
    
    // 選択されているかどうかのフラグ
    protected boolean selected;
    
    
    /**
     * Creates new KarteViewer
     */
    public KarteViewer() {
//s.oh^ 不具合修正
        if(ClientContext.isWin()) {
            timeStampFont = TIMESTAMP_MSFONT;
        }else{
            timeStampFont = TIMESTAMP_FONT;
        }
//s.oh$
    }
    
//s.oh^ 2013/02/07 印刷対応
    public JLabel getTimeStampLabel() {
        return timeStampLabel;
    }
//s.oh$
    
    public int getActualHeight() {
        try {
            JTextPane pane = soaPane.getTextPane();
            int pos = pane.getDocument().getLength();
            Rectangle r = pane.modelToView(pos);
            int hsoa = r.y;
            return hsoa;
            
        } catch (BadLocationException ex) {
            ex.printStackTrace(System.err);
        }
        return 0;
    }
    
    public void adjustSize() {
        int h = getActualHeight();
        int soaWidth = soaPane.getTextPane().getPreferredSize().width; 
        soaPane.getTextPane().setPreferredSize(new Dimension(soaWidth, h));
    }
    
    public String getDocType() {
        if (model != null) {
            String docType = model.getDocInfoModel().getDocType();
            return docType;
        }
        return null;
    }
    
    public void setAvoidEnter(boolean b) {
        avoidEnter = b;
    }
    
    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    // Junzo SATO
    public void printPanel2(final PageFormat format) {
        String name = getContext().getPatient().getFullName();
        boolean printName = true;
        if (pPane==null) {
            printName = printName && Project.getBoolean("plain.print.patinet.name");
        }
        panel2.printPanel(format, 1, false, name, getActualHeight()+60, printName);
    }
    
    public void printPanel2(final PageFormat format, final int copies,
            final boolean useDialog) {
        String name = getContext().getPatient().getFullName();
        boolean printName = true;
        if (pPane==null) {
            printName = printName && Project.getBoolean("plain.print.patinet.name");
        }
        panel2.printPanel(format, copies, useDialog, name, getActualHeight()+60, printName);
    }
    
    @Override
    public void print() {
//s.oh^ 2013/02/07 印刷対応
        //PageFormat pageFormat = getContext().getContext().getPageFormat();
        //this.printPanel2(pageFormat);
        if(Project.getBoolean(Project.KARTE_PRINT_PDF)) {
            printPDF();
        }else{
            PageFormat pageFormat = getContext().getContext().getPageFormat();
            this.printPanel2(pageFormat);
        }
    }
    
//s.oh^ 2013/02/07 印刷対応
    private void printPDF() {
        StringBuilder sb = new StringBuilder();
        sb.append(ClientContext.getTempDirectory());
        KartePaneDumper_2 dumper = null;
        KartePaneDumper_2 pdumper = null;
        dumper = new KartePaneDumper_2();
        pdumper = new KartePaneDumper_2();
        KarteStyledDocument doc = (KarteStyledDocument)soaPane.getTextPane().getDocument();
        dumper.dump(doc);
        KarteStyledDocument pdoc = (KarteStyledDocument)pPane.getTextPane().getDocument();
        pdumper.dump(pdoc);
        if(dumper != null && pdumper != null) {
            KartePDFImpl2 pdf = new KartePDFImpl2(sb.toString(), null,
                                                  getContext().getPatient().getPatientId(), getContext().getPatient().getFullName(),
                                                  timeStampLabel.getText(),
                                                  new Date(), dumper, pdumper);
            String path = pdf.create();
            KartePDFImpl2.printPDF(path);
        }
    }
//s.oh$
    
    /**
     * SOA Pane を返す。
     * @return soaPane
     */
    public KartePane getSOAPane() {
        return soaPane;
    }
    
    /**
     * コンテナからコールされる enter() メソッドで
     * メニューを制御する。
     */
    @Override
    public void enter() {
        
        if (avoidEnter) {
            return;
        }
        super.enter();
        
        // ReadOnly 属性
        boolean canEdit = getContext().isReadOnly() ? false : true;
        
        // 仮保存かどうか
        boolean tmp = model.getDocInfoModel().getStatus().equals(IInfoModel.STATUS_TMP) ? true : false;
        
        // 新規カルテ作成が可能な条件
        boolean newOk = canEdit && (!tmp) ? true : false;
        
        ChartMediator mediator = getContext().getChartMediator();
        mediator.getAction(GUIConst.ACTION_NEW_KARTE).setEnabled(newOk);        // 新規カルテ
        mediator.getAction(GUIConst.ACTION_PRINT).setEnabled(true);             // 印刷
        mediator.getAction(GUIConst.ACTION_MODIFY_KARTE).setEnabled(canEdit);   // 修正
    }
        
    /**
     * シングルカルテで初期化する。
     */
    private void initialize() {
        
        KartePanel1 kp1 = new KartePanel1();
        panel2 = kp1;
        
        // TimeStampLabel を生成する
        timeStampLabel = kp1.getTimeStampLabel();
        timeStampLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timeStampLabel.setForeground(timeStampFore);
        timeStampLabel.setFont(timeStampFont);
        
        // SOA Pane を生成する
        soaPane = new KartePane();
        soaPane.setTextPane(kp1.getSoaTextPane());
        soaPane.setRole(IInfoModel.ROLE_SOA);
        if (model != null) {
            // Schema 画像にファイル名を付けるのために必要
            String docId = model.getDocInfoModel().getDocId();
            soaPane.setDocId(docId);
        }
        
        setUI(kp1);
    }
    
    /**
     * プログラムを開始する。
     */
    @Override
    public void start() {
        
        // Creates GUI
        this.initialize();
        
        // Model を表示する
        if (this.getModel() != null) {
            
            // 確定日を分かりやすい表現に変える
            String timeStamp = ModelUtils.getDateAsFormatString(
                    model.getDocInfoModel().getFirstConfirmDate(),
                    IInfoModel.KARTE_DATE_FORMAT);
            
            if (model.getDocInfoModel().getStatus().equals(IInfoModel.STATUS_TMP)) {
                StringBuilder sb = new StringBuilder();
                sb.append(timeStamp);
                sb.append(UNDER_TMP_SAVE);
                timeStamp = sb.toString();
                KartePanel1 kp2 = (KartePanel1)panel2;
                kp2.getTimeStampPanel().setOpaque(true);
                // (予定カルテ対応)
                //kp2.getTimeStampPanel().setBackground(GUIConst.TEMP_SAVE_KARTE_COLOR);
                //timeStampLabel.setOpaque(true);
                //timeStampLabel.setBackground(GUIConst.TEMP_SAVE_KARTE_COLOR);
                //timeStampLabel.setForeground(Color.WHITE);
                kp2.getTimeStampPanel().setBackground(GUIConst.TEMP_SAVE_KARTE_BK_COLOR);
                timeStampLabel.setOpaque(true);
                timeStampLabel.setBackground(GUIConst.TEMP_SAVE_KARTE_BK_COLOR);
                timeStampLabel.setForeground(GUIConst.TEMP_SAVE_KARTE_FORE_COLOR);
            }
            if (model.getUserModel().getCommonName()!=null) {
                StringBuilder sb = new StringBuilder();
                sb.append(timeStamp).append(" ").append(model.getUserModel().getCommonName());
                timeStamp = sb.toString();
            }
            timeStampLabel.setText(timeStamp);
            KarteRenderer_2 renderer = new KarteRenderer_2(soaPane, null);
            renderer.render(model);
        }
        
        // モデル表示後にリスナ等を設定する
        ChartMediator mediator = getContext().getChartMediator();
        soaPane.init(false, mediator);
        enter();
    }
    
    @Override
    public void stop() {
        soaPane.clear();
    }
    
    /**
     * 表示するモデルを設定する。
     * @param model 表示するDocumentModel
     */
    public void setModel(DocumentModel model) {
        this.model = model;
    }
    
    /**
     * 表示するモデルを返す。
     * @return 表示するDocumentModel
     */
    public DocumentModel getModel() {
        return model;
    }
    
    /**
     * 選択状態を設定する。
     * 選択状態によりViewのボーダの色を変える。
     * @param selected 選択された時 true
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
        if (selected) {
            getUI().setBorder(SELECTED_BORDER);
//            KartePanel2M p = (KartePanel2M)panel2;
//            p.getTimeStampPanel().setBackground(Color.YELLOW);
//            timeStampLabel.setBackground(Color.YELLOW);
        } else {
            getUI().setBorder(NOT_SELECTED_BORDER);
//            KartePanel2M p = (KartePanel2M)panel2;
//            p.getTimeStampPanel().setBackground(defaultPanelBackground);
//            timeStampLabel.setBackground(defaultPanelBackground);
        }
    }
    
    /**
     * 選択されているかどうかを返す。
     * @return 選択されている時 true
     */
    public boolean isSelected() {
        return selected;
    }
    
    public void addMouseListener(MouseListener ml) {
        soaPane.getTextPane().addMouseListener(ml);
    }
    
    @Override
    public int hashCode() {
        return getModel().getDocInfoModel().getDocId().hashCode() + 72;
    }
    
    @Override
    public boolean equals(Object other) {
//minagawa^ Single-Karteと2号Karteの比較も可能にする       
//        if (other != null && other.getClass() == this.getClass()) {
//            DocInfoModel otheInfo = ((KarteViewer) other).getModel()
//            .getDocInfoModel();
//            return getModel().getDocInfoModel().equals(otheInfo);
//        }
        if (other != null && other instanceof KarteViewer) {
            DocInfoModel otheInfo = ((KarteViewer)other).getModel().getDocInfoModel();
            return getModel().getDocInfoModel().equals(otheInfo);
        }
        return false;
//minagawa$        
    }
    
    @Override
    public int compareTo(Object other) {
//minagawa^ Single-Karteと2号Karteの比較も可能にする          
//        if (other != null && other.getClass() == this.getClass()) {
//            DocInfoModel otheInfo = ((KarteViewer) other).getModel()
//            .getDocInfoModel();
//            return getModel().getDocInfoModel().compareTo(otheInfo);
//        }
        if (other != null && other instanceof KarteViewer) {
            DocInfoModel otheInfo = ((KarteViewer) other).getModel().getDocInfoModel();
            return getModel().getDocInfoModel().compareTo(otheInfo);
        }        
        return -1;
//minagawa$        
    }
}