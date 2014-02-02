/*
 * Created on 2004/03/06
 *
 * Copyright (C) 2003 Digital Globe, Inc. All rights reserved. 
 * 
 */
package open.dolphin.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Toolkit;

/**
 * @author Kazushi Minagawa, Digital Globe, Inc.
 *
 */
public class ValueField extends NumericField {
	
	private static Font f = new Font("Dialig", Font.PLAIN, 12);
	private static FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(f);
	private static final int height = 21;
	private static final float widthMargin = 0.5f;
	private int minValue;
	private int maxValue;
	private Color validColor = Color.black;
	private Color invalidColor = Color.red;
	
	public ValueField(int min, int max) {
		
		super(String.valueOf(max).length());
		this.minValue = min;
		this.maxValue = max;
		
		int width = fm.stringWidth(String.valueOf(maxValue));
		width = width + (int)((float)width * widthMargin) + 10;
		Dimension dim = new Dimension(width, height);
		this.setPreferredSize(dim);
		this.setMaximumSize(dim);	
		//Insets insets = new Insets(1,5,1,5);
		//this.setMargin(insets);
		
		StringBuffer buf = new StringBuffer();
		buf.append(minValue);
		buf.append(" ˆÈã ");
		buf.append(maxValue);
		buf.append(" ˆÈ‰º");
		this.setToolTipText(buf.toString());
	}
	
	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}

	public int getMaxValue() {
		return maxValue;
	}
	
	public void setMinValue(int minValue) {
		this.minValue = minValue;
	}

	public int getMinValue() {
		return minValue;
	}	
	
	public boolean isValidValue() {
		boolean ret = false;
		String text = this.getText().trim();
		if (! text.equals("")) {
			try {
				int val = Integer.parseInt(text);
				ret =  (val >= getMinValue() && val <= getMaxValue()) ? true : false;
			} catch (Exception e) {	
			}
		}
		setTextColor(ret);
		return ret;
	}
	
	private void setTextColor(boolean b) {
		Color c = b ? getValidColor() : getInvalidColor();
		this.setForeground(c);
	}

	public void setValidColor(Color validColor) {
		this.validColor = validColor;
	}

	public Color getValidColor() {
		return validColor;
	}

	public void setInvalidColor(Color invalidColor) {
		this.invalidColor = invalidColor;
	}

	public Color getInvalidColor() {
		return invalidColor;
	}
}
