package open.dolphin.client;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.*;
import javax.swing.*;
import open.dolphin.delegater.DocumentDelegater;
import open.dolphin.exception.DolphinException;
import open.dolphin.helper.DBTask;
import open.dolphin.infomodel.*;
import open.dolphin.plugin.PluginLoader;
import open.dolphin.project.Project;
import open.dolphin.util.BeanUtils;
import open.dolphin.util.ZenkakuUtils;

/**
 * 2号カルテクラス。
 *
 * @author Kazushi Minagawa
 */
public class KarteEditor extends AbstractChartDocument implements IInfoModel, NChartDocument {

    // シングルモード
    public static final int SINGLE_MODE = 1;

    // ２号カルテモード
    public static final int DOUBLE_MODE = 2;

    // TimeStamp のカラー
    private static final Color TIMESTAMP_FORE = Color.BLUE;
    private static final int TIMESTAMP_FONT_SIZE = 14;
    private static final Font TIMESTAMP_FONT = new Font("Dialog", Font.PLAIN, TIMESTAMP_FONT_SIZE);
    private static final String DEFAULT_TITLE = "経過記録";
    private static final String UPDATE_TAB_TITLE = "更新";

    // このエディタのモード
    private int mode = 2;

    // このエディタのモデル
    private DocumentModel model;

    // このエディタを構成するコンポーネント
    private JLabel timeStampLabel;

    // Timestamp
    private String timeStamp;

    // 開始時間（カルテオープン）
    private Date started;

    // 終了（保存した時間）
    private Date saved;
    
    // 健康保険Box
    private boolean insuranceVisible;

    // SOA Pane
    private KartePane soaPane;

    // P Pane
    private KartePane pPane;

    // 2号カルテ JPanel
    private Panel2 panel2;

    // タイムスタンプの foreground
    private Color timeStampFore = TIMESTAMP_FORE;

    // タイムスタンプフォント
    private Font timeStampFont = TIMESTAMP_FONT;

    // 編集可能かどうかのフラグ
    // このフラグで KartePane を初期化する
    private boolean editable;

    // 修正時に true
    private boolean modify;

    // CLAIM 送信リスナ
    private ClaimMessageListener claimListener;

    // MML送信リスナ
    private MmlMessageListener mmlListener;

    // MML送信フラグ
    private boolean sendMml;

    // CLAIM 送信フラグ
    private boolean sendClaim;

    // Labtest 送信フラグ
    private boolean sendLabtest;

    // State Manager
    private StateMgr stateMgr;

