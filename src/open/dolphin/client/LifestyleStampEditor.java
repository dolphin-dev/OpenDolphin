/*
 * LifestyleStampEditor.java 
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
 * Copyright (C) 2003,2004 Digital Globe, Inc. All rights reserved.
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
package open.dolphin.client;

import javax.swing.*;
import javax.swing.event.*;

import open.dolphin.infomodel.LifestyleModel;
import open.dolphin.infomodel.ModuleModel;

import java.awt.*;
import java.awt.event.*;
import java.awt.im.InputSubset;

/**
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class LifestyleStampEditor extends StampModelEditor {
   
	private static final long serialVersionUID = 4874515806145819483L;
	
	private LifestylePanel lifestylePanel;
   
	/**
	 * Constructor
	 */
	public LifestyleStampEditor() {
		this.title = "生活習慣情報";
		createComponent();
	}
   
	/**
	 * Creates Lifestyle editting panel.
	 * @return (Component) lifestyle UI panel
	 */
	protected void createComponent() {
		lifestylePanel = new LifestylePanel();
		setLayout(new BorderLayout());
		add(lifestylePanel, BorderLayout.CENTER);
	}
      
	public void setValue(Object stamp) {
		lifestylePanel.setValue(stamp);
	}
   
	public Object getValue() {
		return (Object)lifestylePanel.getValue();
	}
   
	/**
	 * Lifestyle Panel
	 */
	final class LifestylePanel extends JPanel {

		private static final long serialVersionUID = -5494331746255054637L;
		
		//private JTextField tobaccoField = new JTextField(10);
		private String[] tobaccoCategories = ClientContext.getStringArray("lifestyle.tobacco.categories");
		private String[] alcoholCategories = ClientContext.getStringArray("lifestyle.alcohol.categories");
        
		private JRadioButton[] tobaccoRadios;
		private JRadioButton[] alcoholRadios;
		//private JRadioButton noneTobacco = new JRadioButton("吸わない");
		//private JRadioButton littleTobacco = new JRadioButton("少し");
		//private JRadioButton mediumTobacco = new JRadioButton("20 本程度/日");
		//private JRadioButton heavyTobacco = new JRadioButton("ヘビー");
        
		//private JTextField alcoholField = new JTextField(10);
		//private JRadioButton noneAlcohol = new JRadioButton("飲まない");
		//private JRadioButton littleAlcohol = new JRadioButton("少し");
		//private JRadioButton mediumAlcohol = new JRadioButton("普通");
		//private JRadioButton heavyAlcohol = new JRadioButton("多い");
        
		private JTextField occupationField = new JTextField(10);
		private JTextField otherField = new JTextField(10);
		private ModuleModel savedStamp;

		/**
		 * Constructor
		 */
		public LifestylePanel() {

		   DocumentListener dl = new DocumentListener () {
              
				public void changedUpdate(DocumentEvent evt) {
				}
                
				public void insertUpdate(DocumentEvent evt) {
					checkButtons();
				}
                
				public void removeUpdate(DocumentEvent evt) {
					checkButtons();
				}
			};
            
			FocusListener fl = new FocusAdapter() {
				public void focusGained(FocusEvent event) {
					JTextField tf = (JTextField)event.getSource();
					tf.getInputContext().setCharacterSubsets(new Character.Subset[] {InputSubset.KANJI});
				}
			};
            
			ActionListener al = new ActionListener() {
                
				public void actionPerformed(ActionEvent e) {
					checkButtons();
				}
			};
            
			int len = alcoholCategories.length;
			alcoholRadios = new JRadioButton[len];
			ButtonGroup bg = new ButtonGroup();
			for (int i = 0; i < len; i++) {
				alcoholRadios[i] = new JRadioButton(alcoholCategories[i]);
				bg.add(alcoholRadios[i]);
				alcoholRadios[i].addActionListener(al);
			}
            
			len = tobaccoCategories.length;
			tobaccoRadios = new JRadioButton[len];
			bg = new ButtonGroup();
			for (int i = 0; i < len; i++) {
				tobaccoRadios[i] = new JRadioButton(tobaccoCategories[i]);
				bg.add(tobaccoRadios[i]);
				tobaccoRadios[i].addActionListener(al);
			}

			// Sets BoxLayout manager
			setLayout(new BoxLayout (this, BoxLayout.Y_AXIS));

			// タバコフィールド
			JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
			JLabel label = new JLabel("たばこ： ");
			p.add(label);
			for (int i = 0; i < tobaccoCategories.length; i++) {
				p.add(tobaccoRadios[i]);
			}
            
			//p.add(tobaccoField);
			add(p);

			p = new JPanel(new FlowLayout(FlowLayout.LEFT));
			label = new JLabel("飲　酒： ");
			p.add(label);
			for (int i = 0; i < alcoholCategories.length; i++) {
				p.add(alcoholRadios[i]);
			}
			//p.add(alcoholField);
			add(p);

			p = new JPanel(new FlowLayout(FlowLayout.LEFT));
			label = new JLabel("職　業： ");
			p.add(label);
			p.add(occupationField);
			add(p);

			p = new JPanel(new FlowLayout(FlowLayout.LEFT));
			label = new JLabel("その他： ");
			p.add(label);
			p.add(otherField);
			add(p);
                        
			// Adds DocumentListener to TextFields
			//tobaccoField.getDocument().addDocumentListener(dl);
			//alcoholField.getDocument().addDocumentListener(dl);            
			occupationField.getDocument().addDocumentListener(dl);
            
			//tobaccoField.addFocusListener(fl);
			//alcoholField.addFocusListener(fl);
			occupationField.addFocusListener(fl);
		}

		protected void checkButtons() {
         
			//boolean tobaccoEmpty = tobaccoField.getText().equals("");
			//boolean alcoholEmpty = alcoholField.getText().equals("");
			boolean tobaccoOk = false;
			for (int i = 0; i < tobaccoCategories.length; i++) {
				if (tobaccoRadios[i].isSelected()) {
					tobaccoOk = true;
					break;
				}
			}
            
			boolean alcoholOk = false;
			for (int i = 0; i < alcoholCategories.length; i++) {
				if (alcoholRadios[i].isSelected()) {
					alcoholOk = true;
					break;
				}
			}                   
            
			boolean occupationOk = ( ! occupationField.getText().trim().equals("")) ? true : false;
            
			boolean valid = (tobaccoOk && alcoholOk && occupationOk)
							? true
							: false;
                            
			setValidModel(valid);
		}
        
		/**
		 * 値を表示する
		 */
		protected void setValue(Object stamp) {
            
			savedStamp = (ModuleModel)stamp;
            
			LifestyleModel model = (LifestyleModel)savedStamp.getModel();
            
			if (model == null) {
				return;
			}

			String data = model.getTobacco();
			if (data != null) {
				//tobaccoField.setText(data);
				int index = findTobacco(data);
				if (index > -1) {
					tobaccoRadios[index].setSelected(true);
				}
			}

			data = model.getAlcohol();
			if (data != null) {
				//alcoholField.setText(data);
				int index = findAlcohol(data);
				if (index > -1) {
					alcoholRadios[index].setSelected(true);
				}
			}

			data = model.getOccupation();
			if (data != null) {
				occupationField.setText(data);
			}

			data = model.getOther ();
			if (data != null) {
				otherField.setText(data);
			}
		}

		/**
		 * 編集値からモデルを得る
		 */
		protected Object getValue() {

			LifestyleModel model = new LifestyleModel();

			//String data = tobaccoField.getText().trim();
			String data = getTobacco();
			if (data != null) {
				model.setTobacco(data);
			}
            
			//data = alcoholField.getText().trim();
			data = getAlcohol();
			if (data != null) {
				model.setAlcohol(data);
			}

			data = occupationField.getText().trim();
			model.setOccupation(data);
            
			// option field
			data = otherField.getText().trim();
			if (! data.equals("")) {
				model.setOther(data);
			}
            
			savedStamp.setModel(model);
			return (Object)savedStamp;
		}
        
	private int findTobacco(String data) {
		int ret = -1;
		for (int i = 0; i < tobaccoCategories.length; i++) {
			if (data.equals(tobaccoCategories[i])) {
				ret = i;
				break;
			}
		}
		return ret;
	}
    
	private int findAlcohol(String data) {
		int ret = -1;
		for (int i = 0; i < alcoholCategories.length; i++) {
			if (data.equals(alcoholCategories[i])) {
				ret = i;
				break;
			}
		}
		return ret;
	}
    
	private String getTobacco() {
		String ret = null;
		for (int i = 0; i < tobaccoCategories.length; i++) {
			if (tobaccoRadios[i].isSelected()) {
				ret = tobaccoCategories[i];
				break;
			}
		}
		return ret;
	} 
    
	private String getAlcohol() {
		String ret = null;
		for (int i = 0; i < alcoholCategories.length; i++) {
			if (alcoholRadios[i].isSelected()) {
				ret = alcoholCategories[i];
				break;
			}
		}
		return ret;
	}             
	}
}