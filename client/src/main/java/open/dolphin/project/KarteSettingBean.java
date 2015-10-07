package open.dolphin.project;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import open.dolphin.client.ClientContext;

/**
 * KarteSettingPanel
 *
 * @author Minagawa,Kazushi
 * @author s.oh^
 */
public final class KarteSettingBean extends AbstractSettingBean {

    // Inspector layout
    private String topInspector;
    private String secondInspector;
    private String thirdInspector;
    private String forthInspector;

    // Window locator
    private String windowLocator;

    // Schema editor
    private String schemaEditor;
    
    // Schedule
    private boolean useScheduleFunc; 

    // Sorting order of karte
    private String documentSortingOrder;
    
    // Fetch count of karte
    private int fetchKarteCount;
    
    // Scroll direction of Karte window
    private String karteScrollingDirection;
    
    // Extraction period of karte
    private String karteExtractionPeriod;
    
    //s.oh^ 2014/08/21 修正時にアラート表示
    private boolean showModifyMsg;
    //s.oh$
    
    // Sorting order of Diagnosis
    private String diagnosisSortingOrder;
    
    // Extraction period of Diagnosis
    private String diagnosisExtractionPeriod;
    
    // Show only active diagnosis
    private boolean activeOnly;
    
    // Auto input of outcome date
    private boolean autoOutcomeInput;
    
    // Use leading 15 chars as karte title
    private boolean useTop15AsTitle;
    
    // CLAIM send
    private String sendClaimSave;          // at save
    private String sendClaimModify;        // at modify
    private String sendClaimWhenSchedule;  // schedule
    private String sendClaimAtTmp;
      
    // Default title
    private String defaultKarteTitle;
    
    // Interaction check
    private boolean interactionCheck;

    // 適用保険のカラーリング
    private boolean jihiColoring;
    private boolean jibaisekiColoring;
    private boolean rosaiColoring;

    // その他
    private String confirmAtNew;
    private String createKarteMode;         // カルテの作成モード
    private String placeKarteMode;          // 新規カルテの配置方法

    private boolean autoCloseAtSave;        // 自動クローズ
    private String confirmAtSave;           // 保存時の確認ダイアログ表示
    private String saveKarteMode;           // 表示しない場合の保存モード
    private int printKarteCount;            // 表示しない場合のプリント枚数

    private int ageNeedMonth;               // 月齢表示をする年齢（未満）
    
    private String karteFontSize;
    
    // To return tag property
    private final Map<String,String[]> tagMap = new HashMap<>();
    
    
    public KarteSettingBean() {
        
        ResourceBundle bundle = ClientContext.getMyBundle(this.getClass());
        
        String memo = bundle.getString("memo");
        String allergy = bundle.getString("allergy");
        String physical = bundle.getString("physical");
        String history = bundle.getString("history");
        String calendar = bundle.getString("calendar");
        
        putTag("topInspector", new String[]{memo, allergy, physical, history, calendar});
        putTag("secondInspector", new String[]{memo, allergy, physical, history, calendar});
        putTag("thirdInspector", new String[]{memo, allergy, physical, history, calendar});
        putTag("forthInspector", new String[]{memo, allergy, physical, history, calendar});
        
        putTag("windowLocator", new String[]{bundle.getString("platform"), bundle.getString("remember")});
        putTag("schemaEditor", new String[]{bundle.getString("cool"), bundle.getString("simple")});
        
        putTag("documentSortingOrder", new String[]{bundle.getString("ascending"), bundle.getString("descending")});
        putTag("karteScrollingDirection", new String[]{bundle.getString("horizontal"), bundle.getString("vertical")});
        String[] periods = new String[]{
            bundle.getString("year1"), 
            bundle.getString("year2"), 
            bundle.getString("year3"), 
            bundle.getString("year4"), 
            bundle.getString("year5"),
            bundle.getString("all")};
        putTag("karteExtractionPeriod", periods);
        
        putTag("diagnosisSortingOrder", new String[]{bundle.getString("ascending"), bundle.getString("descending")});
        periods = new String[]{
            bundle.getString("d.all"), 
            bundle.getString("d.year1"), 
            bundle.getString("d.year2"), 
            bundle.getString("d.year3"), 
            bundle.getString("d.year5")};
        putTag("diagnosisExtractionPeriod", periods);
        
        putTag("sendClaimSave", new String[]{bundle.getString("send"), bundle.getString("notSent")});
        putTag("sendClaimModify", new String[]{bundle.getString("send"), bundle.getString("notSent")});
        putTag("sendClaimWhenSchedule", new String[]{bundle.getString("send"), bundle.getString("notSent")});
        putTag("sendClaimAtTmp", new String[]{bundle.getString("dependsOnCheck"), bundle.getString("notSent")});
        
        putTag("confirmAtNew", new String[]{bundle.getString("confirmAtNew.show"), bundle.getString("confirmAtNew.notShow")});
        putTag("createKarteMode", new String[]{bundle.getString("blank"), bundle.getString("applyRp"), bundle.getString("wholeCopy")});
        putTag("placeKarteMode", new String[]{bundle.getString("openWindow"), bundle.getString("placeTab")});
        
        putTag("confirmAtSave", new String[]{bundle.getString("confirmAtSave.show"), bundle.getString("confirmAtSave.notShow")});
        putTag("saveKarteMode", new String[]{bundle.getString("save"), bundle.getString("saveTemporary")});
        
        putTag("karteFontSize", new String[]{bundle.getString("fontSize.small"),
                                bundle.getString("fontSize.medium"),
                                bundle.getString("fontSize.large")});
    }
    
