package com.zdj.systemfuncationlibrary;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import androidx.core.app.NotificationManagerCompat;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

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
     * @param context  上下文环境
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
     * @param context  上下文环境
     * @param packageName  包名
     */
    public static void goToAppInfo(Context context, String packageName) {
        Intent data = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        data.setData(Uri.fromParts("package", packageName, null));
        context.startActivity(data);
    }

    private static final String HARMONY_OS = "harmony";
    /**
     * 判断是否是鸿蒙系统
     * @return true 是鸿蒙系统  false 不是鸿蒙系统
     */
    public static boolean isHarmonyOS() {
        try {
            Class clz = Class.forName("com.huawei.system.BuildEx");
            Method method = clz.getMethod("getOsBrand");
            return HARMONY_OS.equals(method.invoke(clz));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static MediaPlayer mediaPlayer = null;

    /**
     * 播放音乐文件
     * @param activity  对应的activity
     * @param raw  音频文件
     * @param isLoop  是否循环播放
     */
    public static void playSound(Activity activity, int raw, boolean isLoop) {
        activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        if (isLoop) {
            mediaPlayer.setLooping(true);
        } else {
            mediaPlayer.setOnCompletionListener(mp -> {
                mp.seekTo(0);
            });
        }

        AssetFileDescriptor file = activity.getResources().openRawResourceFd(raw);
        try {
            mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
            file.close();
            mediaPlayer.setVolume(0.5f, 0.5f);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
            mediaPlayer = null;
        }
    }

    /**
     * 停止播放音乐文件
     */
    public static void stopSound() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer = null;
        }
    }

    /**
     * 获取本机号码
     * 注：
     * 1、本机号码不是100%能够获取到的，这个取决于移动运营商是否把手机号码的数据写入SIM卡。
     * 2、调用该方法需要READ_PHONE_STATE权限（Android 6.0以上的设备上需要动态申请），此处采用
     * @SuppressLint("MissingPermission")
     * 避免报红提示，但是在调用该方法时需要注意权限问题。
     * @param context  上下文环境
     * @return  本机号码
     */
    @SuppressLint("MissingPermission")
    public static String getMobilePhoneNumber(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getLine1Number();
    }

    /**
     * 获取手机移动运营商
     * 注：
     * 1、如果是双卡情况，则获取的是开启移动网络的卡对应的移动运营商。
     * 2、不是100%能够获取到的。
     * @param context  上下文环境
     * @return  手机移动运营商
     */
    public static String getMobileOperator(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String simOperator = tm.getSimOperator();
        String mobileOperator = null;
        if (simOperator != null) {
            if (simOperator.equals("46000") || simOperator.equals("46002") || simOperator.equals("46007")) {
                mobileOperator = "中国移动";
            } else if (simOperator.equals("46001") || simOperator.equals("46006") || simOperator.equals("46009")) {
                mobileOperator = "中国联通";
            } else if (simOperator.equals("46003")) {
                mobileOperator = "中国电信";
            }
        }
        return mobileOperator;
    }

    /**
     * 将APP切换至前台
     * @param context  上下文环境
     */
    @SuppressLint("MissingPermission")
    public static void moveAppToTheFront(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfoList = activityManager.getRunningTasks(100);
        for (int i = 0; i < taskInfoList.size(); i++) {
            if (taskInfoList.get(i).topActivity.getPackageName().equals(context.getPackageName())) {
                activityManager.moveTaskToFront(taskInfoList.get(i).id, 0);
            }
        }
    }

    /**
     * 判断应用通知权限是否开启
     * @param context  上下文环境
     * @return  true: 开启  false: 未开启
     */
    public static boolean isNotificationEnabled(Context context) {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        return notificationManagerCompat.areNotificationsEnabled();
    }
}
