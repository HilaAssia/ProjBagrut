package com.example.bagrutproject.views;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bagrutproject.R;
import com.example.bagrutproject.model.Product;
import com.example.bagrutproject.utils.ImageUtils;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ProductsAdapter extends FirestoreRecyclerAdapter<Product, ProductsAdapter.ProductViewHolder> {

    private Context context;
    boolean isUser;
    Product product;

    public ProductsAdapter(FirestoreRecyclerOptions<Product> options, Context context, boolean isUser) {
        super(options);
        this.context=context;
        this.isUser=isUser;
    }


    @Override
    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Product product) {
        this.product=product;
        if (product.getImage()!=null)
            holder.ivImage.setImageBitmap(ImageUtils.convertStringToBitmap(product.getImage()));
        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(product.getPrice());
        holder.tvDetails.setText(product.getDetails());
        holder.tvQuantity.setText(Integer.toString(product.getQuantity()));

        if(!isUser){
            holder.ibIncrease.setOnClickListener(v -> {
                product.quantityAdd(1);
            });
            holder.ibDecrease.setOnClickListener(v -> {
                product.quantityAdd(-1);
            });
            holder.itemView.setOnClickListener(v-> {
                Intent intent = new Intent(context, EditProductActivity.class);
                intent.putExtra("image", product.getImage());
                intent.putExtra("name", product.getName());
                intent.putExtra("price", product.getPrice());
                intent.putExtra("details", product.getDetails());
                intent.putExtra("quantity", product.getQuantity());
                intent.putExtra("forSale", product.getForSale());
                intent.putExtra("id",product.getId());

                String docId=this.getSnapshots().getSnapshot(position).getId();
                intent.putExtra("docId", docId);
                context.startActivity(intent);
            });
        }

        else {
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ProductDetailsActivity.class);
                intent.putExtra("image", product.getImage());
                intent.putExtra("name", product.getName());
                intent.putExtra("price", product.getPrice());
                intent.putExtra("details", product.getDetails());
                intent.putExtra("id",product.getId());

                String docId=this.getSnapshots().getSnapshot(position).getId();
                intent.putExtra("docId", docId);
                context.startActivity(intent);
            });
        }
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
        return new ProductViewHolder(view);
    }


    public class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvDetails, tvQuantity;
        ImageView ivImage;
        ImageButton ibIncrease, ibDecrease;


        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage=itemView.findViewById(R.id.imgIcon);
            tvName=itemView.findViewById(R.id.tvName);
            tvPrice=itemView.findViewById(R.id.tvPrice);
            tvDetails=itemView.findViewById(R.id.tvDetails);
            tvQuantity=itemView.findViewById(R.id.tvQuantity);

            ibIncrease=itemView.findViewById(R.id.btnIncrease);
            ibDecrease=itemView.findViewById(R.id.btnDecrease);
        }
    }
}
