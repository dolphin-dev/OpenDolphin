/*
 * Created on 2004/03/06
 *
 * Copyright (C) 2003 Digital Globe, Inc. All rights reserved. 
 * 
 */
package open.dolphin.util;

import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * @author Kazushi Minagawa, Digital Globe, Inc.
 *
 */
public class NumericField extends JTextField {
	
	public NumericField() {
		
		Insets insets = new Insets(1,5,1,5);  // top,left,bottom,right
		this.setMargin(insets);
		
		this.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent event) {
			   //pidField.getInputContext().setCharacterSubsets(new Character.Subset[] {InputSubset.KANJI});
				getInputContext().setCharacterSubsets(null);
			}
			public void focusLosted(FocusEvent event) {
			   getInputContext().setCharacterSubsets(null);
			}
		});
	}
	
	public NumericField(int limit) {
		this();
		setLimitLength(limit);
	}
	
	public void setLimitLength(int limit) {
		this.setDocument(new MyDocument(limit));
	}
	
	class MyDocument extends PlainDocument {
    	
		private int limit;
		
		public MyDocument(int limit) {
			this.limit = limit;
		}
    	
		public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
			
			for (int i =0; i < str.length(); i++) {
				char ch = str.charAt(i);
				if (Character.isDigit(ch) == false) {
					Toolkit.getDefaultToolkit().beep();
					return;
				}
			}
			
			int lastPos = str.length();
			if (offset >= limit) {
				Toolkit.getDefaultToolkit().beep();
				return;
			} else if (lastPos <= limit) {
				super.insertString(offset, str, a);
			} else {
				String str2 = str.substring(0, limit - offset);
				super.insertString(offset, str2, a);
			}
		}    
	}
}
