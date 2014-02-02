/*
 * MMLDirector.java
 *
 * Created on 2001/09/17, 19:30
 */

package jp.ac.kumamoto_u.kuh.fc.jsato.math_mml_three;

import org.xml.sax.*;
import java.util.*;

import jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.*;

/**
 *
 * @author	Junzo SATO
 * @version
 */
public class MMLDirector implements ContentHandler {
    MMLBuilder builder = null;
    
    public MMLBuilder getMMLBuilder() {
        return builder;
    }
        
    /** Creates new MMLDirector */
    public MMLDirector() {
        builder = new MMLBuilder(/*v*/);
    }
    
    StatusBean bean = null;
    public MMLDirector(StatusBean bean) {
        this.bean = bean;
        
        builder = new MMLBuilder(/*v*/);
    }
    
    public void releaseDirector() {
        builder.releaseVector();
    }
    
    private void printlnStatus(String s) {
        if (bean != null) {
            bean.printlnStatus(s);
        } else {
            System.out.println(s);
        }
    }
    
    // ContentHandler ----------------------------------------------------------
    public void startDocument() {
        //Receive notification of the beginning of a document. 
        printlnStatus("startDocument:");
    }
    
    public void endDocument() {
        //Receive notification of the end of a document. 
        printlnStatus("endDocument:");
    }
    
    public void startPrefixMapping(String prefix, String uri) {
        //Begin the scope of a prefix-URI Namespace mapping 
        if (prefix == null) {
            printlnStatus("prefix is null");
        }
        printlnStatus("startPrefixMapping: prefix = " + prefix + ", uri = " + uri);
    }
    
    public void endPrefixMapping(String prefix) {
        //End the scope of a prefix-URI mapping. 
        if (prefix == null) {
            printlnStatus("prefix is null");
        }
        printlnStatus("endPrefixMapping: prefix = " + prefix);
    }
    
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
        //Receive notification of the beginning of an element. 
        printlnStatus("startElement: namespaceURI = " + namespaceURI);
        printlnStatus(": localName = " + localName);
        printlnStatus(": qName = " + qName);
        
        if (atts != null) {
            for (int i = 0; i < atts.getLength(); ++i) {
                printlnStatus(": attr URI = " + atts.getURI(i));
                printlnStatus(": attr QName = " + atts.getQName(i));
                printlnStatus(": attr LocalName = " + atts.getLocalName(i));
                printlnStatus(": attr Type = " + atts.getType(i));
                printlnStatus(": attr Value = " + atts.getValue(i));
            }
        }
        
        //------------------------------------------------------------
        builder.buildStart(namespaceURI, localName, qName, atts);
    }
    
    public void endElement(String namespaceURI, String localName, String qName) {
        //Receive notification of the end of an element. 
        printlnStatus("endElement: namespaceURI = " + namespaceURI);
        printlnStatus(": localName = " + localName);
        printlnStatus(": qName = " + qName);
        
        //------------------------------------------------------------
        builder.buildEnd(namespaceURI, localName, qName);
    }
    
    public void characters(char[] ch, int start, int length) {
        //Receive notification of character data. 
        //if (length <= 0) {
        //    System.out.println("characters: IGNORED because it called with non positive length.");
        //    return;
        //}
        
        StringBuffer buffer = new StringBuffer(length);
        buffer.append(ch, start, length);
        printlnStatus("characters: " + buffer.toString());
        //------------------------------------------------------------
        builder.characters(ch, start, length);
    }
    
    public void ignorableWhitespace(char[] ch, int start, int length) {
        //Receive notification of ignorable whitespace in element content. 
        StringBuffer buffer = new StringBuffer(length);
        buffer.append(ch,start,length);
        printlnStatus("ignorableWhitespace: " + buffer.toString());
    }
    
    public void processingInstruction(String target, String data) {
        //Receive notification of a processing instruction. 
        printlnStatus("processingInstruction: target = " + target + ", data = " + data);
        //
        //======================================================================
        //======================================================================
        // Keep the instruction information.
        builder.getMmlInstruction().addElement(target + "<>" + data);
        
        // Fortunately, we can know the 'parent element' of this instruction:-)
        // That is, if the <?mmlResult ...?> is found, the object Mml would be the parent.
        // So MmlModuleItem is for the <?mmlItemResult ...?>.
        if (builder.getMmlTree() != null && builder.getMmlTree().lastElement() != null) {
            builder.getMmlInstructionTable().addElement(builder.getMmlTree().lastElement());
        }
        //======================================================================
        //======================================================================
    }
    
    public void skippedEntity(String name) {
        //Receive notification of a skipped entity. 
        printlnStatus("skippedEntity: name = " + name);
    }

    public void setDocumentLocator(Locator locator) {
        //Receive an object for locating the origin of SAX document events. 
        printlnStatus("setDocumentLocator: locator = " + locator);
    }
    //--------------------------------------------------------------------------
}

