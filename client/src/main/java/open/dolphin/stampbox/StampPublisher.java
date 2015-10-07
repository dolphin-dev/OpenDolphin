package open.dolphin.stampbox;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import open.dolphin.client.BlockGlass;
import open.dolphin.client.ClientContext;
import open.dolphin.client.GUIFactory;
import open.dolphin.client.ReflectActionListener;
import open.dolphin.delegater.StampDelegater;
import open.dolphin.helper.GridBagBuilder;
import open.dolphin.helper.SimpleWorker;
import open.dolphin.infomodel.FacilityModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.IStampTreeModel;
import open.dolphin.infomodel.ModelUtils;
import open.dolphin.project.Project;

/**
 * StampTreePublisher
 *
 * @author Kazushi, Minagawa. Digital Globe, Inc.
 *
 */
public class StampPublisher {
    
    public enum PublishedState {NONE, SAVED_NONE, LOCAL, GLOBAL};
    
    private static final int TT_NONE = -1;
    private static final int TT_LOCAL = 0;
    private static final int TT_PUBLIC = 1;
    
    private final StampBoxPlugin stampBox;
    private final String title;
    
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
    private final int maxEstimation = 90*1000;    // 90 秒
    private final int delay = 300;                // 300 mmsec
    
    
    public StampPublisher(StampBoxPlugin stampBox) {
        title = ClientContext.getMyBundle(StampPublisher.class).getString("title.window");
        this.stampBox = stampBox;
    }
    
