package ru.CryptoProvider.ForWindow;

import ru.CryptoProvider.Utils.FileUtils;
import ru.CryptoProvider.Utils.KeyGeneratorUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainApp extends Component {
    public void main() {
        
        JFrame mainFrame = new JFrame("Криптопровайдер");
        mainFrame.setSize(400, 300);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);

        
        JPanel panel = new JPanel();
        panel.setLayout(null);
        mainFrame.add(panel);

        JButton conteinerButton = new JButton("Создание криптоконтейнера");
        conteinerButton.setBounds(100, 30, 200, 50);
        panel.add(conteinerButton);

        conteinerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                                                  SwingUtilities.invokeLater(() -> {
                                                      // Запрос имени контейнера
                                                      String containerName = JOptionPane.showInputDialog(
                                                              MainApp.this,
                                                              "Введите имя для криптоконтейнера:",
                                                              "Создание криптоконтейнера", JOptionPane.PLAIN_MESSAGE);

                                                      if (containerName == null || containerName.trim().isEmpty()) {
                                                          JOptionPane.showMessageDialog(
                                                                  MainApp.this,
                                                                  "Имя контейнера не может быть пустым.",
                                                                  "Ошибка", JOptionPane.ERROR_MESSAGE);
                                                          return;
                                                      }

                                                      if (!containerName.matches("[a-zA-Z0-9_\\- ]+")) {
                                                          JOptionPane.showMessageDialog(
                                                                  MainApp.this,
                                                                  "Имя контейнера может содержать только буквы, цифры, пробелы, дефисы и подчеркивания.",
                                                                  "Ошибка", JOptionPane.ERROR_MESSAGE);
                                                          return;
                                                      }

                                                      // Создание панели для ввода пароля с кнопкой "Показать пароль"
                                                      JPanel passwordPanel = new JPanel(new BorderLayout());
                                                      JPasswordField passwordField = new JPasswordField(20);
                                                      JCheckBox showPasswordCheckbox = new JCheckBox("Показать пароль");

                                                      passwordPanel.add(passwordField, BorderLayout.CENTER);
                                                      passwordPanel.add(showPasswordCheckbox, BorderLayout.SOUTH);

                                                      // Добавление функционала "Показать пароль"
                                                      showPasswordCheckbox.addActionListener(ev -> {
                                                          if (showPasswordCheckbox.isSelected()) {
                                                              passwordField.setEchoChar((char) 0); // Отображать текст
                                                          } else {
                                                              passwordField.setEchoChar('•'); // Скрывать текст
                                                          }
                                                      });

                                                      int result = JOptionPane.showConfirmDialog(
                                                              MainApp.this,
                                                              passwordPanel,
                                                              "Введите парольную фразу для криптоконтейнера",
                                                              JOptionPane.OK_CANCEL_OPTION,
                                                              JOptionPane.PLAIN_MESSAGE);

                                                      if (result != JOptionPane.OK_OPTION) {
                                                          JOptionPane.showMessageDialog(
                                                                  MainApp.this,
                                                                  "Операция отменена.",
                                                                  "Информация", JOptionPane.INFORMATION_MESSAGE);
                                                          return;
                                                      }

                                                      String passphrase = new String(passwordField.getPassword());

                                                      if (passphrase.trim().isEmpty()) {
                                                          JOptionPane.showMessageDialog(
                                                                  MainApp.this,
                                                                  "Парольная фраза не может быть пустой.",
                                                                  "Ошибка", JOptionPane.ERROR_MESSAGE);
                                                          return;
                                                      }

                                                      if (passphrase.length() < 8) {
                                                          JOptionPane.showMessageDialog(
                                                                  MainApp.this,
                                                                  "Парольная фраза должна быть длиной не менее 8 символов.",
                                                                  "Ошибка", JOptionPane.WARNING_MESSAGE);
                                                          return;
                                                      }

                                                      // Выбор папки для криптоконтейнера
                                                      File parentFolder = FileUtils.selectFolder("Выберите место для создания криптоконтейнера");
                                                      if (parentFolder == null) {
                                                          JOptionPane.showMessageDialog(
                                                                  MainApp.this,
                                                                  "Папка не выбрана. Операция отменена.",
                                                                  "Ошибка", JOptionPane.WARNING_MESSAGE);
                                                          return;
                                                      }

                                                      File containerFolder = new File(parentFolder, containerName);
                                                      if (containerFolder.exists()) {
                                                          JOptionPane.showMessageDialog(
                                                                  MainApp.this,
                                                                  "Криптоконтейнер с таким именем уже существует.",
                                                                  "Ошибка", JOptionPane.ERROR_MESSAGE);
                                                          return;
                                                      }

                                                      if (!containerFolder.mkdirs()) {
                                                          JOptionPane.showMessageDialog(
                                                                  MainApp.this,
                                                                  "Не удалось создать папку криптоконтейнера.",
                                                                  "Ошибка", JOptionPane.ERROR_MESSAGE);
                                                          return;
                                                      }

                                                      // Генерация ключа
                                                      String encryptionKey = KeyGeneratorUtils.generateKeyFromPassphrase(passphrase);
                                                      System.out.println(encryptionKey.length());
                                                      // Создание файла pass.key
                                                      File keyFile = new File(containerFolder, "pass.key");
                                                      try (FileWriter writer = new FileWriter(keyFile)) {
                                                          writer.write(encryptionKey);
                                                      } catch (IOException ioEx) {
                                                          JOptionPane.showMessageDialog(
                                                                  MainApp.this,
                                                                  "Ошибка при создании файла pass.key: " + ioEx.getMessage(),
                                                                  "Ошибка", JOptionPane.ERROR_MESSAGE);
                                                          return;
                                                      }

                                                      JOptionPane.showMessageDialog(
                                                              MainApp.this,
                                                              "Криптоконтейнер \"" + containerName + "\" успешно создан в: " + containerFolder.getAbsolutePath(),
                                                              "Успех", JOptionPane.INFORMATION_MESSAGE);
                                                  });
                                              }
                                          });



        JButton encryptionButton = new JButton("Шифрование");
        encryptionButton.setBounds(100, 100, 200, 50);
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
        decryptionButton.setBounds(100, 170, 200, 50);
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
