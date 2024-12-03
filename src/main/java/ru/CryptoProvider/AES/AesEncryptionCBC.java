package ru.CryptoProvider.AES;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.security.SecureRandom;

public class AesEncryptionCBC {
    public static long encryptFileCBC(File inputFile, File outputFile, String key) throws Exception {
        byte[] fileData = Files.readAllBytes(inputFile.toPath());

        // Генерация вектора инициализации (IV)
        byte[] iv = new byte[16];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

        long startTime = System.nanoTime();
        byte[] encryptedData = cipher.doFinal(fileData);
        long endTime = System.nanoTime();

        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            // Сохраняем IV перед зашифрованными данными
            fos.write(iv);
            fos.write(encryptedData);
        }

        return (endTime - startTime) / 1_000_000;
    }

}
