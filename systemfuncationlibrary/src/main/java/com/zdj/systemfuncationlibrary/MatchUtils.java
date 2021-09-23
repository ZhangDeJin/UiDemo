package com.zdj.systemfuncationlibrary;

import java.util.regex.Pattern;

/**
 * <pre>
 *     author : dejinzhang
 *     time : 2021/09/16
 *     desc : 校验工具类
 * </pre>
 */
public class MatchUtils {
    /**
     * 验证是否为纯数字
     * @param str  数据
     * @return  true：是；false：不是
     */
    public static boolean isNumber(String str) {
        return Pattern.compile("[0-9]*").matcher(str).matches();
    }

    /**
     * 验证是否是合法的手机号码
     * @param mobilePhoneNum  手机号码
     * @return true：是；false：不是
     */
    public static boolean isLegalMobilePhoneNum(String mobilePhoneNum) {
        return Pattern.compile("^[1][3,4,5,6,7,8,9][0-9]{9}$").matcher(mobilePhoneNum).matches();
    }

    /**
     * 验证是否是合法的邮箱
     *
     * 该校验的正则表达式为：
     * "^\\w+((-\\w+)|(\\.\\w+))*@\\w+(\\.\\w{2,3}){1,3}$"
     * "\w"：匹配字母、数字、下划线。等价于'[A-Za-z0-9_]'
     * "|"：或的意思，就是二选一
     * "*"：出现0次或者多次
     * "+"：出现1次或者多次
     * "{n,m}"：至少出现n个，最多出现m个
     * "$"：以前面的字符结束
     *
     * @param email  邮箱
     * @return true：是；false：不是
     */
    public static boolean isLegalEmail(String email) {
        return Pattern.compile("^\\w+((-\\w+)|(\\.\\w+))*@\\w+(\\.\\w{2,3}){1,3}$").matcher(email).matches();
    }

    /**
     * 验证是否是合法的QQ号码
     * @param qq  qq
     * @return true：是；false：不是
     */
    public static boolean isLegalQQ(String qq) {
        return Pattern.compile("[1-9][0-9]{4,11}").matcher(qq).matches();
    }

    /**
     * 对某个内容进行正则校验
     * @param content  某个内容
     * @param regex  正则表达式
     * @return true：满足校验；false：不满足校验
     */
    public static boolean isLegalForSomething(String content, String regex) {
        return Pattern.compile(regex).matcher(content).matches();
    }
}
