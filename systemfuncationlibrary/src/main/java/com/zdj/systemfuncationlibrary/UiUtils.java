package com.zdj.systemfuncationlibrary;

import android.content.Context;

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
}
