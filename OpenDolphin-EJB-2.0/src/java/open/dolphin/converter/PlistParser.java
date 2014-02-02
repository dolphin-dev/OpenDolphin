package open.dolphin.converter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.mail.MessagingException;
import javax.mail.internet.MimeUtility;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class PlistParser {

    private static final int TT_KEY         = 0;
    private static final int TT_STRING      = 1;
    private static final int TT_INTEGER     = 2;
    private static final int TT_REAL        = 3;
    private static final int TT_DATE        = 4;
    private static final int TT_DATA        = 5;
    private static final int TT_TRUE        = 6;
    private static final int TT_FALSE       = 7;
    private static final int TT_DICT        = 8;
    private static final int TT_ARRAY       = 9;

    private static final String XML_LT = "&lt;";
    private static final String XML_GT = "&gt;";
    private static final String XML_AND = "&amp;";
    private static final String XML_QUOT = "&quot;";
    private static final String XML_APOS = "&apos;";

    private static final String STRING_LT = "<";
    private static final String STRING_GT = ">";
    private static final String STRING_AND = "&";
    private static final String STRING_QUOT = "\"";
    private static final String STRING_APOS = "'";

    private static final String DICT = "dict";
    private static final String ARRAY = "array";

    private static final String[] ELEMENTS =
        new String[]{"key", "string", "integer", "real", "date", "data", "true", "false", DICT, ARRAY};

    private static final String MODEL_PACKAGE = "open.dolphin.infomodel.";

    private static final String SET = "set";

    private static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final String BASE64 = "base64";

    private static Boolean DEBUG = false;

    private List<Object> stack;

    private String currentKey;

    private StringBuilder characterBuffer;

    private int currentParsing;

    public PlistParser() {
        stack = new ArrayList<Object>(10);
    }

    public Object parse(String plist) {

        SAXParserFactory factory = SAXParserFactory.newInstance();
        StringReader reader = new StringReader(plist);

        try {

            SAXParser saxParser = factory.newSAXParser();

            DefaultHandler handler = new DefaultHandler() {

                @Override
                public void startElement(String uri, String localName,
                        String qName, Attributes attributes)
                        throws SAXException {

                    if (qName.equals(DICT)) {
                        if (currentKey != null) {
                            Object obj = createObject(currentKey);
                            if (stack.size() > 0) {
                                storeObject(currentKey, obj);
                            }
                            stack.add(0, obj);
                        }

                    } else if (qName.equals(ARRAY)) {
                        List list = new ArrayList();
                        if (stack.size() > 0) {
                            storeList(currentKey, list);
                        }
                        stack.add(0, list);
                    }
                }

                @Override
                public void endElement(String uri, String localName,
                        String qName)
                        throws SAXException {

                    for (int i = 0; i < ELEMENTS.length; i++) {
                        if (qName.equals(ELEMENTS[i])) {
                            currentParsing = i;
                            break;
                        }
                    }

                    // 集積した charactor から文字列を取得
                    String value = builderToString();

                    switch (currentParsing) {

                        case TT_KEY:
                            currentKey = value != null ? value : null;
                            break;

                        case TT_STRING:
                            if (value!=null && (!value.equals(""))) {
                                value = value.replaceAll(XML_LT, STRING_LT);
                                value = value.replaceAll(XML_GT, STRING_GT);
                                value = value.replaceAll(XML_AND, STRING_AND);
                                value = value.replaceAll(XML_QUOT, STRING_QUOT);
                                value = value.replaceAll(XML_APOS, STRING_APOS);
                                storeString(currentKey, value);
                            }
                            break;

                        case TT_INTEGER:
                            if (value != null) {
                                storeInteger(currentKey, value);
                            }
                            break;

                        case TT_REAL:
                            if (value != null) {
                                storeReal(currentKey, value);
                            }
                            break;

                        case TT_DATE:
                            if (value != null) {
                                Date date = parseDate(value);
                                storeDate(currentKey, date);
                            }
                            break;

                        case TT_DATA:
                            if (value != null) {
                                try {
                                    byte[] bytes = base64Decode(value.getBytes());
                                    storeByte(currentKey, bytes);
                                } catch (Exception e) {
                                    System.err.println("TT_DATA Exception: " + e.getMessage());
                                }
                            }
                            break;

                        case TT_TRUE:
                            storeBoolean(currentKey, true);
                            break;

                        case TT_FALSE:
                            storeBoolean(currentKey, false);
                            break;

                        case TT_DICT:
                            if (stack.size() > 1) {
                                stack.remove(0);
                                //System.err.println("dict removed from stack, size=" + stack.size());
                            }
                            break;

                        case TT_ARRAY:
                            if (stack.size() > 1) {
                                stack.remove(0);
                                //System.err.println("list removed from stack, size=" + stack.size());
                            }
                            break;
                    }
                }

                @Override
                public void characters(char ch[], int start, int length)
                        throws SAXException {

                    // ゴミを除去する
                    String parsedCharacterData = currentParsing == TT_DATA
                                               ? new String(ch, start, length).trim()
                                               : new String(ch, start, length);

                    // 集積する
                    if (characterBuffer == null) {
                        characterBuffer = new StringBuilder();
                        characterBuffer.append(parsedCharacterData);
                    } else {
                        characterBuffer.append(parsedCharacterData);
                    }
                }
            };

            saxParser.parse(new InputSource(reader), handler);

            reader.close();

        } catch (Exception e) {
            System.err.println("Exception at convert(): " + e.getMessage());
            for (int i=0; i < stack.size(); i++) {
                stack.remove(0);
            }
        }

        return (stack.size() > 0) ? stack.remove(0) : null;
        
    }

    private Object createObject(String clsName) {

        Object ret = null;

        try {
            StringBuilder sb = new StringBuilder();
            sb.append(MODEL_PACKAGE);
            sb.append(clsName.substring(0,1).toUpperCase());
            sb.append(clsName.substring(1));
            String fullName = sb.toString();

            ret = Class.forName(fullName).newInstance();

        } catch (Exception e) {
            debug(e.getMessage());
            ret = null;
        }

        return ret;
    }
        
    private void storeBoolean(String name, Object value) {
        
        if (name != null && value != null) { 
            setValue(name, value, Boolean.TYPE);
        }
    }

    private void storeByte(String name, byte[] bytes) {

        if (name != null && bytes != null) {
            setValue(name, bytes, bytes.getClass());
        }
    }

    private void storeString(String name, Object value) {

        if (name != null && value != null) {
            if (currentTargetIsList()) {
                addToList(value);
            } else {
                setValue(name, value, String.class);
            }
        }
    }

    private void storeDate(String name, Object value) {

        if (name != null && value != null) {
            if (currentTargetIsList()) {
                addToList(value);
            } else {
                setValue(name, value, Date.class);
            }
        }
    }

    private void storeObject(String name, Object value) {

        if (name != null && value != null) {
            if (currentTargetIsList()) {
                addToList(value);
            } else {
                setValue(name, value, value.getClass());
            }
        }
    }

    private void storeList(String name, Object list) {

        if (list != null) {
            if (currentTargetIsList()) {
                addToList(list);
            } else if (name!=null) {
                setValue(name, list, List.class);
            }
        }
    }

    private void setValue(String name, Object value, Class cls) {

        try {
            Object target = stack.get(0);

            String setter = toSetter(name);

            Method mth = target.getClass().getMethod(setter, cls);
            mth.invoke(target, value);

        } catch (Exception e) {
            //System.err.println("Exception setValue: " + e.getMessage());
        }
    }
    
    private void storeInteger(String name, String value) {

        Object target = stack.get(0);

        String setter = toSetter(name);

        try {
            Method mth = target.getClass().getMethod(setter, long.class);
            mth.invoke(target, Long.parseLong(value));
            return;
        } catch (Exception e) {
        }

        try {
            Method mth = target.getClass().getMethod(setter, int.class);
            mth.invoke(target, Integer.parseInt(value));
        } catch (Exception e) {
        }
    }

    private void storeReal(String name, String value) {

        Object target = stack.get(0);

        String setter = toSetter(name);

        try {
            Method mth = target.getClass().getMethod(setter, float.class);
            mth.invoke(target, Float.parseFloat(value));
            return;
        } catch (Exception e) {
        }

        try {
            Method mth = target.getClass().getMethod(setter, double.class);
            mth.invoke(target, Double.parseDouble(value));
        } catch (Exception e) {
        }
    }

    private void addToList(Object value) {

        try {
            ArrayList target = (ArrayList)stack.get(0);
            target.add(value);

        } catch (Exception e) {
            System.err.println("Exception addToList: " + e.getMessage());
        }
    }

    private boolean currentTargetIsList() {

        Object target = stack.get(0);
        return (target instanceof ArrayList) ? true : false;
    }

    private String toSetter(String name) {
        StringBuilder sb = new StringBuilder();
        sb.append(SET);
        sb.append(name.substring(0,1).toUpperCase());
        sb.append(name.substring(1));
        String setter = sb.toString();
        return setter;
    }

    private String builderToString() {

        String ret = null;

        if (characterBuffer != null && characterBuffer.length() > 0) {
            ret = characterBuffer.toString();
            characterBuffer = null;
        }

        return ret;
    }

    private static Date parseDate(String dateStr) {

        Date ret = null;

        try {
            ret = SDF.parse(dateStr);
        } catch (Exception e) {
        }

        return ret;
    }

    public static byte[] base64Encode(byte[] value) throws IOException, MessagingException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStream b64os = MimeUtility.encode(baos, BASE64);
        b64os.write(value);
        b64os.close();
        return baos.toByteArray();
    }

    public static byte[] base64Decode(byte[] value) throws IOException, MessagingException {
        ByteArrayInputStream bais = new ByteArrayInputStream(value);
        InputStream b64is = MimeUtility.decode(bais, BASE64);
        byte[] tmp = new byte[value.length];
        int n = b64is.read(tmp);
        byte[] res = new byte[n];
        System.arraycopy(tmp, 0, res, 0, n);
        return res;
    }

    private void debug(String str) {
        if (DEBUG) {
            System.err.println(str);
        }
    }
}
