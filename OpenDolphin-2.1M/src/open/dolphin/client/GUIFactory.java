package open.dolphin.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.DocumentListener;

/**
 * @author Kazushi Minagawa Digital Globe, Inc.
 *
 */
public class GUIFactory {
    
    private static final int BUTTON_GAP 		= 5;
    //private static final int LABEL_ITEM_GAP 		= 7;
    private static final int TF_MARGIN_TOP  		= 1;
    private static final int TF_MARGIN_LEFT  		= 2;
    private static final int TF_MARGIN_BOTTOM  		= 1;
    private static final int TF_MARGIN_RIGHT  		= 2;
    private static final int TF_LENGTH          	= 30;
    private static final int TITLE_SPACE_TOP  		= 6;
    private static final int TITLE_SPACE_LEFT  		= 6;
    private static final int TITLE_SPACE_BOTTOM 	= 5;
    private static final int TITLE_SPACE_RIGHT  	= 5;
    
    private static Color dropOkColor = new Color(0, 12, 156);
    
    public static Font createSmallFont() {
        return new Font("Dialog", Font.PLAIN, 10);
    }
    
    public static JButton createOkButton() {
        return new JButton((String)UIManager.get("OptionPane.okButtonText"));
    }
    
    public static JButton createCancelButton() {
        return new JButton((String)UIManager.get("OptionPane.cancelButtonText"));
    }
    
    public static JButton createButton(String text, String mnemonic, ActionListener al) {
        
        JButton ret = new JButton(text);
        
        if (al != null) {
            ret.addActionListener(al);
        }
        
//        if (mnemonic != null) {
//            ret.setMnemonic(mnemonic.charAt(0));
//        }
        
        return ret;
    }
    
    public static JRadioButton createRadioButton(String text, ActionListener al, ButtonGroup bg) {
        
        JRadioButton radio = new JRadioButton(text);
        
        if (al != null) {
            radio.addActionListener(al);
        }
        
        if (bg != null) {
            bg.add(radio);
        }
        
        return radio;
    }
    
    public static JCheckBox createCheckBox(String text, ActionListener al) {
        
        JCheckBox ret = new JCheckBox(text);
        
        if (al != null) {
            ret.addActionListener(al);
        }
        
        return ret;
    }
    
    public static JTextField createTextField(int val, Insets margin, FocusListener fa, DocumentListener dl) {
        
        if (val == 0) {
            val = TF_LENGTH;
        }
        JTextField tf = new JTextField(val);
        
        if (margin == null) {
            margin = new Insets(TF_MARGIN_TOP, TF_MARGIN_LEFT, TF_MARGIN_BOTTOM, TF_MARGIN_RIGHT);
        }
        tf.setMargin(margin);
        
        if (dl != null) {
            tf.getDocument().addDocumentListener(dl);
        }
        
        if (fa != null) {
            tf.addFocusListener(fa);
        }
        
        return tf;
    }
    
    public static JPasswordField createPassField(int val, Insets margin, FocusListener fa, DocumentListener dl) {
        
        val = val == 0 ? val = TF_LENGTH : val;
        JPasswordField tf = new JPasswordField(val);
        
        margin = margin == null ? new Insets(TF_MARGIN_TOP, TF_MARGIN_LEFT, TF_MARGIN_BOTTOM, TF_MARGIN_RIGHT) : margin;
        tf.setMargin(margin);
        
        if (dl != null) {
            tf.getDocument().addDocumentListener(dl);
        }
        
        if (fa != null) {
            tf.addFocusListener(fa);
        }
        
        return tf;
    }
    
    /**
     * FlowLayout にボタンを配置したパネルを生成する。
     * @param btns 配置する Button の配列
     * @param align 配置する方向（FlowLayout.RIGHT/LEFT）
     * @return 5 ピクセル間隔でボタンが配置されたパネル
     */
    public static JPanel createButtonPanel(JButton[] btns, int align) {
        JPanel p = new JPanel(new FlowLayout(align, BUTTON_GAP, 0));
        for (int i = 0; i < btns.length; i++) {
            p.add(btns[i]);
        }
        return p;
    }
    
    /**
     * 右ずめにボタンを配置したパネルを生成する。
     * @param btns 配置する Button の配列
     * @return ボタンが配列されたパネル（左に水平 Glue、右はマージンなし）
     */
    public static JPanel createCommandButtonPanel(JButton[] btns) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.add(Box.createHorizontalGlue());
        p.add(btns[0]);
        for (int i = 1; i < btns.length; i++) {
            p.add(Box.createHorizontalStrut(BUTTON_GAP));
            p.add(btns[i]);
        }
        return p;
    }
    
    public static JPanel createRadioPanel(JRadioButton[] rbs) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, BUTTON_GAP, 0));
        for (int i = 0; i < rbs.length; i++) {
            p.add(rbs[i]);
        }
        return p;
    }
    
    public static JPanel createCheckBoxPanel(JCheckBox[] boxes) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT,BUTTON_GAP, 0));
        for (int i = 0; i < boxes.length; i++) {
            p.add(boxes[i]);
        }
        return p;
    }
    
    public static JPanel createTitledPanel(JComponent c, String title) {
        c.setBorder(BorderFactory.createEmptyBorder(TITLE_SPACE_TOP, TITLE_SPACE_LEFT, TITLE_SPACE_BOTTOM, TITLE_SPACE_RIGHT));
        JPanel p = new JPanel(new BorderLayout());
        p.add(c, BorderLayout.CENTER);
        p.setBorder(BorderFactory.createTitledBorder(title));
        return p;
    }
    
    public static JPanel createZipCodePanel(JTextField tf1, JTextField tf2) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
        p.add(tf1);
        p.add(new JLabel(" - "));
        p.add(tf2);
        return p;
    }
    
    public static JPanel createPhonePanel(JTextField tf1, JTextField tf2, JTextField tf3) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0,0));
        p.add(tf1);
        p.add(new JLabel(" - "));
        p.add(tf2);
        p.add(new JLabel(" - "));
        p.add(tf3);
        return p;
    }
    
    public static JScrollPane createVScrollPane(JComponent c) {
        JScrollPane scroller = new JScrollPane(c);
        scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return scroller;
    }
    
    public static JScrollPane createHScrollPane(JComponent c) {
        JScrollPane scroller = new JScrollPane(c);
        scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        return scroller;
    }
    
    public static JPanel createZeroPanel(JComponent jc) {
        JPanel ret = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
        ret.add(jc);
        return ret;
    }
    
    public static String getCancelButtonText() {
        return (String) UIManager.get("OptionPane.cancelButtonText");
    }
    
    public static Point getCenterLoc(int width, int height) {
        Dimension screen = Toolkit.getDefaultToolkit ().getScreenSize ();
        int x = (screen.width - width) / 2;
        int y = (screen.height - height ) / 3;
        return new Point(x, y);
    }
    
    public static Color getDropOkColor() {
        return dropOkColor;
    }
}
