package com.zdj.systemfuncationlibrary;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

/**
 * <pre>
 *     author : dejinzhang
 *     time : 2021/04/25
 *     desc : 系统工具类
 * </pre>
 */
public class SystemUtils {
    /**
     * 调用系统email发送邮件
     * @param context
     * @param email  接受者邮箱
     * @param params  可变参数，最多两个，依次为邮件标题、邮件内容
     */
    public static void sendEmail(Context context, String email, String... params) {
        Intent data = new Intent(Intent.ACTION_SENDTO);
        data.setData(Uri.parse(new StringBuilder().append("mailto:").append(email).toString()));
        if (params != null && params.length > 0) {
            data.putExtra(Intent.EXTRA_SUBJECT, params[0]);
            if (params.length > 1) {
                data.putExtra(Intent.EXTRA_TEXT, params[1]);
            }
        }
        context.startActivity(data);
    }

    /**
     * 跳转至应用信息界面
     * @param context
     * @param packageName
     */
    public static void goToAppInfo(Context context, String packageName) {
        Intent data = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        data.setData(Uri.fromParts("package", packageName, null));
        context.startActivity(data);
    }
}
