package com.zdj.uidemo;

import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.zdj.systemfuncationlibrary.SystemUtils;
import com.zdj.systemfuncationlibrary.TimeUtils;
import com.zdj.systemfuncationlibrary.UiUtils;
import com.zdj.zdjuilibrary.popup_window.VolumePopupWindow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private TextView tv_volume;
    private VolumePopupWindow volumePopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            /**
             * Snackbar是一款Material Design中控件，用来代替Toast，Snackbar与Toast的主要区别是：
             * Snackbar可以滑动退出，也可以处理用户交互事件。
             */
            Snackbar.make(view, "是否有疑问或者建议呢，如果有的话，可以通过邮件与作者联系", Snackbar.LENGTH_LONG)
                    .setAction("发送邮件", v -> {
                        String title = new StringBuilder().append("UiDemo的反馈（").append(TimeUtils.getDateFormat(new Date(),"yyyy-MM-dd")).append("）").toString();
                        SystemUtils.sendEmail(MainActivity.this, "2439762097@qq.com", title);
                    }).show();
        });

        tv_volume = findViewById(R.id.tv_volume);
        tv_volume.setOnClickListener(clickListener);
        tv_volume.setTag(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            SystemUtils.goToAppInfo(MainActivity.this, getPackageName());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_volume:
                    tv_volume.setTag(!((boolean) tv_volume.getTag()));
                    tv_volume.setTextColor(((boolean)tv_volume.getTag()) ? Color.parseColor("#D6E3F4") : Color.parseColor("#62696C"));
                    tv_volume.setCompoundDrawablesRelativeWithIntrinsicBounds(0, ((boolean)tv_volume.getTag()) ? R.drawable.tel_receiver_highlight : R.drawable.tel_receiver, 0, 0);
                    if ((boolean)tv_volume.getTag()) {
                        showVolumePopupWindow(tv_volume);
                    } else {
                        if (volumePopupWindow != null) {
                            volumePopupWindow.dismiss();
                        }
                    }
                    break;
            }
        }
    };

    private void showVolumePopupWindow(View targetView) {
        if (volumePopupWindow == null) {
            volumePopupWindow = new VolumePopupWindow(LayoutInflater.from(this).inflate(R.layout.popup_window_volume, null), ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            volumePopupWindow.setCallback(progress -> {
                switch (targetView.getId()) {
                    case R.id.tv_volume:
                        //调用方法调节音量
                        Log.i("progress:", "" + progress);
                        break;
                }
            });
        }
        int[] location = new int[2];
        targetView.getLocationOnScreen(location);
        int tempWidth = (UiUtils.dpToPx(this, 80) - targetView.getWidth()) / 2;
        volumePopupWindow.showAtLocation(targetView, Gravity.NO_GRAVITY, location[0] - tempWidth, location[1] - UiUtils.dpToPx(this, 90));
    }
}