package com.zdj.systemfuncationlibrary;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.widget.EditText;

/**
 * <pre>
 *     author : dejinzhang
 *     time : 2021/04/25
 *     desc : UI工具类
 * </pre>
 */
public class UiUtils {
    /**
     * 单位转换：dp--->px
     * @param context  上下文环境
     * @param dp  原始单位
     * @return  返回转换后的单位
     */
    public static int dpToPx(Context context, float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }

    /**
     * 获取屏幕高度
     * @param context  上下文环境
     * @return  返回屏幕高度
     */
    public static int getHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 设置EditText提示文字的大小
     * @param hintText  提示文字
     * @param size  大小
     * @param editText  editText
     */
    public static void setEditTextHintTextSize(String hintText, int size, EditText editText) {
        SpannableString spannableString = new SpannableString(hintText);
        AbsoluteSizeSpan absoluteSizeSpan = new AbsoluteSizeSpan(size, true);
        spannableString.setSpan(absoluteSizeSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        editText.setHint(new SpannableString(spannableString));
    }

    //两次点击间隔不能少于1000ms
    private static final int FAST_CLICK_DELAY_TIME = 1000;
    //上次点击的时间
    private static long lastClickTime;

    /**
     * 判断是否为快速点击
     * @return  返回是否为快速点击
     */
    public static boolean isFastClick() {
        boolean flag = true;
        long currentClickTime = System.currentTimeMillis();
        if ((currentClickTime - lastClickTime) >= FAST_CLICK_DELAY_TIME) {
            flag = false;
        }
        lastClickTime = currentClickTime;
        return flag;
    }

    /**
     * 小于10的数字前面自动加0（常用于时间、日期）
     * @param value  原始数据
     * @return  填充后的数据
     */
    public static String formatAA(int value) {
        return String.format("%02d", value);
    }

    /**
     * 号码脱敏处理
     * @param phoneNum  原始号码
     * @return 脱敏后的号码
     */
    public static String desensitizePhoneNum(String phoneNum) {
        if (MatchUtils.isLegalMobilePhoneNum(phoneNum)) {  //手机号码脱敏处理
            return phoneNum.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        } else if (phoneNum.length() == 5) {  //银行、10086等号码脱敏处理
            return phoneNum.replaceAll("(\\d{1})\\d{3}(\\d{1})", "$1***$2");
        } else if (phoneNum.length() == 10) {  //400号码脱敏处理
            return phoneNum.replaceAll("(\\d{3})\\d{4}(\\d{3})", "$1****$2");
        } else if (phoneNum.length() == 11) {  //固话脱敏处理
            return phoneNum.replaceAll("(\\d{4})\\d{4}(\\d{3})", "$1****$2");
        } else if (phoneNum.length() == 12) {  //固话脱敏处理
            return phoneNum.replaceAll("(\\d{4})\\d{4}(\\d{4})", "$1****$2");
        } else {
            return "*****";
        }
    }
}
