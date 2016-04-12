package open.dolphin.client;

import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.ResourceBundle;
import javax.swing.*;
import open.dolphin.helper.MenuSupport;
import open.dolphin.project.Project;

/**
 * Menu Factory for Mac. 
 * 
 * @author Minagawa, Kazushi.
 * 2015/04/21 ハードコードされたアクション名を GUIConst定数で置き換え
 * 2015/04/21 setName() 廃止 
 */
public class MacMenuFactory extends AbstractMenuFactory {
    
    private MenuSupport main;
    
    private MenuSupport chart;
    
    private JMenuBar menuBar;
    
    private JPanel toolPanel;
    
    private ActionMap actionMap;
    
    private JToolBar toolBar;
    
    
    /** Creates a new instance of ApplicationMenu */
    public MacMenuFactory() {
    }
    
    @Override
    public void setMenuSupports(MenuSupport main, MenuSupport chart) {
        this.main = main;
        this.chart = chart;
    }
    
    @Override
    public JMenuBar getMenuBarProduct() {
        return menuBar;
    }
    
    @Override
    public JPanel getToolPanelProduct() {
        return toolPanel;
    }
    
    @Override
    public ActionMap getActionMap() {
        return actionMap;
    }
    
    @Override
    public JToolBar getToolBar() {
        return toolBar;
    }

    private void storeActions(ActionMap map, ResourceBundle resource) {

        // New Karte
        String text = resource.getString("newKarte.Action.text");
        ImageIcon icon = ClientContext.getImageIconArias("icon_new_karte");
        AbstractAction newKarte = new AbstractAction(text, icon) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain(GUIConst.ACTION_NEW_KARTE);
            }
        };
        map.put(GUIConst.ACTION_NEW_KARTE, newKarte);

        // New Document
        text = resource.getString("newDocument.Action.text");
        icon = ClientContext.getImageIconArias("icon_new_document");       
        AbstractAction newDocument = new AbstractAction(text, icon) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain(GUIConst.ACTION_NEW_DOCUMENT);
            }
        };
        map.put(GUIConst.ACTION_NEW_DOCUMENT, newDocument);

        // Open Karte
        text = resource.getString("openKarte.Action.text");
        AbstractAction openKarte = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                main.sendToChain(GUIConst.ACTION_OPEN_KARTE);
            }
        };
        map.put(GUIConst.ACTION_OPEN_KARTE, openKarte);

        // Close
        text = resource.getString("close.Action.text");
        AbstractAction close = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain(GUIConst.ACTION_CLOSE);
            }
        };
        map.put(GUIConst.ACTION_CLOSE, close);

        // Save
        text = resource.getString("save.Action.text");
        icon = ClientContext.getImageIconArias("icon_save");       
        AbstractAction save = new AbstractAction(text, icon) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain(GUIConst.ACTION_SAVE);
            }
        };
        map.put(GUIConst.ACTION_SAVE, save);

        // Delete
        text = resource.getString("delete.Action.text");
        AbstractAction delete = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain(GUIConst.ACTION_DELETE);
            }
        };
        map.put(GUIConst.ACTION_DELETE, delete);

        // Printer Setup
        text = resource.getString("printerSetup.Action.text");
        AbstractAction printerSetup = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                main.sendToChain(GUIConst.ACTION_PRINTER_SETUP);
            }
        };
        map.put(GUIConst.ACTION_PRINTER_SETUP, printerSetup);

        // Print
        text = resource.getString("print.Action.text");
        icon = ClientContext.getImageIconArias("icon_printer");       
        AbstractAction print = new AbstractAction(text, icon) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain(GUIConst.ACTION_PRINT);
            }
        };
        map.put(GUIConst.ACTION_PRINT, print);

        // Exit
        text = resource.getString("processExit.Action.text");
        AbstractAction processExit = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                main.sendToChain(GUIConst.ACTION_PROCESS_EXIT);
            }
        };
        map.put(GUIConst.ACTION_PROCESS_EXIT, processExit);

        // Modify
        text = resource.getString("modifyKarte.Action.text");
        icon = ClientContext.getImageIconArias("icon_edit_karte_document");       
        AbstractAction modifyKarte = new AbstractAction(text, icon) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain(GUIConst.ACTION_MODIFY_KARTE);
            }
        };
        map.put(GUIConst.ACTION_MODIFY_KARTE, modifyKarte);
        
//s.oh^ 2014/04/03 文書の複製
        //text = "複製";
        text = resource.getString("copyDocument.Action.text");
        icon = ClientContext.getImageIconArias("icon_edit_karte_document");
        AbstractAction copyDocument = new AbstractAction(text, icon) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain("copyDocument");
            }
        };
        map.put("copyDocument", copyDocument);
