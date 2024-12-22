package ru.CryptoProvider.ForWindow;

import ru.CryptoProvider.RSA.RsaEncryption;
import ru.CryptoProvider.Utils.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import static ru.CryptoProvider.RSA.RsaEncryption.generateRSAKeyPair;

public class EncryptionModeWindow extends JFrame {
    public EncryptionModeWindow() {
        
        setTitle("Выбор режима шифрования");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        
        JPanel panel = new JPanel();
        panel.setLayout(null);
        add(panel);

        
        JButton aesButton = new JButton("AES");
        aesButton.setBounds(50, 50, 200, 40);
        panel.add(aesButton);

        
        JButton rsaButton = new JButton("RSA");
        rsaButton.setBounds(50, 100, 200, 40);
        panel.add(rsaButton);

        
        aesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                dispose();
                
                SwingUtilities.invokeLater(() -> {
                    EncryptionAesModeWindow EncryptionAesModeWindow = new EncryptionAesModeWindow();
                    EncryptionAesModeWindow.setVisible(true);
                });
            }
        });

        
        rsaButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(EncryptionModeWindow.this,
                        "Вы выбрали режим RSA для шифрования файлов. В конце запомните ключи!",
                        "Информация", JOptionPane.INFORMATION_MESSAGE);

                File[] inputFiles = FileUtils.selectMultipleFiles("Выберите файлы для шифрования");
                if (inputFiles == null || inputFiles.length == 0) {
                    JOptionPane.showMessageDialog(EncryptionModeWindow.this,
                            "Файлы не выбраны. Операция отменена.",
                            "Ошибка", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                
                MouseTrackerWindow trackerWindow = new MouseTrackerWindow();
                trackerWindow.setVisible(true);

                new Thread(() -> {
                    trackerWindow.waitForTrackingToComplete();
                    KeyPair keyPair = null;
                    try {
                        keyPair = generateRSAKeyPair();
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                    PublicKey publicKey = keyPair.getPublic();
                    PrivateKey privateKey = keyPair.getPrivate();

                    
                    File saveFolder = FileUtils.selectFolder("Выберите папку для сохранения зашифрованных файлов");
                    if (saveFolder == null) {
                        JOptionPane.showMessageDialog(EncryptionModeWindow.this,
                                "Папка не выбрана. Операция отменена.",
                                "Ошибка", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    
                    new Thread(() -> {
                        long totalEncryptionTime = 0;
                        final int[] successCount = {0};
                        List<String> failedFiles = new ArrayList<>();

                        for (File inputFile : inputFiles) {
                            File saveFile = new File(saveFolder, inputFile.getName() + ".enc");

                            try {
                                
                                long encryptionTime = RsaEncryption.encryptFileRSA(inputFile, saveFile, publicKey);
                                totalEncryptionTime += encryptionTime;
                                successCount[0]++;
                            } catch (Exception ex) {
                                
                                String errorMessage = "Не удалось зашифровать файл " + inputFile.getName() + ": " + ex.getMessage();
                                failedFiles.add(errorMessage);  
                                ex.printStackTrace();  
                            }
                        }

                        long finalTotalEncryptionTime = totalEncryptionTime;
                        SwingUtilities.invokeLater(() -> {
                            StringBuilder resultMessage = new StringBuilder();
                            resultMessage.append("<html>Шифрование завершено!<br>")
                                    .append("Успешно зашифровано файлов: ").append(successCount[0]).append("<br>")
                                    .append("Не удалось зашифровать: ").append(failedFiles.size()).append("<br>");

                            if (!failedFiles.isEmpty()) {
                                resultMessage.append("Список файлов с ошибками:<br>");
                                for (String fileError : failedFiles) {
                                    resultMessage.append("- ").append(fileError).append("<br>");
                                }
                            }

                            resultMessage.append("Общее время шифрования: ")
                                    .append(finalTotalEncryptionTime).append(" ms<br>");

                            
                            JPanel panel = new JPanel(new BorderLayout());
                            JLabel resultLabel = new JLabel(resultMessage.toString());
                            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                            JButton copyPublicKeyButton = new JButton("Копировать публичный ключ");

                            copyPublicKeyButton.addActionListener(copyEvent -> {
                                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                                        new StringSelection(String.valueOf(publicKey)), null);
                                JOptionPane.showMessageDialog(EncryptionModeWindow.this,
                                        "Публичный ключ скопирован в буфер обмена!",
                                        "Успех", JOptionPane.INFORMATION_MESSAGE);
                            });

                            JButton saveKeyButton = new JButton("Сохранить ключи в криптоконтейнер");
                            saveKeyButton.addActionListener(saveEvent -> {
                                File containerFolder = FileUtils.selectFolder("Выберите криптоконтейнер для сохранения ключей");
                                if (containerFolder == null || !new File(containerFolder, "pass.key").exists()) {
                                    JOptionPane.showMessageDialog(EncryptionModeWindow.this,
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
                                        EncryptionModeWindow.this,
                                        passwordPanel,
                                        "Авторизация в криптоконтейнере",
                                        JOptionPane.OK_CANCEL_OPTION,
                                        JOptionPane.PLAIN_MESSAGE
                                );

                                if (result != JOptionPane.OK_OPTION) {
                                    JOptionPane.showMessageDialog(EncryptionModeWindow.this,
                                            "Операция отменена.",
                                            "Информация", JOptionPane.INFORMATION_MESSAGE);
                                    return;
                                }

                                char[] passwordChars = passwordField.getPassword();
                                if (passwordChars == null || passwordChars.length == 0) {
                                    JOptionPane.showMessageDialog(EncryptionModeWindow.this,
                                            "Пароль контейнера не может быть пустым.",
                                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                                    return;
                                }

                                String containerPassphrase = new String(passwordChars);
                                
                                try {
                                    File keyFile = new File(containerFolder, inputFiles[0]+"_Public.key");
                                    FileWriter writer = new FileWriter(keyFile);
                                    writer.write(String.valueOf(publicKey));
                                    writer.close();

                                    keyFile = new File(containerFolder, inputFiles[0]+"_Private.key");
                                    writer = new FileWriter(keyFile);
                                    writer.write(String.valueOf(privateKey));
                                    writer.close();

                                    JOptionPane.showMessageDialog(EncryptionModeWindow.this,
                                            "Ключи успешно сохранены в криптоконтейнер!",
                                            "Успех", JOptionPane.INFORMATION_MESSAGE);

                                } catch (IOException ioEx) {
                                    JOptionPane.showMessageDialog(EncryptionModeWindow.this,
                                            "Ошибка записи ключей: " + ioEx.getMessage(),
                                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                                }
                            });

                            buttonPanel.add(copyPublicKeyButton);
                            buttonPanel.add(saveKeyButton);
                            panel.add(resultLabel, BorderLayout.CENTER);
                            panel.add(buttonPanel, BorderLayout.SOUTH);

                            JOptionPane.showMessageDialog(EncryptionModeWindow.this,
                                    panel, "Результаты шифрования", JOptionPane.INFORMATION_MESSAGE);
                        });
                    }).start();

                }).start();
            }
        });
    }
}
