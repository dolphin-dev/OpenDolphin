package open.dolphin.client;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import open.dolphin.helper.SpringUtilities;
import open.dolphin.infomodel.SimpleAddressModel;
import open.dolphin.project.Project;
import open.dolphin.util.AgeCalculater;

/**
 *
 * @author Kazushi Minagawa.
 */
public class BasicInfoInspector {
    
    private JPanel basePanel; // このクラスのパネル
    private  JLabel nameLabel;
    private JLabel addressLabel;
    private Color maleColor;
    private Color femaleColor;
    private Color unknownColor;
    
    // Context このインスペクタの親コンテキスト
    private final ChartImpl context;


    /**
     * BasicInfoInspectorオブジェクトを生成する。
     * @param context
     */
    public BasicInfoInspector(ChartImpl context) {
        this.context = context;
        initComponent();
        update();
    }

    /**
     * レイウアトのためにこのインスペクタのコンテナパネルを返す。
     * @return コンテナパネル
     */
    public JPanel getPanel() {
        return basePanel;
    }

    /**
     * 患者の基本情報を表示する。
     */
    private void update() {

        StringBuilder sb = new StringBuilder();
        sb.append(context.getPatient().getFullName());
        sb.append("  ");
        String age = AgeCalculater.getAgeAndBirthday(context.getPatient().getBirthday(), Project.getInt("ageToNeedMonth", 6));
        sb.append(age);
        nameLabel.setText(sb.toString());

        SimpleAddressModel address = context.getPatient().getSimpleAddressModel();
        if (address != null) {
            String adr = address.getAddress();
            if (adr != null) {
                adr = adr.replaceAll("　", " ");
            }
            addressLabel.setText(adr);
        } else {
            addressLabel.setText("　");
        }

        //String gender = context.getPatient().getGenderDesc();
        String gender = context.getPatient().getGender();
        gender = gender!=null ? gender.toLowerCase() : "u";

        Color color;
        java.util.ResourceBundle bundle = ClientContext.getBundle();
        if (gender.startsWith("m")) {
            color = maleColor;
        } else if (gender.startsWith("f")) {
            color = femaleColor;
        } else {
            color = unknownColor;
        }
        nameLabel.setBackground(color);
        addressLabel.setBackground(color);
        basePanel.setBackground(color);
    }

    /**
     * GUI コンポーネントを初期化する。
     */
    private void initComponent() {
        
        // 性別によって変えるパネルのバックグランドカラー
        maleColor = GUIConst.BASIC_INFO_MALE_COLOR;
        femaleColor = GUIConst.BASIC_INFO_FEMALE_COLOR;
        unknownColor = GUIConst.BASIC_INFO_UNKNOW_COLOR;
        Color foreground = GUIConst.BASIC_INFO_FOREGROUND;
        
        nameLabel = new JLabel("　");
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nameLabel.setForeground(foreground);
        nameLabel.setOpaque(true);
        nameLabel.setMinimumSize(new Dimension(271, 20));
        nameLabel.setMaximumSize(new Dimension(271, 20));
        nameLabel.setMaximumSize(new Dimension(271, 20));
        
        addressLabel = new JLabel("　");
        addressLabel.setHorizontalAlignment(SwingConstants.CENTER);
        addressLabel.setForeground(foreground);
        addressLabel.setOpaque(true);
        addressLabel.setMinimumSize(new Dimension(271, 20));
        addressLabel.setMaximumSize(new Dimension(271, 20));
        addressLabel.setMaximumSize(new Dimension(271, 20));

//        basePanel = new JPanel(new BorderLayout(0, 2));
//        basePanel.setMinimumSize(new Dimension(271, 40));
//        basePanel.setMaximumSize(new Dimension(271, 40));
//        basePanel.setPreferredSize(new Dimension(271, 40));
//        basePanel.add(nameLabel, BorderLayout.NORTH);
//        basePanel.add(addressLabel, BorderLayout.SOUTH);
        
        basePanel = new JPanel(new SpringLayout());
        basePanel.add(nameLabel);
        basePanel.add(addressLabel);
        SpringUtilities.makeCompactGrid(basePanel, 2, 1, 0, 0, 0, 0);
    }
}
