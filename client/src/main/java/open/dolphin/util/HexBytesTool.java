
package open.dolphin.util;

/**
 * Hex文字列とbyte[]を相互変換する
 *
 * @author masuda, Masuda Naika
 */

public class HexBytesTool {
    
    // Hex文字列をbyte[]に変換
    public static byte[] hexToBytes(String hexString) {
        
        int len = hexString.length() / 2;
        byte[] bytes = new byte[len];
        for (int i = 0; i < len; ++i) {
            String hex = hexString.substring(i * 2, i * 2 + 2);
            int data = Integer.parseInt(hex, 16);
            bytes[i] = (byte) data;
        }
        return bytes;
    }

    // byte[]を２桁のHex文字列に変換
    public static String bytesToHex(byte[] bytes){

        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
