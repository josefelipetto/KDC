package Utils;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class AES
{

    public static byte[] cifra(String texto, String chave) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException
    {
        return cifra(texto.getBytes(), chave);
    }

    public static byte[] cifra(byte[] texto, String chave) throws IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException
    {
        Key key = new SecretKeySpec(chave.getBytes(StandardCharsets.UTF_8), "AES");

        Cipher cifrador = Cipher.getInstance("AES");

        cifrador.init(Cipher.ENCRYPT_MODE, key);

        return cifrador.doFinal(texto);
    }

    public static byte[] decifra(byte[] texto, String chave) throws IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException
    {
        Key key = new SecretKeySpec(chave.getBytes(StandardCharsets.UTF_8), "AES");

        Cipher decifrador = Cipher.getInstance("AES");

        decifrador.init(Cipher.DECRYPT_MODE, key);

        return decifrador.doFinal(texto);
    }
}