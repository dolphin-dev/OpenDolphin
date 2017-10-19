package open.dolphin.client;

import java.awt.FlowLayout;
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
 * @author Minagawa, Kazushi
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
//minagawa^ Icon Server        
        //ImageIcon icon = ClientContext.getImageIcon(resource.getString("newKarte.Action.icon"));
        ImageIcon icon = ClientContext.getImageIconArias("icon_new_karte");
//minagawa$        
        AbstractAction newKarte = new AbstractAction(text, icon) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain("newKarte");
            }
        };
        map.put("newKarte", newKarte);

        // New Document
        text = resource.getString("newDocument.Action.text");
//minagawa^ Icon Server        
        //icon = ClientContext.getImageIcon(resource.getString("newDocument.Action.icon"));
        icon = ClientContext.getImageIconArias("icon_new_document");
//minagawa$        
        AbstractAction newDocument = new AbstractAction(text, icon) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain("newDocument");
            }
        };
        map.put("newDocument", newDocument);

        // Open Karte
        text = resource.getString("openKarte.Action.text");
        AbstractAction openKarte = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                main.sendToChain("openKarte");
            }
        };
        map.put("openKarte", openKarte);

        // Close
        text = resource.getString("close.Action.text");
        AbstractAction close = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain("close");
            }
        };
        map.put("close", close);

        // Save
        text = resource.getString("save.Action.text");
//minagawa^ Icon Server         
        //icon = ClientContext.getImageIcon(resource.getString("save.Action.icon"));
        icon = ClientContext.getImageIconArias("icon_save");
//minagawa$        
        AbstractAction save = new AbstractAction(text, icon) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain("save");
            }
        };
        map.put("save", save);

        // Delete
        text = resource.getString("delete.Action.text");
        AbstractAction delete = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain("delete");
            }
        };
        map.put("delete", delete);

        // Printer Setup
        text = resource.getString("printerSetup.Action.text");
        AbstractAction printerSetup = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                main.sendToChain("printerSetup");
            }
        };
        map.put("printerSetup", printerSetup);

        // Print
        text = resource.getString("print.Action.text");
//minagawa^ Icon Server         
        //icon = ClientContext.getImageIcon(resource.getString("print.Action.icon"));
        icon = ClientContext.getImageIconArias("icon_printer");
//minagawa$        
        AbstractAction print = new AbstractAction(text, icon) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain("print");
            }
        };
        map.put("print", print);

        // Exit
        text = resource.getString("processExit.Action.text");
        AbstractAction processExit = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                main.sendToChain("processExit");
            }
        };
        map.put("processExit", processExit);

        // Modify
        text = resource.getString("modifyKarte.Action.text");
//minagawa^ Icon Server         
        //icon = ClientContext.getImageIcon(resource.getString("modifyKarte.Action.icon"));
        icon = ClientContext.getImageIconArias("icon_edit_karte_document");
//minagawa$        
        AbstractAction modifyKarte = new AbstractAction(text, icon) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain("modifyKarte");
            }
        };
        map.put("modifyKarte", modifyKarte);
        
//s.oh^ 2014/04/03 文書の複製
        text = "複製";
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
//minagawa^ Icon Server         
        //icon = ClientContext.getImageIcon(resource.getString("undo.Action.icon"));
        icon = ClientContext.getImageIconArias("icon_undo");
//minagawa$        
        AbstractAction undo = new AbstractAction(text, icon) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain("undo");
            }
        };
        map.put("undo", undo);

        // Redo
        text = resource.getString("redo.Action.text");
//minagawa^ Icon Server         
        //icon = ClientContext.getImageIcon(resource.getString("redo.Action.icon"));
        icon = ClientContext.getImageIconArias("icon_redo");
//minagawa$        
        AbstractAction redo = new AbstractAction(text, icon) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain("redo");
            }
        };
        map.put("redo", redo);

        // Cut
        text = resource.getString("cut.Action.text");
//minagawa^ Icon Server         
        //icon = ClientContext.getImageIcon(resource.getString("cut.Action.icon"));
        icon = ClientContext.getImageIconArias("icon_cut");