    private void putTag(String key, String[] value) {
        tagMap.put(key, value);
    }

    @Override
    public String[] propertyOrder() {
        return new String[] {
            "topInspector", "secondInspector", "thirdInspector", "forthInspector", "windowLocator", "schemaEditor", "useScheduleFunc", 
            "documentSortingOrder", "fetchKarteCount", "karteScrollingDirection", "karteExtractionPeriod", "showModifyMsg",
            "diagnosisSortingOrder", "diagnosisExtractionPeriod", "activeOnly", "autoOutcomeInput",
            "useTop15AsTitle", "defaultKarteTitle", "sendClaimSave", "sendClaimModify", "sendClaimWhenSchedule", "sendClaimAtTmp",
            "jihiColoring", "jibaisekiColoring", "rosaiColoring",
            "confirmAtNew", "createKarteMode", "placeKarteMode", "autoCloseAtSave", "confirmAtSave",
            "saveKarteMode", "interactionCheck", "printKarteCount", "ageNeedMonth", "karteFontSize"
        };
    }
    
    public String[] categoryAnchor() {
        return new String[] {
            "useScheduleFunc", "autoOutcomeInput", "rosaiColoring", "karteFontSize"
        };
    }
    
    public String[] categoryNames() {
        ResourceBundle bundle = ClientContext.getMyBundle(this.getClass());
        return new String[] {
            bundle.getString("category.inspector"), 
            bundle.getString("category.document"), 
            bundle.getString("category.claim"), 
            bundle.getString("category.other")
        };
    }
    
    @Override
    public boolean isTagProperty(String property) {
        return tagMap.get(property)!=null;
    }
    
    @Override
    public String[] getTags(String property) {
        String[] ret = tagMap.get(property);
        return ret;
    }
    
    @Override
    public boolean isSpinnerProperty(String property) {
        return ("fetchKarteCount".equals(property) || 
                "printKarteCount".equals(property) ||
                "ageNeedMonth".equals(property));
    }
    
    @Override
    public int[] getSpinnerSpec(String property) {
        if (property.equals("fetchKarteCount")) {
            return new int[]{1, 10, 1};
        } else if (property.equals("printKarteCount")) {
            return new int[]{0, 5, 1};
        } else if (property.equals("ageNeedMonth")) {
            return new int[]{0, 6, 1};
        }
        return new int[]{0, 0, 0};
    }
    
    @Override
    public boolean isValidBean() {
        boolean ok = isUseTop15AsTitle() ? notEmpty(this.getDefaultKarteTitle()) : true;
        return ok;
    }