//s.oh$

        // Undo
        text = resource.getString("undo.Action.text");
        icon = ClientContext.getImageIconArias("icon_undo");       
        AbstractAction undo = new AbstractAction(text, icon) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain(GUIConst.ACTION_UNDO);
            }
        };
        map.put(GUIConst.ACTION_UNDO, undo);

        // Redo
        text = resource.getString("redo.Action.text");
        icon = ClientContext.getImageIconArias("icon_redo");        
        AbstractAction redo = new AbstractAction(text, icon) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain(GUIConst.ACTION_REDO);
            }
        };
        map.put(GUIConst.ACTION_REDO, redo);

        // Cut
        text = resource.getString("cut.Action.text");
        icon = ClientContext.getImageIconArias("icon_cut");        
        AbstractAction cut = new AbstractAction(text, icon) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.cut();
            }
        };
        map.put(GUIConst.ACTION_CUT, cut);

        // Copy
        text = resource.getString("copy.Action.text");
        icon = ClientContext.getImageIconArias("icon_copy");       
        AbstractAction copy = new AbstractAction(text, icon) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.copy();
            }
        };
        map.put(GUIConst.ACTION_COPY, copy);

        // Paste
        text = resource.getString("paste.Action.text");
        icon = ClientContext.getImageIconArias("icon_paste");       
        AbstractAction paste = new AbstractAction(text, icon) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.paste();
            }
        };
        map.put(GUIConst.ACTION_PASTE, paste);

        // 昇順
        text = resource.getString("ascending.Action.text");
        AbstractAction ascending = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain(GUIConst.ACTION_ASCENDING);
            }
        };
        map.put(GUIConst.ACTION_ASCENDING, ascending);

        // 降順
        text = resource.getString("descending.Action.text");
        AbstractAction descending = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain(GUIConst.ACTION_DESCENDING);
            }
        };
        map.put(GUIConst.ACTION_DESCENDING, descending);

        // 修正履歴表示
        text = resource.getString("showModified.Action.text");
        AbstractAction showModified = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain(GUIConst.ACTION_SHOW_MODIFIED);
            }
        };
        map.put(GUIConst.ACTION_SHOW_MODIFIED, showModified);

        // 環境設定
        text = resource.getString("setKarteEnviroment.Action.text");
        AbstractAction setKarteEnviroment = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                main.sendToChain(GUIConst.ACTION_SET_KARTE_ENVIROMENT);
            }
        };
        map.put(GUIConst.ACTION_SET_KARTE_ENVIROMENT, setKarteEnviroment);
        
        // 保険選択
        AbstractAction selectInsurance = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
            }
        };
        map.put(GUIConst.ACTION_SELECT_INSURANCE, selectInsurance);
        
        // 処方日数一括変更
        text = resource.getString("changeNumOfDatesAll.Action.text");
        AbstractAction changeNumOfDatesAll = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain(GUIConst.ACTION_CHANGE_NUM_OF_DATES_ALL);
            }
        };
        map.put(GUIConst.ACTION_CHANGE_NUM_OF_DATES_ALL, changeNumOfDatesAll);
        
        // 処方箋印刷
        text = resource.getString("createPrescription.Action.text");
        AbstractAction createPrescription = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain("createPrescription");
            }
        };
        map.put("createPrescription", createPrescription);

        // Send Claim 元町皮ふ科
        text = resource.getString("sendClaim.Action.text");
        AbstractAction sendClaim = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain(GUIConst.ACTION_SEND_CLAIM);
            }
        };
        map.put(GUIConst.ACTION_SEND_CLAIM, sendClaim);

        // 併用禁忌チェック
        text = resource.getString("checkInteraction.Action.text");
        AbstractAction checkInteraction = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain(GUIConst.ACTION_CHECK_INTERACTION);
            }
        };
        map.put(GUIConst.ACTION_CHECK_INTERACTION, checkInteraction);

        // 挿入　病名
        text = resource.getString("insertDisease.Action.text");
        AbstractAction insertDisease = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
            }
        };
        map.put(GUIConst.ACTION_INSERT_DISEASE, insertDisease);

        // 挿入　テキスト
        text = resource.getString("insertText.Action.text");
        AbstractAction insertText = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
            }
        };
        map.put(GUIConst.ACTION_INSERT_TEXT, insertText);

        // 挿入　シェーマ
        text = resource.getString("insertSchema.Action.text");
        AbstractAction insertSchema = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
            }
        };
        map.put(GUIConst.ACTION_INSERT_SCHEMA, insertSchema);
        
        // 添付/挿入
        text = resource.getString("attachment.Action.text");
        AbstractAction attachment = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain(GUIConst.ACTION_ATTACHMENT);
            }
        };
        map.put(GUIConst.ACTION_ATTACHMENT, attachment);

        // 挿入　スタンプ
        text = resource.getString("insertStamp.Action.text");
        AbstractAction insertStamp = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
            }
        };
        map.put(GUIConst.ACTION_INSERT_STAMP, insertStamp);

        // Size
        text = resource.getString("size.text");
        AbstractAction size = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
            }
        };
        map.put(GUIConst.ACTION_SIZE, size);

        // 大きく
        text = resource.getString("fontLarger.Action.text");
        AbstractAction fontLarger = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain(GUIConst.ACTION_FONT_LARGER);
            }
        };
        map.put(GUIConst.ACTION_FONT_LARGER, fontLarger);

        // 小さく
        text = resource.getString("fontSmaller.Action.text");
        AbstractAction fontSmaller = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain(GUIConst.ACTION_FONT_SMALLER);
            }
        };
        map.put(GUIConst.ACTION_FONT_SMALLER, fontSmaller);

        // 標準
        text = resource.getString("fontStandard.Action.text");
        AbstractAction fontStandard = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain(GUIConst.ACTION_FONT_STANDARD);
            }
        };
        map.put(GUIConst.ACTION_FONT_STANDARD, fontStandard);

        // スタイル
        text = resource.getString("style.text");
        AbstractAction style = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
            }
        };
        map.put(GUIConst.ACTION_STYLE, style);

        // ボールド
        text = resource.getString("fontBold.Action.text");
        AbstractAction fontBold = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain(GUIConst.ACTION_FONT_BOLD);
            }
        };
        map.put(GUIConst.ACTION_FONT_BOLD, fontBold);

        // イタリック
        text = resource.getString("fontItalic.Action.text");
        AbstractAction fontItalic = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain(GUIConst.ACTION_FONT_ITALIC);
            }
        };
        map.put(GUIConst.ACTION_FONT_ITALIC, fontItalic);

        // 下線
        text = resource.getString("fontUnderline.Action.text");
        AbstractAction fontUnderline = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain(GUIConst.ACTION_FONT_UNDERLINE);
            }
        };
        map.put(GUIConst.ACTION_FONT_UNDERLINE, fontUnderline);

        // Justify
        text = resource.getString("justify.text");
        AbstractAction justify = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
            }
        };
        map.put(GUIConst.ACTION_JUSTIFY, justify);

        // 左揃え
        text = resource.getString("leftJustify.Action.text");
        AbstractAction leftJustify = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain(GUIConst.ACTION_LEFT_JUSTIFY);
            }
        };
        map.put(GUIConst.ACTION_LEFT_JUSTIFY, leftJustify);

        // 中央
        text = resource.getString("centerJustify.Action.text");
        AbstractAction centerJustify = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain(GUIConst.ACTION_CENTER_JUSTIFY);
            }
        };
        map.put(GUIConst.ACTION_CENTER_JUSTIFY, centerJustify);

        // 右よせ
        text = resource.getString("rightJustify.Action.text");
        AbstractAction rightJustify = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain(GUIConst.ACTION_RIGHT_JUSTIFY);
            }
        };
        map.put(GUIConst.ACTION_RIGHT_JUSTIFY, rightJustify);

        // カラー
        text = resource.getString("color.text");
        AbstractAction color = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
            }
        };
        map.put(GUIConst.ACTION_COLOR, color);

        // 赤
        text = resource.getString("fontRed.Action.text");
        AbstractAction fontRed = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain(GUIConst.ACTION_FONT_RED);
            }
        };
        map.put(GUIConst.ACTION_FONT_RED, fontRed);

        // オレンジ
        text = resource.getString("fontOrange.Action.text");
        AbstractAction fontOrange = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain(GUIConst.ACTION_FONT_ORANGE);
            }
        };
        map.put(GUIConst.ACTION_FONT_ORANGE, fontOrange);

        // 黄色
        text = resource.getString("fontYellow.Action.text");
        AbstractAction fontYellow = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain(GUIConst.ACTION_FONT_YELLOW);
            }
        };
        map.put(GUIConst.ACTION_FONT_YELLOW, fontYellow);

        // 緑
        text = resource.getString("fontGreen.Action.text");
        AbstractAction fontGreen = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain(GUIConst.ACTION_FONT_GREEN);
            }
        };
        map.put(GUIConst.ACTION_FONT_GREEN, fontGreen);

        // 青
        text = resource.getString("fontBlue.Action.text");
        AbstractAction fontBlue = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain(GUIConst.ACTION_FONT_BLUE);
            }
        };
        map.put(GUIConst.ACTION_FONT_BLUE, fontBlue);

        // 紫
        text = resource.getString("fontPurple.Action.text");
        AbstractAction fontPurple = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain(GUIConst.ACTION_FONT_PURPLE);
            }
        };
        map.put(GUIConst.ACTION_FONT_PURPLE, fontPurple);

        // グレー
        text = resource.getString("fontGray.Action.text");
        AbstractAction fontGray = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain(GUIConst.ACTION_FONT_GRAY);
            }
        };
        map.put(GUIConst.ACTION_FONT_GRAY, fontGray);

        // ブラック
        text = resource.getString("fontBlack.Action.text");
        AbstractAction fontBlack = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain(GUIConst.ACTION_FONT_BLACK);
            }
        };
        map.put(GUIConst.ACTION_FONT_BLACK, fontBlack);

        // リセット　未使用？
        AbstractAction resetStyle = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain("resetStyle");
            }
        };
        map.put("resetStyle", resetStyle);

        // StampBox
        text = resource.getString("showStampBox.Action.text");
        AbstractAction showStampBox = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                main.sendToChain(GUIConst.ACTION_SHOW_STAMPBOX);
            }
        };
        map.put(GUIConst.ACTION_SHOW_STAMPBOX, showStampBox);

        // シェーマBox
        text = resource.getString("showSchemaBox.Action.text");
        AbstractAction showSchemaBox = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                main.sendToChain(GUIConst.ACTION_SHOW_SCHEMABOX);
            }
        };
        map.put(GUIConst.ACTION_SHOW_SCHEMABOX, showSchemaBox);

        // パスワード変更
        text = resource.getString("changePassword.Action.text");
        AbstractAction changePassword = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                main.sendToChain(GUIConst.ACTION_CHANGE_PASSWORD);
            }
        };
        map.put(GUIConst.ACTION_CHANGE_PASSWORD, changePassword);
        
        // 施設情報編集
        text = resource.getString("editFacilityInfo.Action.text");
        AbstractAction editFacilityInfo = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                main.sendToChain(GUIConst.ACTION_EDIT_FACILITY_INFO);
            }
        };
        map.put(GUIConst.ACTION_EDIT_FACILITY_INFO, editFacilityInfo);

        // ユーザー追加
        text = resource.getString("addUser.Action.text");
        AbstractAction addUser = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                main.sendToChain(GUIConst.ACTION_ADD_USER);
            }
        };
        map.put(GUIConst.ACTION_ADD_USER, addUser);
        
