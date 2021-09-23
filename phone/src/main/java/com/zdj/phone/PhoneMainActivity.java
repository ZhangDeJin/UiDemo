package com.zdj.phone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.telecom.TelecomManager;
import android.view.View;
import android.widget.ImageView;

import com.zdj.phone.core.PhoneManager;
import com.zdj.systemfuncationlibrary.SystemUtils;

public class PhoneMainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView switch_default_phone_application;
    private SharedPreferences appInfoSP;
    private SharedPreferences.Editor appInfoSPEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_main);

        switch_default_phone_application = findViewById(R.id.switch_default_phone_application);
        switch_default_phone_application.setOnClickListener(this);

        appInfoSP = getApplicationContext().getSharedPreferences(getPackageName() + "_appInfo", Context.MODE_PRIVATE);
        appInfoSPEditor = appInfoSP.edit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isDefaultPhoneApplication()) {
            switch_default_phone_application.setTag(true);
            switch_default_phone_application.setImageResource(R.drawable.switch_on);
        } else {
            switch_default_phone_application.setTag(false);
            switch_default_phone_application.setImageResource(R.drawable.switch_off);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.switch_default_phone_application) {
            switchDefaultPhoneApplication();
        }
    }

    private boolean isDefaultPhoneApplication() {
        TelecomManager telecomManager = (TelecomManager) getSystemService(TELECOM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (telecomManager.getDefaultDialerPackage() != null) {
                String defaultDialerPackage = telecomManager.getDefaultDialerPackage();
                if (defaultDialerPackage.equals(getPackageName())) {
                    return true;
                } else {
                    appInfoSPEditor.putString("defaultDialerPackage", defaultDialerPackage).commit();
                }
            }
        }
        return false;
    }

    private void switchDefaultPhoneApplication() {
        if (!(boolean)switch_default_phone_application.getTag()) {
            PhoneManager.setDefaultDialerSetWindow(this, getPackageName());
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (("huawei".equals(android.os.Build.BRAND.toLowerCase()) || "honor".equals(android.os.Build.BRAND.toLowerCase()))
                        && !SystemUtils.isHarmonyOS()) {
                    Intent hwIntent = new Intent();
                    hwIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    hwIntent.setClassName("com.android.settings", "com.android.settings.Settings$PreferredListSettingActivity");
                    startActivity(hwIntent);
                } else {
                    startActivity(new Intent("android.settings.MANAGE_DEFAULT_APPS_SETTINGS"));
                }
            } else {
                PhoneManager.setDefaultDialerSetWindow(this, appInfoSP.getString("defaultDialerPackage", ""));
            }
        }
    }
}