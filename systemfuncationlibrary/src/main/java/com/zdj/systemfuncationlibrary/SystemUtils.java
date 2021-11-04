package com.zdj.systemfuncationlibrary;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.view.DisplayCutout;
import android.view.View;
import android.view.WindowInsets;

import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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

    /**
     * 获取指定文件夹内所有文件大小的和
     * @param file  指定文件夹（文件夹是一种特殊的文件）
     * @return  大小
     */
    public static long getFolderSize(File file) {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (File aFileList: fileList) {
                if (aFileList.isDirectory()) {
                    size = size + getFolderSize(aFileList);
                } else {
                    size = size + aFileList.length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 判断是否为异型屏（刘海屏、水滴屏等等）
     * @param context  上下文环境
     * @return  true: 是  false: 不是
     */
    public static boolean isNotch(Context context) {
        if ((isAndroidPNotch(context) != null) || isNotchOfHuawei(context) || isNotchOfXiaomi(context)
                || isNotchOfOppo(context) || isNotchOfVivo(context)) {
            return true;
        }
        return false;
    }

    /**
     * Android P(9.0)以上，Google官方正式支持异型屏，所以在Android P以上，采用Google官方api进行判断
     * @param context  上下文环境
     * @return  DisplayCutout（确定非功能区域的位置和形状），如果得到该对象，则表示是异型屏
     */
    public static DisplayCutout isAndroidPNotch(Context context) {
        View decorView = ((Activity)context).getWindow().getDecorView();
        if (decorView != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowInsets windowInsets = decorView.getRootWindowInsets();
            if (windowInsets != null) {
                return windowInsets.getDisplayCutout();
            }
        }
        return null;
    }

    /**
     * 华为异型屏判断
     * @param context  上下文环境
     * @return  true: 是  false: 不是
     */
    public static boolean isNotchOfHuawei(Context context) {
        boolean isNotch = false;
        try {
            if ("huawei".equals(Build.BRAND.toLowerCase())) {
                ClassLoader classLoader = context.getClassLoader();
                Class cls = classLoader.loadClass("com.huawei.android.util.HwNotchSizeUtil");
                Method method = cls.getMethod("hasNotchInScreen");
                isNotch = (boolean) method.invoke(cls);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } finally {
            return isNotch;
        }
    }

    /**
     * 小米异型屏判断
     * @param context  上下文环境
     * @return  true: 是  false: 不是
     */
    public static boolean isNotchOfXiaomi(Context context) {
        boolean isNotch = false;
        try {
            if ("xiaomi".equals(Build.BRAND.toLowerCase())) {
                ClassLoader classLoader = context.getClassLoader();
                Class cls = classLoader.loadClass("android.os.SystemProperties");
                Method method = cls.getMethod("getInt", String.class, int.class);
                isNotch = ((int) method.invoke(null, "ro.miui.notch", 0) == 1);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }finally {
            return isNotch;
        }
    }

    /**
     * OPPO异型屏判断
     * @param context
     * @return
     */
    public static boolean isNotchOfOppo(Context context) {
        boolean isNotch = false;
        try {
            if ("oppo".equals(Build.BRAND.toLowerCase())) {
                isNotch = context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return isNotch;
        }
    }

    /**
     * VIVO异型屏判断
     * @param context  上下文环境
     * @return  true: 是  false: 不是
     */
    public static boolean isNotchOfVivo(Context context) {
        boolean isNotch = false;
        try {
            if ("vivo".equals(Build.BRAND.toLowerCase())) {
                ClassLoader classLoader = context.getClassLoader();
                Class cls = classLoader.loadClass("android.util.FtFeature");
                Method method = cls.getMethod("isFeatureSupport", int.class);
                isNotch = (boolean) method.invoke(cls, 0x00000020);  //0x00000020: 是否有刘海  0x00000008: 是否有圆角
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } finally {
            return isNotch;
        }
    }

    /**
     * 获取华为异型屏异型（刘海、水滴等等）的宽度和高度：int[0]值为宽度，int[1]值为高度
     * @param context  上下文环境
     * @return  异型的宽度和高度组成的数组
     */
    public static int[] getNotchSizeOfHuawei(Context context) {
        int[] notchSize = new int[]{0, 0};
        try {
            ClassLoader classLoader = context.getClassLoader();
            Class cls = classLoader.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method method = cls.getMethod("getNotchSize");
            notchSize = (int[]) method.invoke(cls);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } finally {
            return notchSize;
        }
    }

    /**
     * 获取小米异型屏型（刘海、水滴等等）的宽度和高度：int[0]值为宽度，int[1]值为高度
     * @param context  上下文环境
     * @return  异型的宽度和高度组成的数组
     */
    public static int[] getNotchSizeOfXiaomi(Context context) {
        int[] notchSize = new int[]{0, 0};
        int resourceWidthId = context.getResources().getIdentifier("notch_width", "dimen", "android");
        if (resourceWidthId > 0) {
            notchSize[0] = context.getResources().getDimensionPixelSize(resourceWidthId);
        }
        int resourceHeightId = context.getResources().getIdentifier("notch_height", "dimen", "android");
        if (resourceHeightId > 0) {
            notchSize[1] = context.getResources().getDimensionPixelSize(resourceHeightId);
        }
        return notchSize;
    }

    /**
     * 获取小米手机额外高度（小米获取屏幕高度不准确）
     * @param context  上下文环境
     * @return  额外高度
     */
    public static int getMiSupplementHeight(Context context) {
        int result = 0;
        //是否是小米系统，不是小米系统则不需要补充高度
        if ("xiaomi".equals(Build.BRAND.toLowerCase())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1
                    && Settings.Global.getInt(context.getContentResolver(), "force_fsg_nav_bar", 0) == 0) {
                //如果虚拟按键已经显示，则不需要补充高度
            } else {
                //如果虚拟按键没有显示，则需要补充虚拟按键高度到屏幕高度
                Resources res = context.getResources();
                int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
                if (resourceId > 0) {
                    result = res.getDimensionPixelSize(resourceId);
                }
            }
        }
        return result;
    }

    /**
     * 判断手机上是否安装了指定应用程序
     * @param context  上下文环境
     * @param packageName  指定应用程序的包名
     * @return  true: 安装了  false: 没有安装
     */
    public static boolean appInstalled(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                if (packageName.equals(packageInfos.get(i).packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断定位服务是否开启
     * @param context  上下文环境
     * @return  true: 开启了  false: 未开启
     */
    public static boolean isLocationServiceEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return gps || network;
    }

    /**
     * 获取本机默认电话程序的包名
     * @param context  上下文环境
     * @return  本机默认电话程序的包名
     */
    public static String getDefaultPhoneAppPackage(Context context) {
        String defaultDialerPackage = "";
        TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (telecomManager.getDefaultDialerPackage() != null) {
                defaultDialerPackage = telecomManager.getDefaultDialerPackage();
            }
        }
        return defaultDialerPackage;
    }

    public static Intent getOpenIntent(String type, File file, Context context, String authority) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context, authority, file);
        } else {
            uri = Uri.fromFile(file);
        }
        switch (type) {
            case "jgp":
            case "jpeg":
            case "gif":
            case "png":
            case "bmp":
                intent.setDataAndType(uri, "image/*");
                break;
            case "mp3":
                intent.setDataAndType(uri, "audio/*");
                break;
            case "mp4":
                intent.setDataAndType(uri, "video/*");
                break;
            case "txt":
                intent.setDataAndType(uri, "text/plain");
                break;
            case "pdf":
                intent.setDataAndType(uri, "application/pdf");
                break;
            case "doc":
            case "docx":
                intent.setDataAndType(uri, "application/msword");
                break;
            case "xls":
            case "xlsx":
                intent.setDataAndType(uri, "application/vnd.ms-excel");
                break;
            case "ppt":
            case "pptx":
                intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
                break;
            case "rar":
                intent.setDataAndType(uri, "application/x-rar-compressed");
                break;
            case "zip":
                intent.setDataAndType(uri, "application/zip");
                break;
        }
        return intent;
    }
}
