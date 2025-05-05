package com.example.bagrutproject.views;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bagrutproject.R;
import com.example.bagrutproject.model.Order;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.ArrayList;

public class OrdersAdapter extends FirestoreRecyclerAdapter<Order, OrdersAdapter.OrderViewHolder> {
    private Context context;
    Order order;

    public OrdersAdapter(FirestoreRecyclerOptions<Order> options, Context context) {
        super(options);
        this.context=context;
    }

    @Override
    protected void onBindViewHolder(@NonNull OrderViewHolder holder, int position, @NonNull Order order) {
        this.order=order;
        holder.tvUid.setText(order.getEmail());
        holder.tvTP.setText(order.getTotalPrice()+"₪");
        holder.tvTimestamp.setText(order.getTimestamp());

        holder.itemView.setOnClickListener(v -> {
            ArrayList<String> ids=new ArrayList<>();
            Intent intent = new Intent(context, OrderDetailsActivity.class);
            intent.putExtra("uid", order.getUid());
            intent.putExtra("email", order.getEmail());
            intent.putStringArrayListExtra("products", order.getProductsIDs(ids));
            intent.putExtra("cost", order.getTotalPrice());
            intent.putExtra("timestamp", order.getTimestamp());

            String docId=this.getSnapshots().getSnapshot(position).getId();
            intent.putExtra("docId", docId);
            context.startActivity(intent);
        });

    }

    @NonNull
    @Override
    public OrdersAdapter.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        return new OrdersAdapter.OrderViewHolder(view);
    }


    public class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvUid, tvTP, tvTimestamp;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUid=itemView.findViewById(R.id.tvUid);
            tvTP=itemView.findViewById(R.id.tvTP);
            tvTimestamp=itemView.findViewById(R.id.tvTimeStamp);
        }
    }
}
