package open.dolphin.stampbox;

import java.util.ListResourceBundle;

/**
 *
 * @author Kazushi Minagawa. LSC
 */
public class StampBoxResource extends ListResourceBundle {

    @Override
    protected Object[][] getContents() {
        return new Object[][] {
            {"TABNAME_DIAGNOSIS","Disease"},
            {"TABNAME_TEXT","Text"},
            {"TABNAME_PATH","Path"},
            {"TABNAME_ORCA","ORCA"},
            {"TABNAME_GENERAL","General"},
            {"TABNAME_OTHER","Other"},
            {"TABNAME_RADIOLOGY","Image"},
            {"TABNAME_LABO","Lab Test"},
            {"TABNAME_PHYSIOLOGY","Physiology"},
            {"TABNAME_BACTERIA","Bacteria"},
            {"TABNAME_SURGERY","Surgery"},
            {"TABNAME_TREATMENT","Treatment"},
            {"TABNAME_INJECTION","Injection"},
            {"TABNAME_MED","Medication"},
            {"TABNAME_INSTRACTION","Instraction"},
            {"TABNAME_BASE_CHARGE","Base Fee"},
            {"TAB_INDEX_ORCA", 3},
            {"STAMP_NAMES", new String[]{
                "Disease","Text","Path","ORCA","General","Other",
                "Treatment","Surgery","Image","Lab Test","Physiology","Bacteria",
                "Injection","Medication","Base Fee","Instruction"}},
            {"diagnosis","Disease"},
            {"text","Text"},
            {"path","Path"},
            {"orcaSet","ORCA"},
            {"generalOrder","General"},
            {"otherOrder","Other"},
            {"radiologyOrder","Image"},
            {"testOrder","Lab Test"},
            {"physiologyOrder","Physiology"},
            {"bacteriaOrder","Bacteria"},
            {"surgeryOrder","Surgery"},
            {"treatmentOrder","Treatment"},
            {"injectionOrder","Injection"},
            {"medOrder","Medication"},
            {"instractionChargeOrder","Instruction"},
            {"baseChargeOrder","Base Fee"}
        };
    }
}
