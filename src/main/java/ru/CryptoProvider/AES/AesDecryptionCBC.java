package ru.CryptoProvider.AES;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.Arrays;

public class AesDecryptionCBC {
    public static long decryptFileCBC(File encryptedFile, File saveFile, String key) throws Exception {
        byte[] fileData = Files.readAllBytes(encryptedFile.toPath());

        byte[] iv = Arrays.copyOfRange(fileData, 0, 16);
        byte[] encryptedData = Arrays.copyOfRange(fileData, 16, fileData.length);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

        long startTime = System.nanoTime();
        byte[] decryptedData = cipher.doFinal(encryptedData);
        long endTime = System.nanoTime();

        try (FileOutputStream fos = new FileOutputStream(saveFile)) {
            fos.write(decryptedData);
        }

        return (endTime - startTime) / 1_000_000;
    }

}