    /** 
     * Creates new KarteEditor2 
     */
    public KarteEditor() {
        setTitle(DEFAULT_TITLE);
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    /**
     * DocumentModelを返す。
     * @return DocumentModel
     */
    public DocumentModel getModel() {
        return model;
    }

    /**
     * DocumentModelを設定する。
     * @param model DocumentModel
     */
    public void setModel(DocumentModel model) {
        this.model = model;
    }

    private boolean isSendClaim() {
        return sendClaim;
    }

    private boolean isSendMML() {
        return sendMml;
    }

    private boolean isSendLabtest() {
        return sendLabtest;
    }

    public int getActualHeight() {
        try {
            JTextPane pane = soaPane.getTextPane();
            int pos = pane.getDocument().getLength();
            Rectangle r = pane.modelToView(pos);
            int hsoa = r.y;

            if (pPane == null) {
                return hsoa;
            }

            pane = pPane.getTextPane();
            pos = pane.getDocument().getLength();
            r = pane.modelToView(pos);
            int hp = r.y;

            return Math.max(hsoa, hp);

        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        return 0;
    }

    public void printPanel2(final PageFormat format) {
        String name = getContext().getPatient().getFullName();
        boolean printName = true;
        if (mode==SINGLE_MODE) {
            printName = printName && Project.getBoolean("plain.print.patinet.name");
        }
        panel2.printPanel(format, 1, false, name, getActualHeight() + 60, printName);
    }

    public void printPanel2(final PageFormat format, final int copies,
            final boolean useDialog) {
        String name = getContext().getPatient().getFullName();
        boolean printName = true;
        if (mode==SINGLE_MODE) {
            printName = printName && Project.getBoolean("plain.print.patinet.name");
        }
        panel2.printPanel(format, copies, useDialog, name, getActualHeight() + 60, printName);
    }

    @Override
    public void print() {
        PageFormat pageFormat = getContext().getContext().getPageFormat();
        this.printPanel2(pageFormat);
    }

    public void insertImage() {
        JFileChooser chooser = new JFileChooser();
        int selected = chooser.showOpenDialog(getContext().getFrame());
        if (selected == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getPath();
            this.getSOAPane().insertImage(path);

        } else if (selected == JFileChooser.CANCEL_OPTION) {
        }
    }

    /**
     * SOAPaneを返す。
     * @return SOAPane
     */
    protected KartePane getSOAPane() {
        return soaPane;
    }

    /**
     * PPaneを返す。
     * @return PPane
     */
    protected KartePane getPPane() {
        return pPane;
    }

    /**
     * 編集可能属性を設定する。
     * @param b 編集可能な時true
     */
    protected void setEditable(boolean b) {
        editable = b;
    }

    /**
     * MMLリスナを追加する。
     * @param listener MMLリスナリスナ
     */
    public void addMMLListner(MmlMessageListener listener) throws TooManyListenersException {
        if (mmlListener != null) {
            throw new TooManyListenersException();
        }
        mmlListener = listener;
    }

    /**
     * MMLリスナを削除する。
     * @param listener MMLリスナリスナ
     */
    public void removeMMLListener(MmlMessageListener listener) {
        if (mmlListener != null && mmlListener == listener) {
            mmlListener = null;
        }
    }

    /**
     * CLAIMリスナを追加する。
     * @param listener CLAIMリスナ
     * @throws TooManyListenersException
     */
    public void addCLAIMListner(ClaimMessageListener listener)
            throws TooManyListenersException {
        if (claimListener != null) {
            throw new TooManyListenersException();
        }
        claimListener = listener;
    }

    /**
     * CLAIMリスナを削除する。
     * @param listener 削除するCLAIMリスナ
     */
    public void removeCLAIMListener(ClaimMessageListener listener) {
        if (claimListener != null && claimListener == listener) {
            claimListener = null;
        }
    }

    /**
     * 修正属性を設定する。
     * @param b 修正する時true
     */
    protected void setModify(boolean b) {
        modify = b;
    }

    @Override
    public void enter() {
        super.enter();
        stateMgr.controlMenu();
    }

    @Override
    public void setDirty(boolean dirty) {
        if (getMode() == SINGLE_MODE) {
            stateMgr.setDirty(soaPane.isDirty());
        } else {
            boolean bdirty = (soaPane.isDirty() || pPane.isDirty()) ? true : false;
            stateMgr.setDirty(bdirty);
        }
    }

    @Override
    public boolean isDirty() {
        return stateMgr.isDirty();
    }

    /**
     * 初期化する。
     */
    public void initialize() {

        if (getMode() == SINGLE_MODE) {
            initialize1();
        } else if (getMode() == DOUBLE_MODE) {
            initialize2();
        }
    }

    /**
     * シングルモードで初期化する。
     */
    private void initialize1() {

        stateMgr = new StateMgr();

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
        soaPane.setParent(this);
        soaPane.setRole(ROLE_SOA);
        soaPane.getTextPane().setTransferHandler(new SOATransferHandler(soaPane));
        if (model != null) {
            // Schema 画像にファイル名を付けるのために必要
            String docId = model.getDocInfoModel().getDocId();
            soaPane.setDocId(docId);
        }

        JScrollPane scroller = new JScrollPane(kp1);
        getUI().setLayout(new BorderLayout());
        getUI().add(scroller, BorderLayout.CENTER);

        // 初期化の前にモデルがセットしてある。
        // Model を表示する
        displayModel();
    }

    /**
     * 2号カルテモードで初期化する。
     */
    private void initialize2() {

        stateMgr = new StateMgr();

        //KartePanel2 kp2 = new KartePanel2();
        KartePanel2M kp2 = new KartePanel2M();
        panel2 = kp2;

        // TimeStampLabel を生成する
        timeStampLabel = kp2.getTimeStampLabel();
        timeStampLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timeStampLabel.setForeground(timeStampFore);
        timeStampLabel.setFont(timeStampFont);

        // SOA Pane を生成する
        soaPane = new KartePane();
        soaPane.setTextPane(kp2.getSoaTextPane());
        soaPane.setParent(this);
        soaPane.setRole(ROLE_SOA);
        soaPane.getTextPane().setTransferHandler(new SOATransferHandler(soaPane));
        if (model != null) {
            // Schema 画像にファイル名を付けるのために必要
            String docId = model.getDocInfoModel().getDocId();
            soaPane.setDocId(docId);
        }

        // P Pane を生成する
        pPane = new KartePane();
        pPane.setTextPane(kp2.getPTextPane());
        pPane.setParent(this);
        pPane.setRole(ROLE_P);
        pPane.getTextPane().setTransferHandler(new PTransferHandler(pPane));

        JScrollPane scroller = new JScrollPane(kp2);
        getUI().setLayout(new BorderLayout());
        getUI().add(scroller, BorderLayout.CENTER);

        // 初期化の前にモデルがセットしてある。
        // Model を表示する
        displayModel();
    }

    @Override
    public void start() {
        if (getMode() == SINGLE_MODE) {
            start1();
        } else if (getMode() == DOUBLE_MODE) {
            start2();
        }
    }

    @Override
    public void stop() {
    }

    /**
     * シングルモードを開始する。初期化の後コールされる。
     */
    private void start1() {
        // モデル表示後にリスナ等を設定する
        ChartMediator mediator = getContext().getChartMediator();
        soaPane.init(editable, mediator);
        enter();
    }

    /**
     * ２号カルテモードを開始する。初期化の後コールされる。
     */
    private void start2() {
        // モデル表示後にリスナ等を設定する
        ChartMediator mediator = getContext().getChartMediator();
        soaPane.init(editable, mediator);
        pPane.init(editable, mediator);
        enter();
    }

    /**
     * DocumentModelを表示する。
     */
    private void displayModel() {

        // Timestamp を表示する
        //Date now = new Date();
        started = new Date();
        timeStamp = ModelUtils.getDateAsFormatString(started, IInfoModel.KARTE_DATE_FORMAT);

        // 修正の場合
        if (modify) {
            // 更新: YYYY-MM-DDTHH:MM:SS (firstConfirmDate)
            StringBuilder buf = new StringBuilder();
            buf.append(UPDATE_TAB_TITLE);
            buf.append(": ");
            buf.append(timeStamp);
            buf.append(" [");
            buf.append(ModelUtils.getDateAsFormatString(model.getDocInfoModel().getFirstConfirmDate(), IInfoModel.KARTE_DATE_FORMAT));
            buf.append(" ]");
            timeStamp = buf.toString();
        }

        // 内容を表示する
        if (model.getModules() != null) {
            KarteRenderer_2 renderer = new KarteRenderer_2(soaPane, pPane);
            renderer.render(model);
        }

        //---------------------
        // 健康保険を表示する
        //---------------------
        PVTHealthInsuranceModel[] ins = null;

        //-------------------------------------------------
        // 患者が保有する全ての保険情報を配列へ格納する
        // コンテキストが EditotFrame の場合と Chart の場合がある
        //-------------------------------------------------
        if (getContext() instanceof ChartImpl) {
            ins = ((ChartImpl) getContext()).getHealthInsurances();
        } else if (getContext() instanceof EditorFrame) {
            EditorFrame ef = (EditorFrame) getContext();
            ChartImpl chart = (ChartImpl) ef.getChart();
            ins = chart.getHealthInsurances();
        }

        //-------------------------------------------------
        // Model に設定してある健康保険を選択する
        // (カルテを作成する場合にダイアログで保険を選択している）
        // 選択した保険のGUIDと一致するものを配列から見つけ、表示する
        //-------------------------------------------------
        String selecteIns = null;
        String insGUID = getModel().getDocInfoModel().getHealthInsuranceGUID();
        if (insGUID != null) {
            ClientContext.getBootLogger().debug("insGUID = " + insGUID);
            for (int i = 0; i < ins.length; i++) {
                String GUID = ins[i].getGUID();
                if (GUID != null && GUID.equals(insGUID)) {
                    selecteIns = ins[i].toString();
                    ClientContext.getBootLogger().debug("found ins = " + selecteIns);
                    break;
                }
            }
        } else {
            ClientContext.getBootLogger().debug("insGUID is null");
        }

        StringBuilder sb = new StringBuilder();
        sb.append(timeStamp);
        if ( (getMode()==DOUBLE_MODE) && (selecteIns!=null) ) {
            sb.append(" (");
            sb.append(selecteIns);
            sb.append(")");
        }

        timeStampLabel.setText(sb.toString());
        timeStampLabel.addMouseListener(new PopupListener());

        insuranceVisible = true;
    }

    /**
     * 処方日数を一括変更する。
     */
    public void changeNumOfDatesAll() {
        if (getPPane()==null || (!editable) || (!getPPane().hasRP())) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }

        PropertyChangeListener pcl = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent pce) {
                int number = ((Integer)pce.getNewValue()).intValue();
                if (number>0) {
                    getPPane().changeAllRPNumDates(number);
                }
            }
        };

