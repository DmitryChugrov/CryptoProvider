package ru.CryptoProvider.ForWindow;

import ru.CryptoProvider.AES.AesEncryptionCBC;
import ru.CryptoProvider.AES.AesEncryptionECB;
import ru.CryptoProvider.FileUtils.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
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

                            
                            JPanel panel = new JPanel(new BorderLayout());
                            JLabel resultLabel = new JLabel(resultMessage.toString());
                            JButton copyButton = new JButton("Копировать ключ");

                            copyButton.addActionListener(copyEvent -> {
                                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                                        new StringSelection(keyHash), null);
                                JOptionPane.showMessageDialog(EncryptionAesModeWindow.this,
                                        "Ключ скопирован в буфер обмена!",
                                        "Успех", JOptionPane.INFORMATION_MESSAGE);
                            });

                            panel.add(resultLabel, BorderLayout.CENTER);
                            panel.add(copyButton, BorderLayout.SOUTH);

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
                            JButton copyButton = new JButton("Копировать ключ");

                            copyButton.addActionListener(copyEvent -> {
                                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                                        new StringSelection(keyHash), null);
                                JOptionPane.showMessageDialog(EncryptionAesModeWindow.this,
                                        "Ключ скопирован в буфер обмена!",
                                        "Успех", JOptionPane.INFORMATION_MESSAGE);
                            });

                            panel.add(resultLabel, BorderLayout.CENTER);
                            panel.add(copyButton, BorderLayout.SOUTH);

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
