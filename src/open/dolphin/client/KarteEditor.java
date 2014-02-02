/*
 * KarteEditor2.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
 * Copyright (C) 2003-2005 Digital Globe, Inc. All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package open.dolphin.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.*;
import java.awt.print.PageFormat;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Date;
import java.util.TooManyListenersException;
import java.util.prefs.Preferences;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;

import open.dolphin.delegater.DocumentDelegater;
import open.dolphin.exception.DolphinException;
import open.dolphin.infomodel.AccessRightModel;
import open.dolphin.infomodel.ClaimBundle;
import open.dolphin.infomodel.DocInfoModel;
import open.dolphin.infomodel.ExtRefModel;
import open.dolphin.infomodel.ID;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.DocumentModel;
import open.dolphin.infomodel.KarteBean;
import open.dolphin.infomodel.ModelUtils;
import open.dolphin.infomodel.ModuleModel;
import open.dolphin.infomodel.ModuleInfoBean;
import open.dolphin.infomodel.ProgressCourse;
import open.dolphin.infomodel.SchemaModel;
import open.dolphin.message.*;
import open.dolphin.project.Project;
import open.dolphin.util.BeanUtils;

import com.sun.image.codec.jpeg.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import open.dolphin.infomodel.PVTHealthInsuranceModel;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

/**
 * 2号カルテクラス。
 *
 * @author Kazushi Minagawa
 */
public class KarteEditor extends DefaultChartDocument implements IInfoModel {
    
    private static final long serialVersionUID = 5805336364541168205L;
    
    // TimeStamp のカラー
    private static final Color TIMESTAMP_FORE = Color.BLUE;
    
    private static final int TIMESTAMP_FONT_SIZE = 14;
    
    private static final Font TIMESTAMP_FONT = new Font("Dialog", Font.PLAIN,
            TIMESTAMP_FONT_SIZE);
    
    private static final int TIMESTAMP_SPACING = 7;
    
    // ラベル等
    private static final String DEFAULT_TITLE = "経過記録";
    
    private static final String UPDATE_TAB_TITLE = "更新";
    
    private static final String[] TASK_MSG = { "保存しています...", "保存しました",
    "通信もしくはアプリケーションエラーが起きています", "印刷しています..." };
    
    /** このエディタのモデル */
    private DocumentModel model;
    
    /** このエディタを構成するコンポーネント */
    private JLabel timeStampLabel;
    
    private String timeStamp;
    
    //private JLabel sendClaimLabel;
    
    // 健康保険Box
    private boolean insuranceVisible;
    
    /** SOA Pane*/
    private KartePane soaPane;
    
    /** P Pane */
    private KartePane pPane;
    
    /** 2号カルテ JPanel */
    private Panel2 panel2;
    
    /** タイムスタンプの foreground */
    private Color timeStampFore = TIMESTAMP_FORE;
    
    /** タイムスタンプフォント */
    private Font timeStampFont = TIMESTAMP_FONT;
    
    private int timeStampSpacing = TIMESTAMP_SPACING;
    
    /** 編集可能かどうかのフラグ */
    private boolean editable;
    
    /** 修正時に true */
    private boolean modify;
    
    /** CLAIM 送信リスナ */
    private ClaimMessageListener claimListener;
    
    /** MML送信リスナ */
    private MmlMessageListener mmlListener;
    
    /** MML送信フラグ */
    private boolean sendMml;
    
    /** CLAIM 送信フラグ */
    private boolean sendClaim;
    
    /** State Manager */
    private StateMgr stateMgr;
    
    private javax.swing.Timer taskTimer;
    
    private Logger logger;
    
    /** 
     * Creates new KarteEditor2 
     */
    public KarteEditor() {
        logger = ClientContext.getLogger("boot");
        setTitle(DEFAULT_TITLE);
    }
    
    /**
     * DocumentModelを返す。
     * @return DocumentModel
     */
    public DocumentModel getModel() {
        return model;
    }
    
    /**
     * DocumentModelを設定する。
     * @param model DocumentModel
     */
    public void setModel(DocumentModel model) {
        this.model = model;
    }
    
    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    // Junzo SATO
    public void printPanel2(final PageFormat format) {
        String name = getContext().getPatient().getFullName();
        panel2.printPanel(format, 1, false, name);
    }
    
    public void printPanel2(final PageFormat format, final int copies,
        final boolean useDialog) {
        String name = getContext().getPatient().getFullName();
        panel2.printPanel(format, copies, useDialog, name);
    }
    
