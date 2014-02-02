package open.dolphin.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import open.dolphin.infomodel.AllergyModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.SimpleDate;

/**
 * アレルギデータを編集するエディタクラス。
 * 
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class AllergyEditor {
    
    private AllergyInspector inspector;
    private AllergyEditorView view;
    private JDialog dialog;
    private JButton addBtn;
    private JButton clearBtn;
    private boolean ok;
    
    private void checkBtn() {
        
        String factor = view.getFactorFld().getText().trim();
        String date = view.getIdentifiedFld().getText().trim();
        
        boolean newOk = true;
        if (factor.equals("") || date.equals("")) {
            newOk = false;
        }
        
        if (ok != newOk) {
            ok = newOk;
            addBtn.setEnabled(ok);
            clearBtn.setEnabled(ok);
        }
    }
    
    private void add() {
        
        final AllergyModel model = new AllergyModel();
        model.setFactor(view.getFactorFld().getText().trim());
        model.setSeverity((String) view.getReactionCombo().getSelectedItem());
        String memo = view.getMemoFld().getText().trim();
        if (!memo.equals("")) {
            model.setMemo(memo);
        }
        String dateStr = view.getIdentifiedFld().getText().trim();
        if (!dateStr.equals("")) {
            model.setIdentifiedDate(dateStr);
        }
        addBtn.setEnabled(false);
        clearBtn.setEnabled(false);
        inspector.add(model);
    }
    
    private void clear() {
        view.getFactorFld().setText("");
        view.getMemoFld().setText("");
        view.getIdentifiedFld().setText("");
    }
    
    class PopupListener extends MouseAdapter implements PropertyChangeListener {

        private JPopupMenu popup;
        private JTextField tf;

        // private LiteCalendarPanel calendar;
        public PopupListener(JTextField tf) {
            this.tf = tf;
            tf.addMouseListener(PopupListener.this);
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
                tf.setText(SimpleDate.simpleDateToMmldate(sd));
                popup.setVisible(false);
                popup = null;
            }
        }
    }
    
    public AllergyEditor(AllergyInspector inspector) {
        
        this.inspector = inspector;
        view = new AllergyEditorView();
        view.getFactorFld().addFocusListener(AutoKanjiListener.getInstance());
        view.getFactorFld().getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                checkBtn();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkBtn();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkBtn();
            }
        });
        
        view.getMemoFld().addFocusListener(AutoKanjiListener.getInstance());
        
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(IInfoModel.DATE_WITHOUT_TIME);
        String todayString = sdf.format(date);
        view.getIdentifiedFld().setText(todayString);
        PopupListener pl = new PopupListener(view.getIdentifiedFld());
        view.getIdentifiedFld().addFocusListener(AutoRomanListener.getInstance());
        
        addBtn = new JButton("追加");
        addBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                add();
            }
        });
        addBtn.setEnabled(false);
        
        clearBtn = new JButton("クリア");
        clearBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clear();
            }
        });
        clearBtn.setEnabled(false);
                
        Object[] options = new Object[]{addBtn,clearBtn};
        
        JOptionPane pane = new JOptionPane(view,
                                           JOptionPane.PLAIN_MESSAGE,
                                           JOptionPane.DEFAULT_OPTION,
                                           null,
                                           options, addBtn);
        dialog = pane.createDialog(inspector.getContext().getFrame(), ClientContext.getFrameTitle("アレルギー登録"));
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                view.getFactorFld().requestFocus();
            }
        });
        dialog.setVisible(true);
    }
}