    /**
     * ProjectStub から populate する。
     */
    @Override
    public void populate() {

        setTopInspector(Project.getString(Project.TOP_INSPECTOR));
        setSecondInspector(Project.getString(Project.SECOND_INSPECTOR));
        setThirdInspector(Project.getString(Project.THIRD_INSPECTOR));
        setForthInspector(Project.getString(Project.FORTH_INSPECTOR));

        // Window Locator  platform=true remember=false
        boolean b = Project.getBoolean(Project.LOCATION_BY_PLATFORM);
        String value = arrayValueFromBoolean(b, getTags("windowLocator"));
        this.setWindowLocator(value);

        // Schema editor stored as cool|default
        String test = Project.getString("schema.editor.name");
        String[] tags = getTags("schemaEditor");
        value = test.equals("cool") ? tags[0] : tags[1];
        setSchemaEditor(value);
        
        // Schedule functionality use=true
        setUseScheduleFunc(Project.getBoolean(Project.USE_SCHEDULE_KARTE));
        
        // Sorting ascending=true descending=false
        b = Project.getBoolean(Project.DOC_HISTORY_ASCENDING);
        value = arrayValueFromBoolean(b, getTags("documentSortingOrder"));
        setDocumentSortingOrder(value);

        // 取得枚数
        setFetchKarteCount(Project.getInt(Project.DOC_HISTORY_FETCHCOUNT));

        // Scrole direction  vertical=true  horizontal=false
        b = Project.getBoolean(Project.KARTE_SCROLL_DIRECTION);
        value = arrayValueFromBoolean(!b, getTags("karteScrollingDirection"));
        this.setKarteScrollingDirection(value);
        
        // Extraction  stored as index
        int index = Project.getInt(Project.DOC_HISTORY_PERIOD);
        tags = getTags("karteExtractionPeriod");
        index = findExtractionIndex(index);
        setKarteExtractionPeriod(tags[index]);
        
        //s.oh^ 2014/08/21 修正時にアラート表示
        setShowModifyMsg(Project.getBoolean(Project.KARTE_SHOW_MODIFY_MSG));
        //s.oh$

        // Diagnosis order  adcending=true descending=false
        b = Project.getBoolean(Project.DIAGNOSIS_ASCENDING);
        value = arrayValueFromBoolean(b, getTags("diagnosisSortingOrder"));
        this.setDiagnosisSortingOrder(value);

        // Diagnosis extraction periods 
        index = Project.getInt(Project.DIAGNOSIS_PERIOD);
        tags = getTags("diagnosisExtractionPeriod");
        index = findDiagnosisExtractionIndex(index);
        setDiagnosisExtractionPeriod(tags[index]);

        // アクティブ病名のみ表示
        setActiveOnly(Project.getBoolean(Project.DIAGNOSIS_ACTIVE_ONLY));

        // 転帰が設定された場合、疾患終了日を自動入力するかどうか
        setAutoOutcomeInput(Project.getBoolean(Project.DIAGNOSIS_AUTO_OUTCOME_INPUT));
        
        // 先頭15文字をタイトルに設定するかどうか
        setUseTop15AsTitle(Project.getBoolean(Project.KARTE_USE_TOP15_AS_TITLE));
        
        // デフォルトのタイトル
        setDefaultKarteTitle(Project.getString(Project.KARTE_DEFAULT_TITLE));
        
        // 保存時にCLAIM送信するかどうか  send=true not send=false
        b = Project.getBoolean(Project.SEND_CLAIM_SAVE);
        value = arrayValueFromBoolean(b, getTags("sendClaimSave"));
        setSendClaimSave(value);

        // 修正時にCLAIM送信するかどうか send=true not send=false
        b = Project.getBoolean(Project.SEND_CLAIM_MODIFY);
        value = arrayValueFromBoolean(b, getTags("sendClaimModify"));
        setSendClaimModify(value);
        
        // 予定カルテ send=true not send=false
        //b = Project.getBoolean(Project.SEND_CLAIM_EDIT_FROM_SCHEDULE); 予定カルテ->通常カルテ
        b = Project.getBoolean(Project.SEND_CLAIM_WHEN_SCHEDULE);
        value = arrayValueFromBoolean(b, getTags("sendClaimWhenSchedule"));
        setSendClaimWhenSchedule(value);
         
        // 仮保存時
        b = Project.getBoolean(Project.SEND_CLAIM_DEPENDS_ON_CHECK_AT_TMP);
        value = arrayValueFromBoolean(b, getTags("sendClaimAtTmp"));
        setSendClaimAtTmp(value); 
        
        // 併用禁忌チェック
        setInteractionCheck(Project.getBoolean(Project.INTERACTION_CHECK));

        // 自費カラーリング
        setJihiColoring(Project.getBoolean("docHistory.coloring.jihi"));

        // 労災カラーリング
        setRosaiColoring(Project.getBoolean("docHistory.coloring.rosai"));

        // 自賠責カラーリング
        setJibaisekiColoring(Project.getBoolean("docHistory.coloring.jibaiseki"));

        // 病名送信を行うかどうか
        //setSendDiagnosis(Project.getBoolean(Project.SEND_DIAGNOSIS));

        // 新規カルテ作成時にダイアログを表示するかどうか
        b = Project.getBoolean(Project.KARTE_SHOW_CONFIRM_AT_NEW);
        value = arrayValueFromBoolean(b, getTags("confirmAtNew"));
        setConfirmAtNew(value);

        // 新規カルテの作成モード（空白、前回処方、全コピー）
        int mode = Project.getInt(Project.KARTE_CREATE_MODE);
        tags = getTags("createKarteMode");
        setCreateKarteMode(tags[mode]);

        // 新規カルテ画面を置く場所（カルテタブ、別Window）
        b = Project.getBoolean(Project.KARTE_PLACE_MODE);
        value = arrayValueFromBoolean(b, getTags("placeKarteMode"));
        setPlaceKarteMode(value);
        
        // 自動クローズ
        setAutoCloseAtSave(Project.getBoolean(Project.KARTE_AUTO_CLOSE_AFTER_SAVE));

        // 保存時の確認ダイアログ
        b = Project.getBoolean(Project.KARTE_SHOW_CONFIRM_AT_SAVE);
        value = arrayValueFromBoolean(b, getTags("confirmAtSave"));
        setConfirmAtSave(value);

        // 保存モード
        mode = Project.getInt(Project.KARTE_SAVE_ACTION);
        tags = getTags("saveKarteMode");
        setSaveKarteMode(tags[mode]);
        
        // プリント枚数
        setPrintKarteCount(Project.getInt(Project.KARTE_PRINT_COUNT));

        // 月齢表示年齢
        setAgeNeedMonth(Project.getInt(Project.KARTE_AGE_TO_NEED_MONTH));
        
        // Font Size
        int fontSize = Project.getInt("karte.font.size.default");
        tags = getTags("karteFontSize");
        switch (fontSize) {
            case 12:
                setKarteFontSize(tags[0]);
                break;
            case 13:
            case 14:
                setKarteFontSize(tags[1]);
                break;
            case 16:
                setKarteFontSize(tags[2]);
                break;
            default:
                setKarteFontSize(tags[0]);
                break;
        }
    }

