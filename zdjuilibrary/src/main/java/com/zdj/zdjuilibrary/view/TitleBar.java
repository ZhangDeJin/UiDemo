package com.zdj.zdjuilibrary.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.zdj.zdjuilibrary.R;

/**
 * <pre>
 *     author : dejinzhang
 *     time : 2021/08/23
 *     desc : 封装的标题栏
 * </pre>
 */
public class TitleBar extends ConstraintLayout{
    private ConstraintLayout title_bar;
    private ImageView iv_left, iv_right_1, iv_right_2;
    private TextView tv_left, tv_right, tv_title;


    public TitleBar(@NonNull Context context) {
        this(context, null);
    }

    public TitleBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_title_bar, this);
        initView();
    }

    private void initView() {
        title_bar = findViewById(R.id.title_bar);
        iv_left = findViewById(R.id.iv_left);
        iv_right_1 = findViewById(R.id.iv_right_1);
        iv_right_2  = findViewById(R.id.iv_right_2);
        tv_left = findViewById(R.id.tv_left);
        tv_right = findViewById(R.id.tv_right);
        tv_title = findViewById(R.id.tv_title);
    }

    public TitleBar setBackgroundNull() {
        title_bar.setBackground(null);
        return this;
    }

    public TitleBar setViewVisible(boolean leftIvVisible, boolean rightIv1Visible, boolean rightIv2Visible, boolean leftTvVisible, boolean rightTvVisible) {
        iv_left.setVisibility(leftIvVisible ? VISIBLE : GONE);
        iv_right_1.setVisibility(rightIv1Visible ? VISIBLE : GONE);
        iv_right_2.setVisibility(rightIv2Visible ? VISIBLE : GONE);
        tv_left.setVisibility(leftTvVisible ? VISIBLE : GONE);
        tv_right.setVisibility(rightTvVisible ? VISIBLE : GONE);
        return this;
    }

    public TitleBar setTitleTvText(String text) {
        if (!TextUtils.isEmpty(text)) {
            tv_title.setText(text);
        }
        return this;
    }

    public TitleBar setTitleTvColor(int colorId) {
        tv_title.setTextColor(colorId);
        return this;
    }

    public TitleBar setLeftImage(int resId) {
        iv_left.setImageResource(resId);
        return this;
    }

    public TitleBar setRight1Image(int resId) {
        iv_right_1.setImageResource(resId);
        return this;
    }

    public TitleBar setRight2Image(int resId) {
        iv_right_2.setImageResource(resId);
        return this;
    }

    public TitleBar setLeftTvText(String text) {
        if (!TextUtils.isEmpty(text)) {
            tv_left.setText(text);
        }
        return this;
    }

    public TitleBar setRightTvText(String text) {
        if (!TextUtils.isEmpty(text)) {
            tv_right.setText(text);
        }
        return this;
    }

    public TitleBar setLeftTvTextColor(int colorId) {
        tv_left.setTextColor(colorId);
        return this;
    }

    public TitleBar setRightTvTextColor(int colorId) {
        tv_right.setTextColor(colorId);
        return this;
    }

    public TitleBar setLeftIvBackground(int backgroundId) {
        iv_left.setBackgroundResource(backgroundId);
        return this;
    }

    public TitleBar setRightIv1Background(int backgroundId) {
        iv_right_1.setBackgroundResource(backgroundId);
        return this;
    }

    public TitleBar setRightIv2Background(int backgroundId) {
        iv_right_2.setBackgroundResource(backgroundId);
        return this;
    }

    public TitleBar setClickListener(View.OnClickListener clickListener) {
        if (iv_left.getVisibility() == VISIBLE) {
            iv_left.setOnClickListener(clickListener);
        }
        if (iv_right_1.getVisibility() == VISIBLE) {
            iv_right_1.setOnClickListener(clickListener);
        }
        if (iv_right_2.getVisibility() == VISIBLE) {
            iv_right_2.setOnClickListener(clickListener);
        }
        if (tv_left.getVisibility() == VISIBLE) {
            tv_left.setOnClickListener(clickListener);
        }
        if (tv_right.getVisibility() == VISIBLE) {
            tv_right.setOnClickListener(clickListener);
        }
        return this;
    }
}
