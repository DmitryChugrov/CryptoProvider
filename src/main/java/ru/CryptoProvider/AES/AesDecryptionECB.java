package ru.CryptoProvider.AES;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;

public class AesDecryptionECB {
    public static long decryptFileECB(File encryptedFile, File saveFile, String key) throws Exception {
        byte[] encryptedData = Files.readAllBytes(encryptedFile.toPath());

        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        long start = System.nanoTime();
        byte[] decryptedData = cipher.doFinal(encryptedData);
        long end = System.nanoTime();

        try (FileOutputStream fos = new FileOutputStream(saveFile)) {
            fos.write(decryptedData);
        }
        return (end - start) / 1_000_000;
    }

}

