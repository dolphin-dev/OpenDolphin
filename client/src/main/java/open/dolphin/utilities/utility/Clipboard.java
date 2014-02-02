/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.utilities.utility;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.*;
import java.io.IOException;

/**
 * クリップボードクラス
 * @author S.Oh@Life Sciences Computing Corporation.
 */
public class Clipboard {
    /**
     * コンストラクタ
     */
    public Clipboard() {}
    
    /**
     * 文字列をクリップボードへセット
     * @param str 文字列
     */
    public void setClipboardString(String str) {
        Toolkit tool = Toolkit.getDefaultToolkit();
        java.awt.datatransfer.Clipboard clip = tool.getSystemClipboard();
        StringSelection selection = new StringSelection(str);
        clip.setContents(selection, selection);
    }
    
    /**
     * クリップボードから文字列を取得
     * @return 文字列
     */
    public String getClipboardString() {
        Toolkit tool = Toolkit.getDefaultToolkit();
        java.awt.datatransfer.Clipboard clip = tool.getSystemClipboard();
        try{
            return (String)clip.getData(DataFlavor.stringFlavor);
        }catch(UnsupportedFlavorException e) {
            return null;
        }catch(IOException e) {
            return null;
        }
    }
    
    /**
     * クリップボードへ画像をセット
     * @param img 画像
     */
    public void setClipboardImage(Image img) {
        Toolkit tool = Toolkit.getDefaultToolkit();
        java.awt.datatransfer.Clipboard clip = tool.getSystemClipboard();
        ImageSelection selection = new ImageSelection(img);
        clip.setContents(selection, selection);
    }
    
    /**
     * クリップボードから画像を取得
     * @return 画像
     */
    public Image getClipboardImage() {
        Toolkit tool = Toolkit.getDefaultToolkit();
        java.awt.datatransfer.Clipboard clip = tool.getSystemClipboard();
        try{
            return (Image)clip.getData(DataFlavor.imageFlavor);
        }catch(UnsupportedFlavorException e) {
            return null;
        }catch(IOException e) {
            return null;
        }
    }
}

/**
 * ImageSelectionクラス
 * @author S.Oh@Life Sciences Computing Corporation.
 */
class ImageSelection implements Transferable, ClipboardOwner {
    protected Image img;

    /**
     * コンストラクタ
     * @param image 画像
     */
    public ImageSelection(Image image) {
        img = image;
    }

    /**
     * 対応フレーバーの取得
     * @return フレーバー
     */
    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] { DataFlavor.imageFlavor };
    }

    /**
     * フレーバーのチェック
     * @param flavor フレーバー
     * @return サポート結果
     */
    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return DataFlavor.imageFlavor.equals(flavor);
    }

    /**
     * 画像の取得
     * @param flavor フレーバー
     * @return 画像
     * @throws UnsupportedFlavorException
     * @throws IOException 
     */
    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (DataFlavor.imageFlavor.equals(flavor)) {
            return img;
        }
        throw new UnsupportedFlavorException(flavor);
    }

    /**
     * クリップボードデータの破棄
     * @param clipboard
     * @param contents 
     */
    @Override
    public void lostOwnership(java.awt.datatransfer.Clipboard clipboard, Transferable contents) {
        img = null;
    }
}
