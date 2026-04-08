package com.glee.xjpa.util;

/**
 * @author GENSEN
 * @date 2023/12/14
 * @description
 */
public class NameUtil {

    /**
     * "abc" -> "Abc"
     *
     * @param str
     * @return
     */
    public static String toUCase(String str) {
        return ("" + str.charAt(0)).toUpperCase() + str.substring(1);
    }

    /**
     * "Abc" -> "abc"
     *
     * @param str
     * @return
     */
    public static String toLCase(String str) {
        return ("" + str.charAt(0)).toLowerCase() + str.substring(1);
    }

    /**
     * "abc_xyz" -> "AbcXyz"
     *
     * @param str
     * @return
     */
    public static String toHHCase(String str) {
        StringBuilder sb = new StringBuilder();
        for (String s : str.split("_")) {
            sb.append(toUCase(s));
        }
        return sb.toString();
    }

    /**
     * "abc_xyz" -> "abcXyz"
     *
     * @param str
     * @return
     */
    public static String toLHCase(String str) {
        StringBuilder sb = new StringBuilder();
        for (String s : str.split("_")) {
            sb.append(toUCase(s));
        }
        return toLCase(sb.toString());
    }

    /**
     * "AbcXye" -> "abc_xyz"
     *
     * @param str
     * @return
     */
    public static String toUUCase(String str) {
        str = str.replaceAll("([A-Z][a-z])", "_$1");
        StringBuilder sb = new StringBuilder();
        for (String s : str.split("_")) {
            if ("".equals(s.trim())) {
                continue;
            }
            sb.append("_").append(toLCase(s));
        }
        return sb.substring(1);
    }

    /**
     * "abc_xyz" -> "abcXyz"
     * @param str
     * @return
     */
    public static String toLUCase(String str) {
        if(str.startsWith("_")) {
            str = str.replaceFirst("_", "");
        }
        return toLCase(toUUCase(str));
    }

    public static String toUfirst(String name) {
        String first = name.substring(0, 1).toUpperCase();
        String rest = name.substring(1);
        return first + rest;
    }

    /**
     * 驼峰命名转下划线命名
     *
     * @param camelCase 驼峰命名
     * @return 下划线命名
     */
    public static String camelToSnake(String camelCase) {
        return camelCase.replaceAll("([a-z0-9])([A-Z])", "$1_$2").toLowerCase();
    }
}
