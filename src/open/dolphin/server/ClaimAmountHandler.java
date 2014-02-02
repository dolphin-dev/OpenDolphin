/*
 * ClaimAmountHandler.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *	
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *	
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package open.dolphin.server;

import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import java.util.*;
import java.io.*;

/**
 * Add PatientVist record to LDAP Server.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class ClaimAmountHandler extends DefaultHandler {
    
    private LinkedList linkList;
    
    private Trace trace;
        
    /** Creates new PatientRegister */
    public ClaimAmountHandler() {
        super();
    }

    public Trace getTrace() {
        return trace;
    }
    
    public void setTrace(Trace trace) {
        this.trace = trace;
    }
    
    public boolean parse(String claim) {
        
        boolean result = false;
        
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(false);
            spf.setValidating(false);

            SAXParser saxParser = spf.newSAXParser();

            // Get the encapsulated SAX XMLReader
            XMLReader xmlReader = saxParser.getXMLReader();
           
            xmlReader.setContentHandler(this);
            xmlReader.setErrorHandler(new MyErrorHandler (trace));

            BufferedReader br = new BufferedReader(new StringReader(claim));
            InputSource source = new InputSource(br);
            xmlReader.parse(source);
            
            br.close();
            
            result = true;
        }
        catch (Exception e) {
            if (trace != null) {
                trace.error("Exception while reception data: " + e.toString());
            }
        }
        return result;
    }
    
    public void startDocument() {  
        if (trace != null) {
            trace.debug("start document");
        }
        linkList = new LinkedList();
    }

    public void endDocument () {  
        if (trace != null) {
            trace.debug("end document");
            trace.debug("----------------------------------");
            trace.debug("----------------------------------");
        }
    }

    public void startElement(String uri, String name, String qName, Attributes attrs) 
    throws SAXException {
        
        /*System.out.println("start element");
        System.out.println("uri = " + uri);
        System.out.println("name = " + name);
        System.out.println("qName = " + qName);
        if (attrs != null) {
            for (int i = 0; i < attrs.getLength(); i++) {
                String aName = attrs.getLocalName(i); // Attr name 
                if ("".equals(aName)) aName = attrs.getQName(i);
                System.out.println(aName + " =\""+attrs.getValue(i)+"\"");
            }
        }*/
        
        linkList.addLast(qName);
        String path = getCurrentPath();
        
        if (trace != null) {
            trace.debug(path);
        }
    }    
    
    public void endElement(String uri, String name, String qName) throws SAXException {
        /*System.out.println("end element");
        System.out.println("uri = " + uri);
        System.out.println("name = " + name);
        System.out.println("qName = " + qName);*/
        if (linkList.size() == 0) {
            return;
        }
        
        if (trace != null) {
            trace.debug(getCurrentPath());
        }
        linkList.removeLast();
    }
   
    public void characters(char ch[], int start, int length) {
        
        String text = new String(ch, start, length);
        //text.trim();
        
        int st = 0;
        int len = text.length();
        
        while( st < len) {
            if (text.charAt(st) > 32) {
                break;
            }
            st++;
        }
        int ed = len - 1;
        while (ed > st) {
            if (text.charAt(ed) > 32) {
                break;
            }
            ed--;
        }
        
        if (ed != 0) {
            text = text.substring(st, ed + 1);
            if ( (text != null) && (!text.equals("")) ) {
                
                if (trace != null) {
                    trace.debug("value: " + text);
                }
            }
        }
    }
    
    private String getCurrentPath() {
        if (linkList.size() == 0) {
            return null;
        }
        StringBuffer buf = new StringBuffer();
        int len = linkList.size();
        for (int i = 0; i < len -1; i++) {
            buf.append((String)linkList.get(i));
            buf.append("/");
        }
        buf.append((String)linkList.get(len -1));
        return buf.toString();
    }
        
    // Error handler to report errors and warnings
    private static class MyErrorHandler implements ErrorHandler {
        /** Error handler output goes here */
        private Trace trace;

        MyErrorHandler(Trace trace) {
            this.trace = trace;
        }

        /**
         * Returns a string describing parse exception details
         */
        private String getParseExceptionInfo(SAXParseException spe) {
            String systemId = spe.getSystemId();
            if (systemId == null) {
                systemId = "null";
            }
            String info = "URI=" + systemId +
                " Line=" + spe.getLineNumber() +
                ": " + spe.getMessage();
            return info;
        }

        // The following methods are standard SAX ErrorHandler methods.
        // See SAX documentation for more info.

        public void warning (SAXParseException spe) throws SAXException {
            if (trace != null) {
                trace.error ("Warning: " + getParseExceptionInfo(spe));
            }
        }
        
        public void error (SAXParseException spe) throws SAXException {
            String message = "Error: " + getParseExceptionInfo(spe);
            throw new SAXException (message);
        }

        public void fatalError (SAXParseException spe) throws SAXException {
            String message = "Fatal Error: " + getParseExceptionInfo(spe);
            throw new SAXException(message);
        }
    }    
}