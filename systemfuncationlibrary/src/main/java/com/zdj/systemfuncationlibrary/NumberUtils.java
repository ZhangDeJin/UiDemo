package com.zdj.systemfuncationlibrary;

/**
 * <pre>
 *     author : dejinzhang
 *     time : 2021/11/04
 *     desc : 数字工具类
 * </pre>
 */
public class NumberUtils {
    public NumberUtils() {
    }

    /**
     * 中文数字
     */
    private static final String[] CH_NUM = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};

    /**
     * 中文数字单位
     */
    private static final String[] CN_UNIT = {"", "十", "百", "千", "万", "十", "百", "千", "亿", "十", "百", "千"};

    /**
     * 特殊字符：负
     */
    private static final String CN_NEGATIVE = "负";

    /**
     * 特殊字符：点
     */
    private static final String CN_POINT = "点";

    /**
     * int 转 中文数字
     * @param intNum
     * @return
     */
    public static String intToChineseNum(int intNum) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean isNegative = false;
        if (intNum < 0) {
            isNegative = true;
            intNum *= -1;
        }
        int count = 0;
        while (intNum > 0) {
            stringBuilder.insert(0, CH_NUM[intNum % 10] + CN_UNIT[count]);
            intNum /= 10;
            count++;
        }
        if (isNegative) {
            stringBuilder.insert(0, CN_NEGATIVE);
        }
        /**
         * 如果不增加这些replaceAll的处理，可能会出现:
         * 10--->一十零
         * 16--->一十六
         * 60--->六十零
         * 100--->一百零十零
         * 106--->一百零十六
         * 等等情况
         */
        return stringBuilder.toString().replaceAll("零[千百十]", "零").replaceAll("零+万", "万")
                .replaceAll("零+亿", "亿").replaceAll("亿万", "亿零")
                .replaceAll("零+", "零").replaceAll("零$", "");
    }
}