    /**
     * ProjectStubへ保存する。
     */
    @Override
    public void store() {

        // Inspector layout
        Project.setString(Project.TOP_INSPECTOR, getTopInspector());
        Project.setString(Project.SECOND_INSPECTOR, getSecondInspector());
        Project.setString(Project.THIRD_INSPECTOR, getThirdInspector());
        Project.setString(Project.FORTH_INSPECTOR, getForthInspector());

        // Window locator = boolean
        int index = findIndex(getWindowLocator(), getTags("windowLocator"));
        Project.setBoolean(Project.LOCATION_BY_PLATFORM, index==0);

        // Schema Editor Type = cool or default
        index = findIndex(getSchemaEditor(), getTags("schemaEditor"));
        String value = (index==0) ? "cool" : "simple";
        Project.setString("schema.editor.name", value);
        
        // Use Plan Functionality = boolean
        Project.setBoolean(Project.USE_SCHEDULE_KARTE, isUseScheduleFunc());
        
        // Document history ascending=true descending=false
        index = findIndex(getDocumentSortingOrder(), getTags("documentSortingOrder"));
        Project.setBoolean(Project.DOC_HISTORY_ASCENDING, index==0);
        
        // Fetch count
        Project.setInt(Project.DOC_HISTORY_FETCHCOUNT, getFetchKarteCount());

        // Scrolling direction  horizontal=false vertical=true
        index = findIndex(getKarteScrollingDirection(), getTags("karteScrollingDirection"));
        Project.setBoolean(Project.KARTE_SCROLL_DIRECTION, index==1);
        
        // Extraction period  stored as index
        index = findIndex(getKarteExtractionPeriod(), getTags("karteExtractionPeriod"));
        Project.setInt(Project.DOC_HISTORY_PERIOD, saveExtractionIndex(index));
        
        //s.oh^ 2014/08/21 修正時にアラート表示
        Project.setBoolean(Project.KARTE_SHOW_MODIFY_MSG, isShowModifyMsg());
        //s.oh$
        
        // Sorting oreder of diagnosis ascending=true descending=false
        index = findIndex(getDiagnosisSortingOrder(), getTags("diagnosisSortingOrder"));
        Project.setBoolean(Project.DIAGNOSIS_ASCENDING, index==0);
        
        // Extraction period of diagnosis
        index = findIndex(getDiagnosisExtractionPeriod(), getTags("diagnosisExtractionPeriod"));
        Project.setInt(Project.DIAGNOSIS_PERIOD, saveDiagnosisExtractionIndex(index));

        // Show active dicease only
        Project.setBoolean("diagnosis.activeOnly", isActiveOnly());
        
        // Auto input of outcome date
        Project.setBoolean(Project.DIAGNOSIS_AUTO_OUTCOME_INPUT, isAutoOutcomeInput());
        
        // Use leading 15 chars as karte title
        Project.setBoolean(Project.KARTE_USE_TOP15_AS_TITLE, isUseTop15AsTitle());

        // Default title of karte
        String test = getDefaultKarteTitle();
        if (notEmpty(test)) {
            Project.setString(Project.KARTE_DEFAULT_TITLE, test);   //stub.setDefaultKarteTitle(test);
        }
        
        // Send claim at save
        index = findIndex(getSendClaimSave(), getTags("sendClaimSave"));
        Project.setBoolean(Project.SEND_CLAIM_SAVE, index==0);
        
        // Send claim at modify
        index = findIndex(getSendClaimModify(), getTags("sendClaimModify"));
        Project.setBoolean(Project.SEND_CLAIM_MODIFY, index==0);
        
        // Send claim when schedule
        index = findIndex(getSendClaimWhenSchedule(), getTags("sendClaimWhenSchedule"));
        Project.setBoolean(Project.SEND_CLAIM_WHEN_SCHEDULE, index==0);
        
        // Send claim when temporary save
        index = findIndex(getSendClaimAtTmp(), getTags("sendClaimAtTmp"));
        Project.setBoolean(Project.SEND_CLAIM_DEPENDS_ON_CHECK_AT_TMP, index==0);
        
        // 併用禁忌チェック
        Project.setBoolean(Project.INTERACTION_CHECK, interactionCheck);

        // 自費カラーリング
        Project.setBoolean("docHistory.coloring.jihi", jihiColoring);

        // 労災カラーリング
        Project.setBoolean("docHistory.coloring.rosai", rosaiColoring);

        // 自賠責カラーリング
        Project.setBoolean("docHistory.coloring.jibaiseki", jibaisekiColoring);

        // 病名送信
        //Project.setBoolean(Project.SEND_DIAGNOSIS, isSendDiagnosis());
        
        // 新規作成時のダイアログ表示
        index = findIndex(getConfirmAtNew(), getTags("confirmAtNew"));
        Project.setBoolean(Project.KARTE_SHOW_CONFIRM_AT_NEW, index==0);

        // 新規作成モード
        index = findIndex(getCreateKarteMode(), getTags("createKarteMode"));
        Project.setInt(Project.KARTE_CREATE_MODE, index);
        
        // 配置モード
        index = findIndex(getPlaceKarteMode(), getTags("placeKarteMode"));
        Project.setBoolean(Project.KARTE_PLACE_MODE, index==0);
        
        // 自動クローズ
        Project.setBoolean(Project.KARTE_AUTO_CLOSE_AFTER_SAVE, isAutoCloseAtSave());

        // 保存時の確認ダイアログ
        index = findIndex(getConfirmAtSave(), getTags("confirmAtSave"));
        Project.setBoolean(Project.KARTE_SHOW_CONFIRM_AT_SAVE, index==0);
        
        // カルテ保存モード
        index = findIndex(getSaveKarteMode(), getTags("saveKarteMode"));
        Project.setInt(Project.KARTE_SAVE_ACTION, index);
        
        // 印刷枚数
        Project.setInt(Project.KARTE_PRINT_COUNT, getPrintKarteCount());

        // 月齢表示年齢
        Project.setInt(Project.KARTE_AGE_TO_NEED_MONTH, getAgeNeedMonth()); //stub.setAgeToNeedMonth(getAgeNeedMonth());
        
        // ２号カルテ文字サイズ
        index = findIndex(getKarteFontSize(), getTags("karteFontSize"));
        switch (index) {
            case 0:
                Project.setInt("karte.font.size.default", 12);
                break;
                
            case 1:
                Project.setInt("karte.font.size.default", 13);
                break;
                
            case 2:
                Project.setInt("karte.font.size.default", 16);
                break;
                
            default:
                Project.setInt("karte.font.size.default", 12);
                break;
        }
    }
    
