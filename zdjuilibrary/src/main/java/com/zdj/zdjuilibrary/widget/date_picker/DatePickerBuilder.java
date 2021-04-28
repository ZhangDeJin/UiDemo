package com.zdj.zdjuilibrary.widget.date_picker;

import android.content.Context;

import java.util.Calendar;

/**
 * <pre>
 *     author : dejinzhang
 *     time : 2021/04/27
 *     desc : 构造类
 * </pre>
 */
public class DatePickerBuilder {
    private Options options;

    public DatePickerBuilder(Context context, OnDateSelectListener listener) {
        options = new Options();
        options.context = context;
        options.dateSelectListener = listener;
    }

    public DatePickerBuilder setDialogStyle(int style) {
        options.style = style;
        return this;
    }

    public DatePickerBuilder setType(boolean[] type) {
        options.type = type;
        return this;
    }

    public DatePickerBuilder setCalendar(Calendar calendar) {
        options.calendar = calendar;
        return this;
    }

    public DatePickerBuilder setRangDate(Calendar beginCalendar, Calendar endCalendar) {
        options.beginCalendar = beginCalendar;
        options.endCalendar = endCalendar;
        return this;
    }

    public DatePickerBuilder setTitleBgColor(int color) {
        options.titleBgColor = color;
        return this;
    }

    public DatePickerBuilder setCancelBtnColor(int color) {
        options.cancelBtnColor = color;
        return this;
    }

    public DatePickerBuilder setConfirmBtnColor(int color) {
        options.confirmBtnColor = color;
        return this;
    }

    public DatePickerBuilder setCancelBtnText(String text) {
        options.cancelBtnText = text;
        return this;
    }

    public DatePickerBuilder setConfirmBtnText(String text) {
        options.confirmBtnText = text;
        return this;
    }

    public DatePickerDialog build() {
        return new DatePickerDialog(options);
    }
}
