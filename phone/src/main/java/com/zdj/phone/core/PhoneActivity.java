package com.zdj.phone.core;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.zdj.phone.R;
import com.zdj.phone.widget.DialPadDialog;

import java.util.List;

/**
 * <pre>
 *     author : dejinzhang
 *     time : 2021/09/10
 *     desc : 使我们的应用成为一个电话程序必需的组件
 * </pre>
 */
public class PhoneActivity extends Activity {
    private static final int MY_PERMISSIONS_CALL_PHONE = 1;
    private DialPadDialog dialPadDialog;
    private TextView tv_input_numbers;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_phone);
        tv_input_numbers = findViewById(R.id.tv_input_numbers);

        dialPadDialog = new DialPadDialog(this, R.style.NotDarkenAndFirstDialogAnimationStyle);
        dialPadDialog.setCanceledOnTouchOutside(false);
        dialPadDialog.setCallback(new DialPadDialog.Callback() {
            @Override
            public void call(List<Character> list) {
                if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PhoneActivity.this, new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_CALL_PHONE);
                } else {
                    callDeal(list);
                }
            }

            @Override
            public void collapse() {}

            @Override
            public void refreshShow(List<Character> list) {
                if (list.size() > 0) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < list.size(); i++) {
                        stringBuilder.append(list.get(i));
                    }
                    tv_input_numbers.setText(stringBuilder);
                    tv_input_numbers.setVisibility(View.VISIBLE);
                } else {
                    tv_input_numbers.setVisibility(View.GONE);
                }
            }
        });

        Uri uri = getIntent().getData();
        if (uri != null) {
            String uriString = uri.toString();
            if (uriString != null && uriString.startsWith("tel:")) {
                String phoneNumber = uriString.substring("tel:".length());
                if (!TextUtils.isEmpty(phoneNumber)) {
                    char[] numbers = phoneNumber.toCharArray();
                    for (int i = 0; i < numbers.length; i++) {
                        dialPadDialog.getInputStringList().add(numbers[i]);
                    }
                    tv_input_numbers.setText(phoneNumber);
                    tv_input_numbers.setVisibility(View.VISIBLE);
                }
            }
        }

        dialPadDialog.show();
        findViewById(R.id.iv_dial_pad).setOnClickListener(v -> dialPadDialog.show());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_CALL_PHONE) {
            if (!(grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setMessage(getString(R.string.call_permission_hint))
                        .setPositiveButton(getString(R.string.go_open), (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.fromParts("package", getPackageName(), null));
                            startActivity(intent);
                        })
                        .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {});
                builder.create().show();
            } else {
                callDeal(dialPadDialog.getInputStringList());
            }
        }
    }

    private void callDeal(List<Character> list) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            stringBuilder.append(list.get(i));
        }
        PhoneManager.call(this, stringBuilder.toString());
    }
}
