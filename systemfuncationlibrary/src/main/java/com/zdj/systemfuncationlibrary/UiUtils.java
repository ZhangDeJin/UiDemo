package com.zdj.systemfuncationlibrary;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListAdapter;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * <pre>
 *     author : dejinzhang
 *     time : 2021/04/25
 *     desc : UI工具类
 * </pre>
 */
public class UiUtils {
    /**
     * 设置透明状态栏
     * @param activity  对应的activity组件
     * @param dark  高亮显示状态栏文字、图标
     */
    public static void setTranslucentStatus(Activity activity, boolean dark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = activity.getWindow().getDecorView();
            int option;
            if (dark) {
                option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else {
                option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            }
            decorView.setSystemUiVisibility(option);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window win = activity.getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            winParams.flags |= bits;
            win.setAttributes(winParams);
        }
    }

    /**
     * Google原生方式设置状态栏高亮
     * @param activity  对应的activity组件
     */
    public static void setAndroidNativeLightStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = activity.getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

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
     * 单位转换：px--->dp
     * @param context  上下文环境
     * @param px  原始单位
     * @return  返回转换后的单位
     */
    public static int pxToDp(Context context, float px) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(px / scale + 0.5f);
    }

    /**
     * 获取手机屏幕的宽度
     * @param context  上下文环境
     * @return  返回屏幕宽度
     */
    public static int getWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取手机屏幕的高度
     * @param context  上下文环境
     * @return  返回屏幕高度
     */
    public static int getHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取手机状态栏的高度
     * @param context  上下文环境
     * @return  返回状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = -1;
        //获取status_bar_height资源的ID
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    /**
     * 获取手机底部导航栏的高度
     * @param context  上下文环境
     * @return  返回底部导航栏高度
     */
    public static int getNavigationBarHeight(Context context) {
        int navigationBarHeight = -1;
        //获取navigation_bar_height资源的ID
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            navigationBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        return navigationBarHeight;
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

    /**
     * 设置GridView的高度
     * @param gridView  gridView控件
     * @param numColumn  每行的数目
     * @param padding  内间距
     */
    public static void setGridViewHeight(GridView gridView, int numColumn, int padding) {
        ListAdapter listAdapter = gridView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            if (i % numColumn == 0) {
                View listItem = listAdapter.getView(i, null, gridView);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
        }
        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        if (listAdapter.getCount() % numColumn == 0) {
            params.height = totalHeight + (gridView.getVerticalSpacing() * (listAdapter.getCount() / numColumn - 1)) + padding;
        } else {
            params.height = totalHeight + (gridView.getVerticalSpacing() * (listAdapter.getCount() / numColumn)) + padding;
        }
        gridView.setLayoutParams(params);
    }

    /**
     * 专门为带+号的GridView动态设置高度
     * @param gridView  gridView控件
     * @param numColumn  每行的数目
     * @param padding  内间距
     * @param maxCount  最大数量
     */
    public static void setContainAddGridViewHeight(GridView gridView, int numColumn, int padding, int maxCount) {
        ListAdapter listAdapter = gridView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            if (i % numColumn == 0) {
                View listItem = listAdapter.getView(i, null, gridView);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
        }
        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        int count = listAdapter.getCount();
        if (count == maxCount) {
            count--;
        }
        if (count % numColumn == 0) {
            params.height = totalHeight + (gridView.getVerticalSpacing() * (count / numColumn - 1)) + padding;
        } else {
            params.height = totalHeight + (gridView.getVerticalSpacing() * (count / numColumn)) + padding;
        }
        gridView.setLayoutParams(params);
    }

    /**
     * 格式化单位（从B、KB、MB、GB一直到TB应有尽有，根据具体的大小显示合适的单位）
     * @param size  大小
     * @return  格式化后的单位
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return (int)size + "B";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);

        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }

    /**
     * 格式化单位（只限定两种：KB、MB）
     * @param size  大小
     * @return  格式化后的单位
     */
    public static String getFormatSize2(long size) {
        double megaSize = size / (1024d * 1024d);
        if (megaSize >= 1) {
            return new StringBuilder().append(new DecimalFormat("0.0").format(megaSize)).append("MB").toString();
        } else {
            double kiloByte = size / 1024d;
            return new StringBuilder().append(new DecimalFormat("0.0").format(kiloByte)).append("KB").toString();
        }
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
