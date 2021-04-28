package com.zdj.uidemo;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.zdj.zdjuilibrary.widget.date_picker.DatePickerBuilder;
import com.zdj.zdjuilibrary.widget.date_picker.DatePickerDialog;

import androidx.appcompat.widget.Toolbar;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;

import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

public class ItemDetailActivity extends AppCompatActivity {
    private Calendar currentCalendar;
    private DatePickerDialog datePickerDialog1, datePickerDialog2, datePickerDialog3;
    private LinearLayout ll_content, year_week, year_month, year_month_week;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "确定显示内容吗？", Snackbar.LENGTH_LONG)
                .setAction("确定", v -> {
                    ll_content.setVisibility(View.VISIBLE);
                    currentCalendar = Calendar.getInstance();
                    ((TextView)(year_week.getChildAt(0))).setText(new StringBuilder().append(currentCalendar.get(Calendar.YEAR)).append("年").append("第").append(currentCalendar.get(Calendar.WEEK_OF_YEAR)).append("周").toString());
                    ((TextView)(year_month.getChildAt(0))).setText(new StringBuilder().append(currentCalendar.get(Calendar.YEAR)).append("年").append(currentCalendar.get(Calendar.MONTH) + 1).append("月").toString());
                    ((TextView)(year_month_week.getChildAt(0))).setText(new StringBuilder().append(currentCalendar.get(Calendar.YEAR)).append("年").append(currentCalendar.get(Calendar.MONTH) + 1).append("月").append("第").append(currentCalendar.get(Calendar.WEEK_OF_MONTH)).append("周").toString());
                    fab.setVisibility(View.GONE);
                }).show());

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ll_content = findViewById(R.id.ll_content);
        year_week = findViewById(R.id.year_week);
        year_month = findViewById(R.id.year_month);
        year_month_week = findViewById(R.id.year_month_week);
        year_week.setOnClickListener(clickListener);
        year_month.setOnClickListener(clickListener);
        year_month_week.setOnClickListener(clickListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private View.OnClickListener clickListener = v -> {
        switch (v.getId()) {
            case R.id.year_week:
            case R.id.year_month:
            case R.id.year_month_week:
                showDatePickerDialog(v);
                break;
        }
    };

    public void showDatePickerDialog(View targetView) {
        if (targetView.getId() == R.id.year_week) {
            if (datePickerDialog1 == null) {
                boolean[] dialogType = new boolean[3];
                dialogType[0] = true;
                dialogType[1] = false;
                dialogType[2] = true;
                DatePickerBuilder datePickerBuilder = new DatePickerBuilder(this, calendar -> {
                    currentCalendar = calendar;
                    ((TextView)(year_week.getChildAt(0))).setText(new StringBuilder().append(currentCalendar.get(Calendar.YEAR)).append("年").append("第").append(currentCalendar.get(Calendar.WEEK_OF_YEAR)).append("周").toString());
                }).setDialogStyle(R.style.FirstDialogAnimationStyle)
                        .setType(dialogType)
                        .setCalendar(currentCalendar)
                        .setRangDate(null, Calendar.getInstance());
                datePickerDialog1 = datePickerBuilder.build();
                datePickerDialog1.setOnDismissListener(dialog -> year_week.getChildAt(1).animate().setDuration(500).rotation(0).start());
            }
            datePickerDialog1.show();
            year_week.getChildAt(1).animate().setDuration(500).rotation(-180).start();
        } else if (targetView.getId() == R.id.year_month) {
            if (datePickerDialog2 == null) {
                boolean[] dialogType = new boolean[3];
                dialogType[0] = true;
                dialogType[1] = true;
                dialogType[2] = false;
                DatePickerBuilder datePickerBuilder = new DatePickerBuilder(this, calendar -> {
                    currentCalendar = calendar;
                    ((TextView)(year_month.getChildAt(0))).setText(new StringBuilder().append(currentCalendar.get(Calendar.YEAR)).append("年").append(currentCalendar.get(Calendar.MONTH) + 1).append("月").toString());
                }).setDialogStyle(R.style.FirstDialogAnimationStyle)
                        .setType(dialogType)
                        .setCalendar(currentCalendar)
                        .setRangDate(null, Calendar.getInstance());
                datePickerDialog2 = datePickerBuilder.build();
                datePickerDialog2.setOnDismissListener(dialog -> year_month.getChildAt(1).animate().setDuration(500).rotation(0).start());
            }
            datePickerDialog2.show();
            year_month.getChildAt(1).animate().setDuration(500).rotation(-180).start();
        } else if (targetView.getId() == R.id.year_month_week) {
            if (datePickerDialog3 == null) {
                boolean[] dialogType = new boolean[3];
                dialogType[0] = true;
                dialogType[1] = true;
                dialogType[2] = true;
                DatePickerBuilder datePickerBuilder = new DatePickerBuilder(this, calendar -> {
                    currentCalendar = calendar;
                    ((TextView)(year_month_week.getChildAt(0))).setText(new StringBuilder().append(currentCalendar.get(Calendar.YEAR)).append("年").append(currentCalendar.get(Calendar.MONTH) + 1).append("月").append("第").append(currentCalendar.get(Calendar.WEEK_OF_MONTH)).append("周").toString());
                }).setDialogStyle(R.style.FirstDialogAnimationStyle)
                        .setType(dialogType)
                        .setCalendar(currentCalendar)
                        .setRangDate(null, Calendar.getInstance());
                datePickerDialog3 = datePickerBuilder.build();
                datePickerDialog3.setOnDismissListener(dialog -> year_month_week.getChildAt(1).animate().setDuration(500).rotation(0).start());
            }
            datePickerDialog3.show();
            year_month_week.getChildAt(1).animate().setDuration(500).rotation(-180).start();
        }
    }
}