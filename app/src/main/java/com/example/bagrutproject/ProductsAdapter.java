package com.example.bagrutproject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ProductsAdapter extends FirestoreRecyclerAdapter<Product, ProductsAdapter.ProductViewHolder> {

    private Context context;

    public ProductsAdapter(FirestoreRecyclerOptions<Product> options, Context context) {
        super(options);
        this.context=context;
    }


    @Override
    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Product product) {
        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(product.getPrice());
        holder.tvDetails.setText(product.getDetails());

        product.setForSale(holder.checkBox.isChecked());

        holder.itemView.setOnClickListener(v-> {
            Intent intent = new Intent(context, EditProductActivity.class);
            intent.putExtra("name", product.getName());
            intent.putExtra("price", product.getPrice());
            intent.putExtra("details", product.getDetails());
            intent.putExtra("isForSale", product.isForSale());

            String docId=this.getSnapshots().getSnapshot(position).getId();
            intent.putExtra("docId", docId);
            context.startActivity(intent);
        });
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
        return new ProductViewHolder(view);
    }


    public class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvDetails;
        CheckBox checkBox;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName=itemView.findViewById(R.id.tvName);
            tvPrice=itemView.findViewById(R.id.tvPrice);
            tvDetails=itemView.findViewById(R.id.tvDetails);
            checkBox=itemView.findViewById(R.id.checkbox_forSale);
        }
    }
}
