package com.zdj.systemfuncationlibrary;

import java.text.ParseException;
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
     * @param date  时间/日期
     * @param format  格式
     * @return  转化后的内容
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

    /**
     * 根据时间文本得到Calendar对象
     * @param time  时间文本
     * @param format  格式
     * @return  Calendar对象
     */
    public static Calendar getCalendarFromTime(String time, String format) {
        Calendar calendar = null;
        try {
            calendar = Calendar.getInstance();
            calendar.setTime(new SimpleDateFormat(format).parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    /**
     * 根据时间戳得到Calendar对象
     * @param timeInMills  时间戳
     * @return  Calendar对象
     */
    public static Calendar getCalendarFromTime(long timeInMills) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(timeInMills));
        return calendar;
    }

    /**
     * 获取与当前日期相差指定时间的日期
     * @param period  相差时间
     * @param format  格式
     * @return  指定的日期
     */
    public static String getSpecifiedDayTime(int period, String format) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, period);
        return getDateFormat(calendar.getTime(), format);
    }
}
