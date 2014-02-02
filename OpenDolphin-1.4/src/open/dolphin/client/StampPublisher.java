package open.dolphin.client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.WindowConstants;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import open.dolphin.delegater.StampDelegater;
import open.dolphin.helper.GridBagBuilder;
import open.dolphin.helper.SimpleWorker;
import open.dolphin.helper.WorkerService;
import open.dolphin.infomodel.FacilityModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.IStampTreeModel;
import open.dolphin.infomodel.ModelUtils;
import open.dolphin.infomodel.StampTreeModel;
import open.dolphin.project.Project;

/**
 * StampTreePublisher
 *
 * @author Kazushi, Minagawa
 *
 */
public class StampPublisher {
    
    public enum PublishedState {NONE, SAVED_NONE, LOCAL, GLOBAL};
    
    private static final int TT_NONE = -1;
    private static final int TT_LOCAL = 0;
    private static final int TT_PUBLIC = 1;
    
    private StampBoxPlugin stampBox;
    private String title = "スタンプ公開";
    
    private JFrame dialog;
    private BlockGlass blockGlass;
    private JLabel infoLable;
    private JLabel instLabel;
    private JLabel publishedDate;
    private JTextField stampBoxName;
    private JTextField partyName;
    private JTextField contact;
    private JTextField description;
    private JRadioButton local;
    private JRadioButton publc;
    private JButton publish;
    private JButton cancel;
    private JButton cancelPublish;
    
    private JCheckBox[] entities;
    
    private JComboBox category;
    
    private int publishType = TT_NONE;
    private boolean okState;
            
    private StampDelegater sdl;
    
    private PublishedState publishState;

    // timerTask 関連
    private javax.swing.Timer taskTimer;
    private ProgressMonitor monitor;
    private int delayCount;
    private int maxEstimation = 90*1000;    // 90 秒
    private int delay = 300;                // 300 mmsec
    
    
    public StampPublisher(StampBoxPlugin stampBox) {
        this.stampBox = stampBox;
    }
    
    public void start() {

        Runnable awt = new Runnable() {

            @Override
            public void run() {

                stampBox.getBlockGlass().block();

                dialog = new JFrame(ClientContext.getFrameTitle(title));
                dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                dialog.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        stop();
                    }
                });
                JPanel contentPane = createContentPane();
                contentPane.setOpaque(true);
                dialog.setContentPane(contentPane);
                dialog.pack();

                Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
                int n = ClientContext.isMac() ? 3 : 2;
                int x = (screen.width - dialog.getPreferredSize().width) / 2;
                int y = (screen.height - dialog.getPreferredSize().height) / n;
                dialog.setLocation(y, y);

