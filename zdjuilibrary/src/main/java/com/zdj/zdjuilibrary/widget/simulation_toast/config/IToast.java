package com.zdj.zdjuilibrary.widget.simulation_toast.config;

import android.view.View;
import android.widget.TextView;

/**
 * <pre>
 *     author : dejinzhang
 *     time : 2021/11/01
 *     desc : Toast接口
 * </pre>
 */
public interface IToast {
    /**
     * 显示
     */
    void show();

    /**
     * 取消
     */
    void cancel();

    /**
     * 设置文本
     */
    void setText(int id);

    /**
     * 设置文本
     */
    void setText(CharSequence text);

    /**
     * 设置布局
     */
    void setView(View view);

    /**
     * 获取布局
     */
    View getView();

    /**
     * 设置显示时长
     */
    void setDuration(int duration);

    /**
     * 获取显示时长
     */
    int getDuration();

    /**
     * 设置重心偏移
     */
    void setGravity(int gravity, int xOffset, int yOffset);

    /**
     * 获取显示重心
     */
    int getGravity();

    /**
     * 获取水平偏移
     */
    int getXOffset();

    /**
     * 获取垂直偏移
     */
    int getYOffset();

    /**
     * 设置屏幕间距
     */
    void setMargin(float horizontalMargin, float verticalMargin);

    /**
     * 获取水平间距
     */
    float getHorizontalMargin();

    /**
     * 获取垂直间距
     */
    float getVerticalMargin();

    /**
     * 获取用于显示消息的TextView
     * 注意：
     * 只有在java 8以上才可以在接口里面这样写。
     * java 8新特性：接口可以定义非抽象方法，但必须使用default或者static关键字来修饰。
     */
    default TextView findMessageView(View view) {
        if (view instanceof TextView) {
            if (view.getId() == View.NO_ID) {
                view.setId(android.R.id.message);
            } else if (view.getId() != android.R.id.message) {
                //必须将TextView的id值设置成android.R.message
                throw new IllegalArgumentException("You must set the ID value of TextView to android.R.id.message");
            }
            return (TextView) view;
        }

        if (view.findViewById(android.R.id.message) instanceof TextView) {
            return ((TextView) view.findViewById(android.R.id.message));
        }

        //如果设置的布局没有包含一个TextView则抛出异常，必须要包含一个id值成android.R.id.message的TextView
        throw new IllegalArgumentException("You must include a TextView with an ID value of android.R.id.message");
    }
}