    private int getActualHeight() {
        try {
            JTextPane pane = soaPane.getTextPane();
            int pos = pane.getDocument().getLength();
            Rectangle r = pane.modelToView(pos);
            int hsoa = r.y;
            
            pane = pPane.getTextPane();
            pos = pane.getDocument().getLength();
            r = pane.modelToView(pos);
            int hp = r.y;
            
            return Math.max(hsoa, hp);
            
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
        return 0;
    }
    
    public void print() {
        PageFormat pageFormat = getContext().getContext().getPageFormat();
        this.printPanel2(pageFormat);
    }
    
    public void insertImage() {
        JFileChooser chooser = new JFileChooser();
        int selected = chooser.showOpenDialog(getContext().getFrame());
        if (selected == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getPath();
            PlanarImage ri = JAI.create("fileload", path);
            if (ri == null) {
                return;
            }
            BufferedImage bf = ri.getAsBufferedImage();
            
            // insert image to the SOA Pane
            this.getSOAPane().myInsertImage(bf);
            
        } else if (selected == JFileChooser.CANCEL_OPTION) {
            return;
        }
    }
    
    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    
    /**
     * SOAPaneを返す。
     * @return SOAPane
     */
    protected KartePane getSOAPane() {
        return soaPane;
    }
    
    /**
     * PPaneを返す。
     * @return PPane
     */
    protected KartePane getPPane() {
        return pPane;
    }
    
    /**
     * 編集可能属性を設定する。
     * @param b 編集可能な時true
     */
    protected void setEditable(boolean b) {
        editable = b;
    }
    
    /**
     * MMLリスナを追加する。
     * @param listener MMLリスナリスナ
     */
    public void addMMLListner(MmlMessageListener listener) throws TooManyListenersException {
        if (mmlListener != null) {
            throw new TooManyListenersException();
        }
        mmlListener = listener;
    }
    
    /**
     * MMLリスナを削除する。
     * @param listener MMLリスナリスナ
     */ 
    public void removeMMLListener(MmlMessageListener listener) {
        if (mmlListener != null && mmlListener == listener) {
            mmlListener = null;
        }
    }
    
    /**
     * CLAIMリスナを追加する。
     * @param listener CLAIMリスナ
     * @throws TooManyListenersException
     */
    public void addCLAIMListner(ClaimMessageListener listener)
    throws TooManyListenersException {
        if (claimListener != null) {
            throw new TooManyListenersException();
        }
        claimListener = listener;
    }
    
    /**
     * CLAIMリスナを削除する。
     * @param listener 削除するCLAIMリスナ
     */
    public void removeCLAIMListener(ClaimMessageListener listener) {
        if (claimListener != null && claimListener == listener) {
            claimListener = null;
        }
    }
    
    /**
     * 修正属性を設定する。
     * @param b 修正する時true
     */
    protected void setModify(boolean b) {
        modify = b;
    }
    
    /**
     * このエディタに切り替わった時メニューを制御する。
     */
    public void enter() {
        super.enter();
        stateMgr.controlMenu();
    }
    
    /**
     * dirty属性を設定する。
     * @param dirty
     */
    public void setDirty(boolean dirty) {
        boolean bdirty = (soaPane.isDirty() || pPane.isDirty()) ? true : false;
        stateMgr.setDirty(bdirty);
    }
    
    /**
     * Dirtyかどうかを返す。
     * @return dirtyの時true
     */
    public boolean isDirty() {
        return stateMgr.isDirty();
    }
    
    /**
     * 初期化する。
     */
    public void initialize() {
        
        stateMgr = new StateMgr();
        
        // TimeStampLabel を生成する
        timeStampLabel = new JLabel("TimeStamp");
        timeStampLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timeStampLabel.setForeground(timeStampFore);
        timeStampLabel.setFont(timeStampFont);
        
        // SOA Pane を生成する
        soaPane = new KartePane();
        soaPane.setParent(this);
        if (model != null) {
            // Schema 画像にファイル名を付けるのために必要
            String docId = model.getDocInfo().getDocId();
            soaPane.setDocId(docId);
        }
        
        // P Pane を生成する
        pPane = new KartePane();
        pPane.setParent(this);
        
        // 互いに関連ずける
        soaPane.setRole(ROLE_SOA, pPane);
        pPane.setRole(ROLE_P, soaPane);
        
        // TransferHandlerを設定する
        soaPane.getTextPane().setTransferHandler(new SOATransferHandler(soaPane));
        pPane.getTextPane().setTransferHandler(new PTransferHandler(pPane));
        
        // 2号カルテを生成する
        panel2 = new Panel2();
        panel2.setLayout(new BorderLayout());
        
        //JPanel flowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 0));
        JPanel flowPanel = new JPanel();
        flowPanel.setLayout(new BoxLayout(flowPanel, BoxLayout.X_AXIS));
        flowPanel.add(soaPane.getTextPane());
        flowPanel.add(javax.swing.Box.createHorizontalStrut(KartePane.PANE_DIVIDER_WIDTH));
        flowPanel.add(pPane.getTextPane());
                 
        //
        // TimeStamp + Health Insurances を表示する
        //       
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, timeStampSpacing));
        timePanel.add(timeStampLabel);
        //timePanel.add(Box.createHorizontalStrut(20));
        //timePanel.add(insBox);
        
        panel2.add(timePanel, BorderLayout.NORTH);
        panel2.add(flowPanel, BorderLayout.CENTER);
        
        JScrollPane scroller = new JScrollPane(panel2, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        getUI().setLayout(new BorderLayout());
        getUI().add(scroller, BorderLayout.CENTER);
        
//        JPanel sendInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 1));
//        sendClaimLabel = new JLabel(ClientContext.getImageIcon("calc_16.gif"));
//        sendClaimLabel.setToolTipText("診療行為の送信を行う設定になっています。");
//        sendInfoPanel.add(sendClaimLabel);
//        getUI().add(sendInfoPanel, BorderLayout.SOUTH);
        
        // 初期化の前にモデルがセットしてある。
        // Model を表示する
        displayModel();
    }
    
    /**
     * プログラムを開始する。初期化の後コールされる。
     */
    public void start() {
        // モデル表示後にリスナ等を設定する
        ChartMediator mediator = getContext().getChartMediator();
        soaPane.init(editable, mediator);
        pPane.init(editable, mediator);
        enter();
    }
    
