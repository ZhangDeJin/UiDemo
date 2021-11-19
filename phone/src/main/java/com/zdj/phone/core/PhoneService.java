package com.zdj.phone.core;

import android.content.Intent;
import android.telecom.Call;
import android.telecom.InCallService;

import com.zdj.phone.Config;
import com.zdj.phone.ScreenActivity;
import com.zdj.systemfuncationlibrary.LogUtils;

/**
 * <pre>
 *     author : dejinzhang
 *     time : 2021/09/10
 *     desc : 服务
 * </pre>
 */
public class PhoneService extends InCallService {
    private static final String TAG = "PhoneService";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private Call.Callback callback = new Call.Callback() {
        @Override
        public void onStateChanged(Call call, int state) {
            super.onStateChanged(call, state);
            LogUtils.i(TAG, "state:" + state);
            if (state == Call.STATE_ACTIVE) {
                LogUtils.i(TAG, "----接听一个电话----" + call.toString());
                //在这里可以发送广播，通知通话界面变化
                Intent intent = new Intent();
                intent.setAction(ScreenActivity.CALL_NET_ING);
                sendBroadcast(intent);
            } else if (state == Call.STATE_DISCONNECTED) {
                LogUtils.i(TAG, "---挂断一个电话----" + call.toString());
                //在这里可以发送广播，通知通话界面变化
                Intent intent = new Intent();
                intent.setAction(ScreenActivity.CALL_NET_END);
                sendBroadcast(intent);
//                call.unregisterCallback(callback);  //其实这句代码也可以注释掉，因为当连接断开时，即电话挂断的时候，会在进入这个监听后，再回调onCallRemoved，我们在onCallRemoved中取消注册回调即可
            }/* else if (state == Call.STATE_CONNECTING) {
                LogUtils.i(TAG, "----呼叫，正在建立连接----" + call.toString());
                //我们也可以根据业务在这里执行相应动作。
            }*/
        }
    };

    @Override
    public void onCallAdded(Call call) {
        super.onCallAdded(call);
        LogUtils.i(TAG, "----添加一个电话----");
        call.registerCallback(callback);  //注册回调
        Call.Details details = call.getDetails();
        String phoneNumber = "";
        int state;
        if (details != null && details.getHandle() != null) {
            phoneNumber = details.getHandle().getSchemeSpecificPart();
        }
        state = call.getState();
        LogUtils.i(TAG, "phoneNumber:" + phoneNumber);
        LogUtils.i(TAG, "callState:" + state);
        Config.call = call;

        //在这里可以跳转到通话界面
        Intent intent = new Intent();
        intent.setClass(this, ScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        intent.putExtra("phoneNum", phoneNumber);
        if (state == Call.STATE_RINGING) {
            intent.putExtra("from", ScreenActivity.NET_IN);
        } else if (state == Call.STATE_CONNECTING) {
            intent.putExtra("from", ScreenActivity.NET_OUT);
        }
        startActivity(intent);
    }

    @Override
    public void onCallRemoved(Call call) {
        super.onCallRemoved(call);
        LogUtils.i(TAG, "----移除一个电话----" + call.toString());
        call.unregisterCallback(callback);
    }
}
