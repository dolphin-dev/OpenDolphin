package open.dolphin.rest;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author kazushi
 */
public class IOSHelper {
    
    // IOS5 JSON DATE
    //private static final String IOS_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String IOS_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final String IOS_DATE_FORMAT_OLD = "yyyy-MM-dd HH:mm:ss";
    
    
    public static byte[] toXMLBytes(Object bean)  {
        if (bean!=null) {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            XMLEncoder e = new XMLEncoder(new BufferedOutputStream(bo));
            e.writeObject(bean);
            e.close();
            return bo.toByteArray();
        }
        return null;
    }
    
    public static Object xmlDecode(byte[] bytes)  {

        XMLDecoder d = new XMLDecoder(
                new BufferedInputStream(
                new ByteArrayInputStream(bytes)));

        return d.readObject();
    }
    
    public static Date toDate(String dateStr) {
        if (dateStr==null) {
            return null;
        }
        Date ret = null;
        try {
            ret = new SimpleDateFormat(IOS_DATE_FORMAT).parse(dateStr);
        } catch (Exception e) {
            try {
                ret = new SimpleDateFormat(IOS_DATE_FORMAT_OLD).parse(dateStr);
            } catch (ParseException ex) {
            }
        }
        return ret;
    }
    
    public static String toDateStr(Date d) {
        if (d==null) {
            return null;
        }
        String ret = null;
        try {
            ret = new SimpleDateFormat(IOS_DATE_FORMAT).format(d);
        } catch (Exception e) {
            ret = new SimpleDateFormat(IOS_DATE_FORMAT_OLD).format(d);
        }
        return ret;
    }
    
    public static boolean toBool(String bStr) {
        if (bStr!=null) {
            return Boolean.parseBoolean(bStr);
        }
        return false;
    }
    
    public static String toBoolStr(boolean b) {
        return String.valueOf(b);
    }
    
    public static void printProperty(String key, Object value) {
        String valStr = value!=null ? value.toString() : "NULL";
        StringBuilder sb = new StringBuilder();
        sb.append(key).append("=").append(valStr);
        System.err.println(sb.toString());
    }
}
