package com.zdj.zdjuilibrary.widget.date_picker;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.zdj.systemfuncationlibrary.LogUtils;
import com.zdj.zdjuilibrary.R;

import java.lang.reflect.Field;
import java.util.Calendar;

/**
 * <pre>
 *     author : dejinzhang
 *     time : 2021/04/27
 *     desc : 日期选择器
 * </pre>
 */
public class DatePickerDialog extends Dialog implements View.OnClickListener, NumberPicker.OnScrollListener {
    private Options options;

    private ConstraintLayout cl_title;
    private TextView tv_cancel, tv_confirm;

    private NumberPicker year_picker, month_picker, week_picker;
    private TextView year_unit, month_unit, week_unit;

    private DatePickerDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        setContentView(R.layout.dialog_date_picker);
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = layoutParams.MATCH_PARENT;
        layoutParams.height = layoutParams.WRAP_CONTENT;
        window.setAttributes(layoutParams);
        window.setGravity(Gravity.BOTTOM);
    }

    public DatePickerDialog(Options options) {
        this(options.context, options.style);
        this.options = options;
        initView();
    }

    private void initView() {
        cl_title = findViewById(R.id.cl_title);
        tv_cancel = findViewById(R.id.tv_cancel);
        tv_confirm = findViewById(R.id.tv_confirm);

        year_picker = findViewById(R.id.year_picker);
        month_picker = findViewById(R.id.month_picker);
        week_picker = findViewById(R.id.week_picker);

        year_unit = findViewById(R.id.year_unit);
        month_unit = findViewById(R.id.month_unit);
        week_unit = findViewById(R.id.week_unit);

        year_picker.setWrapSelectorWheel(false);
        month_picker.setWrapSelectorWheel(false);
        week_picker.setWrapSelectorWheel(false);
        year_picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        month_picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        week_picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        /**
         * 注意：Android10 以上无法通过反射得到mSelectionDivider和mSelectionDividerHeight，所以不能修改分割线颜色和分割线宽度
         */
        Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFields) {
            LogUtils.i(pf.getName());
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    //设置分割线颜色
                    pf.set(year_picker, new ColorDrawable(Color.parseColor("#666666")));
                    pf.set(month_picker, new ColorDrawable(Color.parseColor("#666666")));
                    pf.set(week_picker, new ColorDrawable(Color.parseColor("#666666")));
                    LogUtils.i("设置分割线颜色完毕");
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        for (Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDividerHeight")) {
                pf.setAccessible(true);
                try {
                    //设置分割线的宽度（分割线的粗细）
                    pf.set(year_picker, 1);
                    pf.set(month_picker, 1);
                    pf.set(week_picker, 1);
                    LogUtils.i("设置分割线宽度完毕");
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Resources.NotFoundException e){
                    e.printStackTrace();
                } catch(IllegalAccessException e){
                    e.printStackTrace();
                }
                break;
            }
        }

        if (options.beginCalendar == null) {
            year_picker.setMinValue(1900);
        } else {
            year_picker.setMinValue(options.beginCalendar.get(Calendar.YEAR));
        }
        month_picker.setMinValue(1);
        if (options.endCalendar == null) {
            year_picker.setMaxValue(2100);
            month_picker.setMaxValue(12);
        } else {
            year_picker.setMaxValue(options.endCalendar.get(Calendar.YEAR));
            month_picker.setMaxValue(options.endCalendar.get(Calendar.MONTH) + 1);
        }

        year_picker.setValue(options.calendar.get(Calendar.YEAR));
        //目前不支持仅有【周】这一个选择这种情况
        if (options.type[1] == true) {
            month_picker.setValue(options.calendar.get(Calendar.MONTH) + 1);
            if (options.type[2] == true) {
                week_picker.setMinValue(1);
                if (options.endCalendar == null) {
                    week_picker.setMaxValue(options.calendar.getActualMaximum(Calendar.WEEK_OF_MONTH));
                } else {
                    week_picker.setMaxValue(options.endCalendar.get(Calendar.WEEK_OF_MONTH));
                }
                week_picker.setValue(options.calendar.get(Calendar.WEEK_OF_MONTH));
            }
        } else if (options.type[0] == true) {
            if (options.type[2] == true) {
                week_picker.setMinValue(1);
                if (options.endCalendar == null) {
                    week_picker.setMaxValue(options.calendar.getActualMaximum(Calendar.WEEK_OF_YEAR));
                } else {
                    week_picker.setMaxValue(options.endCalendar.get(Calendar.WEEK_OF_YEAR));
                }
                week_picker.setValue(options.calendar.get(Calendar.WEEK_OF_YEAR));
            }
        }

        cl_title.setBackgroundColor(options.titleBgColor);
        tv_cancel.setTextColor(options.cancelBtnColor);
        tv_confirm.setTextColor(options.confirmBtnColor);

        if (!TextUtils.isEmpty(options.cancelBtnText)) {
            tv_cancel.setText(options.cancelBtnText);
        }
        if (!TextUtils.isEmpty(options.confirmBtnText)) {
            tv_confirm.setText(options.confirmBtnText);
        }

        tv_cancel.setOnClickListener(this);
        tv_confirm.setOnClickListener(this);

        if (options.type.length != 3) {
            throw new RuntimeException("type[] length is not 3");
        }

        year_picker.setVisibility(options.type[0] ? View.VISIBLE : View.GONE);
        year_unit.setVisibility(options.type[0] ? View.VISIBLE : View.GONE);
        month_picker.setVisibility(options.type[1] ? View.VISIBLE : View.GONE);
        month_unit.setVisibility(options.type[1] ? View.VISIBLE : View.GONE);
        week_picker.setVisibility(options.type[2] ? View.VISIBLE : View.GONE);
        week_unit.setVisibility(options.type[2] ? View.VISIBLE : View.GONE);

        year_picker.setOnScrollListener(this);
        month_picker.setOnScrollListener(this);
        week_picker.setOnScrollListener(this);
    }


    @Override

    public void onClick(View v) {
        if (v.getId() == R.id.tv_cancel) {
            dismiss();
        } else if (v.getId() == R.id.tv_confirm) {
            dismiss();
            if (options.dateSelectListener != null) {
                int year = options.calendar.get(Calendar.YEAR);
                int month = 0;
                int week = 0;
                if (options.type[1] == true) {
                    month = options.calendar.get(Calendar.MONTH) + 1;
                    week = options.calendar.get(Calendar.WEEK_OF_MONTH);
                } else if (options.type[0] == true) {
                    week = options.calendar.get(Calendar.WEEK_OF_YEAR);
                }
                if (options.type[0] == true) {
                    year = year_picker.getValue();
                }
                if (options.type[1] == true) {
                    month = month_picker.getValue();
                }
                if (options.type[2] == true) {
                    week = week_picker.getValue();
                }

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                if (month > 0) {
                    calendar.set(Calendar.MONTH, month - 1);
                }

                if (week > 0) {
                    if (options.type[1] == true) {
                        calendar.set(Calendar.WEEK_OF_MONTH, week);
                    } else if (options.type[0] == true) {
                        calendar.set(Calendar.WEEK_OF_YEAR, week);
                    }
                }
                options.dateSelectListener.onDateSelect(calendar);
            }
        }
    }

    @Override
    public void onScrollStateChange(NumberPicker view, int scrollState) {
        if (scrollState == NumberPicker.OnScrollListener.SCROLL_STATE_IDLE) {
            if (view.getId() == R.id.year_picker) {
                /**
                 * 年滚动后，周的重新设置
                 */
                if (options.type[2] == true) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR, year_picker.getValue());
                    if (options.type[1] == true) {
                        calendar.set(Calendar.MONTH, month_picker.getValue() - 1);
                        if (options.endCalendar != null
                                && (year_picker.getValue() == options.endCalendar.get(Calendar.YEAR))
                                && (month_picker.getValue() - 1) == options.endCalendar.get(Calendar.MONTH)) {
                            week_picker.setMaxValue(options.endCalendar.get(Calendar.WEEK_OF_MONTH));
                        } else {
                            week_picker.setMaxValue(calendar.getActualMaximum(Calendar.WEEK_OF_MONTH));
                        }
                    } else {
                        if (options.endCalendar != null
                                && year_picker.getValue() == options.endCalendar.get(Calendar.YEAR)) {
                            week_picker.setMaxValue(options.endCalendar.get(Calendar.WEEK_OF_YEAR));
                        } else {
                            week_picker.setMaxValue(calendar.getActualMaximum(Calendar.WEEK_OF_YEAR));
                        }
                    }
                }
                /**
                 * 年滚动后，月的重新设置
                 */
                if (options.type[1] == true) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR, year_picker.getValue());
                    if (options.endCalendar != null
                            && year_picker.getValue() == options.endCalendar.get(Calendar.YEAR)) {
                        month_picker.setMaxValue(options.endCalendar.get(Calendar.MONTH) + 1);
                    } else {
                        month_picker.setMaxValue(calendar.getActualMaximum(Calendar.MONTH) + 1);
                    }
                }
            } else if (view.getId() == R.id.month_picker) {
                /**
                 * 月滚动后，周的重新设置
                 */
                if (options.type[2] == true) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR, year_picker.getValue());
                    calendar.set(Calendar.MONTH, month_picker.getValue() - 1);
                    if (options.endCalendar != null
                            && year_picker.getValue() == options.endCalendar.get(Calendar.YEAR)
                            && (month_picker.getValue() - 1) == options.calendar.get(Calendar.MONTH)) {
                        week_picker.setMaxValue(options.endCalendar.get(Calendar.WEEK_OF_MONTH));
                    } else {
                        week_picker.setMaxValue(calendar.getActualMaximum(Calendar.WEEK_OF_MONTH));
                    }
                }
            }
        }
    }
}
