package open.dolphin.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Date;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentListener;
import open.dolphin.delegater.DocumentDelegater;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.PatientMemoModel;
import open.dolphin.project.Project;

/**
 * 患者のメモを表示し編集するクラス。
 * 
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class MemoInspector {

    private boolean dirty;

    private JPanel memoPanel;
    
    private JTextArea memoArea;

    private PatientMemoModel patientMemoModel;
    
    private ChartImpl context;

    /**
     * MemoInspectorオブジェクトを生成する。
     */
    public MemoInspector(ChartImpl context) {
        
        this.context = context;

        initComponents();
        update();

        memoArea.getDocument().addDocumentListener(new DocumentListener() {
            
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                dirtySet();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                dirtySet();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
            }
        });

        // TransferHandlerを設定する
        memoArea.setTransferHandler(new BundleTransferHandler(context.getChartMediator(), memoArea));

        // 右クリックによる編集メニューを登録する
        //memoArea.addMouseListener(new CutCopyPasteAdapter(memoArea));
        memoArea.addMouseListener(CutCopyPasteAdapter.getInstance());
    }

    /**
     * レイアウト用のパネルを返す。
     * @return レイアウトパネル
     */
    public JPanel getPanel() {
        return memoPanel;
    }

    /**
     * GUI コンポーネントを初期化する。
     */
    private void initComponents() {
        memoArea = new JTextArea(5, 10);
        memoArea.putClientProperty("karteCompositor", this);
        memoArea.setLineWrap(true);
        memoArea.setMargin(new java.awt.Insets(3, 3, 2, 2));
        memoArea.addFocusListener(AutoKanjiListener.getInstance());
        memoArea.setToolTipText("メモに使用します。内容は自動的に保存されます。");

        memoPanel = new JPanel(new BorderLayout());
        if (!ClientContext.isMac()) {
            memoPanel.add(new JScrollPane(memoArea), BorderLayout.CENTER);
        } else {
            memoPanel.add(memoArea, BorderLayout.CENTER);
        }

        Dimension size = memoPanel.getPreferredSize();
        int h = size.height;
        int w = 268;
        size = new Dimension(w, h);
        memoPanel.setMinimumSize(size);
        memoPanel.setMaximumSize(size);
    }

    /**
     * 患者メモを表示する。
     */
    private void update() {
        //List list = context.getKarte().getEntryCollection("patientMemo");
        List list = context.getKarte().getMemoList();
        if (list != null && list.size()>0) {
            patientMemoModel = (PatientMemoModel) list.get(0);
            memoArea.setText(patientMemoModel.getMemo());
        }
    }

    /**
     * カルテのクローズ時にコールされ、患者メモを更新する。
     */
    public void save() {

        if (!dirty) {
            return;
        }

        if (patientMemoModel == null) {
            patientMemoModel =  new PatientMemoModel();
        }
        patientMemoModel.setKarteBean(context.getKarte());
        patientMemoModel.setUserModel(Project.getUserModel());
        Date confirmed = new Date();
        patientMemoModel.setConfirmed(confirmed);
        patientMemoModel.setRecorded(confirmed);
        patientMemoModel.setStarted(confirmed);
        patientMemoModel.setStatus(IInfoModel.STATUS_FINAL);
        patientMemoModel.setMemo(memoArea.getText().trim());

        final DocumentDelegater ddl = new DocumentDelegater();

        Runnable r = new Runnable() {

            @Override
            public void run() {
                ddl.updatePatientMemo(patientMemoModel);
                patientMemoModel = null;
                context = null;
            }
        };

        Thread t = new Thread(r);
        t.setPriority(Thread.NORM_PRIORITY);
        t.start();
    }

    /**
     * メモ内容が変化した時、ボタンを活性化する。
     */
    private void dirtySet() {
        dirty = true;
    }
}