    private int findExtractionIndex(int value) {
        int index=0;
        int[] array = new int[]{-12,-24,-36,-48,-60,-120};  //0,-12,-24,-36,-60
        
        for (int test : array) {
            if (test==value) {
                break;
            } else {
                index++;
            }
        }
        
        return index;
    }
    
    private int findDiagnosisExtractionIndex(int value) {
        int index=0;
        int[] array = new int[]{0,-12,-24,-36,-60};  //
        
        for (int test : array) {
            if (test==value) {
                break;
            } else {
                index++;
            }
        }
        
        return (index>=0 && index < 5) ? index : 0;
    }
    
    private int saveExtractionIndex(int index) {
        int[] array = new int[]{-12,-24,-36,-48,-60,-120};
        return array[index];
    }
    
    private int saveDiagnosisExtractionIndex(int index) {
        int[] array = new int[]{0,-12,-24,-36,-60};
        return array[index];
    }
    
    public String getWindowLocator() {
        return windowLocator;
    }
    
    public void setWindowLocator(String str) {
        windowLocator = str;
    }

    public String getSchemaEditor() {
        return schemaEditor;
    }

    public void setSchemaEditor(String name) {
        this.schemaEditor = name;
    }

    public int getFetchKarteCount() {
        return fetchKarteCount;
    }

