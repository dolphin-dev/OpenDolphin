/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.utilities.utility;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import open.dolphin.utilities.common.Dicom;

/**
 * DICOMライブラリクラス
 * @author S.Oh@Life Sciences Computing Corporation.
 */
public final class DicomLib extends Dicom {
    /**
     * コンストラクタ
     */
    public DicomLib() {
        super();
    }
    
    /**
     * DICOMのファイルオープン
     * @param filePath ファイルパス
     * @return 画像
     * @throws IOException 
     */
    public BufferedImage dcm2Bmp(String filePath) throws IOException {
        BufferedImage image = null;
        if(dcm2bmpOpen(filePath) >= 0) {
            if(dcm2bmpWrite(filePath, 1) >= 0) {
                image = getBufImg(this.FILEFMT_BMP);
            }
        }
        return image;
    }
    
    /**
     * 階調処理
     * @param wc センター
     * @param ww 幅
     * @return 画像
     * @throws IOException 
     */
    public BufferedImage changeGradation(float wc, float ww) throws IOException {
        BufferedImage image = null;
        if(changeWCWW(wc, ww) >= 0) {
            if(dcm2bmpWrite(null, 1) >= 0) {
                image = getBufImg(this.FILEFMT_BMP);
            }
        }
        return image;
    }
    
    /**
     * 階調クリア
     * @return 画像
     * @throws IOException 
     */
    public BufferedImage clearGradation() throws IOException {
        BufferedImage image = null;
        if(clearWCWW() >= 0) {
            if(dcm2bmpWrite(null, 1) >= 0) {
                image = getBufImg(this.FILEFMT_BMP);
            }
        }
        return image;
    }

    /**
     * WCの取得
     * @return センター
     */
    public float getWC() {
        return wcVal + wcOffset;
    }
    
    /**
     * WWの取得
     * @return 幅
     */
    public float getWW() {
        return wwVal + wwOffset;
    }
    
    /**
     * 画像データの取得
     * @param fileFormat ファイルフォーマット
     * @return 画像データ
     * @throws IOException 
     */
    public byte[] getData(String fileFormat) throws IOException {
        if(fileFormat.equals(this.FILEFMT_BMP)) {
            return bmpData.toByteArray();
        } else {
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            Iterator<ImageWriter> ite = ImageIO.getImageWritersByFormatName(fileFormat);
            if(!ite.hasNext()) {
                throw new IllegalStateException();
            }
            ImageWriter writer = ite.next();
            ImageOutputStream stream = ImageIO.createImageOutputStream(bOut);
            //ImageInputStream stream = ImageIO.createImageInputStream(bIn);
            //ImageWriteParam
            writer.setOutput(stream);
            writer.write(getBufImg(this.FILEFMT_BMP));
            stream.flush();
            writer.dispose();
            if(stream != null) {
                stream.close();
            }
            return bOut.toByteArray();
        }
    }
    
    /**
     * BufferedImageの取得
     * @param fileFormat ファイルフォーマット
     * @return 画像
     * @throws IOException 
     */
    public BufferedImage getBufImg(String fileFormat) throws IOException {
        return ImageIO.read(new ByteArrayInputStream(getData(fileFormat)));
    }
    
    /**
     * ImageからBufferedImageへの変換
     * @param img 画像
     * @return 画像
     * @throws InterruptedException
     * @throws IOException 
     */
    public BufferedImage getImageToBufImg(Image img) throws InterruptedException, IOException {
        BufferedImage buf = null;
        if(img instanceof BufferedImage) {
            buf = getBufImg(this.FILEFMT_BMP);
        } else {
            // 方法１
            //// ロード待機
            //MediaTracker tck = new MediaTracker(new Component() {});
            //tck.addImage(img, 0);
            //tck.waitForAll();
            //
            //// ImageをBufferedImageへコピー
            //PixelGrabber pixGrb = new PixelGrabber(img, 0, 0, -1, -1, false);
            //pixGrb.grabPixels();
            //ColorModel color = pixGrb.getColorModel();
            //int width = pixGrb.getWidth();
            //int height = pixGrb.getHeight();
            //WritableRaster raster = color.createCompatibleWritableRaster(width, height);
            //// buf = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            //buf = new BufferedImage(color, raster, color.isAlphaPremultiplied(), new Hashtable());
            //buf.getRaster().setDataElements(0, 0, width, height, pixGrb.getPixels());
            
            // 方法２
            //BufferedImage bimg = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
            //Graphics g = bimg.getGraphics();
            //g.drawImage(img, 0, 0, null);
            //g.dispose();
            //return bimg;
        }
        return buf;
    }
    
    /**
     * ファイルの保存
     * @param filePath ファイルパス
     * @param fileFormat ファイルフォーマット
     * @throws IOException 
     */
    public void saveFile(String filePath, String fileFormat) throws IOException {
        File file = new File(filePath);
        ImageIO.write(getBufImg(this.FILEFMT_BMP), fileFormat, file);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        /*
         * 
        int nret=0;
        int cnt =0;
        String dfname = "E:\\Develop\\DicomToBmp\\sample\\Canon_CR_.dcm";
        String bfname = "E:\\Develop\\DicomToBmp\\sample\\Canon_CR_.bmp";

        DicomToBmp dbm = new DicomToBmp();
        nret = dbm.dcm2bmpOpen(dfname);
        nret = dbm.dcm2bmpWrite(bfname);
         */
        /*
         * 
        DicomToBmp dtb = new DicomToBmp();
        if(dtb.dcm2bmpOpen(entry.getPath()) >= 0) {
            if(dtb.dcm2bmpWrite(entry.getPath(), 1) >= 0) {
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(dtb.getBmpData()));
            }
        }
         */
    }
}
