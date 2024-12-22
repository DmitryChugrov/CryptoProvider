package ru.CryptoProvider;

import ru.CryptoProvider.ForWindow.MainApp;
import ru.CryptoProvider.ForWindow.SplashWindow;

import javax.swing.*;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        SplashWindow splashWindow = new SplashWindow();
        Thread splash = new Thread(() -> splashWindow.showSplash());
        splash.start();
        Thread.sleep(4000);
        splashWindow.hideSplash();

        MainApp mainApp = new MainApp();
        mainApp.main();
    }
}