package open.dolphin.converter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.mail.MessagingException;
import javax.mail.internet.MimeUtility;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.KarteBean;
import open.dolphin.infomodel.UserModel;

/**
 *
 * @author Kazushi Minagawa. Digital Globe, Inc.
 */
public final class PlistConverter {

    private static final String XML_START = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    private static final String PLIST_START = "<plist version=\"1.0\">";
    private static final String PLIST_END = "</plist>";

    private static final String KEY_START     = "<key>";
    private static final String KEY_END       = "</key>";
    private static final String DICT_START    = "<dict>";
    private static final String DICT_END      = "</dict>";
    private static final String ARRAY_START   = "<array>";
    private static final String ARRAY_END     = "</array>";
    private static final String STRING_START  = "<string>";
    private static final String STRING_END    = "</string>";
    private static final String DATE_START    = "<date>";
    private static final String DATE_END      = "</date>";
    private static final String INTEGER_START = "<integer>";
    private static final String INTEGER_END   = "</integer>";
    private static final String REAL_START    = "<real>";
    private static final String REAL_END      = "</real>";
    private static final String BOOLEAN_TRUE  = "<true/>";
    private static final String BOOLEAN_FALSE = "<false/>";
    private static final String DATA_START    = "<data>";
    private static final String DATA_END      = "</data>";
    private static final String BASE64        = "base64";

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
    
    private static SimpleDateFormat ISO_DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final String ARRAY_BYTE = "[B";

    private static final String GET = "get";

    private static final String CONVERTER_PACKAGE = "open.dolphin.converter.";
    private static final String CONVERTER_EXT = "Converter";


    private static boolean isGetter(Method method) {
        if (!method.getName().startsWith(GET)) {
            return false;
        }
        if (method.getParameterTypes().length != 0) {
            return false;
        }
        if (void.class.equals(method.getReturnType())) {
            return false;
        }
        return true;
    }

    private static String methodToProperty(Method method) {
        String name = method.getName();
        String first = name.substring(3,4).toLowerCase();
        String rest = name.substring(4);
        return first+rest;
    }

    private static void dictStart(StringWriter writer) throws IOException {
        writer.write(DICT_START);
    }

    private static void dictEnd(StringWriter writer) throws IOException {
        writer.write(DICT_END);
    }

    private static void keyDict(String key, StringWriter writer) throws IOException {
        writer.write(KEY_START);
        writer.write(key);
        writer.write(KEY_END);
        writer.write(DICT_START);
    }

    private static void arrayStart(StringWriter writer) throws IOException {
        writer.write(ARRAY_START);
    }

    private static void arrayEnd(StringWriter writer) throws IOException {
        writer.write(ARRAY_END);
    }

    private static void keyArray(String key, StringWriter writer) throws IOException {
        writer.write(KEY_START);
        writer.write(key);
        writer.write(KEY_END);
        writer.write(ARRAY_START);
    }

    private static void keyInteger(String key, int value, StringWriter writer) throws IOException {
        writer.write(KEY_START);
        writer.write(key);
        writer.write(KEY_END);
        writer.write(INTEGER_START);
        writer.write(String.valueOf(value));
        writer.write(INTEGER_END);
    }

    private static void keyLong(String key, long value, StringWriter writer) throws IOException {
        writer.write(KEY_START);
        writer.write(key);
        writer.write(KEY_END);
        writer.write(INTEGER_START);
        writer.write(String.valueOf(value));
        writer.write(INTEGER_END);
    }

    private static void keyFloat(String key, float value, StringWriter writer) throws IOException {
        writer.write(KEY_START);
        writer.write(key);
        writer.write(KEY_END);
        writer.write(REAL_START);
        writer.write(String.valueOf(value));
        writer.write(REAL_END);
    }

    private static void keyDouble(String key, double value, StringWriter writer) throws IOException {
        writer.write(KEY_START);
        writer.write(key);
        writer.write(KEY_END);
        writer.write(REAL_START);
        writer.write(String.valueOf(value));
        writer.write(REAL_END);
    }

