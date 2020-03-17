package com.djaphar.coffeepointsappcourier.ViewModels;

import android.app.Application;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class MainViewModel extends AndroidViewModel {

    private MutableLiveData<ArrayList<String>> products = new MutableLiveData<>();
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

        productList.add(product1);
        productList.add(product2);
        productList.add(product3);
        productList.add(product4);
        productList.add(product5);
        productList.add(product6);
        productList.add(product7);

        products.setValue(productList);
    }

    public MutableLiveData<ArrayList<String>> getProducts() {
        return products;
    }
}
