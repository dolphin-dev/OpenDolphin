package open.dolphin.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import open.dolphin.infomodel.IInfoModel;
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
    private ChartImpl context;


    /**
     * BasicInfoInspectorオブジェクトを生成する。
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

        String gender = context.getPatient().getGenderDesc();

        Color color = null;
        if (gender.equals(IInfoModel.MALE_DISP)) {
            color = maleColor;
        } else if (gender.equals(IInfoModel.FEMALE_DISP)) {
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
        Color foreground = ClientContext.getColor("patientInspector.basicInspector.foreground"); // new
        maleColor = ClientContext.getColor("color.male"); // Color.CYAN;
        femaleColor = ClientContext.getColor("color.female"); // Color.PINK;
        unknownColor = ClientContext.getColor("color.unknown"); // Color.LIGHT_GRAY;
        //int[] size = ClientContext.getIntArray("patientInspector.basicInspector.size");
        
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


        basePanel = new JPanel(new BorderLayout(0, 2));
        //Dimension dim = new Dimension(size[0], size[1]);
        basePanel.setMinimumSize(new Dimension(271, 40));
        basePanel.setMaximumSize(new Dimension(271, 40));
        basePanel.setPreferredSize(new Dimension(271, 40));
        basePanel.add(nameLabel, BorderLayout.NORTH);
        basePanel.add(addressLabel, BorderLayout.SOUTH);
    }
}