//s.oh^ 2014/07/08 クラウド0対応
//minagawa^ 統計情報
        text = resource.getString("activities.Action.text");
        AbstractAction fetchActivities = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                main.sendToChain(GUIConst.ACTION_FETCH_ACTIVITIES);
            }
        };
        map.put(GUIConst.ACTION_FETCH_ACTIVITIES, fetchActivities);
//minagawa$        
//s.oh$
        
        // 医療機関コード取得
        text = resource.getString("fetchFacilityCode.Action.text");
        AbstractAction fetchFacilityCode = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                main.sendToChain(GUIConst.ACTION_FETCH_FACILITY_CODE);
            }
        };
        map.put(GUIConst.ACTION_FETCH_FACILITY_CODE, fetchFacilityCode);

        // Support
        text = resource.getString("browseDolphinSupport.Action.text");
        AbstractAction browseDolphinSupport = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                main.sendToChain(GUIConst.ACTION_BROWS_DOLPHIN);
            }
        };
        map.put(GUIConst.ACTION_BROWS_DOLPHIN, browseDolphinSupport);

        // Dolphin
        text = resource.getString("browseDolphinProject.Action.text");
        AbstractAction browseDolphinProject = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                main.sendToChain(GUIConst.ACTION_BROWS_DOLPHIN_PROJECT);
            }
        };
        map.put(GUIConst.ACTION_BROWS_DOLPHIN_PROJECT, browseDolphinProject);

        // MedXML
        text = resource.getString("browseMedXml.Action.text");
        AbstractAction browseMedXml = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                main.sendToChain(GUIConst.ACTION_BROWS_MEDXML);
            }
        };
        map.put(GUIConst.ACTION_BROWS_MEDXML, browseMedXml);

        // About
        text = resource.getString("showAbout.Action.text");
        AbstractAction showAbout = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                main.sendToChain(GUIConst.ACTION_SHOW_ABOUT);
            }
        };
        map.put(GUIConst.ACTION_SHOW_ABOUT, showAbout);
        
