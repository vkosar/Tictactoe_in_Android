package com.techsoldev.tictactoegame.starter;

import com.techsoldev.tictactoegame.R;
//import com.techsoldev.tictactoegame.OfflineGameMenuActivity;
import com.techsoldev.tictactoegame.AiGameActivity;

import android.app.ActivityOptions;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationManagerCompat;

import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.ViewStateListenerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StarterService extends Service {
    private static final String TAG = "TTT-MainService";
    private static final int FOREGROUND_SERVICE_ID = 1;
    private static final String NOTIFICATION_CHANNEL_ID = "default";

    Handler mUiThreadHandler;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public StarterService() {}

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");
        return null;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate enter");
        super.onCreate();

        mUiThreadHandler = new Handler(Looper.getMainLooper());
        createFloatingButton();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(FOREGROUND_SERVICE_ID, buildForegroundNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
        } else {
            startForeground(FOREGROUND_SERVICE_ID, buildForegroundNotification());
        }

        Log.i(TAG, "onCreate exit");
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy enter");
        super.onDestroy();
        Log.i(TAG, "onDestroy exit");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, String.format("onStartCommand%s", (intent == null ? " null intent!" : "")));
        if (intent != null) {
            String cmdStr = intent.getStringExtra("command");
            if (cmdStr != null) {
                Log.i(TAG, String.format("Command = '%s'", cmdStr));
                try {
                    JSONObject cmdObj = new JSONObject(cmdStr);
                    if (cmdObj.has("params")) {
                        try {
                            JSONArray params = cmdObj.getJSONArray("params");
                            DbHelper.setParams(getApplicationContext(), params);
                            Log.i(TAG, "Params applied");
                        } catch (JSONException error) {
                            Log.e(TAG, "Can not decode params", error);
                        }
                    }
                } catch (JSONException error) {
                    Log.e(TAG, "Can not decode command", error);
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Notification buildForegroundNotification() {
        NotificationChannelCompat channel = new NotificationChannelCompat.Builder(NOTIFICATION_CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_DEFAULT)
            .setName(getString(R.string.starterservice))
            .build();

        NotificationManagerCompat.from(getApplicationContext()).createNotificationChannel(channel);

        return new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
            .setContentTitle(getString(R.string.starterservice))
            .setContentText(getString(R.string.service_working))
            .setSmallIcon(android.R.drawable.ic_menu_more)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setAutoCancel(false)
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setOngoing(true)
            .build();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void createFloatingButton() {
        final int iconId = R.drawable.tic_tac_toe_96;

        BitmapFactory.Options dimensions = new BitmapFactory.Options();
        dimensions.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), iconId, dimensions);
        final int iconW = dimensions.outWidth;
        final int iconH = dimensions.outHeight;

        Point displaySize = new Point();
        WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getSize(displaySize);
        final int maxX = displaySize.x - iconW;
        final int maxY = displaySize.y - iconH;
        final int defaultX = maxX;
        final int defaultY = (int)(0.50f * displaySize.y); // Approximate Y
        int x = DbHelper.getParamInt(getApplicationContext(), "floatButtonX", defaultX);
        int y = DbHelper.getParamInt(getApplicationContext(), "floatButtonY", defaultY);
        if (x < 0) {
            x = 0;
        }
        if (x > maxX) {
            x = maxX;
        }
        if (y < 0) {
            y = 0;
        }
        if (y > maxY) {
            y = maxY;
        }

        ImageView imageView = new ImageView(getApplicationContext());
        imageView.setImageResource(iconId);
        imageView.setOnClickListener((View v) -> {
            startStopApp();
        });

        FloatWindow
            .with(getApplicationContext())
            .setView(imageView)
            .setX(x)
            .setY(y)
            .setWidth(iconW)
            .setHeight(iconH)
            //.setMoveType(MoveType.slide, 100, -100)
            //.setMoveStyle(500, new BounceInterpolator())
            // Show floating button if desktop is visible
            .setDesktopShow(true)
            .setViewStateListener(new ViewStateListenerAdapter() {
                @Override
                public void onPositionUpdate(int x, int y) {
                try {
                    JSONObject params = new JSONObject();
                    params.put("floatButtonX", x);
                    params.put("floatButtonY", y);
                    DbHelper.setParams(getApplicationContext(), params);
                } catch (JSONException error) { /* empty */ }
                }
            })
            .build();
        // Show floating button on app service start
        FloatWindow.get().show();
    }

    private void startStopApp() {
        if (MyApplication.isForeground()) {
            Log.i(TAG, "Stop application");

            MyApplication.finish();
        } else {
            Log.i(TAG, "Start application");

            DisplayManager displayManager = (DisplayManager)getSystemService(Context.DISPLAY_SERVICE);
            Display[] displays = displayManager.getDisplays();
            int displayIdToShowOn = 0;
            for (Display display: displays) {
                Log.i(TAG, String.format("display id=%s flags=%d", display.getDisplayId(), display.getFlags()));
                if (displayIdToShowOn == 0) {
                    displayIdToShowOn = display.getDisplayId();
                }
            }

            Intent intent = new Intent();
            intent.setClass(this, AiGameActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(
                    intent,
                    ActivityOptions
                            .makeBasic()
                            .setLaunchDisplayId(displayIdToShowOn)
                            .toBundle()
            );
        }
    }
}
