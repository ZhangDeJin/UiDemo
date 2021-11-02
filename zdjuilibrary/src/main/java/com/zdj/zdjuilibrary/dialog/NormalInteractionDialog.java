package com.zdj.zdjuilibrary.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.zdj.systemfuncationlibrary.UiUtils;
import com.zdj.zdjuilibrary.R;

/**
 * <pre>
 *     author : dejinzhang
 *     time : 2021/10/20
 *     desc : 自定义的一个常规的Dialog，该Dialog由标题，内容，取消按钮，执行按钮（具体文案和功能对应具体业务）。
 *            其中标题可以不设置。
 *            其中执行按钮可以隐藏，此时只有取消按钮，此时的Dialog就相当于一个Toast。
 * </pre>
 */
public class NormalInteractionDialog extends Dialog {
    private TextView tv_title, tv_content, tv_cancel, tv_done;
    private View view_divider;

    public NormalInteractionDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        setContentView(R.layout.dialog_normal_interaction);
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.height = layoutParams.WRAP_CONTENT;
        layoutParams.width = (int) (UiUtils.getWidth(context) * 0.8);
        window.setAttributes(layoutParams);

        tv_title = findViewById(R.id.tv_title);
        tv_content = findViewById(R.id.tv_content);
        tv_cancel = findViewById(R.id.tv_cancel);
        tv_done = findViewById(R.id.tv_done);
        view_divider = findViewById(R.id.view_divider);

        tv_cancel.setOnClickListener(v -> {
            dismiss();
            if (callback != null) {
                callback.cancel();
            }
        });

        tv_done.setOnClickListener(v -> {
            dismiss();
            if (callback != null) {
                callback.done();
            }
        });
    }

    public void init(boolean isGoneTitle, boolean isGoneDone,
                     String title, String content, String cancel, String done) {
        if (isGoneTitle) {
            tv_title.setVisibility(View.GONE);
        } else {
            tv_title.setText(title);
        }
        if (isGoneDone) {
            tv_done.setVisibility(View.GONE);
            view_divider.setVisibility(View.GONE);
        } else {
            tv_done.setText(done);
        }
        tv_content.setText(content);
        tv_cancel.setText(cancel);
    }



    private Callback callback;
    public void setCallback(Callback callback) {
        this.callback = callback;
    }
    public interface Callback {
        void done();
        void cancel();
    }
}
