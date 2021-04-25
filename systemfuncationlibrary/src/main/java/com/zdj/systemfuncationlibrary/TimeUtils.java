package com.zdj.systemfuncationlibrary;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <pre>
 *     author : dejinzhang
 *     time : 2021/04/25
 *     desc : 时间工具类
 * </pre>
 */
public class TimeUtils {
    /**
     * 获取字符串形式表示的时间/日期
     * @param date
     * @param format
     * @return
     */
    public static String getDateFormat(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }
}
