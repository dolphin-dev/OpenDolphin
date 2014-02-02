
package open.dolphin.client;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
        
//minagawa^
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
                } catch (InterruptedException ex) {
                    ex.printStackTrace(System.err);
                } catch (ExecutionException ex) {
                    ex.printStackTrace(System.err);
                }
            }    
        };
        
        worker.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getNewValue()==SwingWorker.StateValue.STARTED) {
                    progressBar = new InfiniteProgressBar("相互作用チェック", "投薬歴を収集しています...", context.getFrame());
                    progressBar.start();
                } else if (e.getNewValue()==SwingWorker.StateValue.DONE) {
                    worker.removePropertyChangeListener(this);
                }
            }
        });
        
        worker.execute();
//minagawa$        
    }

    private void showDialog(){
        
        if (rirekiItems!=null) {
            int cnt = rirekiItems.size();
            StringBuilder sb = new StringBuilder();
            sb.append("投薬種類数=").append(cnt);
            lbl_Past_count.setText(sb.toString());
        }
//minagawa^        
        //dialog = new JDialog((Frame) null, true);
        //ClientContext.setDolphinIcon(dialog);
        //dialog.setModal(true);
        dialog = new JDialog((Frame)context.getFrame(), true);
//minagawa$        
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
        sb.append(" - 薬剤併用情報検索");
        dialog.setTitle(ClientContext.getFrameTitle(sb.toString()));
        dialog.pack();
        ComponentMemory cm = new ComponentMemory(dialog, new Point(100, 100), dialog.getPreferredSize(), CheckInteractionPanel.this);
        cm.setToPreferenceBounds();
        dialog.setVisible(true);
    }

    private void collectMedicine() {

        rirekiItems = new HashMap<String, String[]>();

        // 過去３ヶ月の薬剤・注射ののModuleModelを取得する
        MasudaDelegater del = MasudaDelegater.getInstance();
        List<String> entities = new ArrayList<String>();
        entities.add(IInfoModel.ENTITY_MED_ORDER);
        entities.add(IInfoModel.ENTITY_INJECTION_ORDER);

//minagawa^ LSC Test 今日も含める
        GregorianCalendar gcTo = new GregorianCalendar();
        gcTo.add(GregorianCalendar.DAY_OF_MONTH,1);
        Date toDate = gcTo.getTime();
        GregorianCalendar gcFrom = new GregorianCalendar();
        gcFrom.add(GregorianCalendar.MONTH, -searchPeriod);
        Date fromDate = gcFrom.getTime();
//minagawa$        
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
                } catch (Exception ex) {
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
        // キーワードが短すぎるなら
        if (targetName.length() < minKeywordLength) {
            resultArea.setText("薬剤名は" + minKeywordLength + "文字以上入力してください。");
            return;
        }
        // 処方履歴がなかったら
        if (rirekiItems.isEmpty()) {
            resultArea.setText("処方履歴がありません。");
            return;
        }

        StringBuilder sb = new StringBuilder();
        kensakuItems = new HashMap<String, String>();
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
//        if (!daoMaster.isNoError()) {
//            resultArea.setText("ORCAに接続できません。");
//            return;
//        }
        // 検索薬剤がなかったら
        if (medicineEntries==null || medicineEntries.isEmpty()) {
            resultArea.setText("対象薬剤が見つかりません");
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
        //SqlMiscDao daoMisc = SqlMiscDao.getInstance();
        OrcaDelegater odl = OrcaDelegaterFactory.create();
        List<DrugInteractionModel> list = null;
        try {
            list = odl.checkInteraction(codes1, codes2);
        }catch (Exception e) {
        }
//        if (!daoMisc.isNoError()) {
//            resultArea.setText("ORCAに接続できません。");
//            return;
//        }
        // 結果の処理
        if (list != null && !list.isEmpty()) {
            for (DrugInteractionModel model : list){
                sb.append(kensakuItems.get(model.getSrycd1()));
                sb.append(" と ");
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
            sb.append("検索対象薬剤：\n");
            for (String str : kensakuItems.values()) {
                sb.append(str);
                sb.append("\n");
            }
            sb.append("相互作用情報は見つかりませんでした。");
        }

        resultArea.setText(sb.toString());
    }

    private void initComponents() {
        
        lbl_Info = new JLabel("過去３ヶ月間の投薬との併用情報を調べます。");
        lbl_Past_count = new JLabel("投薬数=0");
        lbl_Name = new JLabel("薬剤名");
        lbl_Result = new JLabel("結果");
        keywordFld = new JTextField();
        btn_Exit = new JButton("終了");
        btn_Search = new JButton("検索");
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
        btn_Search.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                search();
            }
        });
        btn_Exit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                closePanel();
            }
        });
        
        keywordFld.addFocusListener(AutoKanjiListener.getInstance());
        keywordFld.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                search();
            }
        });
    }
}
