package open.dolphin.project;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import open.dolphin.client.AutoRomanListener;

/**
 * @author Kazushi Minagawa Digital Globe, Inc.
 *
 */
public class IFSettingPanel extends AbstractSettingPanel {
    
    private static final String ID = "ifSetting";
    private static final String TITLE = "リレー等";
//minagawa^ Icon Server    
    private static final String ICON = "icon_interface_settings_small";
//minagawa$    
    
    // View
    private IFSettingView view;
    
    private StateMgr stateMgr;
    
    public IFSettingPanel() {
        this.setId(ID);
        this.setTitle(TITLE);
        this.setIcon(ICON);
    }
    
    /**
     * MML出力を開始する。
     */
    @Override
    public void start() {
        
        // GUI
        initComponents();
        
        // populate
        populate(getProjectStub());
    }
    
    /**
     * 保存する。
     */
    @Override
    public void save() {
        restore(getProjectStub());
    }
    
    /**
     * GUIを構築する。
     */
    private void initComponents() {
        view = new IFSettingView();
        setUI(view);
        connectMml();
        connectKartePDF();
        connectPvtRelay();
    }
    
    private void connectMml() {
        
        stateMgr = new StateMgr();
        
        // MML送信ボタン
        view.getSendMmlView().getSendMmlChk().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stateMgr.controlSendMml();
            }
        });
        
        // 出力先ディレクトリ
        view.getSendMmlView().getSendMmlDirField().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                stateMgr.checkState();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                stateMgr.checkState();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                stateMgr.checkState();
            }
        });
        
        // IME OFF FocusAdapter
        view.getSendMmlView().getSendMmlDirField().addFocusListener(AutoRomanListener.getInstance());

        // MML出力先選択ボタン
        view.getSendMmlView().getSendMmlSelectionBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                int returnVal = chooser.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    String dir = chooser.getSelectedFile().getPath();
                    view.getSendMmlView().getSendMmlDirField().setText(dir);
                }
            }
        });
        
        // Eventを利用して制御する
        view.getSendMmlView().getSendMmlDirField().setEnabled(false);
        view.getSendMmlView().getSendMmlSelectionBtn().setEnabled(false);
        view.getSendMmlView().getSendMml3Radio().setEnabled(false);
        view.getSendMmlView().getSendMml23Radio().setEnabled(false);
    }
        
    private void connectKartePDF() {
        
        // 送信ボタン
        view.getSendKartePDFView().getSendKartePDFChk().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stateMgr.controlSendKartePDF();
            }
        });
        
        // 出力先ディレクトリ
        view.getSendKartePDFView().getSendKartePDFDirField().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                stateMgr.checkState();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                stateMgr.checkState();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                stateMgr.checkState();
            }
        });
        
        // IME OFF FocusAdapter
        view.getSendKartePDFView().getSendKartePDFDirField().addFocusListener(AutoRomanListener.getInstance());

        // PDF出力先選択ボタン
        view.getSendKartePDFView().getSendKartePDFSelectionBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                int returnVal = chooser.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    String dir = chooser.getSelectedFile().getPath();
                    view.getSendKartePDFView().getSendKartePDFDirField().setText(dir);
                }
            }
        });
        
        // Eventを利用して制御する
        view.getSendKartePDFView().getSendKartePDFDirField().setEnabled(false);
        view.getSendKartePDFView().getSendKartePDFSelectionBtn().setEnabled(false);
    }
    
    private void connectPvtRelay() {
        
        // 受付リレーチェック
        view.getPvtRelayView().getPvtRelayCheck().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stateMgr.controlPvtRelay();
            }
        });
        
        // 出力先ディレクトリ
        view.getPvtRelayView().getPvtRelayDirField().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                stateMgr.checkState();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                stateMgr.checkState();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                stateMgr.checkState();
            }
        });
        
        // IME OFF FocusAdapter
        view.getPvtRelayView().getPvtRelayDirField().addFocusListener(AutoRomanListener.getInstance());

        // Relay先選択ボタン
        view.getPvtRelayView().getPvtRelaySelectionBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                int returnVal = chooser.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    String dir = chooser.getSelectedFile().getPath();
                    view.getPvtRelayView().getPvtRelayDirField().setText(dir);
                }
            }
        });
        
        // エンコーディング
        ButtonGroup bg = new ButtonGroup();
        bg.add(view.getPvtRelayView().getPvtRelayUTF8Radio());
        bg.add(view.getPvtRelayView().getPvtRelaySHIFTJISRadio());
        bg.add(view.getPvtRelayView().getPvtRelayEUCRadio());
        
        // Eventを利用して制御する
        view.getPvtRelayView().getPvtRelayDirField().setEnabled(false);
        view.getPvtRelayView().getPvtRelaySelectionBtn().setEnabled(false);
        view.getPvtRelayView().getPvtRelayUTF8Radio().setEnabled(false);
        view.getPvtRelayView().getPvtRelaySHIFTJISRadio().setEnabled(false);
        view.getPvtRelayView().getPvtRelayEUCRadio().setEnabled(false);
    }
        
    private void populate(ProjectStub stub) {
        
        // MML送信
        boolean send = stub.getBoolean(Project.SEND_MML);
        if (send) {
            view.getSendMmlView().getSendMmlChk().doClick();
        }
        
        // version3 -> disabled
        view.getSendMmlView().getSendMml23Radio().setSelected(true);
        
        // 送信ディレクトリ
        String val = stub.getString(Project.SEND_MML_DIRECTORY);
        if (val != null && ! val.equals("")) {
            view.getSendMmlView().getSendMmlDirField().setText(val);
        }
        
        //----------------------------------------------
        // Karte PDF 送信
        boolean sendKartePDF = stub.getBoolean(Project.KARTE_PDF_SEND_AT_SAVE);
        if (sendKartePDF) {
            view.getSendKartePDFView().getSendKartePDFChk().doClick();
        }
        
        // 送信ディレクトリ
        val = stub.getString(Project.KARTE_PDF_SEND_DIRECTORY);
        if (val != null && ! val.equals("")) {
            view.getSendKartePDFView().getSendKartePDFDirField().setText(val);
        }
        
        //----------------------------------------------
        
        // Relay
        boolean relay = stub.getBoolean(Project.PVT_RELAY);
        if (relay) {
            view.getPvtRelayView().getPvtRelayCheck().doClick();
        }
        
        // Relayディレクトリ
        val = stub.getString(Project.PVT_RELAY_DIRECTORY);
        if (val != null && ! val.equals("")) {
            view.getPvtRelayView().getPvtRelayDirField().setText(val);
        }
        
        // デフォルト値で UTF-8が与えられている
        val = stub.getString(Project.PVT_RELAY_ENCODING, "utf8").toLowerCase();
        val = val.replaceAll("-", "");
        val = val.replaceAll("_", "");
        if (val.equals("utf8")) {
            view.getPvtRelayView().getPvtRelayUTF8Radio().setSelected(true);
        } else if (val.equals("shiftjis")) {
            view.getPvtRelayView().getPvtRelaySHIFTJISRadio().setSelected(true);
        } else if (val.equals("eucjp")) {
            view.getPvtRelayView().getPvtRelayEUCRadio().setSelected(true);
        }
    }

    private void restore(ProjectStub stub) {
        
        // MML送信
        boolean send = view.getSendMmlView().getSendMmlChk().isSelected();
        stub.setBoolean(Project.SEND_MML, send);

        // MML バージョン
        stub.setString(Project.MML_VERSION, "230");

        // 送信先ディレクトリ null値は propertiesに設定できない
        String dir = send ? view.getSendMmlView().getSendMmlDirField().getText().trim() : "";
        stub.setString(Project.SEND_MML_DIRECTORY, dir);
        
        //----------------------------------------------
        // Karte PDF 送信
        boolean sendKartePDF = view.getSendKartePDFView().getSendKartePDFChk().isSelected();
        stub.setBoolean(Project.KARTE_PDF_SEND_AT_SAVE, sendKartePDF);

        // 送信先ディレクトリ null値は propertiesに設定できない
        dir = sendKartePDF ? view.getSendKartePDFView().getSendKartePDFDirField().getText().trim() : "";
        stub.setString(Project.KARTE_PDF_SEND_DIRECTORY, dir);
        
        //----------------------------------------------
        
        // Relay
        boolean relay = view.getPvtRelayView().getPvtRelayCheck().isSelected();
        stub.setBoolean(Project.PVT_RELAY, relay);
        
        // Relayディレクトリ null値は propertiesに設定できない
        String relayDir = relay ? view.getPvtRelayView().getPvtRelayDirField().getText().trim() : "";
        stub.setString(Project.PVT_RELAY_DIRECTORY, relayDir);
        
        if (view.getPvtRelayView().getPvtRelayUTF8Radio().isSelected()) {
            stub.setString(Project.PVT_RELAY_ENCODING, "utf8");
        } else if (view.getPvtRelayView().getPvtRelaySHIFTJISRadio().isSelected()) {
            stub.setString(Project.PVT_RELAY_ENCODING, "shiftjis");
        } else if (view.getPvtRelayView().getPvtRelayEUCRadio().isSelected()) {
            stub.setString(Project.PVT_RELAY_ENCODING, "eucjp");
        }
    }
    
    class StateMgr {
        
        public void checkState() {
            
            AbstractSettingPanel.State newState = (isMmlValid() && isRelayValid() && isKartePDFValid())
                    ? AbstractSettingPanel.State.VALID_STATE
                    : AbstractSettingPanel.State.INVALID_STATE;
            if (newState != state) {
                setState(newState);
            }
        }
        
        public void controlSendMml() {
            boolean send = view.getSendMmlView().getSendMmlChk().isSelected();
            view.getSendMmlView().getSendMmlDirField().setEnabled(send);
            view.getSendMmlView().getSendMmlSelectionBtn().setEnabled(send);
            view.getSendMmlView().getSendMml23Radio().setEnabled(send);
            this.checkState();
        }
        
        public void controlSendKartePDF() {
            boolean send = view.getSendKartePDFView().getSendKartePDFChk().isSelected();
            view.getSendKartePDFView().getSendKartePDFDirField().setEnabled(send);
            view.getSendKartePDFView().getSendKartePDFSelectionBtn().setEnabled(send);
            this.checkState();
        }
        
        public void controlPvtRelay() {
            boolean relay = view.getPvtRelayView().getPvtRelayCheck().isSelected();
            view.getPvtRelayView().getPvtRelayDirField().setEnabled(relay);
            view.getPvtRelayView().getPvtRelaySelectionBtn().setEnabled(relay);
            view.getPvtRelayView().getPvtRelayUTF8Radio().setEnabled(relay);
            view.getPvtRelayView().getPvtRelaySHIFTJISRadio().setEnabled(relay);
            view.getPvtRelayView().getPvtRelayEUCRadio().setEnabled(relay);
            this.checkState();
        }
        
        protected boolean isMmlValid() {
            
            boolean send = view.getSendMmlView().getSendMmlChk().isSelected();
            if (!send) {
                return true;
            }
            
            boolean ok = true;
            ok = ok && (!view.getSendMmlView().getSendMmlDirField().getText().trim().equals(""));
            return ok;
        }
        
        protected boolean isKartePDFValid() {
            
            boolean send = view.getSendKartePDFView().getSendKartePDFChk().isSelected();
            if (!send) {
                return true;
            }
            
            boolean ok = true;
            ok = ok && (!view.getSendKartePDFView().getSendKartePDFDirField().getText().trim().equals(""));
            return ok;
        }
        
        protected boolean isRelayValid() {
            
            boolean relay = view.getPvtRelayView().getPvtRelayCheck().isSelected();
            if (!relay) {
                return true;
            }
            
            boolean ok = true;
            ok = ok && (!view.getPvtRelayView().getPvtRelayDirField().getText().trim().equals(""));
            return ok;
        }
    }
}
