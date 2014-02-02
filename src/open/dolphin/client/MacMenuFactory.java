package open.dolphin.client;

import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.LinkedHashMap;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import open.dolphin.helper.MenuSupport;
import org.jdesktop.application.Action;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;

/**
 * Menu Factory for Mac. 
 * 
 * @author Minagawa, Kazushi
 */
public class MacMenuFactory extends AbstractMenuFactory {
    
    private MenuSupport main;
    
    private MenuSupport chart;
    
    private JMenuBar menuBar;
    
    private JPanel toolPanel;
    
    private ActionMap actionMap;
    
    
    /** Creates a new instance of ApplicationMenu */
    public MacMenuFactory() {
    }
    
    public void setMenuSupports(MenuSupport main, MenuSupport chart) {
        this.main = main;
        this.chart = chart;
    }
    
    public JMenuBar getMenuBarProduct() {
        return menuBar;
    }
    
    public JPanel getToolPanelProduct() {
        return toolPanel;
    }
    
    public ActionMap getActionMap() {
        return actionMap;
    }
    
    @Action
    public void newKarte() {
        chart.sendToChain("newKarte");
    }
    
    @Action
    public void newDocument() {
        chart.sendToChain("newDocument");
    }

    @Action
    public void openKarte() {
        main.sendToChain("openKarte");
    }
       
    @Action
    public void close() {
        chart.sendToChain("close");
    }
    
    @Action
    public void save() {
        chart.sendToChain("save");
    }
    
    @Action
    public void delete() {
        chart.sendToChain("delete");
    }
        
    @Action
    public void printerSetup() {
        main.sendToChain("printerSetup");
    }
     
    @Action
    public void print() {
       chart.sendToChain("print");
    }
    
    @Action
    public void processExit() {
       main.sendToChain("processExit");
    }
   
    @Action
    public void modifyKarte() {
        chart.sendToChain("modifyKarte");
    }
       
    @Action
    public void undo() {
        chart.sendToChain("undo");
    }
        
    @Action
    public void redo() {
        chart.sendToChain("redo");
    }       
      
    @Action
    public void cut() {
        chart.cut();
    }
        
    @Action
    public void copy() {
        chart.copy();
    }             
       
    @Action
    public void paste() {
        chart.paste();
    }       
        
    @Action
    public void ascending() {
        chart.sendToChain("ascending");
    }
     
    @Action
    public void descending() {
        chart.sendToChain("descending");
    }
       
    @Action
    public void showModified() {
        chart.sendToChain("showModified");
    }
    
    @Action
    public void setKarteEnviroment() {
        main.sendToChain("setKarteEnviroment");
    }
    
    @Action
    public void insertDisease() {
    }
       
    @Action
    public void insertText() {
    }
       
    @Action
    public void insertSchema() {
    }
    
    @Action
    public void insertStamp() {
    }
    
    @Action
    public void selectInsurance() {
    }
    
    @Action
    public void size() {
    }
    
    @Action
    public void fontLarger() {
        chart.sendToChain("fontLarger");
    }
    
    @Action
    public void fontSmaller() {
        chart.sendToChain("fontSmaller");
    }
    
    @Action
    public void fontStandard() {
        chart.sendToChain("fontStandard");
    }
    
    @Action
    public void style() {
    }
    
    @Action
    public void fontBold() {
        chart.sendToChain("fontBold");
    }
    
    @Action
    public void fontItalic() {
        chart.sendToChain("fontItalic");
    }
    
    @Action
    public void fontUnderline() {
        chart.sendToChain("fontUnderline");
    }
    
    @Action
    public void justify() {
    }
    
    @Action
    public void leftJustify() {
        chart.sendToChain("leftJustify");
    }
    
    @Action
    public void centerJustify() {
        chart.sendToChain("centerJustify");
    }
    
    @Action
    public void rightJustify() {
        chart.sendToChain("rightJustify");
    }
    
    @Action
    public void color() {
    }
    
    @Action
    public void fontRed() {
        chart.sendToChain("fontRed");
    }
    
    @Action
    public void fontOrange() {
        chart.sendToChain("fontOrange");
    }
    
    @Action
    public void fontYellow() {
        chart.sendToChain("fontYellow");
    }
    
    @Action
    public void fontGreen() {
        chart.sendToChain("fontGreen");
    }
    
    @Action
    public void fontBlue() {
        chart.sendToChain("fontBlue");
    }
    
    @Action
    public void fontPurple() {
        chart.sendToChain("fontPurple");
    }
    
    @Action
    public void fontGray() {
        chart.sendToChain("fontGray");
    }
    
