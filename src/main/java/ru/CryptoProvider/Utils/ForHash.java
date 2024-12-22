package ru.CryptoProvider.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public class ForHash {
    public static String calculateCombinedHash(File[] files, String algorithm) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);

            
            for (File file : files) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    byte[] dataBuffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fis.read(dataBuffer)) != -1) {
                        messageDigest.update(dataBuffer, 0, bytesRead);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            
            byte[] hashBytes = messageDigest.digest();
            return byteArrayToHex(hashBytes);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    
    private static String byteArrayToHex(byte[] hashBytes) {
        try (Formatter formatter = new Formatter()) {
            for (byte b : hashBytes) {
                formatter.format("%02x", b);
            }
            return formatter.toString();
        }
    }
    public static JPanel createHashPanel(String hash) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        
        JLabel hashLabel = new JLabel("Общий хеш для выбранных файлов:");

        
        JLabel hashValueLabel = new JLabel("<html><pre>" + hash + "</pre></html>");
        hashValueLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));  

        
        JButton copyButton = new JButton("Скопировать хеш");
        copyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                StringSelection selection = new StringSelection(hash);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);

                
                JOptionPane.showMessageDialog(null, "Хеш скопирован в буфер обмена!", "Успех", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        
        panel.add(hashLabel);
        panel.add(hashValueLabel);
        panel.add(copyButton);

        return panel;
    }

}
