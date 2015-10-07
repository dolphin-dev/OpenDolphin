package open.dolphin.touch.converter;

import open.dolphin.infomodel.*;

/**
 * IMKDocument2
 *
 * 2014/02/06 iPadのFreeText対応
 * @author S.Oh@Life Sciences Computing Corporation.
 */
public class IMKDocument2 implements java.io.Serializable {
    
    private String key;
    private IDocument2 document;
    
    
    public IMKDocument2() {
        document = new IDocument2();
    }
    
    /**
     * iOSからIDocumentのJSONが送信される。　
     * パースしたIDocumentをDocumentModelへ変換する。
     * @return DocumentModel
     */
    public DocumentModel toModel() {
        return getDocument().toModel();
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return the document
     */
    public IDocument2 getDocument() {
        return document;
    }

    /**
     * @param document the document to set
     */
    public void setDocument(IDocument2 document) {
        this.document = document;
    }
}