package ru.CryptoProvider.ForWindow;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileCheckerWindow {
    public File selectFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Выберите файл для проверки");
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }
    private String calculatePathHash(File file) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] pathBytes = file.getAbsolutePath().getBytes(StandardCharsets.UTF_8);
        byte[] hashBytes = digest.digest(pathBytes);
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }



    private String calculateFileHash(File file) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] byteArray = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(byteArray)) != -1) {
                digest.update(byteArray, 0, bytesRead);
            }
        }
        byte[] hashBytes = digest.digest();
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }


    private void saveHashToFile(File file, String pathHash, String contentHash, File hashFile) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(hashFile, true))) {
            writer.write(pathHash + " : " + contentHash);
            writer.newLine();
        }
    }



    private String getHashFromFile(File file, File hashFile) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(hashFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(file.getName())) {
                    return line.split(" : ")[1];
                }
            }
        }
        return null; 
    }
    private String getHashFromFile(String pathHash, File hashFile) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(hashFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(pathHash)) {
                    return line;
                }
            }
        }
        return null;
    }
    private File prepareHashFile() throws IOException {
        File hashFile = new File("hashes.txt"); // Копируем ресурс в рабочую директорию
        if (!hashFile.exists()) {
            try (InputStream resourceStream = getClass().getResourceAsStream("/hashes.txt")) {
                if (resourceStream == null) {
                    throw new FileNotFoundException("Файл hashes.txt не найден в ресурсах.");
                }
                Files.copy(resourceStream, hashFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
        return hashFile;
    }



    public void checkFileIntegrity() {
        File selectedFile = selectFile();
        if (selectedFile == null) {
            return;
        }

        try {
            File hashFile = prepareHashFile(); // Готовим файл хешей из ресурсов
            String pathHash = calculatePathHash(selectedFile);
            String contentHash = calculateFileHash(selectedFile);
            String storedData = getHashFromFile(pathHash, hashFile);

            if (storedData == null) {
                saveHashToFile(selectedFile, pathHash, contentHash, hashFile);
                JOptionPane.showMessageDialog(null, "Хеши пути и содержимого файла зарегистрированы.", "Информация", JOptionPane.INFORMATION_MESSAGE);
            } else {
                String[] storedHashes = storedData.split(" : ");
                if (!storedHashes[1].equals(contentHash)) {
                    JOptionPane.showMessageDialog(null, "Целостность содержимого файла нарушена!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Целостность файла не нарушена.", "Информация", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (NoSuchAlgorithmException | IOException e) {
            JOptionPane.showMessageDialog(null, "Ошибка: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

}
