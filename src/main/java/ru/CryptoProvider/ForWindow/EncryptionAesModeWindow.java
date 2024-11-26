package ru.CryptoProvider.ForWindow;

import ru.CryptoProvider.AES.AesEncryptionECB;
import ru.CryptoProvider.FileUtils.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

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

                File inputFile = FileUtils.selectFile("Выберите файл для шифрования");
                if (inputFile == null) return;

                File saveFile = FileUtils.selectSaveLocation(inputFile.getName() + ".enc");
                if (saveFile == null) return;

                MouseTrackerWindow trackerWindow = new MouseTrackerWindow();
                trackerWindow.setVisible(true);

                new Thread(() -> {
                    trackerWindow.waitForTrackingToComplete();
                    String keyHash = trackerWindow.getTrackerLogic().hashSumOfCoordinates();

                    try {
                        long encryptionTime = AesEncryptionECB.encryptFileECB(inputFile, saveFile, keyHash);

                        
                        JPanel panel = new JPanel(new BorderLayout());
                        JLabel messageLabel = new JLabel("<html>Шифрование выполнено за " + encryptionTime + " ms.<br>" +
                                "Сохранено в: " + saveFile.getAbsolutePath() + "<br>Ключ: <b>" + keyHash + "</b></html>");
                        JButton copyButton = new JButton("Копировать ключ");
                        
                        copyButton.setPreferredSize(new Dimension(100, 30));
                        
                        copyButton.addActionListener(copyEvent -> {
                            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                                    new StringSelection(keyHash), null);
                            JOptionPane.showMessageDialog(EncryptionAesModeWindow.this,
                                    "Ключ скопирован в буфер обмена!",
                                    "Успех", JOptionPane.INFORMATION_MESSAGE);
                        });

                        
                        panel.add(messageLabel, BorderLayout.CENTER);
                        panel.add(copyButton, BorderLayout.SOUTH);

                        
                        JOptionPane.showMessageDialog(EncryptionAesModeWindow.this,
                                panel, "Успех", JOptionPane.INFORMATION_MESSAGE);

                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(EncryptionAesModeWindow.this,
                                "Ошибка: " + ex.getMessage(),
                                "Ошибка", JOptionPane.ERROR_MESSAGE);
                    }

                    trackerWindow.dispose();
                }).start();
            }
        });



        cbcButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(EncryptionAesModeWindow.this, "Вы выбрали режим CBC", "Информация", JOptionPane.INFORMATION_MESSAGE);
                
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
