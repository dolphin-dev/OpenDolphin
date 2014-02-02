/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.utilities.common;

/**
 * 共通定義インターフェース
 * @author S.Oh@Life Sciences Computing Corporation.
 */
public interface CommonDefImpl {
    // Dicom
    public static final String FILEFMT_BMP = "bmp";
    public static final String FILEFMT_JPG = "jpg";
    public static final String FILEFMT_PNG = "png";
    public static final String FILEFMT_GIF = "gif";
    public static final String FILEFMT_DCM = "dcm";
    public static final int FILEHEADERSIZE  = 14;
    public static final int INFOHEADERSIZE  = 40;
    public static final int HEADERSIZE = FILEHEADERSIZE + INFOHEADERSIZE;
    public static final short UL = 0x554c;
    public static final short OB = 0x4f42;
    public static final short OW = 0x4f57;
    public static final short UN = 0x554e;
    public static final short SQ = 0x5351;

    // Http
    public static final String CONTENTTYPE_HTML = "text/html";
    public static final String CONTENTTYPE_XML = "text/xml";
    public static final String CONTENTTYPE_TEXT = "text/plain";
    public static final String CONTENTTYPE_GIF = "text/gif";
    public static final String CONTENTTYPE_JPEG = "text/jpeg";
    public static final String CONTENTTYPE_MPEG = "text/mpeg";
    public static final String REQUESTMETHOD_GET = "GET";
    public static final String REQUESTMETHOD_POST = "POST";
    public static final String PROTOCOL_HTTP = "http";
    public static final String DEFAULT_CHARSET = "UTF-8";
}

