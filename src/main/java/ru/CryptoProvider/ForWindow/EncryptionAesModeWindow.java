package ru.CryptoProvider.ForWindow;

import ru.CryptoProvider.AES.AesEncryptionCBC;
import ru.CryptoProvider.AES.AesEncryptionECB;
import ru.CryptoProvider.Utils.FileUtils;
import ru.CryptoProvider.Utils.KeyGeneratorUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class EncryptionAesModeWindow extends JFrame {

    public EncryptionAesModeWindow() {
        
        setTitle("Выбор режима шифрования AES");
        setSize(300, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        
        JPanel panel = new JPanel();
        panel.setLayout(null);
        add(panel);

        
        JButton ecbButton = new JButton("ECB");
        ecbButton.setBounds(50, 50, 200, 40);
        panel.add(ecbButton);

        
        JButton cbcButton = new JButton("CBC");
        cbcButton.setBounds(50, 100, 200, 40);
        panel.add(cbcButton);

        
        JButton backButton = new JButton("Назад");
        backButton.setBounds(50, 150, 200, 40);
        panel.add(backButton);

        ecbButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(EncryptionAesModeWindow.this,
                        "Вы выбрали режим ECB. В конце шифрования запомните ключ!",
                        "Информация", JOptionPane.INFORMATION_MESSAGE);

                
                File[] inputFiles = FileUtils.selectMultipleFiles("Выберите файлы для шифрования");
                if (inputFiles == null || inputFiles.length == 0) {
                    JOptionPane.showMessageDialog(EncryptionAesModeWindow.this,
                            "Файлы не выбраны. Операция отменена.",
                            "Ошибка", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                    for (File file : inputFiles) {
                        if (file.length() > 20 * 1024 * 1024) { // Ограничение 20 МБ
                            JOptionPane.showMessageDialog(EncryptionAesModeWindow.this,
                                    "Файл " + file.getName() + " превышает размер 20 МБ.",
                                    "Ошибка", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                String firstFileName = inputFiles[0].getName();
                String keyFileName = firstFileName.contains(".")
                        ? firstFileName.substring(0, firstFileName.lastIndexOf('.')) + ".key"
                        : firstFileName + ".key";
                
                File saveFolder = FileUtils.selectFolder("Выберите папку для сохранения зашифрованных файлов");
                if (saveFolder == null) {
                    JOptionPane.showMessageDialog(EncryptionAesModeWindow.this,
                            "Папка не выбрана. Операция отменена.",
                            "Ошибка", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                
                MouseTrackerWindow trackerWindow = new MouseTrackerWindow();
                trackerWindow.setVisible(true);

                new Thread(() -> {
                    trackerWindow.waitForTrackingToComplete();
                    String keyHash = trackerWindow.getTrackerLogic().hashSumOfCoordinates();

                    
                    JDialog progressDialog = new JDialog(EncryptionAesModeWindow.this, "Шифрование файлов", true);
                    progressDialog.setLayout(new BorderLayout());
                    JProgressBar progressBar = new JProgressBar(0, inputFiles.length);
                    progressBar.setValue(0);
                    progressDialog.add(new JLabel("Шифрование выполняется..."), BorderLayout.NORTH);
                    progressDialog.add(progressBar, BorderLayout.CENTER);
                    progressDialog.setSize(300, 100);
                    progressDialog.setLocationRelativeTo(EncryptionAesModeWindow.this);

                    new Thread(() -> {
                        long totalEncryptionTime = 0;
                        final int[] successCount = {0};
                        List<String> failedFiles = new ArrayList<>();

                        for (int i = 0; i < inputFiles.length; i++) {
                            File inputFile = inputFiles[i];
                            File saveFile = new File(saveFolder, inputFile.getName() + ".enc");

                            try {
                                long encryptionTime = AesEncryptionECB.encryptFileECB(inputFile, saveFile, keyHash);
                                totalEncryptionTime += encryptionTime;
                                successCount[0]++;
                            } catch (Exception ex) {
                                failedFiles.add(inputFile.getName());
                            }

                            
                            final int progress = i + 1;
                            SwingUtilities.invokeLater(() -> progressBar.setValue(progress));
                        }
                        long totalTime = totalEncryptionTime;
                        
                        SwingUtilities.invokeLater(progressDialog::dispose);


                        SwingUtilities.invokeLater(() -> {
                            StringBuilder resultMessage = new StringBuilder();
                            resultMessage.append("<html>Шифрование завершено!<br>")
                                    .append("Успешно зашифровано файлов: ").append(successCount[0]).append("<br>")
                                    .append("Не удалось зашифровать: ").append(failedFiles.size()).append("<br>");

                            if (!failedFiles.isEmpty()) {
                                resultMessage.append("Список файлов с ошибками:<br>");
                                for (String fileName : failedFiles) {
                                    resultMessage.append("- ").append(fileName).append("<br>");
                                }
                            }

                            resultMessage.append("Общее время шифрования: ")
                                    .append(totalTime).append(" ms<br>")
                                    .append("Ключ: <b>").append(keyHash).append("</b></html>");

                            System.out.println(keyHash);

                            JPanel panel = new JPanel(new BorderLayout());
                            JLabel resultLabel = new JLabel(resultMessage.toString());
                            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                            JButton copyButton = new JButton("Копировать ключ");

                            copyButton.addActionListener(copyEvent -> {
                                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                                        new StringSelection(keyHash), null);
                                JOptionPane.showMessageDialog(EncryptionAesModeWindow.this,
                                        "Ключ скопирован в буфер обмена!",
                                        "Успех", JOptionPane.INFORMATION_MESSAGE);
                            });

                            JButton saveKeyButton = new JButton("Сохранить ключ в криптоконтейнер");
                            saveKeyButton.addActionListener(saveEvent -> {

                                File containerFolder = FileUtils.selectFolder("Выберите криптоконтейнер для сохранения ключа");
                                if (containerFolder == null || !new File(containerFolder, "pass.key").exists()) {
                                    JOptionPane.showMessageDialog(EncryptionAesModeWindow.this,
                                            "Выбрана некорректная папка или криптоконтейнер не содержит файл pass.key.",
                                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                                    return;
                                }

                                JPanel passwordPanel = new JPanel(new BorderLayout(5, 5));
                                JPasswordField passwordField = new JPasswordField(20);
                                JCheckBox showPasswordCheckbox = new JCheckBox("Показать пароль");

                                showPasswordCheckbox.addActionListener(ex -> {
                                    if (showPasswordCheckbox.isSelected()) {
                                        passwordField.setEchoChar((char) 0);
                                    } else {
                                        passwordField.setEchoChar('•');
                                    }
                                });

                                passwordPanel.add(new JLabel("Введите пароль криптоконтейнера:"), BorderLayout.NORTH);
                                passwordPanel.add(passwordField, BorderLayout.CENTER);
                                passwordPanel.add(showPasswordCheckbox, BorderLayout.SOUTH);

                                int result = JOptionPane.showConfirmDialog(
                                        EncryptionAesModeWindow.this,
                                        passwordPanel,
                                        "Авторизация в криптоконтейнере",
                                        JOptionPane.OK_CANCEL_OPTION,
                                        JOptionPane.PLAIN_MESSAGE
                                );

                                if (result != JOptionPane.OK_OPTION) {
                                    JOptionPane.showMessageDialog(EncryptionAesModeWindow.this,
                                            "Операция отменена.",
                                            "Информация", JOptionPane.INFORMATION_MESSAGE);
                                    return;
                                }

                                char[] passwordChars = passwordField.getPassword();
                                if (passwordChars == null || passwordChars.length == 0) {
                                    JOptionPane.showMessageDialog(EncryptionAesModeWindow.this,
                                            "Пароль контейнера не может быть пустым.",
                                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                                    return;
                                }

                                String containerPassphrase = new String(passwordChars);

                                File keyFile = new File(containerFolder, "pass.key");
                                String containerKey;
                                try {
                                    containerKey = FileUtils.readFileToString(keyFile, StandardCharsets.UTF_8);
                                    containerPassphrase = KeyGeneratorUtils.generateKeyFromPassphrase(containerPassphrase);
                                    System.out.println(containerPassphrase);
                                    System.out.println(containerKey);
                                    if (!KeyGeneratorUtils.verifyKey(containerPassphrase, containerKey)) {
                                        JOptionPane.showMessageDialog(EncryptionAesModeWindow.this,
                                                "Неверный пароль для криптоконтейнера.",
                                                "Ошибка", JOptionPane.ERROR_MESSAGE);
                                        return;
                                    }
                                } catch (IOException ioEx) {
                                    JOptionPane.showMessageDialog(EncryptionAesModeWindow.this,
                                            "Ошибка чтения файла pass.key: " + ioEx.getMessage(),
                                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                                    return;
                                }

                                String encryptedKey;
                                try {
                                    encryptedKey = KeyGeneratorUtils.encryptWithPassword(keyHash, containerPassphrase);
                                    System.out.println(encryptedKey);
                                } catch (Exception ex) {
                                    JOptionPane.showMessageDialog(EncryptionAesModeWindow.this,
                                            "Ошибка шифрования ключа: " + ex.getMessage(),
                                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                                    return;
                                }

                                File encryptedKeyFile = new File(containerFolder, keyFileName);
                                try (FileWriter writer = new FileWriter(encryptedKeyFile)) {
                                    writer.write(encryptedKey);
                                    JOptionPane.showMessageDialog(EncryptionAesModeWindow.this,
                                            "Ключ успешно сохранен в " + encryptedKeyFile.getAbsolutePath(),
                                            "Успех", JOptionPane.INFORMATION_MESSAGE);
                                } catch (IOException ioEx) {
                                    JOptionPane.showMessageDialog(EncryptionAesModeWindow.this,
                                            "Ошибка записи зашифрованного ключа: " + ioEx.getMessage(),
                                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                                }
                            });

                            buttonPanel.add(copyButton);
                            buttonPanel.add(saveKeyButton);
                            panel.add(resultLabel, BorderLayout.CENTER);
                            panel.add(buttonPanel, BorderLayout.SOUTH);

                            JOptionPane.showMessageDialog(EncryptionAesModeWindow.this,
                                    panel, "Результаты шифрования", JOptionPane.INFORMATION_MESSAGE);
                        });
                    }).start();

                    progressDialog.setVisible(true);
                }).start();
            }
        });



        cbcButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(EncryptionAesModeWindow.this,
                        "Вы выбрали режим CBC. В конце шифрования запомните ключ!",
                        "Информация", JOptionPane.INFORMATION_MESSAGE);


                File[] inputFiles = FileUtils.selectMultipleFiles("Выберите файлы для шифрования");

                if (inputFiles == null || inputFiles.length == 0) {
                    JOptionPane.showMessageDialog(EncryptionAesModeWindow.this,
                            "Файлы не выбраны. Операция отменена.",
                            "Ошибка", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                for (File file : inputFiles) {
                    if (file.length() > 1.2 * 1024 * 1024 * 1024) { // Ограничение 1.2 ГБ
                        JOptionPane.showMessageDialog(EncryptionAesModeWindow.this,
                                "Файл \"" + file.getName() + "\" превышает размер 1.2 ГБ.",
                                "Ошибка", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                String firstFileName = inputFiles[0].getName();
                String keyFileName = firstFileName.contains(".")
                        ? firstFileName.substring(0, firstFileName.lastIndexOf('.')) + ".key"
                        : firstFileName + ".key";

                File saveFolder = FileUtils.selectFolder("Выберите папку для сохранения зашифрованных файлов");
                if (saveFolder == null) {
                    JOptionPane.showMessageDialog(EncryptionAesModeWindow.this,
                            "Папка не выбрана. Операция отменена.",
                            "Ошибка", JOptionPane.WARNING_MESSAGE);
                    return;
                }


                MouseTrackerWindow trackerWindow = new MouseTrackerWindow();
                trackerWindow.setVisible(true);

                new Thread(() -> {
                    trackerWindow.waitForTrackingToComplete();
                    String keyHash = trackerWindow.getTrackerLogic().hashSumOfCoordinates();


                    JDialog progressDialog = new JDialog(EncryptionAesModeWindow.this, "Шифрование файлов", true);
                    progressDialog.setLayout(new BorderLayout());
                    JProgressBar progressBar = new JProgressBar(0, inputFiles.length);
                    progressBar.setValue(0);
                    progressDialog.add(new JLabel("Шифрование выполняется..."), BorderLayout.NORTH);
                    progressDialog.add(progressBar, BorderLayout.CENTER);
                    progressDialog.setSize(300, 100);
                    progressDialog.setLocationRelativeTo(EncryptionAesModeWindow.this);

                    new Thread(() -> {
                        long totalEncryptionTime = 0;
                        final int[] successCount = {0};
                        List<String> failedFiles = new ArrayList<>();

                        for (int i = 0; i < inputFiles.length; i++) {
                            File inputFile = inputFiles[i];
                            File saveFile = new File(saveFolder, inputFile.getName() + ".enc");

                            try {
                                long encryptionTime = AesEncryptionCBC.encryptFileCBC(inputFile, saveFile, keyHash);
                                totalEncryptionTime += encryptionTime;
                                successCount[0]++;
                            } catch (Exception ex) {
                                failedFiles.add(inputFile.getName());
                            }


                            final int progress = i + 1;
                            SwingUtilities.invokeLater(() -> progressBar.setValue(progress));
                        }
                        long totalTime = totalEncryptionTime;

                        SwingUtilities.invokeLater(progressDialog::dispose);


                        SwingUtilities.invokeLater(() -> {
                            StringBuilder resultMessage = new StringBuilder();
                            resultMessage.append("<html>Шифрование завершено!<br>")
                                    .append("Успешно зашифровано файлов: ").append(successCount[0]).append("<br>")
                                    .append("Не удалось зашифровать: ").append(failedFiles.size()).append("<br>");

                            if (!failedFiles.isEmpty()) {
                                resultMessage.append("Список файлов с ошибками:<br>");
                                for (String fileName : failedFiles) {
                                    resultMessage.append("- ").append(fileName).append("<br>");
                                }
                            }

                            resultMessage.append("Общее время шифрования: ")
                                    .append(totalTime).append(" ms<br>")
                                    .append("Ключ: <b>").append(keyHash).append("</b></html>");


                            JPanel panel = new JPanel(new BorderLayout());
                            JLabel resultLabel = new JLabel(resultMessage.toString());
                            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                            JButton copyButton = new JButton("Копировать ключ");

                            copyButton.addActionListener(copyEvent -> {
                                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                                        new StringSelection(keyHash), null);
                                JOptionPane.showMessageDialog(EncryptionAesModeWindow.this,
                                        "Ключ скопирован в буфер обмена!",
                                        "Успех", JOptionPane.INFORMATION_MESSAGE);
                            });
                            JButton saveKeyButton = new JButton("Сохранить ключ в криптоконтейнер");
                            saveKeyButton.addActionListener(saveEvent -> {

                                File containerFolder = FileUtils.selectFolder("Выберите криптоконтейнер для сохранения ключа");
                                if (containerFolder == null || !new File(containerFolder, "pass.key").exists()) {
                                    JOptionPane.showMessageDialog(EncryptionAesModeWindow.this,
                                            "Выбрана некорректная папка или криптоконтейнер не содержит файл pass.key.",
                                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                                    return;
                                }

                                JPanel passwordPanel = new JPanel(new BorderLayout(5, 5));
                                JPasswordField passwordField = new JPasswordField(20);
                                JCheckBox showPasswordCheckbox = new JCheckBox("Показать пароль");

                                showPasswordCheckbox.addActionListener(ex -> {
                                    if (showPasswordCheckbox.isSelected()) {
                                        passwordField.setEchoChar((char) 0);
                                    } else {
                                        passwordField.setEchoChar('•');
                                    }
                                });

                                passwordPanel.add(new JLabel("Введите пароль криптоконтейнера:"), BorderLayout.NORTH);
                                passwordPanel.add(passwordField, BorderLayout.CENTER);
                                passwordPanel.add(showPasswordCheckbox, BorderLayout.SOUTH);

                                int result = JOptionPane.showConfirmDialog(
                                        EncryptionAesModeWindow.this,
                                        passwordPanel,
                                        "Авторизация в криптоконтейнере",
                                        JOptionPane.OK_CANCEL_OPTION,
                                        JOptionPane.PLAIN_MESSAGE
                                );

                                if (result != JOptionPane.OK_OPTION) {
                                    JOptionPane.showMessageDialog(EncryptionAesModeWindow.this,
                                            "Операция отменена.",
                                            "Информация", JOptionPane.INFORMATION_MESSAGE);
                                    return;
                                }

                                char[] passwordChars = passwordField.getPassword();
                                if (passwordChars == null || passwordChars.length == 0) {
                                    JOptionPane.showMessageDialog(EncryptionAesModeWindow.this,
                                            "Пароль контейнера не может быть пустым.",
                                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                                    return;
                                }

                                String containerPassphrase = new String(passwordChars);
                                String password = containerPassphrase;
                                File keyFile = new File(containerFolder, "pass.key");
                                String containerKey;
                                try {
                                    containerKey = FileUtils.readFileToString(keyFile, StandardCharsets.UTF_8);
                                    containerPassphrase = KeyGeneratorUtils.generateKeyFromPassphrase(containerPassphrase);
                                    System.out.println(containerPassphrase);
                                    System.out.println(containerKey);
                                    if (!KeyGeneratorUtils.verifyKey(containerPassphrase, containerKey)) {
                                        JOptionPane.showMessageDialog(EncryptionAesModeWindow.this,
                                                "Неверный пароль для криптоконтейнера.",
                                                "Ошибка", JOptionPane.ERROR_MESSAGE);
                                        return;
                                    }
                                } catch (IOException ioEx) {
                                    JOptionPane.showMessageDialog(EncryptionAesModeWindow.this,
                                            "Ошибка чтения файла pass.key: " + ioEx.getMessage(),
                                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                                    return;
                                }

                                String encryptedKey;
                                try {
                                    encryptedKey = KeyGeneratorUtils.encryptWithPassword(keyHash, password);
                                    System.out.println(encryptedKey);
                                } catch (Exception ex) {
                                    JOptionPane.showMessageDialog(EncryptionAesModeWindow.this,
                                            "Ошибка шифрования ключа: " + ex.getMessage(),
                                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                                    return;
                                }

                                File encryptedKeyFile = new File(containerFolder, keyFileName);
                                try (FileWriter writer = new FileWriter(encryptedKeyFile)) {
                                    writer.write(encryptedKey);
                                    JOptionPane.showMessageDialog(EncryptionAesModeWindow.this,
                                            "Ключ успешно сохранен в " + encryptedKeyFile.getAbsolutePath(),
                                            "Успех", JOptionPane.INFORMATION_MESSAGE);
                                } catch (IOException ioEx) {
                                    JOptionPane.showMessageDialog(EncryptionAesModeWindow.this,
                                            "Ошибка записи зашифрованного ключа: " + ioEx.getMessage(),
                                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                                }
                            });

                            buttonPanel.add(copyButton);
                            buttonPanel.add(saveKeyButton);
                            panel.add(resultLabel, BorderLayout.CENTER);
                            panel.add(buttonPanel, BorderLayout.SOUTH);

                            JOptionPane.showMessageDialog(EncryptionAesModeWindow.this,
                                    panel, "Результаты шифрования", JOptionPane.INFORMATION_MESSAGE);
                        });
                    }).start();

                    progressDialog.setVisible(true);
                }).start();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                dispose();
                
                SwingUtilities.invokeLater(() -> {
                    EncryptionModeWindow encryptionModeWindow = new EncryptionModeWindow();
                    encryptionModeWindow.setVisible(true);
                });
            }
        });
    }
}
