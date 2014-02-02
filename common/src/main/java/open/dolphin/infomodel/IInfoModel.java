package open.dolphin.infomodel;

/**
 * IInfoModel
 *
 * @athor Minagawa, Kazushi
 *
 */
public interface IInfoModel extends java.io.Serializable, java.lang.Cloneable {
    
    /** ISO 8601 style date format */
    public static final String ISO_8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    
    /** カルテの確定日表示用のフォーマット */
    public static final String KARTE_DATE_FORMAT = "yyyy年M月d日'（'EEE'）'H時m分";
    
    /** 時間部分のない Date */
    public static final String DATE_WITHOUT_TIME = "yyyy-MM-dd";
    
    /** 予約用(予定カルテ対応) */
    public static final String DATE_FORMAT_FOR_SCHEDULE = "yyyy年M月d日'（'EEE'）'";
    
    /** Oersistence Query の LIKE 演算子 */
    public static final String LIKE_OPERATOR = "%";
    
    /** 複合キーにするための結合子 */
    public static final String COMPOSITE_KEY_MAKER = ":";
    
    /** 管理者ロール */
    public static final String ADMIN_ROLE = "admin";
    
    /** 利用者ロール */
    public static final String USER_ROLE = "user";
    
    /** 婚姻状況 */
    public static final String MARITAL_STATUS = "maritalStatus";
    
    /** 国籍 */
    public static final String NATIONALITY = "nationality";
    
    /** メモ */
    public static final String MEMO = "memo";    
    
    public static final String MALE 		= "male";
    
    public static final String MALE_DISP 	= "男";
    
    public static final String FEMALE 		= "female";
    
    public static final String FEMALE_DISP 	= "女";
    
    public static final String UNKNOWN 		= "不明";
    
    public static final String AGE 		= "歳";

    /** プレイン文書 */
    public static final String DOCTYPE_S_KARTE = "s_karte";

    /** 2号カルテ */
    public static final String DOCTYPE_KARTE = "karte";

    /** 紹介状 */
    public static final String DOCTYPE_LETTER = "letter";

    /** 紹介状返書 */
    public static final String DOCTYPE_LETTER_REPLY = "letterReply";

    /** 紹介状返書2 */
    public static final String DOCTYPE_LETTER_REPLY2 = "letterReply2";

    /** 紹介状及び返書でプラグイン */
    public static final String DOCTYPE_LETTER_PLUGIN = "letterPlugin";
    
    public static final String PURPOSE_RECORD = "recode";
    
    public static final String PARENT_OLD_EDITION = "oldEdition";
    
    public static final String RELATION_NEW = "newVersion";
    public static final String RELATION_OLD = "oldVersion";
    
    public static final String DEFAULT_DIAGNOSIS_TITLE = "病名登録";
    public static final String DEFAULT_DIAGNOSIS_CATEGORY = "mainDiagnosis";
    public static final String DEFAULT_DIAGNOSIS_CATEGORY_DESC = "主病名";
    public static final String DEFAULT_DIAGNOSIS_CATEGORY_CODESYS = "MML0012";
    public static final String ORCA_OUTCOME_RECOVERED ="治癒";
    public static final String ORCA_OUTCOME_DIED = "死亡";
    public static final String ORCA_OUTCOME_END = "中止";
    public static final String ORCA_OUTCOME_TRANSFERED = "移行";
    
    //
    // Stamp Roles
    //
    /** ProgessCourse */
    public static final String MODULE_PROGRESS_COURSE           = "progressCourse";
    
    /** SOA stamp */
    public static final String ROLE_SOA 			= "soa";
    
    /** P stamp */
    public static final String ROLE_P 				= "p";
    
    /** SOA spec */
    public static final String ROLE_SOA_SPEC 			= "soaSpec";
    
    /** P spec */
    public static final String ROLE_P_SPEC 			= "pSpec";
    
    /** Text stamp */
    public static final String ROLE_TEXT 			= "text";
    
    /** 傷病名 */
    public static final String ROLE_DIAGNOSIS 			= "diagnosis";
    
    /** ORCA 入力セット */
    public static final String ROLE_ORCA_SET                    = "orcaSet";
    
    
    public static final String STATUS_FINAL 			= "F";
    public static final String STATUS_MODIFIED 			= "M";
    public static final String STATUS_TMP                       = "T";
    public static final String STATUS_NONE                      = "N";
    public static final String STATUS_DELETE                    = "D";
    
