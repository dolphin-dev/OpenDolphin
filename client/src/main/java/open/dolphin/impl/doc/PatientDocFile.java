package open.dolphin.impl.doc;

import java.awt.Color;
import java.awt.Window;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import open.dolphin.client.AbstractChartDocument;
import open.dolphin.client.ClientContext;
import open.dolphin.client.GUIConst;
import open.dolphin.delegater.PatientDelegater;
import open.dolphin.infomodel.PatientFileModel;
import open.dolphin.project.Project;
import open.dolphin.util.HashUtil;

/**
 *
 * @author Kazushi Minagawa. Digital Globe, Inc.
 */
public class PatientDocFile extends AbstractChartDocument {
    
    private PatientDocFileView view;
    private AbstractTableModel tableModel;
    private JTable table;
    private List<PatientFileModel> dataList;
    private String[] columnNames = {"種 類", "最終更新日", "タイプ", "サイズ", "メ モ"};
    private String lastFormat = "yyyy'年'MM'月'dd'日 'HH'時'mm'分'ss'秒'";
    private String[] docTypes = {"選択してください", "紹介状", "返書", "診断書", "訪問看護指示書", "", "", ""};
    private AbstractAction deleteAction;
    private AbstractAction addAction;
    private AbstractAction saveAction;
    private AbstractAction openAction;
    private PatientFileModel selected;
    
    public PatientDocFile() {
        setTitle("文書ファイル");
    }
    
    public List<PatientFileModel> getDataList() {
        return dataList;
    }

