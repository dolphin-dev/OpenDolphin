package open.dolphin.client;


import java.awt.Dimension;
import java.util.prefs.Preferences;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import open.dolphin.infomodel.KarteBean;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.project.Project;


public class PatientInspector {
    
    // 個々のインスペクタ
    // 患者基本情報
    private BasicInfoInspector basicInfoInspector;
    
    // 来院歴
    private PatientVisitInspector patientVisitInspector;
    
    // 患者メモ
    private MemoInspector memoInspector;
    
    // 文書履歴
    private DocumentHistory docHistory;
    
    // アレルギ
    private AllergyInspector allergyInspector;
    
    // 身長体重
    private PhysicalInspector physicalInspector;
    
    // インスペクタを格納するタブペイン View
    private JTabbedPane tabbedPane;
    
    // このクラスのコンテナパネル View
    private JPanel container;
    
    // Context このインスペクタの親コンテキスト
    private ChartPlugin context;
    
    /**
     * 患者インスペクタクラスを生成する。
     *
     * @param context インスペクタの親コンテキスト
     */
    public PatientInspector(ChartPlugin context) {
        
        // このインスペクタが格納される Chart Object
        setContext(context);
        
        // GUI を初期化する
        initComponents();
    }
    
    public void dispose() {
        // List をクリアする
        docHistory.clear();
        allergyInspector.clear();
        physicalInspector.clear();
    }
    
    /**
     * コンテキストを返す。
     */
    public ChartPlugin getContext() {
        return context;
    }
    
    /**
     * コンテキストを設定する。
     */
    public void setContext(ChartPlugin context) {
        this.context = context;
    }
    
    /**
     * 患者カルテを返す。
     * @return  患者カルテ
     */
    public KarteBean getKarte() {
        return context.getKarte();
    }
    
    /**
     * 患者を返す。
     * @return 患者
     */
    public PatientModel getPatient() {
        return context.getKarte().getPatient();
    }
    
    /**
     * 基本情報インスペクタを返す。
     * @return 基本情報インスペクタ
     */
    public BasicInfoInspector getBasicInfoInspector() {
        return basicInfoInspector;
    }
    
    /**
     * 来院歴インスペクタを返す。
     * @return 来院歴インスペクタ
     */
    public PatientVisitInspector getPatientVisitInspector() {
        return patientVisitInspector;
    }
    
    /**
     * 患者メモインスペクタを返す。
     * @return 患者メモインスペクタ
     */
    public MemoInspector getMemoInspector() {
        return memoInspector;
    }
    
    /**
     * 文書履歴インスペクタを返す。
     * @return 文書履歴インスペクタ
     */
    public DocumentHistory getDocumentHistory() {
        return docHistory;
    }
    
    /**
     * レイアウトのためにインスペクタのコンテナパネルを返す。
     * @return インスペクタのコンテナパネル
     */
    public JPanel getPanel() {
        return container;
    }
    
    /**
     * GUI コンポーネントを初期化する。
     *
     */
    private void initComponents() {
        
        // タブ及びボーダタイトル名を取得する
        // 来院歴
        String pvtTitle = ClientContext.getString("patientInspector.pvt.title");
        
        // 文書履歴
        String docHistoryTitle = ClientContext.getString("patientInspector.docHistory.title");
        
        // アレルギ
        String allergyTitle = ClientContext.getString("patientInspector.allergy.title");
        
        // 身長体重
        String physicalTitle = ClientContext.getString("patientInspector.physical.title");
        
        // メモ
        String memoTitle = ClientContext.getString("patientInspector.memo.title");
        
        // 各インスペクタを生成する
        basicInfoInspector = new BasicInfoInspector(context);
        patientVisitInspector = new PatientVisitInspector(context);
        memoInspector = new MemoInspector(context);
        docHistory = new DocumentHistory(getContext());
        allergyInspector = new AllergyInspector(context);
        physicalInspector = new PhysicalInspector(context);
        
        // 来院歴とメモは常に見えるように配置する
        JPanel patientVisitPanel = patientVisitInspector.getPanel();
        patientVisitPanel.setBorder(BorderFactory.createTitledBorder(pvtTitle));
        JPanel memoPanel = memoInspector.getPanel();
        memoPanel.setBorder(BorderFactory.createTitledBorder(memoTitle));
        
        // タブパネルへ格納する(文書履歴、健康保険、アレルギ、身長体重はタブパネルで切り替え表示する)
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab(docHistoryTitle, docHistory.getPanel());
        tabbedPane.addTab(allergyTitle, allergyInspector.getPanel());
        tabbedPane.addTab(physicalTitle, physicalInspector.getPanel());
        
        // 全体を配置する
        Preferences pref = Project.getPreferences();
        container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        int memoLoc = pref.getInt(Project.INSPECTOR_MEMO_LOCATION, 0);
        
        switch (memoLoc) {
            
            case 0:
                // カレンダ、文書履歴、メモ （デフォルト）
                container.add(patientVisitPanel);
                container.add(Box.createRigidArea(new Dimension(0,7)));
                container.add(tabbedPane);
                container.add(Box.createRigidArea(new Dimension(0,7)));
                container.add(memoPanel);
                break;
                
            case 1:
                // メモ、カレンダ、文書履歴
                container.add(memoPanel);
                container.add(Box.createRigidArea(new Dimension(0,7)));
                container.add(patientVisitPanel);
                container.add(Box.createRigidArea(new Dimension(0,7)));
                container.add(tabbedPane);
                break;
                
            case 2:
                // メモ、文書履歴カレンダ
                container.add(memoPanel);
                container.add(Box.createRigidArea(new Dimension(0,7)));
                container.add(tabbedPane);
                container.add(Box.createRigidArea(new Dimension(0,7)));
                container.add(patientVisitPanel);
                break;
        }
    }
}
