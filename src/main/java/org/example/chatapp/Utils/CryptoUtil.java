package org.example.chatapp.Utils;

import org.springframework.context.annotation.Configuration;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
public class CryptoUtil {

    private static final String ALGORITHM = "AES";
    private static final String SECRET_KEY = System.getenv("CHAT_SECRET_KEY");

    static {
        if (SECRET_KEY == null || SECRET_KEY.length() < 16) {
            throw new RuntimeException(
                    "CHAT_SECRET_KEY missing or too short (min 16 chars)"
            );
        }
    }

    public static String encrypt(String plainText) {
        try {
            SecretKeySpec key =
                    new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] encrypted = cipher.doFinal(plainText.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);

        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    public static String decrypt(String encryptedText) {
        try {
            SecretKeySpec key =
                    new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] decoded = Base64.getDecoder().decode(encryptedText);
            return new String(cipher.doFinal(decoded));

        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }
}