//minagawa$        
        AbstractAction cut = new AbstractAction(text, icon) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.cut();
            }
        };
        map.put("cut", cut);

        // Copy
        text = resource.getString("copy.Action.text");
//minagawa^ Icon Server         
        //icon = ClientContext.getImageIcon(resource.getString("copy.Action.icon"));
        icon = ClientContext.getImageIconArias("icon_copy");
//minagawa$        
        AbstractAction copy = new AbstractAction(text, icon) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.copy();
            }
        };
        map.put("copy", copy);

        // Paste
        text = resource.getString("paste.Action.text");
//minagawa^ Icon Server         
        //icon = ClientContext.getImageIcon(resource.getString("paste.Action.icon"));
        icon = ClientContext.getImageIconArias("icon_paste");
//minagawa$        
        AbstractAction paste = new AbstractAction(text, icon) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.paste();
            }
        };
        map.put("paste", paste);

        // 昇順
        text = resource.getString("ascending.Action.text");
        AbstractAction ascending = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain("ascending");
            }
        };
        map.put("ascending", ascending);

        // 降順
        text = resource.getString("descending.Action.text");
        AbstractAction descending = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain("descending");
            }
        };
        map.put("descending", descending);

        // 修正履歴表示
        text = resource.getString("showModified.Action.text");
        AbstractAction showModified = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain("showModified");
            }
        };
        map.put("showModified", showModified);

        // 環境設定
        text = resource.getString("setKarteEnviroment.Action.text");
        AbstractAction setKarteEnviroment = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                main.sendToChain("setKarteEnviroment");
            }
        };
        map.put("setKarteEnviroment", setKarteEnviroment);
        
        // 保険選択
        AbstractAction selectInsurance = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
            }
        };
        map.put("selectInsurance", selectInsurance);
        
        // 処方日数一括変更
        text = resource.getString("changeNumOfDatesAll.Action.text");
        AbstractAction changeNumOfDatesAll = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain("changeNumOfDatesAll");
            }
        };
        map.put("changeNumOfDatesAll", changeNumOfDatesAll);
        
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
                chart.sendToChain("sendClaim");
            }
        };
        map.put("sendClaim", sendClaim);

        // 併用禁忌チェック
        text = resource.getString("checkInteraction.Action.text");
        AbstractAction checkInteraction = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain("checkInteraction");
            }
        };
        map.put("checkInteraction", checkInteraction);

        // 挿入　病名
        text = resource.getString("insertDisease.Action.text");
        AbstractAction insertDisease = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
            }
        };
        map.put("insertDisease", insertDisease);

        // 挿入　テキスト
        text = resource.getString("insertText.Action.text");
        AbstractAction insertText = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
            }
        };
        map.put("insertText", insertText);

        // 挿入　シェーマ
        text = resource.getString("insertSchema.Action.text");
        AbstractAction insertSchema = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
            }
        };
        map.put("insertSchema", insertSchema);
        
        // 添付/挿入
        text = resource.getString("attachment.Action.text");
        AbstractAction attachment = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain("attachment");
            }
        };
        map.put("attachment", attachment);

        // 挿入　スタンプ
        text = resource.getString("insertStamp.Action.text");
        AbstractAction insertStamp = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
            }
        };
        map.put("insertStamp", insertStamp);

        // Size
        text = resource.getString("size.text");
        AbstractAction size = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
            }
        };
        map.put("size", size);

        // 大きく
        text = resource.getString("fontLarger.Action.text");
        AbstractAction fontLarger = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain("fontLarger");
            }
        };
        map.put("fontLarger", fontLarger);

        // 小さく
        text = resource.getString("fontSmaller.Action.text");
        AbstractAction fontSmaller = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain("fontSmaller");
            }
        };
        map.put("fontSmaller", fontSmaller);

        // 標準
        text = resource.getString("fontStandard.Action.text");
        AbstractAction fontStandard = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain("fontStandard");
            }
        };
        map.put("fontStandard", fontStandard);

        // スタイル
        text = resource.getString("style.text");
        AbstractAction style = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
            }
        };
        map.put("style", style);

        // ボールド
        text = resource.getString("fontBold.Action.text");
        AbstractAction fontBold = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain("fontBold");
            }
        };
        map.put("fontBold", fontBold);

        // イタリック
        text = resource.getString("fontItalic.Action.text");
        AbstractAction fontItalic = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain("fontItalic");
            }
        };
        map.put("fontItalic", fontItalic);

        // 下線
        text = resource.getString("fontUnderline.Action.text");
        AbstractAction fontUnderline = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain("fontUnderline");
            }
        };
        map.put("fontUnderline", fontUnderline);

        // Justify
        text = resource.getString("justify.text");
        AbstractAction justify = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
            }
        };
        map.put("justify", justify);

        // 左揃え
        text = resource.getString("leftJustify.Action.text");
        AbstractAction leftJustify = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain("leftJustify");
            }
        };
        map.put("leftJustify", leftJustify);

        // 中央
        text = resource.getString("centerJustify.Action.text");
        AbstractAction centerJustify = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain("centerJustify");
            }
        };
        map.put("centerJustify", centerJustify);

        // 右よせ
        text = resource.getString("rightJustify.Action.text");
        AbstractAction rightJustify = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain("rightJustify");
            }
        };
        map.put("rightJustify", rightJustify);

        // カラー
        text = resource.getString("color.text");
        AbstractAction color = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
            }
        };
        map.put("color", color);

        // 赤
        text = resource.getString("fontRed.Action.text");
        AbstractAction fontRed = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain("fontRed");
            }
        };
        map.put("fontRed", fontRed);

        // オレンジ
        text = resource.getString("fontOrange.Action.text");
        AbstractAction fontOrange = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain("fontOrange");
            }
        };
        map.put("fontOrange", fontOrange);

        // 黄色
        text = resource.getString("fontYellow.Action.text");
        AbstractAction fontYellow = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain("fontYellow");
            }
        };
        map.put("fontYellow", fontYellow);

        // 緑
        text = resource.getString("fontGreen.Action.text");
        AbstractAction fontGreen = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain("fontGreen");
            }
        };
        map.put("fontGreen", fontGreen);

        // 青
        text = resource.getString("fontBlue.Action.text");
        AbstractAction fontBlue = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain("fontBlue");
            }
        };
        map.put("fontBlue", fontBlue);

        // 紫
        text = resource.getString("fontPurple.Action.text");
        AbstractAction fontPurple = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain("fontPurple");
            }
        };
        map.put("fontPurple", fontPurple);

        // グレー
        text = resource.getString("fontGray.Action.text");
        AbstractAction fontGray = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain("fontGray");
            }
        };
        map.put("fontGray", fontGray);

        // ブラック
        text = resource.getString("fontBlack.Action.text");
        AbstractAction fontBlack = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                chart.sendToChain("fontBlack");
            }
        };
        map.put("fontBlack", fontBlack);

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
                main.sendToChain("showStampBox");
            }
        };
        map.put("showStampBox", showStampBox);

        // シェーマBox
        text = resource.getString("showSchemaBox.Action.text");
        AbstractAction showSchemaBox = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                main.sendToChain("showSchemaBox");
            }
        };
        map.put("showSchemaBox", showSchemaBox);

        // パスワード変更
        text = resource.getString("changePassword.Action.text");
        AbstractAction changePassword = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                main.sendToChain("changePassword");
            }
        };
        map.put("changePassword", changePassword);
        
        // 施設情報編集
        text = resource.getString("editFacilityInfo.Action.text");
        AbstractAction editFacilityInfo = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                main.sendToChain("editFacilityInfo");
            }
        };
        map.put("editFacilityInfo", editFacilityInfo);

        // ユーザー追加
        text = resource.getString("addUser.Action.text");
        AbstractAction addUser = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                main.sendToChain("addUser");
            }
        };
        map.put("addUser", addUser);
        
