/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package open.dolphin.hiro;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import open.dolphin.infomodel.IInfoModel;

/**
 *
 * @author Masato
 */
public class Utils {

    /**
     * ボタンに設定されているActionCommand と パラメータvalue が合致すれば、選択状態にする。
     * @param group ButtonGroup
     * @param value String
     */
    public static void setBtnValue(ButtonGroup group, String value) {
        for (Enumeration<AbstractButton> e = group.getElements(); e.hasMoreElements();) {
            AbstractButton btn = e.nextElement();
            if (btn.getActionCommand().equals(value)) {
                group.setSelected(btn.getModel(), true);
                break;
            }
        }
    }

    /**
     * 文字列を指定されている日付型に変換する。
     * 変換できない、または変換エラーの場合 null を返す。
     * @param target 文字列
     * @return 日付(型：yyyy-MM-dd)
     */
    public static Date chkDate(String target) {
        Date ret = null;
        try {
            SimpleDateFormat f = getDateFormat();
            f.setLenient(false); // 厳密にチェックする
            if ((target) != null && !"".equals(target)) {
                Date date = f.parse(target);
//                System.out.println("Parse String to Date : " + date);
                ret = date;
            }
        } catch (Exception e) {
//            System.err.println("Exception : input string = " + target);
            e.printStackTrace(System.err);
        }
        return ret;
    }

    /** 日付フォーマット */
    //private static SimpleDateFormat dateFormat;

    /**
     * 日付フォーマットを生成し返す。
     * @return SimpleDateFormat 日付フォーマット(yyyy-MM-dd)
     */
    public static SimpleDateFormat getDateFormat() {
//        try {
//            if (dateFormat == null) {
//                dateFormat = new SimpleDateFormat(IInfoModel.DATE_WITHOUT_TIME);
//                dateFormat.setLenient(false);
//            }
//        } catch (Exception e) {
//        }
//        return dateFormat;
        return new SimpleDateFormat(IInfoModel.DATE_WITHOUT_TIME);
    }
}
