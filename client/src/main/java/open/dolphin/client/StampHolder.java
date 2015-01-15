package open.dolphin.client;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JPopupMenu;
import javax.swing.border.Border;
import javax.swing.text.Position;
import open.dolphin.infomodel.*;
import open.dolphin.order.StampEditor;
import open.dolphin.project.Project;
import open.dolphin.util.Log;
import open.dolphin.util.ZenkakuUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

/**
 * KartePane に Component　として挿入されるスタンプを保持スルクラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class StampHolder extends AbstractComponentHolder implements ComponentHolder {

    private static final String KEY_MODEL = "model";
    private static final String KEY_HINTS = "hints";
    private static final String KEY_STAMP_NAME = "stampName";
    private static final String KEY_STAMP_HOLDER = "stmpHolder";
    private static final String KEY_ENCODING = "UTF-8";
    private static final String DOT_VM = ".vm";
    
    private static final Color FOREGROUND = new Color(20, 20, 140);
    private static final Color BACKGROUND = Color.white;
//masuda^    
    private static final Border nonSelectedBorder = BorderFactory.createLineBorder(GUIConst.STAMP_HOLDER_NON_SELECTED_BORDER);
    private static final Border selectedBorder = BorderFactory.createLineBorder(GUIConst.STAMP_HOLDER_SELECTED_BORDER);
//masuda$
    
    private ModuleModel stamp;
    private StampRenderingHints hints;
    private KartePane kartePane;
    private Position start;
    private Position end;
    private boolean selected;
    
    private Color foreGround = FOREGROUND;
    private Color background = BACKGROUND;
    
////s.oh^ 2014/09/30 スタンプの色変更
//    private static final Color STAMP_1 = new Color(255, 255, 153);
//    private static final Color STAMP_2 = new Color(255, 255, 204);
//    private static final Color STAMP_3 = new Color(204, 255, 255);
//    private static final Color STAMP_4 = new Color(204, 236, 255);
//    private static final Color STAMP_5 = new Color(153, 204, 255);
//    private static final Color STAMP_6 = new Color(255, 204, 204);
//    private static final Color STAMP_7 = new Color(234, 234, 234);
//    private static final Color STAMP_8 = new Color(255, 204, 153);
////s.oh$
    
    /** Creates new StampHolder2 */
    public StampHolder(KartePane kartePane, ModuleModel stamp) {
        super();
        this.kartePane = kartePane;
//minagawa^ LSC Test        
        //setHints(new StampRenderingHints());
        StampRenderingHints h = new StampRenderingHints();
        h.setShowStampName(Project.getBoolean("karte.show.stampName"));
        setHints(h);
//minagawa$        
        setForeground(foreGround);
        setBackground(background);
//masuda^        
        setBorder(nonSelectedBorder);
//masuda$        
////s.oh^ 2014/09/30 スタンプの色変更
//        if(Project.getBoolean(Project.CHANGE_STAMP_COLOR)) {
//            switch(stamp.getModuleInfoBean().getEntity()) {
//                case IInfoModel.ENTITY_PHYSIOLOGY_ORDER:
//                case IInfoModel.ENTITY_BACTERIA_ORDER:
//                case IInfoModel.ENTITY_LABO_TEST:
//                    h.setLabelColor(STAMP_1);
//                    break;
//                case IInfoModel.ENTITY_TREATMENT:
//                    h.setLabelColor(STAMP_2);
//                    break;
//                case IInfoModel.ENTITY_SURGERY_ORDER:
//                    h.setLabelColor(STAMP_3);
//                    break;
//                case IInfoModel.ENTITY_RADIOLOGY_ORDER:
//                    h.setLabelColor(STAMP_4);
//                    break;
//                case IInfoModel.ENTITY_BASE_CHARGE_ORDER:
//                    h.setLabelColor(STAMP_5);
//                    break;
//                case IInfoModel.ENTITY_INSTRACTION_CHARGE_ORDER:
//                    h.setLabelColor(STAMP_6);
//                    break;
//                case IInfoModel.ENTITY_MED_ORDER:
//                    h.setLabelColor(STAMP_7);
//                    break;
//                case IInfoModel.ENTITY_INJECTION_ORDER:
//                    h.setLabelColor(STAMP_8);
//                    break;
//                default:
//                    break;
//            }
//        }
////s.oh$
        setStamp(stamp);
    }
    
    /**
     * Popupメニューを表示する。
     */
    @Override
    public void mabeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            JPopupMenu popup = new JPopupMenu();
            ChartMediator mediator = kartePane.getMediator();
            popup.add(mediator.getAction(GUIConst.ACTION_CUT));
            popup.add(mediator.getAction(GUIConst.ACTION_COPY));
        
//s.oh^ 2014/04/16 メニュー制御
            mediator.getAction(GUIConst.ACTION_COPY).setEnabled(true);
