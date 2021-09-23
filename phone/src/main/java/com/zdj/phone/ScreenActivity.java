package com.zdj.phone;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.zdj.phone.adapter.DialPadAdapter;
import com.zdj.phone.core.PhoneManager;
import com.zdj.systemfuncationlibrary.LogUtils;
import com.zdj.systemfuncationlibrary.MatchUtils;
import com.zdj.systemfuncationlibrary.SystemUtils;
import com.zdj.systemfuncationlibrary.UiUtils;
import com.zdj.zdjuilibrary.popup_window.VolumePopupWindow;


/**
 * <pre>
 *     author : dejinzhang
 *     time : 2021/09/12
 *     desc : 通话界面
 * </pre>
 */
public class ScreenActivity extends Activity implements SensorEventListener {
    private static final String TAG = "ScreenActivity";
    public static final String CALL_NET_END = "call_net_end";
    public static final String CALL_NET_ING = "call_net_ing";
    public static final String NET_IN = "net_in";
    public static final String NET_OUT = "net_out";

    private TextView phoneNumTxt, phoneInfo, inputTxt, statusTxt,
            tv_tel_microphone, tv_tel_receiver, tv_on_speakerphone, tv_dial;
    private LinearLayout ll_no, ll_ok, ll_relevant_operate;
    private GridView gv_dial_pad;

    private VolumePopupWindow volumePopupWindow;  //调节音量的popupWindow

    private String phoneNum = "";
    private String phoneFrom = "";  //标记是呼出，还是呼入，即打电话还是接电话。"net_in"---呼入；"net_out"---呼出。
    private boolean isPhoneHide;  //是否对号码进行脱敏处理
    private boolean isSip;  //是否是sip固话
    private boolean isAxbTwoAutoAnswer;  //是否是axb双呼自动接听
    private boolean isHeadset;  //是否有耳机
    private boolean isRing;  //是否正在响铃
    private boolean isNetting;

    private TelephonyManager telephonyManager;  //电话管理器
    private AudioManager audioManager;  //声音管理器
//    private SensorManager sensorManager;  //传感器管理器
//    private Sensor mProximity;  //传感器实例
    private AudioManager.OnAudioFocusChangeListener mAudioFocusChangeListener;  //更新系统对音频焦点时要调用的回调的接口实例

    private static final int MY_PERMISSIONS_RECORD_AUDIO = 1;

