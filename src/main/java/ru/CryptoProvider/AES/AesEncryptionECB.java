package ru.CryptoProvider.AES;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;

public class AesEncryptionECB {

    public static long encryptFileECB(File inputFile, File outputFile, String key) throws Exception {
        
        byte[] fileData = Files.readAllBytes(inputFile.toPath());

        
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        
        long startTime = System.nanoTime();
        byte[] encryptedData = cipher.doFinal(fileData);
        long endTime = System.nanoTime();

        
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            fos.write(encryptedData);
        }

        
        return (endTime - startTime) / 1_000_000;
    }
}