//s.oh^ 2014/07/08 クラウド0対応
//minagawa^ 統計情報
        text = resource.getString("activities.Action.text");
        AbstractAction fetchActivities = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                main.sendToChain("fetchActivities");
            }
        };
        map.put("fetchActivities", fetchActivities);
//minagawa$        
//s.oh$
        
        // 医療機関コード取得
        text = resource.getString("fetchFacilityCode.Action.text");
        AbstractAction fetchFacilityCode = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                main.sendToChain("fetchFacilityCode");
            }
        };
        map.put("fetchFacilityCode", fetchFacilityCode);

        // Support
        text = resource.getString("browseDolphinSupport.Action.text");
        AbstractAction browseDolphinSupport = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                main.sendToChain("browseDolphinSupport");
            }
        };
        map.put("browseDolphinSupport", browseDolphinSupport);

        // Dolphin
        text = resource.getString("browseDolphinProject.Action.text");
        AbstractAction browseDolphinProject = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                main.sendToChain("browseDolphinProject");
            }
        };
        map.put("browseDolphinProject", browseDolphinProject);

       /**soso*/ // MedXML
       /** text = resource.getString("browseMedXml.Action.text");
        AbstractAction browseMedXml = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                main.sendToChain("browseMedXml");
            }
        };
        map.put("browseMedXml", browseMedXml);*/

        // About
        text = resource.getString("showAbout.Action.text");
        AbstractAction showAbout = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                main.sendToChain("showAbout");
            }
        };
        map.put("showAbout", showAbout);
        
