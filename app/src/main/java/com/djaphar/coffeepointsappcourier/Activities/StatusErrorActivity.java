package com.djaphar.coffeepointsappcourier.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.djaphar.coffeepointsappcourier.LocalDataClasses.User;
import com.djaphar.coffeepointsappcourier.R;
import com.djaphar.coffeepointsappcourier.SupportClasses.OtherClasses.MyAppCompactActivity;
import com.djaphar.coffeepointsappcourier.SupportClasses.OtherClasses.UserChangeChecker;
import com.djaphar.coffeepointsappcourier.ViewModels.MainViewModel;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

public class StatusErrorActivity extends MyAppCompactActivity {

    private UserChangeChecker userChangeChecker;
    private MainViewModel mainViewModel;
    private User user;
    private TextView statusErrorExitTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_error);

        userChangeChecker = new UserChangeChecker(this, new Handler());
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.getUser().observe(this, user -> {
            if (user == null) {
                startActivity(new Intent(this, AuthActivity.class));
                finish();
                return;
            }
            this.user = user;
            if (user.getSupervisor() == null) {
                return;
            }
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
        statusErrorExitTv = findViewById(R.id.status_error_exit_tv);
        statusErrorExitTv.setOnClickListener(lView -> createLogoutDialog());
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

    private void createLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setTitle(R.string.logout_dialog_title)
                .setMessage(R.string.logout_dialog_message)
                .setNegativeButton(R.string.dialog_negative_btn, (dialogInterface, i) -> dialogInterface.cancel())
                .setPositiveButton(R.string.dialog_positive_btn, (dialogInterface, i) -> mainViewModel.logout())
                .show();
    }

    public void requestUser() {
        if (user == null) {
            return;
        }
        mainViewModel.requestUser(user.get_id(), user.getToken(), user.getUserHash());
    }
}
