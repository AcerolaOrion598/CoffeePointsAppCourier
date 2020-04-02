package com.djaphar.coffeepointsappcourier.SupportClasses.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.djaphar.coffeepointsappcourier.Activities.MainActivity;
import com.djaphar.coffeepointsappcourier.ApiClasses.Product;
import com.djaphar.coffeepointsappcourier.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProductsRecyclerViewAdapter extends RecyclerView.Adapter<ProductsRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Product> products, userProducts;
    private ArrayList<Boolean> changedList;
    private MainActivity mainActivity;

    public ProductsRecyclerViewAdapter(ArrayList<Product> products, ArrayList<Product> userProducts, MainActivity mainActivity) {
        this.products = products;
        this.userProducts = userProducts;
        this.mainActivity = mainActivity;
        changedList = new ArrayList<>(products.size());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.products_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);
        changedList.add(false);
        if (product == null) {
            return;
        }
        holder.productNameTv.setText(product.getName());
        holder.productTypeTv.setText(product.getType());
        for (Product userProduct : userProducts) {
            if (userProduct.get_id().equals(product.get_id())) {
                holder.productCheckbox.setChecked(true);
                break;
            }
        }

        holder.productCheckbox.setOnCheckedChangeListener((compoundButton, b) -> {
            changedList.set(position, !changedList.get(position));
            if (changedList.get(position)) {
                mainActivity.addUpdatedProduct(product);
            } else {
                mainActivity.removeUpdatedProduct(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ViewHolder extends  RecyclerView.ViewHolder {
        RelativeLayout parentLayout;
        TextView productNameTv, productTypeTv;
        CheckBox productCheckbox;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            parentLayout = itemView.findViewById(R.id.parent_layout_products);
            productNameTv = itemView.findViewById(R.id.product_name_tv);
            productTypeTv = itemView.findViewById(R.id.product_type_tv);
            productCheckbox = itemView.findViewById(R.id.product_checkbox);
        }
    }
}
