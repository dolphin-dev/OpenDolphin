package open.dolphin.order;

import java.util.ListResourceBundle;

/**
 * MML Table Dictionary class.
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class ClaimResource_ja extends ListResourceBundle {

    @Override
    protected Object[][] getContents() {
        return new Object[][]{
            {"110", "初診"},
            {"120", "再診(再診)"},
            {"122", "再診(外来管理加算)"},
            {"123", "再診(時間外)"},
            {"124", "再診(休日)"},
            {"125", "再診(深夜)"},
            {"130", "指導"},
            {"140", "在宅"},
            {"210", "投薬(内服・頓服・調剤)(入院外)"},
            {"211", "投薬(内服・頓服・調剤)(院内)"},
            {"212", "投薬(内服・頓服・調剤)(院外)"},
            {"230", "投薬(外用・調剤)(入院外)"},
            {"231", "投薬(外用・調剤)(院内)"},
            {"232", "投薬(外用・調剤)(院外)"},
            {"240", "投薬(調剤)(入院)"},
            {"250", "投薬(処方)"},
            {"260", "投薬(麻毒)"},
            {"270", "投薬(調基)"},
            {"300", "注射(生物学的製剤・精密持続点滴・麻薬)"},
            {"310", "注射(皮下筋肉内)"},
            {"320", "注射(静脈内)"},
            {"330", "注射(その他)"},
            {"311", "注射(皮下筋肉内)"},
            {"321", "注射(静脈内)"},
            {"331", "注射(その他)"},
            {"400", "処置"},
            {"500", "手術(手術)"},
            {"502", "手術(輸血)"},
            {"503", "手術(ギプス)"},
            {"540", "麻酔"},
            {"600", "検査"},
            {"700", "画像診断"},
            {"800", "その他"},
            {"903", "入院(入院料)"},
            {"906", "入院(外泊)"},
            {"910", "入院(入院時医学管理料)"},
            {"920", "入院(特定入院料・その他)"},
            {"970", "入院(食事療養)"},
            {"971", "入院(標準負担額)"},
            // RP
            {"PRESCRIPTION_NAME", new String[]{"内用（院内処方)","内用（院外処方）","頓用（院内処方）","頓用（院外処方）","外用（院内処方）","外用（院外処方）","臨時（院内処方）","臨時（院外処方）"}},
            {"PRESCRIPTION_CODE", new String[]{"211","212","221","222","231","232","291","292"}},
            {"PRESCRIPTION_EXTERNAL", "院外処方"},
            {"PRESCRIPTION_HOSPITAL", "院内処方"},
            {"IN_MEDICINE","院内処方"},
            {"EXT_MEDICINE","院外処方"},
            {"ADMIN_CODE_REGEXP", new String[]{"","0010001","0010002","0010003","0010004","(0010005|0010007)","0010006","0010008","0010009","001"}},
            {"INJECTION_CLASS_CODE", new String[]{"31","32","33"}},
            // Insurance
            {"INSURANCE_SELF", "自費"},
            {"INSURANCE_SELF_CODE", "Z1"},
            {"INSURANCE_SELF_PREFIX", "Z"},
            {"INSURANCE_ROSAI_PREFIX", "R1"},
            {"INSURANCE_JIBAISEKI_PREFIX", "R3"},
            {"INSURANCE_SYS", "MML031"},
            // アクセス権
            {"PERMISSION_ALL", "all"},
            {"PERMISSION_READ", "read"},
            {"ACCES_RIGHT_PATIENT", "patient"},
            {"ACCES_RIGHT_CREATOR", "creator"},
            {"ACCES_RIGHT_EXPERIENCE", "experience"},
            {"ACCES_RIGHT_PATIENT_DISP", "被記載者(患者)"},
            {"ACCES_RIGHT_CREATOR_DISP", "記載者施設"},
            {"ACCES_RIGHT_EXPERIENCE_DISP", "診療歴のある施設"},
            {"ACCES_RIGHT_PERSON_CODE", "personCode"},
            {"ACCES_RIGHT_FACILITY_CODE", "facilityCode"},
            {"ACCES_RIGHT_EXPERIENCE_CODE", "facilityCode"},
            // 病名
            {"DEFAULT_DIAGNOSIS_TITLE", "病名登録"},
            {"DEFAULT_DIAGNOSIS_CATEGORY", "mainDiagnosis"},
            {"DEFAULT_DIAGNOSIS_CATEGORY_DESC", "主病名"},
            {"DEFAULT_DIAGNOSIS_CATEGORY_CODESYS", "MML0012"},
            {"ORCA_OUTCOME_RECOVERED", "治癒"},
            {"ORCA_OUTCOME_DIED", "死亡"},
            {"ORCA_OUTCOME_END", "中止"},
            {"ORCA_OUTCOME_TRANSFERED", "移行"},
            // 
            {"MARITAL_STATUS", "maritalStatus"},
            {"NATIONALITY", "nationality"},
            {"MEMO", "memo"},
            {"MALE", "male"},
            {"MALE_DISP", "男"},
            {"FEMALE", "female"},
            {"FEMALE_DISP", "女"},
            {"UNKNOWN", "不明"},
            {"AGE", "歳"},
            //
            {"DEFAULT_NUMBER", "0"},
            {"DEFAULT_STAMP_NAME", "新規スタンプ"},
            {"FROM_EDITOR_STAMP_NAME", "エディタから"},
            // Master 
            {"MED_COST_FLGAS", new String[]{"廃", "金", "都", "", "", "", "", "減", "不"}},
            {"TOOL_COST_FLGAS", new String[]{"廃", "金", "都", "", "", "%加", "", "", "", "乗"}},
            {"TREAT_COST_FLGAS", new String[]{"廃", "金", "", "+点", "都", "%加", "%減", "減", "-点"}},
            {"IN_OUT_FLAGS", new String[]{"入外", "入", "外"}},
            {"HOSPITAL_CLINIC_FLAGS", new String[]{"病診", "病", "診"}},
            {"OLD_FLAGS", new String[]{"社老", "社", "老"}},
            // 用法
            {"ADMIN_MARK", "[用法] "},
            {"REG_ADMIN_MARK", "\\\\[用法\\\\] "},
            // 組み合わせができるマスター項目
            {"REG_BASE_CHARGE", "[手そ]"},
            {"REG_INSTRACTION_CHARGE", "[手そ薬材]"}, // 在宅で薬剤、材料を追加
            {"REG_MED_ORDER", "[薬用材そ]"},          // 保険適用外の医薬品等追加
            {"REG_INJECTION_ORDER", "[手そ注材]"},
            {"REG_TREATMENT", "[手そ薬材]"},
            {"REG_SURGERY_ORDER", "[手そ薬材]"},
            {"REG_BACTERIA_ORDER", "[手そ薬材]"},
            {"REG_PHYSIOLOGY_ORDER", "[手そ薬材]"},
            {"REG_LABO_TEST", "[手そ薬材]"},
            {"REG_RADIOLOGY_ORDER", "[手そ薬材部]"},
            {"REG_OTHER_ORDER", "[手そ薬材]"},
            {"REG_GENERAL_ORDER", "[手そ薬材用部]"},
            // セットできる診療行為区分
            {"SHIN_BASE_CHARGE", "^(11|12)"},
            {"SHIN_INSTRACTION_CHARGE", "^(13|14)"},
            {"SHIN_MED_ORDER", ""},                // 210|220|230
            {"SHIN_INJECTION_ORDER", "^3"},        // 310|320|330
            {"SHIN_TREATMENT", "^4"},
            {"SHIN_SURGERY_ORDER", "^5"},
            {"SHIN_BACTERIA_ORDER", "^6"},
            {"SHIN_PHYSIOLOGY_ORDER", "^6"},
            {"SHIN_LABO_TEST", "^6"},
            {"SHIN_RADIOLOGY_ORDER", "^7"},
            {"SHIN_OTHER_ORDER", "^8"},
            {"SHIN_GENERAL_ORDER", "\\\\d"},
            // エディタに表示する名前
            {"NAME_BASE_CHARGE", "診断料"},
            {"NAME_INSTRACTION_CHARGE", "管理料"}, // 指導・在宅
            {"NAME_MED_ORDER", "処 方"},
            {"NAME_INJECTION_ORDER", "注 射"},
            {"NAME_TREATMENT", "処 置"},
            {"NAME_SURGERY_ORDER", "手 術"},
            {"NAME_BACTERIA_ORDER", "細菌検査"},
            {"NAME_PHYSIOLOGY_ORDER", "生理・内視鏡検査"},
            {"NAME_LABO_TEST", "検体検査"},
            {"NAME_RADIOLOGY_ORDER", "放射線"},
            {"NAME_OTHER_ORDER", "その他"},
            {"NAME_GENERAL_ORDER", "汎 用"},
            // 暗黙の診療行為区分
            {"IMPLIED_BASE_CHARGE", ""},
            {"IMPLIED_INSTRACTION_CHARGE", ""},
            {"IMPLIED_MED_ORDER", ""},
            {"IMPLIED_INJECTION_ORDER", ""},
            {"IMPLIED_TREATMENT", "400"},
            {"IMPLIED_SURGERY_ORDER", ""},
            {"IMPLIED_BACTERIA_ORDER", "600"},
            {"IMPLIED_PHYSIOLOGY_ORDER", "600"},
            {"IMPLIED_LABO_TEST", "600"},
            {"IMPLIED_RADIOLOGY_ORDER", "700"},
            {"IMPLIED_OTHER_ORDER", "800"},
            {"IMPLIED_GENERAL_ORDER", ""},
            // 情報
            {"INFO_BASE_CHARGE", "診断料（診区=110-120）"},
            {"INFO_INSTRACTION_CHARGE", "管理料（診区=130-140）"},
            {"INFO_MED_ORDER", "処 方"},
            {"INFO_INJECTION_ORDER", "注 射（診区=300）"},
            {"INFO_TREATMENT", "処 置（診区=400）"},
            {"INFO_SURGERY_ORDER", "手 術（診区=500）"},
            {"INFO_BACTERIA_ORDER", "細菌検査（診区=600）"},
            {"INFO_PHYSIOLOGY_ORDER", "生理・内視鏡検査（診区=600）"},
            {"INFO_LABO_TEST", "検体検査（診区=600）"},
            {"INFO_RADIOLOGY_ORDER", "放射線（診区=700）"},
            {"INFO_OTHER_ORDER", "その他（診区=800）"},
            {"INFO_GENERAL_ORDER", "汎 用（診区=100-999）"},
            // 病名
            {"NAME_DIAGNOSIS", "傷病名"},
            {"REG_DIAGNOSIS", "[手そ薬材用部]"},
            // 辞書のキー
            {"KEY_ORDER_NAME", "orderName"},
            {"KEY_PASS_REGEXP", "passRegExp"},
            {"KEY_SHIN_REGEXP", "shinkuRegExp"},
            {"KEY_INFO", "info"},
            {"KEY_IMPLIED", "implied007"},
            // 編集不可コメント
            {"COMMENT_82", new String[]{"82","0082","８２","００８２"}},
            // 編集可能コメント
            {"NUMBER_EDITABLE_COMMENT", new String[]{"84","0084"}},
            {"NAME_EDITABLE_COMMENT", new String[]{"81","0081","83","0083","85","0085","86","0086"}},
            // 検索特殊記号文字
            {"ASTERISK_HALF", "*"},
            {"ASTERISK_FULL", "＊"},
            {"TENSU_SEARCH_HALF", "///"},
            {"TENSU_SEARCH_FULL", "／／／"},
            {"COMMENT_SEARCH_HALF", "8"},
            {"COMMENT_SEARCH_FULL", "８"},
            // Slot
            {"SLOT_SYUGI","手技"},
            {"SLOT_NAIYO_YAKU","内用薬"},
            {"SLOT_TYUSHYA_YAKU","注射薬"},
            {"SLOT_GAIYO_YAKU","外用薬"},
            {"SLOT_YAKUZAI","薬剤"},
            {"SLOT_MEDICINE","薬"},
            {"SLOT_ZAIRYO","材料"},
            {"SLOT_YOHO","用法"},
            {"SLOT_BUI","部位"},
            {"SLOT_OTHER","その他"},
            // Unit
            {"UNIT_T","錠"},
            {"UNIT_G","ｇ"},
            {"UNIT_ML","ｍＬ"},
            {"UNIT_CAPSULE","カプセル"}
        };
    }
}