    private static void keyString(String key, String value, StringWriter writer) throws IOException {
        if (value != null) {

            value = value.replaceAll(STRING_LT, XML_LT);
            value = value.replaceAll(STRING_GT, XML_GT);
            value = value.replaceAll(STRING_AND, XML_AND);
            value = value.replaceAll(STRING_QUOT, XML_QUOT);
            value = value.replaceAll(STRING_APOS, XML_APOS);

            writer.write(KEY_START);
            writer.write(key);
            writer.write(KEY_END);
            writer.write(STRING_START);
            writer.write(value);
            writer.write(STRING_END);
        }
    }

    protected static void string(String value, StringWriter writer) throws IOException {
        if (value != null) {
            value = value.replaceAll(STRING_LT, XML_LT);
            value = value.replaceAll(STRING_GT, XML_GT);
            value = value.replaceAll(STRING_AND, XML_AND);
            value = value.replaceAll(STRING_QUOT, XML_QUOT);
            value = value.replaceAll(STRING_APOS, XML_APOS);
            writer.write(STRING_START);
            writer.write(value);
            writer.write(STRING_END);
        }
    }
    
    private static void keyBoolean(String key, boolean value, StringWriter writer) throws IOException {
        writer.write(KEY_START);
        writer.write(key);
        writer.write(KEY_END);
        if (value) {
            writer.write(BOOLEAN_TRUE);
        } else {
            writer.write(BOOLEAN_FALSE);
        }
    }

    private static void keyDate(String key, Date value, StringWriter writer) throws IOException {
        if (value != null) {
            writer.write(KEY_START);
            writer.write(key);
            writer.write(KEY_END);
            writer.write(DATE_START);
            writer.write(ISO_DF.format(value));
            writer.write(DATE_END);
        }
    }

    private static void keyData(String key, byte[] value, StringWriter writer) throws IOException, MessagingException {
        if (value != null) {
            String base64Str = new String(base64Encode(value));
            writer.write(KEY_START);
            writer.write(key);
            writer.write(KEY_END);
            writer.write(DATA_START);
            writer.write(base64Str);
            writer.write(DATA_END);
        }
    }

    private static byte[] base64Encode(byte[] value) throws IOException, MessagingException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStream b64os = MimeUtility.encode(baos, BASE64);
        b64os.write(value);
        b64os.close();
        return baos.toByteArray();
     }

