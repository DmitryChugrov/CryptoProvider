package ru.CryptoProvider.ForWindow;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainApp {
    public static void main(String[] args) {
        
        JFrame mainFrame = new JFrame("Mouse Tracker Launcher");
        mainFrame.setSize(400, 300);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);

        
        JPanel panel = new JPanel();
        panel.setLayout(null);
        mainFrame.add(panel);
        
        JButton encryptionButton = new JButton("Шифрование");
        encryptionButton.setBounds(100, 30, 200, 50);
        panel.add(encryptionButton);

        
        encryptionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                SwingUtilities.invokeLater(() -> {
                    EncryptionModeWindow encryptionModeWindow = new EncryptionModeWindow();
                    encryptionModeWindow.setVisible(true);
                });
            }
        });


        JButton decryptionButton = new JButton("Расшифрование");
        decryptionButton.setBounds(100, 100, 200, 50);
        panel.add(decryptionButton);

        decryptionButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> {
                    DecryptionModeWindow decryptionModeWindow = new DecryptionModeWindow();
                    decryptionModeWindow.setVisible(true);
                });
            }
        });
        
        mainFrame.setVisible(true);
    }
}
