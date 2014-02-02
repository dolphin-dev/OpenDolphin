package open.dolphin.stampbox;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.*;
import open.dolphin.client.BlockGlass;
import open.dolphin.helper.InfiniteProgressBar;
import open.dolphin.infomodel.IInfoModel;

/**
 * StampBox の特別メニュー
 * @author pns
 * modified by masuda
 */
public class UserStampBoxExportImporter {

    private StampBoxPlugin context;
    private AbstractStampBox stampBox;
    private InfiniteProgressBar progressBar;

    public UserStampBoxExportImporter(StampBoxPlugin ctx) {
        super();
        context = ctx;
        stampBox = context.getUserStampBox();
    }
    
    private BlockGlass getBlockGlass() {
        BlockGlass blockGlass = context.getBlockGlass();
        blockGlass.setSize(context.getFrame().getSize());
        return blockGlass;
    }

    /**
     * スタンプを xml ファイルに書き出す
     */
    public void exportUserStampBox() {

        // 保存する StampTree の XML データを生成する

//masuda^   blockGlassを入れたりSwingWorkerを入れたり・・・

//masuda    エクスポートデータ作成より前にファイル選択させる
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooser.setDialogTitle("スタンプエクスポート");
        File current = fileChooser.getCurrentDirectory();
        fileChooser.setSelectedFile(new File(current.getPath(), "DolphinStamp.xml"));
        int selected = fileChooser.showSaveDialog(context.getFrame());

        if (selected == JFileChooser.APPROVE_OPTION) {

            final File file = fileChooser.getSelectedFile();
            if (!file.exists() || overwriteConfirmed(file)) {

                SwingWorker worker = new SwingWorker<String, Void>() {

                    @Override
                    protected String doInBackground() throws Exception {
//masuda    stampBytesを含めたデータを書き出す
                        ExtendedStampTreeXmlBuilder builder = new ExtendedStampTreeXmlBuilder();
                        ExtendedStampTreeXmlDirector director = new ExtendedStampTreeXmlDirector(builder);
                        //BlockGlass blockGlass = getBlockGlass();
                        //blockGlass.setText("スタンプ箱をエクスポート中です。");
                        //blockGlass.block();
                        ArrayList<StampTree> publishList = new ArrayList<StampTree>(IInfoModel.STAMP_ENTITIES.length);
                        publishList.addAll(stampBox.getAllTrees());
                        String ret = director.build(publishList);
                        return ret;
                    }

                    @Override
                    protected void done() {
                        String xml = null;
//minagawa^ mac jdk7                        
//                        FileOutputStream fos = null;
//                        OutputStreamWriter writer = null;
//minagawa$
                        try {
                            xml = get();
//minagawa^ mac jdk7                            
//                            fos = new FileOutputStream(file);
//                            writer = new OutputStreamWriter(fos, "UTF-8");
//                            // 書き出す内容
//                            writer.write(xml);
//                            writer.flush();
//minagawa$                            
                            Path destpath = file.toPath();
                            Files.write(destpath, xml.getBytes("UTF-8"));
                            
                        } catch (InterruptedException | ExecutionException ex) {
                            processException(ex);
                        } catch (FileNotFoundException ex) {
                            processException(ex);
                        } catch (UnsupportedEncodingException ex) {
                            processException(ex);
                        } catch (IOException ex) {
                            processException(ex);
                        } 
//                        finally {
//                            try {
//                                writer.close();
//                                fos.close();
//                            } catch (IOException | NullPointerException ex) {
//                                processException(ex);
//                            }
//                        }
                        BlockGlass blockGlass = getBlockGlass();
                        blockGlass.unblock();
                        progressBar.stop();
                        progressBar = null;
                    }

                    private void processException(Exception ex){
                        System.out.println("StampBoxPluginExtraMenu.java: " + ex);
                    }
                };
//minagawa^ 念のため doInbackground の外に出す               
                BlockGlass blockGlass = getBlockGlass();
                blockGlass.block();
                progressBar = new InfiniteProgressBar("StampBoxExport", "スタンプ箱をエクスポート中です...", stampBox);
                progressBar.start();
//minagawa$
                worker.execute();
            }
        }
//masuda$
    }

