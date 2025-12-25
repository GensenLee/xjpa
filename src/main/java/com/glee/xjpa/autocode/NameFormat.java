package com.glee.xjpa.autocode;

public class NameFormat {
	
	private static String getStanderString(String str) {
		if(str.startsWith("_")) {
			str = str.replaceFirst("_", "");
		}
		return str;
	}
	/**
	 * "abc" -> "Abc"
	 * @param str
	 * @return
	 */
	public static String toUCase(String str) {
		str = getStanderString(str);
		return ("" + str.charAt(0)).toUpperCase() + str.substring(1);
	}
	
	/**
	 * "Abc" -> "abc"
	 * @param str
	 * @return
	 */
	public static String toLCase(String str) {
		str = getStanderString(str);
		return ("" + str.charAt(0)).toLowerCase() + str.substring(1);
	}
	
	/**
	 * "Abc" -> "abc"   "AAA" -> "abc"
	 * @param str
	 * @return
	 */
	public static String toLCase2(String str) {
		str = getStanderString(str);
		if(str.matches("^[A-Z_]+$")){
			return str.toLowerCase();
		}
		return ("" + str.charAt(0)).toLowerCase() + str.substring(1);
	}
	
	/**
	 * "abc_xyz" -> "AbcXyz"
	 * @param str
	 * @return
	 */
	public static String toUUCase(String str) {
		str = getStanderString(str);
		StringBuilder sb = new StringBuilder();
		for(String s : str.split("_")) {
			sb.append(toUCase(s));
		}
		return sb.toString();
	}
	
	/**
	 * "abc_xyz" -> "abcXyz"
	 * @param str
	 * @return
	 */
	public static String toLUCase(String str) {
		str = getStanderString(str);
		return toLCase(toUUCase(str));
	}
	
	/**
	 * "abc_xyz" -> "AbcXyz"
	 * @param str
	 * @return
	 */
	public static String toUUCaseEx(String str) {
		str = getStanderString(str);
		StringBuilder sb = new StringBuilder();
		for(String s : str.split("_")) {
			sb.append(toUCase(s.toLowerCase()));
		}
		return sb.toString();
	}
	
	/**
	 * "AbcXye" -> "abc_xyz"
	 * @param str
	 * @return
	 */
	public static String toUUCaseStr(String str) {
		str = getStanderString(str);
		str = str.replaceAll("([A-Z][a-z])", "_$1");
		StringBuilder sb = new StringBuilder();
		for(String s : str.split("_")) {
			if("".equals(s.trim())){
				continue;
			}
			sb.append("_"+toLCase(s));
		}
		return sb.toString().substring(1);
	}
	
	public static String toUfirst(String name) {
		String first = name.substring(0, 1).toUpperCase();
		String rest = name.substring(1, name.length());
		return new StringBuffer(first).append(rest).toString(); 
	}

}
