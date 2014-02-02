/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.utilities.control;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EventObject;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

/**
 * アコーディオンパネル
 * @author Life Sciences Computing Corporation.
 */
public class AccordionPanel extends JScrollPane {
    private Box verticalBox;
    private Component verticalGlue;
    private AccordionListener acdListener;

    /**
     * コンストラクタ
     */
    public AccordionPanel() {
        super(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        verticalBox = Box.createVerticalBox();
        verticalGlue = Box.createVerticalGlue();
        
        getVerticalScrollBar().setUnitIncrement(25);
        getViewport().add(verticalBox);
        verticalBox.setOpaque(true);
        verticalBox.setBackground(Color.WHITE);
        verticalBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        acdListener = new AccordionListener() {
            @Override
            public void accordionStateChanged(AccordionEvent e) {
                verticalBox.revalidate();
            }
        };
        
        getViewport().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                verticalBox.revalidate();
            }
        });
    }
    
    /**
     * コンポーネントの追加
     * @param title タイトル
     * @param comp コンポーネント
     */
    public void addComponent(String title, Component comp) {
        AccordionPartPanel accordion = new AccordionPartPanel(title, comp);
        
        verticalBox.remove(verticalGlue);
        verticalBox.add(Box.createVerticalStrut(5));
        verticalBox.add(accordion);
        verticalBox.add(verticalGlue);
        verticalBox.revalidate();
        
        accordion.addAccordionListener(acdListener);
    }
    
    /**
     * パネルを開く
     * @param title タイトル
     */
    public void openPanel(String title) {
        for(int i = 0; i < verticalBox.getComponentCount(); i++) {
            Component comp = verticalBox.getComponent(i);
            if(comp != null && comp instanceof AccordionPartPanel) {
                AccordionPartPanel panel = (AccordionPartPanel)comp;
                String val = panel.getTitle();
                if(title.equals(val)) {
                    panel.changeStatePanel();
                    break;
                }
            }
        }
    }
    
    /**
     * パネルが開いてるかどうかの取得
     * @param title タイトル
     * @return Open/Close
     */
    public boolean isPanelOpen(String title) {
        boolean open = false;
        for(int i = 0; i < verticalBox.getComponentCount(); i++) {
            Component comp = verticalBox.getComponent(i);
            if(comp != null && comp instanceof AccordionPartPanel) {
                AccordionPartPanel panel = (AccordionPartPanel)comp;
                String val = panel.getTitle();
                if(title.equals(val)) {
                    if(panel.isPanelOpen()) {
                        open = true;
                    }
                    break;
                }
            }
        }
        return open;
    }
    
    public static void main( String[] args ) {
        //JFrame frame = new JFrame();
        //AccordionPanel panel = new AccordionPanel();
        //panel.addPanel("Test", new JPanel());
        //frame.add(panel);
    }
}

/**
 * アコーディオンの部品
 * @author Life Sciences Computing Corporation.
 */
class AccordionPartPanel extends JPanel {
    private static final String MARK_CLOSE = "▲ ";
    private static final String MARK_OPEN = "▽ ";
    private String partTitle;
    private JLabel partLabel;
    private JPanel partPanel;
    private ArrayList accordionListenerList;
    private Color COLOR_TITLE_GRADATION1;
    private Color COLOR_TITLE_GRADATION2;
    private Color COLOR_TITLE_TEXT;
    private Color COLOR_BODY_BACKGROUND;