    @Override
    public void start() {
        
        // createGUI
        view = new PatientDocFileView();
        
        // Actions
//minagawa^ Icon Server        
        //deleteAction = new AbstractAction("削除", ClientContext.getImageIcon("del_16.gif")) {
        deleteAction = new AbstractAction("削除", ClientContext.getImageIconArias("icon_delete_small")) {
//minagawa$            
            @Override
            public void actionPerformed(ActionEvent ae) {
                //delete();
            }
        };
        
//minagawa^ Icon Server         
        //addAction = new AbstractAction("追加", ClientContext.getImageIcon("add_16.gif")) {
        addAction = new AbstractAction("追加", ClientContext.getImageIconArias("icon_add_small")) {    
//minagawa$            
            @Override
            public void actionPerformed(ActionEvent ae) {
                add();
            }
        };
        
//minagawa^ Icon Server         
        //saveAction = new AbstractAction("保存", ClientContext.getImageIcon("save_16.gif")) {
        saveAction = new AbstractAction("保存", ClientContext.getImageIconArias("icon_save_small")) {    
//minagawa$            
            @Override
            public void actionPerformed(ActionEvent ae) {
                save();
            }
        };
        
//minagawa^ Icon Server         
        //openAction = new AbstractAction("開く", ClientContext.getImageIcon("open_16.gif")) {
        openAction = new AbstractAction("開く", ClientContext.getImageIconArias("icon_open_small")) {    
//minagawa$            
            @Override
            public void actionPerformed(ActionEvent ae) {
                //open();
            }
        };
        
        // TableModel
        tableModel = new AbstractTableModel() {
            
            @Override
            public boolean isCellEditable(int row, int col) {
                return (col==4);    // memo
            }

            @Override
            public int getRowCount() {
                return (dataList!=null) ? dataList.size() : 0;
            }

            @Override
            public int getColumnCount() {
                return columnNames.length;
            }
            
            @Override
            public String getColumnName(int index) {
                return columnNames[index];
            }

            @Override
            public Object getValueAt(int row, int col) {
                
                if (dataList==null || (row>=dataList.size()) || row<0) {
                    return null;
                }
                
                PatientFileModel model = dataList.get(row);
                
                String ret = null;
                
                switch (col) {
                    
                    case 0:
                        ret = model.getDocType();
                        break;
                        
                    case 1:
                        ret = millisToTime(model.getLastModified());
                        break;
                        
                    case 2:
                        ret = model.getContentType();
                        break;
                        
                    case 3:
                        ret = String.valueOf(model.getContentSize());
                        break;
                        
                    case 4:
                        ret = model.getMemo();
                        break;
                }
                
                return ret;
            }
            
            @Override
            public void setValueAt(Object o, int row, int col) {
                
                if (dataList==null || row>=dataList.size() || row<0) {
                    return;
                }
               
                PatientFileModel model = dataList.get(row);
                String memo = ((String)o).trim();
                model.setMemo(memo);
            }
        };
        
        // FileList table
        table = view.getTable();
        table.setModel(tableModel);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setDragEnabled(true);
        table.setTransferHandler(new PatientFileTableTransferHandler(this));
        table.setRowHeight(ClientContext.getMoreHigherRowHeight());

//        TableColumn column;
//        for (int i = 0; i < columnCount; i++) {
//            column = view.getTable().getColumnModel().getColumn(i);
//            column.setPreferredWidth(cellWidth);
//        }
//        table.setRowHeight(cellHeight);
        
        table.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount()==1) {
                    int row = table.getSelectedRow();
                    PatientFileModel entry = (dataList!=null && row<dataList.size() && row>=0) ? dataList.get(row) : null;
                    boolean enabled = (entry!=null);
                    
                    Action copy = getContext().getChartMediator().getAction(GUIConst.ACTION_COPY);
                    copy.setEnabled(enabled);
                    view.getOpenBtn().setEnabled(enabled);
                    view.getSaveBtn().setEnabled(enabled);
                    view.getDeleteBtn().setEnabled(enabled);
                }
                else if(e.getClickCount()==2) {
                    int row = table.getSelectedRow();
                    PatientFileModel entry = (dataList!=null && row<dataList.size() && row>=0) ? dataList.get(row) : null;
                    if (entry != null) {
                        selected = entry;
                        open();
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent me) {
                mabeShowPopup(me);
            }

            @Override
            public void mouseReleased(MouseEvent me) {
                mabeShowPopup(me);
            }

            private void mabeShowPopup(MouseEvent e) {

                if (!e.isPopupTrigger()) {
                    return;
                }

                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                Object entry = tableModel.getValueAt(row, col);

                if (entry==null) {
                    return;
                }

                JPopupMenu contextMenu = new JPopupMenu();
                JMenuItem micp = new JMenuItem("コピー");
                Action copy = getContext().getChartMediator().getAction(GUIConst.ACTION_COPY);
                micp.setAction(copy);
                contextMenu.add(micp);
                
                contextMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        });
        
        // Action
        view.getAddBtn().setAction(addAction);
        view.getOpenBtn().setAction(openAction);
        view.getSaveBtn().setAction(saveAction);
        view.getDeleteBtn().setAction(deleteAction);
        