//s.oh^ テキストの挿入 2013/08/12
        text = resource.getString("soapane.Action.text");
        icon = ClientContext.getImageIconArias(resource.getString("soapane.Action.icon"));
        AbstractAction insertSOAText = new AbstractAction(text, icon) {
            @Override
            public void actionPerformed(ActionEvent e) {
                chart.sendToChain("insertSOAText");
            }
        };
        map.put("insertSOAText", insertSOAText);
        
        text = resource.getString("ppane.Action.text");
        icon = ClientContext.getImageIconArias(resource.getString("ppane.Action.icon"));
        AbstractAction insertPText = new AbstractAction(text, icon) {
            @Override
            public void actionPerformed(ActionEvent e) {
                chart.sendToChain("insertPText");
            }
        };
        map.put("insertPText", insertPText);
//s.oh$
        
////s.oh^ 他プロセス連携(アイコン) 2014/05/09
////minagawa^ Icon Server         
//        //icon = ClientContext.getImageIcon(resource.getString("ppane.Action.icon"));
//        icon = ClientContext.getImageIconArias("icon_other_process");
////minagawa$
//        AbstractAction otherProcessIcon1Link = new AbstractAction("", icon) {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                chart.sendToChain("otherProcessIcon1Link");
//            }
//        };
//        map.put("otherProcessIcon1Link", otherProcessIcon1Link);
//        
//        AbstractAction otherProcessIcon2Link = new AbstractAction("", icon) {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                chart.sendToChain("otherProcessIcon2Link");
//            }
//        };
//        map.put("otherProcessIcon2Link", otherProcessIcon2Link);
//        
//
//        AbstractAction otherProcessIcon3Link = new AbstractAction("", icon) {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                chart.sendToChain("otherProcessIcon3Link");
//            }
//        };
//        map.put("otherProcessIcon3Link", otherProcessIcon3Link);
////s.oh$
        
//minagawa^ delete
////s.oh^ 2014/08/19 受付バーコード対応
//        text = resource.getString("receipt.barcode.Action.text");
//        AbstractAction receiptBarcode = new AbstractAction(text) {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                main.sendToChain("receiptBarcode");
//            }
//        };
//        map.put("receiptBarcode", receiptBarcode);
////s.oh$
        
//s.oh^ 2014/07/22 一括カルテPDF出力
        text = resource.getString("allkartepdf.Action.text");
        AbstractAction outputAllKartePdf = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                main.sendToChain("outputAllKartePdf");
            }
        };
        map.put("outputAllKartePdf", outputAllKartePdf);
