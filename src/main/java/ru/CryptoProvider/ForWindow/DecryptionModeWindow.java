package ru.CryptoProvider.ForWindow;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
                JOptionPane.showMessageDialog(DecryptionModeWindow.this, "Вы выбрали RSA", "Информация", JOptionPane.INFORMATION_MESSAGE);
// не робит
            }
        });
    }
}
