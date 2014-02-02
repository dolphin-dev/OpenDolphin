/*
 * BasicInfoInspector.java
 *
 * Created on 2007/01/18, 16:48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package open.dolphin.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.SimpleAddressModel;

/**
 *
 * @author kazm
 */
public class BasicInfoInspector {
    
    private JPanel basePanel; // このクラスのパネル
    private  JLabel nameLabel;
    private JLabel addressLabel;
    private Color maleColor;
    private Color femaleColor;
    private Color unknownColor;
    
    // Context このインスペクタの親コンテキスト
    private ChartPlugin context;


    /**
     * BasicInfoInspectorオブジェクトを生成する。
     */
    public BasicInfoInspector(ChartPlugin context) {
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
        sb.append(context.getPatient().getAgeBirthday());
        nameLabel.setText(sb.toString());

        SimpleAddressModel address = context.getPatient().getAddress();
        if (address != null) {
            addressLabel.setText(address.getAddress());
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
        int[] size = ClientContext.getIntArray("patientInspector.basicInspector.size");
        
        nameLabel = new JLabel("　");
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nameLabel.setForeground(foreground);
        nameLabel.setOpaque(true);
        
        addressLabel = new JLabel("　");
        addressLabel.setHorizontalAlignment(SwingConstants.CENTER);
        addressLabel.setForeground(foreground);
        addressLabel.setOpaque(true);

        basePanel = new JPanel(new BorderLayout(0, 2));
        basePanel.add(nameLabel, BorderLayout.CENTER);
        basePanel.add(addressLabel, BorderLayout.SOUTH);
        basePanel.setBorder(BorderFactory.createEtchedBorder());
        Dimension dim = new Dimension(size[0], size[1]);
        basePanel.setMinimumSize(dim);
        basePanel.setMaximumSize(dim);
        basePanel.setPreferredSize(dim);
    }
    
}