                blockGlass = new BlockGlass();
                dialog.setGlassPane(blockGlass);
                dialog.setVisible(true);
            }
        };
        
        SwingUtilities.invokeLater(awt);
    }
    
    public void stop() {
        dialog.setVisible(false);
        dialog.dispose();
        stampBox.getBlockGlass().unblock();
    }
    
    private JPanel createContentPane() {
        
        JPanel contentPane = new JPanel();
        
        // GUIコンポーネントを生成する
        infoLable = new JLabel(ClientContext.getImageIcon("about_16.gif"));
        instLabel = new JLabel("");
        instLabel.setFont(new Font("Dialog", Font.PLAIN, ClientContext.getInt("watingList.state.font.size")));
        publishedDate = new JLabel("");
        
        stampBoxName = GUIFactory.createTextField(15, null, null, null);
        partyName = GUIFactory.createTextField(20, null, null, null);
        contact = GUIFactory.createTextField(30, null, null, null);
        description = GUIFactory.createTextField(30, null, null, null);
        local = new JRadioButton(IInfoModel.PUBLISH_TREE_LOCAL);
        publc = new JRadioButton(IInfoModel.PUBLISH_TREE_PUBLIC);
        publish = new JButton("");
        publish.setEnabled(false);
        cancelPublish = new JButton("公開を止める");
        cancelPublish.setEnabled(false);
        cancel = new JButton("ダイアログを閉じる");
        entities = new JCheckBox[IInfoModel.STAMP_NAMES.length];
        for (int i = 0; i < IInfoModel.STAMP_NAMES.length; i++) {
            entities[i] = new JCheckBox(IInfoModel.STAMP_NAMES[i]);
            if (IInfoModel.STAMP_NAMES[i].equals(IInfoModel.TABNAME_ORCA)) {
                entities[i].setEnabled(false);
            }
        }
        JPanel chkPanel1 = GUIFactory.createCheckBoxPanel(new JCheckBox[]{entities[0], entities[1], entities[2], entities[3], entities[4], entities[5], entities[6], entities[7]});
        JPanel chkPanel2 = GUIFactory.createCheckBoxPanel(new JCheckBox[]{entities[8], entities[9], entities[10], entities[11], entities[12], entities[13], entities[14], entities[15]});
        
        String[] categories = ClientContext.getStringArray("stamp.publish.categories");
        category = new JComboBox(categories);
        JPanel categoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        categoryPanel.add(category);
        
        // 公開先RadioButtonパネル
        JPanel radioPanel = GUIFactory.createRadioPanel(new JRadioButton[]{local, publc});
        
        // 属性設定パネル
        GridBagBuilder gbl = new GridBagBuilder("スタンプ公開設定");
        
        int y = 0;
        gbl.add(infoLable, 0, y, GridBagConstraints.EAST);
        gbl.add(instLabel, 1, y, GridBagConstraints.WEST);
        
        y++;
        gbl.add(new JLabel("公開スタンプセット名"), 0, y, GridBagConstraints.EAST);
        gbl.add(stampBoxName, 1, y, GridBagConstraints.WEST);
        
        y++;
        gbl.add(new JLabel("公開先"), 0, y, GridBagConstraints.EAST);
        gbl.add(radioPanel, 1, y, GridBagConstraints.WEST);
        
        y++;
        gbl.add(new JLabel("カテゴリ"), 0, y, GridBagConstraints.EAST);
        gbl.add(categoryPanel, 1, y, GridBagConstraints.WEST);
        
        y++;
        gbl.add(new JLabel("公開するスタンプ"), 0, y, GridBagConstraints.EAST);
        gbl.add(chkPanel1, 1, y, GridBagConstraints.WEST);
        
        y++;
        gbl.add(new JLabel(" "), 0, y, GridBagConstraints.EAST);
        gbl.add(chkPanel2, 1, y, GridBagConstraints.WEST);
        
        y++;
        gbl.add(new JLabel("公開者名"), 0, y, GridBagConstraints.EAST);
        gbl.add(partyName, 1, y, GridBagConstraints.WEST);
        
        y++;
        gbl.add(new JLabel("URL等"), 0, y, GridBagConstraints.EAST);
        gbl.add(contact, 1, y, GridBagConstraints.WEST);
        
        y++;
        gbl.add(new JLabel("利用者への説明"), 0, y, GridBagConstraints.EAST);
        gbl.add(description, 1, y, GridBagConstraints.WEST);
        
        y++;
        gbl.add(new JLabel("公開日"), 0, y, GridBagConstraints.EAST);
        gbl.add(publishedDate, 1, y, GridBagConstraints.WEST);
        
        // コマンドパネル
        JPanel cmdPanel = null;
        if (ClientContext.isMac()) {
            cmdPanel = GUIFactory.createCommandButtonPanel(new JButton[]{cancel, cancelPublish, publish});
        } else {
            cmdPanel = GUIFactory.createCommandButtonPanel(new JButton[]{publish, cancelPublish, cancel});
        }
        
        // 配置する
        contentPane.setLayout(new BorderLayout(0, 17));
        contentPane.add(gbl.getProduct(), BorderLayout.CENTER);
        contentPane.add(cmdPanel, BorderLayout.SOUTH);
        contentPane.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
        
        // PublishState に応じて振り分ける
        IStampTreeModel stmpTree = stampBox.getUserStampBox().getStampTreeModel();
        FacilityModel facility = Project.getUserModel().getFacilityModel();
        String facilityId = facility.getFacilityId();
        long treeId = stmpTree.getId();
        String publishTypeStr = stmpTree.getPublishType();
        
        if (treeId == 0L && publishTypeStr == null) {
            //
            // Stamptree非保存（最初のログイン時）
            //
            publishState = PublishedState.NONE;
        } else if (treeId != 0L && publishTypeStr == null) {
            //
            // 保存されているStamptreeで非公開のケース
            //
            publishState = PublishedState.SAVED_NONE;
        } else if (treeId != 0L && publishTypeStr != null && publishTypeStr.equals(facilityId)) {
            //
            // publishType=facilityId ローカルに公開されている
            //
            publishState = PublishedState.LOCAL;
        } else if (treeId != 0L && publishTypeStr != null && publishTypeStr.equals(IInfoModel.PUBLISHED_TYPE_GLOBAL)) {
            //
            // publishType=global グローバルに公開されている
            //
            publishState = PublishedState.GLOBAL;
        }
        
        // GUIコンポーネントに初期値を入力する
        switch (publishState) {
            
            case NONE:
                instLabel.setText("このスタンプは公開されていません。");
                partyName.setText(facility.getFacilityName());
                String url = facility.getUrl();
                if (url != null) {
                    contact.setText(url);
                }
                String dateStr = ModelUtils.getDateAsString(new Date());
                publishedDate.setText(dateStr);
                publish.setText("公開する");
                break;
                
            case SAVED_NONE:
                instLabel.setText("このスタンプは公開されていません。");
                partyName.setText(stmpTree.getPartyName());
                url = facility.getUrl();
                if (url != null) {
                    contact.setText(url);
                }
                dateStr = ModelUtils.getDateAsString(new Date());
                publishedDate.setText(dateStr);
                publish.setText("公開する");
                break;
                
            case LOCAL:
                instLabel.setText("このスタンプは院内に公開されています。");
                stampBoxName.setText(stmpTree.getName());
                local.setSelected(true);
                publc.setSelected(false);
                publishType = TT_LOCAL;
                
                //
                // Publish している Entity をチェックする
                //
                String published = ((StampTreeModel) stmpTree).getPublished();
                if (published != null) {
                    StringTokenizer st = new StringTokenizer(published, ",");
                    while (st.hasMoreTokens()) {
                        String entity = st.nextToken();
                        for (int i = 0; i < IInfoModel.STAMP_ENTITIES.length; i++) {
                            if (entity.equals(IInfoModel.STAMP_ENTITIES[i])) {
                                entities[i].setSelected(true);
                                break;
                            }
                        }
                    }
                }
                
                category.setSelectedItem(stmpTree.getCategory());
                partyName.setText(stmpTree.getPartyName());
                contact.setText(stmpTree.getUrl());
                description.setText(stmpTree.getDescription());
                StringBuilder sb = new StringBuilder();
                sb.append(ModelUtils.getDateAsString(stmpTree.getPublishedDate()));
                sb.append("  最終更新日( ");
                sb.append(ModelUtils.getDateAsString(stmpTree.getLastUpdated()));
                sb.append(" )");
                publishedDate.setText(sb.toString());
                publish.setText("更新する");
                publish.setEnabled(true);
                cancelPublish.setEnabled(true);
                break;
                
            case GLOBAL:
                instLabel.setText("このスタンプはグローバルに公開されています。");
                stampBoxName.setText(stmpTree.getName());
                local.setSelected(false);
                publc.setSelected(true);
                category.setSelectedItem(stmpTree.getCategory());
                partyName.setText(stmpTree.getPartyName());
                contact.setText(stmpTree.getUrl());
                description.setText(stmpTree.getDescription());
                publishType = TT_PUBLIC;
                
                published = ((StampTreeModel) stmpTree).getPublished();
                if (published != null) {
                    StringTokenizer st = new StringTokenizer(published, ",");
                    while (st.hasMoreTokens()) {
                        String entity = st.nextToken();
                        for (int i = 0; i < IInfoModel.STAMP_ENTITIES.length; i++) {
                            if (entity.equals(IInfoModel.STAMP_ENTITIES[i])) {
                                entities[i].setSelected(true);
                                break;
                            }
                        }
                    }
                }
                
                sb = new StringBuilder();
                sb.append(ModelUtils.getDateAsString(stmpTree.getPublishedDate()));
                sb.append("  最終更新日( ");
                sb.append(ModelUtils.getDateAsString(stmpTree.getLastUpdated()));
                sb.append(" )");
                publishedDate.setText(sb.toString());
                publish.setText("更新する");
                publish.setEnabled(true);
                cancelPublish.setEnabled(true);
                break;
        }
        
        // コンポーネントのイベント接続を行う
        // Text入力をチェックする
        //ReflectDocumentListener dl = new ReflectDocumentListener(this, "checkButton");
        DocumentListener dl = new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                checkButton();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkButton();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                //checkButton();
            }
        };
        stampBoxName.getDocument().addDocumentListener(dl);
        partyName.getDocument().addDocumentListener(dl);
        contact.getDocument().addDocumentListener(dl);
        description.getDocument().addDocumentListener(dl);
        
        // RadioButton
        ButtonGroup bg = new ButtonGroup();
        bg.add(local);
        bg.add(publc);
        PublishTypeListener pl = new PublishTypeListener();
        local.addActionListener(pl);
        publc.addActionListener(pl);
        
        // CheckBox listener
        ReflectActionListener cbListener = new ReflectActionListener(this, "checkButton");
        for (JCheckBox cb : entities) {
            cb.addActionListener(cbListener);
        }
        
        // publish & cancel
        publish.addActionListener(new ReflectActionListener(this, "publish"));
        cancelPublish.addActionListener(new ReflectActionListener(this, "cancelPublish"));
        cancel.addActionListener(new ReflectActionListener(this, "stop"));
        
        return contentPane;
    }
    
    class PublishTypeListener implements ActionListener {
        
        @Override
        public void actionPerformed(ActionEvent e) {
            if (local.isSelected()) {
                publishType = TT_LOCAL;
                category.setSelectedIndex(ClientContext.getInt("stamp.publish.categories.localItem"));
            } else if (publc.isSelected()) {
                publishType = TT_PUBLIC;
            }
            checkButton();
        }
    }
    
    /**
     * スタンプを公開する。
     */
    public void publish() {
        
        //
        // 公開するStampTreeを取得する
        //
        ArrayList<StampTree> publishList = new ArrayList<StampTree>(IInfoModel.STAMP_ENTITIES.length);
        
        // Entity のカンマ連結用 StringBuilder 
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < IInfoModel.STAMP_ENTITIES.length; i++) {
            
            if (entities[i].isSelected()) {
                //
                // Entity チェックボックスがチェックされている時
                // 対応するEntity名を取得する
                //
                String entity = IInfoModel.STAMP_ENTITIES[i];
                
                //
                // StampBox からEmtityに対応するStampTreeを得る
                //
                StampTree st = stampBox.getStampTreeFromUserBox(entity);
                
                //
                // 公開リストに加える
                //
                publishList.add(st);
                
                // Entity 名をカンマで連結する
                sb.append(",");
                sb.append(entity);
            }
        }
        String published = sb.toString();
        published = published.substring(1);
        
        //
        // 公開する StampTree の XML データを生成する
        //
        DefaultStampTreeXmlBuilder builder = new DefaultStampTreeXmlBuilder();
        StampTreeXmlDirector director = new StampTreeXmlDirector(builder);
        String publishXml = director.build(publishList);
        byte[] bytes = null;
        try {
            bytes = publishXml.getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
        final byte[] publishBytes = bytes;
        
        //
        // 公開時の自分（個人用）の StampTree と同期をとる
        // 公開時の自分（個人用）Stamptreeを保存/更新する
        //
        List<StampTree> personalTree = stampBox.getUserStampBox().getAllTrees();
        builder = new DefaultStampTreeXmlBuilder();
        director = new StampTreeXmlDirector(builder);
        String treeXml = director.build((ArrayList<StampTree>) personalTree);
        
        //
        // 個人用のStampTreeModelに公開時のXMLをセットする
        //
        final open.dolphin.infomodel.StampTreeModel stmpTree = (open.dolphin.infomodel.StampTreeModel) stampBox.getUserStampBox().getStampTreeModel();
        stmpTree.setTreeXml(treeXml);
        
        //
        // 公開情報を設定する
        //
        stmpTree.setName(stampBoxName.getText().trim());
        String pubType = publc.isSelected() ? IInfoModel.PUBLISHED_TYPE_GLOBAL : Project.getUserModel().getFacilityModel().getFacilityId();
        stmpTree.setPublishType(pubType);
        stmpTree.setCategory((String) category.getSelectedItem());
        stmpTree.setPartyName(partyName.getText().trim());
        stmpTree.setUrl(contact.getText().trim());
        stmpTree.setDescription(description.getText().trim());
        stmpTree.setPublished(published);
        
        // 公開及び更新日を設定する
        switch (publishState) {
            case NONE:
            case SAVED_NONE:
                Date date = new Date();
                stmpTree.setPublishedDate(date);
                stmpTree.setLastUpdated(date);
                break;
            case LOCAL:
            case GLOBAL:
                stmpTree.setLastUpdated(new Date());
                break;
        }

        // task
        final SimpleWorker worker = new SimpleWorker<Void, Void>() {
        
            @Override
            protected Void doInBackground() throws Exception {

                StampDelegater sdl = new StampDelegater();

                switch (publishState) {

                    case NONE:
                        //
                        // 最初のログイン時、まだ自分のStamptreeが保存されていない状態の時
                        // 自分（個人用）StampTreeModelを保存し公開する
                        //
                        long id = sdl.saveAndPublishTree(stmpTree, publishBytes);
                        stmpTree.setId(id);
                        break;

                    case SAVED_NONE:
                        //
                        // 自分用のStampTreeがあって新規に公開する場合
                        //
                        sdl.publishTree(stmpTree, publishBytes);
                        break;

                    case LOCAL:
                        //
                        // Localに公開されていて更新する場合
                        //
                        sdl.updatePublishedTree(stmpTree, publishBytes);
                        break;

                    case GLOBAL:
                        //
                        // Global に公開されていて更新する場合
                        //
                        sdl.updatePublishedTree(stmpTree, publishBytes);
                        break;
                }

                if (!sdl.isNoError()) {
                    throw new Exception(sdl.getErrorMessage());
                }

                return null;
            }
            
            @Override
            protected void succeeded(Void result) {
                JOptionPane.showMessageDialog(dialog,
                        "スタンプを公開しました。",
                        ClientContext.getFrameTitle(title),
                        JOptionPane.INFORMATION_MESSAGE);
                stop();

            }
            
            @Override
            protected void failed(java.lang.Throwable cause) {
                JOptionPane.showMessageDialog(dialog,
                            cause.getMessage(),
                            ClientContext.getFrameTitle(title),
                            JOptionPane.WARNING_MESSAGE);
            }
        };

        String message = "スタンプ公開";
        String note = "公開しています...";
        Component c = dialog;
        monitor = new ProgressMonitor(c, message, note, 0, maxEstimation / delay);

        taskTimer = new Timer(delay, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                delayCount++;

                if (monitor.isCanceled() && (!worker.isCancelled())) {
                   // no cancel
                } else if (delayCount >= monitor.getMaximum() && (!worker.isCancelled())) {
                    worker.cancel(true);

                } else {
                    monitor.setProgress(delayCount);
                }
            }
        });

        WorkerService service = new WorkerService() {

            @Override
            protected void startProgress() {
                delayCount = 0;
                blockGlass.block();
                taskTimer.start();
            }

            @Override
            protected void stopProgress() {
                taskTimer.stop();
                monitor.close();
                blockGlass.unblock();
                taskTimer = null;
                monitor = null;
            }
        };

        service.execute(worker);
    }
    
    /**
     * 公開しているTreeを取り消す。
     */
    public void cancelPublish() {
        
        // 確認を行う
        JLabel msg1 = new JLabel("公開を取り消すとサブスクライブしているユーザがあなたの");
        JLabel msg2 = new JLabel("スタンプを使用できなくなります。公開を取り消しますか?");
        JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3));
        p1.add(msg1);
        JPanel p2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3));
        p2.add(msg2);
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.add(p1);
        box.add(p2);
        box.setBorder(BorderFactory.createEmptyBorder(0, 0, 11, 11));
        
        int option = JOptionPane.showConfirmDialog(dialog,
                new Object[]{box},
                ClientContext.getFrameTitle(title),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                ClientContext.getImageIcon("sinfo_32.gif"));
        
        if (option != JOptionPane.YES_OPTION) {
            return;
        }
        
        //
        // StampTree を表す XML データを生成する
        //
        List<StampTree> list = stampBox.getUserStampBox().getAllTrees();
        DefaultStampTreeXmlBuilder builder = new DefaultStampTreeXmlBuilder();
        StampTreeXmlDirector director = new StampTreeXmlDirector(builder);
        String treeXml = director.build((ArrayList<StampTree>) list);
        
        //
        // 個人用のStampTreeModelにXMLをセットする
        //
        final open.dolphin.infomodel.StampTreeModel stmpTree = (open.dolphin.infomodel.StampTreeModel) stampBox.getUserStampBox().getStampTreeModel();
        
        //
        // 公開データをクリアする
        //
        stmpTree.setTreeXml(treeXml);
        stmpTree.setPublishType(null);
        stmpTree.setPublishedDate(null);
        stmpTree.setLastUpdated(null);
        stmpTree.setCategory(null);
        stmpTree.setName(ClientContext.getString("stampTree.personal.box.name"));
        stmpTree.setDescription(ClientContext.getString("stampTree.personal.box.tooltip"));

        // task
        final SimpleWorker worker = new SimpleWorker<Void, Void>() {
               
            @Override
            protected Void doInBackground() throws Exception {
                StampDelegater sdl = new StampDelegater();
                sdl.cancelPublishedTree(stmpTree);
                if (!sdl.isNoError()) {
                    throw new Exception(sdl.getErrorMessage());
                }
                return null;
            }
            
            @Override
            protected void succeeded(Void result) {
                JOptionPane.showMessageDialog(dialog,
                            "公開を取り消しました。",
                            ClientContext.getFrameTitle(title),
                            JOptionPane.INFORMATION_MESSAGE);
                stop();
            }
            
            @Override
            protected void failed(java.lang.Throwable cause) {
                JOptionPane.showMessageDialog(dialog,
                            sdl.getErrorMessage(),
                            ClientContext.getFrameTitle(title),
                            JOptionPane.WARNING_MESSAGE);
            }
        };

        String message = "スタンプ公開";
        String note = "取り消しています...";
        Component c = dialog;
        monitor = new ProgressMonitor(c, message, note, 0, maxEstimation / delay);

        taskTimer = new Timer(delay, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                delayCount++;

                if (monitor.isCanceled() && (!worker.isCancelled())) {
                   // no cancel
                } else if (delayCount >= monitor.getMaximum() && (!worker.isCancelled())) {
                    worker.cancel(true);

                } else {
                    monitor.setProgress(delayCount);
                }
            }
        });

        WorkerService service = new WorkerService() {

            @Override
            protected void startProgress() {
                delayCount = 0;
                blockGlass.block();
                taskTimer.start();
            }

            @Override
            protected void stopProgress() {
                taskTimer.stop();
                monitor.close();
                blockGlass.unblock();
                taskTimer = null;
                monitor = null;
            }
        };

        service.execute(worker);
        
    }
    
    public void checkButton() {
        
        switch (publishType) {
            case TT_NONE:
                break;
                
            case TT_LOCAL:
                boolean stampNameOk = stampBoxName.getText().trim().equals("") ? false : true;
                boolean partyNameOk = partyName.getText().trim().equals("") ? false : true;
                boolean descriptionOk = description.getText().trim().equals("") ? false : true;
                boolean checkOk = false;
                for (JCheckBox cb : entities) {
                    if (cb.isSelected()) {
                        checkOk = true;
                        break;
                    }
                }
                boolean newOk = (stampNameOk && partyNameOk && descriptionOk && checkOk) ? true : false;
                if (newOk != okState) {
                    okState = newOk;
                    publish.setEnabled(okState);
                }
                break;
                
            case TT_PUBLIC:
                stampNameOk = stampBoxName.getText().trim().equals("") ? false : true;
                partyNameOk = partyName.getText().trim().equals("") ? false : true;
                boolean urlOk = contact.getText().trim().equals("") ? false : true;
                descriptionOk = description.getText().trim().equals("") ? false : true;
                checkOk = false;
                for (JCheckBox cb : entities) {
                    if (cb.isSelected()) {
                        checkOk = true;
                        break;
                    }
                }
                newOk = (stampNameOk && partyNameOk && urlOk && descriptionOk && checkOk) ? true : false;
                if (newOk != okState) {
                    okState = newOk;
                    publish.setEnabled(okState);
                }
                break;
        }
    }
}



