package open.dolphin.impl.login;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.util.ResourceBundle;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;
import open.dolphin.client.ClientContext;
import open.dolphin.helper.SpringUtilities;

/**
 *
 * @author Kazushi Minagawa
 */
public class SignInPanel extends JPanel {
    
    private JTextField userIdField;
    private JPasswordField passwordField;
    private JProgressBar progressBar;
    private JButton settingBtn;
    private JButton cancelBtn;
    private JButton loginBtn;
    
    public SignInPanel() {
        
        //this.setLayout(new SpringLayout());
        BoxLayout box = new BoxLayout(this, BoxLayout.X_AXIS);
        this.setLayout(box);
        
        ResourceBundle bundle = ClientContext.getMyBundle(LoginPanel.class);

        JLabel logo = new JLabel(ClientContext.getImageIcon("splash.jpg"));
        progressBar = new JProgressBar();
        
        userIdField = new JTextField(3);
        userIdField.setMaximumSize(userIdField.getPreferredSize());
        passwordField = new JPasswordField(3);
        passwordField.setMaximumSize(passwordField.getPreferredSize());
        
        settingBtn = new JButton(bundle.getString("settingBtn.text"));
        settingBtn.setToolTipText(bundle.getString("settingBtn.toolTipText"));
        if (ClientContext.isMac()) {
            settingBtn.setMargin(new Insets(0, -7, 0, -7));
        }
        
        cancelBtn = new JButton(bundle.getString("cancelBtn.text"));
        settingBtn.setToolTipText(bundle.getString("cancelBtn.toolTipText"));
        if (ClientContext.isMac()) {
            cancelBtn.setMargin(new Insets(0, -7, 0, -7));
        }
        
        loginBtn = new JButton(bundle.getString("loginBtn.text"));
        if (ClientContext.isMac()) {
            loginBtn.setMargin(new Insets(0, -7, 0, -7));
        }
        
        //JPanel logoPanel = new JPanel(new BorderLayout());
        //logoPanel.add(logo);
        //logoPanel.add(progressBar, BorderLayout.SOUTH);
        this.add(logo);
        
        JPanel panel = new JPanel(new SpringLayout());
        //panel.add(new JLabel(""));
        panel.add(new JLabel(bundle.getString("userIdLabel.text")));
        panel.add(userIdField);
        panel.add(new JLabel(bundle.getString("passwordLabel.text")));
        panel.add(passwordField);
        panel.add(new JLabel(""));
        panel.add(progressBar);
        SpringUtilities.makeCompactGrid(panel, 6, 1, 6, 6, 6, 6);
        panel.setBorder(new EmptyBorder(24,24,24,24));
        
        JPanel cmd = new JPanel(new FlowLayout(FlowLayout.RIGHT,6,6));
        cmd.add(settingBtn);
        cmd.add(cancelBtn);
        cmd.add(loginBtn);
        
        JPanel right = new JPanel(new BorderLayout());
        right.add(panel);
        right.add(cmd, BorderLayout.SOUTH);
        this.add(right);
        //right.setPreferredSize(logo.getPreferredSize());
        
        //this.add(logoPanel);
        //this.add(panel);
        //this.add(cmd);
        
        SpringUtilities.makeCompactGrid(this, 3, 1, 6, 6, 6, 6);
    }

    public JTextField getUserIdField() {
        return userIdField;
    }

    public void setUserIdField(JTextField userIdField) {
        this.userIdField = userIdField;
    }

    public JPasswordField getPasswordField() {
        return passwordField;
    }

    public void setPasswordField(JPasswordField passwordField) {
        this.passwordField = passwordField;
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(JProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public JButton getSettingBtn() {
        return settingBtn;
    }

    public void setSettingBtn(JButton settingBtn) {
        this.settingBtn = settingBtn;
    }

    public JButton getCancelBtn() {
        return cancelBtn;
    }

    public void setCancelBtn(JButton cancelBtn) {
        this.cancelBtn = cancelBtn;
    }

    public JButton getLoginBtn() {
        return loginBtn;
    }

    public void setLoginBtn(JButton loginBtn) {
        this.loginBtn = loginBtn;
    }
}