//s.oh$
    }
    
    @Override
    public void build(JMenuBar menuBar) {
        
        this.menuBar = menuBar;
        ResourceBundle resource = ClientContext.getMyBundle(MacMenuFactory.class);
        actionMap = new ActionMap();
        storeActions(actionMap, resource);
        
        // ToolBar
        toolBar = new JToolBar();
        toolBar.setBorderPainted(false);
        toolBar.addSeparator();
        
        if (chart != null) {
//s.oh^ 2014/09/19 ツールバーの表示改善
            //toolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            toolPanel = new JPanel();
            toolPanel.setLayout(new BoxLayout(toolPanel, BoxLayout.X_AXIS));
//s.oh$
            toolPanel.add(toolBar);
        }

        //******************************************************
        // File Menu
        //******************************************************
        JMenu file = new JMenu();

        // 新規カルテ
        JMenuItem newKarte = new JMenuItem();
        newKarte.setAction(actionMap.get(GUIConst.ACTION_NEW_KARTE));
        setAccelerator(newKarte, KeyEvent.VK_N);
        newKarte.setIcon(null);
        file.add(newKarte);
        if (chart != null) {
            JButton newKarteBtn = new JButton();
            newKarteBtn.setAction(actionMap.get(GUIConst.ACTION_NEW_KARTE));
            newKarteBtn.setText(null);
            newKarteBtn.setToolTipText(resource.getString("newKarte.Action.toolTipText"));
            newKarteBtn.setMargin(new Insets(3,3,3,3));
            newKarteBtn.setFocusable(false);
            newKarteBtn.setBorderPainted(false);
            toolBar.add(newKarteBtn);
        }

        // 新規文書
        JMenuItem newDocument = new JMenuItem();
        newDocument.setAction(actionMap.get(GUIConst.ACTION_NEW_DOCUMENT));
        newDocument.setIcon(null);
        file.add(newDocument);
        if (chart != null) {
            JButton newDocBtn = new JButton();
            newDocBtn.setAction(actionMap.get(GUIConst.ACTION_NEW_DOCUMENT));
            newDocBtn.setText(null);
            newDocBtn.setToolTipText(resource.getString("newDocument.Action.toolTipText"));
            newDocBtn.setMargin(new Insets(3,3,3,3));
            newDocBtn.setFocusable(false);
            newDocBtn.setBorderPainted(false);
            toolBar.add(newDocBtn);
        }

        // 開く
        JMenuItem openKarte = new JMenuItem();
        openKarte.setAction(actionMap.get(GUIConst.ACTION_OPEN_KARTE));
        setAccelerator(openKarte, KeyEvent.VK_O);
        file.add(openKarte);
        
        file.add(new JSeparator());

        // 閉じる
        JMenuItem close = new JMenuItem();
        close.setAction(actionMap.get(GUIConst.ACTION_CLOSE));
        setAccelerator(close, KeyEvent.VK_W);
        file.add(close);

        // 保存
        JMenuItem save = new JMenuItem();
        save.setAction(actionMap.get(GUIConst.ACTION_SAVE));
        setAccelerator(save, KeyEvent.VK_S);
        save.setIcon(null);
        file.add(save);
        if (chart != null) {
            JButton saveBtn = new JButton();
            saveBtn.setAction(actionMap.get(GUIConst.ACTION_SAVE));
            saveBtn.setText(null);
            saveBtn.setToolTipText(resource.getString("save.Action.toolTipText"));
            saveBtn.setMargin(new Insets(3,3,3,3));
            saveBtn.setFocusable(false);
            saveBtn.setBorderPainted(false);
            toolBar.add(saveBtn);
        }
        
        file.add(new JSeparator());

        // 削除
//s.oh^ 2013/09/05
        if(Project.getBoolean("delete.karte.enable", true)) {
//s.oh$
            JMenuItem delete = new JMenuItem();
            delete.setAction(actionMap.get(GUIConst.ACTION_DELETE));
            file.add(delete);

            file.add(new JSeparator());
        }

        // 印刷設定
        JMenuItem printerSetup = new JMenuItem();
        printerSetup.setAction(actionMap.get(GUIConst.ACTION_PRINTER_SETUP));
        file.add(printerSetup);

        // 印刷
        JMenuItem print = new JMenuItem();
        print.setAction(actionMap.get(GUIConst.ACTION_PRINT));
        setAccelerator(print, KeyEvent.VK_P);
        print.setIcon(null);
        file.add(print);
        if (chart != null) {
            JButton printBtn = new JButton();
            printBtn.setAction(actionMap.get(GUIConst.ACTION_PRINT));
            printBtn.setText(null);
            printBtn.setToolTipText(resource.getString("print.Action.toolTipText"));
            printBtn.setMargin(new Insets(3,3,3,3));
            printBtn.setFocusable(false);
            printBtn.setBorderPainted(false);
            toolBar.add(printBtn);
        }

        //******************************************************
        // Edit Menu
        //******************************************************
        JMenu edit = new JMenu();
        toolBar.addSeparator();

        // 修正
        JMenuItem modifyKarte = new JMenuItem();
        modifyKarte.setAction(actionMap.get(GUIConst.ACTION_MODIFY_KARTE));
        setAccelerator(modifyKarte, KeyEvent.VK_M);
        modifyKarte.setIcon(null);
        edit.add(modifyKarte);
        if (chart != null) {
            JButton modifyKarteBtn = new JButton();
            modifyKarteBtn.setAction(actionMap.get(GUIConst.ACTION_MODIFY_KARTE));
            modifyKarteBtn.setText(null);
            modifyKarteBtn.setToolTipText(resource.getString("modifyKarte.Action.toolTipText"));
            modifyKarteBtn.setMargin(new Insets(3,3,3,3));
            modifyKarteBtn.setFocusable(false);
            modifyKarteBtn.setBorderPainted(false);
            toolBar.add(modifyKarteBtn);
        }
        
        edit.add(new JSeparator());

        // Undo
        JMenuItem undo = new JMenuItem();
        undo.setAction(actionMap.get(GUIConst.ACTION_UNDO));
        setAccelerator(undo, KeyEvent.VK_Z);
        undo.setIcon(null);
        edit.add(undo);
        if (chart != null) {
            JButton undoBtn = new JButton();
            undoBtn.setAction(actionMap.get(GUIConst.ACTION_UNDO));
            undoBtn.setText(null);
            undoBtn.setToolTipText(resource.getString("undo.Action.toolTipText"));
            undoBtn.setMargin(new Insets(3,3,3,3));
            undoBtn.setFocusable(false);
            undoBtn.setBorderPainted(false);
            toolBar.add(undoBtn);
        }

        // Redo
        JMenuItem redo = new JMenuItem();
        redo.setAction(actionMap.get(GUIConst.ACTION_REDO));
        setAccelerator(redo, KeyEvent.VK_Z, true);
        redo.setIcon(null);
        edit.add(redo);
        if (chart != null) {
            JButton redoBtn = new JButton();
            redoBtn.setAction(actionMap.get(GUIConst.ACTION_REDO));
            redoBtn.setText(null);
            redoBtn.setToolTipText(resource.getString("redo.Action.toolTipText"));
            redoBtn.setMargin(new Insets(3,3,3,3));
            redoBtn.setFocusable(false);
            redoBtn.setBorderPainted(false);
            toolBar.add(redoBtn);
        }
        
        edit.add(new JSeparator());

        // Cut
        JMenuItem cut = new JMenuItem();
        cut.setAction(actionMap.get(GUIConst.ACTION_CUT));
        setAccelerator(cut, KeyEvent.VK_X);
        cut.setIcon(null);
        edit.add(cut);
        if (chart != null) {
            JButton cutBtn = new JButton();
            cutBtn.setAction(actionMap.get(GUIConst.ACTION_CUT));
            cutBtn.setText(null);
            cutBtn.setToolTipText(resource.getString("cut.Action.toolTipText"));
            cutBtn.setMargin(new Insets(3,3,3,3));
            cutBtn.setFocusable(false);
            cutBtn.setBorderPainted(false);
            toolBar.add(cutBtn);
        }

        // Copy
        JMenuItem copy = new JMenuItem();
        copy.setAction(actionMap.get(GUIConst.ACTION_COPY));
        setAccelerator(copy, KeyEvent.VK_C);
        copy.setIcon(null);
        edit.add(copy);
        if (chart != null) {
            JButton copyBtn = new JButton();
            copyBtn.setAction(actionMap.get(GUIConst.ACTION_COPY));
            copyBtn.setText(null);
            copyBtn.setToolTipText(resource.getString("copy.Action.toolTipText"));
            copyBtn.setMargin(new Insets(3,3,3,3));
            copyBtn.setFocusable(false);
            copyBtn.setBorderPainted(false);
            toolBar.add(copyBtn);
        }

        // Paste
        JMenuItem paste = new JMenuItem();
        paste.setAction(actionMap.get(GUIConst.ACTION_PASTE));
        setAccelerator(paste, KeyEvent.VK_V);
        paste.setIcon(null);
        edit.add(paste);
        if (chart != null) {
            JButton pasteBtn = new JButton();
            pasteBtn.setAction(actionMap.get(GUIConst.ACTION_PASTE));
            pasteBtn.setText(null);
            pasteBtn.setToolTipText(resource.getString("paste.Action.toolTipText"));
            pasteBtn.setMargin(new Insets(3,3,3,3));
            pasteBtn.setFocusable(false);
            pasteBtn.setBorderPainted(false);
            toolBar.add(pasteBtn);
        }
        
        //******************************************************
        // Karte Menu
        //******************************************************
        JMenu karte = new JMenu();

        // 処方日数変更
        JMenuItem changeNumOfDatesAll = new JMenuItem();
        changeNumOfDatesAll.setAction(actionMap.get(GUIConst.ACTION_CHANGE_NUM_OF_DATES_ALL));
        setAccelerator(changeNumOfDatesAll, KeyEvent.VK_R);
        karte.add(changeNumOfDatesAll);

        // 元町皮ふ科 CLAIM 送信
        JMenuItem sendClaim = new JMenuItem();
        sendClaim.setAction(actionMap.get(GUIConst.ACTION_SEND_CLAIM));
        setAccelerator(sendClaim, KeyEvent.VK_L);
        karte.add(sendClaim);
        
        //-------------------
        // 処方箋印刷 createPrescription
        //-------------------
//s.oh^ 2013/01/24 不要機能の削除
        //JMenuItem createPrescription = new JMenuItem();
        //createPrescription.setName("createPrescription");
        //createPrescription.setAction(actionMap.get("createPrescription"));
        ////setAccelerator(createPrescription, KeyEvent.VK_M);
        //karte.add(createPrescription);
//s.oh$
        
        //-------------------
        // 併用禁忌チェック checkInteraction
        //-------------------
//s.oh^ 2013/01/24 不要機能の削除(復活) -> 不要機能とは何事???
        JMenuItem checkInteraction = new JMenuItem();
        checkInteraction.setAction(actionMap.get(GUIConst.ACTION_CHECK_INTERACTION));
        setAccelerator(checkInteraction, KeyEvent.VK_I);
        karte.add(checkInteraction);
//s.oh$
        
        karte.add(new JSeparator());

        // 昇順
        JRadioButtonMenuItem ascending = new JRadioButtonMenuItem();
        ascending.setAction(actionMap.get(GUIConst.ACTION_ASCENDING));
        actionMap.get(GUIConst.ACTION_ASCENDING).putValue("menuItem",ascending);
        karte.add(ascending);

        // 降順
        JRadioButtonMenuItem descending = new JRadioButtonMenuItem();
        descending.setAction(actionMap.get(GUIConst.ACTION_DESCENDING));
        actionMap.get(GUIConst.ACTION_DESCENDING).putValue("menuItem",descending);
        karte.add(descending);

        // RadiButtonGroup
        ButtonGroup bg = new ButtonGroup();
        bg.add(ascending);
        bg.add(descending);
        
        // 修正履歴表示
        JCheckBoxMenuItem showModified = new JCheckBoxMenuItem();
        showModified.setAction(actionMap.get(GUIConst.ACTION_SHOW_MODIFIED));
        actionMap.get(GUIConst.ACTION_SHOW_MODIFIED).putValue("menuItem",showModified);
        karte.add(showModified);
        
        karte.add(new JSeparator());

        // 環境設定
        JMenuItem setKarteEnviroment = new JMenuItem();
        setKarteEnviroment.setAction(actionMap.get(GUIConst.ACTION_SET_KARTE_ENVIROMENT));
        setAccelerator(setKarteEnviroment, KeyEvent.VK_E);
        karte.add(setKarteEnviroment);
        
        //******************************************************
        // Insert Menu
        //******************************************************
        JMenu insert = new JMenu();
        insert.setName(GUIConst.MENU_NAME_INSERT);
        if (chart != null) {
            insert.addMenuListener(chart);
        }
        
        JMenu insertDisease = new JMenu();
        insertDisease.setAction(actionMap.get(GUIConst.ACTION_INSERT_DISEASE));
        insert.add(insertDisease);
        
        JMenu insertText = new JMenu();
        insertText.setAction(actionMap.get(GUIConst.ACTION_INSERT_TEXT));
        insert.add(insertText);
        
        JMenu insertSchema = new JMenu();
        insertSchema.setAction(actionMap.get(GUIConst.ACTION_INSERT_SCHEMA));
        insert.add(insertSchema);
        
        JMenu insertStamp = new JMenu();
        insertStamp.setAction(actionMap.get(GUIConst.ACTION_INSERT_STAMP));
        insert.add(insertStamp);
        
        //******************************************************
        // Text Menu
        //******************************************************
        JMenu text = new JMenu();
        text.setName(GUIConst.MENU_NAME_TEXT);
        if (chart != null) {
            text.addMenuListener(chart);
        }
        
        //// size ////
        JMenu size = new JMenu();
        size.setAction(actionMap.get(GUIConst.ACTION_SIZE));
        text.add(size);
        
        JMenuItem fontLarger = new JMenuItem();
        fontLarger.setAction(actionMap.get(GUIConst.ACTION_FONT_LARGER));
        size.add(fontLarger);
        
        JMenuItem fontSmaller = new JMenuItem();
        fontSmaller.setAction(actionMap.get(GUIConst.ACTION_FONT_SMALLER));
        size.add(fontSmaller);
        
        JMenuItem fontStandard = new JMenuItem();
        fontStandard.setAction(actionMap.get(GUIConst.ACTION_FONT_STANDARD));
        size.add(fontStandard);  
        
        //// style ////
        JMenu style = new JMenu();
        style.setAction(actionMap.get(GUIConst.ACTION_STYLE));
        text.add(style);
        
        JMenuItem fontBold = new JMenuItem();
        fontBold.setAction(actionMap.get(GUIConst.ACTION_FONT_BOLD));
        setAccelerator(fontBold, KeyEvent.VK_B);
        style.add(fontBold);
        
        JMenuItem fontItalic = new JMenuItem();
        fontItalic.setAction(actionMap.get(GUIConst.ACTION_FONT_ITALIC));
        setAccelerator(fontItalic, KeyEvent.VK_I);
        style.add(fontItalic);
        
        JMenuItem fontUnderline = new JMenuItem();
        fontUnderline.setAction(actionMap.get(GUIConst.ACTION_FONT_UNDERLINE));
        setAccelerator(fontUnderline, KeyEvent.VK_U);
        style.add(fontUnderline);

        //// justify ////
        JMenu justify = new JMenu();
        justify.setAction(actionMap.get(GUIConst.ACTION_JUSTIFY));
        text.add(justify);
        
        JMenuItem leftJustify = new JMenuItem();
        leftJustify.setAction(actionMap.get(GUIConst.ACTION_LEFT_JUSTIFY));
        justify.add(leftJustify);
        
        JMenuItem centerJustify = new JMenuItem();
        centerJustify.setAction(actionMap.get(GUIConst.ACTION_CENTER_JUSTIFY));
        justify.add(centerJustify);
        
        JMenuItem rightJustify = new JMenuItem();
        rightJustify.setAction(actionMap.get(GUIConst.ACTION_RIGHT_JUSTIFY));
        justify.add(rightJustify);
        
        //// Color ////
        JMenu color = new JMenu();
        color.setAction(actionMap.get(GUIConst.ACTION_COLOR));
        text.add(color);
        
        JMenuItem fontRed = new JMenuItem();
        fontRed.setAction(actionMap.get(GUIConst.ACTION_FONT_RED));
        color.add(fontRed);
        
        JMenuItem fontOrange = new JMenuItem();
        fontOrange.setAction(actionMap.get(GUIConst.ACTION_FONT_ORANGE));
        color.add(fontOrange);
        
        JMenuItem fontYellow = new JMenuItem();
        fontYellow.setAction(actionMap.get(GUIConst.ACTION_FONT_YELLOW));
        color.add(fontYellow);
        
        JMenuItem fontGreen = new JMenuItem();
        fontGreen.setAction(actionMap.get(GUIConst.ACTION_FONT_GREEN));
        color.add(fontGreen);
        
        JMenuItem fontBlue = new JMenuItem();
        fontBlue.setAction(actionMap.get(GUIConst.ACTION_FONT_BLUE));
        color.add(fontBlue);
        
        JMenuItem fontPurple = new JMenuItem();
        fontPurple.setAction(actionMap.get(GUIConst.ACTION_FONT_PURPLE));
        color.add(fontPurple);
        
        JMenuItem fontGray = new JMenuItem();
        fontGray.setAction(actionMap.get(GUIConst.ACTION_FONT_GRAY));
        color.add(fontGray);
        
        JMenuItem fontBlack = new JMenuItem();
        fontBlack.setAction(actionMap.get(GUIConst.ACTION_FONT_BLACK));
        color.add(fontBlack);
        
        //******************************************************
        // Tool menu
        //******************************************************
        JMenu tool = new JMenu();
        
        JMenuItem showStampBox = new JMenuItem();
        showStampBox.setAction(actionMap.get(GUIConst.ACTION_SHOW_STAMPBOX));
        tool.add(showStampBox);
        
        JMenuItem showSchemaBox = new JMenuItem();
        showSchemaBox.setAction(actionMap.get(GUIConst.ACTION_SHOW_SCHEMABOX));
        tool.add(showSchemaBox);
        
        tool.add(new JSeparator());
        
        JMenuItem changePassword = new JMenuItem();
        changePassword.setAction(actionMap.get(GUIConst.ACTION_CHANGE_PASSWORD));
        tool.add(changePassword);
        
        JMenuItem editFacilityInfo = new JMenuItem();
        editFacilityInfo.setAction(actionMap.get(GUIConst.ACTION_EDIT_FACILITY_INFO));
        tool.add(editFacilityInfo);
        
        JMenuItem addUser = new JMenuItem();
        addUser.setAction(actionMap.get(GUIConst.ACTION_ADD_USER));
        tool.add(addUser);
        
        tool.add(new JSeparator());
       
//s.oh^ 2014/07/08 クラウド0対応    
        if(Project.isCloudZero()) {
            JMenuItem activities = new JMenuItem();
            activities.setAction(actionMap.get(GUIConst.ACTION_FETCH_ACTIVITIES));
            tool.add(activities);
        }       
//s.oh$
        
        // 不要機能の削除(復活) -> 不要機能とは何事 ???
        JMenuItem fetchFacilityCode = new JMenuItem();
        fetchFacilityCode.setAction(actionMap.get(GUIConst.ACTION_FETCH_FACILITY_CODE));
        tool.add(fetchFacilityCode);
        
        LinkedHashMap<String, String> toolProviders = ClientContext.getToolProviders();
        
        if (toolProviders != null && toolProviders.size() > 0) {
            
            tool.add(new JSeparator());
            Iterator<String> iter = toolProviders.keySet().iterator();
            
            while (iter.hasNext()) {
                String cmd = iter.next();
                final String className = toolProviders.get(cmd);
                JMenuItem mItem = new JMenuItem();
                AbstractAction a = new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        main.sendToChain("invokeToolPlugin", className);
                    }
                };
                mItem.setAction(a);
                mItem.setText(cmd);
                tool.add(mItem);
            }
        } 
        
