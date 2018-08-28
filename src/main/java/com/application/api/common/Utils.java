package com.application.api.common;

public class Utils {

    public static String clean(String string) {
        int n = string.length();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < n; i++) {
            if (string.charAt(i) != ' ') {
                sb.append(string.charAt(i));
            }
        }
        return sb.toString();
    }

    public static String replaceComas(String string) {
        int n = string.length();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            if (string.charAt(i) == ',') {
                sb.append('.');
            } else {
                sb.append(string.charAt(i));
            }
        }
        return sb.toString();
    }


}
