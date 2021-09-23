package com.zdj.systemfuncationlibrary;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * <pre>
 *     author : dejinzhang
 *     time : 2021/09/22
 *     desc : 吐司工具类
 * </pre>
 */
public class ToastUtils {
    /**
     * 短时间显示Toast
     * @param context  上下文环境
     * @param message  显示的内容
     */
    public static void showShort(Context context, CharSequence message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 居中显示吐司
     * @param context  上下文环境
     * @param message  显示的内容
     * @param duration  显示时长
     */
    public static void showCenterToast(Context context, CharSequence message, int duration) {
        Toast toast = Toast.makeText(context, message, duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * 自定义Toast的View
     * @param context  上下文环境
     * @param message  显示的内容
     * @param duration  显示时长
     * @param tagId  图标标签的资源id （比如说成功、失败等等，为0时，表示无）
     */
    public static void customToastView(Context context, CharSequence message, int duration, int tagId) {
        Toast toast = Toast.makeText(context, message, duration);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_custom_toast, null);
        TextView tv_content = view.findViewById(R.id.tv_content);
        tv_content.setText(message);
        tv_content.setCompoundDrawablesRelativeWithIntrinsicBounds(0, tagId, 0, 0);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