//s.oh^ テキストの挿入 2013/08/12
        text = resource.getString("soapane.Action.text");
//minagawa^ Icon Server         
        //icon = ClientContext.getImageIcon(resource.getString("soapane.Action.icon"));
        icon = ClientContext.getImageIconArias(resource.getString("soapane.Action.icon"));
//minagawa$
        AbstractAction insertSOAText = new AbstractAction(text, icon) {
            @Override
            public void actionPerformed(ActionEvent e) {
                chart.sendToChain("insertSOAText");
            }
        };
        map.put("insertSOAText", insertSOAText);
        
        text = resource.getString("ppane.Action.text");
//minagawa^ Icon Server         
        //icon = ClientContext.getImageIcon(resource.getString("ppane.Action.icon"));
        icon = ClientContext.getImageIconArias(resource.getString("ppane.Action.icon"));
//minagawa$
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
        
//s.oh^ 2014/08/19 受付バーコード対応
        text = resource.getString("receipt.barcode.Action.text");
        AbstractAction receiptBarcode = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.sendToChain("receiptBarcode");
            }
        };
        map.put("receiptBarcode", receiptBarcode);
//s.oh$
        
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
        ResourceBundle resource = ClientContext.getBundle(this.getClass());
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

        //----------------
        // ファイル
        //----------------
        JMenu file = new JMenu();
        file.setName("fileMenu");

        //----------------
        // 新規カルテ
        //----------------
        JMenuItem newKarte = new JMenuItem();
        newKarte.setName("newKarte");
        newKarte.setAction(actionMap.get("newKarte"));
        setAccelerator(newKarte, KeyEvent.VK_N);
        newKarte.setIcon(null);
        file.add(newKarte);
        if (chart != null) {
            JButton newKarteBtn = new JButton();
            newKarteBtn.setAction(actionMap.get("newKarte"));
            newKarteBtn.setText(null);
            newKarteBtn.setToolTipText("カルテを新規に作成します。");
            newKarteBtn.setMargin(new Insets(3,3,3,3));
            newKarteBtn.setFocusable(false);
            newKarteBtn.setBorderPainted(false);
            toolBar.add(newKarteBtn);
        }

        //----------------
        // 新規文書
        //----------------
        JMenuItem newDocument = new JMenuItem();
        newDocument.setName("newDocument");
        newDocument.setAction(actionMap.get("newDocument"));
        newDocument.setIcon(null);
        file.add(newDocument);
        if (chart != null) {
            JButton newDocBtn = new JButton();
            newDocBtn.setAction(actionMap.get("newDocument"));
            newDocBtn.setText(null);
            newDocBtn.setToolTipText("紹介状等の文書を新規に作成します。");
            newDocBtn.setMargin(new Insets(3,3,3,3));
            newDocBtn.setFocusable(false);
            newDocBtn.setBorderPainted(false);
            toolBar.add(newDocBtn);
        }

        //----------------
        // 開く
        //----------------
        JMenuItem openKarte = new JMenuItem();
        openKarte.setName("openKarte");
        openKarte.setAction(actionMap.get("openKarte"));
        setAccelerator(openKarte, KeyEvent.VK_O);
        file.add(openKarte);
        
        file.add(new JSeparator());

        //----------------
        // 閉じる
        //----------------
        JMenuItem close = new JMenuItem();
        close.setName("close");
        close.setAction(actionMap.get("close"));
        setAccelerator(close, KeyEvent.VK_W);
        file.add(close);

        //----------------
        // 保存
        //----------------
        JMenuItem save = new JMenuItem();
        save.setName("save");
        save.setAction(actionMap.get("save"));
        setAccelerator(save, KeyEvent.VK_S);
        save.setIcon(null);
        file.add(save);
        if (chart != null) {
            JButton saveBtn = new JButton();
            saveBtn.setAction(actionMap.get("save"));
            saveBtn.setText(null);
            saveBtn.setToolTipText("カルテや文書を保存します。");
            saveBtn.setMargin(new Insets(3,3,3,3));
            saveBtn.setFocusable(false);
            saveBtn.setBorderPainted(false);
            toolBar.add(saveBtn);
        }
        
        file.add(new JSeparator());

        //----------------
        // 削除
        //----------------
