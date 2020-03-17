package com.djaphar.coffeepointsappcourier.SupportClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.djaphar.coffeepointsappcourier.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProductsRecyclerViewAdapter extends RecyclerView.Adapter<ProductsRecyclerViewAdapter.ViewHolder> {

    private ArrayList<String> products;

    public ProductsRecyclerViewAdapter(ArrayList<String> products) {
        this.products = products;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.products_list, parent, false);
//        viewHolder.parentLayout.setOnClickListener(lView -> {
//
//        });

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String product = products.get(position);
        holder.productNameTv.setText(product);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ViewHolder extends  RecyclerView.ViewHolder {
        RelativeLayout parentLayout;
        TextView productNameTv;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            parentLayout = itemView.findViewById(R.id.parent_layout_products);
            productNameTv = itemView.findViewById(R.id.product_name_tv);
        }
    }
}