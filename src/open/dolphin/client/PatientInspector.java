package open.dolphin.client;

import java.awt.Dimension;
import javax.swing.BorderFactory;
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
    private ChartImpl context;
    
    private boolean bMemo;
    private boolean bAllergy;
    private boolean bPhysical;
    private boolean bCalendar;  
    
    /**
     * 患者インスペクタクラスを生成する。
     *
     * @param context インスペクタの親コンテキスト
     */
    public PatientInspector(ChartImpl context) {
        
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
    public ChartImpl getContext() {
        return context;
    }
    
    /**
     * コンテキストを設定する。
     */
    public void setContext(ChartImpl context) {
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
    
    
    private void initComponents() {
        
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
        
        String topInspector = Project.getPreferences().get("topInspector", "メモ");
        String secondInspector = Project.getPreferences().get("secondInspector", "カレンダ");
        String thirdInspector = Project.getPreferences().get("thirdInspector", "文書履歴");
        String forthInspector = Project.getPreferences().get("forthInspector", "アレルギ");
        
        // 各インスペクタを生成する
        basicInfoInspector = new BasicInfoInspector(context);
        patientVisitInspector = new PatientVisitInspector(context);
        memoInspector = new MemoInspector(context);
        docHistory = new DocumentHistory(getContext());
        allergyInspector = new AllergyInspector(context);
        physicalInspector = new PhysicalInspector(context);
        
        // タブパネルへ格納する(文書履歴、健康保険、アレルギ、身長体重はタブパネルで切り替え表示する)
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab(docHistoryTitle, docHistory.getPanel());
        
        int prefW = 260;
        int prefW2 = 260;
        if (ClientContext.isMac()) {
            prefW2 += 20;
        }
        basicInfoInspector.getPanel().setPreferredSize(new Dimension(prefW2, 40));
        basicInfoInspector.getPanel().setMaximumSize(new Dimension(prefW2, 40));
        basicInfoInspector.getPanel().setMinimumSize(new Dimension(prefW2, 40));
        memoInspector.getPanel().setPreferredSize(new Dimension(prefW, 70));
        allergyInspector.getPanel().setPreferredSize(new Dimension(prefW, 100));
        docHistory.getPanel().setPreferredSize(new Dimension(prefW, 280));
        physicalInspector.getPanel().setPreferredSize(new Dimension(prefW, 110));
        //int prefH = patientVisitInspector.getPanel().getPreferredSize().height;
        //patientVisitInspector.getPanel().setPreferredSize(new Dimension(prefW, prefH));
        
        container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        
        // 左側のレイアウトを行う
        layoutRow(container, topInspector);
        layoutRow(container, secondInspector);
        layoutRow(container, thirdInspector);
        layoutRow(container, forthInspector);
        
        // 左側にレイアウトされなかったものをタブに格納する
        if (!bMemo) {
            tabbedPane.addTab(memoTitle, memoInspector.getPanel());
        }
        
        if (!bCalendar) {
            tabbedPane.addTab(pvtTitle, patientVisitInspector.getPanel());
        }
        
        if (!bAllergy) {
            tabbedPane.addTab(allergyTitle, allergyInspector.getPanel());
        }
        
        if (!bPhysical) {
            tabbedPane.addTab(physicalTitle, physicalInspector.getPanel());
        }
    }
    
    private void layoutRow(JPanel content, String itype) {
        
        if (itype.equals("メモ")) {
           memoInspector.getPanel().setBorder(BorderFactory.createTitledBorder("メモ"));
           content.add(memoInspector.getPanel());
           bMemo = true;
        
        } else if (itype.equals("カレンダ")) {
            patientVisitInspector.getPanel().setBorder(BorderFactory.createTitledBorder("来院歴"));
            content.add(patientVisitInspector.getPanel());
            bCalendar = true;
        
        } else if (itype.equals("文書履歴")) {
            content.add(tabbedPane);
        
        } else if (itype.equals("アレルギ")) {
            allergyInspector.getPanel().setBorder(BorderFactory.createTitledBorder("アレルギ"));
            content.add(allergyInspector.getPanel());
            bAllergy = true;
        
        } else if (itype.equals("身長体重")) {
            physicalInspector.getPanel().setBorder(BorderFactory.createTitledBorder("身長体重"));
            content.add(physicalInspector.getPanel());
            bPhysical = true;
        }
    }
}




























