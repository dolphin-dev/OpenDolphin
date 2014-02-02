package open.dolphin.client;

import java.awt.*;
import java.util.*;
import java.text.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * MasterRenderer
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class MasterRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 4397037194989155042L;
    protected Color beforStartColor;
    protected Color afterEndColor;
    protected Color inUseColor;
    protected String refDate;
    protected static final Color DEFAULT_ODD_COLOR = Color.white;
    protected static final Color DEFAULT_EVENN_COLOR = new Color(237, 243, 254);
    protected Color oddColor;
    protected Color evenColor;

    /** Creates a new instance of MasterRenderer */
    public MasterRenderer() {
        setOpaque(true);
        setOddColor(oddColor);
        setEvenColor(evenColor);
        GregorianCalendar gc = new GregorianCalendar();
        SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
        refDate = f.format(gc.getTime()).toString();
    }

    public MasterRenderer(String refDate) {
        super();
        this.refDate = refDate;
    }

    /**
     * @param oddColor The oddColor to set.
     */
    public void setOddColor(Color oddColor) {
        this.oddColor = oddColor;
    }

    /**
     * @return Returns the oddColor.
     */
    public Color getOddColor() {
        return oddColor;
    }

    /**
     * @param evenColor The evenColor to set.
     */
    public void setEvenColor(Color evenColor) {
        this.evenColor = evenColor;
    }

    /**
     * @return Returns the evenColor.
     */
    public Color getEvenColor() {
        return evenColor;
    }

    public Color getBeforStartColor() {
        return beforStartColor;
    }

    public void setBeforStartColor(Color val) {
        beforStartColor = val;
    }

    public Color getAfterEndColor() {
        return afterEndColor;
    }

    public void setAfterEndColor(Color val) {
        afterEndColor = val;
    }

    public Color getInUseColor() {
        return inUseColor;
    }

    public void setInUseColor(Color val) {
        inUseColor = val;
    }

    public void setColor(JLabel label, String startDate, String endDate) {

        switch (useState(startDate, endDate)) {

            case 0:
                label.setEnabled(false);
                //label.setForeground(beforStartColor);
                label.setForeground(Color.BLACK);
                break;

            case 1:
                label.setEnabled(true);
                //label.setForeground(inUseColor);
                label.setForeground(Color.BLACK);
                break;

            case 2:
                label.setEnabled(false);
                //label.setForeground(afterEndColor);
                label.setForeground(Color.BLACK);
                break;
        }
    }

    public void setColor(JLabel label, String endDate) {

        setColor(label, null, endDate);
    }

    protected int useState(String startDate, String endDate) {

        if (startDate != null && refDate.compareTo(startDate) < 0) {
            return 0;

        } else if (endDate != null && refDate.compareTo(endDate) > 0) {
            return 2;
        }

        return 1;
    }
}