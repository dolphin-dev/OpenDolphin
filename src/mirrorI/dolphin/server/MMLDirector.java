/*
 * MMLDirector.java
 *
 * Created on 2001/09/17, 19:30
 *
 * Modified by Mirror-I on 2003/01/30 for storing parsed data in Postgres DB, logger function added
 *
 * Last updated on 2003/02/28
 */

package mirrorI.dolphin.server;

import org.xml.sax.*;
import java.util.logging.*;

import jp.ac.kumamoto_u.kuh.fc.jsato.math_mml.*;

/**
 *
 * @author	Junzo SATO
 * @version
 */
public class MMLDirector implements ContentHandler {

    MMLBuilder builder = null;
    private Logger logger;


    /** Creates new MMLDirector */
    public MMLDirector(Logger loggerLabo) {

		//Logger for logging messages
    	logger = loggerLabo;

      	builder = new MMLBuilder();
    }

    public MMLBuilder getMMLBuilder() {
	        return builder;
	}

    public void releaseDirector() {
        builder.releaseVector();
    }

    // ContentHandler ----------------------------------------------------------
    public void startDocument() {
        //Receive notification of the beginning of a document.
        logger.finer("startDocument:");
    }

    public void endDocument() {
        //Receive notification of the end of a document.
        logger.finer("endDocument:");
    }

    public void startPrefixMapping(String prefix, String uri) {
        //Begin the scope of a prefix-URI Namespace mapping
        if (prefix == null) {
            logger.finer("prefix is null");
        }
        logger.finer("startPrefixMapping: prefix = " + prefix + ", uri = " + uri);
    }

    public void endPrefixMapping(String prefix) {
        //End the scope of a prefix-URI mapping.
        if (prefix == null) {
           logger.finer("prefix is null");
        }
        logger.finer("endPrefixMapping: prefix = " + prefix);
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
        //Receive notification of the beginning of an element.
        logger.finer("startElement: namespaceURI = " + namespaceURI);
        logger.finer(": localName = " + localName);
        logger.finer(": qName = " + qName);

        if (atts != null) {
            for (int i = 0; i < atts.getLength(); ++i) {
                logger.finer(": attr URI = " + atts.getURI(i));
                logger.finer(": attr QName = " + atts.getQName(i));
                logger.finer(": attr LocalName = " + atts.getLocalName(i));
                logger.finer(": attr Type = " + atts.getType(i));
                logger.finer(": attr Value = " + atts.getValue(i));
            }
        }

        //------------------------------------------------------------
        builder.buildStart(namespaceURI, localName, qName, atts);
    }

    public void endElement(String namespaceURI, String localName, String qName) {
        //Receive notification of the end of an element.
        logger.finer("endElement: namespaceURI = " + namespaceURI);
        logger.finer(": localName = " + localName);
        logger.finer(": qName = " + qName);

        //------------------------------------------------------------
        builder.buildEnd(namespaceURI, localName, qName);
    }

    public void characters(char[] ch, int start, int length) {

        StringBuffer buffer = new StringBuffer(length);
        buffer.append(ch, start, length);
        logger.finer("characters: " + buffer.toString());
        //------------------------------------------------------------
        builder.characters(ch, start, length);
    }

    public void ignorableWhitespace(char[] ch, int start, int length) {
        //Receive notification of ignorable whitespace in element content.
        StringBuffer buffer = new StringBuffer(length);
        buffer.append(ch,start,length);
        logger.finer("ignorableWhitespace: " + buffer.toString());
    }

    public void processingInstruction(String target, String data) {
        //Receive notification of a processing instruction.
       logger.finer("processingInstruction: target = " + target + ", data = " + data);
        //
        //======================================================================
        //======================================================================
        // Keep the instruction information.
        builder.getMmlInstruction().addElement(target + "<>" + data);

        // Fortunately, we can know the 'parent element' of this instruction:-)
        // That is, if the <?mmlResult ...?> is found, the object Mml would be the parent.
        // So MmlModuleItem is for the <?mmlItemResult ...?>.
        if (builder.getMmlTree() != null && builder.getMmlTree().size() >0 && builder.getMmlTree().lastElement() != null) {
            builder.getMmlInstructionTable().addElement(builder.getMmlTree().lastElement());
        }
        //======================================================================
        //======================================================================
    }

    public void skippedEntity(String name) {
        //Receive notification of a skipped entity.
        logger.finer("skippedEntity: name = " + name);
    }

    public void setDocumentLocator(Locator locator) {
        //Receive an object for locating the origin of SAX document events.
        logger.finer("setDocumentLocator: locator = " + locator);
    }
    //--------------------------------------------------------------------------
}