        // 種類選択
        view.getFilterCombo().addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent ie) {
                if (ie.getStateChange()==ItemEvent.SELECTED) {
                    String docType = (String)view.getFilterCombo().getSelectedItem();
                    fetchFiles();
                }
            }
        });
        
        // Disables
        view.getOpenBtn().setEnabled(false);
        view.getSaveBtn().setEnabled(false);
        view.getDeleteBtn().setEnabled(false);
        
        setUI(view);
        
        // Fetch data
        fetchFiles();
        
    }

    @Override
    public void stop() {
        if (dataList!=null) {
            dataList.clear();
        }
    }
    
    private void open() {
        
    }
    
    public void fileDropped(List<File> files) {
        
        final File file = files.get(0);
                
        SwingWorker worker = new SwingWorker<PatientFileModel, Void>() {

            @Override
            protected PatientFileModel doInBackground() throws Exception {
                
                // Content を読み込む
                FileInputStream in = new FileInputStream(file);
                FileChannel fChan = in.getChannel();
                long len = fChan.size();
                byte[] data = new byte[(int)len];
                ByteBuffer buf = ByteBuffer.allocate((int)len);
                fChan.read(buf);
                buf.rewind();
                buf.get(data);
                fChan.close();
                in.close();
                
                // Extension & Icon
                String fileName = file.getName();
                int index = fileName.lastIndexOf(".");
                String ext = null;
                if (index>=0) {
                    ext = fileName.substring(index+1).toLowerCase();
                }
                
                // MIME type
                FileNameMap fileNameMap = URLConnection.getFileNameMap();
                String type = fileNameMap.getContentTypeFor(file.getName());
                type = type !=null ? type : ext;
                
                // PatientFileModel を生成する
                PatientFileModel attachment = new PatientFileModel();
                attachment.setFileName(file.getName());             // file name;
                attachment.setLocation(file.getParent());           // location
                attachment.setContentType(type);                    // mime type
                attachment.setContentSize(len);                     // length
                attachment.setLastModified(file.lastModified());    // lastmodified
                attachment.setDigest(HashUtil.MD5(data));           // md5
                attachment.setExtension(ext);                       // extension
                attachment.setFileData(data);                       // data bytes
                    
                return attachment;
            }
            
            @Override
            protected void done() {
                try {
                    PatientFileModel model = get();
                    showFileInfo(model);
                } catch (InterruptedException ex) {
                    ex.printStackTrace(System.err);
                } catch (ExecutionException ex) {
                    ex.printStackTrace(System.err);
                }
            }
        };
       
        worker.execute();
    }
    
    // 保存するファイルを選択する。
    private void add() {
        // Fileを選択する
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnVal = chooser.showOpenDialog(null);

        if (returnVal!=JFileChooser.APPROVE_OPTION) {
            return;
        }
        
        // 添付または挿入
        File file = new File(chooser.getSelectedFile().getPath());
        List<File> list = new ArrayList<File>(1);
        list.add(file);
        fileDropped(list);
    }
    
    
    /**
     * File の情報を表示し、種別を選択させて DB へ登録する。
     * @param model PatientFileModel
     */
    private void showFileInfo(final PatientFileModel model) {
        
        // View
        final PatientFileInfoView infoView = new PatientFileInfoView();
        
        // 情報を設定する
        infoView.getFileNameLbl().setText(model.getFileName());
        infoView.getLocationLbl().setText(model.getLocation());
        infoView.getTypeLbl().setText(model.getContentType());
        
        // 容量を制限する 1TB とか保存する人がいるので...
        int maxSize = Project.getInt("attachment.max.size"); // Default = 1MB
        int attSize = (int)model.getContentSize();
        boolean canAttach = (attSize <= maxSize);
        StringBuilder sb = new StringBuilder(); 
        if (canAttach) {
            sb.append(String.valueOf(attSize)).append(" バイト");
        } else {
            sb.append("保存できません。(最大容量=");
            sb.append(Project.getString("attachment.max.size.display"));
            sb.append("まで)");
            infoView.getSizeLbl().setForeground(Color.red);
        }
        infoView.getSizeLbl().setText(sb.toString());
        
        infoView.getLastModifiedLbl().setText(millisToTime(model.getLastModified()));
        infoView.getDigestLbl().setText(model.getDigest());
        
        // 文書種別コンボボックスの中味を入れ替える
        infoView.getDocTypeCombo().removeAllItems();
        for (String str : docTypes) {
            infoView.getDocTypeCombo().addItem(str);
        }
        infoView.getDocTypeCombo().setEditable(true);
        
        // Option
        String okText = (String)UIManager.get("OptionPane.okButtonText");
        String cancelText = (String)UIManager.get("OptionPane.cancelButtonText");
        final JButton okBtn = new JButton(okText);
        final JButton cancelBtn = new JButton(cancelText);
        Object[] options = new JButton[]{okBtn, cancelBtn};
        
        infoView.getDocTypeCombo().addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange()==ItemEvent.SELECTED){
                    String data = (String)infoView.getDocTypeCombo().getSelectedItem();
                    if (data!=null && (!data.equals("")) && (!data.equals(docTypes[0]))) {
                        okBtn.setEnabled(true);
                    } else {
                        okBtn.setEnabled(false);
                    }
                }
            }
        });
        final JTextField maybe = (JTextField)infoView.getDocTypeCombo().getEditor().getEditorComponent();
        maybe.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent de) {
                String text = maybe.getText().trim();
                okBtn.setEnabled((!text.equals("")));
            }

            @Override
            public void removeUpdate(DocumentEvent de) {
                String text = maybe.getText().trim();
                okBtn.setEnabled((!text.equals("")));
            }

            @Override
            public void changedUpdate(DocumentEvent de) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
        
        infoView.getDocTypeCombo().setSelectedIndex(0);
        infoView.getDocTypeCombo().setEnabled(canAttach);
        okBtn.setEnabled(false);
        
        // Dialogを表示する
        Window w = SwingUtilities.getWindowAncestor(getUI());
        JOptionPane pane = new JOptionPane(infoView,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                options,
                okBtn);
        
        final JDialog dialog = pane.createDialog(w, ClientContext.getFrameTitle("文書ファイル保存"));
        dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        dialog.addWindowListener(new WindowAdapter() {

            @Override
            public void windowOpened(WindowEvent we) {
                infoView.getDocTypeCombo().requestFocusInWindow();
            }

            @Override
            public void windowClosing(WindowEvent we) {
                dialog.setVisible(false);
                dialog.dispose();
            }
        });
        
        cancelBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                dialog.setVisible(false);
                dialog.dispose();
            }
        });
        
        okBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                dialog.setVisible(false);
                dialog.dispose();
                model.setDocType((String)infoView.getDocTypeCombo().getSelectedItem());
                String memo = infoView.getMemoField().getText().trim();
                if (!memo.equals("")) {
                    model.setMemo(memo);
                }
                saveFile(model);
            }
        });
        dialog.setVisible(true);
        
    }
    
    private String millisToTime(long l) {
        return new SimpleDateFormat(lastFormat).format(new Date(l));
    }
    
    private void fetchFiles() {
        
        SwingWorker worker = new SwingWorker<List<PatientFileModel>, Void>() {

            @Override
            protected List<PatientFileModel> doInBackground() throws Exception {
                PatientDelegater pdl = new PatientDelegater();
                return null;
            }
            
            @Override
            protected void done() {
                try {
                    List<PatientFileModel> list = get();
                    if (list!=null) {
                        if (dataList!=null) {
                            dataList.clear();
                            dataList = null;
                        }
                        dataList = list;
                        tableModel.fireTableDataChanged();
                        view.getCountLbl().setText(dataList.size()+ " 件");
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace(System.err);
                } catch (ExecutionException ex) {
                    ex.printStackTrace(System.err);
                }
            }
        };
        
        view.getCountLbl().setText("0 件");
        worker.execute();
    }
    
    private void saveFile(final PatientFileModel model) {
        
        SwingWorker worker = new SwingWorker<Long, Void>() {

            @Override
            protected Long doInBackground() throws Exception {
                PatientDelegater pdl = new PatientDelegater();
                return new Long(1);
            }
            
            @Override
            protected void done() {
                try {
                    Long pk = get();
                    if (pk!=null) {
                        model.setId(pk.longValue());
                        if (dataList==null) {
                            dataList = new ArrayList<PatientFileModel>();
                        }
                        dataList.add(model);
                        tableModel.fireTableDataChanged();
                        view.getCountLbl().setText(dataList.size()+ " 件");
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace(System.err);
                } catch (ExecutionException ex) {
                    ex.printStackTrace(System.err);
                }
            }
        };
        
        worker.execute();
    }
}
