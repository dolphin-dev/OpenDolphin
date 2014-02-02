package open.dolphin.letter;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import open.dolphin.client.CalendarCardPanel;
import open.dolphin.client.ClientContext;
import open.dolphin.infomodel.SimpleDate;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class PopupCalendarListener extends MouseAdapter implements PropertyChangeListener {
    
    private JPopupMenu popup;

    private JTextField tf;

    public PopupCalendarListener(JTextField tf) {
        this.tf = tf;
        tf.addMouseListener(PopupCalendarListener.this);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        maybeShowPopup(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        maybeShowPopup(e);
    }

    private void maybeShowPopup(MouseEvent e) {

        if (e.isPopupTrigger()) {
            popup = new JPopupMenu();
            CalendarCardPanel cc = new CalendarCardPanel(ClientContext.getEventColorTable());
            cc.addPropertyChangeListener(CalendarCardPanel.PICKED_DATE, this);
            cc.setCalendarRange(new int[]{-12, 0});
            popup.insert(cc, 0);
            popup.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        if (e.getPropertyName().equals(CalendarCardPanel.PICKED_DATE)) {
            SimpleDate sd = (SimpleDate) e.getNewValue();
            String mmldate = SimpleDate.simpleDateToMmldate(sd);
            tf.setText(mmldate);
            popup.setVisible(false);
            popup = null;
        }
    }
}