    /**
     * コンストラクタ
     * @param title タイトル
     * @param comp コンポーネント
     */
    public AccordionPartPanel(String title, Component comp) {
        super(new BorderLayout());
        
        accordionListenerList = new ArrayList();
        partTitle = title;
        COLOR_TITLE_GRADATION1 = Color.WHITE;
        COLOR_TITLE_GRADATION2 = Color.ORANGE;
        COLOR_TITLE_TEXT = Color.BLACK;
        COLOR_BODY_BACKGROUND = Color.GRAY;
        
        partLabel = new JLabel(MARK_CLOSE + partTitle) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                //g2.setPaint(new GradientPaint(50, 0, COLOR_TITLE_GRADATION1, getWidth(), getHeight(), COLOR_TITLE_GRADATION2));
                g2.setPaint(new GradientPaint(0, 0, COLOR_TITLE_GRADATION1, 0, 30, COLOR_TITLE_GRADATION2));
                g2.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        partLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                changeStatePanel();
            }
        });
        partLabel.setForeground(COLOR_TITLE_TEXT);
        //partLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 2));
        partLabel.setBorder(BorderFactory.createBevelBorder(EtchedBorder.LOWERED));
        partLabel.setPreferredSize(new Dimension(0, 50));
        Font font = new Font("Label.font", Font.BOLD, 13);
        partLabel.setFont(font);
        add(partLabel, BorderLayout.NORTH);

        partPanel = new JPanel();
        partPanel.add(comp);
        partPanel.setVisible(false);
        partPanel.setOpaque(true);
        partPanel.setBackground(COLOR_BODY_BACKGROUND);
        Border outBorder = BorderFactory.createMatteBorder(0, 2, 2, 2, Color.WHITE);
        Border inBorder  = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        Border border    = BorderFactory.createCompoundBorder(outBorder, inBorder);
        partPanel.setBorder(border);
        add(partPanel);
    }
    
    /**
     * サイズの取得
     * @return サイズ
     */
    @Override
    public Dimension getPreferredSize() {
        Dimension d = partLabel.getPreferredSize();
        if(partPanel.isVisible()) {
            d.height += partPanel.getPreferredSize().height;
        }
        return d;
    }
    
    /**
     * 最大サイズの取得
     * @return サイズ
     */
    @Override
    public Dimension getMaximumSize() {
        Dimension d = getPreferredSize();
        d.width = Short.MAX_VALUE;
        return d;
    }
    
    /**
     * パネルの状態変更
     */
    public void changeStatePanel() {
        partPanel.setVisible(!partPanel.isVisible());
        partLabel.setText((partPanel.isVisible() ? MARK_OPEN : MARK_CLOSE) + partTitle);
        revalidate();
        execAccordionEvent();
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                partPanel.scrollRectToVisible(partPanel.getBounds());
            }
        });
    }
    
    /**
     * アコーディオンリスナーの追加
     * @param listener 
     */
    public void addAccordionListener(AccordionListener listener) {
        if(!accordionListenerList.contains(listener)) {
            accordionListenerList.add(listener);
        }
    }
    
    /**
     * アコーディオンリスナーの削除
     * @param listener 
     */
    public void removeAccordionListener(AccordionListener listener) {
        accordionListenerList.remove(listener);
    }
    
    /**
     * アコーディオンイベントの実行
     */
    public void execAccordionEvent() {
        ArrayList list = (ArrayList)accordionListenerList.clone();
        AccordionEvent e = new AccordionEvent(this);
        for(int i = 0; i < list.size(); i++) {
            AccordionListener listener = (AccordionListener)list.get(i);
            listener.accordionStateChanged(e);
        }
    }
    
    /**
     * タイトルの取得
     * @return タイトル
     */
    public String getTitle() {
        return partTitle;
    }
    
    /**
     * パネルが開いてるかどうかの取得
     * @return Open/Close
     */
    public boolean isPanelOpen() {
        return partPanel.isVisible();
    }
}

/**
 * アコーディオンイベント
 * @author Life Sciences Computing Corporation.
 */
class AccordionEvent extends EventObject{
    /**
     * コンストラクタ
     * @param source 親
     */
    public AccordionEvent(Object source) {
        super(source);
    }
}

/**
 * アコーディオンリスナーのインターフェース
 * @author Life Sciences Computing Corporation.
 */
interface AccordionListener{
    /**
     * Boxの再描画
     * @param e イベント
     */
    public void accordionStateChanged(AccordionEvent e);
}
