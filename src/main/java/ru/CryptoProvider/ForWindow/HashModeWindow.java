package ru.CryptoProvider.ForWindow;

import ru.CryptoProvider.Utils.FileUtils;
import ru.CryptoProvider.Utils.ForHash;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;


public class HashModeWindow extends JFrame {
    private String currentHash;
    public HashModeWindow(){

        setTitle("Выбор режима хеширования");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);


        JPanel panel = new JPanel();
        panel.setLayout(null);
        add(panel);


        JButton sha256Button = new JButton("SHA-256");
        sha256Button.setBounds(50, 50, 200, 40);
        panel.add(sha256Button);


        JButton sha512Button = new JButton("SHA-512");
        sha512Button.setBounds(50, 100, 200, 40);
        panel.add(sha512Button);


        sha256Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Открыть диалог выбора нескольких файлов
                File[] selectedFiles = FileUtils.selectMultipleFiles("Выберите файлы для SHA-256");
                if (selectedFiles != null) {
                    
                    SwingUtilities.invokeLater(() -> {
                        String hash = ForHash.calculateCombinedHash(selectedFiles, "SHA-256");
                        if (hash != null) {
                            currentHash = hash; 

                            
                            JPanel hashPanel = ForHash.createHashPanel(hash);

                            
                            JOptionPane.showMessageDialog(null, hashPanel, "Результат хеширования", JOptionPane.INFORMATION_MESSAGE);
                        }
                    });
                }
            }
        });


        sha512Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                File[] selectedFiles = FileUtils.selectMultipleFiles("Выберите файлы для SHA-512");
                if (selectedFiles != null) {
                    
                    SwingUtilities.invokeLater(() -> {
                        String hash = ForHash.calculateCombinedHash(selectedFiles, "SHA-512");
                        if (hash != null) {
                            currentHash = hash; 

                            
                            JPanel hashPanel = ForHash.createHashPanel(hash);

                            
                            JOptionPane.showMessageDialog(null, hashPanel, "Результат хеширования", JOptionPane.INFORMATION_MESSAGE);
                        }
                    });
                }
            }
        });
    }
}
