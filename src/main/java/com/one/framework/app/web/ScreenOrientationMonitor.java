package com.one.framework.app.web;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;

/**
 * 屏幕横竖屏监控器
 *
 * 当H5页面要求为横屏且屏幕设置为自动旋转时,将屏幕设置为SCREEN_ORIENTATION_FULL_SENSOR(自动旋转)。其他情况都为强制竖屏
 *
 * 需要检测两个指标, H5页面的设置及系统屏幕设置
 *
 * Created by zhengtao on 17/5/16.
 */
public class ScreenOrientationMonitor {

    private static final int SCREEN_SETTING_PORTRAIT = 0;

    private WebActivity mWebActivity;
    private RotationObserver mRotationObserver;
    private boolean mH5ScreenOrientationSetting = false;

    public ScreenOrientationMonitor(WebActivity activity) {
        mWebActivity = activity;
        mRotationObserver = new RotationObserver(new Handler());
    }

    /**
     * H5端屏幕设置
     * @param settingValue true: 自动旋转   false:无要求
     */
    public void setH5ScreenOrientationSetting(boolean settingValue) {
        mH5ScreenOrientationSetting = settingValue;
        updateActivityOrientation(false);
    }


    public void onCreate() {
        mRotationObserver.startObserver();
    }


    public void onDestroy() {
        mRotationObserver.stopObserver();
    }


    //监控系统屏幕设置
    private class RotationObserver extends ContentObserver {

        ContentResolver mResolver;

        public RotationObserver(Handler handler) {
            super(handler);
            mResolver = mWebActivity.getContentResolver();
        }

        //屏幕旋转设置改变时调用
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            updateActivityOrientation(false);
        }

        public void startObserver() {
            mResolver.registerContentObserver(Settings.System.getUriFor(Settings.System.ACCELEROMETER_ROTATION), false, this);
        }

        public void stopObserver() {
            mResolver.unregisterContentObserver(this);
        }
    }


    public void updateActivityOrientation(boolean focusPortrait) {
        if (focusPortrait) {
            //如果强制竖屏,忽略各种设置。例如弹出登陆框时,要求强制竖屏
            mWebActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return;
        }
        if (mH5ScreenOrientationSetting && getRotationStatus(mWebActivity) != SCREEN_SETTING_PORTRAIT) {
            mWebActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        } else {
            mWebActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }


    private int getRotationStatus(Context context) {
        int status = 0;
        try {
            status = Settings.System.getInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return status;
    }

}
