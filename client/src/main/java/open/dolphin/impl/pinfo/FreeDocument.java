package open.dolphin.impl.pinfo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.print.PrinterException;
import java.util.Date;
import javax.swing.*;
import javax.swing.event.DocumentListener;
import open.dolphin.client.AbstractChartDocument;
import open.dolphin.client.AutoKanjiListener;
import open.dolphin.client.BundleTransferHandler;
import open.dolphin.client.ClientContext;
import open.dolphin.client.CutCopyPasteAdapter;
import open.dolphin.client.GUIConst;
import open.dolphin.delegater.DocumentDelegater;
import open.dolphin.infomodel.PatientFreeDocumentModel;

/**
 * サマリー対応
 *
 * @author S.Oh@Life Sciences Computing Corporation.
 */
public class FreeDocument extends AbstractChartDocument {
    
    // Title
//    private static final String TITLE = "サマリー";
    
    private boolean dirty;

    private JPanel freeDocPanel;
    
    private JTextArea freeDocArea;

    private PatientFreeDocumentModel model;
    
    /** 
     * Creates new FreeDocument 
     */
    public FreeDocument() {
        String title = ClientContext.getMyBundle(FreeDocument.class).getString("title.document");
        setTitle(title);
    }
    
    private void initialize() {
        
        freeDocArea = new JTextArea();
        freeDocArea.putClientProperty("karteCompositor", this);
        freeDocArea.setLineWrap(true);
        freeDocArea.setMargin(new java.awt.Insets(3, 3, 2, 2));
        freeDocArea.addFocusListener(AutoKanjiListener.getInstance());
        //freeDocArea.setEnabled(!getContext().isReadOnly());
        freeDocArea.setEditable(!getContext().isReadOnly());
        freeDocPanel = new JPanel(new BorderLayout());
        final JScrollPane scroller = new JScrollPane(freeDocArea);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if(scroller != null) {
                    scroller.getVerticalScrollBar().setValue(0);
                }
                freeDocArea.setCaretPosition(0);
            }
        });
        freeDocArea.setTransferHandler(new TransferHandler() {
            @Override
            public boolean importData(TransferHandler.TransferSupport support) {
                return false;
            }
        });
        freeDocPanel.add(scroller, BorderLayout.CENTER);

        Dimension size = freeDocPanel.getPreferredSize();
        int h = size.height;
        int w = 268;
        size = new Dimension(w, h);
        freeDocPanel.setMinimumSize(size);
        freeDocPanel.setMaximumSize(size);
        
        setUI(freeDocPanel);
        
        DocumentDelegater ddl = new DocumentDelegater();
        try {
            model = ddl.getPatientFreeDocument(getContext().getPatient().getPatientId());
        } catch (Exception ex) {
        } catch (Throwable ex) {
        }
        if(model == null) {
            model = new PatientFreeDocumentModel();
        }
        freeDocArea.setText(model.getComment());
        
        freeDocArea.getDocument().addDocumentListener(new DocumentListener() {
            
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                dirtySet();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                dirtySet();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
            }
        });

        // TransferHandlerを設定する
        freeDocArea.setTransferHandler(new BundleTransferHandler(getContext().getChartMediator(), freeDocArea));

        if(!getContext().isReadOnly()) {
            freeDocArea.addMouseListener(CutCopyPasteAdapter.getInstance());
        }
        
        enter();
    }
    
    private void dirtySet() {
        dirty = true;
    }
    
    @Override
    public void start() {
        initialize();
    }
    
    @Override
    public void stop() {
        save();
    }
    
    @Override
    public void enter() {
        super.enter();
        getContext().enabledAction(GUIConst.ACTION_NEW_KARTE, false);           // 新規カルテ
        getContext().enabledAction(GUIConst.ACTION_NEW_DOCUMENT, false);        // 新規文書
        getContext().enabledAction(GUIConst.ACTION_MODIFY_KARTE, false);        // 修正
        getContext().enabledAction(GUIConst.ACTION_DELETE, false);              // 削除
        getContext().enabledAction(GUIConst.ACTION_PRINT, false);               // 印刷   
        getContext().enabledAction(GUIConst.ACTION_ASCENDING, false);           // 昇順
        getContext().enabledAction(GUIConst.ACTION_DESCENDING, false);          // 降順
        getContext().enabledAction(GUIConst.ACTION_SHOW_MODIFIED, false);       // 修正履歴表示
        getContext().enabledAction(GUIConst.ACTION_SEND_CLAIM, false);          // CLAIM送信
        getContext().enabledAction(GUIConst.ACTION_CREATE_PRISCRIPTION, false); // 処方箋印刷
        getContext().enabledAction(GUIConst.ACTION_CHECK_INTERACTION, false);   // 併用禁忌チェック
    }
    
    @Override
    public boolean isDirty() {
//        if (stateMgr != null) {
//            return stateMgr.isDirtyState();
//        } else {
//            return super.isDirty();
//        }
        return false;
    }
    
    /**
     * 患者情報を更新する。
     */
    @Override
    public void save() {
        
        if (!dirty || getContext().isReadOnly()) {
            return;
        }

        if (model == null) {
            model =  new PatientFreeDocumentModel();
        }
        Date confirmed = new Date();
        model.setFacilityPatId(getContext().getPatient().getPatientId());
        model.setConfirmed(confirmed);
        model.setComment(freeDocArea.getText());

        final DocumentDelegater ddl = new DocumentDelegater();

        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    ddl.updatePatientFreeDocument(model);
                    model = null;
                    //context = null;
                } catch (Exception e) {
                }
            }
        };

        Thread t = new Thread(r);
        t.setPriority(Thread.NORM_PRIORITY);
        t.start();
    }
    
    @Override
    public void print() {
        if(freeDocArea != null && freeDocArea.getText().length() > 0) {
            try {
                freeDocArea.print();
            } catch (PrinterException ex) {
            }
        }
    }
}