    /**
     * ファイル上書き確認ダイアログを表示する。
     * @param file 上書き対象ファイル
     * @return 上書きOKが指示されたらtrue
     */
    private boolean overwriteConfirmed(File file){
        String title = "上書き確認";
        String message = "既存のファイル " + file.toString() + "\n"
                        +"を上書きしようとしています。続けますか？";

        int confirm = JOptionPane.showConfirmDialog(
        //int confirm = MyJSheet.showConfirmDialog(
            context.getFrame(), message, title,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE );

        if(confirm == JOptionPane.OK_OPTION) {
            return true;
        }

        return false;
    }
    
    /**
     * xml ファイルから新しい userStampBox を作る
     * modified minagawa. doInBackgroundから component へのアクセスを外す。
     */
    public void importUserStampBox() {

        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        fileChooser.setDialogTitle("スタンプインポート");
        File current = fileChooser.getCurrentDirectory();
        //fileChooser.setSelectedFile(new File(current.getPath(), "DolphinStamp.xml"));
        //int selected = fileChooser.showSaveDialog(context.getFrame());
        int selected = fileChooser.showOpenDialog(context.getFrame());

        if (selected == JFileChooser.APPROVE_OPTION) {
            final File file = fileChooser.getSelectedFile();

            SwingWorker worker = new SwingWorker<List<StampTree>, Void>(){

                @Override
                protected List<StampTree> doInBackground() throws Exception {
                    //BlockGlass blockGlass = getBlockGlass();
                    //blockGlass.setText("スタンプ箱インポート中です。");
                    //blockGlass.block();
                    
                    // xml ファイルから StampTree 作成
//minagawa^ mac jdk7                    
                    //FileInputStream in = new FileInputStream(file);
                    //BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));   
                    BufferedReader reader = Files.newBufferedReader(file.toPath(), Charset.forName("UTF-8"));
//minagawa$                    
//masuda^   stampBytesを含めたデータを読み込む
                    ExtendedStampTreeDirector director
                            = new ExtendedStampTreeDirector(new ExtendedStampTreeBuilder());
//masuda$
                    List<StampTree> userTrees = director.build(reader);
                    reader.close();
                    
                    return userTrees;
                }

                @Override
                protected void done() {
                    try {
                        List<StampTree> userTrees = get();
                        int currentTab = stampBox.getSelectedIndex();
                        StampTreeTransferHandler transferHandler = new StampTreeTransferHandler();
                        for (final StampTree stampTree : userTrees) {
                            // ORCA は無視
                            if (stampTree.getEntity().equals(IInfoModel.ENTITY_ORCA)) {
                                continue;
                            }
                            // 読み込んだ stampTree から StampTreePanel を作る
                            stampTree.setUserTree(true);
                            stampTree.setTransferHandler(transferHandler);
                            stampTree.setStampBox(context);
                            StampTreePanel treePanel = new StampTreePanel(stampTree);

                            // 作った StampTreePanel を該当する tab に replace
                            String treeName = stampTree.getTreeName();
                            int index = stampBox.indexOfTab(treeName);
                            stampBox.removeTabAt(index);
                            //stampBox.addTab(treeName, treePanel, index);
                            stampBox.add(treePanel, treeName, index);
                        }
                        stampBox.setSelectedIndex(currentTab);
                        
                    } catch (InterruptedException | ExecutionException ex) {
                        processException(ex);
                    }
                    
                    BlockGlass blockGlass = getBlockGlass();
                    blockGlass.unblock();
                    progressBar.stop();
                    progressBar = null;
                }

                private void processException(Exception ex) {
                    System.out.println("StampBoxPluginExtraMenu.java: " + ex);
                }
            };
            
            BlockGlass blockGlass = getBlockGlass();
            blockGlass.block();
            progressBar = new InfiniteProgressBar("StampBoxImport", "スタンプ箱をインポート中です...", stampBox);
            progressBar.start();
            
            worker.execute();
        }
    }
}