package ru.CryptoProvider.RSA;

import javax.crypto.Cipher;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

public class RsaDecryption {
    public static long decryptFileRSA(File inputFile, File outputFile, PrivateKey privateKey) throws Exception {
        byte[] fileData = Files.readAllBytes(inputFile.toPath());
        int blockSize = 256; 

        
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        long startTime = System.nanoTime();
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            int offset = 0;
            while (offset < fileData.length) {
                int chunkSize = Math.min(blockSize, fileData.length - offset);
                byte[] chunk = Arrays.copyOfRange(fileData, offset, offset + chunkSize);

                
                byte[] decryptedChunk = cipher.doFinal(chunk);

                
                fos.write(decryptedChunk);

                offset += chunkSize;
            }
        }
        long endTime = System.nanoTime();

        
        return (endTime - startTime) / 1_000_000;
    }
    public static PrivateKey getPrivateKeyFromString(String keyString) throws Exception {
        
        String privateKeyPEM = keyString.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);

        return keyFactory.generatePrivate(keySpec);
    }

}
