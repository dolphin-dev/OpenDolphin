package open.dolphin.touch.converter;

import open.dolphin.infomodel.*;

/**
 * IDocument
 *
 * @author Minagawa,Kazushi. Digital Globe, Inc.
 */
public class IMKDocument implements java.io.Serializable {
    
    private String key;
    private IDocument document;
    
    
    public IMKDocument() {
        document = new IDocument();
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
    public IDocument getDocument() {
        return document;
    }

    /**
     * @param document the document to set
     */
    public void setDocument(IDocument document) {
        this.document = document;
    }
}