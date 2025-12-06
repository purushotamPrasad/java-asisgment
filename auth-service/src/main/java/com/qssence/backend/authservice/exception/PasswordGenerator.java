package com.qssence.backend.authservice.exception;

import java.security.SecureRandom;

public class PasswordGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUabcdefghijklmnopqrstu0123456789@$";
    private static final int PASSWORD_LENGTH = 8;

    public static String generatePassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            password.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return password.toString();
    }

//    public static String randomInteger() {
//        String password = "";
//        for (int i = 0; i < 3; i++) {
//            int randomDigit = (int) (Math.random() * 10);
//            password += randomDigit;
//        }
//        return password;
//    }
}
