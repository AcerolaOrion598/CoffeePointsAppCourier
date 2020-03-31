package com.djaphar.coffeepointsappcourier.SupportClasses.OtherClasses;

import android.os.Handler;

public class UserChangeChecker {

    private Handler handler;
    private MyAppCompactActivity activity;

    public UserChangeChecker(MyAppCompactActivity activity, Handler handler) {
        this.handler = handler;
        this.activity = activity;
    }

    public void startUserChangeCheck() {
        asyncStatusChecker.run();
    }

    public void stopUserChangeCheck() {
        handler.removeCallbacksAndMessages(null);
    }

    private Runnable asyncStatusChecker = () -> {
        activity.requestUser();
        handler.postDelayed(this::startUserChangeCheck, 5000);
    };
}
