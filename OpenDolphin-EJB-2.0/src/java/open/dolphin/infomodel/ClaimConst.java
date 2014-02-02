package open.dolphin.infomodel;

public final class ClaimConst {
    
    public static final String DISEASE_MASTER_SYSTEM = "mml.codeSystem.diseaseMaster=ICD10_2001-10-03MEDIS";
    public static final String CLASS_CODE_ID    = "Claim007";	// 診療行為区分テーブルID
    public static final String SUBCLASS_CODE_ID = "Claim003";	// 手技、材料、薬剤区分テーブルID
    public static final String NUMBER_CODE_ID   = "Claim004";	// 数量コードテーブルID

    /** 手技 */
    public static final int SYUGI = 0;

    /** 材料 */
    public static final int ZAIRYO = 1;

    /** 薬剤 */
    public static final int YAKUZAI = 2;

    /** 用法 */
    public static final int ADMIN = 3;

    /** 部位 */
    public static final int BUI = 4;
    
    /** その他 */
    public static final int OTHER = 5;

    /** 薬剤区分 内用 */
    public static final String YKZ_KBN_NAIYO = "1";

    /** 薬剤区分 注射 */
    public static final String YKZ_KBN_INJECTION = "4";

    /** 薬剤区分 外用 */
    public static final String YKZ_KBN_GAIYO = "6";

    /** レセ電算コード 内用 */
    public static final String RECEIPT_CODE_NAIYO = "210";

    /** レセ電算コード 内用院内 */
    public static final String RECEIPT_CODE_NAIYO_IN = "211";

    /** レセ電算コード 内用院外 */
    public static final String RECEIPT_CODE_NAIYO_EXT = "212";
    
    /** レセ電算コード 内用包括 */
    public static final String RECEIPT_CODE_NAIYO_HOKATSU = "213";

    /** レセ電算コード 頓用 */
    public static final String RECEIPT_CODE_TONYO = "220";

    /** レセ電算コード 頓用院内 */
    public static final String RECEIPT_CODE_TONYO_IN = "221";

    /** レセ電算コード 頓用院外 */
    public static final String RECEIPT_CODE_TONYO_EXT = "222";

    /** レセ電算コード 頓用包括 */
    public static final String RECEIPT_CODE_TONYO_HOKATSU = "222";

    /** レセ電算コード 外用*/
    public static final String RECEIPT_CODE_GAIYO = "230";

    /** レセ電算コード 外用院内*/
    public static final String RECEIPT_CODE_GAIYO_IN = "231";

    /** レセ電算コード 外用院外*/
    public static final String RECEIPT_CODE_GAIYO_EXT = "232";

     /** レセ電算コード 外用包括*/
    public static final String RECEIPT_CODE_GAIYO_HOKATSU = "233";

    public static final String YAKUZAI_TOYORYO = "10";          // 薬剤投与量
    public static final String YAKUZAI_TOYORYO_1KAI = "11";	// 薬剤投与量１回
    public static final String YAKUZAI_TOYORYO_1NICHI = "12";	// 薬剤投与量１日
    public static final String ZAIRYO_KOSU = "21";		// 材料個数
    public static final String INJECTION_310 = "310";
    public static final String INJECTION_320 = "320";
    public static final String INJECTION_330 = "330";
    public static final String INJECTION_311 = "311";
    public static final String INJECTION_321 = "321";
    public static final String INJECTION_331 = "331";

    /** 手技（診療行為）コードの頭番号 */
    public static final String SYUGI_CODE_START = "1";

    /** 薬剤コードの頭番号 */
    public static final String YAKUZAI_CODE_START = "6";

    /** 材料コードの頭番号 */
    public static final String ZAIRYO_CODE_START = "7";

    /** 用法コードの頭番号 */
    public static final String ADMIN_CODE_START = "001";

    /** 放射線部位コードの頭番号 */
    public static final String RBUI_CODE_START = "002";

    /** 院内処方 */
    public static final String IN_MEDICINE = "院内処方";
    
    /** 院外処方 */
    public static final String EXT_MEDICINE = "院外処方";

    public static final String SLOT_SYUGI = "手技";
    public static final String SLOT_NAIYO_YAKU = "内用薬";
    public static final String SLOT_TYUSHYA_YAKU = "注射薬";
    public static final String SLOT_GAIYO_YAKU = "外用薬";
    public static final String SLOT_YAKUZAI = "薬剤";
    public static final String SLOT_MEDICINE = "薬";
    public static final String SLOT_ZAIRYO = "材料";
    public static final String SLOT_YOHO = "用法";
    public static final String SLOT_BUI = "部位";
    public static final String SLOT_OTHER = "その他";
    
    public static final String UNIT_T = "錠";
    public static final String UNIT_G = "ｇ";
    public static final String UNIT_ML = "ｍＬ";
    public static final String UNIT_CAPSULE = "カプセル";
}
