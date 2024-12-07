package ru.CryptoProvider.Utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class KeyGeneratorUtils {
    public static String generateKeyFromPassphrase(String passphrase) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(passphrase.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash).substring(0, 32);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Ошибка при генерации ключа из парольной фразы", e);
        }
    }
    public static String encryptWithPassword(String data, String password) throws Exception {
        // Генерация ключа на основе пароля
        SecretKeySpec secretKey = getKeyFromPassword(password);

        // Инициализация шифрования
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] encryptedData = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedData);
    }
    public static String decryptKeyWithPassword(String encryptedKey, String password) throws Exception {
        // Генерация ключа на основе пароля
        SecretKeySpec secretKey = getKeyFromPassword(password);

        // Инициализация дешифрования
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        byte[] decodedKey = Base64.getDecoder().decode(encryptedKey);
        byte[] decryptedKey = cipher.doFinal(decodedKey);

        // Преобразуем расшифрованный ключ в строку
        return new String(decryptedKey, StandardCharsets.UTF_8);
    }
    private static SecretKeySpec getKeyFromPassword(String password) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] key = password.getBytes(StandardCharsets.UTF_8);
        key = sha.digest(key);
        return new SecretKeySpec(Arrays.copyOf(key, 32), "AES");
    }
    public static boolean verifyKey(String encryptedData, String password) {
       if(encryptedData.equals(password)){
           return true;
       }
           return false;
    }


}