    /**
     * DocumentModelを表示する。
     */
    private void displayModel() {
        
        // Timestamp を表示する
        Date now = new Date();
        timeStamp = ModelUtils.getDateAsFormatString(now, IInfoModel.KARTE_DATE_FORMAT);
        
        // 修正の場合
        if (modify) {
            // 更新: YYYY-MM-DDTHH:MM:SS (firstConfirmDate)
            StringBuilder buf = new StringBuilder();
            buf.append(UPDATE_TAB_TITLE);
            buf.append(": ");
            buf.append(timeStamp);
            buf.append(" [");
            buf.append(ModelUtils.getDateAsFormatString(model.getDocInfo().getFirstConfirmDate(), IInfoModel.KARTE_DATE_FORMAT));
            buf.append(" ]");
            timeStamp = buf.toString();
        }
        
        // 内容を表示する
        if (model.getModules() != null) {
            KarteRenderer_2 renderer = new KarteRenderer_2(soaPane, pPane);
            renderer.render(model);
        }
        
        //
        // 健康保険を表示する
        //
        PVTHealthInsuranceModel[] ins = null;
        
        //
        // コンテキストが EditotFrame の場合と Chart の場合がある
        //
        if (getContext() instanceof ChartPlugin) {
            ins = ((ChartPlugin) getContext()).getHealthInsurances();
        } else if (getContext() instanceof EditorFrame) {
            EditorFrame ef = (EditorFrame) getContext();
            ChartPlugin chart = (ChartPlugin) ef.getChart();
            ins = chart.getHealthInsurances();
        }
        
        //
        // Model に設定してある健康保険を選択する
        //
        String selecteIns = null;
        String insGUID = getModel().getDocInfo().getHealthInsuranceGUID();
        if (insGUID != null) {
            for (int i = 0; i < ins.length; i++) {
                String GUID = ins[i].getGUID();
                if (GUID != null && GUID.equals(insGUID)) {
                    selecteIns = ins[i].toString();
                    break;
                }
            }
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(timeStamp);
        if (selecteIns != null) {
            sb.append(" (");
            sb.append(selecteIns);
            sb.append(")");
        }
        
        timeStampLabel.setText(sb.toString());
        timeStampLabel.addMouseListener(new PopupListener());
        
        insuranceVisible = true;
    }
    
    public void applyInsurance(PVTHealthInsuranceModel hm) {
        
        getModel().getDocInfo().setHealthInsurance(hm.getInsuranceClassCode());
        //getModel().getDocInfo().setHealthInsuranceDesc(hm.getInsuranceClass());
        getModel().getDocInfo().setHealthInsuranceDesc(hm.toString());
        getModel().getDocInfo().setHealthInsuranceGUID(hm.getGUID());
        
        if (isInsuranceVisible()) {
            StringBuilder sb = new StringBuilder();
            sb.append(timeStamp);
            sb.append(" (");
            sb.append(hm.toString());
            sb.append(")");

            timeStampLabel.setText(sb.toString());
            timeStampLabel.revalidate();
        }
    }
    
    public void setInsuranceVisible(Boolean b) {
        
        boolean old = insuranceVisible;
        
        if (old != b) {
            
            insuranceVisible = b;
            
            StringBuilder sb = new StringBuilder();
            sb.append(timeStamp);
            
            if (b) {
                sb.append(" (");
                sb.append(getModel().getDocInfo().getHealthInsuranceDesc());
                sb.append(")");
            } 
            
            timeStampLabel.setText(sb.toString());
            timeStampLabel.revalidate();
        }
    }
    
    public boolean isInsuranceVisible() {
        return insuranceVisible;
    }
    
    class PopupListener extends MouseAdapter {

        public PopupListener() {
        }
        
        public void mouseClicked(MouseEvent e) {
            
            if (e.getClickCount() == 1) {
                boolean b = isInsuranceVisible();
                setInsuranceVisible(new Boolean(!b));
            }
            e.consume();
        }

//        public void mousePressed(MouseEvent e) {
//            maybeShowPopup(e);
//        }
//
//        public void mouseReleased(MouseEvent e) {
//            maybeShowPopup(e);
//        }
//
//        private void maybeShowPopup(MouseEvent e) {
//
//            if (e.isPopupTrigger()) {
//                
//                JPopupMenu popup = new JPopupMenu();
//                
//                ReflectActionListener ra =  null;
//                
//                if (isInsuranceVisible()) {
//                    ra = new ReflectActionListener(KarteEditor.this,
//                                                   "setInsuranceVisible", 
//                                                   new Class[]{Boolean.class}, 
//                                                   new Object[]{Boolean.FALSE});
//                } else {
//                    ra = new ReflectActionListener(KarteEditor.this,
//                                                   "setInsuranceVisible", 
//                                                   new Class[]{Boolean.class}, 
//                                                   new Object[]{Boolean.TRUE});
//                }
//                
//                JCheckBoxMenuItem mi = new JCheckBoxMenuItem("保険情報非表示");
//                mi.setSelected(!isInsuranceVisible());
//                mi.addActionListener(ra);
//                popup.add(mi);
//                
//                popup.show(e.getComponent(), e.getX(), e.getY());
//            }
//        }
    }
    
//    public boolean copyStamp() {
//            return pPane.copyStamp();
//    }
//
//    public void pasteStamp() {
//            pPane.pasteStamp();
//    }
    
    /**
     * 保存ダイアログを表示し保存時のパラメータを取得する。
     * @params sendMML MML送信フラグ 送信するとき true
     */
    private SaveParams getSaveParams(boolean joinAreaNetwork) {
        
        //
        // Title が設定されているか
        //
        String text = model.getDocInfo().getTitle();
        if(text == null || text.equals("")) {

            // SOAPane から最初の１５文字を文書タイトルとして取得する
            text = soaPane.getTitle();
            if ((text == null) || text.equals("")) {
                text = DEFAULT_TITLE;
            }
        }
        
        SaveParams params = null;
        
        //
        // 新規カルテで保存の場合
        // 仮保存から修正がかかっている場合
        // 修正の場合
        //
        DocInfoModel docInfo = getModel().getDocInfo();
        
        if (!modify && docInfo.getStatus().equals(IInfoModel.STATUS_NONE)) {
            logger.debug("saveFromNew");
            if (sendClaim) {
                sendClaim = Project.getSendClaimSave();
            }
            
        } else if (modify && docInfo.getStatus().equals(IInfoModel.STATUS_TMP)) {
            logger.debug("saveFromTmp");
            if (sendClaim) {
                sendClaim = Project.getSendClaimSave();
            }
            
        } else if (modify) {
            logger.debug("saveFromModify");
            if (sendClaim) {
                sendClaim = Project.getSendClaimModify();
            }
        }
        
        //
        // 確認ダイアログを表示するかどうか
        //
        if (Project.getPreferences().getBoolean(Project.KARTE_SHOW_CONFIRM_AT_SAVE, true)) {
            //
            // ダイアログを表示し、アクセス権等の保存時のパラメータを取得する
            //
            params = new SaveParams(joinAreaNetwork);
            params.setTitle(text);
            params.setDepartment(model.getDocInfo().getDepartmentDesc());
            
            // 印刷枚数をPreferenceから取得する
            Preferences prefs = Preferences.userNodeForPackage(this.getClass());
            int numPrint = prefs.getInt("karte.print.count", 0);
            params.setPrintCount(numPrint);
            
            //
            // CLAIM 送信
            //
            params.setSendClaim(sendClaim);

            SaveDialog sd = (SaveDialog) Project.createSaveDialog(getParentFrame(), params);
            sd.start();
            params = sd.getValue();

            // 印刷枚数を保存する
            if (params != null) {
                prefs.putInt("karte.print.count", params.getPrintCount());
            }
            
        } else {
            
            //
            // 確認ダイアログを表示しない
            //
            params = new SaveParams(false);
            params.setTitle(text);
            params.setDepartment(model.getDocInfo().getDepartmentDesc());
            params.setPrintCount(Project.getPreferences().getInt(Project.KARTE_PRINT_COUNT, 0));
            
            //
            // 仮保存が指定されている端末の場合
            //
            int sMode = Project.getPreferences().getInt(Project.KARTE_SAVE_ACTION, 0);
            boolean tmpSave = sMode == 1 ? true : false;
            params.setTmpSave(tmpSave);
            if (tmpSave) {
                params.setSendClaim(false);
            } else {
                //
                // 保存が実行される端末の場合
                //
                params.setSendClaim(sendClaim);
            }
            
            //
            // 患者参照、施設参照不可
            //
            params.setAllowClinicRef(false);
            params.setAllowPatientRef(false);
        }
        
        return params;
    }
    
    /**
     * 編集したDocumentModelを保存する。
     */
    public void save() {
        
        try {
            // 何も書かれていない時はリターンする
            if (!stateMgr.isDirty()) {
                return;
            }
            
            // MML送信用のマスタIDを取得する
            // ケース１ HANIWA 方式 facilityID + patientID
            // ケース２ HIGO 方式 地域ID を使用
            ID masterID = Project.getMasterId(getContext().getPatient().getPatientId());
            if (masterID == null) {
            }
            
            sendMml = (Project.getSendMML() && masterID != null && mmlListener != null)
            ? true
            : false;
            
            //
            // この段階での CLAIM 送信 = 診療行為送信かつclaimListener!=null
            //
            sendClaim = (Project.getSendClaim() && claimListener != null) ? true : false;
            
            // 保存ダイアログを表示し、パラメータを得る
            // 地域連携に参加もしくはMML送信を行う場合は患者及び診療歴のある施設への参照許可
            // パラメータが設定できるようにする
            // boolean karteKey = (Project.getJoinAreaNetwork() || sendMml) ? true : false;
            // 地域連携に参加する場合のみに変更する
            SaveParams params = getSaveParams(Project.getJoinAreaNetwork());
            
            //
            // キャンセルの場合はリターンする
            //
            if (params != null) {
                //
                // 次のステージを実行する
                //
                save2(params);
            }
            
        } catch (DolphinException e) {
           e.printStackTrace();
       }
    }
    
    /**
     * 保存処理の主な部分を実行する。
     **/
    private void save2(final SaveParams params) throws DolphinException {
        
        //
        // DocInfoに値を設定する
        //
        final DocInfoModel docInfo = model.getDocInfo();
        
        // 現在時刻を ConfirmDate にする
        Date confirmed = new Date();
        docInfo.setConfirmDate(confirmed);
        
        //
        // 修正でない場合は FirstConfirmDate = ConfirmDate にする
        // 修正の場合は FirstConfirmDate は既に設定されている
        // 修正でない新規カルテは parentId = null である
        //
        if (docInfo.getParentId() == null) {
            docInfo.setFirstConfirmDate(confirmed);
        }
        
        //
        // Status 仮保存か確定保存かを設定する
        // final の時は CLAIM 送信するが前の状態に依存する
        //
        if (!params.isTmpSave()) {
            // 
            // 編集が開始された時の state を取得する
            //
            String oldStatus = docInfo.getStatus();
            
            if (oldStatus.equals(STATUS_NONE)) {
                //
                // NONEから確定への遷移 newSave
                //
                sendClaim = params.isSendClaim();
                logger.debug("NONEから確定 : " + sendClaim);
                
            } else if (oldStatus.equals(STATUS_TMP)) {
                //
                // 仮保存から確定へ遷移する場合   saveFromTmp
                // 同日の場合だけ CLIAM 送信する
                //
                //String first = ModelUtils.getDateAsString(docInfo.getFirstConfirmDate());
                //String cd = ModelUtils.getDateAsString(docInfo.getConfirmDate());
                //if (first.equals(cd)) {
                    //sendClaim = params.isSendClaim();
                //} else {
                    //sendClaim = false;
                //}
                sendClaim = params.isSendClaim();
                logger.debug("仮保存から確定 : " + sendClaim);
                
            } else {
                //
                // 確定から確定（修正の場合に相当する）以前は sendClaim = false;
                //
                sendClaim = params.isSendClaim();   //sendClaim && Project.getSendClaimModify();
                
                logger.debug("修正 : " + sendClaim);
            }
            
            //
            // 保存時の state を final にセットする
            //
            docInfo.setStatus(STATUS_FINAL);
            
        } else {
            //
            // 仮保存の場合 CLAIM 送信しない
            //
            sendClaim = false;
            logger.debug("仮保存 : " + sendClaim);
            
            sendMml = false;
            docInfo.setStatus(STATUS_TMP);
        }
        
        // titleを設定する
        docInfo.setTitle(params.getTitle());
        
        // デフォルトのアクセス権を設定をする TODO
        AccessRightModel ar = new AccessRightModel();
        ar.setPermission(PERMISSION_ALL);
        ar.setLicenseeCode(ACCES_RIGHT_CREATOR);
        ar.setLicenseeName(ACCES_RIGHT_CREATOR_DISP);
        ar.setLicenseeCodeType(ACCES_RIGHT_FACILITY_CODE);
        docInfo.addAccessRight(ar);
        
        // 患者のアクセス権を設定をする
        if (params.isAllowPatientRef()) {
            ar = new AccessRightModel();
            ar.setPermission(PERMISSION_READ);
            ar.setLicenseeCode(ACCES_RIGHT_PATIENT);
            ar.setLicenseeName(ACCES_RIGHT_PATIENT_DISP);
            ar.setLicenseeCodeType(ACCES_RIGHT_PERSON_CODE);
            docInfo.addAccessRight(ar);
        }
        
        // 診療履歴のある施設のアクセス権を設定をする
        if (params.isAllowClinicRef()) {
            ar = new AccessRightModel();
            ar.setPermission(PERMISSION_READ);
            ar.setLicenseeCode(ACCES_RIGHT_EXPERIENCE);
            ar.setLicenseeName(ACCES_RIGHT_EXPERIENCE_DISP);
            ar.setLicenseeCodeType(ACCES_RIGHT_EXPERIENCE_CODE);
            docInfo.addAccessRight(ar);
        }
        
        // ProgressCourseModule の ModuleInfo を保存しておく
        ModuleInfoBean[] progressInfo = model.getModuleInfo(MODULE_PROGRESS_COURSE);
        if (progressInfo == null) {
            // 存在しない場合は新規に作成する
            progressInfo = new ModuleInfoBean[2];
            ModuleInfoBean mi = new ModuleInfoBean();
            mi.setStampName(MODULE_PROGRESS_COURSE);
            mi.setEntity(MODULE_PROGRESS_COURSE);
            mi.setStampRole(ROLE_SOA_SPEC);
            progressInfo[0] = mi;
            mi = new ModuleInfoBean();
            mi.setStampName(MODULE_PROGRESS_COURSE);
            mi.setEntity(MODULE_PROGRESS_COURSE);
            mi.setStampRole(ROLE_P_SPEC);
            progressInfo[1] = mi;
        }
        
        //
        // モデルのモジュールをヌルに設定する
        // エディタの画面をダンプして生成したモジュールを設定する
        //
        model.clearModules();
        model.clearSchema();
        
        //
        // SOAPane をダンプし model に追加する
        // 
        KartePaneDumper_2 dumper = new KartePaneDumper_2();
        KarteStyledDocument doc = (KarteStyledDocument) soaPane.getTextPane().getDocument();
        dumper.dump(doc);
        ModuleModel[] soa = dumper.getModule();
        if (soa != null && soa.length > 0) {
            model.addModule(soa);
        }
        
        // ProgressCourse SOA を生成する
        ProgressCourse pc = new ProgressCourse();
        pc.setFreeText(dumper.getSpec());
        ModuleModel progressSoa = new ModuleModel();
        progressSoa.setModuleInfo(progressInfo[0]);
        progressSoa.setModel(pc);
        model.addModule(progressSoa);
        
        // 
        // Schema を追加する
        //      
        int maxImageWidth = ClientContext.getInt("image.max.width");
        int maxImageHeight = ClientContext.getInt("image.max.height");
        Dimension maxSImageSize = new Dimension(maxImageWidth, maxImageHeight);
        SchemaModel[] schemas = dumper.getSchema();
        if (schemas != null && schemas.length > 0) {
            // 保存のため Icon を JPEG に変換する
            for (SchemaModel schema : schemas) {
                ImageIcon icon = schema.getIcon();
                icon = adjustImageSize(icon, maxSImageSize);
                byte[] jpegByte = getJPEGByte(icon.getImage());
                schema.setJpegByte(jpegByte);
                schema.setIcon(null);
                model.addSchema(schema);
            }
        }
        
        //
        // PPane をダンプし model に追加する
        // 
        dumper = new KartePaneDumper_2();
        doc = (KarteStyledDocument) pPane.getTextPane().getDocument();
        dumper.dump((DefaultStyledDocument) pPane.getTextPane().getDocument());
        ModuleModel[] plan = dumper.getModule();
        
        if (plan != null && plan.length > 0) {
            model.addModule(plan);
        } else {
            sendClaim = false;
        }
        
        // ProgressCourse P を生成する
        pc = new ProgressCourse();
        pc.setFreeText(dumper.getSpec());
        ModuleModel progressP = new ModuleModel();
        progressP.setModuleInfo(progressInfo[1]);
        progressP.setModel(pc);
        model.addModule(progressP);
        
        // FLAGを設定する
        // image があるかどうか
        boolean flag = model.getSchema() != null ? true : false;
        docInfo.setHasImage(flag);
        
        // RP があるかどうか
        flag = model.getModule(ENTITY_MED_ORDER) != null ? true : false;
        docInfo.setHasRp(flag);
        
        // 処置があるかどうか
        flag = model.getModule(ENTITY_TREATMENT) != null ? true : false;
        docInfo.setHasTreatment(flag);
        
        // LaboTest があるかどうか
        flag = model.getModule(ENTITY_LABO_TEST) != null ? true : false;
        docInfo.setHasLaboTest(flag);
        
        //
        // EJB3.0 Model の関係を構築する
        //
        // confirmed, firstConfirmed は設定済み
        KarteBean karte = getContext().getKarte();
        model.setKarte(karte);                          // karte
        model.setCreator(Project.getUserModel());       // 記録者
        model.setRecorded(docInfo.getConfirmDate());    // 記録日
        
        // Moduleとの関係を設定する
        Collection<ModuleModel> moduleBeans = model.getModules();
        int number = 0;
        int totalSize = 0;
        for (ModuleModel bean : moduleBeans) {
            bean.setId(0L);                             // unsaved-value
            bean.setKarte(karte);                       // Karte
            bean.setCreator(Project.getUserModel());    // 記録者
            bean.setDocument(model);                    // Document
            bean.setConfirmed(docInfo.getConfirmDate());            // 確定日
            bean.setFirstConfirmed(docInfo.getFirstConfirmDate());  // 適合開始日
            bean.setRecorded(docInfo.getConfirmDate());             // 記録日
            bean.setStatus(STATUS_FINAL);                           // status
            bean.setBeanBytes(BeanUtils.getXMLBytes(bean.getModel()));
             
            // ModuleInfo を設定する
            // Name, Role, Entity は設定されている
            ModuleInfoBean mInfo = bean.getModuleInfo();
            mInfo.setStampNumber(number++);
            
            int size = bean.getBeanBytes().length / 1024;
            logger.debug("stamp size(KB) = " + size);
            totalSize += size;
        }
        logger.debug("stamp total size(KB) = " + totalSize);
        totalSize = 0;
        
        // 画像との関係を設定する
        number = 0;
        Collection<SchemaModel> imagesimages = model.getSchema();
        if (imagesimages != null && imagesimages.size() > 0) {
            for (SchemaModel bean : imagesimages) {
                bean.setId(0L);                                         // unsaved
                bean.setKarte(karte);                                   // Karte
                bean.setCreator(Project.getUserModel());                // Creator
                bean.setDocument(model);                                // Document
                bean.setConfirmed(docInfo.getConfirmDate());            // 確定日
                bean.setFirstConfirmed(docInfo.getFirstConfirmDate());  // 適合開始日
                bean.setRecorded(docInfo.getConfirmDate());             // 記録日
                bean.setStatus(STATUS_FINAL);                           // Status
                bean.setImageNumber(number++);
                
                ExtRefModel ref = bean.getExtRef();
                StringBuilder sb = new StringBuilder();
                sb.append(model.getDocInfo().getDocId());
                sb.append("-");
                sb.append(number++);
                sb.append(".jpg");
                ref.setHref(sb.toString());
                
                int size = bean.getJpegByte().length / 1024;
                logger.debug("schema size(KB) = " + size);
                totalSize += size;
            }
            logger.debug("total schema size(KB) = " + totalSize);
        }
        
        // 保存タスクを開始する
        final IStatusPanel statusPanel = getContext().getStatusPanel();
        
        final DocumentDelegater ddl = new DocumentDelegater();
        
        // Worker を生成する
        int maxEstimation = ClientContext.getInt("task.default.maxEstimation");
        int delay = ClientContext.getInt("task.default.delay");
        final SaveTask worker = new SaveTask(model, ddl, maxEstimation/delay);
        
        // タイマーを起動する
        taskTimer = new javax.swing.Timer(delay, new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                
                worker.getCurrent();
                statusPanel.setMessage(worker.getMessage());
                
                if (worker.isDone()) {
                    // 保存後の処理を行う
                    statusPanel.stop();
                    taskTimer.stop();
                    
                    //long putCode = worker.getResult();
                    
                    if (ddl.isNoError()) {
                        // 印刷
                        int copies = params.getPrintCount();
                        if (copies > 0) {
                            statusPanel.setMessage(TASK_MSG[3]);
                            printPanel2(getContext().getContext().getPageFormat(), copies, false);
                        }
                        
                        statusPanel.stop(TASK_MSG[1]);
                        
                        // 編集不可に設定する
                        soaPane.setEditableProp(false);
                        pPane.setEditableProp(false);
                        
                        // 状態遷移する
                        stateMgr.setSaved(true);

                        //
                        // Chart の状態を設定する
                        //
                        if (docInfo.getStatus().equals(STATUS_TMP)) {
                            getContext().setChartState(ChartPlugin.OPEN_NONE);
                            
                        } else if (docInfo.getStatus().equals(STATUS_FINAL)) {
                            getContext().setChartState(ChartPlugin.OPEN_SAVE);
                        }
                        
                        //
                        // 文書履歴の更新を通知する
                        //
                        getContext().getDocumentHistory().getDocumentHistory();
                        
                        
                        
                    } else {
                        // エラーを表示する
                        JFrame parent = getContext().getFrame();
                        String title = ClientContext.getString("karte.task.saveTitle");
                        JOptionPane.showMessageDialog(
                                parent,
                                ddl.getErrorMessage(),
                                ClientContext.getFrameTitle(title),
                                JOptionPane.WARNING_MESSAGE);
                    }
                    
                } else if (worker.isTimeOver()) {
                    statusPanel.stop();
                    taskTimer.stop();
                    JFrame parent = getContext().getFrame();
                    String title = ClientContext.getString("karte.task.saveTitle");
                    new TimeoutWarning(parent, title, null).start();
                }
            }
        });
        statusPanel.start("");
        worker.start();
        taskTimer.start();
    }
    
    /**
     * Courtesy of Junzo SATO
     */
    private byte[] getJPEGByte(Image image) {
        
        byte[] ret = null;
        BufferedOutputStream writer = null;
        
        try {

            JPanel myPanel = getUI();
            Dimension d = new Dimension(image.getWidth(myPanel), image.getHeight(myPanel));
            BufferedImage bf = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
            Graphics g = bf.getGraphics();
            g.setColor(Color.white);
            g.drawImage(image, 0, 0, d.width, d.height, myPanel);
            
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            writer = new BufferedOutputStream(bo);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(writer);
            encoder.encode(bf);
            writer.flush();
            writer.close();
            ret = bo.toByteArray();
            
        } catch (IOException e) {
            e.printStackTrace();
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e2) {
                }
            }
        }
        return ret;
    }
    
        
    private ImageIcon adjustImageSize(ImageIcon icon, Dimension dim) {
        
        if ( (icon.getIconHeight() > dim.height) ||
                (icon.getIconWidth() > dim.width) ) {
            Image img = icon.getImage();
            float hRatio = (float)icon.getIconHeight() / dim.height;
            float wRatio = (float)icon.getIconWidth() / dim.width;
            int h, w;
            if (hRatio > wRatio) {
                h = dim.height;
                w = (int)(icon.getIconWidth() / hRatio);
            } else {
                w = dim.width;
                h = (int)(icon.getIconHeight() / wRatio);
            }
            img = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } else {
            return icon;
        }
    }
    
    ////////////////////////////////////////////////////////////////////
    
    /**
     * カルテの保存を実行するタスククラス。
     */
    protected class SaveTask extends AbstractInfiniteTask {
        
        private DocumentModel model;
        private DocumentDelegater ddl;
        private long putCode;
        
        public SaveTask(DocumentModel model, DocumentDelegater ddl, int taskLength) {
            this.model = model;
            this.ddl = ddl;
            setTaskLength(taskLength);
        }
        
        protected long getResult() {
            return putCode;
        }
        
        protected void doTask() {
            
            message = TASK_MSG[0];
            
            // 最初にデータベースへ保存する
            putCode = ddl.putKarte(model);
            
            if (ddl.isNoError()) {
                // 成功した場合
                // CLAIM送信を行う
                if (sendClaim && claimListener != null) {
                    sendClaim();
                }
                
                // MML送信を行う
                if (Project.getJoinAreaNetwork() || sendMml) {
                    sendMml();
                }
                
                message = TASK_MSG[1];
                
            } else {
                message = TASK_MSG[2];
            }
            
            setDone(true);
        }
        
        /**
         * CLAIM 送信を行う。
         *
         */
        private void sendClaim() {
            
            // ヘルパークラスを生成しVelocityが使用するためのパラメータを設定する
            ClaimHelper helper = new ClaimHelper();
            DocInfoModel docInfo = model.getDocInfo();
            Collection<ModuleModel> modules = model.getModules();
            
            String confirmedStr = ModelUtils.getDateTimeAsString(docInfo.getConfirmDate());
            helper.setConfirmDate(confirmedStr);
            helper.setCreatorId(model.getCreator().getUserId());
            helper.setCreatorName(model.getCreator().getCommonName());
            helper.setCreatorDept(docInfo.getDepartment());
            helper.setCreatorDeptDesc(docInfo.getDepartmentDesc());
            helper.setCreatorLicense(model.getCreator().getLicenseModel().getLicense());
            helper.setPatientId(model.getKarte().getPatient().getPatientId());
            helper.setGenerationPurpose(docInfo.getPurpose());
            helper.setDocId(docInfo.getDocId());
            helper.setHealthInsuranceGUID(docInfo.getHealthInsuranceGUID());
            helper.setHealthInsuranceClassCode(docInfo.getHealthInsurance());
            helper.setHealthInsuranceDesc(docInfo.getHealthInsuranceDesc());
            
            // 保存する KarteModel の全モジュールをチェックし
            // それが ClaimBundle ならヘルパーへ追加する
            for (ModuleModel module : modules) {
                IInfoModel model = module.getModel();
                if (model instanceof ClaimBundle) {
                    helper.addClaimBundle((ClaimBundle) model);
                }
            }
            
            MessageBuilder mb = new MessageBuilder();
            String claimMessage = mb.build(helper);
            ClaimMessageEvent cvt = new ClaimMessageEvent(this);
            cvt.setClaimInstance(claimMessage);
            
            cvt.setPatientId(model.getKarte().getPatient().getPatientId());
            cvt.setPatientName(model.getKarte().getPatient().getFullName());
            cvt.setPatientSex(model.getKarte().getPatient().getGender());
            
            cvt.setTitle(model.getDocInfo().getTitle());
            cvt.setConfirmDate(confirmedStr);
            
            // debug 出力を行う
            if (ClientContext.getLogger("claim") != null) {
                ClientContext.getLogger("claim").debug(claimMessage);
            }
            
            if (claimListener != null) {
                claimListener.claimMessageEvent(cvt);
            }
        }
        
        /**
         * MML送信を行う
         */
        private void sendMml() {
            
            // MML Message を生成する
            MMLHelper mb = new MMLHelper();
            mb.setDocument(model);
            mb.setUser(Project.getUserModel());
            mb.setPatientId(getContext().getPatient().getPatientId());
            mb.buildText();
            
            try {
                VelocityContext context = ClientContext.getVelocityContext();
                context.put("mmlHelper", mb);

                // このスタンプのテンプレートファイルを得る
                String templateFile = "mml2.3Helper.vm";

                // Merge する
                StringWriter sw = new StringWriter();
                BufferedWriter bw = new BufferedWriter(sw);
                InputStream instream = ClientContext.getTemplateAsStream(templateFile);
                BufferedReader reader = new BufferedReader(new InputStreamReader(instream, "SHIFT_JIS"));
                Velocity.evaluate(context, bw, "mml", reader);
                bw.flush();
                bw.close();
                reader.close();
                String mml = sw.toString();
                //System.out.println(mml);
                
                // debug出力を行う
                if (ClientContext.getLogger("mml") != null) {
                    ClientContext.getLogger("mml").debug(mml);
                }
                
                if (sendMml && mmlListener != null) {
                    MmlMessageEvent mevt = new MmlMessageEvent(this);
                    mevt.setGroupId(mb.getDocId());
                    mevt.setMmlInstance(mml);
                    if (mb.getSchema() != null) {
                        mevt.setSchema(mb.getSchema());
                    }
                    mmlListener.mmlMessageEvent(mevt);
                }
                
                if (Project.getJoinAreaNetwork()) {
                    // TODO
                }
                

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    // //////////////////////////////////////////////////////////////////
    
    /**
     * このエディタの抽象状態クラス
     */
    protected abstract class EditorState {
        
        public EditorState() {
        }
        
        public abstract boolean isDirty();
        
        public abstract void controlMenu();
    }
    
    /**
     * No dirty 状態クラス
     */
    protected final class NoDirtyState extends EditorState {
        
        public NoDirtyState() {
        }
        
        public void controlMenu() {
            ChartMediator mediator = getContext().getChartMediator();
            mediator.getAction(GUIConst.ACTION_SAVE).setEnabled(false); // 保存
            mediator.getAction(GUIConst.ACTION_PRINT).setEnabled(false); // 印刷
            mediator.getAction(GUIConst.ACTION_CUT).setEnabled(false);
            mediator.getAction(GUIConst.ACTION_COPY).setEnabled(false);
            mediator.getAction(GUIConst.ACTION_PASTE).setEnabled(false);
            mediator.getAction(GUIConst.ACTION_UNDO).setEnabled(false);
            mediator.getAction(GUIConst.ACTION_REDO).setEnabled(false);
            mediator.getAction(GUIConst.ACTION_INSERT_TEXT).setEnabled(false);
            mediator.getAction(GUIConst.ACTION_INSERT_SCHEMA).setEnabled(false);
            mediator.getAction(GUIConst.ACTION_INSERT_STAMP).setEnabled(false);
            mediator.getAction(GUIConst.ACTION_SELECT_INSURANCE).setEnabled(!modify);
        }
        
        public boolean isDirty() {
            return false;
        }
    }
    
    /**
     * Dirty 状態クラス
     */
    protected final class DirtyState extends EditorState {
        
        public DirtyState() {
        }
        
        public void controlMenu() {
            ChartMediator mediator = getContext().getChartMediator();
            mediator.getAction(GUIConst.ACTION_SAVE).setEnabled(true);
            mediator.getAction(GUIConst.ACTION_PRINT).setEnabled(true);
            mediator.getAction(GUIConst.ACTION_SELECT_INSURANCE).setEnabled(!modify);
        }
        
        public boolean isDirty() {
            return true;
        }
    }
    
    /**
     * EmptyNew 状態クラス
     */
    protected final class SavedState extends EditorState {
        
        public SavedState() {
        }
        
        public void controlMenu() {
            ChartMediator mediator = getContext().getChartMediator();
            mediator.getAction(GUIConst.ACTION_SAVE).setEnabled(false);
            mediator.getAction(GUIConst.ACTION_PRINT).setEnabled(true);
            mediator.getAction(GUIConst.ACTION_CUT).setEnabled(false);
            mediator.getAction(GUIConst.ACTION_COPY).setEnabled(false);
            mediator.getAction(GUIConst.ACTION_PASTE).setEnabled(false);
            mediator.getAction(GUIConst.ACTION_UNDO).setEnabled(false);
            mediator.getAction(GUIConst.ACTION_REDO).setEnabled(false);
            mediator.getAction(GUIConst.ACTION_INSERT_TEXT).setEnabled(false);
            mediator.getAction(GUIConst.ACTION_INSERT_SCHEMA).setEnabled(false);
            mediator.getAction(GUIConst.ACTION_INSERT_STAMP).setEnabled(false);
            mediator.getAction(GUIConst.ACTION_SELECT_INSURANCE).setEnabled(false);
        }
        
        public boolean isDirty() {
            return false;
        }
    }
    
    /**
     * 状態マネージャ
     */
    protected final class StateMgr {
        
        private EditorState noDirtyState = new NoDirtyState();
        
        private EditorState dirtyState = new DirtyState();
        
        private EditorState savedState = new SavedState();
        
        private EditorState currentState;
        
        public StateMgr() {
            currentState = noDirtyState;
        }
        
        public boolean isDirty() {
            return currentState.isDirty();
        }
        
        public void setDirty(boolean dirty) {
            currentState = dirty ? dirtyState : noDirtyState;
            currentState.controlMenu();
        }
        
        public void setSaved(boolean saved) {
            if (saved) {
                currentState = savedState;
                currentState.controlMenu();
            }
        }
        
        public void controlMenu() {
            currentState.controlMenu();
        }
    }
}