package ru.CryptoProvider.Utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
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
        SecretKeySpec secretKey = getKeyFromPassword(password);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] encryptedData = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedData);
    }
    public static String decryptKeyWithPassword(String encryptedKey, String password) throws Exception {
        SecretKeySpec secretKey = getKeyFromPassword(password);

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        byte[] decodedKey = Base64.getDecoder().decode(encryptedKey);
        byte[] decryptedKey = cipher.doFinal(decodedKey);

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

    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    private static SecretKeySpec generateAESKeyFromPassword(String password) {
        byte[] keyBytes = new byte[32];
        System.arraycopy(password.getBytes(), 0, keyBytes, 0, Math.min(password.length(), 32));
        return new SecretKeySpec(keyBytes, "AES");
    }

    public static void savePublicKey(PublicKey publicKey, File file) throws IOException {
        byte[] keyBytes = publicKey.getEncoded();
        String encodedKey = Base64.getEncoder().encodeToString(keyBytes);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("-----BEGIN PUBLIC KEY-----\n");
            writer.write(encodedKey);
            writer.write("\n-----END PUBLIC KEY-----\n");
        }
    }

    public static void saveEncryptedPrivateKey(PrivateKey privateKey, File file, String password) throws Exception {
        String base64Key = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        String encryptedKey = encryptWithPassword(base64Key, password);

        Files.write(file.toPath(), encryptedKey.getBytes(StandardCharsets.UTF_8));
    }

    public static byte[] signFileWithPrivateKey(File file, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);

        try (FileInputStream fis = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(fis)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = bis.read(buffer)) != -1) {
                signature.update(buffer, 0, len);
            }
        }

        return signature.sign();
    }


    public static PrivateKey loadEncryptedPrivateKey(File file, String password) throws Exception {
        String encryptedKey = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
//        System.out.println("Содержимое ключа: " + encryptedKey);

        String decryptedBase64Key = decryptKeyWithPassword(encryptedKey, password);

        byte[] keyBytes = Base64.getDecoder().decode(decryptedBase64Key);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
    }

    public static PublicKey loadPublicKey(File pemFile) throws Exception {
        String pemContent;
        try (BufferedReader reader = new BufferedReader(new FileReader(pemFile))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("PUBLIC KEY")) continue;
                sb.append(line.trim());
            }
            pemContent = sb.toString();
        }

        byte[] keyBytes = Base64.getDecoder().decode(pemContent);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }




}