//s.oh$
            
            // copyAsText
            AbstractAction copyAsTextAction = new AbstractAction("テキストとしてコピー") {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    IInfoModel im = stamp.getModel();
                    if (im instanceof BundleDolphin) {
                        BundleDolphin bundle = (BundleDolphin)im;
                        StringSelection ss = new StringSelection(bundle.toString());
                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
                    }
                }
            };
            popup.add(copyAsTextAction);
            
//s.oh^ 2014/01/27 スタンプのテキストコピー機能拡張
            if(Project.getBoolean("stamp.text.copy.patid")) {
                // copyAsTextAndPatID
                AbstractAction copyAsTextAndOtherAction = new AbstractAction("テキストとしてコピー(患者ID含む)") {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        IInfoModel im = stamp.getModel();
                        if (im instanceof BundleDolphin) {
                            BundleDolphin bundle = (BundleDolphin)im;
                            StringSelection ss = null;
                            if(kartePane.getPatID() != null) {
                                ss = new StringSelection(bundle.toString(kartePane.getPatID(), getStamp().getModuleInfoBean().getStampName()));
                            }else{
                                ss = new StringSelection(bundle.toString());
                            }
                            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
                        }
                    }
                };
                popup.add(copyAsTextAndOtherAction);
            }
//s.oh$
            
            popup.add(mediator.getAction(GUIConst.ACTION_PASTE));
            
            // 編集可の時のみ
            if (kartePane.getTextPane().isEditable()) {
                popup.addSeparator();

                // 右クリックで編集
                AbstractAction editAction = new AbstractAction("編集") {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        edit();
                    }
                };
                popup.add(editAction);
            }
            
            popup.show(e.getComponent(), e.getX(), e.getY());
        }
    }
    
    /**
     * このスタンプホルダのKartePaneを返す。
     */
    @Override
    public KartePane getKartePane() {
        return kartePane;
    }
    
    /**
     * スタンプホルダのコンテントタイプを返す。
     */
    @Override
    public int getContentType() {
        return ComponentHolder.TT_STAMP;
    }
    
    /**
     * このホルダのモデルを返す。
     * @return
     */
    public ModuleModel getStamp() {
        return stamp;
    }
    
    /**
     * このホルダのモデルを設定する。
     * @param stamp
     */
    public void setStamp(ModuleModel stamp) {
        if (this.stamp!=stamp) {
            this.stamp = stamp;
        }
        setMyText();
    }
    
    /**
     * Itemを追加しtextを再描画する
     */
    public void addItems(ClaimItem[] items) {
        if (stamp!=null && items!=null) {
            ClaimBundle bundle = (ClaimBundle)stamp.getModel();
            for (ClaimItem ci : items) {
                bundle.addClaimItem(ci);
            }
            setMyText();
        }
    }
    
    public StampRenderingHints getHints() {
        return hints;
    }
    
    public void setHints(StampRenderingHints hints) {
        this.hints = hints;
    }
    
    /**
     * 選択されているかどうかを返す。
     * @return 選択されている時 true
     */
    @Override
    public boolean isSelected() {
        return selected;
    }
    
    /**
     * 選択属性を設定する。
     * @param selected 選択の時 true
     */
    @Override
    public void setSelected(boolean selected) {
        boolean old = this.selected;
        this.selected = selected;
//masuda^
        if (selected) {
            this.setBorder(selectedBorder);
            this.selected = true;
        } else {
            this.setBorder(nonSelectedBorder);
            this.selected = false;
        }
//masuda

    }
    
    /**
     * KartePane でこのスタンプがダブルクリックされた時コールされる。
     * StampEditor を開いてこのスタンプを編集する。
     */
    @Override
    public void edit() {
        if (kartePane.getTextPane().isEditable()) {
            StampEditor nse = new StampEditor(stamp, StampHolder.this);
        }
    }
    
    /**
     * エディタで編集した値を受け取り内容を表示する。
     */
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        
        Object newStamp = e.getNewValue();
        
        // スタンプを置き換える
        if (newStamp!=null) {
            importStamp((ModuleModel)newStamp);
        }
    }
    
    /**
     * スタンプの内容を置き換える。
     * @param newStamp
     */
    public void importStamp(ModuleModel newStamp) {
        
        // 処方でまとめるかどうか
        boolean merge = Project.getBoolean("merge.rp.with.sameAdmin");
        merge = merge && (newStamp.getModuleInfoBean().getEntity().equals(IInfoModel.ENTITY_MED_ORDER));
        
//s.oh^ 2013/02/22 不具合修正(同じ用法がマージされない)
        setStamp(newStamp);
//s.oh$
        
        if (merge) {
                        
            StampHolder sh = kartePane.findCanMergeRpHolder(this);

            if (sh!=null) {
                // newStampのclaimItemをshへ追加する
                sh.addItems(((BundleMed)newStamp.getModel()).getClaimItem());
                
                // このStampは削除する
//s.oh^ 2013/11/26 スクロールバーのリセット
                //kartePane.removeStamp(this);
                kartePane.removeStamp(this, false);
//s.oh$
                return;
            }
        }
        
//s.oh^ 2014/01/27 同じ検体検査をまとめる
//        boolean mergeLabTest = Project.getBoolean(Project.KARTE_MERGE_WITH_LABTEST, false);
//        mergeLabTest = mergeLabTest && (newStamp.getModuleInfoBean().getEntity().equals(IInfoModel.ENTITY_LABO_TEST));
//        if (mergeLabTest) {
//            StampHolder sh = kartePane.findCanMergeLabTestHolder(this);
//
//            if (sh!=null) {
//                // newStampのclaimItemをshへ追加する
//                sh.addItems(((BundleDolphin)newStamp.getModel()).getClaimItem());
//                
//                // このStampは削除する
//                kartePane.removeStamp(this, false);
//                return;
//            }
//        }
//s.oh$
        
//s.oh^ 2013/02/22 不具合修正(同じ用法がマージされない)
        //setStamp(newStamp);
//s.oh$
        kartePane.setDirty(true);
        kartePane.getTextPane().validate();
        kartePane.getTextPane().repaint();
    }
    
    /**
     * TextPane内での開始と終了ポジションを保存する。
     */
    @Override
    public void setEntry(Position start, Position end) {
        this.start = start;
        this.end = end;
    }
    
    /**
     * 開始ポジションを返す。
     */
    @Override
    public int getStartPos() {
        return start.getOffset();
    }
    
    /**
     * 終了ポジションを返す。
     */
    @Override
    public int getEndPos() {
        return end.getOffset();
    }
    
    /**
     * Velocity を利用してスタンプの内容を表示する。
     */
    private void setMyText() {

        if (getStamp()==null) {
            return;
        }
        
//        String clsName = getStamp().getModel().getClass().getName();
//        System.err.println(clsName);
//        if (clsName.equals("open.dolphin.infomodel.ProgressCourse")) {
//            ProgressCourse pc = (ProgressCourse)getStamp().getModel();
//            System.err.println(pc.getFreeText());
//            return;
//        }
        
        try {
            IInfoModel model = getStamp().getModel();
            VelocityContext context = ClientContext.getVelocityContext();
            context.put(KEY_MODEL, model);
            context.put(KEY_HINTS, getHints());
            context.put(KEY_STAMP_NAME, getStamp().getModuleInfoBean().getStampName());
            
//s.oh^ 2014/02/03 撮影分割数対応
            Map<String, String> items = new HashMap<String, String>();
            if(((BundleDolphin)model).getClaimItem() != null) {
                for(ClaimItem item : ((BundleDolphin)model).getClaimItem()) {
                    if(item.getCode().startsWith("7") && item.getNumber().indexOf("-") > 0) {
                        items.put(item.getCode(), item.getNumber());
                        item.setNumber(item.getNumber().split("-")[0]);
                    }
                }
            }
//s.oh$
            
            String templateFile = getStamp().getModel().getClass().getName() + DOT_VM;
            
            // このスタンプのテンプレートファイルを得る
            if (getStamp().getModuleInfoBean().getEntity()!=null) {
                if (getStamp().getModuleInfoBean().getEntity().equals(IInfoModel.ENTITY_LABO_TEST)) {
                    if (Project.getBoolean("laboFold", true)) {
                        templateFile = "labo.vm";
                    }  
                } 
            }
            
            // Merge する
            StringWriter sw = new StringWriter();
            BufferedWriter bw = new BufferedWriter(sw);
            InputStream instream = ClientContext.getTemplateAsStream(templateFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(instream, KEY_ENCODING));
            Velocity.evaluate(context, bw, KEY_STAMP_HOLDER, reader);
            bw.flush();
            bw.close();
            reader.close();
            
            // 全角数字とスペースを直す
            String text = sw.toString();
            this.setText(ZenkakuUtils.toHalfNumber(text));
            
            // カルテペインへ展開された時広がるのを防ぐ
            this.setMaximumSize(this.getPreferredSize());
            
//s.oh^ 2014/02/03 撮影分割数対応
            if(((BundleDolphin)model).getClaimItem() != null) {
                for(ClaimItem item : ((BundleDolphin)model).getClaimItem()) {
                    if(item.getCode().startsWith("7")) {
                        String data = items.get(item.getCode());
                        if(data != null && data.length() > 0) {
                            item.setNumber(data);
                        }
                    }
                }
            }
//s.oh$
            
        } catch (Exception e) {
            //e.printStackTrace(System.err);
            Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_ERROR, e.toString());
        }
    }
}
