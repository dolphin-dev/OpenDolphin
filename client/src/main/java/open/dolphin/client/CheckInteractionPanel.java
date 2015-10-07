
package open.dolphin.client;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import javax.swing.*;
import open.dolphin.delegater.MasudaDelegater;
import open.dolphin.delegater.OrcaDelegater;
import open.dolphin.delegater.OrcaDelegaterFactory;
import open.dolphin.helper.ComponentMemory;
import open.dolphin.helper.InfiniteProgressBar;
import open.dolphin.infomodel.*;
import open.dolphin.util.StringTool;

/**
 * 薬剤併用チェックを行うためのパネル
 *
 * @author masuda, Masuda Naika
 */
public class CheckInteractionPanel {

    private Chart context;
    private JDialog dialog;
    private long karteId;
    private HashMap<String, String[]> rirekiItems;      // カルテに記録されている薬剤
    private HashMap<String, String> kensakuItems;        // 検索にマッチした薬剤

    private static final String yakuzaiClassCode = "2";    // 薬剤のclaim class code
    private static final int searchPeriod = 3;
    private BlockGlass blockGlass;

    private JButton btn_Exit;
    private JButton btn_Search;
    private JLabel lbl_Info;
    private JLabel lbl_Past_count;
    private JLabel lbl_Name;
    private JLabel lbl_Result;
    private JTextField keywordFld;
    private JTextArea resultArea;
    private JPanel view;
    private InfiniteProgressBar progressBar;

    public CheckInteractionPanel() {
        initComponents();
    }

    public void enter(Chart chart) {
        context = chart;
        karteId = context.getKarte().getId();
        
        final SwingWorker worker = new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                collectMedicine();
                return null;
            }
            
