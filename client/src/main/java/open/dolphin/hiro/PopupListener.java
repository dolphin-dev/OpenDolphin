/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.hiro;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import open.dolphin.client.CalendarCardPanel;
import open.dolphin.client.CalendarCardPanel;
import open.dolphin.client.ClientContext;
import open.dolphin.client.ClientContext;
import open.dolphin.infomodel.SimpleDate;

/* Created 2010/07/02 */
/**
 * テキストフィールドへ日付を入力するためのカレンダーポップアップメニュークラス
 * @author Masato
 */
public class PopupListener extends MouseAdapter implements PropertyChangeListener {

    /** ポップアップメニュー */
    private JPopupMenu popup;
    /** ターゲットのテキストフィールド */
    private JTextField tf;
    /** カレンダーの表示期間 開始月 */
    int start;
    /** カレンダーの表示期間 最終月 */
    int end;

    /**
     * コンストラクタ
     * @param tf
     */
    public PopupListener(JTextField tf) {
        this.tf = tf;
        tf.addMouseListener(this);
        this.start = -6;
        this.end = 6;
    }

    /**
     * コンストラクタ
     * @param tf
     * @param start 表示期間開始月
     * @param end 表示期間最終月
     */
    public PopupListener(JTextField tf, int start, int end) {
        this.tf = tf;
        tf.addMouseListener(this);
        this.start = start;
        this.end = end;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        maybeShowPopup(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        maybeShowPopup(e);
    }

    /**
     * カレンダーをポップアップ表示する。
     * @param e MouseEvent
     */
    private void maybeShowPopup(MouseEvent e) {

        if (e.isPopupTrigger()) {
            popup = new JPopupMenu();
            CalendarCardPanel cc = new CalendarCardPanel(ClientContext.getEventColorTable());
            cc.addPropertyChangeListener(CalendarCardPanel.PICKED_DATE, this);
            cc.setCalendarRange(new int[]{start, end});
            popup.insert(cc, 0);
            popup.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    /**
     * テキストフィールドにカレンダーの値を設定し、カレンダーを閉じる。
     * @param e PropertyChangeEvent
     */
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        if (CalendarCardPanel.PICKED_DATE.equals(e.getPropertyName())) {
            SimpleDate sd = (SimpleDate) e.getNewValue();
            tf.setText(SimpleDate.simpleDateToMmldate(sd));
            popup.setVisible(false);
            popup = null;
        }
    }

    /**
     * テキストフィールドを返す。
     * @return JTextField
     */
    public JTextField getTextField() {
        return tf;
    }
}