    public void setFetchKarteCount(int fetchKarteCount) {
        this.fetchKarteCount = fetchKarteCount;
    }
    
    public String getDocumentSortingOrder() {
        return documentSortingOrder;
    }
    
    public void setDocumentSortingOrder(String order) {
        documentSortingOrder = order;
    }
    
    public String getKarteScrollingDirection() {
        return karteScrollingDirection;
    }
    
    public void setKarteScrollingDirection(String str) {
        karteScrollingDirection = str;
    }

    public String getKarteExtractionPeriod() {
        return karteExtractionPeriod;
    }

    public void setKarteExtractionPeriod(String karteExtractionPeriod) {
        this.karteExtractionPeriod = karteExtractionPeriod;
    }
    
    public String getDiagnosisSortingOrder() {
        return diagnosisSortingOrder;
    }
    
    public void setDiagnosisSortingOrder(String str) {
        diagnosisSortingOrder = str;
    }

    public String getDiagnosisExtractionPeriod() {
        return diagnosisExtractionPeriod;
    }

    public void setDiagnosisExtractionPeriod(String diagnosisExtractionPeriod) {
        this.diagnosisExtractionPeriod = diagnosisExtractionPeriod;
    }

    public boolean isActiveOnly() {
        return activeOnly;
    }

    public void setActiveOnly(boolean b) {
        activeOnly = b;
    }

    public boolean isAutoOutcomeInput() {
        return autoOutcomeInput;
    }

    public void setAutoOutcomeInput(boolean b) {
        autoOutcomeInput = b;
    }

    public String getSendClaimWhenSchedule() {
        return sendClaimWhenSchedule;
    }

    public void setSendClaimWhenSchedule(String b) {
        sendClaimWhenSchedule = b;
    }
    
    public String getSendClaimAtTmp() {
        return sendClaimAtTmp;
    }

