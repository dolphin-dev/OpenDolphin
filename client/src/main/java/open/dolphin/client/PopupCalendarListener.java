package open.dolphin.client;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import open.dolphin.infomodel.SimpleDate;

/**
 * PopupCalendarListener
 * (予定カルテ対応)
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 * @author modified by masuda, Masuda Naika
 */
public class PopupCalendarListener extends MouseAdapter implements PropertyChangeListener {

    private static final int[] defaultRange = {-12, 0};
    private JPopupMenu popup;
    private int[] range;
//minagawa^ 予定カルテ    
    private SimpleDate[] acceptRange;
//minagawa$    
    protected JTextField tf;
    
    public PopupCalendarListener(JTextField tf) {
        this(tf, defaultRange);
    }
    
    public PopupCalendarListener(JTextField tf, int[] range) {
        this.tf = tf;
        this.range = range;
        tf.addMouseListener(PopupCalendarListener.this);
    }
    
//minagawa^ 予定カルテ    
    public PopupCalendarListener(JTextField tf, int[] range, SimpleDate[] acceptRange) {
        this.tf = tf;
        this.range = range;
        this.acceptRange = acceptRange;
        tf.addMouseListener(PopupCalendarListener.this);
    }
//minagawa$    
    
    public void setValue(SimpleDate sd) {
        tf.setText(SimpleDate.simpleDateToMmldate(sd));
    }

    @Override
    public final void mousePressed(MouseEvent e) {
        maybeShowPopup(e);
    }

    @Override
    public final void mouseReleased(MouseEvent e) {
        maybeShowPopup(e);
    }

    private void maybeShowPopup(MouseEvent e) {

        if (e.isPopupTrigger()) {
            popup = new JPopupMenu();
            CalendarCardPanel cc = new CalendarCardPanel(ClientContext.getEventColorTable());
            cc.addPropertyChangeListener(CalendarCardPanel.PICKED_DATE, this);
            cc.setCalendarRange(range);
//minagawa^ 予定カルテ            
            cc.setAcceptRange(acceptRange);
//minagawa$            
            popup.insert(cc, 0);
            popup.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    @Override
    public final void propertyChange(PropertyChangeEvent e) {
        if (e.getPropertyName().equals(CalendarCardPanel.PICKED_DATE)) {
            SimpleDate sd = (SimpleDate)e.getNewValue();
            setValue(sd);
            popup.setVisible(false);
            popup = null;
        }
    }
}
