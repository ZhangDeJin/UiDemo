package com.zdj.phone.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.zdj.phone.R;
import com.zdj.phone.adapter.DialPadAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : dejinzhang
 *     time : 2021/10/19
 *     desc : 拨号盘弹框
 * </pre>
 */
public class DialPadDialog extends Dialog {
    Context mContext;
    List<Character> inputStringList;

    public DialPadDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        setContentView(R.layout.dialog_dial_pad);
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = layoutParams.MATCH_PARENT;
        layoutParams.height = layoutParams.WRAP_CONTENT;
        window.setAttributes(layoutParams);
        window.setGravity(Gravity.BOTTOM);
        mContext = context;

        initView();
    }

    private void initView() {
        GridView gridView = findViewById(R.id.gv_dial_pad);
        ImageView iv_call = findViewById(R.id.iv_call);
        ImageView iv_collapse = findViewById(R.id.iv_collapse);
        ImageView iv_delete = findViewById(R.id.iv_delete);

        final DialPadAdapter dialPadAdapter = new DialPadAdapter(mContext, 2);
        gridView.setAdapter(dialPadAdapter);
        gridView.setSelector(mContext.getDrawable(R.drawable.selector_dial_pad_item));
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            inputStringList.add(dialPadAdapter.getList().get(position).getMainText());
            if (callback != null) {
                callback.refreshShow(inputStringList);
            }
        });

        View.OnClickListener clickListener = v -> {
            if (callback != null) {
                if (v.getId() == R.id.iv_call) {
                    if (inputStringList.size() > 0) {
                        callback.call(inputStringList);
                    }
                } else if (v.getId() == R.id.iv_collapse) {
                    dismiss();
                } else if (v.getId() == R.id.iv_delete) {
                    if (inputStringList.size() > 0) {
                        inputStringList.remove(inputStringList.size() - 1);
                    }
                    callback.refreshShow(inputStringList);
                }
            }
        };
        iv_call.setOnClickListener(clickListener);
        iv_collapse.setOnClickListener(clickListener);
        iv_delete.setOnClickListener(clickListener);

        inputStringList = new ArrayList<>();
    }

    public void clearStringList() {
        if (inputStringList != null) {
            inputStringList.clear();
        }
    }

    public List<Character> getInputStringList() {
        return inputStringList;
    }

    private Callback callback;
    public void setCallback(Callback callback) {
        this.callback = callback;
    }
    public interface Callback {
        void call(List<Character> list);
        void collapse();
        void refreshShow(List<Character> list);
    }
}