    public void setSendClaimAtTmp(String b) {
        sendClaimAtTmp = b;
    }       

    public String getSendClaimSave() {
        return sendClaimSave;
    }

    public void setSendClaimSave(String sendClaimSave) {
        this.sendClaimSave = sendClaimSave;
    }

    public String getSendClaimModify() {
        return sendClaimModify;
    }

    public void setSendClaimModify(String sendClaimModify) {
        this.sendClaimModify = sendClaimModify;
    }
    
    public String getConfirmAtNew() {
        return confirmAtNew;
    }

    public void setConfirmAtNew(String confirmAtNew) {
        this.confirmAtNew = confirmAtNew;
    }

    public String getCreateKarteMode() {
        return createKarteMode;
    }

    public void setCreateKarteMode(String createKarteMode) {
        this.createKarteMode = createKarteMode;
    }

    public String getPlaceKarteMode() {
        return placeKarteMode;
    }

    public void setPlaceKarteMode(String placeKarteMode) {
        this.placeKarteMode = placeKarteMode;
    }
    
    public String getConfirmAtSave() {
        return confirmAtSave;
    }

    public void setConfirmAtSave(String confirmAtSave) {
        this.confirmAtSave = confirmAtSave;
    }

    public String getSaveKarteMode() {
        return saveKarteMode;
    }

    public void setSaveKarteMode(String saveKarteMode) {
        this.saveKarteMode = saveKarteMode;
    }

    public int getPrintKarteCount() {
        return printKarteCount;
    }

    public void setPrintKarteCount(int printKarteCount) {
        this.printKarteCount = printKarteCount;
    }

    public boolean isAutoCloseAtSave() {
        return autoCloseAtSave;
    }

    public void setAutoCloseAtSave(boolean b) {
        autoCloseAtSave = b;
    }

    public int getAgeNeedMonth() {
        return ageNeedMonth;
    }

    public void setAgeNeedMonth(int age) {
        ageNeedMonth = age;
    }

    public String getTopInspector() {
        return topInspector;
    }

    public void setTopInspector(String topInspector) {
        this.topInspector = topInspector;
    }

    public String getSecondInspector() {
        return secondInspector;
    }

    public void setSecondInspector(String secondInspector) {
        this.secondInspector = secondInspector;
    }

    public String getThirdInspector() {
        return thirdInspector;
    }

    public void setThirdInspector(String thirdInspector) {
        this.thirdInspector = thirdInspector;
    }

    public String getForthInspector() {
        return forthInspector;
    }

    public void setForthInspector(String forthInspector) {
        this.forthInspector = forthInspector;
    }

    public String getDefaultKarteTitle() {
        return defaultKarteTitle;
    }

    public void setDefaultKarteTitle(String defaultKarteTitle) {
        this.defaultKarteTitle = defaultKarteTitle;
    }

    public boolean isUseTop15AsTitle() {
        return useTop15AsTitle;
    }

    public void setUseTop15AsTitle(boolean useTop15AsTitle) {
        this.useTop15AsTitle = useTop15AsTitle;
    }

    public boolean isInteractionCheck() {
        return interactionCheck;
    }

    public void setInteractionCheck(boolean check) {
        this.interactionCheck = check;
    }

    public boolean isJihiColoring() {
        return jihiColoring;
    }

    public void setJihiColoring(boolean b) {
        jihiColoring = b;
    }

    public boolean isRosaiColoring() {
        return rosaiColoring;
    }

    public void setRosaiColoring(boolean b) {
        rosaiColoring = b;
    }

    public boolean isJibaisekiColoring() {
        return jibaisekiColoring;
    }

    public void setJibaisekiColoring(boolean b) {
        jibaisekiColoring = b;
    }

    public boolean isUseScheduleFunc() {
        return useScheduleFunc;
    }

    public void setUseScheduleFunc(boolean b) {
        useScheduleFunc = b;
    }        

    public boolean isShowModifyMsg() {
        return showModifyMsg;
    }

    public void setShowModifyMsg(boolean b) {
        showModifyMsg = b;
    }

    public String getKarteFontSize() {
        return karteFontSize;
    }

    public void setKarteFontSize(String karteFontSize) {
        this.karteFontSize = karteFontSize;
    }
}
