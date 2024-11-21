package Common.Cryptology;

import org.bouncycastle.asn1.x500.X500Name;

import javax.crypto.*;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Date;

public class Cryptology {
    public static byte[] CryptSymDES(SecretKey key, byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding","BC");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    public static byte[] DecryptSymDES(SecretKey cle, byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding","BC");
        cipher.init(Cipher.DECRYPT_MODE, cle);
        return cipher.doFinal(data);
    }

    public static byte[] CryptASymRSA(PublicKey cle, byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding","BC");
        cipher.init(Cipher.ENCRYPT_MODE, cle);
        return cipher.doFinal(data);
    }

    public static byte[] CryptASymRSA(PrivateKey cle, byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding","BC");
        cipher.init(Cipher.ENCRYPT_MODE, cle);
        return cipher.doFinal(data);
    }

    public static byte[] DecryptASymRSA(PrivateKey cle, byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding","BC");
        cipher.init(Cipher.DECRYPT_MODE, cle);
        return cipher.doFinal(data);
    }

    public static byte[] DecryptASymRSA(PublicKey cle, byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding","BC");
        cipher.init(Cipher.DECRYPT_MODE, cle);
        return cipher.doFinal(data);
    }
}