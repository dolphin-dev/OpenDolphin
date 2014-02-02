package open.dolphin.letter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import open.dolphin.infomodel.ModelUtils;

/**
 *
 * @author Kazushi, Minagawa. Digital Globe, Inc.
 */
public class LetterHelper {

    protected static final String SIMPLE_DATE_FORMAT = "yyyy-MM-dd";
    protected static final SimpleDateFormat SDF = new SimpleDateFormat(SIMPLE_DATE_FORMAT);

    protected static void setModelValue(JTextField tf, String value) {
        if (value != null) {
            tf.setText(value);
        }
    }

    protected static void setModelValue(JTextArea ta, String value) {
        if (value != null) {
            ta.setText(value);
        }
    }

    protected static void setModelValue(JLabel lbl, String value) {
        if (value != null) {
            lbl.setText(value);
        }
    }

    protected static String getFieldValue(JTextField tf) {
        String ret = tf.getText().trim();
        if (!ret.equals("")) {
            return ret;
        }
        return null;
    }

    protected static String getAreaValue(JTextArea ta) {
        String ret = ta.getText().trim();
        if (!ret.equals("")) {
            return ret;
        }
        return null;
    }

    protected static String getLabelValue(JLabel lbl) {
        String ret = lbl.getText().trim();
        if (!ret.equals("")) {
            return ret;
        }
        return null;
    }

    protected static String getDateAsString(Date date, String patterm) {
        SimpleDateFormat sdf = new SimpleDateFormat(patterm);
        return sdf.format(date);
    }

    protected static String getDateAsString(Date date) {
        return getDateAsString(date, "yyyy年M月d日");
    }

    protected static Date getSimpleDateFromString(String dateStr) {
        try {
            Date ret = SDF.parse(dateStr);
            return ret;
        } catch (ParseException ex) {
            Logger.getLogger(LetterHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    protected static String getDateString(String mmlDate) {
        Date d = ModelUtils.getDateAsObject(mmlDate);
        return getDateAsString(d);
    }

    protected static String getBirdayWithAge(String birthday, String age) {
        StringBuilder sb = new StringBuilder();
        sb.append(birthday);
        sb.append(" (");
        sb.append(age);
        sb.append(" 歳)");
        return sb.toString();
    }

    protected static String getAddressWithZipCode(String address, String zip) {
        StringBuilder sb = new StringBuilder();
        sb.append(zip);
        sb.append(" ");
        sb.append(address);
        return sb.toString();
    }
}
