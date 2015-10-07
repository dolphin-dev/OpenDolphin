package open.dolphin.client;

import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import open.dolphin.infomodel.KarteBean;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.project.Project;

/**
 *
 * @author Kazushi Minagawa.
 */
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
        this.context = context;
        
        // GUI を初期化する
        initComponents();
    }
    
    public void dispose() {
        // List をクリアする
        docHistory.clear();
        allergyInspector.clear();
        physicalInspector.clear();
        memoInspector.save();
    }
    
    /**
     * コンテキストを返す。
     * @return 
     */
    public ChartImpl getContext() {
        return context;
    }
    
    /**
     * コンテキストを設定する。
     * @param context
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
        return context.getKarte().getPatientModel();
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
        
        java.util.ResourceBundle bundle = ClientContext.getMyBundle(PatientInspector.class);

        String inspectorNameMemo = bundle.getString("inspectorName.memo");
        String inspectorNamePVT = bundle.getString("inspectorName.pvt");
        String inspectorNameRecordHistory = bundle.getString("inspectorName.recordHistory");
        String inspectorNameAllergy = bundle.getString("inspectorName.allergy");
        String inspectorNameHeightWeight = bundle.getString("inspectorName.heightWeight");
        
        String topInspector = Project.getString("topInspector", inspectorNameMemo);
        String secondInspector = Project.getString("secondInspector", inspectorNamePVT);
        String thirdInspector = Project.getString("thirdInspector", inspectorNameRecordHistory);
        String forthInspector = Project.getString("forthInspector", inspectorNameAllergy);
        String[] settingNames = new String[]{inspectorNameMemo,inspectorNamePVT,inspectorNameRecordHistory,inspectorNameAllergy,inspectorNameHeightWeight};
        
        //System.out.println("debug");
        
        // 各インスペクタを生成する
        basicInfoInspector = new BasicInfoInspector(context);
        patientVisitInspector = new PatientVisitInspector(context);
        memoInspector = new MemoInspector(context);
        memoInspector.getPanel().setBorder(BorderFactory.createEtchedBorder());
        docHistory = new DocumentHistory(getContext());
        allergyInspector = new AllergyInspector(context);
        physicalInspector = new PhysicalInspector(context);
        
        // タブパネルへ格納する(文書履歴、健康保険、アレルギ、身長体重はタブパネルで切り替え表示する)
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab(inspectorNameRecordHistory, docHistory.getPanel());
        
        int prefW = 260;
        int prefW2 = 260;
        if (ClientContext.isMac()) {
            prefW2 += 20;
        }
        basicInfoInspector.getPanel().setPreferredSize(new Dimension(prefW2, 40));
        basicInfoInspector.getPanel().setMaximumSize(new Dimension(prefW2, 40));
        basicInfoInspector.getPanel().setMinimumSize(new Dimension(prefW2, 40));
        
        // cut & try
        memoInspector.getPanel().setPreferredSize(new Dimension(prefW, 100));
        allergyInspector.getPanel().setPreferredSize(new Dimension(prefW, 80));
        docHistory.getPanel().setPreferredSize(new Dimension(prefW, 300));
        physicalInspector.getPanel().setPreferredSize(new Dimension(prefW, 80));
        
        container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        
        // 左側のレイアウトを行う
        layoutRow(container, topInspector, settingNames);
        layoutRow(container, secondInspector, settingNames);
        layoutRow(container, thirdInspector, settingNames);
        layoutRow(container, forthInspector, settingNames);
        
        // 左側にレイアウトされなかったものをタブに格納する
        if (!bMemo) {
            tabbedPane.addTab(inspectorNameMemo, memoInspector.getPanel());
        }
        
        if (!bCalendar) {
            tabbedPane.addTab(inspectorNamePVT, patientVisitInspector.getPanel());
        }
        
        if (!bAllergy) {
            tabbedPane.addTab(inspectorNameAllergy, allergyInspector.getPanel());
        }
        
        if (!bPhysical) {
            tabbedPane.addTab(inspectorNameHeightWeight, physicalInspector.getPanel());
        }
        
        //SpringUtilities.makeCompactGrid(container, 4, 1, 0, 0, 0, 0);
    }
    
    private void layoutRow(JPanel content, String itype, String[] inspectorValues) {
        
        int index=0;
        for (String str : inspectorValues) {
            if (itype.equals(str)) {
                break;
            } else {
                index++;
            }
        }
        
        switch (index) {
            case 0:
                content.add(memoInspector.getPanel());
                bMemo = true;
                break;
                
            case 1:
                content.add(patientVisitInspector.getPanel());
                bCalendar = true;
                break;
                
            case 2:
                content.add(tabbedPane);
                break;
                
            case 3:
                content.add(allergyInspector.getPanel());
                bAllergy = true;
                break;
                
            case 4:
                content.add(physicalInspector.getPanel());
                bPhysical = true;
                break;
        }
    }
}