//     private static byte[] base64Decode(byte[] value) throws IOException,MessagingException {
//        ByteArrayInputStream bais = new ByteArrayInputStream(value);
//        InputStream b64is = MimeUtility.decode(bais, BASE64);
//        byte[] tmp = new byte[value.length];
//        int n = b64is.read(tmp);
//        byte[] res = new byte[n];
//        System.arraycopy(tmp, 0, res, 0, n);
//        return res;
//     }

     protected static KarteBean createDuumyKarteBean(long pk) {
         KarteBean ret = new KarteBean();
         ret.setId(pk);
         return ret;
     }

     protected static UserModel createDummyUserModel(long pk) {
         UserModel ret = new UserModel();
         ret.setId(pk);
         return ret;
     }

     private static void closeWriter(StringWriter sw) {

         try {
             if (sw != null) {
                 sw.close();
             }

         } catch (IOException e) {
         }
     }
         
     private static void reflectConvert(IInfoModelConverter target, StringWriter writer)
            throws IOException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, MessagingException, ClassNotFoundException, InstantiationException {

        // classñºÇ©ÇÁ key Çê∂ê¨Ç∑ÇÈ
        String clsName = target.getClass().getName();
        int from = clsName.lastIndexOf(".") + 1;
        int to = clsName.indexOf(CONVERTER_EXT);
        clsName = clsName.substring(from, to);

        StringBuilder sb = new StringBuilder();
        sb.append(clsName.substring(0,1).toLowerCase());
        sb.append(clsName.substring(1));

        keyDict(sb.toString(), writer);

        //--------------------------
        // property
        //--------------------------
        Method[] methods = target.getClass().getMethods();

        for (Method method : methods) {

            if (!isGetter(method)) {
                continue;
            }
             
            Object value = method.invoke(target, (Object[])null);

            if (value == null) {
                continue;
            }

            String prop = methodToProperty(method);
            Class retType = method.getReturnType();

            if (retType.equals(long.class)) {
                keyLong(prop, ((Long)value).longValue(), writer);

            } else if (retType.equals(String.class)) {
                keyString(prop, (String)value, writer);

            } else if (retType.equals(Date.class)) {
                keyDate(prop, (Date)value, writer);

            } else if (retType.getName().equals(ARRAY_BYTE)) {
                // byte[]
                keyData(prop, (byte[])value, writer);

            } else if (retType.equals(List.class)) {
                keyArray(prop, writer);
                List list = (List)value;
                for (Object obj : list) {

                    if (obj instanceof String) {
                        string((String)obj, writer);
                    } else {
                        IInfoModelConverter converter = createConverter((IInfoModel)obj);
                        reflectConvert(converter, writer);
                    }
                }
                arrayEnd(writer);

            } else if (value instanceof IInfoModel) {
                IInfoModelConverter ac = createConverter((IInfoModel)value);
                reflectConvert(ac, writer);

            } else if (retType.equals(int.class)) {
                keyInteger(prop, ((Integer)value).intValue(), writer);

            } else if (retType.equals(float.class)) {
                keyFloat(prop, ((Float)value).floatValue(), writer);

            } else if (retType.equals(double.class)) {
                keyDouble(prop, ((Double)value).doubleValue(), writer);

            } else if (retType.equals(Boolean.TYPE)) {
                keyBoolean(prop, (Boolean)value, writer);
            }
        }

        dictEnd(writer);
    }

    private static IInfoModelConverter createConverter(IInfoModel model)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        String className = model.getClass().getName();
        int index = className.lastIndexOf(".");
        className = className.substring(index+1);

        StringBuilder sb = new StringBuilder();
        sb.append(CONVERTER_PACKAGE);
        sb.append(className);
        sb.append(CONVERTER_EXT);
        String converterName = sb.toString();

        IInfoModelConverter ret = (IInfoModelConverter) Class.forName(converterName).newInstance();

        ret.setModel(model);

        return ret;
    }

    private static String convertAsList(List list)
            throws IOException, ClassNotFoundException, InstantiationException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, MessagingException {

        String ret = null;
        
        StringWriter writer = new StringWriter();
        // <array>
        arrayStart(writer);

        for (Object o : list) {

            if (o instanceof IInfoModel) {

                IInfoModelConverter converter = createConverter((IInfoModel)o);
                reflectConvert(converter, writer);

            } else if (o instanceof List) {

                arrayStart(writer);

                List l = (List)o;
                for (Object obj : l) {

                    IInfoModelConverter converter = createConverter((IInfoModel)obj);
                    reflectConvert(converter, writer);
                }

                arrayEnd(writer);
            }
        }

        // </array>
        arrayEnd(writer);

        ret = writer.toString();
        closeWriter(writer);

        return ret;
    }
        
    private static String convertAsRoot(IInfoModel target)
            throws IOException, ClassNotFoundException, InstantiationException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, MessagingException {
        
        String ret = null;
        StringWriter writer = new StringWriter();

        dictStart(writer);

        IInfoModelConverter converter = createConverter(target);
        reflectConvert(converter, writer);

        dictEnd(writer);
        ret = writer.toString();
        closeWriter(writer);

        return ret;
    }

    public String convert(Object obj) throws ConverterException {

        try {
            StringWriter writer = new StringWriter();
            writer.write(XML_START);
            writer.write(PLIST_START);

            if (obj instanceof IInfoModel) {

                writer.write(convertAsRoot((IInfoModel)obj));

            } else if (obj instanceof List) {

                writer.write(convertAsList((List)obj));
            }

            writer.write(PLIST_END);

            String result = writer.toString();
            closeWriter(writer);
            return result;

        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw new ConverterException(e);
        }
    }
}
