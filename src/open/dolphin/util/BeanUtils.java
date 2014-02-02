package open.dolphin.util;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class BeanUtils {
	
    public static String beanToXml(Object bean)  {
    	
		String ret = null;
		try {
			ret = new String(getXMLBytes(bean), "UTF-8");
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
		return ret;
	}
	
    public static Object xmlToBean(String beanXml) {
		
		Object ret = null;
		
		// XMLDecode
		try {
			byte[] bytes = beanXml.getBytes("UTF-8");
			
			XMLDecoder d = new XMLDecoder(
				new BufferedInputStream(
					new ByteArrayInputStream(bytes)));

			ret = d.readObject();
		} catch (Exception e) {
			ret = null;
			e.printStackTrace();
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

}
