package ru.CryptoProvider.Utils;

import java.io.*;
import java.security.*;


public class RSAWithKeyContainerUtils {

    public static byte[] signFileWithPrivateKey(byte[] fileBytes, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(fileBytes);
        return signature.sign();
    }
    public static boolean verifyFileSignature(byte[] fileBytes, byte[] signatureBytes, PublicKey publicKey) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(fileBytes);
        return signature.verify(signatureBytes);
    }

    private static byte[] readFile(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] fileBytes = new byte[(int) file.length()];
            fis.read(fileBytes);
            return fileBytes;
        }
    }

}
