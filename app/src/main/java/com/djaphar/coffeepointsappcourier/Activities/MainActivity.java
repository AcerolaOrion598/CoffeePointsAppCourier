package com.djaphar.coffeepointsappcourier.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;

import com.djaphar.coffeepointsappcourier.ApiClasses.Coordinates;
import com.djaphar.coffeepointsappcourier.ApiClasses.Product;
import com.djaphar.coffeepointsappcourier.ApiClasses.UpdatableUser;
import com.djaphar.coffeepointsappcourier.LocalDataClasses.User;
import com.djaphar.coffeepointsappcourier.R;
import com.djaphar.coffeepointsappcourier.SupportClasses.Adapters.ProductsRecyclerViewAdapter;
import com.djaphar.coffeepointsappcourier.SupportClasses.OtherClasses.MyAppCompactActivity;
import com.djaphar.coffeepointsappcourier.SupportClasses.OtherClasses.UserChangeChecker;
import com.djaphar.coffeepointsappcourier.ViewModels.MainViewModel;

import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends MyAppCompactActivity {

    private MainViewModel mainViewModel;
    private RecyclerView productsRecyclerView;
    private TextView statusTv, ownerNameTv;
    private UserChangeChecker userChangeChecker;
    private User user;
    private UpdatableUser updatableUser;
    private ArrayList<Product> updatedProducts = new ArrayList<>();
    private ArrayList<Product> products;
    private Coordinates coordinates = new Coordinates(37.55, 35.77);
    private Boolean visible, status;
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
        Button saveBtn = findViewById(R.id.save_btn);
        TextView unsetOwnerBtn = findViewById(R.id.unset_owner_btn);
        TextView exitTv = findViewById(R.id.exit_tv);
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
                this.user = user;
                if (user.getSupervisor() == null) {
                    startActivity(new Intent(this, StatusErrorActivity.class));
                    finish();
                    return;
                }
                mainViewModel.requestSupervisor(user.getSupervisor());
                mainViewModel.requestSupervisorProducts(user.getToken(), user.getSupervisor());
                mainViewModel.requestUpdatableUser(user.get_id(), user.getToken());
                return;
            }
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

    private void createDialog(int title, int message, int methodId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
            .setTitle(title)
            .setMessage(message)
            .setNegativeButton(R.string.dialog_negative_btn, (dialogInterface, i) -> dialogInterface.cancel())
            .setPositiveButton(R.string.dialog_positive_btn, (dialogInterface, i) -> {
                switch (methodId) {
                    case UNSET_OWNER_ID:
                        setUpdatableUserOptions(false, false, null, coordinates);
                        mainViewModel.requestUpdateCourier(user.get_id(), user.getToken(), updatableUser,true);
                        break;
                    case LOGOUT_ID:
                        mainViewModel.logout();
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
        setUpdatableUserOptions(visible, status, user.getSupervisor(), coordinates);
        mainViewModel.requestUpdateCourier(user.get_id(), user.getToken(), updatableUser, false);
        requestProductsListToggle();
    }

    private void requestProductsListToggle() {
        for (Product product : updatedProducts) {
            mainViewModel.requestProductsListToggle(user.get_id(), product.get_id(), user.getToken(), user.getSupervisor());
        }
        updatedProducts.clear();
    }

    private void setUpdatableUserOptions(boolean active, boolean notHere, String supervisorId, Coordinates coordinates) {
        updatableUser.setActive(active);
        updatableUser.setCurrentlyNotHere(notHere);
        updatableUser.setSupervisor(supervisorId);
        updatableUser.setCoordinates(coordinates);
    }

//    private void refresh() {
//        mainViewModel.requestUser(user.get_id(), user.getToken(), user.getUserHash());
//        mainViewModel.requestSupervisor(user.getSupervisor());
//        mainViewModel.requestSupervisorProducts(user.getToken(), user.getSupervisor());
//        mainViewModel.requestUpdatableUser(user.get_id(), user.getToken());
//    }

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
        mainViewModel.requestUser(user.get_id(), user.getToken(), user.getUserHash());
//        mainViewModel.requestSupervisor(user.getSupervisor());
//        mainViewModel.requestSupervisorProducts(user.getToken(), user.getSupervisor());
//        mainViewModel.requestUpdatableUser(user.get_id(), user.getToken());
    }
}
