package com.zdj.phone.core;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * <pre>
 *     author : dejinzhang
 *     time : 2021/09/10
 *     desc : 使我们的应用成为一个电话程序必需的组件
 * </pre>
 */
public class PhoneActivity extends Activity implements View.OnClickListener{
    public static int taskId = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskId = getTaskId();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(() -> {
            //要执行的任务
            Message msg = new Message();
            msg.what = 1;
            handler.sendMessage(msg);
        }, 1000);
    }

    private MyHandler handler = new MyHandler();
    private static class MyHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 1) {

            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        taskId = -1;
    }
}
