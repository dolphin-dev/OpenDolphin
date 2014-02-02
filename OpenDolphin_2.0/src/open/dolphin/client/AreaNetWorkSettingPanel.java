package open.dolphin.client;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import open.dolphin.helper.GridBagBuilder;

import open.dolphin.project.ProjectStub;

/**
 * AreaNetWorkSettingPanel
 *
 * @author Kazushi Minagawa Digital Globe, Inc.
 *
 */
public class AreaNetWorkSettingPanel extends AbstractSettingPanel {
    
    private static final String ID = "areaNetwork";
    private static final String TITLE = "地域連携";
    private static final String ICON = "web_16.gif";
    
    // 地域連携用コンポーネント
    private JRadioButton joinAreaNetwork;
    private JRadioButton noJoinAreaNetwork;
    private JComboBox areaNetworkCombo;
    private JTextField facilityIdField;
    private JTextField creatorIdField;
    private NameValuePair[] networkProjects;
    
    private NetworkModel model;
    
    private StateMgr stateMgr;
    
    public AreaNetWorkSettingPanel() {
        setId(ID);
        this.setTitle(TITLE);
        this.setIcon(ICON);
    }
    
    /**
     * 地域連携設定を開始する。
     */
    @Override
    public void start() {
        
        //
        // モデルを生成する
        //
        model = new NetworkModel();
        
        //
        // GUIを生成する
        //
        initComponents();
        
        //
        // populate
        //
        model.populate(getProjectStub());
                
    }
    
    /**
     * 保存する。
     */
    @Override
    public void save() {
        model.restore(getProjectStub());
    }
    
    /**
     * GUIを生成する
     */
    private void initComponents() {
        
        ButtonGroup bg = new ButtonGroup();
        joinAreaNetwork = GUIFactory.createRadioButton("参加する", null, bg);
        noJoinAreaNetwork = GUIFactory.createRadioButton("参加しない", null, bg);
        networkProjects = ClientContext.getNameValuePair("areaNetwork.list");
        areaNetworkCombo = new JComboBox(networkProjects);
        facilityIdField = GUIFactory.createTextField(20, null, null, null);
        creatorIdField = GUIFactory.createTextField(20, null, null, null);
               
        // 地域連携情報
        GridBagBuilder gbl = new GridBagBuilder("地域連携");
        gbl.add(GUIFactory.createRadioPanel(new JRadioButton[]{noJoinAreaNetwork,joinAreaNetwork}),0, 0, 2, 1, GridBagConstraints.CENTER);
        gbl.add(new JLabel("プロジェクト:"), 0, 1, GridBagConstraints.EAST);
        gbl.add(areaNetworkCombo,	   1, 1, GridBagConstraints.WEST);
        gbl.add(new JLabel("連携用医療機関ID:"), 0, 2, GridBagConstraints.EAST);
        gbl.add(facilityIdField, 	      1, 2, GridBagConstraints.WEST);
        gbl.add(new JLabel("連携用ユーザID:"),  0, 3, GridBagConstraints.EAST);
        gbl.add(creatorIdField, 	      1, 3, GridBagConstraints.WEST);
        JPanel content = gbl.getProduct();
        
        // 全体をレイアウトする
        gbl = new GridBagBuilder();
        gbl.add(content,        0, 0, GridBagConstraints.HORIZONTAL, 1.0, 0.0);
        gbl.add(new JLabel(""), 0, 1, GridBagConstraints.BOTH,       1.0, 1.0);
        
        setUI(gbl.getProduct());
       
    }
    
    public void connect() {
        
        stateMgr = new StateMgr();
        
        // 地域連携参加ボタンのActionListenerを生成する
        ActionListener alArea = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                stateMgr.controlJoinArea();
            }
        };
        joinAreaNetwork.addActionListener(alArea);
        noJoinAreaNetwork.addActionListener(alArea);
        
        // 地域連携名のリスナを生成する
        areaNetworkCombo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    stateMgr.checkState();
                }
            }
        });
        
        // DocumentListener
        //DocumentListener dl = ProxyDocumentListener.create(stateMgr, "checkState");
        DocumentListener dl = new DocumentListener() {

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
        };
        facilityIdField.getDocument().addDocumentListener(dl);
        creatorIdField.getDocument().addDocumentListener(dl);
        
        //
        // IME OFF FocusAdapter
        //
        facilityIdField.addFocusListener(AutoRomanListener.getInstance());
        creatorIdField.addFocusListener(AutoRomanListener.getInstance());
        
        stateMgr.controlJoinArea();
    }
    
    class NetworkModel {
        
        public void populate(ProjectStub stub) {
            
            boolean join = stub.getJoinAreaNetwork();
            joinAreaNetwork.setSelected(join);
            noJoinAreaNetwork.setSelected(! join);
            
            String val = stub.getAreaNetworkName();
            if (val != null) {
                for (int i = 0; i < networkProjects.length; i++) {
                    if (val.equals(networkProjects[i].getValue())) {
                        areaNetworkCombo.setSelectedIndex(i);
                        break;
                    }
                }
            }
            
            val = stub.getAreaNetworkFacilityId();
            val = val != null ? val : "";
            facilityIdField.setText(val);
            
            val = stub.getAreaNetworkCreatorId();
            val = val != null ? val : "";
            creatorIdField.setText(val);
            
            connect();
        }
        
        public void restore(ProjectStub stub) {
            
            boolean join = joinAreaNetwork.isSelected();
            stub.setJoinAreaNetwork(join);
            
            NameValuePair pair = (NameValuePair) areaNetworkCombo.getSelectedItem();
            stub.setAreaNetworkName(pair.getValue());
            
            String val = facilityIdField.getText().trim();
            if (!val.equals("")) {
                stub.setAreaNetworkFacilityId(val);
            }
            
            val = creatorIdField.getText().trim();
            if (!val.equals("")) {
                stub.setAreaNetworkCreatorId(val);
            }
        }
    }
    
    class StateMgr {
       
        public void checkState() {
            
            AbstractSettingPanel.State newState = isValid()
            ? AbstractSettingPanel.State.VALID_STATE
                    : AbstractSettingPanel.State.INVALID_STATE;
            if (newState != state) {
                setState(newState);
            }
        }
        
        public void controlJoinArea() {
            boolean join = joinAreaNetwork.isSelected();
            areaNetworkCombo.setEnabled(join);
            facilityIdField.setEnabled(join);
            creatorIdField.setEnabled(join);
            this.checkState();
        }
        
        private boolean isValid() {
            if (joinAreaNetwork.isSelected()) {
                boolean projOk = areaNetworkCombo.getSelectedIndex() != 0 ? true : false;
                boolean facilityOk = facilityIdField.getText().trim().equals("") ? false : true;
                boolean creatorOk = creatorIdField.getText().trim().equals("") ? false : true;
                return (projOk && facilityOk && creatorOk) ? true : false;
            }
            return true;
        }
    }
}
