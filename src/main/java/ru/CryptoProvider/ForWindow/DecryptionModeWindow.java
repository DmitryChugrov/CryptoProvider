package ru.CryptoProvider.ForWindow;

import ru.CryptoProvider.RSA.RsaDecryption;
import ru.CryptoProvider.Utils.FileUtils;
import ru.CryptoProvider.Utils.KeyGeneratorUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;

public class DecryptionModeWindow extends JFrame {
    public DecryptionModeWindow(){

        setTitle("Выбор режима расшифрования");
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
                    DecryptionAesModeWindow decryptionAesModeWindow = new DecryptionAesModeWindow();
                    decryptionAesModeWindow.setVisible(true);
                });
            }
        });


        rsaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(DecryptionModeWindow.this,
                        "Вы выбрали режим RSA. Укажите приватный ключ для расшифровки!",
                        "Информация", JOptionPane.INFORMATION_MESSAGE);

                
                Object[] options = {"Ввести ключ вручную", "Загрузить ключ из файла"};
                int choice = JOptionPane.showOptionDialog(
                        DecryptionModeWindow.this,
                        "Как вы хотите указать ключ?",
                        "Выбор ключа",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]
                );

                String key = null;

                if (choice == 0) { 
                    JPanel keyPanel = new JPanel();
                    keyPanel.setLayout(new BoxLayout(keyPanel, BoxLayout.Y_AXIS));

                    JPasswordField keyField = new JPasswordField(20);
                    JButton showKeyButton = new JButton("Показать ключ");

                    
                    showKeyButton.addActionListener(ex -> {
                        if (keyField.getEchoChar() == (char) 0) {
                            keyField.setEchoChar('*');  
                            showKeyButton.setText("Показать ключ");
                        } else {
                            keyField.setEchoChar((char) 0);  
                            showKeyButton.setText("Скрыть ключ");
                        }
                    });

                    keyPanel.add(new JLabel("Введите приватный ключ для расшифровки:"));
                    keyPanel.add(keyField);
                    keyPanel.add(showKeyButton);

                    int option = JOptionPane.showConfirmDialog(
                            DecryptionModeWindow.this,
                            keyPanel,
                            "Ввод ключа",
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.PLAIN_MESSAGE
                    );

                    if (option != JOptionPane.OK_OPTION) {
                        return; 
                    }

                    key = new String(keyField.getPassword()).trim();

                    if (key.isEmpty()) {
                        JOptionPane.showMessageDialog(DecryptionModeWindow.this,
                                "Ключ не может быть пустым!",
                                "Ошибка", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                } else if (choice == 1) { 
                    File keyFile = FileUtils.selectFile("Выберите файл ключа (.key)");
                    if (keyFile == null) {
                        JOptionPane.showMessageDialog(DecryptionModeWindow.this,
                                "Файл ключа не выбран. Операция отменена.",
                                "Ошибка", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    try {
                        
                        String encryptedKey = FileUtils.readFileToString(keyFile, StandardCharsets.UTF_8);

                        JPanel passwordPanel = new JPanel();
                        passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.Y_AXIS));

                        JPasswordField passwordField = new JPasswordField(20);
                        JButton showPasswordButton = new JButton("Показать пароль");

                        
                        showPasswordButton.addActionListener(ex -> {
                            if (passwordField.getEchoChar() == (char) 0) {
                                passwordField.setEchoChar('*');  
                                showPasswordButton.setText("Показать пароль");
                            } else {
                                passwordField.setEchoChar((char) 0);  
                                showPasswordButton.setText("Скрыть пароль");
                            }
                        });

                        passwordPanel.add(new JLabel("Введите пароль для расшифровки ключа:"));
                        passwordPanel.add(passwordField);
                        passwordPanel.add(showPasswordButton);

                        int passwordOption = JOptionPane.showConfirmDialog(
                                DecryptionModeWindow.this,
                                passwordPanel,
                                "Ввод пароля",
                                JOptionPane.OK_CANCEL_OPTION,
                                JOptionPane.PLAIN_MESSAGE
                        );

                        if (passwordOption != JOptionPane.OK_OPTION) {
                            return; 
                        }

                        String password = new String(passwordField.getPassword()).trim();

                        if (password.isEmpty()) {
                            JOptionPane.showMessageDialog(DecryptionModeWindow.this,
                                    "Пароль не может быть пустым!",
                                    "Ошибка", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        
                        key = KeyGeneratorUtils.decryptKeyWithPassword(encryptedKey, password);

                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(DecryptionModeWindow.this,
                                "Ошибка чтения или расшифровки файла ключа: " + ex.getMessage(),
                                "Ошибка", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                } else {
                    return; 
                }

                
                File[] encryptedFiles = FileUtils.selectMultipleFiles("Выберите файлы для расшифровки");
                if (encryptedFiles == null || encryptedFiles.length == 0) {
                    JOptionPane.showMessageDialog(DecryptionModeWindow.this,
                            "Файлы не выбраны. Операция отменена.",
                            "Ошибка", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                for (File file : encryptedFiles) {
                    if (!file.getName().toLowerCase().endsWith(".enc")) {
                        JOptionPane.showMessageDialog(DecryptionModeWindow.this,
                                "Файл " + file.getName() + " не имеет расширения .enc. Выберите корректные файлы.",
                                "Ошибка", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }

                File saveFolder = FileUtils.selectFolder("Выберите папку для сохранения расшифрованных файлов");
                if (saveFolder == null) {
                    JOptionPane.showMessageDialog(DecryptionModeWindow.this,
                            "Папка не выбрана. Операция отменена.",
                            "Ошибка", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                JDialog progressDialog = new JDialog(DecryptionModeWindow.this, "Расшифровка файлов", true);
                progressDialog.setLayout(new BorderLayout());
                JProgressBar progressBar = new JProgressBar(0, encryptedFiles.length);
                progressBar.setValue(0);
                progressDialog.add(new JLabel("Расшифровка выполняется..."), BorderLayout.NORTH);
                progressDialog.add(progressBar, BorderLayout.CENTER);
                progressDialog.setSize(300, 100);
                progressDialog.setLocationRelativeTo(DecryptionModeWindow.this);


                String finalKey = key;
                new Thread(() -> {
                    long totalDecryptionTime = 0;
                    final int[] successCount = {0};
                    List<String> failedFiles = new ArrayList<>();

                    for (int i = 0; i < encryptedFiles.length; i++) {
                        File encryptedFile = encryptedFiles[i];
                        File saveFile = new File(saveFolder, encryptedFile.getName().replace(".enc", ""));

                        try {
                            PrivateKey privateKey = RsaDecryption.getPrivateKeyFromString(finalKey);
                            long decryptionTime = RsaDecryption.decryptFileRSA(encryptedFile, saveFile,privateKey);
                            totalDecryptionTime += decryptionTime;
                            successCount[0]++;
                        } catch (Exception ex) {
                            failedFiles.add(encryptedFile.getName());
                        }

                        final int progress = i + 1;
                        SwingUtilities.invokeLater(() -> progressBar.setValue(progress));
                    }
                    long totalTime = totalDecryptionTime;

                    SwingUtilities.invokeLater(progressDialog::dispose);

                    SwingUtilities.invokeLater(() -> {
                        StringBuilder resultMessage = new StringBuilder();
                        resultMessage.append("<html>Расшифровка завершена!<br>")
                                .append("Успешно расшифровано файлов: ").append(successCount).append("<br>")
                                .append("Не удалось расшифровать: ").append(failedFiles.size()).append("<br>");

                        if (!failedFiles.isEmpty()) {
                            resultMessage.append("Список файлов с ошибками:<br>");
                            for (String fileName : failedFiles) {
                                resultMessage.append("- ").append(fileName).append("<br>");
                            }
                        }

                        resultMessage.append("Общее время расшифровки: ")
                                .append(totalTime).append(" ms</html>");

                        JOptionPane.showMessageDialog(DecryptionModeWindow.this,
                                resultMessage.toString(),
                                "Результаты расшифровки", JOptionPane.INFORMATION_MESSAGE);
                    });
                }).start();
                progressDialog.setVisible(true);
            }
        });
    }
}