    public static final String PERMISSION_ALL = "all";
    public static final String PERMISSION_READ = "read";
    public static final String ACCES_RIGHT_PATIENT = "patient";
    public static final String ACCES_RIGHT_CREATOR = "creator";
    public static final String ACCES_RIGHT_EXPERIENCE = "experience";
    public static final String ACCES_RIGHT_PATIENT_DISP = "被記載者(患者)";
    public static final String ACCES_RIGHT_CREATOR_DISP = "記載者施設";
    public static final String ACCES_RIGHT_EXPERIENCE_DISP = "診療歴のある施設";
    public static final String ACCES_RIGHT_PERSON_CODE = "personCode";
    public static final String ACCES_RIGHT_FACILITY_CODE = "facilityCode";
    public static final String ACCES_RIGHT_EXPERIENCE_CODE = "facilityCode";
    
    /** 処方コード */
    public static final String CLAIM_210 = "210";
    
    /** 保険区別 */
    public static final String INSURANCE_SELF = "自費";
    public static final String INSURANCE_SELF_CODE = "Z1";
    public static final String INSURANCE_SELF_PREFIX = "Z";
    public static final String INSURANCE_ROSAI_PREFIX = "R1";
    public static final String INSURANCE_JIBAISEKI_PREFIX = "R3";
    public static final String INSURANCE_SYS = "MML031";
    
    //
    // StampTreeのエンティティ（情報の実体）名
    //
    /** 傷病名 */
    public static final String ENTITY_DIAGNOSIS = "diagnosis";
    
    /** テキスト */
    public static final String ENTITY_TEXT = "text";
    
    /** パ ス */
    public static final String ENTITY_PATH = "path";
    
    /** 汎用 */
    public static final String ENTITY_GENERAL_ORDER = "generalOrder";
    
    /** その他 */
    public static final String ENTITY_OTHER_ORDER = "otherOrder";
    
    /** 処 置 */
    public static final String ENTITY_TREATMENT = "treatmentOrder";
    
    /** 手 術 */
    public static final String ENTITY_SURGERY_ORDER = "surgeryOrder";
    
    /** 放射線 */
    public static final String ENTITY_RADIOLOGY_ORDER = "radiologyOrder";
    
    /** ラボテスト */
    public static final String ENTITY_LABO_TEST = "testOrder";
    
    /** 生体検査 */
    public static final String ENTITY_PHYSIOLOGY_ORDER = "physiologyOrder";
    
    /** 細菌検査 */
    public static final String ENTITY_BACTERIA_ORDER = "bacteriaOrder";
    
    /** 注 射 */
    public static final String ENTITY_INJECTION_ORDER = "injectionOrder";
    
    /** 処 方 */
    public static final String ENTITY_MED_ORDER = "medOrder";
    
    /** 診 断 */
    public static final String ENTITY_BASE_CHARGE_ORDER = "baseChargeOrder";
    
    /** 指 導 */
    public static final String ENTITY_INSTRACTION_CHARGE_ORDER = "instractionChargeOrder";
    
    /** ORCA セット */
    public static final String ENTITY_ORCA = "orcaSet";
    
    /** Entity の配列 */
    public static final String[] STAMP_ENTITIES = new String[] {
        ENTITY_DIAGNOSIS, ENTITY_TEXT, ENTITY_PATH, ENTITY_ORCA, ENTITY_GENERAL_ORDER, ENTITY_OTHER_ORDER, ENTITY_TREATMENT,
        ENTITY_SURGERY_ORDER, ENTITY_RADIOLOGY_ORDER, ENTITY_LABO_TEST, ENTITY_PHYSIOLOGY_ORDER,
        ENTITY_BACTERIA_ORDER, ENTITY_INJECTION_ORDER, ENTITY_MED_ORDER, ENTITY_BASE_CHARGE_ORDER, ENTITY_INSTRACTION_CHARGE_ORDER
    };
    
    //
    // StampTreeのタブ名
    //
    /** 傷病名 */
    public static final String TABNAME_DIAGNOSIS = "傷病名";
    
    /** テキスト */
    public static final String TABNAME_TEXT = "テキスト";
    
    /** パ ス */
    public static final String TABNAME_PATH = "パ ス";
    
    /** ORCA セット */
    public static final String TABNAME_ORCA = "ORCA";
    
