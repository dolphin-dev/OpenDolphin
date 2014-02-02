/*
 * MasterSearchStatusPanel.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *	
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *	
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package open.dolphin.order;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;

import open.dolphin.client.*;
import open.dolphin.util.*;

/**
 *
 * @author  kazushi Minagawa, Digital Globe, inc.
 */
public final class MasterSearchStatusPanel extends JPanel implements PropertyChangeListener {
          
    private Dimension countDim = new Dimension(40, 20);
    //private Dimension progressDim = new Dimension(170, 10);
    private JTextField countField = new JTextField();
    private JProgressBar progressBar;  // = new JProgressBar(0, 100);
    //private AnimationLabel animation;
    private JRadioButton frequent = new JRadioButton("頻用のみ");
    private JRadioButton all = new JRadioButton("全て");
    private PropertyChangeSupport boundSupport = new PropertyChangeSupport(this);
    private String master;
    private boolean freq;
    
    public MasterSearchStatusPanel(String m) {
        
        super();
        
        this.master = m;

        // 頻用と全てのラジオボタングループを生成する
        ButtonGroup bg = new ButtonGroup();
        bg.add(frequent);
        bg.add(all);
        
        // プレファレンスから検索方法を読み込む
        freq = ClientContext.getPreferences().getBoolean(master + "Master.freqProp", false);
        frequent.setSelected(freq);
        all.setSelected(! freq);
        frequent.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean b = frequent.isSelected();
                notifyFreqProp(b);
                ClientContext.getPreferences().putBoolean(master + "Master.freqProp", b);
            }
        });
        all.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean b = all.isSelected();
                notifyFreqProp(! b);
                ClientContext.getPreferences().putBoolean(master + "Master.freqProp", !b);
            }
        });        

        // 件数フィールドのディメンションを設定する
        countField.setEditable(false);
        countField.setPreferredSize(countDim);
        countField.setMaximumSize(countDim);

        // レイアウト
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.add(Box.createRigidArea(new Dimension(11, 0)));
        this.add(new JLabel("抽出基準"));
        this.add(Box.createRigidArea(new Dimension(7,0)));
        this.add(frequent);
        this.add(all);

        this.add(Box.createHorizontalGlue());

        // Count field
        this.add(new JLabel("該当件数"));
        this.add(Box.createRigidArea(new Dimension(5,0)));
        this.add(countField);
        this.add(Box.createRigidArea(new Dimension(11,0)));

        // ProgressBar
        //progressBar.setMaximumSize(progressDim);
        progressBar = DesignFactory.createProgressBar();
        this.add(progressBar);
        /*animation = new AnimationLabel();
        ImageIcon[] frames = new ImageIcon[20];
        for (int i = 1; i <=20; i++) {
            frames[i-1] = new ImageIcon(this.getClass().getResource("/open/dolphin/resources/images/us" + i + ".gif"));
        }
        animation.setup(frames);
        panel.add(animation);*/
        this.add(Box.createRigidArea(new Dimension(11, 0)));      
    }
    
    public boolean getFreq() {
        return freq;
    }
    
    public void addPropertyChangeListener(String prop, PropertyChangeListener l) {
        boundSupport.addPropertyChangeListener(prop, l);
    }
    
    public void removePropertyChangeListener(String prop, PropertyChangeListener l) {
        boundSupport.removePropertyChangeListener(prop, l);
    }   

    public void propertyChange(PropertyChangeEvent e) {

        String prop = e.getPropertyName();

        if (prop.equals("busyProp")) {
            boolean b = ((Boolean)e.getNewValue()).booleanValue();
            if (b) {
                progressBar.setIndeterminate(true);
                countField.setText("");
            } else {
                progressBar.setIndeterminate(false);
                progressBar.setValue(0);
            }
        } else if (prop.equals("countProp")) {
            int count = ((Integer)e.getNewValue()).intValue();
            countField.setText(String.valueOf(count));
        }   
    }
    
    private void notifyFreqProp(boolean b) {
        freq = b;
        boundSupport.firePropertyChange("freqProp", !freq, freq);
    }
}
