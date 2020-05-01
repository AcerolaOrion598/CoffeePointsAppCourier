package com.djaphar.coffeepointsappcourier.Activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.djaphar.coffeepointsappcourier.ApiClasses.Product;
import com.djaphar.coffeepointsappcourier.ApiClasses.UpdatableUser;
import com.djaphar.coffeepointsappcourier.LocalDataClasses.User;
import com.djaphar.coffeepointsappcourier.R;
import com.djaphar.coffeepointsappcourier.SupportClasses.Adapters.ProductsRecyclerViewAdapter;
import com.djaphar.coffeepointsappcourier.SupportClasses.OtherClasses.MyAppCompactActivity;
import com.djaphar.coffeepointsappcourier.SupportClasses.OtherClasses.PermissionDriver;
import com.djaphar.coffeepointsappcourier.SupportClasses.OtherClasses.UserChangeChecker;
import com.djaphar.coffeepointsappcourier.SupportClasses.Services.LocationUpdateService;
import com.djaphar.coffeepointsappcourier.ViewModels.MainViewModel;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends MyAppCompactActivity {

    private MainViewModel mainViewModel;
    private RecyclerView productsRecyclerView;
    private TextView statusTv, ownerNameTv;
    private EditText hintEd;
    private UserChangeChecker userChangeChecker;
    private User user;
    private UpdatableUser updatableUser;
    private ArrayList<Product> updatedProducts = new ArrayList<>();
    private ArrayList<Product> products;
    private Boolean visible, status;
    private String[] perms = new String[2];
    private HashMap<String, String> authHeaderMap = new HashMap<>();
    private static final int  LOGOUT_ID = 1, UNSET_OWNER_ID = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userChangeChecker = new UserChangeChecker(this, new Handler());
        setContentView(R.layout.activity_main);
        SwitchCompat visibleSwitch = findViewById(R.id.visible_switch);
        SwitchCompat statusSwitch = findViewById(R.id.status_switch);
        statusTv = findViewById(R.id.status_tv);
        productsRecyclerView = findViewById(R.id.products_recycler_view);
        ownerNameTv = findViewById(R.id.owner_name_tv);
        hintEd = findViewById(R.id.hint_ed);
        Button saveBtn = findViewById(R.id.save_btn);
        TextView unsetOwnerBtn = findViewById(R.id.unset_owner_btn);
        TextView exitTv = findViewById(R.id.exit_tv);
        perms[0] = Manifest.permission.ACCESS_COARSE_LOCATION;
        perms[1] = Manifest.permission.ACCESS_FINE_LOCATION;
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        mainViewModel.getProducts().observe(this, products -> {
            this.products = products;
            if (updatableUser == null) {
                return;
            }
            setProductListAdapter(products, updatableUser);
        });

        mainViewModel.getUser().observe(this, user -> {
            if (user != null) {
                if (this.user != null && !this.user.getSupervisor().equals(user.getSupervisor())) {
                    stopService(new Intent(this, LocationUpdateService.class));
                }

                if (user.getSupervisor() == null) {
                    startActivity(new Intent(this, StatusErrorActivity.class));
                    finish();
                    return;
                }

                this.user = user;
                authHeaderMap.put(getString(R.string.authorization_header), user.getToken());
                mainViewModel.requestSupervisor(user.getSupervisor());
                mainViewModel.requestSupervisorProducts(authHeaderMap, user.getSupervisor());
                mainViewModel.requestUpdatableUser(authHeaderMap);
                requestUser();
                return;
            }
            stopService(new Intent(this, LocationUpdateService.class));
            startActivity(new Intent(this, AuthActivity.class));
            finish();
        });

        mainViewModel.getUpdatableUser().observe(this, updatableUser -> {
            if (updatableUser != null) {
                this.updatableUser = updatableUser;
                visible = updatableUser.isActive();
                status = updatableUser.isCurrentlyNotHere();
                visibleSwitch.setChecked(updatableUser.isActive());
                statusSwitch.setChecked(updatableUser.isCurrentlyNotHere());
                if (updatableUser.isActive()) {
                    if (PermissionDriver.hasPerms(perms, getApplicationContext())) {
                        startLocationUpdateService();
                    } else {
                        PermissionDriver.requestPerms(this, perms);
                    }
                }
                String hint = updatableUser.getHint();
                if (hint == null) {
                    return;
                }
                hintEd.setText(hint);
            }

            if (products == null) {
                return;
            }
            setProductListAdapter(products, this.updatableUser);
        });

        mainViewModel.getSupervisor().observe(this, supervisor -> {
            if (supervisor == null) {
                return;
            }
            ownerNameTv.setText(supervisor.getName());
        });

        visibleSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> visible = isChecked);

        statusSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            status = isChecked;
            if (isChecked) {
                statusTv.setText(R.string.status_true);
                statusTv.setTextColor(getResources().getColor(R.color.colorGreen87));
            } else {
                statusTv.setText(R.string.status_false);
                statusTv.setTextColor(getResources().getColor(R.color.colorRed87));
            }
        });

        saveBtn.setOnClickListener(lView -> saveUpdates());

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        startLocationUpdateService();
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
                        setUpdatableUserOptions(false, false);
                        mainViewModel.requestUnsubscribe(user.get_id(), authHeaderMap);
                        break;
                    case LOGOUT_ID:
                        setUpdatableUserOptions(false, false);
                        mainViewModel.requestUpdateCourier(authHeaderMap, updatableUser,  true);
                        break;
                }
            })
            .show();
    }

    private void setProductListAdapter(ArrayList<Product> products, UpdatableUser updatableUser) {
        productsRecyclerView.setAdapter(new ProductsRecyclerViewAdapter(products, updatableUser.getProductList(), this));
        productsRecyclerView.setNestedScrollingEnabled(false);
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void saveUpdates() {
        stopService(new Intent(this, LocationUpdateService.class));
        setUpdatableUserOptions(visible, status);
        mainViewModel.requestUpdateCourier(authHeaderMap, updatableUser,false);
        requestProductsListToggle();
    }

    private void requestProductsListToggle() {
        for (Product product : updatedProducts) {
            mainViewModel.requestProductsListToggle(product.get_id(), authHeaderMap, user.getSupervisor());
        }
        updatedProducts.clear();
    }

    private void setUpdatableUserOptions(boolean active, boolean notHere) {
        updatableUser.setActive(active);
        updatableUser.setCurrentlyNotHere(notHere);
        updatableUser.setHint(hintEd.getText().toString());
        updatableUser.setCoordinates(null);
    }

    private void startLocationUpdateService() {
        Intent intent = new Intent(this, LocationUpdateService.class);
        stopService(intent);
        intent.putExtra("isActive", visible);
        intent.putExtra("isCurrentlyNotHere", status);
        intent.putExtra("supervisor", updatableUser.getSupervisor());
        intent.putExtra("hint", updatableUser.getHint());
        intent.putExtra("userId", user.get_id());
        intent.putExtra("authorization_header", getString(R.string.authorization_header));
        intent.putExtra("userToken", user.getToken());
        startService(intent);
    }

    public void addUpdatedProduct(Product product) {
        updatedProducts.add(product);
    }

    public void removeUpdatedProduct(Product product) {
        updatedProducts.remove(product);
    }

    public void requestUser() {
        if (user == null) {
            return;
        }
        mainViewModel.requestUser(authHeaderMap, user.getUserHash());
    }
}