        ChangeNumDatesDialog dialog = new ChangeNumDatesDialog(getContext().getFrame(), pcl);
        dialog.show();
    }

    /**
     * Chart画面で保険選択が行われた時にコールされる。
     * @param hm 選択された保険情報
     */
    public void applyInsurance(PVTHealthInsuranceModel hm) {

        getModel().getDocInfoModel().setHealthInsurance(hm.getInsuranceClassCode());
        getModel().getDocInfoModel().setHealthInsuranceDesc(hm.toString());
        getModel().getDocInfoModel().setHealthInsuranceGUID(hm.getGUID());  // GUID
        stateMgr.setDirty(true);

        if (isInsuranceVisible()) {
            StringBuilder sb = new StringBuilder();
            sb.append(timeStamp);
            sb.append(" (");
            sb.append(hm.toString());
            sb.append(")");

            timeStampLabel.setText(sb.toString());
            timeStampLabel.revalidate();
        }
    }

    public void setInsuranceVisible(Boolean b) {

        boolean old = insuranceVisible;

        if (old != b) {

            insuranceVisible = b;

            StringBuilder sb = new StringBuilder();
            sb.append(timeStamp);

            if (b) {
                sb.append(" (");
                sb.append(getModel().getDocInfoModel().getHealthInsuranceDesc());
                sb.append(")");
            }

            timeStampLabel.setText(sb.toString());
            timeStampLabel.revalidate();
        }
    }

    public boolean isInsuranceVisible() {
        return insuranceVisible;
    }

    class PopupListener extends MouseAdapter {

        public PopupListener() {
        }

        @Override
        public void mouseClicked(MouseEvent e) {

            if (e.getClickCount() == 1) {
                boolean b = isInsuranceVisible();
                setInsuranceVisible((!b));
            }
            e.consume();
        }
    }

    /**
     * 保存ダイアログを表示し保存時のパラメータを取得する。
     * @params sendMML MML送信フラグ 送信するとき true
     */
    private SaveParams getSaveParams(boolean joinAreaNetwork) {

        // Title が設定されているか
        String text = model.getDocInfoModel().getTitle();
        if (text == null || text.equals("")) {

            if (Project.getBoolean("useTop15AsTitle")) {
                // SOAPane から最初の１５文字を文書タイトルとして取得する
                text = soaPane.getTitle();
            } else {
                text = Project.getString("defaultKarteTitle");
            }

            if ((text == null) || text.equals("")) {
                text = DEFAULT_TITLE;
            }
        }

        SaveParams params;

        //-------------------------------
        // 新規カルテで保存の場合
        // 仮保存から修正がかかっている場合
        // 修正の場合
        //-------------------------------
        DocInfoModel docInfo = getModel().getDocInfoModel();

        if (!modify && docInfo.getStatus().equals(IInfoModel.STATUS_NONE)) {
            ClientContext.getBootLogger().debug("saveFromNew");
            if (sendClaim) {
                sendClaim = Project.getBoolean(Project.SEND_CLAIM_SAVE);    //Project.getSendClaimSave();
            }

        } else if (modify && docInfo.getStatus().equals(IInfoModel.STATUS_TMP)) {
            ClientContext.getBootLogger().debug("saveFromTmp");
            if (sendClaim) {
                sendClaim = Project.getBoolean(Project.SEND_CLAIM_TMP); //Project.getSendClaimSave();
            }

        } else if (modify) {
            ClientContext.getBootLogger().debug("saveFromModify");
            if (sendClaim) {
                sendClaim = Project.getBoolean(Project.SEND_CLAIM_MODIFY);  //Project.getSendClaimModify();
            }
            // 修正保存の場合
            if (sendLabtest) {
                sendLabtest = false;
            }
        }

        // 保存時に確認ダイアログを表示するかどうか
        if (Project.getBoolean(Project.KARTE_SHOW_CONFIRM_AT_SAVE)) {

            params = new SaveParams(joinAreaNetwork);
            params.setTitle(text);
            params.setDepartment(model.getDocInfoModel().getDepartmentDesc());

            // 印刷枚数をPreferenceから取得する
            int numPrint = Project.getInt("karte.print.count", 0);
            params.setPrintCount(numPrint);

            //-----------------------------
            // Single Mode の時は送信なし
            //-----------------------------
            params.setSendEnabled(getMode()!=SINGLE_MODE);

            //-----------------------------
            // CLAIM 送信
            // 保存ダイアログで変更する事が可能
            //-----------------------------
            params.setSendClaim(sendClaim);

            //-----------------------------
            // Labtest 送信
            //-----------------------------
            params.setSendLabtest(sendLabtest);
            if (getMode()==DOUBLE_MODE && pPane!=null) {
                params.setHasLabtest(pPane.hasLabtest());
            }

            // 保存ダイアログを表示する
            Window parent = SwingUtilities.getWindowAncestor(this.getUI());
            SaveDialog sd = new SaveDialog(parent);
            params.setAllowPatientRef(false);    // 患者の参照
            params.setAllowClinicRef(false);     // 診療履歴のある医療機関
            sd.setValue(params);
            sd.start();                          // showDaialog
            params = sd.getValue();

            // 印刷枚数を保存する
            if (params != null) {
                Project.setInt("karte.print.count", params.getPrintCount());
            }

        } else {
            //-----------------------------
            // 確認ダイアログを表示しない
            //-----------------------------
            params = new SaveParams(false);
            params.setTitle(text);
            params.setDepartment(model.getDocInfoModel().getDepartmentDesc());
            params.setPrintCount(Project.getInt(Project.KARTE_PRINT_COUNT, 0));

            // 仮保存が指定されている端末の場合
            int sMode = Project.getInt(Project.KARTE_SAVE_ACTION);
            boolean tmpSave = sMode == 1 ? true : false;
            params.setTmpSave(tmpSave);
            if (tmpSave) {
                params.setSendClaim(false);
                params.setSendLabtest(false);
            } else {
                // 保存が実行される端末の場合
                params.setSendClaim(sendClaim);
                params.setSendLabtest(sendLabtest);
            }

            // 患者参照、施設参照不可
            params.setAllowClinicRef(false);
            params.setAllowPatientRef(false);

        }

        return params;
    }

    @Override
    public void save() {

        try {
            // 何も書かれていない時はリターンする
            if (!stateMgr.isDirty()) {
                return;
            }

            // MML送信用のマスタIDを取得する
            // ケース１ HANIWA 方式 facilityID + patientID
            // ケース２ HIGO 方式 地域ID を使用
            ID masterID = Project.getMasterId(getContext().getPatient().getPatientId());

            sendMml = (Project.getBoolean(Project.SEND_MML) && masterID != null && mmlListener != null)
                    ? true
                    : false;

            //----------------------------------------------------------
            // この段階での CLAIM 送信 = 診療行為送信かつclaimListener!=null
            //----------------------------------------------------------
            sendClaim = getContext().isSendClaim();
            sendLabtest = getContext().isSendLabtest();

            // 保存ダイアログを表示し、パラメータを得る
            // 地域連携に参加もしくはMML送信を行う場合は患者及び診療歴のある施設への参照許可
            // パラメータが設定できるようにする
            // boolean karteKey = (Project.getJoinAreaNetwork() || sendMml) ? true : false;
            // 地域連携に参加する場合のみに変更する
            SaveParams params = getSaveParams(Project.getBoolean(Project.JOIN_AREA_NETWORK));

            // キャンセルの場合はリターンする
            if (params != null) {
                //------------------------
                // 次のステージを実行する
                //------------------------
                saved = new Date();
                
                if (getMode() == SINGLE_MODE) {
                    save1(params);
                } else if (getMode() == DOUBLE_MODE) {
                    save2(params);
                }
            }

        } catch (DolphinException e) {
            ClientContext.getBootLogger().warn(e);
        }
    }

    /**
     * シングルモードの保存を行う。
     **/
    private void save1(final SaveParams params) throws DolphinException {

        //----------------------
        // DocInfoに値を設定する
        //----------------------
        final DocInfoModel docInfo = model.getDocInfoModel();

        // 現在時刻を ConfirmDate にする
        Date confirmed = saved;
        docInfo.setConfirmDate(confirmed);

        //----------------------------------------------------
        // 修正でない場合は FirstConfirmDate = ConfirmDate にする
        // 修正の場合は FirstConfirmDate は既に設定されている
        // 修正でない新規カルテは parentId = null である
        //----------------------------------------------------
        if (docInfo.getParentId() == null) {
            docInfo.setFirstConfirmDate(confirmed);
        }

        //----------------------------------------------------
        // Status 仮保存か確定保存かを設定する
        // final の時は CLAIM 送信するが前の状態に依存する
        //----------------------------------------------------
        if (!params.isTmpSave()) {
            // 
            // 編集が開始された時の state を取得する
            //
            String oldStatus = docInfo.getStatus();

            if (oldStatus.equals(STATUS_NONE)) {
                //
                // NONEから確定への遷移 newSave
                //
                sendClaim = false;
                sendLabtest = false;

            } else if (oldStatus.equals(STATUS_TMP)) {

                sendClaim = false;
                sendLabtest = false;

            } else {
                //
                // 確定から確定（修正の場合に相当する）以前は sendClaim = false;
                //
                sendClaim = false;
                sendLabtest = false;
            }

            //
            // 保存時の state を final にセットする
            //
            docInfo.setStatus(STATUS_FINAL);

        } else {
            //
            // 仮保存の場合 CLAIM 送信しない
            //
            sendClaim = false;
            sendLabtest = false;
            sendMml = false;
            docInfo.setStatus(STATUS_TMP);
        }

        // titleを設定する
        docInfo.setTitle(params.getTitle());

        // デフォルトのアクセス権を設定をする TODO
        AccessRightModel ar = new AccessRightModel();
        ar.setPermission(PERMISSION_ALL);
        ar.setLicenseeCode(ACCES_RIGHT_CREATOR);
        ar.setLicenseeName(ACCES_RIGHT_CREATOR_DISP);
        ar.setLicenseeCodeType(ACCES_RIGHT_FACILITY_CODE);
        docInfo.addAccessRight(ar);

        // 患者のアクセス権を設定をする
        if (params.isAllowPatientRef()) {
            ar = new AccessRightModel();
            ar.setPermission(PERMISSION_READ);
            ar.setLicenseeCode(ACCES_RIGHT_PATIENT);
            ar.setLicenseeName(ACCES_RIGHT_PATIENT_DISP);
            ar.setLicenseeCodeType(ACCES_RIGHT_PERSON_CODE);
            docInfo.addAccessRight(ar);
        }

        // 診療履歴のある施設のアクセス権を設定をする
        if (params.isAllowClinicRef()) {
            ar = new AccessRightModel();
            ar.setPermission(PERMISSION_READ);
            ar.setLicenseeCode(ACCES_RIGHT_EXPERIENCE);
            ar.setLicenseeName(ACCES_RIGHT_EXPERIENCE_DISP);
            ar.setLicenseeCodeType(ACCES_RIGHT_EXPERIENCE_CODE);
            docInfo.addAccessRight(ar);
        }

        // ProgressCourseModule の ModuleInfo を保存しておく
        ModuleInfoBean[] progressInfo = model.getModuleInfo(MODULE_PROGRESS_COURSE);
        if (progressInfo == null) {
            // 存在しない場合は新規に作成する
            progressInfo = new ModuleInfoBean[1];
            ModuleInfoBean mi = new ModuleInfoBean();
            mi.setStampName(MODULE_PROGRESS_COURSE);
            mi.setEntity(MODULE_PROGRESS_COURSE);
            mi.setStampRole(ROLE_SOA_SPEC);
            progressInfo[0] = mi;
        }

        //----------------------------------------------
        // モデルのモジュールをヌルに設定する
        // エディタの画面をダンプして生成したモジュールを設定する
        //----------------------------------------------
        model.clearModules();
        model.clearSchema();

        //----------------------------------------------
        // SOAPane をダンプし model に追加する
        //----------------------------------------------
        KartePaneDumper_2 dumper = new KartePaneDumper_2();
        KarteStyledDocument doc = (KarteStyledDocument) soaPane.getTextPane().getDocument();
        dumper.dump(doc);
        ModuleModel[] soa = dumper.getModule();
        if (soa != null && soa.length > 0) {
            model.addModule(soa);
        }

        // ProgressCourse SOA を生成する
        ProgressCourse pc = new ProgressCourse();
        pc.setFreeText(dumper.getSpec());
        ModuleModel progressSoa = new ModuleModel();
        progressSoa.setModuleInfoBean(progressInfo[0]);
        progressSoa.setModel(pc);
        model.addModule(progressSoa);

        // 
        // Schema を追加する
        //      
        int maxImageWidth = ClientContext.getInt("image.max.width");
        int maxImageHeight = ClientContext.getInt("image.max.height");
        Dimension maxSImageSize = new Dimension(maxImageWidth, maxImageHeight);
        SchemaModel[] schemas = dumper.getSchema();
        if (schemas != null && schemas.length > 0) {
            // 保存のため Icon を JPEG に変換する
            for (SchemaModel schema : schemas) {
                ImageIcon icon = schema.getIcon();
                icon = adjustImageSize(icon, maxSImageSize);
                byte[] jpegByte = getJPEGByte(icon.getImage());
                schema.setJpegByte(jpegByte);
                schema.setIcon(null);
                model.addSchema(schema);
            }
        }

        // FLAGを設定する
        // image があるかどうか
        boolean flag = model.getSchema() != null ? true : false;
        docInfo.setHasImage(flag);

        //----------------------------------------------
        // EJB3.0 Model の関係を構築する
        // confirmed, firstConfirmed は設定済み
        //----------------------------------------------
        KarteBean karte = getContext().getKarte();
        model.setKarteBean(karte);                          // karte
        model.setUserModel(Project.getUserModel());         // 記録者
        model.setRecorded(docInfo.getConfirmDate());        // 記録日

        // Moduleとの関係を設定する
        Collection<ModuleModel> moduleBeans = model.getModules();
        int number = 0;
        for (ModuleModel mb : moduleBeans) {
            mb.setId(0L);                                           // unsaved-value
            mb.setKarteBean(karte);                                 // Karte
            mb.setUserModel(Project.getUserModel());                // 記録者
            mb.setDocumentModel(model);                             // Document
            mb.setConfirmed(docInfo.getConfirmDate());              // 確定日
            mb.setFirstConfirmed(docInfo.getFirstConfirmDate());    // 適合開始日
            mb.setRecorded(docInfo.getConfirmDate());               // 記録日
            mb.setStatus(STATUS_FINAL);                             // status
            mb.setBeanBytes(BeanUtils.getXMLBytes(mb.getModel()));  // byte[]

            // ModuleInfo を設定する
            // Name, Role, Entity は設定されている
            ModuleInfoBean mInfo = mb.getModuleInfoBean();
            mInfo.setStampNumber(number++);
        }

        // 画像との関係を設定する
        number = 0;
        Collection<SchemaModel> imagesimages = model.getSchema();
        if (imagesimages != null && imagesimages.size() > 0) {
            for (SchemaModel sm : imagesimages) {
                sm.setId(0L);                                         // unsaved
                sm.setKarteBean(karte);                               // Karte
                sm.setUserModel(Project.getUserModel());              // Creator
                sm.setDocumentModel(model);                           // Document
                sm.setConfirmed(docInfo.getConfirmDate());            // 確定日
                sm.setFirstConfirmed(docInfo.getFirstConfirmDate());  // 適合開始日
                sm.setRecorded(docInfo.getConfirmDate());             // 記録日
                sm.setStatus(STATUS_FINAL);                           // Status
                sm.setImageNumber(number);

                ExtRefModel ref = sm.getExtRefModel();
                StringBuilder sb = new StringBuilder();
                sb.append(model.getDocInfoModel().getDocId());
                sb.append("-");
                sb.append(number);
                sb.append(".jpg");
                ref.setHref(sb.toString());

                number++;
            }
        }

        final DocumentDelegater ddl = new DocumentDelegater();
        final DocumentModel saveModel = model;
        final Chart chart = this.getContext();

        DBTask task = new DBTask<Void, Void>(chart) {

            @Override
            protected Void doInBackground() throws Exception {
                ddl.putKarte(saveModel);
                return null;
            }

            @Override
            protected void succeeded(Void result) {

                // 印刷
                int copies = params.getPrintCount();
                if (copies > 0) {
                    printPanel2(chart.getContext().getPageFormat(), copies, false);
                }

                // 編集不可に設定する
                soaPane.setEditableProp(false);

                // 状態遷移する
                stateMgr.setSaved(true);

                //------------------------
                // 文書履歴の更新を通知する
                //------------------------
                chart.getDocumentHistory().getDocumentHistory();
            }
        };

        task.execute();
    }
        
    /**
     * Courtesy of Junzo SATO
     */
    private byte[] getJPEGByte(Image image) {

        byte[] ret = null;
        BufferedOutputStream writer = null;

        try {
            JPanel myPanel = getUI();
            Dimension d = new Dimension(image.getWidth(myPanel), image.getHeight(myPanel));
            BufferedImage bf = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
            Graphics g = bf.getGraphics();
            g.setColor(Color.white);
            g.drawImage(image, 0, 0, d.width, d.height, myPanel);

            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            writer = new BufferedOutputStream(bo);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(writer);
            encoder.encode(bf);
            writer.flush();
            writer.close();
            ret = bo.toByteArray();

        } catch (IOException e) {
            e.printStackTrace(System.err);
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e2) {
                }
            }
        }
        return ret;
    }

    private ImageIcon adjustImageSize(ImageIcon icon, Dimension dim) {

        if ((icon.getIconHeight() > dim.height) ||
                (icon.getIconWidth() > dim.width)) {
            Image img = icon.getImage();
            float hRatio = (float) icon.getIconHeight() / dim.height;
            float wRatio = (float) icon.getIconWidth() / dim.width;
            int h,w;
            if (hRatio > wRatio) {
                h = dim.height;
                w = (int) (icon.getIconWidth() / hRatio);
            } else {
                w = dim.width;
                h = (int) (icon.getIconHeight() / wRatio);
            }
            img = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } else {
            return icon;
        }
    }

    /**
     * ２号カルテ保存処理の主な部分を実行する。
     **/
    private void save2(SaveParams params) throws DolphinException {
        
        //-----------------------------------------------
        // SOAPane をダンプし model に追加する
        //-----------------------------------------------
        KartePaneDumper_2 dumper = new KartePaneDumper_2();
        KarteStyledDocument doc = (KarteStyledDocument) soaPane.getTextPane().getDocument();    // component
        dumper.dump(doc);
        ModuleModel[] soa = dumper.getModule();
        String soaText = dumper.getSpec();
        SchemaModel[] schemas = dumper.getSchema();
        
        //-----------------------------------------------
        // PPane をダンプし model に追加する
        //-----------------------------------------------
        KartePaneDumper_2 pdumper = new KartePaneDumper_2();
        KarteStyledDocument pdoc = (KarteStyledDocument) pPane.getTextPane().getDocument(); // component
        pdumper.dump(pdoc);
        ModuleModel[] plan = pdumper.getModule();
        String pText = pdumper.getSpec();
        
        //--------------------------------
        // Editor Frame 閉じる
        //--------------------------------
        EditorFrame ef = null;
        if (KarteEditor.this.getContext() instanceof EditorFrame) {
            if (Project.getBoolean(Project.KARTE_AUTO_CLOSE_AFTER_SAVE, false)) {
                ef = (EditorFrame)KarteEditor.this.getContext();
            }
        }
        
        //-----------------------------------------------
        // 保存と送信タスク
        //-----------------------------------------------
        SaveAdnSender saveSender = new SaveAdnSender(getContext(),params,soa,soaText,schemas,plan,pText,ef);
        saveSender.doTask();
    }
   
    /**
     * 保存と送信タスククラス。
     */
    protected class SaveAdnSender {
        
        private Chart chart;
        private SaveParams params;
        private ModuleModel[] soa;
        private String soaText;
        private SchemaModel[] schemas;
        private ModuleModel[] plan;
        private String pText;
        private EditorFrame ef;
        private int theState = 0;
        
        public SaveAdnSender(Chart chart,
                SaveParams params, 
                ModuleModel[] soa, 
                String soaText,
                SchemaModel[] schemas,
                ModuleModel[] plan,
                String pText,
                EditorFrame ef) {
            
            this.chart = chart;
            this.params = params;
            this.soa = soa;
            this.soaText = soaText;
            this.schemas = schemas;
            this.plan = plan;
            this.pText = pText;
            this.ef = ef;
        }
        
        public void doTask() {
            
            DBTask dbTask = new DBTask<Void, Void>(chart) {

                @Override
                protected Void doInBackground() throws Exception {
                    
                    //----------------------------------------------------
                    // DocInfoに値を設定する
                    //----------------------------------------------------
                    final DocInfoModel docInfo = model.getDocInfoModel();

                    // 現在時刻を ConfirmDate にする
                    Date confirmed = saved;
                    docInfo.setConfirmDate(confirmed);

                    //----------------------------------------------------
                    // 修正でない場合は FirstConfirmDate = ConfirmDate にする
                    // 修正の場合は FirstConfirmDate は既に設定されている
                    // 修正でない新規カルテは parentId = null である
                    //----------------------------------------------------
                    if (docInfo.getParentId() == null) {
                        docInfo.setFirstConfirmDate(confirmed);
                    }

                    //----------------------------------------------------
                    // Status 仮保存か確定保存かを設定する
                    // final の時は CLAIM 送信するが前の状態に依存する
                    //----------------------------------------------------
                    if (!params.isTmpSave()) {

                        // 編集が開始された時の state を取得する
                        String oldStatus = docInfo.getStatus();

                        if (oldStatus.equals(STATUS_NONE)) {
                            //------------------------------
                            // NONEから確定への遷移 newSave
                            //------------------------------
                            sendClaim = params.isSendClaim();
                            sendLabtest = params.isSendLabtest();

                        } else if (oldStatus.equals(STATUS_TMP)) {
                            //-------------------------------------
                            // 仮保存から確定へ遷移する場合 saveFromTmp
                            // ------------------------------------
                            sendClaim = params.isSendClaim();
                            sendLabtest = params.isSendLabtest();

                        } else {
                            //-------------------------------------
                            // 確定から確定（修正の場合に相当する）
                            //-------------------------------------
                            sendClaim = params.isSendClaim();
                            sendLabtest = params.isSendLabtest();
                        }

                        //-------------------------------------
                        // 保存時の state を final にセットする
                        //-------------------------------------
                        docInfo.setStatus(STATUS_FINAL);

                    } else {
                        //-------------------------------------
                        // 仮保存の場合 CLAIM 送信しない
                        //-------------------------------------
                        sendClaim = false;
                        sendMml = false;
                        sendLabtest = false;
                        docInfo.setStatus(STATUS_TMP);
                    }

                    // titleを設定する
                    docInfo.setTitle(params.getTitle());

                    // デフォルトのアクセス権を設定をする TODO
                    AccessRightModel ar = new AccessRightModel();
                    ar.setPermission(PERMISSION_ALL);
                    ar.setLicenseeCode(ACCES_RIGHT_CREATOR);
                    ar.setLicenseeName(ACCES_RIGHT_CREATOR_DISP);
                    ar.setLicenseeCodeType(ACCES_RIGHT_FACILITY_CODE);
                    docInfo.addAccessRight(ar);

                    // 患者のアクセス権を設定をする
                    if (params.isAllowPatientRef()) {
                        ar = new AccessRightModel();
                        ar.setPermission(PERMISSION_READ);
                        ar.setLicenseeCode(ACCES_RIGHT_PATIENT);
                        ar.setLicenseeName(ACCES_RIGHT_PATIENT_DISP);
                        ar.setLicenseeCodeType(ACCES_RIGHT_PERSON_CODE);
                        docInfo.addAccessRight(ar);
                    }

                    // 診療履歴のある施設のアクセス権を設定をする
                    if (params.isAllowClinicRef()) {
                        ar = new AccessRightModel();
                        ar.setPermission(PERMISSION_READ);
                        ar.setLicenseeCode(ACCES_RIGHT_EXPERIENCE);
                        ar.setLicenseeName(ACCES_RIGHT_EXPERIENCE_DISP);
                        ar.setLicenseeCodeType(ACCES_RIGHT_EXPERIENCE_CODE);
                        docInfo.addAccessRight(ar);
                    }

                    // ProgressCourseModule の ModuleInfo を保存しておく
                    ModuleInfoBean soaProgressInfo = null;
                    ModuleInfoBean pProgressInfo = null;
                    ModuleInfoBean[] progressInfos = model.getModuleInfo(MODULE_PROGRESS_COURSE);

                    if (progressInfos == null) {
                        // 存在しない場合は新規に作成する
                        soaProgressInfo = new ModuleInfoBean();
                        soaProgressInfo.setStampName(MODULE_PROGRESS_COURSE);
                        soaProgressInfo.setEntity(MODULE_PROGRESS_COURSE);
                        soaProgressInfo.setStampRole(ROLE_SOA_SPEC);

                        pProgressInfo = new ModuleInfoBean();
                        pProgressInfo.setStampName(MODULE_PROGRESS_COURSE);
                        pProgressInfo.setEntity(MODULE_PROGRESS_COURSE);
                        pProgressInfo.setStampRole(ROLE_P_SPEC);

                    } else {
                        if (progressInfos[0].getStampRole().equals(ROLE_SOA_SPEC)) {
                            soaProgressInfo = progressInfos[0];
                            pProgressInfo = progressInfos[1];
                        } else if (progressInfos[1].getStampRole().equals(ROLE_SOA_SPEC)) {
                            soaProgressInfo = progressInfos[1];
                            pProgressInfo = progressInfos[0];
                        }
                    }

                    //-----------------------------------------------
                    // モデルのモジュールをヌルに設定する
                    // エディタの画面をダンプして生成したモジュールを設定する
                    //-----------------------------------------------
                    model.clearModules();
                    model.clearSchema();

                    // SOA モジュールを追加する
                    if (soa != null && soa.length > 0) {
                        model.addModule(soa);
                    }

                    // ProgressCourse SOA を生成する
                    ProgressCourse soaPc = new ProgressCourse();
                    soaPc.setFreeText(soaText);
                    ModuleModel soaProgressModule = new ModuleModel();
                    soaProgressModule.setModuleInfoBean(soaProgressInfo);
                    soaProgressModule.setModel(soaPc);
                    model.addModule(soaProgressModule);     // SOA テキスト

                    //-----------------------------------------------
                    // Schema を追加する
                    //-----------------------------------------------
                    int maxImageWidth = ClientContext.getInt("image.max.width");
                    int maxImageHeight = ClientContext.getInt("image.max.height");
                    Dimension maxSImageSize = new Dimension(maxImageWidth, maxImageHeight);
                    //
                    if (schemas != null && schemas.length > 0) {
                        // 保存のため Icon を JPEG に変換する
                        for (SchemaModel schema : schemas) {
                            ImageIcon icon = schema.getIcon();
                            icon = adjustImageSize(icon, maxSImageSize);
                            byte[] jpegByte = getJPEGByte(icon.getImage());
                            schema.setJpegByte(jpegByte);
                            schema.setIcon(null);
                            model.addSchema(schema);
                        }
                    }


                    if (plan != null && plan.length > 0) {
                        model.addModule(plan);
                    } else {
                        sendClaim = false;
                    }

                    // ProgressCourse P を生成する
                    ProgressCourse pProgressCourse = new ProgressCourse();
                    pProgressCourse.setFreeText(pText);
                    ModuleModel pProgressModule = new ModuleModel();
                    pProgressModule.setModuleInfoBean(pProgressInfo);
                    pProgressModule.setModel(pProgressCourse);
                    model.addModule(pProgressModule);       // P テキスト

                    // FLAGを設定する
                    // image があるかどうか
                    Collection tmpC = model.getSchema();
                    boolean flag = (tmpC != null && tmpC.size() > 0 ) ? true : false;
                    docInfo.setHasImage(flag);

                    // RP があるかどうか
                    flag = model.getModule(ENTITY_MED_ORDER) != null ? true : false;
                    docInfo.setHasRp(flag);

                    // 処置があるかどうか
                    flag = model.getModule(ENTITY_TREATMENT) != null ? true : false;
                    docInfo.setHasTreatment(flag);

                    // LaboTest があるかどうか
                    flag = model.getModule(ENTITY_LABO_TEST) != null ? true : false;
                    docInfo.setHasLaboTest(flag);

                    //-------------------------------------
                    // EJB3.0 Model の関係を構築する
                    // confirmed, firstConfirmed は設定済み
                    //-------------------------------------
                    KarteBean karte = chart.getKarte();
                    model.setKarteBean(karte);                          // karte
                    model.setUserModel(Project.getUserModel());         // 記録者
                    model.setRecorded(docInfo.getConfirmDate());        // 記録日

                    // Moduleとの関係を設定する
                    Collection<ModuleModel> moduleBeans = model.getModules();
                    int number = 0;
                    for (ModuleModel bean : moduleBeans) {

                        bean.setId(0L);                                 // unsaved-value
                        bean.setKarteBean(karte);                       // Karte
                        bean.setUserModel(Project.getUserModel());      // 記録者
                        bean.setDocumentModel(model);                    // Document
                        bean.setConfirmed(docInfo.getConfirmDate());            // 確定日
                        bean.setFirstConfirmed(docInfo.getFirstConfirmDate());  // 適合開始日
                        bean.setRecorded(docInfo.getConfirmDate());             // 記録日
                        bean.setStatus(STATUS_FINAL);                           // status

                        // 全角を Kill する
                        if (bean.getModel() instanceof BundleMed) {
                            BundleMed med = (BundleMed) bean.getModel();
                            ClaimItem[] items = med.getClaimItem();
                            if (items != null && items.length > 0) {
                                for (ClaimItem item : items) {
                                    String num = item.getNumber();
                                    if (num != null) {
                                        num = ZenkakuUtils.toHalfNumber(num);
                                        item.setNumber(num);
                                    }
                                }
                            }
                            String bNum = med.getBundleNumber();
                            if (bNum != null) {
                                bNum = ZenkakuUtils.toHalfNumber(bNum);
                                med.setBundleNumber(bNum);
                            }
                        } else if (bean.getModel() instanceof ClaimBundle) {
                            ClaimBundle bundle = (ClaimBundle) bean.getModel();
                            ClaimItem[] items = bundle.getClaimItem();
                            if (items != null && items.length > 0) {
                                for (ClaimItem item : items) {
                                    String num = item.getNumber();
                                    if (num != null) {
                                        num = ZenkakuUtils.toHalfNumber(num);
                                        item.setNumber(num);
                                    }
                                }
                            }
                            String bNum = bundle.getBundleNumber();
                            if (bNum != null) {
                                bNum = ZenkakuUtils.toHalfNumber(bNum);
                                bundle.setBundleNumber(bNum);
                            }
                        }

                        bean.setBeanBytes(BeanUtils.getXMLBytes(bean.getModel()));

                        // ModuleInfo を設定する
                        // Name, Role, Entity は設定されている
                        ModuleInfoBean mInfo = bean.getModuleInfoBean();
                        mInfo.setStampNumber(number++);
                    }

                    // 画像との関係を設定する
                    number = 0;
                    Collection<SchemaModel> imagesimages = model.getSchema();
                    if (imagesimages != null && imagesimages.size() > 0) {
                        for (SchemaModel bean : imagesimages) {
                            bean.setId(0L);                                         // unsaved
                            bean.setKarteBean(karte);                               // Karte
                            bean.setUserModel(Project.getUserModel());              // Creator
                            bean.setDocumentModel(model);                           // Document
                            bean.setConfirmed(docInfo.getConfirmDate());            // 確定日
                            bean.setFirstConfirmed(docInfo.getFirstConfirmDate());  // 適合開始日
                            bean.setRecorded(docInfo.getConfirmDate());             // 記録日
                            bean.setStatus(STATUS_FINAL);                           // Status
                            bean.setImageNumber(number);

                            ExtRefModel ref = bean.getExtRefModel();
                            StringBuilder sb = new StringBuilder();
                            sb.append(model.getDocInfoModel().getDocId());
                            sb.append("-");
                            sb.append(number);
                            sb.append(".jpg");
                            ref.setHref(sb.toString());
                            number++;
                        }
                    }

                    //-------------------------------------------------------
                    // 送信に必要な環境を設定する
                    //-------------------------------------------------------
                    model.getDocInfoModel().setSendClaim(isSendClaim());
                    model.getDocInfoModel().setSendMml(isSendMML());
                    model.getDocInfoModel().setSendLabtest(isSendLabtest());
                    
                    //--------------------------------------------------------
                    // カルテ保存、CLAIM 送信
                    //--------------------------------------------------------
                    //----------------------------------------------
                    // Prepare
                    //----------------------------------------------
                    List<IKarteSender> senderList = new ArrayList<IKarteSender>(3);
                    PluginLoader<IKarteSender> loader = PluginLoader.load(IKarteSender.class);
                    Iterator<IKarteSender> iter = loader.iterator();
                    while (iter.hasNext()) {
                        IKarteSender sender = iter.next();
                        sender.setContext(chart);
                        sender.prepare(model);
                        senderList.add(sender);
                    }

                    //----------------------------------------------
                    // 保存
                    //----------------------------------------------
                    DocumentDelegater ddl = new DocumentDelegater();
                    long pvtPK = chart.getPatientVisit()!=null ? chart.getPatientVisit().getId() : 0L;
                    if (sendClaim && pvtPK!=0L) {
                        theState = modify ? 4 : 2;
                    }

                    if (theState!=0 && pvtPK!=0L) {
                        ddl.putAndUpdatePVTState(model, pvtPK, theState);
                    } else {
                        ddl.putKarte(model);
                    }

                    //----------------------------------------------
                    // Send
                    //----------------------------------------------
                    for (IKarteSender sender : senderList) {
                        sender.send(model);
                    }
                    
                    return null;
                }
                
                @Override
                protected void succeeded(Void result) {

                    // status 変更
                    chart.setChartState(theState);

                    // 印刷
                    int copies = params.getPrintCount();
                    if (copies > 0) {
                        printPanel2(chart.getContext().getPageFormat(), copies, false);
                    }

                    // 編集不可に設定する
                    soaPane.setEditableProp(false);
                    pPane.setEditableProp(false);

                    // 状態遷移する
                    stateMgr.setSaved(true);

                    //--------------------------------
                    // 閉じる
                    //--------------------------------
                    if (ef!=null) {
                        ef.close();
                    }

                    //--------------------------------
                    // 文書履歴の更新を通知する
                    // ここで保存したカルテが履歴に表示される
                    //--------------------------------
                    chart.getDocumentHistory().getDocumentHistory();
                }
            };
            
            dbTask.execute();
        }
    }
    
    /**
     * このエディタの抽象状態クラス
     */
    protected abstract class EditorState {

        public EditorState() {
        }

        public abstract boolean isDirty();

        public abstract void controlMenu();
    }

    /**
     * No dirty 状態クラス
     */
    protected final class NoDirtyState extends EditorState {

        public NoDirtyState() {
        }

        @Override
        public void controlMenu() {
            Chart chart = getContext();
            chart.enabledAction(GUIConst.ACTION_SAVE, false);   // 保存
            chart.enabledAction(GUIConst.ACTION_PRINT, false);  // 印刷
            chart.enabledAction(GUIConst.ACTION_CUT, false);
            chart.enabledAction(GUIConst.ACTION_COPY, false);
            chart.enabledAction(GUIConst.ACTION_PASTE, false);
            chart.enabledAction(GUIConst.ACTION_UNDO, false);
            chart.enabledAction(GUIConst.ACTION_REDO, false);
            // 元町皮ふ科
            chart.enabledAction(GUIConst.ACTION_SEND_CLAIM, false);
            chart.enabledAction(GUIConst.ACTION_INSERT_TEXT, false);
            chart.enabledAction(GUIConst.ACTION_INSERT_SCHEMA, false);
            chart.enabledAction(GUIConst.ACTION_INSERT_STAMP, false);
            chart.enabledAction(GUIConst.ACTION_CHANGE_NUM_OF_DATES_ALL, (getMode()==DOUBLE_MODE)); //true
            chart.enabledAction(GUIConst.ACTION_SELECT_INSURANCE, (getMode()==DOUBLE_MODE)); //true
        }

        @Override
        public boolean isDirty() {
            return false;
        }
    }

    /**
     * Dirty 状態クラス
     */
    protected final class DirtyState extends EditorState {

        public DirtyState() {
        }

        @Override
        public void controlMenu() {
            Chart chart = getContext();
            chart.enabledAction(GUIConst.ACTION_SAVE, true);
            chart.enabledAction(GUIConst.ACTION_PRINT, true);
            chart.enabledAction(GUIConst.ACTION_CHANGE_NUM_OF_DATES_ALL, (getMode()==DOUBLE_MODE)); //true
            chart.enabledAction(GUIConst.ACTION_SELECT_INSURANCE, (getMode()==DOUBLE_MODE));    //true
        }

        @Override
        public boolean isDirty() {
            return true;
        }
    }

    /**
     * EmptyNew 状態クラス
     */
    protected final class SavedState extends EditorState {

        public SavedState() {
        }

        @Override
        public void controlMenu() {
            Chart chart = getContext();
            chart.enabledAction(GUIConst.ACTION_SAVE, false);
            chart.enabledAction(GUIConst.ACTION_PRINT, true);
            chart.enabledAction(GUIConst.ACTION_CUT, false);
            chart.enabledAction(GUIConst.ACTION_COPY, false);
            chart.enabledAction(GUIConst.ACTION_PASTE, false);
            chart.enabledAction(GUIConst.ACTION_UNDO, false);
            chart.enabledAction(GUIConst.ACTION_REDO, false);

            // 元町皮ふ科
            chart.enabledAction(GUIConst.ACTION_SEND_CLAIM, sendClaim);

            chart.enabledAction(GUIConst.ACTION_INSERT_TEXT, false);
            chart.enabledAction(GUIConst.ACTION_INSERT_SCHEMA, false);
            chart.enabledAction(GUIConst.ACTION_INSERT_STAMP, false);
            chart.enabledAction(GUIConst.ACTION_CHANGE_NUM_OF_DATES_ALL, false);
            chart.enabledAction(GUIConst.ACTION_SELECT_INSURANCE, false);
        }

        @Override
        public boolean isDirty() {
            return false;
        }
    }

    /**
     * 状態マネージャ
     */
    protected final class StateMgr {

        private EditorState noDirtyState = new NoDirtyState();
        private EditorState dirtyState = new DirtyState();
        private EditorState savedState = new SavedState();
        private EditorState currentState;

        public StateMgr() {
            currentState = noDirtyState;
        }

        public boolean isDirty() {
            return currentState.isDirty();
        }

        public void setDirty(boolean dirty) {
            currentState = dirty ? dirtyState : noDirtyState;
            currentState.controlMenu();
        }

        public void setSaved(boolean saved) {
            if (saved) {
                currentState = savedState;
                currentState.controlMenu();
            }
        }

        public void controlMenu() {
            currentState.controlMenu();
        }
    }
}
