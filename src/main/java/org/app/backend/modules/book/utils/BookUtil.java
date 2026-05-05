package org.app.backend.modules.book.utils;

import java.security.SecureRandom;

public class BookUtil {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateBarcode() {
        StringBuilder barcode = new StringBuilder(19);
        for (int i = 0; i < 19; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            barcode.append(CHARACTERS.charAt(index));
        }
        return barcode.toString();
    }
}