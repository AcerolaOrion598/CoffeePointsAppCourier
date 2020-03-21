package com.djaphar.coffeepointsappcourier.SupportClasses;

import android.content.Intent;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class StatusChecker {
    private Handler handler;
    private AppCompatActivity activity;
    private Intent intent;
    private boolean first = true;

    public StatusChecker(Handler handler, AppCompatActivity activity, Intent intent) {
        this.handler = handler;
        this.activity = activity;
        this.intent = intent;
    }

    public void startStatusCheck() {
        asyncStatusChecker.run();
    }

    public void stopStatusCheck() {
        handler.removeCallbacks(asyncStatusChecker);
    }

    private Runnable asyncStatusChecker = () -> {
      try {
          if (!first) {
              activity.startActivity(intent);
              activity.finish();
          } else {
              first = false;
              handler.postDelayed(this::startStatusCheck, 7000);
          }
      } catch (Exception e) {
          e.printStackTrace();
      }
    };
}
