package open.dolphin.stampbox;

import java.util.ListResourceBundle;

/**
 *
 * @author Kazushi Minagawa. LSC
 */
public class StampBoxResource_ja extends ListResourceBundle {

    @Override
    protected Object[][] getContents() {
        return new Object[][] {
            {"TABNAME_DIAGNOSIS","傷病名"},
            {"TABNAME_TEXT","テキスト"},
            {"TABNAME_PATH","パ ス"},
            {"TABNAME_ORCA","ORCA"},
            {"TABNAME_GENERAL","汎 用"},
            {"TABNAME_OTHER","その他"},
            {"TABNAME_RADIOLOGY","放射線"},
            {"TABNAME_LABO","検体検査"},
            {"TABNAME_PHYSIOLOGY","生体検査"},
            {"TABNAME_BACTERIA","細菌検査"},
            {"TABNAME_SURGERY","手 術"},
            {"TABNAME_TREATMENT","処 置"},
            {"TABNAME_INJECTION","注 射"},
            {"TABNAME_MED","処 方"},
            {"TABNAME_INSTRACTION","指導・在宅"},
            {"TABNAME_BASE_CHARGE","初診・再診"},
            {"TAB_INDEX_ORCA", 3},
            {"STAMP_NAMES", new String[]{
                "傷病名","テキスト","パ ス","ORCA","汎 用","その他",
                "処 置","手 術","放射線","検体検査","生体検査","細菌検査",
                "注 射","処 方","初診・再診","指導・在宅"}},
            {"diagnosis","傷病名"},
            {"text","テキスト"},
            {"path","パ ス"},
            {"orcaSet","ORCA"},
            {"generalOrder","汎 用"},
            {"otherOrder","その他"},
            {"radiologyOrder","放射線"},
            {"testOrder","検体検査"},
            {"physiologyOrder","生体検査"},
            {"bacteriaOrder","細菌検査"},
            {"surgeryOrder","手 術"},
            {"treatmentOrder","処 置"},
            {"injectionOrder","注 射"},
            {"medOrder","処 方"},
            {"instractionChargeOrder","指導・在宅"},
            {"baseChargeOrder","初診・再診"}
        };
    }
}
