package open.dolphin.util;

import java.beans.Encoder;
import java.beans.Expression;
import java.beans.PersistenceDelegate;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 *
 * @author Kazushi Minagawa.
 */
public class BeanUtils {

    private static final String UTF8 = "UTF-8";
    
    public static String beanToXml(Object bean)  {
        
        String ret = null;
        try {
            ret = new String(getXMLBytes(bean), UTF8);
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace(System.err);
        }
        return ret;
    }
    
    public static Object xmlToBean(String beanXml) {
        
        Object ret = null;
        
        // XMLDecode
        try {
            byte[] bytes = beanXml.getBytes(UTF8);
            
            XMLDecoder d = new XMLDecoder(
                    new BufferedInputStream(
                    new ByteArrayInputStream(bytes)));
            
            ret = d.readObject();
        } catch (Exception e) {
            ret = null;
            e.printStackTrace(System.err);
        }
        
        return ret;
    }
    
    public static byte[] getXMLBytes(Object bean)  {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        XMLEncoder e = new XMLEncoder(new BufferedOutputStream(bo));
        e.writeObject(bean);
        e.close();
        return bo.toByteArray();
    }
    
    public static byte[] xmlEncode(Object bean)  {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        XMLEncoder e = new XMLEncoder(new BufferedOutputStream(bo));

        //masuda^   java.sql.Dateとjava.sql.TimestampがxmlEncodeで失敗する
        DatePersistenceDelegate dpd = new DatePersistenceDelegate();
        e.setPersistenceDelegate(java.sql.Date.class, dpd);
        TimestampPersistenceDelegate tpd = new TimestampPersistenceDelegate();
        e.setPersistenceDelegate(java.sql.Timestamp.class, tpd);
        //masuda$

        e.writeObject(bean);
        e.close();
        return bo.toByteArray();
    }
    
    public static Object xmlDecode(byte[] bytes)  {
        
        XMLDecoder d = new XMLDecoder(
                new BufferedInputStream(
                new ByteArrayInputStream(bytes)));
        return d.readObject();
    }

    //masuda^   http://forums.sun.com/thread.jspa?threadID=427879
   private static class DatePersistenceDelegate extends PersistenceDelegate {

       @Override
       protected Expression instantiate(Object oldInstance, Encoder out) {
           java.sql.Date date = (java.sql.Date) oldInstance;
           long time = Long.valueOf(date.getTime());
           return new Expression(date, date.getClass(), "new", new Object[]{time});
       }
   }

   private static class TimestampPersistenceDelegate extends PersistenceDelegate {

       @Override
       protected Expression instantiate(Object oldInstance, Encoder out) {
           java.sql.Timestamp date = (java.sql.Timestamp) oldInstance;
           long time = Long.valueOf(date.getTime());
           return new Expression(date, date.getClass(), "new", new Object[]{time});
       }
   }
//masuda$
}
