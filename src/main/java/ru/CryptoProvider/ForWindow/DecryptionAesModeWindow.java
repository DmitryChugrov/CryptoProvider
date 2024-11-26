package ru.CryptoProvider.ForWindow;

import ru.CryptoProvider.AES.AesDecryptionECB;
import ru.CryptoProvider.AES.AesEncryptionECB;
import ru.CryptoProvider.FileUtils.FileUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

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

                
                File encryptedFile = FileUtils.selectFile("Выберите файл для расшифровки");
                if (encryptedFile == null) {
                    JOptionPane.showMessageDialog(DecryptionAesModeWindow.this,
                            "Файл не выбран. Операция отменена.",
                            "Ошибка", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (!encryptedFile.getName().toLowerCase().endsWith(".enc")) {
                    JOptionPane.showMessageDialog(DecryptionAesModeWindow.this,
                            "Выбранный файл не имеет расширения .enc. Выберите корректный файл.",
                            "Ошибка", JOptionPane.WARNING_MESSAGE);
                    return;
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

                
                File saveFile = FileUtils.selectSaveLocation(encryptedFile.getName().replace(".enc", ""));
                if (saveFile == null) {
                    JOptionPane.showMessageDialog(DecryptionAesModeWindow.this,
                            "Место сохранения не выбрано. Операция отменена.",
                            "Ошибка", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                new Thread(() -> {
                    try {
                        long decryptionTime = AesDecryptionECB.decryptFileECB(encryptedFile, saveFile, key);
                        JOptionPane.showMessageDialog(DecryptionAesModeWindow.this,
                                "Расшифровка выполнена за " + decryptionTime + " ms.\nСохранено в: " + saveFile.getAbsolutePath(),
                                "Успех", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(DecryptionAesModeWindow.this,
                                "Ошибка: " + ex.getMessage(),
                                "Ошибка", JOptionPane.ERROR_MESSAGE);
                    }
                }).start();
            }
        });


        cbcButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(DecryptionAesModeWindow.this, "Вы выбрали режим CBC", "Информация", JOptionPane.INFORMATION_MESSAGE);
                
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
