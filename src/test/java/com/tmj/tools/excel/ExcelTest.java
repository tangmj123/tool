package com.tmj.tools.excel;

public class ExcelTest {
  
	
	public static String camelToSplitName(String camelName, String split) {
	    if (camelName == null || camelName.length() == 0) {
	        return camelName;
	    }
	    StringBuilder buf = null;
	    for (int i = 0; i < camelName.length(); i ++) {
	        char ch = camelName.charAt(i);
	        if (ch >= 'A' && ch <= 'Z') {
	            if (buf == null) {
	                buf = new StringBuilder();
	                if (i > 0) {
	                    buf.append(camelName.substring(0, i));
	                }
	            }
	            if (i > 0) {
	                buf.append(split);
	            }
	            buf.append(Character.toLowerCase(ch));
	        } else if (buf != null) {
	            buf.append(ch);
	        }
	    }
	    return buf == null ? camelName : buf.toString();
	}
	
	public static void main(String[] args) {
		System.out.println(camelToSplitName("camelName", "-"));
	}
}
