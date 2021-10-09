package com.qiezitv.common;

public class StringUtil {
    static String CHN_NUMBER[] = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
    static String CHN_UNIT[] = {"", "十", "百", "千"};          //权位
    static String CHN_UNIT_SECTION[] = {"", "万", "亿", "万亿"}; //节权位
    /**
     * 定义下划线
     */
    private static final char UNDERLINE = '_';

    /**
     * String为空判断(不允许空格)
     *
     * @param str
     * @return boolean
     */
    public static boolean isBlank(String str) {
        return str == null || "".equals(str.trim());
    }

    /**
     * String不为空判断(不允许空格)
     *
     * @param str
     * @return boolean
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * Byte数组为空判断
     *
     * @param bytes
     * @return boolean
     */
    public static boolean isNull(byte[] bytes) {
        if(bytes == null){
            return true;
        }
        // 根据byte数组长度为0判断
        return bytes.length == 0;
    }

    /**
     * Byte数组不为空判断
     *
     * @param bytes
     * @return boolean
     */
    public static boolean isNotNull(byte[] bytes) {
        return !isNull(bytes);
    }

    /**
     * 驼峰转下划线工具
     *
     * @param param
     * @return java.lang.String
     */
    public static String camelToUnderline(String param) {
        if (isNotBlank(param)) {
            int len = param.length();
            StringBuilder sb = new StringBuilder(len);
            for (int i = 0; i < len; ++i) {
                char c = param.charAt(i);
                if (Character.isUpperCase(c)) {
                    sb.append(UNDERLINE);
                    sb.append(Character.toLowerCase(c));
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        } else {
            return "";
        }
    }

    /**
     * 下划线转驼峰工具
     *
     * @param param
     * @return java.lang.String
     */
    public static String underlineToCamel(String param) {
        if (isNotBlank(param)) {
            int len = param.length();
            StringBuilder sb = new StringBuilder(len);
            for (int i = 0; i < len; ++i) {
                char c = param.charAt(i);
                if (c == 95) {
                    ++i;
                    if (i < len) {
                        sb.append(Character.toUpperCase(param.charAt(i)));
                    }
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        } else {
            return "";
        }
    }

    /**
     * 在字符串两周添加''
     *
     * @param param
     * @return java.lang.String
     */
    public static String addSingleQuotes(String param) {
        return "\'" + param + "\'";
    }

    /**
     * 阿拉伯数字转换为中文数字的核心算法实现。
     * @param num 为需要转换为中文数字的阿拉伯数字，是无符号的整形数
     * @return
     */
    public static String getChinesNum(int num) {
        StringBuilder returnStr = new StringBuilder();
        Boolean needZero = false;
        int pos=0;           //节权位的位置
        if(num==0){
            //如果num为0，进行特殊处理。
            returnStr.insert(0,CHN_NUMBER[0]);
        }
        while (num > 0) {
            int section = num % 10000;
            if (needZero) {
                returnStr.insert(0, CHN_NUMBER[0]);
            }
            String sectionToChn = SectionNumToChn(section);
            //判断是否需要节权位
            sectionToChn += (section != 0) ? CHN_UNIT_SECTION[pos] : CHN_UNIT_SECTION[0];
            returnStr.insert(0, sectionToChn);
            needZero = ((section < 1000 && section > 0) ? true : false); //判断section中的千位上是不是为零，若为零应该添加一个零。
            pos++;
            num = num / 10000;
        }
        return returnStr.toString();
    }
    /**
     * 将四位的section转换为中文数字
     * @param section
     * @return
     */
    public static String SectionNumToChn(int section) {
        StringBuilder returnStr = new StringBuilder();
        int unitPos = 0;       //节权位的位置编号，0-3依次为个十百千;

        Boolean zero = true;
        while (section > 0) {

            int v = (section % 10);
            if (v == 0) {
                if ((section == 0) || !zero) {
                    zero = true; /*需要补0，zero的作用是确保对连续的多个0，只补一个中文零*/
                    //chnStr.insert(0, chnNumChar[v]);
                    returnStr.insert(0, CHN_NUMBER[v]);
                }
            } else {
                zero = false; //至少有一个数字不是0
                StringBuilder tempStr = new StringBuilder();
                if(v == 1 && unitPos == 1){
//                    tempStr.append(CHN_NUMBER[v]);//数字v所对应的中文数字
                    tempStr.append(CHN_UNIT[unitPos]);  //数字v所对应的中文权位
                }else{
                    tempStr.append(CHN_NUMBER[v]);//数字v所对应的中文数字
                    tempStr.append(CHN_UNIT[unitPos]);  //数字v所对应的中文权位
                }
                returnStr.insert(0, tempStr);
            }
            unitPos++; //移位
            section = section / 10;
        }
        return returnStr.toString();
    }
    /**
     * 判定输入的是否是汉字
     *
     * @param c
     *  被校验的字符
     * @return true代表是汉字
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }
}
