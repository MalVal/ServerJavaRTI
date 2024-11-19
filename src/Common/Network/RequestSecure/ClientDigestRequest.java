package Common.Network.RequestSecure;

import Common.Network.Request.Request;
import Common.Network.ResponseSecure.ServerSalt;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class ClientDigestRequest implements Request {

    private final Integer id;
    private final byte[] digest;

    public ClientDigestRequest(Integer id, ServerSalt salt) throws NoSuchAlgorithmException, NoSuchProviderException, IOException {
        this.id = id;
        MessageDigest md = MessageDigest.getInstance("SHA-1", "BC");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(byteArrayOutputStream);
        dos.writeInt(this.id);
        dos.writeLong(salt.getTime());
        dos.writeDouble(salt.getRandom());
        md.update(byteArrayOutputStream.toByteArray());
        this.digest = md.digest();
    }

    public boolean VerifyDigest(Integer id, ServerSalt salt) throws NoSuchAlgorithmException, NoSuchProviderException, IOException {
        MessageDigest md = MessageDigest.getInstance("SHA-1","BC");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(byteArrayOutputStream);
        dos.writeInt(id);
        dos.writeLong(salt.getTime());
        dos.writeDouble(salt.getRandom());
        md.update(byteArrayOutputStream.toByteArray());
        byte[] localDigest = md.digest();
        return MessageDigest.isEqual(this.digest, localDigest);
    }

    public Integer getId() {
        return id;
    }
}
