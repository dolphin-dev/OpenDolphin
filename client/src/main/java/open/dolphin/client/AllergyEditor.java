package open.dolphin.client;

import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
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
    
    private final AllergyInspector inspector;
    private AllergyEditorView view;
    private JDialog dialog;
    private final JButton addBtn;
    private final JButton clearBtn;
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
        //if (!dateStr.equals("")) {
        if(dateStr != null) {
            String[] tmp = dateStr.split("-");
            if(dateStr.length() != 10 || tmp.length != 3) {
                String warning = ClientContext.getMyBundle(AllergyEditor.class).getString("warning.identifiedDate");
                JOptionPane.showMessageDialog(null, warning, ClientContext.getString("productString"), JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            model.setIdentifiedDate(dateStr);
        }
        addBtn.setEnabled(false);
        clearBtn.setEnabled(false);
        inspector.add(model);
    }
    
    private void clear() {
        view.getFactorFld().setText("");
        view.getMemoFld().setText("");
    }
    
    class PopupListener extends MouseAdapter implements PropertyChangeListener {

        private JPopupMenu popup;
        private final JTextField tf;

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
//s.oh^ 不具合修正
                checkBtn();
//s.oh$
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
//s.oh^ 不具合修正
                //checkBtn();
                SwingUtilities.invokeLater(() -> {
                    checkBtn();
                });
//s.oh$
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
//s.oh^ 不具合修正
                //checkBtn();
                SwingUtilities.invokeLater(() -> {
                    checkBtn();
                });
//s.oh$
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
//s.oh^ 不具合修正
                //checkBtn();
                SwingUtilities.invokeLater(() -> {
                    checkBtn();
                });
//s.oh$
            }
        });
        
//s.oh^ 不具合修正
        view.getIdentifiedFld().getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                SwingUtilities.invokeLater(() -> {
                    checkBtn();
                });
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                SwingUtilities.invokeLater(() -> {
                    checkBtn();
                });
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                SwingUtilities.invokeLater(() -> {
                    checkBtn();
                });
            }
        });
//s.oh$
        
        view.getMemoFld().addFocusListener(AutoKanjiListener.getInstance());
        
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(IInfoModel.DATE_WITHOUT_TIME);
        String todayString = sdf.format(date);
        view.getIdentifiedFld().setText(todayString);
        PopupListener pl = new PopupListener(view.getIdentifiedFld());
        view.getIdentifiedFld().addFocusListener(AutoRomanListener.getInstance());
        
        java.util.ResourceBundle bundle = ClientContext.getMyBundle(AllergyEditor.class);
        
        String addText = bundle.getString("actionText.add");
        addBtn = new JButton(addText);
        addBtn.addActionListener((ActionEvent e) -> {
            add();
            dialog.setVisible(false);
        });
        addBtn.setEnabled(false);
        
        String clearText = bundle.getString("actionText.clear");
        clearBtn = new JButton(clearText);
        clearBtn.addActionListener((ActionEvent e) -> {
            clear();
        });
        clearBtn.setEnabled(false);
                
        Object[] options = new Object[]{addBtn,clearBtn};
        
        String title = bundle.getString("title.optionPane.addAllergy");
        JOptionPane pane = new JOptionPane(view,
                                           JOptionPane.PLAIN_MESSAGE,
                                           JOptionPane.DEFAULT_OPTION,
                                           null,
                                           options, addBtn);
        dialog = pane.createDialog(inspector.getContext().getFrame(), ClientContext.getFrameTitle(title));
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                view.getFactorFld().requestFocus();
            }
        });
        dialog.setVisible(true);
    }
}
