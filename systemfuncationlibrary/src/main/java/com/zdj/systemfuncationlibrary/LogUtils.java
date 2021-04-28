package com.zdj.systemfuncationlibrary;

import android.util.Log;

/**
 * <pre>
 *     author : dejinzhang
 *     time : 2021/04/28
 *     desc : 日志工具类
 * </pre>
 */
public class LogUtils {
    public static boolean isPrintLog = true;
    private final static String APP_TAG = "UiDemo";

    /**
     * 获取相关数据：类名，方法名，行号等
     * @return
     */
    private static String getFunctionName() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        if (sts != null) {
            for (int i = 0; i < sts.length; i++) {
                if (sts[i].isNativeMethod()) {
                    continue;
                }
                if (sts[i].getClassName().equals(Thread.class.getName())) {
                    continue;
                }
                if (sts[i].getClassName().equals(LogUtils.class.getName())) {
                    continue;
                }
                return "[Thread:]" + Thread.currentThread().getName() + ", at " + sts[i].getClassName() + "." + sts[i].getMethodName()
                        + "(" + sts[i].getFileName() + ":" + sts[i].getLineNumber() + ")" + " ]";
            }
        }
        return null;
    }

    /**
     * 定义输出
     * @param msg
     * @return
     */
    private static String getMsgFormat(String msg) {
        return msg + ";" + getFunctionName();
    }

    public static void v(String msg) {
        if (isPrintLog) {
            Log.v(APP_TAG, getMsgFormat(msg));
        }
    }

    public static void v(String tag, String msg) {
        if (isPrintLog) {
            Log.v(tag, getMsgFormat(msg));
        }
    }

    public static void d(String msg) {
        if (isPrintLog) {
            Log.d(APP_TAG, getMsgFormat(msg));
        }
    }

    public static void d(String tag, String msg) {
        if (isPrintLog) {
            Log.d(tag, getMsgFormat(msg));
        }
    }

    public static void i(String msg) {
        if (isPrintLog) {
            Log.i(APP_TAG, getMsgFormat(msg));
        }
    }

    public static void i(String tag, String msg) {
        if (isPrintLog) {
            Log.i(tag, msg);
        }
    }

    public static void e(String msg) {
        if (isPrintLog) {
            Log.e(APP_TAG, getMsgFormat(msg));
        }
    }

    public static void e(String tag, String msg) {
        if (isPrintLog) {
            Log.e(tag, msg);
        }
    }
}
