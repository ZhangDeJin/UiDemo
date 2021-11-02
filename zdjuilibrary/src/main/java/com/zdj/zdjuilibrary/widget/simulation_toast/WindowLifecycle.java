package com.zdj.zdjuilibrary.widget.simulation_toast;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * <pre>
 *     author : dejinzhang
 *     time : 2021/11/01
 *     desc : WindowManager 生命周期管控
 * </pre>
 */
public class WindowLifecycle implements Application.ActivityLifecycleCallbacks{
    /**
     * 当前Activity对象
     */
    private Activity mActivity;

    /**
     * 自定义Toast实现类
     */
    private ToastImpl mToastImpl;

    WindowLifecycle(Activity activity) {
        mActivity = activity;
    }

    /**
     * 获取Activity
     */
    public Activity getActivity() {
        return mActivity;
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {}

    @Override
    public void onActivityStarted(@NonNull Activity activity) {}

    @Override
    public void onActivityResumed(@NonNull Activity activity) {}

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        if (mActivity != activity) {
            return;
        }

        if (mToastImpl == null) {
            return;
        }

        /**
         * 不能放在onStop或者onDestroyed方法中，因为此时新的Activity已经创建完成，必须在这个新的Activity未创建之前关闭这个WindowManager。
         * 否则调用取消显示会直接导致新的Activity的onCreate调用显示吐司可能显示不出来的问题，又或者有时候会立马显示然后立马消失的那种效果。
         */
        mToastImpl.cancel();
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {}

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        if (mActivity != activity) {
            return;
        }
        if (mToastImpl != null) {
            mToastImpl.cancel();
        }
        unregister();
        mActivity = null;
    }

    /**
     * 注册
     */
    void register(ToastImpl impl) {
        mToastImpl = impl;
        if (mActivity == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mActivity.registerActivityLifecycleCallbacks(this);
        } else {
            mActivity.getApplication().registerActivityLifecycleCallbacks(this);
        }
    }

    /**
     * 取消注册
     */
    void unregister() {
        mToastImpl = null;
        if (mActivity == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mActivity.unregisterActivityLifecycleCallbacks(this);
        } else {
            mActivity.getApplication().unregisterActivityLifecycleCallbacks(this);
        }
    }
}
