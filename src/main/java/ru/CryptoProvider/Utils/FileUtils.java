package ru.CryptoProvider.Utils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

public class FileUtils {

    public static File selectFile(String dialogTitle) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(dialogTitle);
        int result = chooser.showOpenDialog(null);
        return result == JFileChooser.APPROVE_OPTION ? chooser.getSelectedFile() : null;
    }

    public static File selectSaveLocation(String defaultName) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Сохранить файл");
        chooser.setSelectedFile(new File(defaultName));
        int result = chooser.showSaveDialog(null);
        return result == JFileChooser.APPROVE_OPTION ? chooser.getSelectedFile() : null;
    }

    public static File[] selectMultipleFiles(String dialogTitle) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(dialogTitle);
        chooser.setMultiSelectionEnabled(true);
        int result = chooser.showOpenDialog(null);
        return result == JFileChooser.APPROVE_OPTION ? chooser.getSelectedFiles() : null;
    }
    public static File selectFolder(String dialogTitle) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(dialogTitle);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = chooser.showOpenDialog(null);
        return result == JFileChooser.APPROVE_OPTION ? chooser.getSelectedFile() : null;
    }
    public static String readFileToString(File file, Charset charset) throws IOException {
        if (file == null || !file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("Некорректный файл для чтения: " + file);
        }
        return Files.readString(file.toPath(), charset);
    }
    public static File getUniqueFile(File folder, String baseName, String originalExtension) {
        String extension = originalExtension;
        File file = new File(folder, baseName + extension);
        int counter = 1;

        while (file.exists()) {
            file = new File(folder, baseName + "(" + counter + ")" + extension);
            counter++;
        }
        return file;
    }

    public static File getUniqueFileENC(File folder, String baseName, String originalExtension) {
        String extension = originalExtension + ".enc";
        File file = new File(folder, baseName + extension);
        int counter = 1;

        while (file.exists()) {
            file = new File(folder, baseName + "(" + counter + ")" + extension);
            counter++;
        }
        return file;
    }



}

