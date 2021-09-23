package com.zdj.systemfuncationlibrary;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    /**
     * 格式化显示耗时
     * 注意：这里针对的是HH:mm:ss这种格式，如果需要其他格式，比如mm:ss，那么在方法中不能进行int hour = minute / 60;的操作。
     * @param duration  原始耗时数据，单位：秒。比如100秒。
     * @param simpleDateFormat  格式
     * @return  转换后的显示内容
     */
    public static String calculateDuration(int duration, SimpleDateFormat simpleDateFormat) {
        int minute = duration / 60;
        int hour = minute / 60;
        int second = duration - hour * 3600 - minute * 60;
        Calendar calendar = Calendar.getInstance();
        calendar.set(0, 0, 0, hour, minute, second);
        return simpleDateFormat.format(calendar.getTime());
    }
}
