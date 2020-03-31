package com.djaphar.coffeepointsappcourier.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.djaphar.coffeepointsappcourier.LocalDataClasses.User;
import com.djaphar.coffeepointsappcourier.R;
import com.djaphar.coffeepointsappcourier.SupportClasses.OtherClasses.MyAppCompactActivity;
import com.djaphar.coffeepointsappcourier.SupportClasses.OtherClasses.UserChangeChecker;
import com.djaphar.coffeepointsappcourier.ViewModels.MainViewModel;

import androidx.lifecycle.ViewModelProvider;

public class StatusErrorActivity extends MyAppCompactActivity {

    private UserChangeChecker userChangeChecker;
    private MainViewModel mainViewModel;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_error);

        userChangeChecker = new UserChangeChecker(this, new Handler());
        userChangeChecker.startUserChangeCheck();
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.getUser().observe(this, user -> {
            if (user == null) {
                return;
            }
            this.user = user;
            if (user.getSupervisor() == null) {
                return;
            }
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        userChangeChecker.startUserChangeCheck();
    }

    @Override
    protected void onPause() {
        super.onPause();
        userChangeChecker.stopUserChangeCheck();
    }

    public void requestUser() {
        if (user == null) {
            return;
        }
        mainViewModel.requestUser(user.get_id(), user.getToken(), user.getUserHash());
    }
}
