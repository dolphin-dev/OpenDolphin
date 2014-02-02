package open.dolphin.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.IStampTreeModel;
import open.dolphin.infomodel.ModuleInfoBean;
import open.dolphin.infomodel.ModuleModel;
import open.dolphin.infomodel.PublishedTreeModel;
import open.dolphin.infomodel.RegisteredDiagnosisModel;
import open.dolphin.infomodel.StampTreeModel;
import open.dolphin.order.EditorSetPanel;
import open.dolphin.helper.ComponentMemory;
import open.dolphin.project.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.apache.log4j.Logger;

/**
 * StampBox クラス。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class StampBoxPlugin extends AbstractMainTool {
    
    private static final String NAME = "スタンプ箱";
    
    // frameのデフォルトの大きさ及びタイトル
    private final int DEFAULT_WIDTH     = 320;
    private final int DEFAULT_HEIGHT    = 690;
    private final int IMPORT_TREE_OFFSET = 1;
    
    // StampBox の JFrame
    private JFrame frame;
    
    // StampBox
    private JTabbedPane parentBox;
    
    //ユーザ個人用の StampBox
    private AbstractStampBox userBox;
    
    // 現在選択されている StampBox
    private AbstractStampBox curBox;
    
    // インポートしている StampTree のリスト
    private List<Long> importedTreeList;
    
    // 現在選択されている StampBox の情報を表示するラベル
    private JLabel curBoxInfo;
    
    // StampBox JFrame のcontentPane
    private JPanel content;
    
    // Stampmaker ボタン
    private JToggleButton toolBtn;
    
    // 公開ボタン
    private JButton publishBtn;
    
    // インポートボタン
    private JButton importBtn;
    
    // StampMaker のエディタセット
    private EditorSetPanel editors;
    
    // Editorの編集値リスナ
    private EditorValueListener editorValueListener;
    
    // StampMaker モードのフラグ
    private boolean editing;
    
    // StampBox 位置
    private Point stampBoxLoc;
    
    // StampBox 幅
    private int stampBoxWidth;
    
    // StampBox 高さ
    private int stampBoxHeight;
    
    // Block Glass Pane
    private BlockGlass glass;
    
    // Container Panel
    private JPanel stampBoxPanel;
    
    // このスタンプボックスの StmpTreeModel
    private List<IStampTreeModel> stampTreeModels;
    
    // Logger
    private Logger logger;
    
    /**
     * Creates new StampBoxPlugin
     */
    public StampBoxPlugin() {
        setName(NAME);
        logger = ClientContext.getBootLogger();
    }
    
    /**
     * StampTreeModel を返す。
     * @return StampTreeModelのリスト
     */
    public List<IStampTreeModel> getStampTreeModels() {
        return stampTreeModels;
    }
    
    /**
     * StampTreeModel を設定する。
     * @param stampTreeModels StampTreeModelのリスト
     */
    public void setStampTreeModels(List<IStampTreeModel> stampTreeModels) {
        this.stampTreeModels = stampTreeModels;
    }
    
    /**
     * 現在のStampBoxを返す。
     * @return 現在選択されているStampBox
     */
    public AbstractStampBox getCurrentBox() {
        return curBox;
    }
    
    /**
     * 現在のStampBoxを設定する。
     * @param curBox 選択されたStampBox
     */
    public void setCurrentBox(AbstractStampBox curBox) {
        this.curBox = curBox;
    }
    
    /**
     * User(個人用)のStampBoxを返す。
     * @return User(個人用)のStampBox
     */
    public AbstractStampBox getUserStampBox() {
        return userBox;
    }
    
    /**
     * User(個人用)のStampBoxを設定する。
     * @param userBox User(個人用)のStampBox
     */
    public void setUserStampBox(AbstractStampBox userBox) {
        this.userBox = userBox;
    }
    
    /**
     * StampBox の JFrame を返す。
     * @return StampBox の JFrame
     */
    public JFrame getFrame() {
        return frame;
    }
    
    /**
     * インポートしているStampTreeのリストを返す。
     * @return インポートしているStampTreeのリスト
     */
    public List<Long> getImportedTreeList() {
        return importedTreeList;
    }
    
    /**
     * Block用GlassPaneを返す。
     * @return Block用GlassPane
     */
    public BlockGlass getBlockGlass() {
        return glass;
    }
    
    /**
     * プログラムを開始する。
     */
    @Override
    public void start() {
        
        if (stampTreeModels == null) {
            logger.fatal("StampTreeModel is null");
            throw new RuntimeException("Fatal error: StampTreeModel is null at start.");
        }
        
        //
        // StampBoxのJFrameを生成する
        //
        String title = ClientContext.getFrameTitle(getName());
        Rectangle setBounds = new Rectangle(0, 0, 1000, 690);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int defaultX = (screenSize.width - setBounds.width) / 2;
        int defaultY = (screenSize.height - setBounds.height) / 2;
        int defaultWidth = setBounds.width;
        int defaultHeight = setBounds.height;
        setBounds = new Rectangle(defaultX, defaultY, defaultWidth, defaultHeight);
        int x = (defaultX + defaultWidth) - DEFAULT_WIDTH;
        int y = defaultY;
        int width = DEFAULT_WIDTH;
        int height = DEFAULT_HEIGHT;
        frame = new JFrame(title);
        glass = new BlockGlass();
        frame.setGlassPane(glass);
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (editing) {
                    toolBtn.doClick();
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        });
        ComponentMemory cm = new ComponentMemory(frame, new Point(x, y), new Dimension(width, height), this);
        cm.setToPreferenceBounds();
        
        //
        // 全体のボックスを生成する
        //
        parentBox = new JTabbedPane();
        parentBox.setTabPlacement(JTabbedPane.BOTTOM);
        
        //
        // 読み込んだStampTreeをTabbedPaneに格納し、さらにそれをparentBoxに追加する
        //
        for (IStampTreeModel model : stampTreeModels) {
            
            if (model != null) {
                
                logger.debug("id = " + model.getId());
                logger.debug("name = " + model.getName());
                logger.debug("publishType = " + model.getPublishType());
                logger.debug("category = " + model.getCategory());
                logger.debug("partyName = " + model.getPartyName());
                logger.debug("url = " + model.getUrl());
                logger.debug("description = " + model.getDescription());
                logger.debug("publishedDate = " + model.getPublishedDate());
                logger.debug("lastUpdated = " + model.getLastUpdated());
                logger.debug("userId = " + model.getUserModel());
                
                //
                // ユーザ個人用StampTreeの場合
                //
                if (model.getUserModel().getId() == Project.getUserModel().getId() && model instanceof StampTreeModel) {
                    
                    //------------------------------------------
                    // 個人用のスタンプボックス(JTabbedPane)を生成する
                    //------------------------------------------
                    userBox = new UserStampBox();
                    userBox.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
                    userBox.setContext(this);
                    userBox.setStampTreeModel(model);
                    userBox.buildStampBox();
                    
                    //
                    // ParentBox に追加する
                    //
                    parentBox.addTab(ClientContext.getString("stampTree.personal.box.name"), userBox);
                    
                } else if (model instanceof PublishedTreeModel) {
                    //
                    // インポートしているTreeの場合
                    //
                    importPublishedTree(model);
                }
                model.setTreeXml(null);
            }
        }
        
        //
        // StampTreeModel を clear する
        //
        stampTreeModels.clear();
        
        // ParentBox のTab に tooltips を設定する
        for (int i = 0; i < parentBox.getTabCount(); i++) {
            AbstractStampBox box = (AbstractStampBox) parentBox.getComponentAt(i);
            parentBox.setToolTipTextAt(i, box.getInfo());
        }
        
        //
        // ParentBoxにChangeListenerを登録しスタンプメーカの制御を行う
        //
        parentBox.addChangeListener(new BoxChangeListener());
        setCurrentBox(userBox);
        
        //
        // ユーザBox用にChangeListenerを設定する
        //
        userBox.addChangeListener(new TabChangeListener());
        
        //
        // スタンプメーカを起動するためのボタンを生成する
        //
        toolBtn = new JToggleButton(ClientContext.getImageIcon("tools_24.gif"));
        toolBtn.setToolTipText("スタンプメーカを起動します");
        toolBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!editing) {
                    startStampMake();
                    editing = true;
                } else {
                    stopStampMake();
                    editing = false;
                }
            }
        });
        
        //
        // スタンプ公開ボタンを生成する
        //
        publishBtn = new JButton(ClientContext.getImageIcon("exp_24.gif"));
        publishBtn.setToolTipText("スタンプの公開を管理をします");
        publishBtn.addActionListener(new ReflectActionListener(this, "publishStamp"));
        
        //
        // インポートボタンを生成する
        //
        importBtn = new JButton(ClientContext.getImageIcon("impt_24.gif"));
        importBtn.setToolTipText("スタンプのインポートを管理をします");
        importBtn.addActionListener(new ReflectActionListener(this, "importStamp"));
        
        //
        // curBoxInfoラベルを生成する
        //
        curBoxInfo = new JLabel("");
        curBoxInfo.setFont(GUIFactory.createSmallFont());
        
        //
        // レイアウトする
        //
        stampBoxPanel = new JPanel(new BorderLayout());
        stampBoxPanel.add(parentBox, BorderLayout.CENTER);
        JPanel cmdPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cmdPanel.add(toolBtn);
        cmdPanel.add(publishBtn);
        cmdPanel.add(importBtn);
        cmdPanel.add(curBoxInfo);
        stampBoxPanel.add(cmdPanel, BorderLayout.NORTH);
        
        //
        // コンテントパネルを生成する
        //
        content = new JPanel(new BorderLayout());
        content.add(stampBoxPanel, BorderLayout.CENTER);
        content.setOpaque(true);
        
        //
        // Frame に加える
        //
        frame.setContentPane(content);
        
        //
        // 前回終了時のタブを選択する
        //
        String name = this.getClass().getName();
        int index = Project.getInt(name + "_parentBox", 0);
        index = ( index >= 0 && index <= (parentBox.getTabCount() -1) ) ? index : 0;
        parentBox.setSelectedIndex(index);
        index = Project.getInt(name + "_stampBox", 0);
        index = ( index >= 0 && index <= (userBox.getTabCount() -1) ) ? index : 0;
        
        //
        // ORCA タブが選択されていて ORCA に接続がない場合を避ける
        //
        index = index == IInfoModel.TAB_INDEX_ORCA ? 0 : index;
        userBox.setSelectedIndex(index);
        
        //
        // ボタンをコントロールする
        //
        boxChanged();
    }
    
    /**
     * 選択されているIndexでボタンを制御する。
     */
    private void boxChanged() {
        
        int index = parentBox.getSelectedIndex();
        setCurrentBox((AbstractStampBox) parentBox.getComponentAt(index));
        String info = getCurrentBox().getInfo();
        curBoxInfo.setText(info);
        
        if (getCurrentBox() == userBox) {
            publishBtn.setEnabled(true);
            int index2 = userBox.getSelectedIndex();
            boolean enabled = userBox.isHasEditor(index2);
            toolBtn.setEnabled(enabled);
            
        } else {
            toolBtn.setEnabled(false);
            publishBtn.setEnabled(false);
        }
    }
    
    /**
     * ImportしたStampBoxの選択可能を制御する。
     * @param enabled 選択可能な時 true
     */
    private void enabledImportBox(boolean enabled) {
        int cnt = parentBox.getTabCount();
        for (int i = 0 ; i < cnt; i++) {
            if ((JTabbedPane) parentBox.getComponentAt(i) != userBox) {
                parentBox.setEnabledAt(i, enabled);
            }
        }
    }
    
    /**
     * TabChangeListener
     * User用StampBoxのTab切り替えリスナクラス。
     */
    class TabChangeListener implements ChangeListener {
        
        @Override
        public void stateChanged(ChangeEvent e) {
            
            if (!editing) {
                // スタンプメーカ起動中でない時
                // テキストスタンプタブが選択されたらスタンプメーカボタンを disabledにする
                // ORCA セットタブの場合を処理する
                int index = userBox.getSelectedIndex();
                StampTree tree = userBox.getStampTree(index);
                tree.enter();
                boolean enabled = userBox.isHasEditor(index);
                toolBtn.setEnabled(enabled);
                
            } else {
                // スタンプメーカ起動中の時
                // 選択されたタブに対応するエディタを表示する
                int index = userBox.getSelectedIndex();
                StampTree tree = userBox.getStampTree(index);
                if (editors != null && (!tree.getEntity().equals(IInfoModel.ENTITY_TEXT)) ) {
                    editors.show(tree.getEntity());
                }
            }
        }
    }
    
    /**
     * ParentBox の TabChangeListenerクラス。
     */
    class BoxChangeListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            boxChanged();
        }
    }

    /**
     * スタンプメーカを起動する。
     */
    public void startStampMake() {

        if (editing) {
            return;
        }

        Runnable awt = new Runnable() {

            @Override
            public void run() {
                createAndShowEditorSet();
            }
        };

        SwingUtilities.invokeLater(awt);
    }
    
    /**
     * スタンプメーカを起動する。
     */
    private void createAndShowEditorSet() {

        // インポートボックスを選択不可にする
        enabledImportBox(false);
        
        // 現在の位置と大きさを保存する
        stampBoxLoc = frame.getLocation();
        stampBoxWidth = frame.getWidth();
        stampBoxHeight = frame.getHeight();
        
        // 現在のタブからtreeのEntityを得る
        int index = userBox.getSelectedIndex();
        StampTree tree = userBox.getStampTree(index);
        String entity = tree.getEntity();
        
        // エディタセットを生成する
        editors = new EditorSetPanel();

        // text タブを選択不可にする
        userBox.setHasNoEditorEnabled(false);

        //----------------------------------------------------------
        // 全 Tree に edirorSet を treeSelectionListener として登録する
        //----------------------------------------------------------
        List<StampTree> allTrees = userBox.getAllTrees();
        for (StampTree st : allTrees) {
            st.addTreeSelectionListener(editors);
        }

        // Editorへ編集値を受けとるためのリスナを登録する
        editorValueListener = new EditorValueListener();
        editors.addPropertyChangeListener(EditorSetPanel.EDITOR_VALUE_PROP, editorValueListener);

        // EditorSet へ現在のentity(Tree)に対応するエディタを表示させる
        editors.show(entity);

        // StampBox の Frame を再構築する
        frame.setVisible(false);
        content.removeAll();
        content.add(editors, BorderLayout.CENTER);
        content.add(stampBoxPanel, BorderLayout.EAST);
        stampBoxPanel.setPreferredSize(new Dimension(300, 650));
        content.revalidate();
        frame.pack();
        
        // 前回終了時の位置とサイズを取得する
        String name = this.getClass().getName();
        int locX = Project.getInt(name + ".stampmMaker.x", 0);
        int locY = Project.getInt(name + ".stampmMaker.y", 0);
        int width = Project.getInt(name + ".stampmMaker.width", 0);
        int height = Project.getInt(name + ".stampmMaker.height", 0);

        width=0;
        height=0;

        if (width == 0 || height == 0) {
            // センタリングする
            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            int x = (screen.width - frame.getSize().width)/2;
            int y = (screen.height - frame.getSize().height)/2;
            frame.setLocation(x, y);
        } else {
            frame.setLocation(locX, locY);
            frame.setSize(width, height);
        }

        frame.setVisible(true);
        editing = true;
        toolBtn.setToolTipText("スタンプメーカを終了します");
        publishBtn.setEnabled(false);
        importBtn.setEnabled(false);
    }


    public void stopStampMake() {

        if (!editing) {
            return;
        }

        Runnable awt = new Runnable() {

            @Override
            public void run() {
                disposeEditorSet();
            }
        };

        SwingUtilities.invokeLater(awt);
    }

    
    /**
     * スタンプメーカを終了する。
     */
    private void disposeEditorSet() {
        
        // 現在の大きさと位置をPreferenceに保存ずる
        String name = this.getClass().getName();
        Project.setInt(name + ".stampmMaker.x", frame.getLocation().x);
        Project.setInt(name + ".stampmMaker.y", frame.getLocation().y);
        Project.setInt(name + ".stampmMaker.width", frame.getWidth());
        Project.setInt(name + ".stampmMaker.height", frame.getHeight());
        
        editors.close();
        editors.removePropertyChangeListener(EditorSetPanel.EDITOR_VALUE_PROP, editorValueListener);
        List<StampTree> allTrees = userBox.getAllTrees();
        for (StampTree st : allTrees) {
            st.removeTreeSelectionListener(editors);
        }
        
        content.removeAll();
        content.add(stampBoxPanel, BorderLayout.CENTER);
        
        editors = null;
        editorValueListener = null;
        userBox.setHasNoEditorEnabled(true);
        content.revalidate();
        frame.setLocation(stampBoxLoc);
        frame.setSize(new Dimension(stampBoxWidth, stampBoxHeight));
        editing = false;
        toolBtn.setToolTipText("スタンプメーカを起動します");
        publishBtn.setEnabled(true);
        importBtn.setEnabled(true);
        
        //
        // ASP ボックスを選択可にする
        //
        enabledImportBox(true);
    }
    
    /**
     * EditorValueListener
     * エディタで作成したスタンプをStampTreeに加える。
     */
    class EditorValueListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent e) {
            Object obj = e.getNewValue();
            if (obj != null && obj instanceof ModuleModel) {
                //--------------------
                // 編集したスタンプ
                //--------------------
                ModuleModel stamp = (ModuleModel) obj;
                String entity = stamp.getModuleInfoBean().getEntity();
                StampTree tree = userBox.getStampTree(entity);

                if (stamp.getModuleInfoBean().getStampId()!=null) {
                    // stampId!=nullなら上書き
                    tree.replaceStamp(stamp);
                } else {
                    // でなければ新規
                    tree.addStamp(stamp, null);
                }
                
            } else if (obj != null && obj instanceof ArrayList) {
                //-------------------
                // 傷病名
                //-------------------
                //System.err.println("EditorValueListener: 傷病名");
                StampTree tree = getStampTree(IInfoModel.ENTITY_DIAGNOSIS);
                tree.addDiagnosis((ArrayList<RegisteredDiagnosisModel>) obj);
            }
        }
    }
    
    /**
     * スタンプパブリッシャーを起動する。
     */
    public void publishStamp() {
        StampPublisher publisher = new StampPublisher(this);
        publisher.start();
    }
    
    /**
     * スタンプインポーターを起動する。
     */
    public void importStamp() {
        StampImporter importer = new StampImporter(this);
        importer.start();
    }
    
    /**
     * 公開されているスタンプTreeをインポートする。
     * @param importTree インポートする公開Tree
     */
    public void importPublishedTree(IStampTreeModel importTree) {
        
        //
        // Asp StampBox を生成し parentBox に加える
        //
        AbstractStampBox aspBox = new AspStampBox();
        aspBox.setContext(this);
        aspBox.setStampTreeModel(importTree);
        aspBox.buildStampBox();
        parentBox.addTab(importTree.getName(), aspBox);
        
        //
        // インポートリストに追加する
        //
        if (importedTreeList == null) {
            importedTreeList = new ArrayList<Long>(5);
        }
        importedTreeList.add(new Long(importTree.getId()));
    }
    
    /**
     * インポートしている公開Treeを削除する。
     * @param removeId 削除する公開TreeのId
     */
    public void removeImportedTree(long removeId) {
        
        if (importedTreeList != null) {
            for (int i = 0; i < importedTreeList.size(); i++) {
                Long id = importedTreeList.get(i);
                if (id.longValue() == removeId) {
                    parentBox.removeTabAt(i+IMPORT_TREE_OFFSET);
                    importedTreeList.remove(i);
                    break;
                }
            }
        }
    }
    
    /**
     * プログラムを終了する。
     */
    @Override
    public void stop() {
        frame.setVisible(false);
        frame.dispose();
    }
    
    /**
     * フレームを前面に出す。
     */
    @Override
    public void enter() {
        if (frame != null) {
            frame.toFront();
        }
    }

    /**
     * アプリケーションの終了時にスタンプツリーを返し保存する。
     * @return StamPtreeMode; 
     */
    public IStampTreeModel getUsersTreeTosave() {

        preSave();

        //
        // User Tree のみを保存する
        //
        ArrayList<StampTree> list = (ArrayList<StampTree>) userBox.getAllTrees();
        if (list == null || list.isEmpty()) {
            // never
            return null;
        }

        //
        // ORCA セットは除く
        //
        for (StampTree tree : list) {
            if (tree.getTreeInfo().getEntity().equals(IInfoModel.ENTITY_ORCA)) {
                list.remove(tree);
                logger.debug("ORCAセットを除きました");
                break;
            }
        }

        // StampTree を表す XML データを生成する
        DefaultStampTreeXmlBuilder builder = new DefaultStampTreeXmlBuilder();
        StampTreeXmlDirector director = new StampTreeXmlDirector(builder);
        String treeXml = director.build(list);

        // 個人用のStampTreeModelにXMLをセットする
        IStampTreeModel treeTosave = userBox.getStampTreeModel();
        treeTosave.setTreeXml(treeXml);

        return treeTosave;
    }

    /**
     * 位置大きさを保存する。
     * @throws Exception
     */
    private void preSave() {

        String name = (StampBoxPlugin.this).getClass().getName();

        // StampMeker modeで終了した場合、
        // 次回起動時に通常モードの位置と大きさで表示するため
        if (editing) {
            Project.setInt(name + "_x", stampBoxLoc.x);
            Project.setInt(name + "_y", stampBoxLoc.y);
            Project.setInt(name + "_width", stampBoxWidth);
            Project.setInt(name + "_height", stampBoxHeight);
        }

        // 終了時のタブ選択インデックスを保存する
        Project.setInt(name + "_parentBox", parentBox.getSelectedIndex());
        Project.setInt(name + "_stampBox", userBox.getSelectedIndex());
    }

    
    /**
     * 引数のカテゴリに対応するTreeを返す。
     * @param category Treeのカテゴリ
     * @return カテゴリにマッチするStampTree
     */
    public StampTree getStampTree(String entity) {
        return getCurrentBox().getStampTree(entity);
    }
    
    public StampTree getStampTreeFromUserBox(String entity) {
        return getUserStampBox().getStampTree(entity);
    }
    
    /**
     * スタンプボックスに含まれる全treeのTreeInfoリストを返す。
     * @return TreeInfoのリスト
     */
    public List<TreeInfo> getAllTress() {
        return getCurrentBox().getAllTreeInfos();
    }
    
    /**
     * スタンプボックスに含まれる全treeを返す。
     * @return StampTreeのリスト
     */
    public List<StampTree> getAllTrees() {
        return getCurrentBox().getAllTrees();
    }
    
    /**
     * スタンプボックスに含まれる全treeを返す。
     * @return StampTreeのリスト
     */
    public List<StampTree> getAllAllPTrees() {
        
        int cnt = parentBox.getTabCount();
        ArrayList<StampTree> ret = new ArrayList<StampTree>();
        
        for (int i = 0; i < cnt; i++) {
            AbstractStampBox stb = (AbstractStampBox) parentBox.getComponentAt(i);
            ret.addAll(stb.getAllPTrees());
        }
        
        return ret;
    }
    
    /**
     * Currentボックスの P 関連Staptreeを返す。
     * @return StampTreeのリスト
     */
    public List<StampTree> getAllPTrees() {
        
        AbstractStampBox stb = (AbstractStampBox) getCurrentBox();
        return stb.getAllPTrees();
    }
    
    /**
     * 引数のエンティティ配下にある全てのスタンプを返す。
     * これはメニュー等で使用する。
     * @param entity Treeのエンティティ
     * @return 全てのスタンプのリスト
     */
    public List<ModuleInfoBean> getAllStamps(String entity) {
        return getCurrentBox().getAllStamps(entity);
    }
}