    @Action
    public void fontBlack() {
        chart.sendToChain("fontBlack");
    }
    
    @Action
    public void resetStyle() {
        chart.sendToChain("resetStyle");
    }
    
    @Action
    public void showStampBox() {
        main.sendToChain("showStampBox");
    }
      
    @Action
    public void showSchemaBox() {
        main.sendToChain("showSchemaBox");
    }
        
    @Action
    public void changePassword() {
        main.sendToChain("changePassword");
    }
        
    @Action
    public void addUser() {
        main.sendToChain("addUser");
    }
        
    @Action
    public void update1() {
        main.sendToChain("update");
    }
    
    @Action
    public void browseDolphinSupport() {
        main.sendToChain("browseDolphinSupport");
    }
     
    @Action
    public void browseDolphinProject() {
        main.sendToChain("browseDolphinProject");
    }
        
    @Action
    public void browseMedXml() {
        main.sendToChain("browseMedXml");
    }
    
    @Action
    public void showAbout() {
        main.sendToChain("showAbout");
    }
    
    public void build(JMenuBar menuBar) {
        
        this.menuBar = menuBar;
        ApplicationContext ctx = ClientContext.getApplicationContext();
        ResourceMap resMap = ctx.getResourceMap(MacMenuFactory.class);
        actionMap = ctx.getActionMap(this);
        
        // ToolBar
        JToolBar fileBar = null;
        JToolBar editBar = null;
        if (chart != null) {
            fileBar = new JToolBar();
            fileBar.setName("fileBar");
            editBar = new JToolBar();
            editBar.setName("editBar");
            toolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            toolPanel.add(fileBar);
            toolPanel.add(editBar);
        }
        
        // File
        JMenu file = new JMenu();
        file.setName("fileMenu");
        
        // êVãKÉJÉãÉe
        JMenuItem newKarte = new JMenuItem();
        newKarte.setName("newKarte");
        newKarte.setAction(actionMap.get("newKarte"));
        setAccelerator(newKarte, KeyEvent.VK_N);
        file.add(newKarte);
        if (chart != null) {
            JButton newKarteBtn = new JButton();
            newKarteBtn.setAction(actionMap.get("newKarte"));
            newKarteBtn.setText(null);
            fileBar.add(newKarteBtn);
        }
        
        // êVãKï∂èë
        JMenuItem newDocument = new JMenuItem();
        newDocument.setName("newDocument");
        newDocument.setAction(actionMap.get("newDocument"));
        file.add(newDocument);
        if (chart != null) {
            JButton newDocBtn = new JButton();
            newDocBtn.setAction(actionMap.get("newDocument"));
            newDocBtn.setText(null);
            fileBar.add(newDocBtn);
        }
        
        // äJÇ≠
        JMenuItem openKarte = new JMenuItem();
        openKarte.setName("openKarte");
        openKarte.setAction(actionMap.get("openKarte"));
        setAccelerator(openKarte, KeyEvent.VK_O);
        file.add(openKarte);
        
        file.add(new JSeparator());
        
        // ï¬Ç∂ÇÈ
        JMenuItem close = new JMenuItem();
        close.setName("close");
        close.setAction(actionMap.get("close"));
        setAccelerator(close, KeyEvent.VK_W);
        file.add(close);
        
        // ï€ë∂
        JMenuItem save = new JMenuItem();
        save.setName("save");
        save.setAction(actionMap.get("save"));
        setAccelerator(save, KeyEvent.VK_S);
        file.add(save);
        if (chart != null) {
            JButton saveBtn = new JButton();
            saveBtn.setAction(actionMap.get("save"));
            saveBtn.setText(null);
            fileBar.add(saveBtn);
        }
        
        file.add(new JSeparator());
        
        // çÌèú
        JMenuItem delete = new JMenuItem();
        delete.setName("delete");
        delete.setAction(actionMap.get("delete"));
        file.add(delete);
        
        file.add(new JSeparator());
        
        // àÛç¸ê›íË
        JMenuItem printerSetup = new JMenuItem();
        printerSetup.setName("printerSetup");
        printerSetup.setAction(actionMap.get("printerSetup"));
        file.add(printerSetup);
        
        // àÛç¸
        JMenuItem print = new JMenuItem();
        print.setName("print");
        print.setAction(actionMap.get("print"));
        setAccelerator(print, KeyEvent.VK_P);
        file.add(print);
        if (chart != null) {
            JButton printBtn = new JButton();
            printBtn.setAction(actionMap.get("print"));
            printBtn.setText(null);
            fileBar.add(printBtn);
        }
        
        /******************************************************/
        
        // Edit
        JMenu edit = new JMenu();
        edit.setName("editMenu");
        
        // èCê≥
        JMenuItem modifyKarte = new JMenuItem();
        modifyKarte.setName("modifyKarte");
        modifyKarte.setAction(actionMap.get("modifyKarte"));
        setAccelerator(modifyKarte, KeyEvent.VK_M);
        edit.add(modifyKarte);
        if (chart != null) {
            JButton modifyKarteBtn = new JButton();
            modifyKarteBtn.setAction(actionMap.get("modifyKarte"));
            modifyKarteBtn.setText(null);
            editBar.add(modifyKarteBtn);
        }
        
        edit.add(new JSeparator());
        
        // Undo
        JMenuItem undo = new JMenuItem();
        undo.setName("undo");
        undo.setAction(actionMap.get("undo"));
        setAccelerator(undo, KeyEvent.VK_Z);
        edit.add(undo);
        if (chart != null) {
            JButton undoBtn = new JButton();
            undoBtn.setAction(actionMap.get("undo"));
            undoBtn.setText(null);
            editBar.add(undoBtn);
        }
        
        // Redo
        JMenuItem redo = new JMenuItem();
        redo.setName("redo");
        redo.setAction(actionMap.get("redo"));
        setAccelerator(redo, KeyEvent.VK_Z, true);
        edit.add(redo);
        if (chart != null) {
            JButton redoBtn = new JButton();
            redoBtn.setAction(actionMap.get("redo"));
            redoBtn.setText(null);
            editBar.add(redoBtn);
        }
        
        edit.add(new JSeparator());
        
        // Cut
        JMenuItem cut = new JMenuItem();
        cut.setName("cut");
        cut.setAction(actionMap.get("cut"));
        setAccelerator(cut, KeyEvent.VK_X);
        edit.add(cut);
        if (chart != null) {
            JButton cutBtn = new JButton();
            cutBtn.setAction(actionMap.get("cut"));
            cutBtn.setText(null);
            editBar.add(cutBtn);
        }
        
        // Copy
        JMenuItem copy = new JMenuItem();
        copy.setName("copy");
        copy.setAction(actionMap.get("copy"));
        setAccelerator(copy, KeyEvent.VK_C);
        edit.add(copy);
        if (chart != null) {
            JButton copyBtn = new JButton();
            copyBtn.setAction(actionMap.get("copy"));
            copyBtn.setText(null);
            editBar.add(copyBtn);
        }
        
        // Paste
        JMenuItem paste = new JMenuItem();
        paste.setName("paste");
        paste.setAction(actionMap.get("paste"));
        setAccelerator(paste, KeyEvent.VK_V);
        edit.add(paste);
        if (chart != null) {
            JButton pasteBtn = new JButton();
            pasteBtn.setAction(actionMap.get("paste"));
            pasteBtn.setText(null);
            editBar.add(pasteBtn);
        }
        
        /******************************************************/
        
        // Karte
        JMenu karte = new JMenu();
        karte.setName("karteMenu");
        
        // è∏èá
        JRadioButtonMenuItem ascending = new JRadioButtonMenuItem();
        ascending.setName("ascending");
        ascending.setAction(actionMap.get("ascending"));
        actionMap.get("ascending").putValue("menuItem",ascending);
        karte.add(ascending);
        
        // ç~èá
        JRadioButtonMenuItem descending = new JRadioButtonMenuItem();
        descending.setName("descending");
        descending.setAction(actionMap.get("descending"));
        actionMap.get("descending").putValue("menuItem",descending);
        karte.add(descending);
        
        // RadiButtonGroup
        ButtonGroup bg = new ButtonGroup();
        bg.add(ascending);
        bg.add(descending);
        
        
        // èCê≥óöóï\é¶ 
        JCheckBoxMenuItem showModified = new JCheckBoxMenuItem();
        showModified.setName("showModified");
        showModified.setAction(actionMap.get("showModified"));
        actionMap.get("showModified").putValue("menuItem",showModified);
        karte.add(showModified);
        
        // ä¬ã´ê›íË 
        JMenuItem setKarteEnviroment = new JMenuItem();
        setKarteEnviroment.setName("setKarteEnviroment");
        setKarteEnviroment.setAction(actionMap.get("setKarteEnviroment"));
        setAccelerator(setKarteEnviroment, KeyEvent.VK_E);
        karte.add(setKarteEnviroment);
        
        /******************************************************/
        
        // Insert
        JMenu insert = new JMenu();
        insert.setName("insertMenu");
        if (chart != null) {
            insert.addMenuListener(chart);
        }
        
        JMenu insertDisease = new JMenu();
        insertDisease.setName("insertDisease");
        insertDisease.setAction(actionMap.get("insertDisease"));
        insert.add(insertDisease);
        
        JMenu insertText = new JMenu();
        insertText.setName("insertText");
        insertText.setAction(actionMap.get("insertText"));
        insert.add(insertText);
        
        JMenu insertSchema = new JMenu();
        insertSchema.setName("insertSchema");
        insertSchema.setAction(actionMap.get("insertSchema"));
        insert.add(insertSchema);
        
        JMenu insertStamp = new JMenu();
        insertStamp.setName("insertStamp");
        insertStamp.setAction(actionMap.get("insertStamp"));
        insert.add(insertStamp);
        
        /******************************************************/
        
        // Text
        JMenu text = new JMenu();
        text.setName("textMenu");
        if (chart != null) {
            text.addMenuListener(chart);
        }
        
        //// size ////
        JMenu size = new JMenu();
        size.setName("size");
        size.setAction(actionMap.get("size"));
        text.add(size);
        
        JMenuItem fontLarger = new JMenuItem();
        fontLarger.setName("fontLarger");
        fontLarger.setAction(actionMap.get("fontLarger"));
        //setAccelerator(fontLarger, KeyEvent.VK_PLUS, true);
        size.add(fontLarger);
        
        JMenuItem fontSmaller = new JMenuItem();
        fontSmaller.setName("fontSmaller");
        fontSmaller.setAction(actionMap.get("fontSmaller"));
        //setAccelerator(fontSmaller, KeyEvent.VK_MINUS);
        size.add(fontSmaller);
        
        JMenuItem fontStandard = new JMenuItem();
        fontStandard.setName("fontStandard");
        fontStandard.setAction(actionMap.get("fontStandard"));
        //setAccelerator(fontStandard, KeyEvent.VK_NUMBER_SIGN, true);
        size.add(fontStandard);  
        
        //// style ////
        JMenu style = new JMenu();
        style.setName("style");
        style.setAction(actionMap.get("style"));
        text.add(style);
        
        JMenuItem fontBold = new JMenuItem();
        fontBold.setName("fontBold");
        fontBold.setAction(actionMap.get("fontBold"));
        setAccelerator(fontBold, KeyEvent.VK_B);
        style.add(fontBold);
        
        JMenuItem fontItalic = new JMenuItem();
        fontItalic.setName("fontItalic");
        fontItalic.setAction(actionMap.get("fontItalic"));
        setAccelerator(fontItalic, KeyEvent.VK_I);
        style.add(fontItalic);
        
        JMenuItem fontUnderline = new JMenuItem();
        fontUnderline.setName("fontUnderline");
        fontUnderline.setAction(actionMap.get("fontUnderline"));
        setAccelerator(fontUnderline, KeyEvent.VK_U);
        style.add(fontUnderline);

        //// justify ////
        JMenu justify = new JMenu();
        justify.setName("justify");
        justify.setAction(actionMap.get("justify"));
        text.add(justify);
        
        JMenuItem leftJustify = new JMenuItem();
        leftJustify.setName("leftJustify");
        leftJustify.setAction(actionMap.get("leftJustify"));
        //setAccelerator(leftJustify, KeyEvent.VK_OPEN_BRACKET);
        justify.add(leftJustify);
        
        JMenuItem centerJustify = new JMenuItem();
        centerJustify.setName("centerJustify");
        centerJustify.setAction(actionMap.get("centerJustify"));
        //setAccelerator(centerJustify, KeyEvent.VK_CIRCUMFLEX);
        justify.add(centerJustify);
        
        JMenuItem rightJustify = new JMenuItem();
        rightJustify.setName("rightJustify");
        rightJustify.setAction(actionMap.get("rightJustify"));
        //setAccelerator(rightJustify, KeyEvent.VK_CLOSE_BRACKET);
        justify.add(rightJustify);
        
        //// Color ////
        JMenu color = new JMenu();
        color.setName("color");
        color.setAction(actionMap.get("color"));
        text.add(color);
        
        JMenuItem fontRed = new JMenuItem();
        fontRed.setName("fontRed");
        fontRed.setAction(actionMap.get("fontRed"));
        color.add(fontRed);
        
        JMenuItem fontOrange = new JMenuItem();
        fontOrange.setName("fontOrange");
        fontOrange.setAction(actionMap.get("fontOrange"));
        color.add(fontOrange);
        
        JMenuItem fontYellow = new JMenuItem();
        fontYellow.setName("fontYellow");
        fontYellow.setAction(actionMap.get("fontYellow"));
        color.add(fontYellow);
        
        JMenuItem fontGreen = new JMenuItem();
        fontGreen.setName("fontGreen");
        fontGreen.setAction(actionMap.get("fontGreen"));
        color.add(fontGreen);
        
        JMenuItem fontBlue = new JMenuItem();
        fontBlue.setName("fontBlue");
        fontBlue.setAction(actionMap.get("fontBlue"));
        color.add(fontBlue);
        
        JMenuItem fontPurple = new JMenuItem();
        fontPurple.setName("fontPurple");
        fontPurple.setAction(actionMap.get("fontPurple"));
        color.add(fontPurple);
        
        JMenuItem fontGray = new JMenuItem();
        fontGray.setName("fontGray");
        fontGray.setAction(actionMap.get("fontGray"));
        color.add(fontGray);
        
        JMenuItem fontBlack = new JMenuItem();
        fontBlack.setName("fontBlack");
        fontBlack.setAction(actionMap.get("fontBlack"));
        color.add(fontBlack);
        
        /******************************************************/
        
        // Tool
        JMenu tool = new JMenu();
        tool.setName("toolMenu");
        
        JMenuItem showStampBox = new JMenuItem();
        showStampBox.setName("showStampBox");
        showStampBox.setAction(actionMap.get("showStampBox"));
        tool.add(showStampBox);
        
        JMenuItem showSchemaBox = new JMenuItem();
        showSchemaBox.setName("showSchemaBox");
        showSchemaBox.setAction(actionMap.get("showSchemaBox"));
        tool.add(showSchemaBox);
        
        tool.add(new JSeparator());
        
        JMenuItem changePassword = new JMenuItem();
        changePassword.setName("changePassword");
        changePassword.setAction(actionMap.get("changePassword"));
        tool.add(changePassword);
        
        JMenuItem addUser = new JMenuItem();
        addUser.setName("addUser");
        addUser.setAction(actionMap.get("addUser"));
        tool.add(addUser);
        
        tool.add(new JSeparator());
        
        JMenuItem update1 = new JMenuItem();
        update1.setName("update1");
        update1.setAction(actionMap.get("update1"));
        tool.add(update1);

        LinkedHashMap<String, String> toolProviders = ClientContext.getToolProviders();
        
        if (toolProviders != null && toolProviders.size() > 0) {
            
            tool.add(new JSeparator());
            Iterator<String> iter = toolProviders.keySet().iterator();
            
            while (iter.hasNext()) {
                String cmd = iter.next();
                final String className = toolProviders.get(cmd);
                JMenuItem mItem = new JMenuItem();
                AbstractAction a = new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        main.sendToChain("invokeToolPlugin", className);
                    }
                };
                mItem.setAction(a);
                mItem.setText(cmd);
                tool.add(mItem);
            }
        } 
        
        /******************************************************/
        
        // Help
        JMenu help = new JMenu();
        help.setName("helpMenu");
        
        JMenuItem browseDolphinSupport = new JMenuItem();
        browseDolphinSupport.setName("browseDolphinSupport");
        browseDolphinSupport.setAction(actionMap.get("browseDolphinSupport"));
        help.add(browseDolphinSupport);
        
        JMenuItem browseDolphinProject = new JMenuItem();
        browseDolphinProject.setName("browseDolphinProject");
        browseDolphinProject.setAction(actionMap.get("browseDolphinProject"));
        help.add(browseDolphinProject);
        
        JMenuItem browseMedXml = new JMenuItem();
        browseMedXml.setName("browseMedXml");
        browseMedXml.setAction(actionMap.get("browseMedXml"));
        help.add(browseMedXml);
        
        /******************************************************/
        
        menuBar.add(file,   0);
        menuBar.add(edit,   1);
        menuBar.add(karte,  2);
        menuBar.add(insert, 3);
        menuBar.add(text,   4);
        menuBar.add(tool,   5);
        // 6 = Window
        menuBar.add(help,   7);
        
        /******************************************************/
        
        resMap.injectComponents(menuBar);
    }
    
    private void setAccelerator(JMenuItem item, int key) {
        item.setAccelerator(KeyStroke.getKeyStroke(key, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    }
    
    private void setAccelerator(JMenuItem item, int key, boolean shiftMask) {
        item.setAccelerator(
                        KeyStroke.getKeyStroke(key, (java.awt.event.InputEvent.SHIFT_MASK | (Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()))));
    }
}















