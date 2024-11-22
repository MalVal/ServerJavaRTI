package Common.Cryptology;

import javax.crypto.*;
import java.security.PrivateKey;
import java.security.PublicKey;

public class Cryptology {
    public static byte[] CryptTripleSymDES(SecretKey key, byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding","BC");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    public static byte[] DecryptTripleSymDES(SecretKey cle, byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding","BC");
        cipher.init(Cipher.DECRYPT_MODE, cle);
        return cipher.doFinal(data);
    }

    public static byte[] CryptASymRSA(PublicKey cle, byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding","BC");
        cipher.init(Cipher.ENCRYPT_MODE, cle);
        return cipher.doFinal(data);
    }

    public static byte[] DecryptASymRSA(PrivateKey cle, byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding","BC");
        cipher.init(Cipher.DECRYPT_MODE, cle);
        return cipher.doFinal(data);
    }
}