    /** 汎 用 */
    public static final String TABNAME_GENERAL = "汎 用";
    
    /** その他 */
    public static final String TABNAME_OTHER = "その他";
    
    /** 放射線 */
    public static final String TABNAME_RADIOLOGY = "放射線";
    
    /** 検体検査 */
    public static final String TABNAME_LABO = "検体検査";
    
    /** 生体検査 */
    public static final String TABNAME_PHYSIOLOGY = "生体検査";
    
    /** 細菌検査 */
    public static final String TABNAME_BACTERIA = "細菌検査";
    
    /** 手 術 */
    public static final String TABNAME_SURGERY = "手 術";
    
    /** 処 置 */
    public static final String TABNAME_TREATMENT = "処 置";
    
    /** 注 射 */
    public static final String TABNAME_INJECTION = "注 射";
    
    /** 処 方 */
    public static final String TABNAME_MED = "処 方";
    
    /** 指導・在宅 */
    public static final String TABNAME_INSTRACTION = "指導・在宅";
    
    /** 初診・再診 */
    public static final String TABNAME_BASE_CHARGE = "初診・再診";
    
    
    /** ORCA のタブ番号 */
    public static final int TAB_INDEX_ORCA = 3;
    
    
    /** スタンプのタブ名配列 */
    public static String[] STAMP_NAMES = {
        TABNAME_DIAGNOSIS, TABNAME_TEXT, TABNAME_PATH, TABNAME_ORCA, 
        TABNAME_GENERAL, TABNAME_OTHER, TABNAME_TREATMENT, TABNAME_SURGERY, 
        TABNAME_RADIOLOGY, TABNAME_LABO, TABNAME_PHYSIOLOGY, TABNAME_BACTERIA,
        TABNAME_INJECTION, TABNAME_MED, TABNAME_BASE_CHARGE, TABNAME_INSTRACTION
    };
    
    /** スタンプのCLAIM版点数集計先 */
    public static String[] CLAIM_CLASS_CODE = {
        "", "", "", "", "", "800-899", "400-499", "500-599", "700-799", "600-699", "600-699",
        "600", "300-331", "210-230", "110-125", "130-140"
    };
    
    // Observation
    public static final String OBSERVATION_ALLERGY = "Allergy";
    public static final String OBSERVATION_BLOODTYPE = "Bloodtype";
    public static final String OBSERVATION_INFECTION = "Infection";
    public static final String OBSERVATION_LIFESTYLE = "Lifestyle";
    public static final String OBSERVATION_PHYSICAL_EXAM = "PhysicalExam";
    public static final String OBSERVATION_VITAL_SIGN = "VitalSign";
    
    // 身長体重
    public static final String PHENOMENON_BODY_HEIGHT = "bodyHeight";
    public static final String PHENOMENON_BODY_WEIGHT = "bodyWeight";
    public static final String UNIT_BODY_WEIGHT = "Kg";
    public static final String UNIT_BODY_HEIGHT = "cm";
    
    // 生活習慣
    public static final String PHENOMENON_TOBACCO = "tobacco";
    public static final String PHENOMENON_ALCOHOL = "alcohol";
    public static final String PHENOMENON_OCCUPATION = "occupation";
    
    // バイタルサイン
    public static final String PHENOMENON_BODY_TEMPERATURE = "bodyTemperature";
    public static final String PHENOMENON_BLOOD_PRESSURE_SYSTOLIC = "bloodPressureSistolic";
    public static final String PHENOMENON_BLOOD_PRESSURE_DIASTOLIC = "bloodPressureDiastoric";
    public static final String PHENOMENON_PULSE_RATE = "pulseRate";
    public static final String PHENOMENON_RESPAIRATION_RATE = "respirationRate";
    public static final String UNIT_BODY_TEMPERATURE = "degree";
    public static final String UNIT_BLLOD_PRESSURE = "mmHg";
    public static final String UNIT_PULSE_RATE = "times/m";
    public static final String UNIT_RESPIRATION_RATE = "times/m";
    
    public static final String PUBLISH_TREE_LOCAL = "院内";
    public static final String PUBLISH_TREE_PUBLIC = "グローバル";
    public static final String PUBLISHED_TYPE_GLOBAL = "global";

    public static final String CONSULTANT = "consultant";
    public static final String CLIENT = "client";

    public static final String MEDICAL_CERTIFICATE = "medicalCertificate";
    
}
