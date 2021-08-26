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
    public static int dpToPx(Context context, float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }

    public static int getHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static void setEditTextHintTextSize(String hintText, int size, EditText editText) {
        SpannableString spannableString = new SpannableString(hintText);
        AbsoluteSizeSpan absoluteSizeSpan = new AbsoluteSizeSpan(size, true);
        spannableString.setSpan(absoluteSizeSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        editText.setHint(new SpannableString(spannableString));
    }
}
