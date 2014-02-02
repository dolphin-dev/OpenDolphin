/*
 * MMLObject.java
 *
 * Created on 2001/09/19, 2:44
 */

package jp.ac.kumamoto_u.kuh.fc.jsato.math_mml_three;

import java.awt.*;
import org.xml.sax.*;

import java.io.*;

/**
 *
 * @author	Junzo SATO
 * @version
 */
public abstract class MMLObject {
    
    // this parentElement is NOT an actual object in the tree, 
    // but the parent builder element in the builder vector.
    // this reference in used by the parser.
    public MMLObject parentElement = null;    
    
    // index of the element object in the tree
    protected int parentIndex = -1;
    public void setParentIndex( int parentIndex ) {
        this.parentIndex = parentIndex;
    }
    public int getParentIndex() {
        return parentIndex;
    }
    
    public void printObject(PrintWriter pw, MMLVisitor visitor) throws IOException {
        if (this.qName != null) {
            pw.print( visitor.getTabPadding() + "<" + this.qName);
            if (atts != null) {
                for (int i = 0; i < atts.getLength(); ++i) {
                    pw.print(" " + atts.getQName(i) + "='" + atts.getValue(i) + "'");
                }
            }
            pw.print(">");
            ////////////////////////////////////////////////////////////
            pw.print( visitor.getTabPadding() + "</" + this.qName + ">");
            pw.flush();
        }
    }

    // this is a tiny utility to get a string representation of the mml object
    public String toString(MMLVisitor visitor) {
        String s = null;
        
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(new BufferedWriter(sw));
            this.printObject(pw, visitor);
            sw.flush();
            pw.flush();
            s = sw.toString();
            sw.close();
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return s;
    }
    
    //===================================================================================
    
    /** Holds value of property namespace. */
    private String namespace = null;
    
    /** Holds value of property localName. */
    private String localName = null;
    
    /** Holds value of property qName. */
    private String qName = null;
    
    /** Holds value of property atts. */
    private Attributes atts = null;
        
    public boolean buildStart(String namespaceURI, String localName, String qName, Attributes atts, MMLBuilder builder) {
        // only the subclass object with correct tag name should call this method.
        // inside the subclass you must override this method and check the tag name.
        this.namespace = namespaceURI;
        this.localName = localName;
        this.qName = qName;
        this.atts = atts;
        /*
        System.out.println("namespaceURI:" + namespaceURI);
        System.out.println("localName:" + localName);
        System.out.println("qName:" + qName);
        System.out.println("atts:" + atts);
         */
        
        // save current builder element
        parentElement = builder.getCurrentElement();
        // update current builder element with this element
        builder.setCurrentElement(this);
        return false;
    }
    
    public boolean buildEnd(String namespaceURI, String localName, String qName, MMLBuilder builder) {
        // only the subclass object with correct tag name should call this method.
        // inside the subclass you must override this method and check the tag name.
        //
        // restore current element by saved one
        builder.setCurrentElement(parentElement);
        return false;
    }
    
    public boolean characters(char[] ch, int start, int length, MMLBuilder builder) {
        return false;
    }
    
    public void printlnStatus(String s) {
        //System.out.println(s);
    }
    
    /** Getter for property namespace.
     * @return Value of property namespace.
     */
    public String getNamespace() {
        return namespace;
    }
    
    /** Setter for property namespace.
     * @param namespace New value of property namespace.
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
    
    /** Getter for property localName.
     * @return Value of property localName.
     */
    public String getLocalName() {
        return localName;
    }
    
    /** Setter for property localName.
     * @param localName New value of property localName.
     */
    public void setLocalName(String localName) {
        this.localName = localName;
    }
    
    /** Getter for property qName.
     * @return Value of property qName.
     */
    public String getQName() {
        return qName;
    }
    
    /** Setter for property qName.
     * @param qName New value of property qName.
     */
    public void setQName(String qName) {
        this.qName = qName;
    }
    
    /** Getter for property atts.
     * @return Value of property atts.
     */
    public Attributes getAtts() {
        return atts;
    }
    
    /** Setter for property atts.
     * @param atts New value of property atts.
     */
    public void setAtts(Attributes atts) {
        this.atts = atts;
    }
}

