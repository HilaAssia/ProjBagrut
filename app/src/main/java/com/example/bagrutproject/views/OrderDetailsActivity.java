package com.example.bagrutproject.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bagrutproject.R;
import com.example.bagrutproject.model.Product;
import com.example.bagrutproject.utils.FireStoreHelper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailsActivity extends AppCompatActivity {

    FireStoreHelper fireStoreHelper;
    TextView tvUid, tvEmail, tvOrderCost, tvTimestamp;
    RecyclerView rvOrderProducts;
    Button acceptBtn;
    String docId, uEmail;
    ProductsAdapter productsAdapter;
    ArrayList<String> orderProductsIDs;
    String orderString="";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        tvUid=findViewById(R.id.tvUid);
        tvEmail=findViewById(R.id.tvEmail);
        tvOrderCost=findViewById(R.id.tvOrderCost);
        tvTimestamp=findViewById(R.id.tvTimestamp);
        rvOrderProducts=findViewById(R.id.rvOrderProducts);
        fireStoreHelper=new FireStoreHelper();

        docId = getIntent().getStringExtra("docId");
        if (docId != null && !docId.isEmpty()){
            tvUid.setText("user: "+getIntent().getStringExtra("uid"));
            uEmail=getIntent().getStringExtra("email");
            tvEmail.setText(uEmail);
            orderProductsIDs=getIntent().getStringArrayListExtra("products");
            setupRecyclerView(orderProductsIDs);
            tvOrderCost.setText("total cost: "+getIntent().getStringExtra("cost")+"₪");
            tvTimestamp.setText("timestamp: "+getIntent().getStringExtra("timestamp"));
        }

        acceptBtn=findViewById(R.id.btnAccept);
        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail(orderProductsIDs);
                fireStoreHelper.deleteOrder(docId);
                Intent intent = new Intent(OrderDetailsActivity.this, ManagerActivity.class);
                intent.putExtra("fragmentToLoad", "orders"); // נעביר מידע לאיזה Fragment לעבור
                startActivity(intent);
                finish();
            }
        });

    }

    private void setupRecyclerView(List<String> ids){
        if (ids!=null && !ids.isEmpty()){
            // עכשיו נבצע את השאילתה עבור המוצרים עם ה-IDs האלו
            Query query = FireStoreHelper.getCollectionRefProduct()
                    .whereIn("id", ids)  // מחפש רק את המוצרים עם מזהים ברשימה
                    .orderBy("name", Query.Direction.DESCENDING);  // ניתן להוסיף סידור אם צריך

            FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>()
                    .setQuery(query, Product.class).build();

            rvOrderProducts.setLayoutManager(new LinearLayoutManager(OrderDetailsActivity.this));
            productsAdapter = new ProductsAdapter(options, OrderDetailsActivity.this,true,fireStoreHelper);
            rvOrderProducts.setAdapter(productsAdapter);
            //productsAdapter.notifyDataSetChanged();
        }
        //String docId = sp.getString("product",null);
        else {
            // במקרה שאין מוצרים ברשימה או שהיא ריקה, תוכל להציג הודעה או לפעול אחרת
            Toast.makeText(this, "No products to display.", Toast.LENGTH_SHORT).show();
            Log.d("CartActivity", "No products to display.");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        productsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        productsAdapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        productsAdapter.notifyDataSetChanged();
    }

    public void sendEmail(ArrayList<String> orderProductsIDs) {
        fireStoreHelper.getCollectionRefProduct().whereIn("id", orderProductsIDs)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        ArrayList<Product> order = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Product product = document.toObject(Product.class);
                            order.add(product);
                        }

                        String orderSummary = buildOrderString(order); // בניית מחרוזת מסודרת

                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("message/rfc822");
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{uEmail.toString()});
                        intent.putExtra(Intent.EXTRA_SUBJECT, "GearUp - order receipt");
                        intent.putExtra(Intent.EXTRA_TEXT, "You ordered:\n\n" + orderSummary);

                        try {
                            this.startActivity(Intent.createChooser(intent, "בחר אפליקציה לשליחת מייל"));
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(this, "אין אפליקציות מייל מותקנות!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "שגיאה בטעינת המוצרים", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String buildOrderString(ArrayList<Product> order) {
        StringBuilder sb = new StringBuilder();
        int total = 0;

        for (Product product : order) {
            sb.append("• ").append(product.getName())
                    .append(" - ").append(product.getPrice())
                    .append(" ₪\n");
            total += Integer.parseInt(product.getPrice());
        }

        sb.append("\nTotal: ").append(total).append(" ₪");
        return sb.toString();
    }
}