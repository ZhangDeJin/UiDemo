package com.zdj.zdjuilibrary.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * <pre>
 *     author : dejinzhang
 *     time : 2021/09/18
 *     desc : 自定义View实现水波纹动画
 * </pre>
 */
public class RippleView extends RelativeLayout implements Runnable {
    private int mMaxRadius = 70;
    private int mInterval = 20;
    private int count = 0;

    private Bitmap mCacheBitmap;
    private Paint mRipplePaint;
    private Paint mCirclePaint;
    private Path mArcPath;

    public RippleView(Context context) {
        this(context, null);
    }

    public RippleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RippleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mRipplePaint = new Paint();
        mRipplePaint.setAntiAlias(true);
        mRipplePaint.setStyle(Paint.Style.STROKE);
        mRipplePaint.setColor(0xff262b31);
        mRipplePaint.setStrokeWidth(6f);

        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setColor(Color.WHITE);

        mArcPath = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mCacheBitmap != null) {
            mCacheBitmap.recycle();
            mCacheBitmap = null;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //获取加号图片View
        View plusChild = getChildAt(0);
        if (plusChild == null) {
            return;
        }

        //获取加号图片大小
        final int pw = plusChild.getWidth();
        final int ph = plusChild.getHeight();

        if (pw == 0 || ph == 0) {
            return;
        }

        //加号图片中心点坐标
        final float px = plusChild.getX() + pw / 2.0f;
        final float py = plusChild.getY() + ph / 2.0f;

        final int rw = pw / 2;
        final int rh = ph / 2;

        if (mCacheBitmap == null) {
            mCacheBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            Canvas cv = new Canvas(mCacheBitmap);
            super.onDraw(cv);

            //清空所有已经画过的path至原始状态
            mArcPath.reset();

            //起始轮廓点移至x，y坐标点，即加号图片正下方再往下20位置
//            mArcPath.moveTo(px, py + rh + mInterval);
            //设置二次贝塞尔，实现平滑曲线，前两个参数为操作点坐标，后两个参数为结束点坐标
//            mArcPath.quadTo(px, fy - mInterval, fx + fw * 0.618f, fy - mInterval);
            //0-255，数值越小越透明
            mRipplePaint.setAlpha(255);
            cv.drawPath(mArcPath, mRipplePaint);
            //绘制半径为6的实心圆点
//            cv.drawCircle(px, py + rh + mInterval, 6, mCirclePaint);
        }

        //绘制背景图片
        canvas.drawBitmap(mCacheBitmap, 0, 0, mCirclePaint);

        //保存画布当前的状态
        int save = canvas.save();
        for (int step = count; step <= mMaxRadius; step+=mInterval) {
            //step越大越靠外就越透明
            mRipplePaint.setAlpha(255 * (mMaxRadius - step) / mMaxRadius);
            canvas.drawCircle(px, py, (float)(rw + step * 5), mRipplePaint);
        }
        //恢复Canvas的状态
        canvas.restoreToCount(save);
        //延迟80毫秒后开始运行
        postDelayed(this, 80);
    }

    @Override
    public void run() {
        //把run对象的引用从队列里拿出来，这样，它就不会执行了，但是run没有销毁
        removeCallbacks(this);
        count += 2;
        count %= mInterval;
        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mCacheBitmap != null) {
            mCacheBitmap.recycle();
            mCacheBitmap = null;
        }
    }
}
