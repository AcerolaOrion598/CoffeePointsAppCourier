package com.djaphar.coffeepointsappcourier.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import com.djaphar.coffeepointsappcourier.R;
import com.djaphar.coffeepointsappcourier.SupportClasses.ProductsRecyclerViewAdapter;
import com.djaphar.coffeepointsappcourier.SupportClasses.StatusChecker;
import com.djaphar.coffeepointsappcourier.ViewModels.MainViewModel;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    private MainViewModel mainViewModel;
    private RecyclerView productsRecyclerView;
    private SwitchCompat statusSwitch;
    private TextView statusTv;
    private StatusChecker statusChecker;
    private static final int  LOGOUT_ID = 1, UNSET_OWNER_ID = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        statusChecker = new StatusChecker(new Handler(), this, new Intent(this, StatusErrorActivity.class));
        statusChecker.startStatusCheck();

        setContentView(R.layout.activity_main);
        productsRecyclerView = findViewById(R.id.products_recycler_view);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.getProducts().observe(this, products -> {
            ProductsRecyclerViewAdapter adapter = new ProductsRecyclerViewAdapter(products);
            productsRecyclerView.setAdapter(adapter);
            productsRecyclerView.setNestedScrollingEnabled(false);
            productsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        });
        statusSwitch = findViewById(R.id.status_switch);
        statusTv = findViewById(R.id.status_tv);
        TextView unsetOwnerBtn = findViewById(R.id.unset_owner_btn);
        TextView exitTv = findViewById(R.id.exit_tv);

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
    protected void onDestroy() {
        super.onDestroy();
        statusChecker.stopStatusCheck();
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
                        mainViewModel.unsetOwner();
                        Toast.makeText(this, R.string.mayoi_chan, Toast.LENGTH_SHORT).show();
                        break;
                    case LOGOUT_ID:
                        mainViewModel.logout();
                        Toast.makeText(this, R.string.ononoki_chan, Toast.LENGTH_SHORT).show();
                        break;
                }
            })
            .show();
    }
}
