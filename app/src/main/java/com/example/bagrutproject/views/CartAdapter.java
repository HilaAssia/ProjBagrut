package com.example.bagrutproject.views;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bagrutproject.R;
import com.example.bagrutproject.model.Product;
import com.example.bagrutproject.utils.ImageUtils;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class CartAdapter extends FirestoreRecyclerAdapter<Product, CartAdapter.CartViewHolder> {

    private Context context;

    public CartAdapter(FirestoreRecyclerOptions<Product> options, Context context) {
        super(options);
        this.context=context;
    }

    @Override
    protected void onBindViewHolder(@NonNull CartAdapter.CartViewHolder holder, int position, @NonNull Product product) {
        if (product.getImage()!=null)
            holder.ivImage.setImageBitmap(ImageUtils.convertStringToBitmap(product.getImage()));
        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(product.getPrice());
        //holder.tvDetails.setText(product.getDetails());
        //holder.tvQuantity.setText(Integer.toString(product.getQuantity()));

        holder.itemView.setOnClickListener(v-> {
            Intent intent = new Intent(context, EditProductActivity.class);
            intent.putExtra("image", product.getImage());
            intent.putExtra("name", product.getName());
            intent.putExtra("price", product.getPrice());
            intent.putExtra("details", product.getDetails());
            //intent.putExtra("quantity", product.getQuantity());
            //intent.putExtra("forSale", product.getForSale());

            String docId=this.getSnapshots().getSnapshot(position).getId();
            intent.putExtra("docId", docId);
            context.startActivity(intent);
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailsActivity.class);
            intent.putExtra("isUser", true);

            intent.putExtra("image", product.getImage());
            intent.putExtra("name", product.getName());
            intent.putExtra("price", product.getPrice());
            intent.putExtra("details", product.getDetails());

            String docId=this.getSnapshots().getSnapshot(position).getId();
            intent.putExtra("docId", docId);
            context.startActivity(intent);
        });

    }

    @NonNull
    @Override
    public CartAdapter.CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new CartAdapter.CartViewHolder(view);    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice;//, tvDetails, tvQuantity;
        ImageView ivImage;
        //ImageButton ibIncrease, ibDecrease;


        public CartViewHolder (@NonNull View itemView) {
            super(itemView);
            ivImage=itemView.findViewById(R.id.imgIcon);
            tvName=itemView.findViewById(R.id.tvName);
            tvPrice=itemView.findViewById(R.id.tvPrice);
            //tvDetails=itemView.findViewById(R.id.tvDetails);
            //tvQuantity=itemView.findViewById(R.id.tvQuantity);

            //ibIncrease=itemView.findViewById(R.id.btnIncrease);
            //ibDecrease=itemView.findViewById(R.id.btnDecrease);
        }
    }
}

