package com.djaphar.coffeepointsappcourier.Activities;

import android.os.Bundle;

import com.djaphar.coffeepointsappcourier.R;
import com.djaphar.coffeepointsappcourier.SupportClasses.ProductsRecyclerViewAdapter;
import com.djaphar.coffeepointsappcourier.ViewModels.MainViewModel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    private MainViewModel mainViewModel;
    private RecyclerView productsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        productsRecyclerView = findViewById(R.id.products_recycler_view);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.getProducts().observe(this, products -> {
            ProductsRecyclerViewAdapter adapter = new ProductsRecyclerViewAdapter(products);
            productsRecyclerView.setAdapter(adapter);
            productsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        });
    }
}