    private final BroadcastReceiver mEndCallReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }
            LogUtils.i(TAG, "intent.getAction()===" + intent.getAction());
            if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
                telephonyManager = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
                switch (telephonyManager.getCallState()) {
                    case TelephonyManager.CALL_STATE_RINGING:  //来电响铃
                        LogUtils.i(TAG, "---来电话了---");
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        LogUtils.i(TAG, "---接听电话---");  //接听电话
                        Message message = Message.obtain();
                        message.what = 1;
                        message.obj = intent.getAction();
                        callbackHandler.sendMessage(message);
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:  //挂断电话
                        LogUtils.i(TAG, "---挂断电话---");
                        Toast.makeText(ScreenActivity.this, getString(R.string.call_ended), Toast.LENGTH_SHORT).show();
                        break;
                }
            } else if (intent.getAction().equals("android.intent.action.HEADSET_PLUG")) {
                if (intent.hasExtra("state")) {
                    if (intent.getIntExtra("state", 0) == 0) {
                        isHeadset = false;
                    } else if (intent.getIntExtra("state", 0) == 1) {
                        isHeadset = true;
                        if (isRing) {
                            return;
                        }
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                            audioManager.setSpeakerphoneOn(false);
                            audioManager.setMode(AudioManager.MODE_IN_CALL);
                        } else {
                            audioManager.setSpeakerphoneOn(false);
                        }
                    }
                }
            } else {
                Message msg = Message.obtain();
                msg.what = 1;
                msg.obj = intent.getAction();
                callbackHandler.sendMessage(msg);
            }
        }
    };

    private final Handler callbackHandler = new CallbackHandler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        mProximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mAudioFocusChangeListener = focusChange -> {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                LogUtils.i(TAG, "---失去焦点---");
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                LogUtils.i(TAG, "---获得焦点---");
            }
        };
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_screen);

        if (getIntent().hasExtra("isPhoneHide")) {
            isPhoneHide = getIntent().getBooleanExtra("isPhoneHide", false);
        }

        if (getIntent().hasExtra("isAxbTwoAutoAnswer")) {
            isAxbTwoAutoAnswer = getIntent().getBooleanExtra("isAxbTwoAutoAnswer", false);
        }

        if (getIntent().hasExtra("from")) {
            phoneFrom = getIntent().getStringExtra("from");
        }

        initView();

        if (getIntent().hasExtra("phoneNum")) {
            phoneNum = getIntent().getStringExtra("phoneNum");
            if (!TextUtils.isEmpty(phoneNum)) {
                if (phoneNum.contains("sip:") && phoneNum.contains("@")) {
                    isSip = true;
                    phoneNum = phoneNum.substring(phoneNum.indexOf(":") + 1, phoneNum.indexOf("@"));
                    if (phoneFrom.equals(NET_IN)) {
                        //调用后端接口获取客户信息并显示在界面上。如果获取不到，表示来电不在库中，此时不显示即可。
                    }
                } else {
                    isSip = false;
                }
            } else {
                isSip = true;  //默认按sip来处理
                phoneNum = "未知号码";
            }
        }

        if (phoneFrom.equals(NET_IN)) {  //呼入
            isRing = true;
            audioManager.requestAudioFocus(mAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            if (!isAxbTwoAutoAnswer) {
                ll_ok.setVisibility(View.VISIBLE);
                ll_no.setVisibility(View.VISIBLE);
                SystemUtils.playSound(this, R.raw.ring, true);  //播放铃声
                callStatus = 2;
            } else {
                ll_no.setVisibility(View.VISIBLE);
                PhoneManager.answer(Config.call);
                callStatus = 3;
                registerReceiver(mEndCallReceiver, new IntentFilter(CALL_NET_END));
                animationHandler.postDelayed(timeRunnable, 1000);
                ll_relevant_operate.setVisibility(View.VISIBLE);
                tv_tel_receiver.setVisibility(View.GONE);
                tv_tel_microphone.setVisibility(View.GONE);
            }
        } else if (phoneFrom.equals(NET_OUT)) {  //呼出
            audioManager.requestAudioFocus(mAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            ll_no.setVisibility(View.VISIBLE);
            ll_relevant_operate.setVisibility(View.VISIBLE);
            callStatus = CONNECTING;
            if (!isSip) {
                tv_tel_receiver.setVisibility(View.GONE);
                tv_tel_microphone.setVisibility(View.GONE);
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (isPhoneHide) {
            if (!phoneNum.equals("未知号码")) {
                stringBuilder.append(UiUtils.desensitizePhoneNum(phoneNum));
            } else {
                stringBuilder.append(phoneNum);
            }
        } else {
            if (MatchUtils.isLegalMobilePhoneNum(phoneNum)) {
                stringBuilder.append(phoneNum.substring(0, 3)).append("-").append(phoneNum.substring(3, 7)).append("-").append(phoneNum.substring(7));
            } else if (phoneNum.startsWith("400") && phoneNum.length() == 10) {
                stringBuilder.append(phoneNum.substring(0, 3)).append("-").append(phoneNum.substring(3, 6)).append("-").append(phoneNum.substring(6));
            } else if (phoneNum.length() > 5) {
                if (phoneNum.startsWith("01") || phoneNum.startsWith("02")) {
                    stringBuilder.append(phoneNum.substring(0, 3)).append("-").append(phoneNum.substring(3));
                } else {
                    stringBuilder.append(phoneNum.substring(0, 4)).append("-").append(phoneNum.substring(4));
                }
            } else {
                stringBuilder.append(phoneNum);
            }
        }
        phoneNumTxt.setText(stringBuilder);

        if (!isAxbTwoAutoAnswer) {
            registerReceiver(mEndCallReceiver, new IntentFilter(CALL_NET_END));
            registerReceiver(mEndCallReceiver, new IntentFilter(CALL_NET_ING));
            if (isSip) {
                registerReceiver(mEndCallReceiver, new IntentFilter("android.intent.action.PHONE_STATE"));
                registerReceiver(mEndCallReceiver, new IntentFilter("android.intent.action.HEADSET_PLUG"));
            }
            animationHandler.postDelayed(timeRunnable, 1000);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_RECORD_AUDIO);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*//注册传感器
        if (sensorManager != null) {
            sensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
        }*/
        //如果是sip，这里要发送CALL_NET_END的广播
    }

    @Override
    protected void onPause() {
        super.onPause();
        /*//取消注册传感器
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }*/
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(mEndCallReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!isAxbTwoAutoAnswer) {
            animationHandler.removeCallbacks(timeRunnable);
        }
        currentTime = 0;
        audioManager.abandonAudioFocus(mAudioFocusChangeListener);  //释放焦点
        //如果是sip的话，这里要进行一些恢复操作
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_RECORD_AUDIO:
                if (!(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this)
                            .setMessage(getString(R.string.microphone_permission_request_hint_text))
                            .setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                                ActivityCompat.requestPermissions(ScreenActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_RECORD_AUDIO);
                            })
                            .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                                if (isSip) {
                                    //调用sip的挂断接口
                                } else {
                                    PhoneManager.hangup(Config.call);
                                }
                            });
                    builder.create().show();
                }
                break;
        }
    }

    private void initView() {
        ll_no = findViewById(R.id.ll_no);
        ll_ok = findViewById(R.id.ll_ok);
        ImageView iv_no = findViewById(R.id.iv_no);
        ImageView iv_ok = findViewById(R.id.iv_ok);
        phoneNumTxt = findViewById(R.id.call_phone_num);
        phoneInfo = findViewById(R.id.phone_info);
        inputTxt = findViewById(R.id.input_text);
        statusTxt = findViewById(R.id.call_status);
        ll_relevant_operate = findViewById(R.id.ll_relevant_operate);
        tv_tel_microphone = findViewById(R.id.tv_tel_microphone);
        tv_tel_receiver = findViewById(R.id.tv_tel_receiver);
        tv_on_speakerphone = findViewById(R.id.tv_on_speakerphone);
        tv_dial = findViewById(R.id.tv_dial);
        gv_dial_pad = findViewById(R.id.gv_dial_pad);
        final DialPadAdapter dialPadAdapter = new DialPadAdapter(this, DialPadAdapter.SCREEN_UI);
        gv_dial_pad.setAdapter(dialPadAdapter);
        gv_dial_pad.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gv_dial_pad.setOnItemClickListener((parent, view, position, id) -> {
            String str = dialPadAdapter.getList().get(position).getMainText();
            String existsText = inputTxt.getText().toString();
            StringBuilder stringBuilder = new StringBuilder();
            if (!TextUtils.isEmpty(existsText)) {
                stringBuilder.append(existsText);
            }
            stringBuilder.append(str);
            inputTxt.setText(stringBuilder);
            inputTxt.setVisibility(View.VISIBLE);
            if (isSip) {
                //调用sip的输入指令的接口
            } else {
                PhoneManager.playDtmfTone(Config.call, str.charAt(0));
            }
        });

        View.OnClickListener clickListener = v -> {
            if (v.getId() == R.id.tv_tel_microphone) {
                tv_tel_microphone.setTag(!((boolean) tv_tel_microphone.getTag()));
                tv_tel_microphone.setTextColor(((boolean) tv_tel_microphone.getTag()) ? Color.parseColor("#D6E3F4") : Color.parseColor("#62696C"));
                tv_tel_microphone.setCompoundDrawablesRelativeWithIntrinsicBounds(0, ((boolean) tv_tel_microphone.getTag()) ? R.drawable.tel_microphone_highlight : R.drawable.tel_microphone, 0, 0);
                if ((boolean)tv_tel_receiver.getTag()) {
                    tv_tel_receiver.setTag(false);
                    tv_tel_receiver.setTextColor(Color.parseColor("#62696C"));
                    tv_tel_receiver.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.tel_receiver, 0, 0);
                    if (volumePopupWindow != null) {
                        volumePopupWindow.dismiss();
                    }
                }
                if ((boolean) tv_tel_microphone.getTag()) {
                    showVolumePopupWindow(tv_tel_microphone);
                } else {
                    if (volumePopupWindow != null) {
                        volumePopupWindow.dismiss();
                    }
                }
            } else if (v.getId() == R.id.tv_tel_receiver) {
                tv_tel_receiver.setTag(!((boolean) tv_tel_receiver.getTag()));
                tv_tel_receiver.setTextColor(((boolean) tv_tel_receiver.getTag()) ? Color.parseColor("#D6E3F4") : Color.parseColor("#62696C"));
                tv_tel_receiver.setCompoundDrawablesRelativeWithIntrinsicBounds(0, ((boolean) tv_tel_receiver.getTag()) ? R.drawable.tel_receiver_highlight : R.drawable.tel_receiver, 0, 0);
                if ((boolean)tv_tel_microphone.getTag()) {
                    tv_tel_microphone.setTag(false);
                    tv_tel_microphone.setTextColor(Color.parseColor("#62696C"));
                    tv_tel_microphone.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.tel_microphone, 0, 0);
                    if (volumePopupWindow != null) {
                        volumePopupWindow.dismiss();
                    }
                }
                if ((boolean)tv_tel_receiver.getTag()) {
                    showVolumePopupWindow(tv_tel_receiver);
                } else {
                    if (volumePopupWindow != null) {
                        volumePopupWindow.dismiss();
                    }
                }
            } else if (v.getId() == R.id.tv_on_speakerphone) {
                tv_on_speakerphone.setTag(!((boolean) tv_on_speakerphone.getTag()));
                tv_on_speakerphone.setTextColor(((boolean) tv_on_speakerphone.getTag()) ? Color.parseColor("#D6E3F4") : Color.parseColor("#62696C"));
                tv_on_speakerphone.setCompoundDrawablesRelativeWithIntrinsicBounds(0, ((boolean) tv_on_speakerphone.getTag()) ? R.drawable.on_speakerphone_highlight : R.drawable.on_speakerphone, 0, 0);
                switchSpeaker((boolean)tv_on_speakerphone.getTag());
            } else if (v.getId() == R.id.tv_dial) {
                tv_dial.setTag(!((boolean)tv_dial.getTag()));
                if ((boolean)tv_dial.getTag()) {
                    gv_dial_pad.setVisibility(View.VISIBLE);
                } else {
                    gv_dial_pad.setVisibility(View.GONE);
                }
            }
        };
        tv_tel_microphone.setOnClickListener(clickListener);
        tv_tel_microphone.setTag(false);
        tv_tel_receiver.setOnClickListener(clickListener);
        tv_tel_receiver.setTag(false);
        tv_on_speakerphone.setOnClickListener(clickListener);
        tv_on_speakerphone.setTag(false);
        tv_dial.setOnClickListener(clickListener);
        tv_dial.setTag(false);

        //接听电话
        iv_ok.setOnClickListener(v -> {
            if (phoneFrom.equals(NET_IN)) {
                if (isSip) {
                    //调用sip的接听电话的接口
                    Intent intent = new Intent();
                    intent.setAction(ScreenActivity.CALL_NET_ING);
                    sendBroadcast(intent);
                    ll_relevant_operate.setVisibility(View.VISIBLE);
                } else {
                    PhoneManager.answer(Config.call);
                    ll_relevant_operate.setVisibility(View.VISIBLE);
                    tv_tel_receiver.setVisibility(View.GONE);
                    tv_tel_microphone.setVisibility(View.GONE);
                }
            }
        });
        //挂断电话
        iv_no.setOnClickListener(v -> {
            if (phoneFrom.equals(NET_IN) || phoneFrom.equals(NET_OUT)) {
                if (isSip) {
                    //调用sip的挂断电话的接口
                } else {
                    PhoneManager.hangup(Config.call);
                }
            }
        });
    }

    //未知
    private static final int UNKNOWN = 0;
    //正在呼叫
    private static final int CONNECTING = 1;
    //正在响铃（等待接听）
    private static final int RING = 2;
    //通话中
    private static final int CONNECTED = 3;
    private int callStatus = UNKNOWN;
    private int currentTime;
    private Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            currentTime++;
            switch (callStatus) {
                case CONNECTING:
                    if (currentTime % 4 == 0) {
                        statusTxt.setText("正在呼叫");
                    } else if (currentTime % 4 == 1) {
                        statusTxt.setText(" 正在呼叫.");
                    } else if (currentTime % 4 == 2) {
                        statusTxt.setText("  正在呼叫..");
                    } else if (currentTime % 4 == 3) {
                        statusTxt.setText("   正在呼叫...");
                    }
                    break;
                case RING:
                    break;
                case CONNECTED:
                    statusTxt.setText(new StringBuilder().append(UiUtils.formatAA(currentTime / 60)).append(":").append(UiUtils.formatAA(currentTime % 60)));
                    break;
            }
            animationHandler.postDelayed(timeRunnable, 1000);
        }
    };
    private final Handler animationHandler = new Handler();

    /**
     * 注意：该方法目前不会被回调，因为通过传感器判断距离，进一步自动打开/关闭扬声器的功能，目前禁掉了，但是代码保留，需要用开放即可。
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (isHeadset) {
            return;
        }
        float[] its = event.values;
        LogUtils.i(TAG, "its[0]:" + its[0]);
        if (!(boolean) tv_on_speakerphone.getTag()) {  //表示未手动打开扬声器
            if (its[0] == 0.0) {  //贴近手机
                switchSpeaker(false);
                tv_on_speakerphone.setTextColor(Color.parseColor("#62696C"));
                tv_on_speakerphone.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.on_speakerphone, 0, 0);
                tv_on_speakerphone.setTag(false);
            } else {  //远离手机
                switchSpeaker(true);
                tv_on_speakerphone.setTextColor(Color.parseColor("#D6E3F4"));
                tv_on_speakerphone.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.on_speakerphone_highlight, 0, 0);
                tv_on_speakerphone.setTag(true);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private void switchSpeaker(boolean on) {
        if (on) {  //打开扬声器
            audioManager.setSpeakerphoneOn(true);
        } else {  //关闭扬声器
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                audioManager.setSpeakerphoneOn(false);
                audioManager.setMode(AudioManager.MODE_IN_CALL);
            } else {
                audioManager.setSpeakerphoneOn(false);
            }
        }
    }

    private void showVolumePopupWindow(final View targetView) {
        if (volumePopupWindow == null) {
            volumePopupWindow = new VolumePopupWindow(LayoutInflater.from(this).inflate(R.layout.popup_window_volume, null),
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        volumePopupWindow.setCallback(progress -> {
            if (targetView.getId() == R.id.tv_tel_microphone) {
                //调用sip的接口调节话筒的音量
            } else if (targetView.getId() == R.id.tv_tel_receiver) {
                //调用sip的接口调节听筒的音量
            }
        });
        if (targetView.getId() == R.id.tv_tel_microphone) {
//            volumePopupWindow.initProgress();  //initProgress方法中的参数使用sip的接口获取
        } else if (targetView.getId() == R.id.tv_tel_receiver) {
//            volumePopupWindow.initProgress();  //initProgress方法中的参数使用sip的接口获取
        }
        int[] location = new int[2];
        targetView.getLocationOnScreen(location);
        int temp_width = (UiUtils.dpToPx(this, 80) - targetView.getWidth()) / 2;
        volumePopupWindow.showAtLocation(targetView, Gravity.NO_GRAVITY, location[0] - temp_width, location[1] - UiUtils.dpToPx(this, 90));
    }

    class CallbackHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 1) {
                String action = String.valueOf(msg.obj);
                if (action.equals(CALL_NET_ING)) {  //正在通话
                    if (isNetting) {
                        return;
                    }
                    isNetting = true;
                    isRing = false;
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        audioManager.setSpeakerphoneOn(false);
                        audioManager.setMode(AudioManager.MODE_IN_CALL);
                    } else {
                        audioManager.setSpeakerphoneOn(false);
                    }
                    currentTime = 0;
                    callStatus = CONNECTED;
                    if (ll_ok != null) {
                        ll_ok.setVisibility(View.GONE);
                    }
                    if (ll_no != null) {
                        ll_no.setVisibility(View.VISIBLE);
                    }
                    SystemUtils.stopSound();  //停止播放铃声
                } else if (action.equals(CALL_NET_END)) {
                    if (ll_ok != null) {
                        ll_ok.setVisibility(View.GONE);
                    }
                    if (ll_no != null) {
                        ll_no.setVisibility(View.GONE);
                    }
                    isRing = false;
                    LogUtils.i(TAG, "---call_net_end---");
                    SystemUtils.stopSound();  //停止播放铃声
                    statusTxt.setText(getString(R.string.call_ended));
                    Toast.makeText(ScreenActivity.this, getString(R.string.call_ended), Toast.LENGTH_SHORT).show();
                    finish();
                } else if (action.equals("android.intent.action.PHONE_STATE")) {
                    //调用sip的挂断电话的接口
                }
            }
        }
    }
}
