package open.dolphin.converter14;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
    
    //private static SimpleDateFormat ISO_DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final String ARRAY_BYTE = "[B";

    private static final String GET = "get";

    private static final String CONVERTER_PACKAGE = "open.dolphin.converter.";
    private static final String CONVERTER_EXT = "Converter";


    /**
     * 引数のメソッドがgetterかどうかを返す。
     * @param method テストする Method
     * @return getterの時 true
     */
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

    /**
     * 引数のメソッドからプロパティ名を生成する。
     * @param method Method
     * @return プロパティ名
     */
    private static String methodToProperty(Method method) {
        // ex. getName,setName
        // n + ame
        String name = method.getName();
        String first = name.substring(3,4).toLowerCase();
        String rest = name.substring(4);
        return first+rest;
    }

    /**
     * 引数のwriterへ <dict>を書き込む。
     * @param writer 書き込む writer
     * @throws IOException 
     */
    private static void dictStart(StringWriter writer) throws IOException {
        writer.write(DICT_START);
    }

    /**
     * 引数のwriterへ </dict>を書き込む。
     * @param writer 書き込む writer
     * @throws IOException 
     */
    private static void dictEnd(StringWriter writer) throws IOException {
        writer.write(DICT_END);
    }

    /**
     * 引数のwriterへ <key>keyName</key><dict>を書き込む。
     * @param key keyの名前
     * @param writer 書き込む writer
     * @throws IOException 
     */
    private static void keyDict(String keyName, StringWriter writer) throws IOException {
        writer.write(KEY_START);
        writer.write(keyName);
        writer.write(KEY_END);
        writer.write(DICT_START);
    }

    /**
     * 引数のwriterへ <array>を書き込む。
     * @param writer 書き込む writer
     * @throws IOException 
     */
    private static void arrayStart(StringWriter writer) throws IOException {
        writer.write(ARRAY_START);
    }

    /**
     * 引数のwriterへ </array>を書き込む。
     * @param writer 書き込む writer
     * @throws IOException 
     */
    private static void arrayEnd(StringWriter writer) throws IOException {
        writer.write(ARRAY_END);
    }

    /**
     * 引数のwriterへ <key>keyName</key><array>を書き込む。
     * @param key keyの名前
     * @param writer 書き込む writer
     * @throws IOException 
     */
    private static void keyArray(String key, StringWriter writer) throws IOException {
        writer.write(KEY_START);
        writer.write(key);
        writer.write(KEY_END);
        writer.write(ARRAY_START);
    }

    /**
     * writerへ <key>keyName</key><integer>value</integer>を書き込む。
     * @param key 名前
     * @param value int の値
     * @param writer 書き込む writer
     * @throws IOException 
     */
    private static void keyInteger(String keyName, int value, StringWriter writer) throws IOException {
        writer.write(KEY_START);
        writer.write(keyName);
        writer.write(KEY_END);
        writer.write(INTEGER_START);
        writer.write(String.valueOf(value));
        writer.write(INTEGER_END);
    }

    /**
     * writerへ <key>keyName</key><integer>value</integer>を書き込む。
     * @param key 名前
     * @param value long の値
     * @param writer 書き込む writer
     * @throws IOException 
     */
    private static void keyLong(String key, long value, StringWriter writer) throws IOException {
        writer.write(KEY_START);
        writer.write(key);
        writer.write(KEY_END);
        writer.write(INTEGER_START);
        writer.write(String.valueOf(value));
        writer.write(INTEGER_END);
    }

    /**
     * writerへ <key>keyName</key><real>value</real>を書き込む。
     * @param key 名前
     * @param value long の値
     * @param writer 書き込む writer
     * @throws IOException 
     */
    private static void keyFloat(String key, float value, StringWriter writer) throws IOException {
        writer.write(KEY_START);
        writer.write(key);
        writer.write(KEY_END);
        writer.write(REAL_START);
        writer.write(String.valueOf(value));
        writer.write(REAL_END);
    }

    /**
     * writerへ <key>keyName</key><real>value</real>を書き込む。
     * @param key 名前
     * @param value double の値
     * @param writer 書き込む writer
     * @throws IOException 
     */
    private static void keyDouble(String key, double value, StringWriter writer) throws IOException {
        writer.write(KEY_START);
        writer.write(key);
        writer.write(KEY_END);
        writer.write(REAL_START);
        writer.write(String.valueOf(value));
        writer.write(REAL_END);
    }

    /**
     * writerへ <key>keyName</key><string>value</string>を書き込む。
     * @param key 名前
     * @param value string の値
     * @param writer 書き込む writer
     * @throws IOException 
     */
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

    /**
     * writerへ <string>value</string>を書き込む。
     * @param value string の値
     * @param writer 書き込む writer
     * @throws IOException 
     */
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
    
    /**
     * writerへ <key>keyName</key><true/>または<false/>を書き込む。
     * @param key key 名前
     * @param value 真偽値
     * @param writer 書き込む writer
     * @throws IOException 
     */
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

    /**
     * writerへ <key>keyName</key><date>Date</date>を書き込む。
     * @param key key 名前
     * @param value Date値
     * @param writer 書き込む writer
     * @throws IOException 
     */
    private static void keyDate(String key, Date value, StringWriter writer) throws IOException {
        if (value != null) {
            writer.write(KEY_START);
            writer.write(key);
            writer.write(KEY_END);
            writer.write(DATE_START);
            writer.write(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(value));
            writer.write(DATE_END);
        }
    }

    /**
     * writerへ <key>keyName</key><data>byte[]</data>を書き込む。
     * @param key key 名前
     * @param value byte[]の値
     * @param writer 書き込む writer
     * @throws IOException 
     */
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

    /**
     * バイトデータを Base64にエンコードする。
     * @param value byteデータ
     * @return base64 byte[]
     * @throws IOException
     * @throws MessagingException 
     */
    private static byte[] base64Encode(byte[] value) throws IOException, MessagingException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStream b64os = MimeUtility.encode(baos, BASE64);
        b64os.write(value);
        b64os.close();
        return baos.toByteArray();
    }

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
         
    /**
     * 引数のオブジェクトをplistへ変換する。
     * @param target 変換するオブジェクト
     * @param writer 書き込むwriter
     * @throws IOException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws MessagingException
     * @throws ClassNotFoundException
     * @throws InstantiationException 
     */
    private static void reflectConvert(IInfoModelConverter target, StringWriter writer)
            throws IOException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, MessagingException, ClassNotFoundException, InstantiationException {

        // class名から key を生成する
        // open.dolphin.infomodel.PatientModelConverter -> PatientModel -> patientModel
        String clsName = target.getClass().getName();
        int from = clsName.lastIndexOf(".") + 1;
        int to = clsName.indexOf(CONVERTER_EXT);
        clsName = clsName.substring(from, to);
        StringBuilder sb = new StringBuilder();
        sb.append(clsName.substring(0,1).toLowerCase());
        sb.append(clsName.substring(1));

        //------------------------------------------
        // <key>patientModel</key>
        // <dict>...........</dict>
        //------------------------------------------
        keyDict(sb.toString(), writer);

        // Methodを調べ、plistのxml要素へ変換する
        Method[] methods = target.getClass().getMethods();

        for (Method method : methods) {

            // getterでなければ continue
            if (!isGetter(method)) {
                continue;
            }
             
            // getterの値を取得する
            Object value = method.invoke(target, (Object[])null);

            // nullなら continue
            if (value == null) {
                continue;
            }

            // getterからプロパティ名を得る
            String prop = methodToProperty(method);
            
            // getterのreturn Typeに応じて xml要素を生成する
            Class retType = method.getReturnType();

            if (retType.equals(long.class)) {
                // <key>prop</key><integer>val</integer>
                keyLong(prop, ((Long)value).longValue(), writer);

            } else if (retType.equals(String.class)) {
                // <key>prop</key><string>val</string>
                keyString(prop, (String)value, writer);

            } else if (retType.equals(Date.class)) {
                // <key>prop</key><date>val</date>
                keyDate(prop, (Date)value, writer);

            } else if (retType.getName().equals(ARRAY_BYTE)) {
                // byte[]
                // <key>prop</key><data>(byte[])val</data>
                keyData(prop, (byte[])value, writer);

            } else if (retType.equals(List.class)) {
                // <key>prop</key><array>
                keyArray(prop, writer);
                
                // Listをiterate
                List list = (List)value;
                for (Object obj : list) {
                    //----------------------------------------------------
                    // Listの要素はStringまたはInfoModelの制限
                    if (obj instanceof String) {
                        string((String)obj, writer);
                    } else {
                        IInfoModelConverter converter = createConverter((IInfoModel)obj);
                        reflectConvert(converter, writer);
                    }
                    //----------------------------------------------------
                }
                // </array>
                arrayEnd(writer);

            } else if (value instanceof IInfoModel) {
                // 再起する
                IInfoModelConverter ac = createConverter((IInfoModel)value);
                reflectConvert(ac, writer);

            } else if (retType.equals(int.class)) {
                // <key>prop</key><integer>val</integer>
                keyInteger(prop, ((Integer)value).intValue(), writer);

            } else if (retType.equals(float.class)) {
                // <key>prop</key><real>val</real>
                keyFloat(prop, ((Float)value).floatValue(), writer);

            } else if (retType.equals(double.class)) {
                // <key>prop</key><real>val</real>
                keyDouble(prop, ((Double)value).doubleValue(), writer);

            } else if (retType.equals(Boolean.TYPE)) {
                // <key>prop</key><true/> or <false/>
                keyBoolean(prop, (Boolean)value, writer);
            }
        }

        // </dict>
        dictEnd(writer);
    }

    /**
     * InfoModelのConverterを生成する。
     * @param model InfoModel
     * @return 対応するConverter
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException 
     */
    private static IInfoModelConverter createConverter(IInfoModel model)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        // open.dolphin.infomodel.PatientModel ->
        // open.dolphin.converter.PatientModelConverter
        String className = model.getClass().getName();
        int index = className.lastIndexOf(".");
        className = className.substring(index+1);

        StringBuilder sb = new StringBuilder();
        sb.append(CONVERTER_PACKAGE);
        sb.append(className);
        sb.append(CONVERTER_EXT);
        String converterName = sb.toString();

        IInfoModelConverter ret = (IInfoModelConverter)Class.forName(converterName).newInstance();

        // ConverterへModelをセットする
        ret.setModel(model);

        return ret;
    }

    /**
     * plistのトップ要素が<array>の場合のコンバート。
     * @param list コンバートする list
     * @return <array>......</array>
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws MessagingException 
     */
    private static String convertAsList(List list)
            throws IOException, ClassNotFoundException, InstantiationException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, MessagingException {
        
        StringWriter writer = new StringWriter();
        // <array>
        arrayStart(writer);

        for (Object o : list) {

            if (o instanceof IInfoModel) {

                dictStart(writer);
                IInfoModelConverter converter = createConverter((IInfoModel)o);
                reflectConvert(converter, writer);
                dictEnd(writer);

            } else if (o instanceof List) {

                arrayStart(writer);

                List l = (List)o;
                for (Object obj : l) {

                    dictStart(writer);
                    IInfoModelConverter converter = createConverter((IInfoModel)obj);
                    reflectConvert(converter, writer);
                    dictEnd(writer);
                }

                arrayEnd(writer);
            }
        }

        // </array>
        arrayEnd(writer);

        String ret = writer.toString();
        closeWriter(writer);

        return ret;
    }
        
    /**
     * plistのトップ要素が<dict>の場合のコンバート。
     * @param target コンバートする InfoModel
     * @return <dict>......</dict>
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws MessagingException 
     */
    private static String convertAsRoot(IInfoModel target)
            throws IOException, ClassNotFoundException, InstantiationException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, MessagingException {
        
        StringWriter writer = new StringWriter();

        dictStart(writer);

        IInfoModelConverter converter = createConverter(target);
        reflectConvert(converter, writer);

        dictEnd(writer);
        String ret = writer.toString();
        closeWriter(writer);

        return ret;
    }

    /**
     * 引数のオブジェクトをplistへコンバートする。
     * @param obj コンバートするオブジェクト
     * @return plist
     * @throws ConverterException 
     */
    public String convert(Object obj) throws ConverterException {

        try {
            // <?xml version="1.0" encoding="UTF-8"?>
            // <plist version="1.0">...........</plist>
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
