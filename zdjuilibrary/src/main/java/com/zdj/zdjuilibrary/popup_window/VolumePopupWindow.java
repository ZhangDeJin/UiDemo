package com.zdj.zdjuilibrary.popup_window;

import android.view.View;
import android.widget.PopupWindow;
import android.widget.SeekBar;

import com.zdj.zdjuilibrary.R;
import com.zdj.zdjuilibrary.view.VerticalSeekBar;

/**
 * <pre>
 *     author : dejinzhang
 *     time : 2021/04/25
 *     desc : 音量调节PopupWindow
 * </pre>
 */
public class VolumePopupWindow extends PopupWindow {
    private VerticalSeekBar seekBar;
    public VolumePopupWindow(View contentView, int width, int height) {
        /**
         * 这里解释一下：因为我们要实现的是focusable为false的popupWindow（即popup没有被focused，
         * 也就是不能点击其他区域让其消失），所以我们直接重写VolumePopupWindow(View contentView, int width, int height)
         * 构造方法，在这个方法里面先通过调用父类的方法，即super(contentView, width, height)，通过查看源码
         * 我们知道在这个方法里面又调用了this(contentView, width, height, false);第四个参数传的是false，从而
         * 实现focusable为false的PopupWindow.
         */
        super(contentView, width, height);
        seekBar = contentView.findViewById(R.id.seekBar);
        seekBar.setMax(10);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (callback != null) {
                    callback.progressCallback(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void initProgress(int progress) {
        seekBar.setProgress(progress);
    }


    private Callback callback;
    public void setCallback(Callback callback) {
        this.callback = callback;
    }
    public interface Callback {
        void progressCallback(int progress);
    }
}
