package ru.CryptoProvider.ForWindow;

import ru.CryptoProvider.Utils.FileUtils;
import ru.CryptoProvider.Utils.KeyGeneratorUtils;
import ru.CryptoProvider.Utils.RSAWithKeyContainerUtils;

import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;

public class ForSignWindow extends Component {
    public void createSignature() {
        File containerFolder = FileUtils.selectFolder("Выберите криптоконтейнер");
        if (containerFolder == null || !new File(containerFolder, "pass.key").exists()) {
            JOptionPane.showMessageDialog(this,
                    "Выбрана некорректная папка или криптоконтейнер не содержит файл pass.key.",
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JPanel passwordPanel = new JPanel(new BorderLayout(5, 5));
        JPasswordField passwordField = new JPasswordField(20);
        JCheckBox showPasswordCheckbox = new JCheckBox("Показать пароль");

        showPasswordCheckbox.addActionListener(ex -> {
            if (showPasswordCheckbox.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('•');
            }
        });

        passwordPanel.add(new JLabel("Введите пароль криптоконтейнера:"), BorderLayout.NORTH);
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        passwordPanel.add(showPasswordCheckbox, BorderLayout.SOUTH);

        int result = JOptionPane.showConfirmDialog(
                this,
                passwordPanel,
                "Авторизация в криптоконтейнере",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(this,
                    "Операция отменена.",
                    "Информация", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        char[] passwordChars = passwordField.getPassword();
        if (passwordChars == null || passwordChars.length == 0) {
            JOptionPane.showMessageDialog(this,
                    "Пароль контейнера не может быть пустым.",
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String containerPassphrase = new String(passwordChars);

        File passKeyFile = new File(containerFolder, "pass.key");
        try {
            String containerKey = FileUtils.readFileToString(passKeyFile, StandardCharsets.UTF_8);
            containerPassphrase = KeyGeneratorUtils.generateKeyFromPassphrase(containerPassphrase);
            if (!KeyGeneratorUtils.verifyKey(containerPassphrase, containerKey)) {
                JOptionPane.showMessageDialog(this,
                        "Неверный пароль для криптоконтейнера.",
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (IOException ioEx) {
            JOptionPane.showMessageDialog(this,
                    "Ошибка чтения файла pass.key: " + ioEx.getMessage(),
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            KeyPair keyPair = KeyGeneratorUtils.generateKeyPair();
            File publicKeyFile = new File(containerFolder, "public.key");
            KeyGeneratorUtils.savePublicKey(keyPair.getPublic(), publicKeyFile);
//            System.out.println(keyPair.getPrivate());
//            System.out.println(keyPair.getPrivate());
            File privateKeyFile = new File(containerFolder, "private.key");
            KeyGeneratorUtils.saveEncryptedPrivateKey(keyPair.getPrivate(), privateKeyFile, containerPassphrase);

            JOptionPane.showMessageDialog(this, "Пара ключей успешно создана и сохранена в криптоконтейнер.", "Успех", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ошибка при создании ключей: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }


    public void signFile() {
        File containerFolder = FileUtils.selectFolder("Выберите криптоконтейнер с закрытым ключом");
        if (containerFolder == null || !new File(containerFolder, "pass.key").exists() || !new File(containerFolder, "private.key").exists()) {
            JOptionPane.showMessageDialog(this,
                    "Выбрана некорректная папка или криптоконтейнер не содержит необходимые файлы (pass.key, private.key).",
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JPanel passwordPanel = new JPanel(new BorderLayout(5, 5));
        JPasswordField passwordField = new JPasswordField(20);
        JCheckBox showPasswordCheckbox = new JCheckBox("Показать пароль");

        showPasswordCheckbox.addActionListener(ex -> {
            if (showPasswordCheckbox.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('•');
            }
        });

        passwordPanel.add(new JLabel("Введите пароль криптоконтейнера:"), BorderLayout.NORTH);
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        passwordPanel.add(showPasswordCheckbox, BorderLayout.SOUTH);

        int result = JOptionPane.showConfirmDialog(
                this,
                passwordPanel,
                "Авторизация в криптоконтейнере",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(this,
                    "Операция отменена.",
                    "Информация", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        char[] passwordChars = passwordField.getPassword();
        if (passwordChars == null || passwordChars.length == 0) {
            JOptionPane.showMessageDialog(this,
                    "Пароль контейнера не может быть пустым.",
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String containerPassphrase = new String(passwordChars);

        PrivateKey privateKey;
        try {
            File passKeyFile = new File(containerFolder, "pass.key");
            String containerKey = FileUtils.readFileToString(passKeyFile, StandardCharsets.UTF_8);
            containerPassphrase = KeyGeneratorUtils.generateKeyFromPassphrase(containerPassphrase);
            if (!KeyGeneratorUtils.verifyKey(containerPassphrase, containerKey)) {
                JOptionPane.showMessageDialog(this,
                        "Неверный пароль для криптоконтейнера.",
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JFileChooser keyChooser = new JFileChooser();
            keyChooser.setDialogTitle("Выберите файл с закрытым ключом");
            int keyResult = keyChooser.showOpenDialog(this);

            if (keyResult != JFileChooser.APPROVE_OPTION) {
                JOptionPane.showMessageDialog(this,
                        "Файл с закрытым ключом не выбран. Операция отменена.",
                        "Ошибка", JOptionPane.WARNING_MESSAGE);
                return;
            }

            File privateKeyFile = keyChooser.getSelectedFile();
            privateKey = KeyGeneratorUtils.loadEncryptedPrivateKey(privateKeyFile, containerPassphrase);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ошибка при загрузке закрытого ключа. Возможно выбран неверный файл!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Выберите файл для подписи");
        int returnValue = fileChooser.showOpenDialog(this);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File fileToSign = fileChooser.getSelectedFile();

            try {
                byte[] fileBytes = Files.readAllBytes(fileToSign.toPath());

                byte[] signature = RSAWithKeyContainerUtils.signFileWithPrivateKey(fileBytes, privateKey);

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                outputStream.write(fileBytes);
                outputStream.write(signature);
                byte[] signedData = outputStream.toByteArray();

                File signedFile = new File(fileToSign.getParentFile(), fileToSign.getName() + ".sig");
                Files.write(signedFile.toPath(), signedData);

                JOptionPane.showMessageDialog(this,
                        "Файл успешно подписан. Подписанный файл сохранен в: " + signedFile.getAbsolutePath(),
                        "Успех", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ошибка при подписании файла: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void verifySignature() {
        JFileChooser publicKeyChooser = new JFileChooser();
        publicKeyChooser.setDialogTitle("Выберите файл с открытым ключом");
        int publicKeyResult = publicKeyChooser.showOpenDialog(this);

        if (publicKeyResult != JFileChooser.APPROVE_OPTION) {
            JOptionPane.showMessageDialog(this,
                    "Файл с открытым ключом не выбран. Операция отменена.",
                    "Ошибка", JOptionPane.WARNING_MESSAGE);
            return;
        }

        File publicKeyFile = publicKeyChooser.getSelectedFile();

        PublicKey publicKey;
        try {
            publicKey = KeyGeneratorUtils.loadPublicKey(publicKeyFile);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ошибка при загрузке открытого ключа: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Выберите подписанный файл для проверки");
        int fileResult = fileChooser.showOpenDialog(this);

        if (fileResult != JFileChooser.APPROVE_OPTION) {
            JOptionPane.showMessageDialog(this,
                    "Файл для проверки не выбран. Операция отменена.",
                    "Ошибка", JOptionPane.WARNING_MESSAGE);
            return;
        }

        File signedFile = fileChooser.getSelectedFile();

        try {
            byte[] signedData = Files.readAllBytes(signedFile.toPath());

            int signatureLength = 256;
            if (signedData.length < signatureLength) {
                JOptionPane.showMessageDialog(this,
                        "Файл подписан некорректно. Отсутствует подпись.",
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            byte[] fileBytes = Arrays.copyOfRange(signedData, 0, signedData.length - signatureLength);
            byte[] signatureBytes = Arrays.copyOfRange(signedData, signedData.length - signatureLength, signedData.length);

            boolean isVerified = RSAWithKeyContainerUtils.verifyFileSignature(fileBytes, signatureBytes, publicKey);

            if (isVerified) {
                JOptionPane.showMessageDialog(this,
                        "Подпись действительна!",
                        "Успех", JOptionPane.INFORMATION_MESSAGE);

                if (signedFile.getName().endsWith(".sig")) {
                    int response = JOptionPane.showConfirmDialog(this,
                            "Преобразовать файл в нужный формат?",
                            "Преобразование файла", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                    if (response == JOptionPane.YES_OPTION) {
                        File originalFile = new File(signedFile.getAbsolutePath().replace(".sig", ""));
                        if (!originalFile.exists()) {
                            JOptionPane.showMessageDialog(this,
                                    "Исходный файл не найден. Объединение невозможно.",
                                    "Ошибка", JOptionPane.ERROR_MESSAGE);
                            return;
                        } else  {
                            JOptionPane.showMessageDialog(this,
                                    "Файл преобразован.",
                                    "Успех", JOptionPane.INFORMATION_MESSAGE);
                    }
                    }
                }
            }else {
                JOptionPane.showMessageDialog(this,
                        "Подпись недействительна!",
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ошибка при проверке подписи: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

}
