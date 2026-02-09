package org.example.chatapp.Utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

public class CryptoUtil {

    private static final String ALGORITHM = "AES";
    private static final String SECRET = System.getenv("CHAT_SECRET_KEY");

    static {
        if (SECRET == null || SECRET.isBlank()) {
            throw new RuntimeException("CHAT_SECRET_KEY is missing");
        }
    }

    private static SecretKeySpec getKey() {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] key = sha.digest(SECRET.getBytes(StandardCharsets.UTF_8));
            return new SecretKeySpec(Arrays.copyOf(key, 32), ALGORITHM); // AES-256
        } catch (Exception e) {
            throw new RuntimeException("Invalid encryption key", e);
        }
    }

    public static String encrypt(String plainText) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, getKey());
            return Base64.getEncoder().encodeToString(
                    cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8))
            );
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    public static String decrypt(String encryptedText) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, getKey());
            return new String(
                    cipher.doFinal(Base64.getDecoder().decode(encryptedText)),
                    StandardCharsets.UTF_8
            );
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }
}