//s.oh^ 2013/09/05
        if(Project.getBoolean("delete.karte.enable", true)) {
//s.oh$
            JMenuItem delete = new JMenuItem();
            delete.setName("delete");
            delete.setAction(actionMap.get("delete"));
            file.add(delete);

            file.add(new JSeparator());
        }

        //----------------
        // 印刷設定
        //----------------
        JMenuItem printerSetup = new JMenuItem();
        printerSetup.setName("printerSetup");
        printerSetup.setAction(actionMap.get("printerSetup"));
        file.add(printerSetup);

        //----------------
        // 印刷
        //----------------
        JMenuItem print = new JMenuItem();
        print.setName("print");
        print.setAction(actionMap.get("print"));
        setAccelerator(print, KeyEvent.VK_P);
        print.setIcon(null);
        file.add(print);
        if (chart != null) {
            JButton printBtn = new JButton();
            printBtn.setAction(actionMap.get("print"));
            printBtn.setText(null);
            printBtn.setToolTipText("印刷します。");
            printBtn.setMargin(new Insets(3,3,3,3));
            printBtn.setFocusable(false);
            printBtn.setBorderPainted(false);
            toolBar.add(printBtn);
        }
        
        /******************************************************/

        //----------------
        // Edit
        //----------------
        JMenu edit = new JMenu();
        edit.setName("editMenu");
        toolBar.addSeparator();

        //----------------
        // 修正
        //----------------
        JMenuItem modifyKarte = new JMenuItem();
        modifyKarte.setName("modifyKarte");
        modifyKarte.setAction(actionMap.get("modifyKarte"));
        setAccelerator(modifyKarte, KeyEvent.VK_M);
        modifyKarte.setIcon(null);
        edit.add(modifyKarte);
        if (chart != null) {
            JButton modifyKarteBtn = new JButton();
            modifyKarteBtn.setAction(actionMap.get("modifyKarte"));
            modifyKarteBtn.setText(null);
            modifyKarteBtn.setToolTipText("カルテや文書を修正します。");
            modifyKarteBtn.setMargin(new Insets(3,3,3,3));
            modifyKarteBtn.setFocusable(false);
            modifyKarteBtn.setBorderPainted(false);
            toolBar.add(modifyKarteBtn);
        }
        
        edit.add(new JSeparator());

        //----------------
        // Undo
        //----------------
        JMenuItem undo = new JMenuItem();
        undo.setName("undo");
        undo.setAction(actionMap.get("undo"));
        setAccelerator(undo, KeyEvent.VK_Z);
        undo.setIcon(null);
        edit.add(undo);
        if (chart != null) {
            JButton undoBtn = new JButton();
            undoBtn.setAction(actionMap.get("undo"));
            undoBtn.setText(null);
            undoBtn.setToolTipText("操作をやり直します。");
            undoBtn.setMargin(new Insets(3,3,3,3));
            undoBtn.setFocusable(false);
            undoBtn.setBorderPainted(false);
            toolBar.add(undoBtn);
        }

        //----------------
        // Redo
        //----------------
        JMenuItem redo = new JMenuItem();
        redo.setName("redo");
        redo.setAction(actionMap.get("redo"));
        setAccelerator(redo, KeyEvent.VK_Z, true);
        redo.setIcon(null);
        edit.add(redo);
        if (chart != null) {
            JButton redoBtn = new JButton();
            redoBtn.setAction(actionMap.get("redo"));
            redoBtn.setText(null);
            redoBtn.setToolTipText("操作を再実行します。");
            redoBtn.setMargin(new Insets(3,3,3,3));
            redoBtn.setFocusable(false);
            redoBtn.setBorderPainted(false);
            toolBar.add(redoBtn);
        }
        
        edit.add(new JSeparator());

        //----------------
        // Cut
        //----------------
        JMenuItem cut = new JMenuItem();
        cut.setName("cut");
        cut.setAction(actionMap.get("cut"));
        setAccelerator(cut, KeyEvent.VK_X);
        cut.setIcon(null);
        edit.add(cut);
        if (chart != null) {
            JButton cutBtn = new JButton();
            cutBtn.setAction(actionMap.get("cut"));
            cutBtn.setText(null);
            cutBtn.setToolTipText("テキスト、スタンプ、画像をカットします。");
            cutBtn.setMargin(new Insets(3,3,3,3));
            cutBtn.setFocusable(false);
            cutBtn.setBorderPainted(false);
            toolBar.add(cutBtn);
        }

        //----------------
        // Copy
        //----------------
        JMenuItem copy = new JMenuItem();
        copy.setName("copy");
        copy.setAction(actionMap.get("copy"));
        setAccelerator(copy, KeyEvent.VK_C);
        copy.setIcon(null);
        edit.add(copy);
        if (chart != null) {
            JButton copyBtn = new JButton();
            copyBtn.setAction(actionMap.get("copy"));
            copyBtn.setText(null);
            copyBtn.setToolTipText("テキスト、スタンプ、画像をコピーします。");
            copyBtn.setMargin(new Insets(3,3,3,3));
            copyBtn.setFocusable(false);
            copyBtn.setBorderPainted(false);
            toolBar.add(copyBtn);
        }

        //----------------
        // Paste
        //----------------
        JMenuItem paste = new JMenuItem();
        paste.setName("paste");
        paste.setAction(actionMap.get("paste"));
        setAccelerator(paste, KeyEvent.VK_V);
        paste.setIcon(null);
        edit.add(paste);
        if (chart != null) {
            JButton pasteBtn = new JButton();
            pasteBtn.setAction(actionMap.get("paste"));
            pasteBtn.setText(null);
            pasteBtn.setToolTipText("テキスト、スタンプ、画像をペーストします。");
            pasteBtn.setMargin(new Insets(3,3,3,3));
            pasteBtn.setFocusable(false);
            pasteBtn.setBorderPainted(false);
            toolBar.add(pasteBtn);
        }
        
        /******************************************************/

        //----------------
        // Karte
        //----------------
        JMenu karte = new JMenu();
        karte.setName("karteMenu");
