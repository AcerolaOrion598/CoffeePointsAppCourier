package com.djaphar.coffeepointsappcourier.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.djaphar.coffeepointsappcourier.R;
import com.djaphar.coffeepointsappcourier.SupportClasses.StatusChecker;

import androidx.appcompat.app.AppCompatActivity;

public class StatusErrorActivity extends AppCompatActivity {

    private StatusChecker statusChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_error);

        statusChecker = new StatusChecker(this, new Handler(), new Intent(this, MainActivity.class));
        statusChecker.startStatusCheck();
    }

    @Override
    protected void onPause() {
        super.onPause();
        statusChecker.stopStatusCheck();
    }
}
