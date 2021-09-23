package com.zdj.systemfuncationlibrary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.Settings;

import java.io.IOException;
import java.lang.reflect.Method;

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
}
