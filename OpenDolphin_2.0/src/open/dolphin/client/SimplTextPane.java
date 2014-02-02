package open.dolphin.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.TransferHandler;
import javax.swing.WindowConstants;
import javax.swing.text.Style;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledEditorKit;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;

/**
 *
 * @author Kazushi Minagawa.
 */
public class SimplTextPane extends AbstractMainTool implements PropertyChangeListener {
    
    private static final String TITLE = "新規診療録";
    
    //private Logger logger = Logger.getLogger(this.getClass().getName());
    
    private Action saveAction;
    private Action undoAction;
    private Action redoAction;
    private Action cutAction;
    private Action copyAction;
    private Action pasteAction;
        
    private JTextPane currentPane;
    
    private JFrame frame;
    
    /** Creates a new instance of SimplTextPane */
    public SimplTextPane() {
        setName(TITLE);
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        
        String prop = e.getPropertyName();
        boolean enabled = ((Boolean)e.getNewValue()).booleanValue();
        
        if (prop.equals(BaseTextPane.CAN_UNDO)) {
            undoAction.setEnabled(enabled);
        
        } else if (prop.equals(BaseTextPane.CAN_REDO)) {
            redoAction.setEnabled(enabled);
            
        } else if (prop.equals(BaseTextPane.CAN_CUT_COPY)) {
            cutAction.setEnabled(enabled);
            copyAction.setEnabled(enabled);
        
        } else if (prop.equals(BaseTextPane.CAN_PASTE)) {
            pasteAction.setEnabled(enabled);
        
        } else if (prop.equals(BaseTextPane.DIRTY_PROP)) {
            saveAction.setEnabled(enabled);
        
        }
    }
    
