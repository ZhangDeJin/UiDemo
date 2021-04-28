package com.zdj.zdjuilibrary.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatSeekBar;

/**
 * <pre>
 *     author : dejinzhang
 *     time : 2021/04/23
 *     desc :
 *     垂直的SeekBar
 *     需求：实现垂直的SeekBar
 *     思路：系统原生的seekBar的样式是水平的，那么我们定义一个类继承于SeekBar，在onDraw方法中旋转视图
 * </pre>
 */
public class VerticalSeekBar extends AppCompatSeekBar {
    public VerticalSeekBar(Context context) {
        super(context);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        /**
         * 在调用onDraw方法之前，先将画布旋转一下
         */

        //实现向上[0, max]的seekBar
        canvas.rotate(-90);  //将seekBar逆时针旋转90度
        canvas.translate(-getHeight(), 0);  //将旋转后的视图移动回来，否则将没有内容。rotate()方法，从字面上来看旋转的是"画布"，但是我们最好是理解成旋转的是画布的坐标轴

        /*//实现向下[0, max]的seekBar
        canvas.rotate(90);
        canvas.translate(0, -getWidth());*/
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                /**
                 * 对这里的计算，解释下为什么是这样子：
                 * 我们实现的是向上滑动调节大小，越上面越大。
                 * 而Android的坐标原点是在屏幕的左上角，越往右，x坐标的越大，越往下，y坐标越大。
                 * 结合起来，我们就不难得出progress和当前滑动位置的计算公式为：
                 * getMax() - (getMax() * event.getY() / getHeight())
                 */
                setProgress((int) (getMax() - (getMax() * event.getY() / getHeight())));
                break;
            default:
                return super.onTouchEvent(event);
        }
        return true;
    }

    @Override
    public synchronized void setProgress(int progress) {
        super.setProgress(progress);
        onSizeChanged(getWidth(), getHeight(), 0, 0);
    }
}