    public void start() {

        Runnable awt = () -> {
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
            dialog.setLocation(x, y);
            
            blockGlass = new BlockGlass();
            dialog.setGlassPane(blockGlass);
            dialog.setVisible(true);
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
        
        java.util.ResourceBundle bundle = ClientContext.getMyBundle(StampPublisher.class);
        String toolTipTexMandatory = bundle.getString("toolTipText.mandatory");
        String buttonTextStopPublish = bundle.getString("buttonText.stopPublish");
        String buttonTextClose = bundle.getString("toolTipText.closeDalog");
        
        // GUIコンポーネントを生成する
        infoLable = new JLabel(ClientContext.getImageIconArias("icon_info_small"));       
        instLabel = new JLabel("");
        instLabel.setFont(new Font("Dialog", Font.PLAIN, 10));
        publishedDate = new JLabel("");
        
        stampBoxName = GUIFactory.createTextField(15, null, null, null);
        partyName = GUIFactory.createTextField(20, null, null, null);
        contact = GUIFactory.createTextField(30, null, null, null);
        description = GUIFactory.createTextField(30, null, null, null);
        stampBoxName.setToolTipText(toolTipTexMandatory);
        partyName.setToolTipText(toolTipTexMandatory);
        contact.setToolTipText(toolTipTexMandatory);
        description.setToolTipText(toolTipTexMandatory);
        local = new JRadioButton(IInfoModel.PUBLISH_TREE_LOCAL);
        publc = new JRadioButton(IInfoModel.PUBLISH_TREE_PUBLIC);
        publish = new JButton("");
        publish.setEnabled(false);
        cancelPublish = new JButton(buttonTextStopPublish);
        cancelPublish.setEnabled(false);
        cancel = new JButton(buttonTextClose);
        java.util.ResourceBundle b = java.util.ResourceBundle.getBundle("open.dolphin.stampbox.StampBoxResource");
        String[] stampNames = (String[])b.getObject("STAMP_NAMES");
        String tabNameORCA = b.getString("TABNAME_ORCA");
        entities = new JCheckBox[stampNames.length];
        for (int i = 0; i < stampNames.length; i++) {
            entities[i] = new JCheckBox(stampNames[i]);
            if (stampNames[i].equals(tabNameORCA)) {
                entities[i].setEnabled(false);
            }
        }
        JPanel chkPanel1 = GUIFactory.createCheckBoxPanel(new JCheckBox[]{entities[0], entities[1], entities[2], entities[3], entities[4], entities[5], entities[6], entities[7]});
        JPanel chkPanel2 = GUIFactory.createCheckBoxPanel(new JCheckBox[]{entities[8], entities[9], entities[10], entities[11], entities[12], entities[13], entities[14], entities[15]});
        
        String[] categories = bundle.getString("stamp.publish.categories").split(",");
        category = new JComboBox(categories);
        JPanel categoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        categoryPanel.add(category);
        
        // 公開先RadioButtonパネル
        JPanel radioPanel = GUIFactory.createRadioPanel(new JRadioButton[]{local, publc});
        
        // 属性設定パネル
        String labelText = bundle.getString("labelText.publishSetting");
        GridBagBuilder gbl = new GridBagBuilder(labelText);
        
        int y = 0;
        gbl.add(infoLable, 0, y, GridBagConstraints.EAST);
        gbl.add(instLabel, 1, y, GridBagConstraints.WEST);
        
        y++;
        labelText = bundle.getString("labelText.setName.publishedStamp");
        gbl.add(new JLabel(labelText), 0, y, GridBagConstraints.EAST);
        gbl.add(stampBoxName, 1, y, GridBagConstraints.WEST);
        
        // DolphinPro NO
        // 5m && 70.1
        // OpenDolphin YES
        if (Project.canGlobalPublish()) {
            y++;
            labelText = bundle.getString("labelText.publishDestination");
            gbl.add(new JLabel(labelText), 0, y, GridBagConstraints.EAST);
            gbl.add(radioPanel, 1, y, GridBagConstraints.WEST);
        }
        
        y++;
        labelText = bundle.getString("labelText.category");
        gbl.add(new JLabel(labelText), 0, y, GridBagConstraints.EAST);
        gbl.add(categoryPanel, 1, y, GridBagConstraints.WEST);
        
        y++;
        labelText = bundle.getString("labelText.stampToPublish");
        gbl.add(new JLabel(labelText), 0, y, GridBagConstraints.EAST);
        gbl.add(chkPanel1, 1, y, GridBagConstraints.WEST);
        
        y++;
        gbl.add(new JLabel(" "), 0, y, GridBagConstraints.EAST);
        gbl.add(chkPanel2, 1, y, GridBagConstraints.WEST);
        
        y++;
        labelText = bundle.getString("labelText.publisherName");
        gbl.add(new JLabel(labelText), 0, y, GridBagConstraints.EAST);
        gbl.add(partyName, 1, y, GridBagConstraints.WEST);
        
        y++;
        labelText = bundle.getString("labelText.url");
        gbl.add(new JLabel(labelText), 0, y, GridBagConstraints.EAST);
        gbl.add(contact, 1, y, GridBagConstraints.WEST);
        
        y++;
        labelText = bundle.getString("labelText.description");
        gbl.add(new JLabel(labelText), 0, y, GridBagConstraints.EAST);
        gbl.add(description, 1, y, GridBagConstraints.WEST);
        
        y++;
        labelText = bundle.getString("labelText.publishedDate");
        gbl.add(new JLabel(labelText), 0, y, GridBagConstraints.EAST);
        gbl.add(publishedDate, 1, y, GridBagConstraints.WEST);
        
        // コマンドパネル
        JPanel cmdPanel;
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
                labelText = bundle.getString("labeltext.notPublished");
                instLabel.setText(labelText);
                partyName.setText(facility.getFacilityName());
                String url = facility.getUrl();
                if (url != null) {
                    contact.setText(url);
                }
                String dateStr = ModelUtils.getDateAsString(new Date());
                publishedDate.setText(dateStr);
                String btnText = bundle.getString("buttonText.publish");
                publish.setText(btnText);
                break;
                
            case SAVED_NONE:
                labelText = bundle.getString("labeltext.notPublished");
                instLabel.setText(labelText);
                partyName.setText(stmpTree.getPartyName());
                url = facility.getUrl();
                if (url != null) {
                    contact.setText(url);
                }
                dateStr = ModelUtils.getDateAsString(new Date());
                publishedDate.setText(dateStr);
                btnText = bundle.getString("buttonText.publish");
                publish.setText(btnText);
                break;
                
            case LOCAL:
                labelText = bundle.getString("labelText.underPublishedInternally");
                instLabel.setText(labelText);
                stampBoxName.setText(stmpTree.getName());
                local.setSelected(true);
                publc.setSelected(false);
                publishType = TT_LOCAL;
                
                //
                // Publish している Entity をチェックする
                //
                String published = ((open.dolphin.infomodel.StampTreeModel) stmpTree).getPublished();
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
                
                String fmt = bundle.getString("messageFormat.updatedDate");
                String info = new MessageFormat(fmt).format(new Object[]{
                    ModelUtils.getDateAsString(stmpTree.getPublishedDate()),
                    ModelUtils.getDateAsString(stmpTree.getLastUpdated())
                });
                btnText = bundle.getString("buttonText.update");
                publishedDate.setText(info);
                publish.setText(btnText);
                publish.setEnabled(true);
                cancelPublish.setEnabled(true);
                break;
                
            case GLOBAL:
                labelText = bundle.getString("labelText.underPublishedGlobal");
                instLabel.setText(labelText);
                stampBoxName.setText(stmpTree.getName());
                local.setSelected(false);
                publc.setSelected(true);
                category.setSelectedItem(stmpTree.getCategory());
                partyName.setText(stmpTree.getPartyName());
                contact.setText(stmpTree.getUrl());
                description.setText(stmpTree.getDescription());
                publishType = TT_PUBLIC;
                
                published = ((open.dolphin.infomodel.StampTreeModel)stmpTree).getPublished();
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
                
                fmt = bundle.getString("messageFormat.updatedDate");
                info = new MessageFormat(fmt).format(new Object[]{
                    ModelUtils.getDateAsString(stmpTree.getPublishedDate()),
                    ModelUtils.getDateAsString(stmpTree.getLastUpdated())
                });
                publishedDate.setText(info);
                btnText = bundle.getString("buttonText.update");
                publish.setText(btnText);
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

        //if (ClientContext.isDolphinPro()) {
        if (!Project.canGlobalPublish()) {    
            local.doClick();
        }
        
        return contentPane;
    }
    
    class PublishTypeListener implements ActionListener {
        
        @Override
        public void actionPerformed(ActionEvent e) {
            if (local.isSelected()) {
                publishType = TT_LOCAL;
                java.util.ResourceBundle bundle = ClientContext.getMyBundle(StampPublisher.class);
                int index = Integer.parseInt(bundle.getString("stamp.publish.categories.localItem"));
                category.setSelectedIndex(index);
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
        
        // 公開するStampTreeを取得する
        ArrayList<StampTree> publishList = new ArrayList<>(IInfoModel.STAMP_ENTITIES.length);
        
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
            ex.printStackTrace(System.err);
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
        String urltest = contact.getText().trim();
        urltest = urltest.equals("") ? null : urltest;
        stmpTree.setUrl(urltest);
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
                String version;

                switch (publishState) {

                    case NONE:
                        //
                        // 最初のログイン時、まだ自分のStamptreeが保存されていない状態の時
                        // 自分（個人用）StampTreeModelを保存し公開する
                        //
                        //long id = sdl.saveAndPublishTree(stmpTree, publishBytes);
                        //stmpTree.setId(id);
                        break;

                    case SAVED_NONE:
                        //
                        // 自分用のStampTreeがあって新規に公開する場合
                        //
                        version = sdl.publishTree(stmpTree, publishBytes);
                        stmpTree.setVersionNumber(version);
                        break;

                    case LOCAL:
                        //
                        // Localに公開されていて更新する場合
                        //
                        version = sdl.updatePublishedTree(stmpTree, publishBytes);
                        stmpTree.setVersionNumber(version);
                        break;

                    case GLOBAL:
                        //
                        // Global に公開されていて更新する場合
                        //
                        version = sdl.updatePublishedTree(stmpTree, publishBytes);
                        stmpTree.setVersionNumber(version);
                        break;
                }

                return null;
            }
            
            @Override
            protected void succeeded(Void result) {
                String msg = ClientContext.getMyBundle(StampPublisher.class).getString("message.publishedDone");
                JOptionPane.showMessageDialog(dialog,
                        msg,
                        ClientContext.getFrameTitle(title),
                        JOptionPane.INFORMATION_MESSAGE);
                stop();

            }
            
            @Override
            protected void failed(java.lang.Throwable cause) {
                String dispErr;
                String test = (cause!=null && cause.getMessage()!=null) ? cause.getMessage() : null;
                if (test!=null && test.contains("First Commit Win")) {
//                    StringBuilder sb = new StringBuilder();
//                    sb.append("スタンプツリーは他の端末により先に保存されています。").append("\n");
//                    sb.append("整合性を保ため、再ログインし、改めて実行してください。");
//                    dispErr = sb.toString();
                    dispErr = ClientContext.getMyBundle(StampPublisher.class).getString("warning.firstCommitWin");
                } else {
//                    StringBuilder sb = new StringBuilder();
//                    sb.append("スタンプツリーの保存に失敗しました。");
//                    dispErr = sb.toString();
                    dispErr = ClientContext.getMyBundle(StampPublisher.class).getString("error.saveStampTree");
                }
                JOptionPane.showMessageDialog(dialog,
                            dispErr,
                            ClientContext.getFrameTitle(title),
                            JOptionPane.WARNING_MESSAGE);
            }

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

        java.util.ResourceBundle bundle = ClientContext.getMyBundle(StampPublisher.class);
        String message = bundle.getString("message.progress.publishStamp");
        String note = bundle.getString("note.publisStamp");
        Component c = dialog;
        monitor = new ProgressMonitor(c, message, note, 0, maxEstimation / delay);

        taskTimer = new Timer(delay, (ActionEvent e) -> {
            delayCount++;
            
            if (monitor.isCanceled() && (!worker.isCancelled())) {
                // no cancel
            } else if (delayCount >= monitor.getMaximum() && (!worker.isCancelled())) {
                worker.cancel(true);
                
            } else {
                monitor.setProgress(delayCount);
            }
        });

        worker.execute();
    }
    
    /**
     * 公開しているTreeを取り消す。
     */
    public void cancelPublish() {
        
        // 確認を行う
        final java.util.ResourceBundle bundle = ClientContext.getMyBundle(StampPublisher.class);
        String msg_1 = bundle.getString("message.stopPublish_1");
        String msg_2 = bundle.getString("message.stopPublish_2");
        
        JLabel msg1 = new JLabel(msg_1);
        JLabel msg2 = new JLabel(msg_2);
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
        String name = bundle.getString("name.personalStampTree");
        stmpTree.setName(name);
        String toolTipText = bundle.getString("toolTipText.personalTree");
        stmpTree.setDescription(toolTipText);

        // task
        final SimpleWorker worker = new SimpleWorker<Void, Void>() {
               
            @Override
            protected Void doInBackground() throws Exception {
                StampDelegater sdl = new StampDelegater();
//s.oh^ 2013/10/11 スタンプ公開時のエラー
                //sdl.cancelPublishedTree(stmpTree);
                String version = sdl.cancelPublishedTree(stmpTree);
                stmpTree.setVersionNumber(version);
//s.oh$
                return null;
            }
            
            @Override
            protected void succeeded(Void result) {
                String message = bundle.getString("message.stoppedPublish");
                JOptionPane.showMessageDialog(dialog,
                            message,
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

        String message = bundle.getString("message.progress.publishStamp");
        String note = bundle.getString("note.canceling");
        Component c = dialog;
        monitor = new ProgressMonitor(c, message, note, 0, maxEstimation / delay);

        taskTimer = new Timer(delay, (ActionEvent e) -> {
            delayCount++;
            
            if (monitor.isCanceled() && (!worker.isCancelled())) {
                // no cancel
            } else if (delayCount >= monitor.getMaximum() && (!worker.isCancelled())) {
                worker.cancel(true);
                
            } else {
                monitor.setProgress(delayCount);
            }
        });

        worker.execute();
    }
    
    public void checkButton() {
        
        switch (publishType) {
            case TT_NONE:
                break;
                
            case TT_LOCAL:
            case TT_PUBLIC:
                boolean checkOk = false;
                for (JCheckBox cb : entities) {
                    if (cb.isSelected()) {
                        checkOk = true;
                        break;
                    }
                }
                boolean ok = true;
                ok = ok && checkOk;
                ok = ok && (!stampBoxName.getText().trim().equals(""));
                ok = ok && (!partyName.getText().trim().equals(""));
                ok = ok && (!contact.getText().trim().equals(""));
                ok = ok && (!description.getText().trim().equals(""));
                if (ok != okState) {
                    okState = ok;
                    publish.setEnabled(okState);
                }
                break;
        }
    }
}