    @Override
    public void start() {
        
        JPanel content = initComponents();
        content.setOpaque(true);
       
        // Frame を生成する
        frame = new JFrame(getName());
        frame.setName("SimplTextPane");
        frame.setContentPane(content);
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stop();
            }
        });
        frame.pack();
        int n = ClientContext.isMac() ? 3 : 2;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - frame.getWidth()) / 2;
        int y = (screenSize.height - frame.getHeight()) / n;
        frame.setLocation(x, y);
        
        // SessionStrage からリストアする
        try {
            ClientContext.getSessionStorage().restore(frame, "simpleTextPane.xml");
            
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
        
        frame.setVisible(true);
    }
    
    @Override
    public void stop() {
        try {
            ClientContext.getSessionStorage().save(frame, "simpleTextPane.xml");
            
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
        frame.setVisible(true);
        frame.dispose();
    }
    
    @org.jdesktop.application.Action
    public void newDocument() {
        
    }
    
    @org.jdesktop.application.Action
    public void openDocument() {
        
    }
    
    @org.jdesktop.application.Action
    public void saveDocument() {
        
    }
    
    @org.jdesktop.application.Action
    public void closeDocument() {
        
    }
    
////    @org.jdesktop.application.Action
////    public void myPrinterSetup() {
////        
////    }
//    
    @org.jdesktop.application.Action
    public void myPrint() {
        
    }
    
    @org.jdesktop.application.Action
    public void myCut() {
        if (currentPane != null) {
            Action a = currentPane.getActionMap().get(TransferHandler.getCutAction().getValue(Action.NAME));
            if (a != null) {
                a.actionPerformed(new ActionEvent(currentPane,
                        ActionEvent.ACTION_PERFORMED,
                        null));
            }
        }        
    }
    
    @org.jdesktop.application.Action
    public void myCopy() {
        if (currentPane != null) {
            Action a = currentPane.getActionMap().get(TransferHandler.getCopyAction().getValue(Action.NAME));
            if (a != null) {
                a.actionPerformed(new ActionEvent(currentPane,
                        ActionEvent.ACTION_PERFORMED,
                        null));
            }
        }
    }
    
    @org.jdesktop.application.Action
    public void myPaste() {
        if (currentPane != null) {
            Action a = currentPane.getActionMap().get(TransferHandler.getPasteAction().getValue(Action.NAME));
            if (a != null) {
                a.actionPerformed(new ActionEvent(currentPane,
                        ActionEvent.ACTION_PERFORMED,
                        null));
            }
        }
    }
    
    @org.jdesktop.application.Action
    public void myUndo() {
        if (currentPane != null) {
            BaseTextPane pane = (BaseTextPane) currentPane.getClientProperty("baseTextPane");
            if (pane != null) {
                pane.undo();
            }
        }
    }
    
    @org.jdesktop.application.Action
    public void myRedo() {
        if (currentPane != null) {
            BaseTextPane pane = (BaseTextPane) currentPane.getClientProperty("baseTextPane");
            if (pane != null) {
                pane.redo();
            }
        }
    }
    
    @org.jdesktop.application.Action
    public void reset() {
        
        Style s = currentPane.getStyledDocument().getStyle(StyleContext.DEFAULT_STYLE);
        currentPane.setCharacterAttributes(s, true);
    }
    
    private JPanel initComponents() {
        
        BaseTextPane basePane = new BaseTextPane();
        JTextPane textPane = basePane.getTextPane();
        
        ActionMap actions = textPane.getActionMap();
        
        JButton newBtn = new JButton();
        newBtn.setName("newBtn");
        
        JButton openBtn = new JButton();
        openBtn.setName("openBtn");
        
        JButton closeBtn = new JButton();
        closeBtn.setName("closeBtn");
        
        JButton printBtn = new JButton();
        printBtn.setName("printBtn");
        
        JButton saveBtn = new JButton();
        saveBtn.setName("saveBtn");
        
        JButton undoBtn = new JButton();
        undoBtn.setName("undoBtn");
        
        JButton redoBtn = new JButton();
        redoBtn.setName("redoBtn");
        
        JButton cutBtn = new JButton();
        cutBtn.setName("cutBtn");
        
        JButton copyBtn = new JButton();
        copyBtn.setName("copyBtn");
        
        JButton pasteBtn = new JButton();
        pasteBtn.setName("pasteBtn");
        //
        //
        //
        JButton boldBtn = new JButton();
        boldBtn.setName("boldBtn");
        boldBtn.setAction(actions.get("font-bold"));
        
        JButton italicBtn = new JButton();
        italicBtn.setName("italicBtn");
        italicBtn.setAction(actions.get("font-italic"));
        
        JButton underlineBtn = new JButton();
        underlineBtn.setName("underlineBtn");
        underlineBtn.setAction(actions.get("font-underline"));
        
        JButton leftBtn = new JButton(); // left-justify
        leftBtn.setName("leftBtn");
        leftBtn.setAction(actions.get("left-justify"));
        
        JButton centerBtn = new JButton();
        centerBtn.setName("centerBtn");
        centerBtn.setAction(actions.get("center-justify"));
        
        JButton rightBtn = new JButton();
        rightBtn.setName("rightBtn");
        rightBtn.setAction(actions.get("right-justify"));
        
        JButton size10Btn = new JButton();
        size10Btn.setName("size10Btn");
        size10Btn.setAction(actions.get("font-size-10"));
        
        JButton size12Btn = new JButton();
        size12Btn.setName("size12Btn");
        size12Btn.setAction(actions.get("font-size-12"));
        
        JButton size18Btn = new JButton();
        size18Btn.setName("size18Btn");
        size18Btn.setAction(actions.get("font-size-18"));
        
        JButton redBtn = new JButton();
        redBtn.setName("redBtn");
        redBtn.setAction(new StyledEditorKit.ForegroundAction("赤", Color.red));
        
        JButton resetBtn = new JButton();
        resetBtn.setName("resetBtn");
        
        JToolBar fileBar = new JToolBar();
        JToolBar editBar = new JToolBar();
        JToolBar styleBar = new JToolBar();
        JToolBar alignBar = new JToolBar();
        JToolBar sizeBar = new JToolBar();
        JToolBar colorBar = new JToolBar();
        
        fileBar.add(newBtn);
        fileBar.add(openBtn);
        fileBar.add(saveBtn);
        fileBar.add(closeBtn);
        fileBar.add(printBtn);
        
        editBar.add(undoBtn);
        editBar.add(redoBtn);
        editBar.add(cutBtn);
        editBar.add(copyBtn);
        editBar.add(pasteBtn);
        
        styleBar.add(boldBtn);
        styleBar.add(italicBtn);
        styleBar.add(underlineBtn);
        
        alignBar.add(leftBtn);
        alignBar.add(centerBtn);
        alignBar.add(rightBtn);
        
        sizeBar.add(size10Btn);
        sizeBar.add(size12Btn);
        sizeBar.add(size18Btn);
        
        colorBar.add(redBtn);
        colorBar.add(resetBtn);
        
        JPanel north = new JPanel();
        north.add(fileBar);
        north.add(editBar);
        north.add(styleBar);
        north.add(alignBar);
        north.add(sizeBar);
        north.add(colorBar);
        
        JPanel content = new JPanel();
        content.setLayout(new BorderLayout());
        content.add(north, BorderLayout.NORTH);
        content.add(new JScrollPane(textPane), BorderLayout.CENTER);
        
        KeyboardFocusManager focusManager =
                KeyboardFocusManager.getCurrentKeyboardFocusManager();
            focusManager.addPropertyChangeListener(
                    new PropertyChangeListener() {
            @Override
                public void propertyChange(PropertyChangeEvent e) {
                    String prop = e.getPropertyName();
                    if ("focusOwner".equals(prop)) {
                        Component comp = (Component) e.getNewValue();
                        if (comp != null) {
                            //logger.info("focusOwner = " + comp.getClass().getName());
                            if (comp instanceof JTextPane) {
                                currentPane = (JTextPane) comp;
                            }
                        }
                    }
                }
            }
        );
        ResourceMap resMap = ClientContext.getResourceMap(SimplTextPane.class);
        resMap.injectComponents(content);
        
        ApplicationContext ctx = ClientContext.getApplicationContext();
        ActionMap map = ctx.getActionMap(this);

        newBtn.setAction(map.get("newDocument"));
        openBtn.setAction(map.get("openDocument"));
        closeBtn.setAction(map.get("closeDocument"));
        printBtn.setAction(map.get("myPrint"));
        
        saveAction = map.get("saveDocument");
        saveBtn.setAction(saveAction);
        saveAction.setEnabled(false);
        
        undoAction = map.get("myUndo");
        undoBtn.setAction(undoAction);
        
        redoAction = map.get("myRedo");
        redoBtn.setAction(redoAction);
        
        cutAction = map.get("myCut");
        cutBtn.setAction(cutAction);
        cutAction.setEnabled(false);
        
        copyAction = map.get("myCopy");
        copyBtn.setAction(copyAction);
        copyAction.setEnabled(false);
        
        pasteAction = map.get("myPaste");
        pasteBtn.setAction(pasteAction);
        
        resetBtn.setAction(map.get("reset"));
        
        basePane.addPropertyChangeListener(this);
         
        return content;
        
    }
}
