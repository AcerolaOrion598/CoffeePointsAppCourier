package com.djaphar.coffeepointsappcourier.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.djaphar.coffeepointsappcourier.ApiClasses.Coordinates;
import com.djaphar.coffeepointsappcourier.ApiClasses.UpdatedUser;
import com.djaphar.coffeepointsappcourier.LocalDataClasses.User;
import com.djaphar.coffeepointsappcourier.R;
import com.djaphar.coffeepointsappcourier.SupportClasses.Adapters.ProductsRecyclerViewAdapter;
import com.djaphar.coffeepointsappcourier.SupportClasses.OtherClasses.MyAppCompactActivity;
import com.djaphar.coffeepointsappcourier.SupportClasses.OtherClasses.UserChangeChecker;
import com.djaphar.coffeepointsappcourier.ViewModels.MainViewModel;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends MyAppCompactActivity {

    private MainViewModel mainViewModel;
    private RecyclerView productsRecyclerView;
    private SwitchCompat statusSwitch;
    private TextView statusTv, ownerNameTv;
    private UserChangeChecker userChangeChecker;
    private User user;
    private static final int  LOGOUT_ID = 1, UNSET_OWNER_ID = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userChangeChecker = new UserChangeChecker(this, new Handler());
        setContentView(R.layout.activity_main);
        statusSwitch = findViewById(R.id.status_switch);
        statusTv = findViewById(R.id.status_tv);
        productsRecyclerView = findViewById(R.id.products_recycler_view);
        ownerNameTv = findViewById(R.id.owner_name_tv);
        TextView unsetOwnerBtn = findViewById(R.id.unset_owner_btn);
        TextView exitTv = findViewById(R.id.exit_tv);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.getProducts().observe(this, products -> {
            productsRecyclerView.setAdapter(new ProductsRecyclerViewAdapter(products));
            productsRecyclerView.setNestedScrollingEnabled(false);
            productsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        });
        mainViewModel.getUser().observe(this, user -> {
            if (user != null) {
                this.user = user;
                if (user.getSupervisor() == null) {
                    startActivity(new Intent(this, StatusErrorActivity.class));
                    finish();
                    return;
                }
                ownerNameTv.setText(user.getSupervisor());
                return;
            }
            startActivity(new Intent(this, AuthActivity.class));
            finish();
        });

        statusSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                statusTv.setText(R.string.status_true);
                statusTv.setTextColor(getResources().getColor(R.color.colorGreen87));
            } else {
                statusTv.setText(R.string.status_false);
                statusTv.setTextColor(getResources().getColor(R.color.colorRed87));
            }
        });

        unsetOwnerBtn.setOnClickListener(lView -> createDialog(R.string.unset_owner_dialog_title, R.string.unset_owner_dialog_message, UNSET_OWNER_ID));

        exitTv.setOnClickListener(lView -> createDialog(R.string.logout_dialog_title, R.string.logout_dialog_message, LOGOUT_ID));
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

    private void createDialog(int title, int message, int methodId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
            .setTitle(title)
            .setMessage(message)
            .setNegativeButton(R.string.dialog_negative_btn, (dialogInterface, i) -> dialogInterface.cancel())
            .setPositiveButton(R.string.dialog_positive_btn, (dialogInterface, i) -> {
                switch (methodId) {
                    case UNSET_OWNER_ID:
                        mainViewModel.unsetOwner(user.get_id(), user.getToken(),
                                new UpdatedUser(true, false, null, new Coordinates(37.55, 35.77)));
                        break;
                    case LOGOUT_ID:
                        mainViewModel.logout();
                        break;
                }
            })
            .show();
    }

    public void requestUser() {
        if (user == null) {
            return;
        }
        mainViewModel.requestUser(user.get_id(), user.getToken(), user.getUserHash());
    }
}
