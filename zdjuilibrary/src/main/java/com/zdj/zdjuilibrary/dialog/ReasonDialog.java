package com.zdj.zdjuilibrary.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.zdj.systemfuncationlibrary.UiUtils;
import com.zdj.zdjuilibrary.R;
import com.zdj.zdjuilibrary.view.TitleBar;

/**
 * <pre>
 *     author : dejinzhang
 *     time : 2021/08/12
 *     desc : 原因弹框
 *            注：该弹框支持跟随手指向下滑动
 *            实现思路：自定义Dialog，重写onTouchEvent。在down的时候记录y，滑动的时候计算出滑动距离。scrollBy控制view(decorView，通过getWindow().getDecorView()得到dialog的decorView)的滑动。
 *            确保view不能往上滑动溢出，所以控制view.getScrollY大于0的时候重置。手指抬起的时候判断滑动方向如果是向下并且超过四分之一，隐藏dialog，然后重置view的内容的位置。
 *            通过改变mScrollY来变化view的位置，实际上view本身并没有发生移动，移动的是view的内容。view的内容和view本身的横向纵向距离就是mScrollX和mScrollY的值。
 * </pre>
 */
public class ReasonDialog extends Dialog {
    Context mContext;
    View mView;

    public ReasonDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        setContentView(R.layout.dialog_reason);
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = layoutParams.MATCH_PARENT;
        layoutParams.height = (int) (UiUtils.getHeight(context) * 0.6);
        window.setAttributes(layoutParams);
        window.setGravity(Gravity.BOTTOM);
        mContext = context;
        mView = window.getDecorView();
    }

    public void init(final String dialogTitle, final String reasonTitle, final int reasonLimit, final Callback callback) {
        TitleBar titleBar = findViewById(R.id.titleBar);
        titleBar.setViewVisible(false, false, false, true, true)
                .setTitleTvText(dialogTitle)
                .setLeftTvText(mContext.getString(R.string.cancel))
                .setRightTvText(mContext.getString(R.string.confirm))
                .setLeftTvTextColor(mContext.getResources().getColor(R.color.light_gray_2))
                .setRightTvTextColor(mContext.getResources().getColor(R.color.blue));
        ((TextView)findViewById(R.id.tv_key)).setText(reasonTitle);
        final EditText et_reason = findViewById(R.id.et_reason);
        if (reasonLimit > 0) {
            /**
             * 注意：这个是直接以中文示例，如果考虑多语言适配，那么这里是需要判断当前的语言环境的，然后再根据不同语言的语法进行拼接。
             * 判断语言环境，可以通过mContext.getResources().getConfiguration().locale.getLanguage()
             */
            UiUtils.setEditTextHintTextSize(new StringBuilder().append(reasonLimit).append("个字以内").toString(), 14, et_reason);
            et_reason.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() > reasonLimit) {
                        s = s.subSequence(0, reasonLimit);
                        et_reason.setText(s);
                        et_reason.setSelection(reasonLimit);
                        Toast toast = Toast.makeText(mContext, mContext.getString(R.string.reached_the_upper_limit), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        } else {
            UiUtils.setEditTextHintTextSize(mContext.getString(R.string.please_input), 14, et_reason);
        }
        titleBar.setClickListener(v -> {
            if (v.getId() == R.id.tv_left) {
                dismiss();
            } else if (v.getId() == R.id.tv_right) {
                dismiss();
                if (callback != null) {
                    String reason = et_reason.getText().toString();
                    if (reason == null) {
                        reason = "";
                    }
                    callback.confirm(reason);
                }
            }
        });
    }

    float startY, moveY;

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                moveY = event.getY() - startY;
                mView.scrollBy(0, -(int)moveY);
                startY = event.getY();
                if (mView.getScrollY() > 0) {
                    mView.scrollTo(0, 0);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mView.getScrollY() < -getWindow().getAttributes().height / 4) {
                    dismiss();
                }
                mView.scrollTo(0, 0);
                break;
        }
        return super.onTouchEvent(event);
    }



    public interface Callback {
        void confirm(String reason);
    }
}
