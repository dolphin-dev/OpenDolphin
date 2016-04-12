package open.dolphin.adm20.mbean;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import net.oauth.jsontoken.JsonToken;
import net.oauth.jsontoken.crypto.RsaSHA256Signer;
import org.joda.time.Instant;

/**
 *
 * @author kazushi minagawa
 */
@Singleton
@Startup
public class IdentityService {
    
    private static final long TWO_MINUTES_IN_MILLISECONDS = 1000L * 60L * 2L;
    
    private LayerConfig layerConfig;
    
    @PostConstruct
    public void init() {
        layerConfig = new LayerConfig();
    }

    @PreDestroy
    public void stop() {
    }
    
    public String getIdentityToken(String nonce, String userId)  {

        try {
            final Calendar cal = Calendar.getInstance();
            final RsaSHA256Signer signer = new RsaSHA256Signer(null, null, getPrivateKey());
            final JsonToken token = new JsonToken(signer);
            final com.google.gson.JsonObject header = token.getHeader();

            header.addProperty("typ", "JWT");
            header.addProperty("alg", "RS256");
            header.addProperty("cty", "layer-eit;v=1");
            header.addProperty("kid", layerConfig.getLayerKeyId());

            token.setParam("iss", layerConfig.getProviderId());
            token.setParam("prn", userId);
            token.setIssuedAt(new Instant(cal.getTimeInMillis()));
            token.setExpiration(new Instant(cal.getTimeInMillis() + TWO_MINUTES_IN_MILLISECONDS));
            token.setParam("nce", nonce);

            String ret = token.serializeAndSign();
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "token={0}", ret);
            return ret;
            
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException | InvalidKeyException | SignatureException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    private byte[] readPrivateKeyFromDisk(final String path) throws IOException {
        final File privateKeyFile = new File(path);
        final FileInputStream fileInputStream = new FileInputStream(privateKeyFile);
        final DataInputStream dis = new DataInputStream(fileInputStream);
        final byte[] privateBytes = new byte[(int) privateKeyFile.length()];
        try {
            dis.readFully(privateBytes);
        } catch (IOException ioe) {
            /** No-op **/
        } finally {
            fileInputStream.close();
        }
        return privateBytes;
    }
    
    private RSAPrivateKey getPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException,
            IOException {

        final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        final byte[] encodedKey = readPrivateKeyFromDisk(layerConfig.getRsaKeyPath());
        final EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedKey);
        final PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
        return (RSAPrivateKey) privateKey;
    }
}
