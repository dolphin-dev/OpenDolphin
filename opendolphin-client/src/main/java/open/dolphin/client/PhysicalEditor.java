package open.dolphin.client;

import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.PhysicalModel;
import open.dolphin.infomodel.SimpleDate;

/**
 * 身長体重データを編集するエディタクラス。
 * 
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class PhysicalEditor {
    
    private PhysicalInspector inspector;
    private PhysicalEditorView view;
    private JDialog dialog;
    private JButton addBtn;
    private JButton clearBtn;
    private boolean ok;
    
    private void checkBtn() {
        
        boolean newOk = true;
        String height = view.getHeightFld().getText().trim();
        String weight = view.getWeightFld().getText().trim();
        String dateStr = view.getIdentifiedDateFld().getText().trim();
        
        if (height.equals("") && weight.equals("")) {
            newOk = false;
        } else if (dateStr.equals("")) {
            newOk = false;
        }
        
        if (ok != newOk) {
            ok = newOk;
            addBtn.setEnabled(ok);
            clearBtn.setEnabled(ok);
        }
    }
    
    private void add() {
        
        String h = view.getHeightFld().getText().trim();
        String w = view.getWeightFld().getText().trim();
        final PhysicalModel model = new PhysicalModel();

        if (!h.equals("")) {
            model.setHeight(h);
        }
        if (!w.equals("")) {
            model.setWeight(w);
        }

        // 同定日
        String confirmedStr = view.getIdentifiedDateFld().getText().trim();
        model.setIdentifiedDate(confirmedStr);
        
        addBtn.setEnabled(false);
        clearBtn.setEnabled(false);
        inspector.add(model);
    }
    
    private void clear() {
        view.getHeightFld().setText("");
        view.getWeightFld().setText("");
        view.getIdentifiedDateFld().setText("");
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
    
    public PhysicalEditor(PhysicalInspector inspector) {
        
        this.inspector = inspector;
        
        DocumentListener dl = new DocumentListener() {
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
        };
        
        view = new PhysicalEditorView();
        
        view.getHeightFld().getDocument().addDocumentListener(dl);
        view.getWeightFld().getDocument().addDocumentListener(dl);
        view.getIdentifiedDateFld().getDocument().addDocumentListener(dl);
        
        // Return で移動
        view.getHeightFld().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                view.getWeightFld().requestFocus();
            }
        });
        
        view.getWeightFld().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                view.getIdentifiedDateFld().requestFocus();
            }
        });
        
        // Alignment
        view.getHeightFld().setHorizontalAlignment(SwingConstants.RIGHT);
        view.getWeightFld().setHorizontalAlignment(SwingConstants.RIGHT);
        
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(IInfoModel.DATE_WITHOUT_TIME);
        String todayString = sdf.format(date);
        view.getIdentifiedDateFld().setText(todayString);
        PopupListener npl = new PopupListener(view.getIdentifiedDateFld());
        
        view.getHeightFld().addFocusListener(AutoRomanListener.getInstance());
        view.getWeightFld().addFocusListener(AutoRomanListener.getInstance());
        view.getIdentifiedDateFld().addFocusListener(AutoRomanListener.getInstance());
        
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
        dialog = pane.createDialog(inspector.getContext().getFrame(), ClientContext.getFrameTitle("身長体重登録"));
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                view.getHeightFld().requestFocus();
            }
        });
        dialog.setVisible(true);
    }
}
