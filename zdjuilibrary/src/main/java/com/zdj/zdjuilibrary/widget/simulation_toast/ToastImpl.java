package com.zdj.zdjuilibrary.widget.simulation_toast;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;
import android.widget.Toast;

import com.zdj.zdjuilibrary.widget.simulation_toast.config.IToast;

/**
 * <pre>
 *     author : dejinzhang
 *     time : 2021/11/01
 *     desc : 自定义Toast实现类
 * </pre>
 */
public class ToastImpl {
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());

    //短吐司显示的时长
    private static final int SHORT_DURATION_TIMEOUT = 2000;
    //长吐司显示的时长
    private static final int LONG_DURATION_TIMEOUT = 3500;

    /**
     * 当前的吐司对象
     */
    private IToast mToast;

    /**
     * WindowManager辅助类
     */
    private WindowLifecycle mWindowLifecycle;

    /**
     * 当前应用的包名
     */
    private String mPackageName;

    /**
     * 当前是否已显示
     */
    private boolean mShow;

    public ToastImpl(Activity activity, IToast toast) {
        mToast = toast;
        mPackageName = activity.getPackageName();
        mWindowLifecycle = new WindowLifecycle(activity);
    }

    boolean isShow() {
        return mShow;
    }

    void setShow(boolean show) {
        mShow = show;
    }

    void show() {
        if (isShow()) {
            return;
        }
        HANDLER.removeCallbacks(mShowRunnable);
        HANDLER.post(mShowRunnable);
    }

    void cancel() {
        if (!isShow()) {
            return;
        }
        //移除之前移除吐司的任务
        HANDLER.removeCallbacks(mCancelRunnable);
        HANDLER.post(mCancelRunnable);
    }

    private final Runnable mShowRunnable = new Runnable() {
        @Override
        public void run() {
            Activity activity = mWindowLifecycle.getActivity();
            if (activity == null || activity.isFinishing()) {
                return;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed()) {
                return;
            }

            WindowManager windowManager = activity.getWindowManager();
            if (windowManager == null) {
                return;
            }

            final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
            params.format = PixelFormat.TRANSLUCENT;
            params.windowAnimations = android.R.style.Animation_Toast;
            params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
            params.packageName = mPackageName;
            params.gravity = mToast.getGravity();
            params.x = mToast.getXOffset();
            params.y = mToast.getYOffset();
            params.verticalMargin = mToast.getVerticalMargin();
            params.horizontalMargin = mToast.getHorizontalMargin();

            try {
                windowManager.addView(mToast.getView(), params);
                //添加一个移除吐司的任务
                HANDLER.postDelayed(() -> cancel(), mToast.getDuration() == Toast.LENGTH_LONG ? LONG_DURATION_TIMEOUT : SHORT_DURATION_TIMEOUT);
                //注册生命周期管控
                mWindowLifecycle.register(ToastImpl.this);
                //当前已经显示
                setShow(true);
            } catch (IllegalStateException | WindowManager.BadTokenException e) {
                /**
                 * 如果这个View对象被重复添加到WindowManager则会抛出异常
                 * java.lang.IllegalStateException: View android.widget.TextView has already been added to the window manager.
                 * 如果WindowManager绑定的Activity已经销毁，则会抛出异常
                 * android.view.WindowManager$BadTokenException: Unable to add window -- token android.os.BinderProxy@eff1cb6 is not valid; is your activity running?
                 */
                e.printStackTrace();
            }
        }
    };

    private final Runnable mCancelRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                Activity activity = mWindowLifecycle.getActivity();
                if (activity == null) {
                    return;
                }

                WindowManager windowManager = activity.getWindowManager();
                if (windowManager == null) {
                    return;
                }

                windowManager.removeViewImmediate(mToast.getView());
            } catch (IllegalArgumentException e) {
                /**
                 * 如果当前WindowManager没有附加这个View则会抛出异常
                 * java.lang.IllegalArgumentException: View android.widget.TextView not attached to window manager
                 */
                e.printStackTrace();
            } finally {
                //取消注册生命周期管控
                mWindowLifecycle.unregister();
                //当前没有显示
                setShow(false);
            }
        }
    };
}
