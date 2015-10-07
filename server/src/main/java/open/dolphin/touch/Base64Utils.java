package open.dolphin.touch;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.mail.internet.MimeUtility;

/**
 *
 * @author kazushi Minagawa, Digital Globe, Inc.
 */
public class Base64Utils {

    public static byte[] encode(byte[] b) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStream b64os = MimeUtility.encode(baos, "base64");
        b64os.write(b);
        b64os.close();
        return baos.toByteArray();
     }

     public static byte[] decode(byte[] b) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        InputStream b64is = MimeUtility.decode(bais, "base64");
        byte[] tmp = new byte[b.length];
        int n = b64is.read(tmp);
        byte[] res = new byte[n];
        System.arraycopy(tmp, 0, res, 0, n);
        return res;
     }
}
