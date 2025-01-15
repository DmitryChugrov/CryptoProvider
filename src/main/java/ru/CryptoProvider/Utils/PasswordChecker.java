package ru.CryptoProvider.Utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public enum PasswordChecker {
    WEAK, MEDIUM, STRONG;

    private static final Set<String> COMMON_PASSWORDS = new HashSet<>();

    
    static {
        try (InputStream inputStream = PasswordChecker.class.getClassLoader().getResourceAsStream("passwords_check_list.txt")) {
            if (inputStream != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        COMMON_PASSWORDS.add(line.trim());
                    }
                }
            } else {
                System.err.println("Файл common_passwords.txt не найден. Проверка на часто используемые пароли пропущена.");
            }
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке файла common_passwords.txt: " + e.getMessage());
        }
    }

    public static PasswordChecker assess(String password) {
        
        if (!COMMON_PASSWORDS.isEmpty() && COMMON_PASSWORDS.contains(password)) {
            return WEAK;
        }

        int criteriaMet = 0;

        
        if (password.matches(".*\\d.*")) {
            criteriaMet++;
        }
        
        if (password.matches(".*[a-zA-Z].*")) {
            criteriaMet++;
        }
        
        if (password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            criteriaMet++;
        }

        
        if (criteriaMet <= 1) {
            return WEAK;
        } else if (criteriaMet == 2) {
            return MEDIUM;
        } else {
            return STRONG;
        }
    }
}
