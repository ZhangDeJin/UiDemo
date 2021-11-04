package com.zdj.systemfuncationlibrary;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;

/**
 * <pre>
 *     author : zhangdj
 *     time   : 2021/01/18
 *     desc   : 音频焦点管理器
 * </pre>
 */
public class AudioFocusManager implements AudioManager.OnAudioFocusChangeListener{
    private static AudioFocusManager sAudioFocusManager;
    private AudioManager mAudioManager;
    private AudioFocusManager(Context context) {
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }
    
    private AudioFocusRequest mFocusRequest;
    private AudioAttributes mAudioAttributes;

    private OnRequestFocusResultListener mOnRequestFocusResultListener;
    private OnAudioFocusChangeListener mAudioFocusChangeListener;

    public static AudioFocusManager getInstance(Context context) {
        if (sAudioFocusManager == null) {
            synchronized(AudioFocusManager.class) {
                if (sAudioFocusManager == null) {
                    sAudioFocusManager = new AudioFocusManager(context);
                }
            }
        }
        return sAudioFocusManager;
    }

    

    /**
     * 请求音频焦点
     */
    public void requestFocus() {
        int result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (mFocusRequest == null) {
                if (mAudioAttributes == null) {
                    mAudioAttributes = new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build();
                }
                mFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                        .setAudioAttributes(mAudioAttributes)
                        .setWillPauseWhenDucked(false)
                        .setOnAudioFocusChangeListener(this)
                        .build();
            }
            result = mAudioManager.requestAudioFocus(mFocusRequest);
        } else {
            result = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }
        if (mOnRequestFocusResultListener != null) {
            mOnRequestFocusResultListener.onHandleResult(result);
        }
    }


    @Override
    public void onAudioFocusChange(int focusChange) {
        if (mAudioFocusChangeListener != null) {
            mAudioFocusChangeListener.onAudioFocusChange(focusChange);
        }
    }

    /**
     * 释放音频焦点
     */
    public void releaseAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mAudioManager.abandonAudioFocusRequest(mFocusRequest);
        } else {
            mAudioManager.abandonAudioFocus(this);
        }
    }

    /**
     * 处理音频焦点的结果
     */
    public interface OnRequestFocusResultListener {
        void onHandleResult(int result);
    }

    public void setOnHandleResultListener(OnRequestFocusResultListener listener) {
        mOnRequestFocusResultListener = listener;
    }

    public interface OnAudioFocusChangeListener {
        void onAudioFocusChange(int focusChange);
    }

    public void setOnAudioFocusChangeListener(OnAudioFocusChangeListener listener) {
        mAudioFocusChangeListener = listener;
    }
}
