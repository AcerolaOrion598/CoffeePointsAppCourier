package com.djaphar.coffeepointsappcourier.Activities;

import android.content.Intent;
import android.os.Bundle;

import com.djaphar.coffeepointsappcourier.ViewModels.AuthViewModel;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AuthViewModel authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        authViewModel.getUser().observe(this, user -> {
            if (user != null) {
                if (user.getSupervisor() == null) {
                    startNewActivity(new Intent(this, StatusErrorActivity.class));
                } else {
                    startNewActivity(new Intent(this, MainActivity.class));
                }
            } else {
                startNewActivity(new Intent(this, AuthActivity.class));
            }
        });
    }

    private void startNewActivity(Intent intent) {
        startActivity(intent);
        finish();
    }
}
