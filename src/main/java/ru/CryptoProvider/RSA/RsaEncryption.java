package ru.CryptoProvider.RSA;


import javax.crypto.Cipher;
import java.io.*;
import java.nio.file.Files;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;

public class RsaEncryption {

    public static long encryptFileRSA(File inputFile, File outputFile, PublicKey publicKey) throws Exception {
        byte[] fileData = Files.readAllBytes(inputFile.toPath());
        int blockSize = 245; 

        
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        long startTime = System.nanoTime();
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            int offset = 0;
            while (offset < fileData.length) {
                int chunkSize = Math.min(blockSize, fileData.length - offset);
                byte[] chunk = Arrays.copyOfRange(fileData, offset, offset + chunkSize);

                
                byte[] encryptedChunk = cipher.doFinal(chunk);

                
                fos.write(encryptedChunk);

                offset += chunkSize;
            }
        }
        long endTime = System.nanoTime();

        
        return (endTime - startTime) / 1_000_000;
    }

    public static KeyPair generateRSAKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);  
        return keyPairGenerator.generateKeyPair();  
    }
}

