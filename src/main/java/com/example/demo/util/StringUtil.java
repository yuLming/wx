package com.example.demo.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    private static final Logger logger = LoggerFactory.getLogger(StringUtil.class);

    public static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }


    public static List<Map<String, String>> addressResolution(String address) {
        String regex="((?<province>[^省]+省|.+自治区)|上海|北京|天津|重庆)(?<city>[^市]+市|.+自治州)(?<county>[^县]+县|.+区|.+镇|.+局)?(?<town>[^区]+区|.+镇)?(?<village>.*)";
        Matcher m = Pattern.compile(regex).matcher(address);
        String province = null, city = null, county = null, town = null, village = null;
        List<Map<String, String>> list = new ArrayList<>();
        Map<String, String> row = null;
        while (m.find()) {
            row = new LinkedHashMap<>();
            province = m.group("province");
            row.put("province", province == null ? "" : province.trim());
            city = m.group("city");
            row.put("city", city == null ? "" : city.trim());
            county = m.group("county");
            row.put("county", county == null ? "" : county.trim());
            town = m.group("town");
            row.put("town", town == null ? "" : town.trim());
            village = m.group("village");
            row.put("village", village == null ? "" : village.trim());
            list.add(row);
        }
        return list;
    }


    /**
     * 判断是否为空字符串或者为空。
     *
     * @param param：需要判断的字符串。
     * @return false：非空返回  true:空字符串或者null时返回。
     */
    public static boolean isNull(String param) {
        if (null == param) {
            return true;
        } else if (0 == param.trim().length()) {
            return true;
        } else if ("null".equals(param.trim())) {
            return true;
        } else if ("".equals(param.trim())) {
            return true;
        }
        return false;
    }

    public static String getListStr(List<String> strList, String split) {
        String result = "";
        if (strList != null) {
            for (int i = 0; i < strList.size(); i++) {
                if (i == strList.size() - 1) {
                    result += strList.get(i);
                } else {
                    result += strList.get(i) + split;
                }
            }
        }
        return result;
    }

    public static String getArrayStr(Object[] array, String split) {
        String result = "";
        if (array != null) {
            for (int i = 0; i < array.length; i++) {
                if (i == array.length - 1) {
                    result += array[i];
                } else {
                    result += array[i] + split;
                }
            }
        }
        return result;
    }

    public static String getArrayStr(byte[] array, String split) {
        String result = "";
        if (array != null) {
            for (int i = 0; i < array.length; i++) {
                if (i == array.length - 1) {
                    result += array[i];
                } else {
                    result += array[i] + split;
                }
            }
        }
        return result;
    }

    //为null的转换成""
    public static String changeNullToString(String var) {
        return var == null ? "" : var;
    }

    /**
     * 64进制
     */
    final static char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
            'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
            '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
            'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
            'Z', '+', '/',};


    /**
     * 把10进制的数字转换成64进制
     *
     * @param number
     * @return
     */
    public static String number_10_to_64(long number) {
        char[] buf = new char[64];
        int charPos = 64;
        int radix = 1 << 6;
        long mask = radix - 1;
        do {
            buf[--charPos] = digits[(int) (number & mask)];
            number >>>= 6;
        } while (number != 0);
        return new String(buf, charPos, (64 - charPos));
    }

    /**
     * 把64进制的字符串转换成10进制
     *
     * @param decompStr
     * @return
     */
    public static long number_64_to_10(String decompStr) {
        long result = 0;
        for (int i = decompStr.length() - 1; i >= 0; i--) {
            for (int j = 0; j < digits.length; j++) {
                if (decompStr.charAt(i) == digits[j]) {
                    result += ((long) j) << 6 * (decompStr.length() - 1 - i);
                }
            }
        }
        return result;
    }

    /**
     * 地址字符串优化
     * @param text
     * @return
     */
    public static String getAddressStr(String text) {
        if (isNull(text)) return "";
        if (text.contains("无信息")) return "";
        if (text.contains("无信息")) return "";
        if (text.contains("解释地址失败")) return "";
        if (text.contains("未解析出安装位置信息")) return "";
        if (text.contains("地址为空")) return "";
        text = text.replaceAll("其它","");
        if (isNull(text)) return "";
        return text;
    }
}
