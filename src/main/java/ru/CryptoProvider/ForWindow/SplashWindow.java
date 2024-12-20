package ru.CryptoProvider.ForWindow;

import javax.swing.*;
import java.awt.*;

public class SplashWindow extends JFrame {
    public SplashWindow() {
        setUndecorated(true);
        JLabel splashLabel = new JLabel(new ImageIcon(SplashWindow.class.getResource("/images/Криптопровайдер.png"))); // Укажите путь к вашей картинке
        getContentPane().add(splashLabel);
        setSize(1152, 647);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - getSize().width) / 2;
        int y = (screenSize.height - getSize().height) / 2;
        setLocation(x, y);
    }

    public void showSplash() {
        setVisible(true);
    }

    public void hideSplash() {
        setVisible(false);
        dispose();
    }
}