//minagawa^ delete        
////s.oh^ 2014/08/19 受付バーコード対応
//        tool.add(new JSeparator());
//        JMenuItem receiptBarcode = new JMenuItem();
//        receiptBarcode.setAction(actionMap.get(GUIConst.ACTION_RECEIPT_BARCODE));
//        tool.add(receiptBarcode);
////s.oh$
        
//s.oh^ 2014/07/22 一括カルテPDF出力
        if(!Project.isReadOnly() && Project.getBoolean("output.all.karte.pdf")) {
            tool.add(new JSeparator());
            JMenuItem outputAllKartePdf = new JMenuItem();
            outputAllKartePdf.setAction(actionMap.get(GUIConst.ACTION_OUTPUT_ALLKARTEPDF));
            tool.add(outputAllKartePdf);
        }
//s.oh$
        
        //*****************************************************
        // Help Menu
        //*****************************************************
        JMenu help = new JMenu();
        
        JMenuItem browseDolphinSupport = new JMenuItem();
        browseDolphinSupport.setAction(actionMap.get(GUIConst.ACTION_BROWS_DOLPHIN));
        help.add(browseDolphinSupport);
        
        JMenuItem browseDolphinProject = new JMenuItem();
        browseDolphinProject.setAction(actionMap.get(GUIConst.ACTION_BROWS_DOLPHIN_PROJECT));
        help.add(browseDolphinProject);
        
        JMenuItem browseMedXml = new JMenuItem();
        browseMedXml.setAction(actionMap.get(GUIConst.ACTION_BROWS_MEDXML));
        help.add(browseMedXml);
        
        /******************************************************/
        
        menuBar.add(file,   0);
