package com.example.demo.utils;

public class StringUtils {

    private static final String REGEX = "([a-z])([A-Z]+)";
    private static final String REPLACEMENT = "$1_$2";

    private StringUtils() {
        super();
    }

    // https://www.geeksforgeeks.org/convert-camel-case-string-to-snake-case-in-java/
    public static String camelToSnake(String str) {
        return str.replaceAll(REGEX, REPLACEMENT).toLowerCase();
    }
}
