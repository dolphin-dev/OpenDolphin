/*
 * MyTableCellData.java
 *
 * Created on 2001/12/10, 6:54
 */

package jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans;

import java.awt.*;
import java.util.*;

/**
 *
 * @author  Junzo SATO
 * @version 
 */

// This class is used by LaboTestBean and LaboTestSearchTask.
public class MyTableCellData {
    Object obj = null;
    //--------------------------------------
    String cellText = "";
    String low = "下限値：", up = "上限値：", normal = "基準値：", out = "異常値フラグ：";
    Vector memo = null;
    boolean item = false;
    String itemCode;
    String itemCodeID;
    
    public Color getStatusColor() {
        if (out.equals("異常値フラグ：" + "L")) {
            return Color.blue;
        } else if (out.equals("異常値フラグ：" + "N")) {
            return new Color(0,200,0);//Color.green;
        } else if (out.equals("異常値フラグ：" + "H")) {
            return Color.red;
        } else {
            return Color.black;
        }
    }
    //--------------------------------------
    public MyTableCellData(Object obj) {
        this.obj = obj;
        memo = null;
    }

    public MyTableCellData(
        String cellText,
        String low, String up, String normal, String out, 
        Vector memo) {

        obj = null;

        // set the item flag to true
        item = true;
        // set informations about laboItem...
        // cell text which will be displayed
        this.cellText = cellText;
        // strings for popup menu
        this.low = this.low + low;
        this.up = this.up + up;
        this.normal = this.normal + normal;
        this.out = this.out + out;

        if (memo == null) {
            System.out.println("Vector memo is null.");
            return;
        }
        if (memo.size() <= 0) {
            System.out.println("memo is empty");
            return;
        }
        this.memo = memo;
    }

    public Object getObj() {
        return obj;
    }

    public String getCellText() {
        return cellText;
    }

    public void setCellText(String txt) {
        cellText = txt;
    }

    public Vector getMemo() {
        return memo;
    }

    public String getLow() {
        return low;
    }

    public String getUp() {
        return up;
    }

    public String getNormal() {
        return normal;
    }

    public String getOut() {
        return out;
    }

    public boolean isItem() {
        return item;
    }
    
    public String getItemCode() {
        return itemCode;
    }
    
    public void setItemCode(String val) {
        itemCode = val;
    }
    
    public String getItemCodeID() {
        return itemCodeID;
    }
    
    public void setItemCodeID(String val) {
        itemCodeID = val;
    }    

    public String toString() {
        if (obj != null && obj.getClass().getName().equals("java.lang.String")) {
            return (String)obj;
        }/* else if (memo != null && memo.size() > 0) {
            String s = "<html><body>";
            for (int i = 0; i < memo.size(); ++i) {
                s = s + (String)memo.elementAt(i) + "<br>";
            }
            s = s + "</body></html>";
            return s;
        }*/ else {
            return cellText;
        }
    }
}