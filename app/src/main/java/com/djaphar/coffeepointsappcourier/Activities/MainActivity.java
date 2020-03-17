package com.djaphar.coffeepointsappcourier.Activities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.djaphar.coffeepointsappcourier.R;
import com.djaphar.coffeepointsappcourier.SupportClasses.ProductsRecyclerViewAdapter;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        exitTv.setOnClickListener(lView -> createLogoutDialog());
    }

    private void createLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
            .setTitle(R.string.logout_dialog_title)
            .setMessage(R.string.logout_dialog_message)
            .setNegativeButton(R.string.logout_dialog_negative_btn, (dialogInterface, i) -> dialogInterface.cancel())
            .setPositiveButton(R.string.logout_dialog_positive_btn, (dialogInterface, i) -> {
                mainViewModel.logout();
                Toast.makeText(this, R.string.ononoki_chan, Toast.LENGTH_SHORT).show();
            })
            .show();
    }
}
