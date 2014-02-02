/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.utilities.control;

import java.awt.Image;
import javax.swing.ImageIcon;

/**
 * テーブルに表示するImageIconクラス
 * @author S.Oh@Life Sciences Computing Corporation.
 */
public class ImageIconEx {
    public static final int MAX_ICONSIZE = 100;
    
    private ImageIcon imgIcon;
    private String imgText;
    private int iconMaxSize;
    
    /**
     * コンストラクタ
     */
    public ImageIconEx() {
        imgIcon = null;
        imgText = null;
        iconMaxSize = MAX_ICONSIZE;
    }
    
    /**
     * アイコンの最大サイズのセット
     * @param size サイズ
     */
    public void setIconMaxSize(int size) {
        iconMaxSize = size;
    }
    
    /**
     * アイコンの最大サイズの取得
     * @return サイズ
     */
    public int getIconMaxSize() {
        return iconMaxSize;
    }
    
    /**
     * アイコンのセット
     * @param path アイコンのパス
     */
    public void setIcon(String path) {
        ImageIcon icon = new ImageIcon(path);
        setIcon(icon);
    }
    
    /**
     * アイコンのセット
     * @param icon アイコン
     */
    public void setIcon(ImageIcon icon) {
        int width = icon.getIconWidth();
        int height = icon.getIconHeight();
        float scale = 0f;
        if(width >= height) {
            scale = (float)getIconMaxSize() / (float)width;
            width = getIconMaxSize();
            height = (int)((float)height * scale);
        }else{
            scale = (float)getIconMaxSize() / (float)height;
            width = (int)((float)width * scale);
            height = getIconMaxSize();
        }
        imgIcon = new ImageIcon(icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
    }
    
    /**
     * アイコンの取得
     * @return アイコン
     */
    public ImageIcon getIcon() {
        return imgIcon;
    }
    
    /**
     * テキストのセット
     * @param text テキスト
     */
    public void setText(String text) {
        imgText = text;
    }
    
    /**
     * テキストの取得
     * @return テキスト
     */
    public String getText() {
        return imgText;
    }
    
    public static void main( String[] args ) {
        //ImageIconEx img = new ImageIconEx();
        //img.setIcon(ImageIcon);
        //img.setText(テキスト);
    }
}
