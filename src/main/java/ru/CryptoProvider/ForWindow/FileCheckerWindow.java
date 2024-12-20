package ru.CryptoProvider.ForWindow;

import javax.swing.*;
import java.io.*;
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

    
    private void saveHashToFile(File file, String hash, File hashFile) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(hashFile, true))) {
            writer.write(file.getName() + " : " + hash);
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

    
    public void checkFileIntegrity() {
        File selectedFile = selectFile();
        if (selectedFile == null) {
            return; 
        }

        File hashFile = new File("hashes.txt");

        try {
            String currentHash = calculateFileHash(selectedFile);
            String storedHash = getHashFromFile(selectedFile, hashFile);

            if (storedHash == null) {
                
                saveHashToFile(selectedFile, currentHash, hashFile);
                JOptionPane.showMessageDialog(null, "Хеш-сумма для файла " + selectedFile.getName() + " зарегистрирована в криптопровайдере.", "Информация", JOptionPane.INFORMATION_MESSAGE);
            } else if (!currentHash.equals(storedHash)) {
                
                JOptionPane.showMessageDialog(null, "Целостность файла нарушена!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            } else {
                
                JOptionPane.showMessageDialog(null, "Целостность файла не нарушена.", "Информация", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NoSuchAlgorithmException | IOException e) {
            JOptionPane.showMessageDialog(null, "Ошибка при вычислении хеш-суммы файла: " + e.getMessage(),
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }
}
