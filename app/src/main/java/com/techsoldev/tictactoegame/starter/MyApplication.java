package com.techsoldev.tictactoegame.starter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.HashSet;

public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks {
    @SuppressLint("StaticFieldLeak")
    private static Activity mRootActivity = null;
    private static HashSet<Activity> mChildActivities = new HashSet<Activity>();
    private static boolean mForeground = false;

    private static final String TAG = "TTT-MyApplication";

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate enter");
        mRootActivity = null;
        mChildActivities = new HashSet<Activity>();
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
        Log.i(TAG, "onCreate exit");
    }

    @Override
    public void onTerminate() {
        Log.i(TAG, "onTerminate enter");
        mRootActivity = null;
        mChildActivities = new HashSet<Activity>();
        super.onTerminate();
        Log.i(TAG, "onTerminate exit");
    }

    public static boolean isForeground() {
        return mForeground;
    }

    public static void finish() {
        Activity[] activities = mChildActivities.toArray(new Activity[]{});
        for (Activity activity: activities) {
            mChildActivities.remove(activity);
            activity.finish();
        }
        if (mRootActivity != null) {
            mRootActivity.finish();
        }
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, Bundle savedInstanceState) {
        if (mRootActivity == null) {
            mRootActivity = activity;
            Log.i(TAG, String.format("root activity created %s", activity.getClass().getName()));
        } else {
            mChildActivities.add(activity);
            Log.i(TAG, String.format("child activity created %s", activity.getClass().getName()));
        }
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {}

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        mForeground = true;
        Log.i(TAG, String.format("foreground = true %s", activity.getClass().getName()));
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        mForeground = false;
        Log.i(TAG, String.format("foreground = false %s", activity.getClass().getName()));
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {}

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        if (activity == mRootActivity) {
            mRootActivity = null;
            Log.i(TAG, String.format("root activity destroyed %s", activity.getClass().getName()));
        } else {
            mChildActivities.remove(activity);
            Log.i(TAG, String.format("child activity destroyed %s", activity.getClass().getName()));
        }
    }
}