//s.oh^ 2014/08/19 ID権限
        //menuBar.add(edit,   1);
        //menuBar.add(karte,  2);
        //menuBar.add(insert, 3);
        //menuBar.add(text,   4);
        //menuBar.add(tool,   5);
        //// 6 = Window
        //menuBar.add(help,   7);
        if(!Project.isOtherCare()) {
            menuBar.add(edit,   1);
            menuBar.add(karte,  2);
            menuBar.add(insert, 3);
            menuBar.add(text,   4);
            menuBar.add(tool,   5);
            // 6 = Window
            menuBar.add(help,   7);
        }
//s.oh$
        /******************************************************/
        file.setText(resource.getString("fileMenu.text"));
        edit.setText(resource.getString("editMenu.text"));
        karte.setText(resource.getString("karteMenu.text"));
        insert.setText(resource.getString("insertMenu.text"));
        text.setText(resource.getString("textMenu.text"));
        tool.setText(resource.getString("toolMenu.text"));
        help.setText(resource.getString("helpMenu.text"));
        size.setText(resource.getString("size.text"));
        style.setText(resource.getString("style.text"));
        justify.setText(resource.getString("justify.text"));
        color.setText(resource.getString("color.text"));
        /******************************************************/
    }
    
    private void setAccelerator(JMenuItem item, int key) {
        item.setAccelerator(KeyStroke.getKeyStroke(key, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    }
    
    private void setAccelerator(JMenuItem item, int key, boolean shiftMask) {
        item.setAccelerator(
                        KeyStroke.getKeyStroke(key, (java.awt.event.InputEvent.SHIFT_MASK | (Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()))));
    }
}