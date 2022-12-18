package com.opentable.coding.challenge.util;

import java.util.Random;

public class RandomStringGenerator {

    private static Random random = new Random();

    public static String generate(int strLen) {
        //  lowercase alphabets are from 97 to 122. uppercase alphabets are from 65 to 90
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < strLen; i++) {
            int n = random.nextInt(52);
            char c;
            if (n < 26) {
                c = (char) (n + 65);
            } else {
                c = (char) (n - 26 + 97);
            }
            buf.append(c);
        }
        return buf.toString();
    }
}
