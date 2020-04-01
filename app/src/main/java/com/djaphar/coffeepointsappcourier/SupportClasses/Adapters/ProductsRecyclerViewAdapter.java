package com.djaphar.coffeepointsappcourier.SupportClasses.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.djaphar.coffeepointsappcourier.ApiClasses.Product;
import com.djaphar.coffeepointsappcourier.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProductsRecyclerViewAdapter extends RecyclerView.Adapter<ProductsRecyclerViewAdapter.ViewHolder> {

    private List<Product> products;

    public ProductsRecyclerViewAdapter(List<Product> products) {
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
        Product product = products.get(position);
        if (product == null) {
            return;
        }
        holder.productNameTv.setText(product.getName());
        holder.productTypeTv.setText(product.getType());
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ViewHolder extends  RecyclerView.ViewHolder {
        RelativeLayout parentLayout;
        TextView productNameTv, productTypeTv;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            parentLayout = itemView.findViewById(R.id.parent_layout_products);
            productNameTv = itemView.findViewById(R.id.product_name_tv);
            productTypeTv = itemView.findViewById(R.id.product_type_tv);
        }
    }
}
