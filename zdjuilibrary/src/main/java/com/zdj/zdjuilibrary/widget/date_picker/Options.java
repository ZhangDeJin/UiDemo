package com.zdj.zdjuilibrary.widget.date_picker;

import android.content.Context;

import java.util.Calendar;

/**
 * <pre>
 *     author : dejinzhang
 *     time : 2021/04/27
 *     desc : 配置类
 * </pre>
 */
public class Options {
    //标题栏的默认背景颜色
    private static final int PICKER_VIEW_TITLE_BG_COLOR = 0xFFF1F2F6;
    //取消按钮的默认文本颜色
    private static final int PICKER_VIEW_CANCEL_BTN_COLOR = 0xFFA5A9AF;
    //确定按钮的默认文本颜色
    private static final int PICKER_VIEW_CONFIRM_BTN_COLOR = 0xFF3E7BF8;

    public Context context;
    /**
     * DatePickerDialog的样式
     */
    public int style;

    /**
     * 显示类型，默认年月周全部显示
     */
    public boolean[] type = new boolean[]{true, true, true};
    /**
     * 设置日历（即日期）,默认为当前日期
     */
    public Calendar calendar = Calendar.getInstance();
    /**
     * 开始日期
     */
    public Calendar beginCalendar;
    /**
     * 结束日期
     */
    public Calendar endCalendar;
    /**
     * 标题栏的背景颜色
     */
    public int titleBgColor = PICKER_VIEW_TITLE_BG_COLOR;
    /**
     * 取消按钮的文本颜色
     */
    public int cancelBtnColor = PICKER_VIEW_CANCEL_BTN_COLOR;
    /**
     * 确定按钮的文本颜色
     */
    public int confirmBtnColor = PICKER_VIEW_CONFIRM_BTN_COLOR;
    /**
     * 取消按钮的文本内容
     */
    public String cancelBtnText;
    /**
     * 确认按钮的文本内容
     */
    public String confirmBtnText;

    public OnDateSelectListener dateSelectListener;
}