//        if (chart != null) {
//            karte.addMenuListener(chart);
//        }

        //-------------------
        // 処方日数変更 createPrescription
        //-------------------
        JMenuItem changeNumOfDatesAll = new JMenuItem();
        changeNumOfDatesAll.setName("changeNumOfDatesAll");
        changeNumOfDatesAll.setAction(actionMap.get("changeNumOfDatesAll"));
        setAccelerator(changeNumOfDatesAll, KeyEvent.VK_R);
        karte.add(changeNumOfDatesAll);

        //--------------------
        // 元町皮ふ科 CLAIM 送信
        //--------------------
        JMenuItem sendClaim = new JMenuItem();
        sendClaim.setName("sendClaim");
        sendClaim.setAction(actionMap.get("sendClaim"));
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
//s.oh^ 2013/01/24 不要機能の削除(復活)
        JMenuItem checkInteraction = new JMenuItem();
        checkInteraction.setName("checkInteraction");
        checkInteraction.setAction(actionMap.get("checkInteraction"));
        setAccelerator(checkInteraction, KeyEvent.VK_I);
        karte.add(checkInteraction);
//s.oh$
        
        karte.add(new JSeparator());

        //----------------
        // 昇順
        //----------------
        JRadioButtonMenuItem ascending = new JRadioButtonMenuItem();
        ascending.setName("ascending");
        ascending.setAction(actionMap.get("ascending"));
        actionMap.get("ascending").putValue("menuItem",ascending);
        karte.add(ascending);

        //----------------
        // 降順
        //----------------
        JRadioButtonMenuItem descending = new JRadioButtonMenuItem();
        descending.setName("descending");
        descending.setAction(actionMap.get("descending"));
        actionMap.get("descending").putValue("menuItem",descending);
        karte.add(descending);

        //----------------
        // RadiButtonGroup
        //----------------
        ButtonGroup bg = new ButtonGroup();
        bg.add(ascending);
        bg.add(descending);
        
        //----------------
        // 修正履歴表示
        //----------------
        JCheckBoxMenuItem showModified = new JCheckBoxMenuItem();
        showModified.setName("showModified");
        showModified.setAction(actionMap.get("showModified"));
        actionMap.get("showModified").putValue("menuItem",showModified);
        karte.add(showModified);
        
        karte.add(new JSeparator());

        //----------------
        // 環境設定
        //----------------
        JMenuItem setKarteEnviroment = new JMenuItem();
        setKarteEnviroment.setName("setKarteEnviroment");
        setKarteEnviroment.setAction(actionMap.get("setKarteEnviroment"));
        setAccelerator(setKarteEnviroment, KeyEvent.VK_E);
        karte.add(setKarteEnviroment);
        
        /******************************************************/

        //----------------
        // Insert
        //----------------
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

        //----------------
        // Text
        //----------------
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
        
        JMenuItem editFacilityInfo = new JMenuItem();
        editFacilityInfo.setName("editFacilityInfo");
        editFacilityInfo.setAction(actionMap.get("editFacilityInfo"));
        tool.add(editFacilityInfo);
        
        JMenuItem addUser = new JMenuItem();
        addUser.setName("addUser");
        addUser.setAction(actionMap.get("addUser"));//fetchFacilityCode
        tool.add(addUser);
        
        tool.add(new JSeparator());
       
