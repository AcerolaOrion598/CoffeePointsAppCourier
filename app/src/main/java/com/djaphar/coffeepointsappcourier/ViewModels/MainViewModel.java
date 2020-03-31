package com.djaphar.coffeepointsappcourier.ViewModels;

import android.app.Application;

import com.djaphar.coffeepointsappcourier.LocalDataClasses.User;
import com.djaphar.coffeepointsappcourier.LocalDataClasses.UserDao;
import com.djaphar.coffeepointsappcourier.LocalDataClasses.UserRoom;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class MainViewModel extends AndroidViewModel {

    private LiveData<User> userLiveData;
    private MutableLiveData<ArrayList<String>> products = new MutableLiveData<>();
    private UserDao userDao;
    private ArrayList<String> productList = new ArrayList<>();

    public MainViewModel(@NonNull Application application) {
        super(application);

        productList.clear();
        String product1 ="Кофеёк 1";
        String product2 ="Кофеёк 2";
        String product3 ="Мороженное";
        String product4 ="Кофеёк 3";
        String product5 ="Блинчики";
        String product6 ="Мороженка 2";
        String product7 ="Блинчики 2";
        String product8 ="Блинчики 3";
        String product9 ="Блинчики 4";
        String product10 ="Блинчики 5";

        productList.add(product1);
        productList.add(product2);
        productList.add(product3);
        productList.add(product4);
        productList.add(product5);
        productList.add(product6);
        productList.add(product7);
        productList.add(product8);
        productList.add(product9);
        productList.add(product10);

        products.setValue(productList);
        UserRoom userRoom = UserRoom.getDatabase(application);
        userDao = userRoom.userDao();
        userLiveData = userDao.getUserLiveData();
    }

    public MutableLiveData<ArrayList<String>> getProducts() {
        return products;
    }

    public LiveData<User> getUser() {
        return userLiveData;
    }

    public void logout() {
        UserRoom.databaseWriteExecutor.execute(() -> userDao.deleteUser());
    }

    public void unsetOwner() { }
}
