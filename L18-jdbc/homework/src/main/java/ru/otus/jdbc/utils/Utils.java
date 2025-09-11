package ru.otus.jdbc.utils;

public class Utils {

    public static String toSnake(String s) {
        return s.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }
}