//s.oh^ 2014/07/08 クラウド0対応
//minagawa^ 統計情報        
        if(Project.isCloudZero()) {
            JMenuItem activities = new JMenuItem();
            activities.setName("activities");
            activities.setAction(actionMap.get("fetchActivities"));
            tool.add(activities);
        }
//mingawa$        
//s.oh$
        
        // 不要機能の削除(復活)
        JMenuItem fetchFacilityCode = new JMenuItem();
        fetchFacilityCode.setName("fetchFacilityCode");
        fetchFacilityCode.setAction(actionMap.get("fetchFacilityCode"));//
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
        
//s.oh^ 2014/08/19 受付バーコード対応
        tool.add(new JSeparator());
        JMenuItem receiptBarcode = new JMenuItem();
        receiptBarcode.setName("receiptBarcode");
        receiptBarcode.setAction(actionMap.get("receiptBarcode"));
        tool.add(receiptBarcode);
//s.oh$
        
//s.oh^ 2014/07/22 一括カルテPDF出力
        if(!Project.isReadOnly() && Project.getBoolean("output.all.karte.pdf")) {
            tool.add(new JSeparator());
            JMenuItem outputAllKartePdf = new JMenuItem();
            outputAllKartePdf.setName("outputAllKartePdf");
            outputAllKartePdf.setAction(actionMap.get("outputAllKartePdf"));
            tool.add(outputAllKartePdf);
        }
//s.oh$
        
        /******************************************************/

        //----------------
        // Help
        //----------------
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
    }
    
    private void setAccelerator(JMenuItem item, int key) {
        item.setAccelerator(KeyStroke.getKeyStroke(key, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    }
    
    private void setAccelerator(JMenuItem item, int key, boolean shiftMask) {
        item.setAccelerator(
                        KeyStroke.getKeyStroke(key, (java.awt.event.InputEvent.SHIFT_MASK | (Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()))));
    }
}















