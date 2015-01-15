package open.dolphin.client;

import java.io.Serializable;
import javax.swing.ImageIcon;

/**
 * ImageEntry
 *
 * @author  Kazushi Minagawa, Digital globe, Inc.
 */
public class ImageEntry implements Serializable {
    
    private String confirmDate;
    
    private String title;
    
    private String medicalRole;
    
    private String contentType;
    
    private ImageIcon imageIcon;
    
    private long id;
    
    private String url;
    
    private String fileName;
    
    private String path;
    
    private int numImages = 1;
    
    private int width;
    
    private int height;

    private boolean dicomFileIsSOP;

    private long lastModified;
    
    private boolean directrory;
    
    
    /** Creates a new instance of ImageEntry */
    public ImageEntry() {
    }
    
    public String getConfirmDate() {
        return confirmDate;
    }
    
    public void setConfirmDate(String val) {
        confirmDate = val;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String val) {
        title = val;
    }
    
    public String getMedicalRole() {
        return medicalRole;
    }
    
    public void setMedicalRole(String val) {
        medicalRole = val;
    }
    
    public String getContentType() {
        return contentType;
    }
    
    public void setContentType(String val) {
        contentType = val;
    }
    
    public ImageIcon getImageIcon() {
        return imageIcon;
    }
    
    public void setImageIcon(ImageIcon val) {
        imageIcon = val;
    }
    
    public long getId() {
        return id;
    }
    
    public void setId(long val) {
        id = val;
    }
    
    /**
     * @param url The url to set.
     */
    public void setUrl(String url) {
        this.url = url;
    }
    
    /**
     * @return Returns the url.
     */
    public String getUrl() {
        return url;
    }

    public String getFileName() {
        return fileName;
    }

    public String getPath() {
        return path;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getNumImages() {
        return numImages;
    }

    public void setNumImages(int numImages) {
        this.numImages = numImages;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public boolean isDicomFileIsSOP() {
        return dicomFileIsSOP;
    }

    public void setDicomFileIsSOP(boolean dicomFileIsSOP) {
        this.dicomFileIsSOP = dicomFileIsSOP;
    }

    public boolean isDirectrory() {
        return directrory;
    }

    public void setDirectrory(boolean directrory) {
        this.directrory = directrory;
    }
    
//s.oh^ 2014/05/07 PDF・画像タブの改善
    public ImageEntry copy() {
        ImageEntry entry = new ImageEntry();
        entry.setConfirmDate(this.getConfirmDate());
        entry.setContentType(this.getContentType());
        entry.setDicomFileIsSOP(this.isDicomFileIsSOP());
        entry.setDirectrory(this.isDirectrory());
        entry.setFileName(this.getFileName());
        entry.setHeight(this.getHeight());
        entry.setId(this.getId());
        entry.setImageIcon(this.getImageIcon());
        entry.setLastModified(this.getLastModified());
        entry.setMedicalRole(this.getMedicalRole());
        entry.setNumImages(this.getNumImages());
        entry.setPath(this.getPath());
        entry.setTitle(this.getTitle());
        entry.setUrl(this.getUrl());
        entry.setWidth(this.getWidth());
        return entry;
    }
//s.oh$
}