            @Override
            protected void done() {
                try {
                    get();
                    progressBar.stop();
                    progressBar = null;
                    showDialog();
                } catch (InterruptedException | ExecutionException ex) {
                    ex.printStackTrace(System.err);
                }
            }    
        };
        
        worker.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getNewValue()==SwingWorker.StateValue.STARTED) {
                    progressBar = new InfiniteProgressBar(ClientContext.getMyBundle(CheckInteractionPanel.class).getString("message.progress.checkInteraction"), ClientContext.getMyBundle(CheckInteractionPanel.class).getString("note.progress.fetchingRPHistory"), context.getFrame());
                    progressBar.start();
                } else if (e.getNewValue()==SwingWorker.StateValue.DONE) {
                    worker.removePropertyChangeListener(this);
                }
            }
        });
        
        worker.execute();       
    }

    private void showDialog(){
        
        java.util.ResourceBundle bundle = ClientContext.getMyBundle(CheckInteractionPanel.class);
        if (rirekiItems!=null) {
            int cnt = rirekiItems.size();
            StringBuilder sb = new StringBuilder();
            sb.append(bundle.getString("totalKindsOfMedication")).append(cnt);
            lbl_Past_count.setText(sb.toString());
        }
        dialog = new JDialog((Frame)context.getFrame(), true);      
        dialog.setContentPane(view);

        blockGlass = new BlockGlass();
        dialog.setGlassPane(blockGlass);
        blockGlass.setSize(dialog.getSize());

        // dialogのタイトルを設定
        StringBuilder sb = new StringBuilder();
        sb.append(context.getPatient().getFullName());
        sb.append("(");
        sb.append(context.getPatient().getKanaName());
        sb.append(") : ");
        sb.append(context.getPatient().getPatientId());
        sb.append(bundle.getString("contraindicationsSearch"));
        dialog.setTitle(ClientContext.getFrameTitle(sb.toString()));
        dialog.pack();
        ComponentMemory cm = new ComponentMemory(dialog, new Point(100, 100), dialog.getPreferredSize(), CheckInteractionPanel.this);
        cm.setToPreferenceBounds();
        dialog.setVisible(true);
    }

    private void collectMedicine() {

        rirekiItems = new HashMap<>();

        // 過去３ヶ月の薬剤・注射ののModuleModelを取得する
        MasudaDelegater del = MasudaDelegater.getInstance();
        List<String> entities = new ArrayList<>();
        entities.add(IInfoModel.ENTITY_MED_ORDER);
        entities.add(IInfoModel.ENTITY_INJECTION_ORDER);

        GregorianCalendar gcTo = new GregorianCalendar();
        gcTo.add(GregorianCalendar.DAY_OF_MONTH,1);
        Date toDate = gcTo.getTime();
        GregorianCalendar gcFrom = new GregorianCalendar();
        gcFrom.add(GregorianCalendar.MONTH, -searchPeriod);
        Date fromDate = gcFrom.getTime();
        
        List<ModuleModel> pastModuleList = del.getModulesEntitySearch(karteId, fromDate, toDate, entities);
        if (pastModuleList == null) {
            return;
        }

        // ModuleModelの薬剤を取得
        for (ModuleModel mm : pastModuleList) {
            ClaimBundle cb = (ClaimBundle) mm.getModel();
            for (ClaimItem ci : cb.getClaimItem()) {
                if (yakuzaiClassCode.equals(ci.getClassCode())) {     // 用法などじゃなくて薬剤なら、薬剤リストに追加
                    final SimpleDateFormat frmt = new SimpleDateFormat("yyyy-MM-dd");
                    String code = ci.getCode();     // コード
                    String name = ci.getName();     // 薬剤名
                    String date = frmt.format(mm.getStarted());     // 処方日
                    rirekiItems.put(code, new String[]{name, date});
                }
            }
        }
    }

    private void closePanel() {
        dialog.setVisible(false);
        dialog.dispose();
    }

    private void search() {
        final SwingWorker worker = new SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {
                blockGlass.block();
                searchTask();
                return null;
            }
            @Override
            protected void done() {
                try {
                    get();
                    blockGlass.unblock();
                } catch (InterruptedException | ExecutionException ex) {
                }
            }
        };
        worker.execute();
    }

    private void searchTask() {

        final int minKeywordLength = 3;     // キーワードの最短文字数制限

        // 空白だったらそのままリターン
        final String targetName = StringTool.hiraganaToKatakana(keywordFld.getText());
        if ("".equals(targetName)) {
            resultArea.setText("");
            return;
        }
        
        java.util.ResourceBundle bundle = ClientContext.getMyBundle(CheckInteractionPanel.class);
        
        // キーワードが短すぎるなら
        if (targetName.length() < minKeywordLength) {
            String fmt = bundle.getString("meesageFormat.medicineNameLength");
            String msg = new MessageFormat(fmt).format(new Object[]{minKeywordLength});
            resultArea.setText(msg);
            return;
        }
        // 処方履歴がなかったら
        if (rirekiItems.isEmpty()) {
            resultArea.setText(bundle.getString("noHistory"));
            return;
        }

        StringBuilder sb = new StringBuilder();
        kensakuItems = new HashMap<>();
        // ORCAでキーワードに当てはまる薬剤を取得する。
        SimpleDateFormat effectiveFormat = new SimpleDateFormat("yyyyMMdd");
        String d = effectiveFormat.format(new Date());
        
        //SqlMasterDao daoMaster = SqlMasterDao.getInstance();
        OrcaDelegater daoMaster = OrcaDelegaterFactory.create();
        List<TensuMaster> medicineEntries = null;
        
        try {
            medicineEntries = daoMaster.getTensuMasterByName(targetName, d, false);
        } catch (Exception e) {
        }
        
        // 検索薬剤がなかったら
        if (medicineEntries==null || medicineEntries.isEmpty()) {
            resultArea.setText(bundle.getString("cannotFindTargetMedicine"));
            return;
        }
        // 検索対象のコードと薬剤名を記録する
        for (TensuMaster me : medicineEntries) {
            kensakuItems.put(me.getSrycd(), me.getName());
        }
        // 検索する薬剤コードと名前の配列を用意する
        Collection<String> codes1 = kensakuItems.keySet();
        // 過去処方薬のコードの配列を用意する。
        Collection<String> codes2 = rirekiItems.keySet();

        // データベースで検索する。まとめてSQLをなげる
        OrcaDelegater odl = OrcaDelegaterFactory.create();
        List<DrugInteractionModel> list = null;
        try {
            list = odl.checkInteraction(codes1, codes2);
        } catch (Exception e) {
        }
        
        // 結果の処理
        if (list != null && !list.isEmpty()) {
            for (DrugInteractionModel model : list){
                sb.append(kensakuItems.get(model.getSrycd1()));
                sb.append(bundle.getString("text.and"));
                String[] data = rirekiItems.get(model.getSrycd2());
                sb.append(data[0]);
                sb.append(" (");
                sb.append(data[1]);
                sb.append(")\n");
                sb.append(model.getSskijo());
                sb.append(" ");
                sb.append(model.getSyojyoucd());
                sb.append("\n");
            }
        }

        if (sb.length() == 0) {
            sb.append(bundle.getString("targetMedicineToSearch"));
            for (String str : kensakuItems.values()) {
                sb.append(str);
                sb.append("\n");
            }
            sb.append(bundle.getString("CouldnotFindTheInteractionData"));
        }

        resultArea.setText(sb.toString());
    }

    private void initComponents() {
        
        java.util.ResourceBundle bundle = ClientContext.getMyBundle(CheckInteractionPanel.class);
        
        lbl_Info = new JLabel(bundle.getString("checkPast3monthOfMedicationANdContradication"));
        lbl_Past_count = new JLabel(bundle.getString("numMedications"));
        lbl_Name = new JLabel(bundle.getString("medicineName"));
        lbl_Result = new JLabel(bundle.getString("results"));
        keywordFld = new JTextField();
        btn_Exit = new JButton(bundle.getString("quit"));
        btn_Search = new JButton(bundle.getString("search"));
        resultArea = new JTextArea();
        JScrollPane scroll = new JScrollPane(resultArea);

        view = new JPanel();
        view.setLayout(new BorderLayout());
        JPanel north = new JPanel();
        north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
        north.add(Box.createVerticalStrut(5));
        lbl_Info.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(lbl_Info);
        panel.add(Box.createHorizontalGlue());
        panel.add(lbl_Past_count);
        north.add(panel);
        north.add(Box.createVerticalStrut(5));
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(lbl_Name);
        panel.add(keywordFld);
        panel.add(btn_Search);
        north.add(panel);
        view.add(north, BorderLayout.NORTH);
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(Box.createVerticalStrut(5));
        lbl_Result.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        panel.add(lbl_Result);
        scroll.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        panel.add(scroll);
        view.add(panel, BorderLayout.CENTER);
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(Box.createHorizontalGlue());
        panel.add(btn_Exit);
        view.add(panel, BorderLayout.SOUTH);
        
        view.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));

        resultArea.setLineWrap(true);
        btn_Search.addActionListener((ActionEvent e) -> {
            search();
        });
        btn_Exit.addActionListener((ActionEvent e) -> {
            closePanel();
        });
        
        keywordFld.addFocusListener(AutoKanjiListener.getInstance());
        keywordFld.addActionListener((ActionEvent e) -> {
            search();
        });
    }
}
