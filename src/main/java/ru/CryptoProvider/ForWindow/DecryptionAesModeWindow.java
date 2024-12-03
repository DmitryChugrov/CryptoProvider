package ru.CryptoProvider.ForWindow;

import ru.CryptoProvider.AES.AesDecryptionCBC;
import ru.CryptoProvider.AES.AesDecryptionECB;
import ru.CryptoProvider.AES.AesEncryptionECB;
import ru.CryptoProvider.FileUtils.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DecryptionAesModeWindow extends JFrame {

    public DecryptionAesModeWindow() {

        setTitle("Выбор режима расшифрования AES");
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
                JOptionPane.showMessageDialog(DecryptionAesModeWindow.this,
                        "Вы выбрали режим ECB. Укажите ключ для расшифровки!",
                        "Информация", JOptionPane.INFORMATION_MESSAGE);

                
                File[] encryptedFiles = FileUtils.selectMultipleFiles("Выберите файлы для расшифровки");
                if (encryptedFiles == null || encryptedFiles.length == 0) {
                    JOptionPane.showMessageDialog(DecryptionAesModeWindow.this,
                            "Файлы не выбраны. Операция отменена.",
                            "Ошибка", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                
                for (File file : encryptedFiles) {
                    if (!file.getName().toLowerCase().endsWith(".enc")) {
                        JOptionPane.showMessageDialog(DecryptionAesModeWindow.this,
                                "Файл " + file.getName() + " не имеет расширения .enc. Выберите корректные файлы.",
                                "Ошибка", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }

                
                String key = JOptionPane.showInputDialog(DecryptionAesModeWindow.this,
                        "Введите ключ для расшифровки:",
                        "Ввод ключа", JOptionPane.PLAIN_MESSAGE);

                if (key == null || key.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(DecryptionAesModeWindow.this,
                            "Ключ не может быть пустым!",
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (key.length() != 16 && key.length() != 24 && key.length() != 32) {
                    JOptionPane.showMessageDialog(DecryptionAesModeWindow.this,
                            "Длина ключа должна быть 16, 24 или 32 символа!",
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                
                File saveFolder = FileUtils.selectFolder("Выберите папку для сохранения расшифрованных файлов");
                if (saveFolder == null) {
                    JOptionPane.showMessageDialog(DecryptionAesModeWindow.this,
                            "Папка не выбрана. Операция отменена.",
                            "Ошибка", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                
                JDialog progressDialog = new JDialog(DecryptionAesModeWindow.this, "Расшифровка файлов", true);
                progressDialog.setLayout(new BorderLayout());
                JProgressBar progressBar = new JProgressBar(0, encryptedFiles.length);
                progressBar.setValue(0);
                progressDialog.add(new JLabel("Расшифровка выполняется..."), BorderLayout.NORTH);
                progressDialog.add(progressBar, BorderLayout.CENTER);
                progressDialog.setSize(300, 100);
                progressDialog.setLocationRelativeTo(DecryptionAesModeWindow.this);

                new Thread(() -> {
                    long totalDecryptionTime = 0;
                    final int[] successCount = {0};
                    List<String> failedFiles = new ArrayList<>();

                    for (int i = 0; i < encryptedFiles.length; i++) {
                        File encryptedFile = encryptedFiles[i];
                        File saveFile = new File(saveFolder, encryptedFile.getName().replace(".enc", ""));

                        try {
                            long decryptionTime = AesDecryptionECB.decryptFileECB(encryptedFile, saveFile, key);
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

                        JOptionPane.showMessageDialog(DecryptionAesModeWindow.this,
                                resultMessage.toString(),
                                "Результаты расшифровки", JOptionPane.INFORMATION_MESSAGE);
                    });
                }).start();

                progressDialog.setVisible(true);
            }
        });



        cbcButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(DecryptionAesModeWindow.this,
                        "Вы выбрали режим CBC. Укажите ключ для расшифровки!",
                        "Информация", JOptionPane.INFORMATION_MESSAGE);


                File[] encryptedFiles = FileUtils.selectMultipleFiles("Выберите файлы для расшифровки");
                if (encryptedFiles == null || encryptedFiles.length == 0) {
                    JOptionPane.showMessageDialog(DecryptionAesModeWindow.this,
                            "Файлы не выбраны. Операция отменена.",
                            "Ошибка", JOptionPane.WARNING_MESSAGE);
                    return;
                }


                for (File file : encryptedFiles) {
                    if (!file.getName().toLowerCase().endsWith(".enc")) {
                        JOptionPane.showMessageDialog(DecryptionAesModeWindow.this,
                                "Файл " + file.getName() + " не имеет расширения .enc. Выберите корректные файлы.",
                                "Ошибка", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }


                String key = JOptionPane.showInputDialog(DecryptionAesModeWindow.this,
                        "Введите ключ для расшифровки:",
                        "Ввод ключа", JOptionPane.PLAIN_MESSAGE);

                if (key == null || key.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(DecryptionAesModeWindow.this,
                            "Ключ не может быть пустым!",
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (key.length() != 16 && key.length() != 24 && key.length() != 32) {
                    JOptionPane.showMessageDialog(DecryptionAesModeWindow.this,
                            "Длина ключа должна быть 16, 24 или 32 символа!",
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }


                File saveFolder = FileUtils.selectFolder("Выберите папку для сохранения расшифрованных файлов");
                if (saveFolder == null) {
                    JOptionPane.showMessageDialog(DecryptionAesModeWindow.this,
                            "Папка не выбрана. Операция отменена.",
                            "Ошибка", JOptionPane.WARNING_MESSAGE);
                    return;
                }


                JDialog progressDialog = new JDialog(DecryptionAesModeWindow.this, "Расшифровка файлов", true);
                progressDialog.setLayout(new BorderLayout());
                JProgressBar progressBar = new JProgressBar(0, encryptedFiles.length);
                progressBar.setValue(0);
                progressDialog.add(new JLabel("Расшифровка выполняется..."), BorderLayout.NORTH);
                progressDialog.add(progressBar, BorderLayout.CENTER);
                progressDialog.setSize(300, 100);
                progressDialog.setLocationRelativeTo(DecryptionAesModeWindow.this);

                new Thread(() -> {
                    long totalDecryptionTime = 0;
                    final int[] successCount = {0};
                    List<String> failedFiles = new ArrayList<>();

                    for (int i = 0; i < encryptedFiles.length; i++) {
                        File encryptedFile = encryptedFiles[i];
                        File saveFile = new File(saveFolder, encryptedFile.getName().replace(".enc", ""));

                        try {
                            long decryptionTime = AesDecryptionCBC.decryptFileCBC(encryptedFile, saveFile, key);
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

                        JOptionPane.showMessageDialog(DecryptionAesModeWindow.this,
                                resultMessage.toString(),
                                "Результаты расшифровки", JOptionPane.INFORMATION_MESSAGE);
                    });
                }).start();

                progressDialog.setVisible(true);
            }
        });


        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                dispose();

                SwingUtilities.invokeLater(() -> {
                    DecryptionModeWindow decryptionModeWindow = new DecryptionModeWindow();
                    decryptionModeWindow.setVisible(true);
                });
            }
        });
    }
}
