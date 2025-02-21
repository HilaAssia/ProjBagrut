package com.example.bagrutproject.views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bagrutproject.R;
import com.example.bagrutproject.model.Product;
import com.example.bagrutproject.utils.FireStoreHelper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    SharedPreferences sp;
    CartAdapter cartAdapter;
    RecyclerView rvProducts;
    Button buyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        rvProducts=findViewById(R.id.crvProducts);
        buyBtn=findViewById(R.id.button);
        buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CartActivity.this,UserActivity.class);
                startActivity(intent);
            }
        });

        setupRecyclerView(getCartItems());
    }

    private void setupRecyclerView(List<String> Ids){
        if (Ids!=null && !Ids.isEmpty()){
            // עכשיו נבצע את השאילתה עבור המוצרים עם ה-IDs האלו
            Query query = FireStoreHelper.getCollectionRef()
                    .whereIn("id", Ids)  // מחפש רק את המוצרים עם מזהים ברשימה
                    .orderBy("name", Query.Direction.DESCENDING);  // ניתן להוסיף סידור אם צריך

            FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>()
                    .setQuery(query, Product.class).build();

            rvProducts.setLayoutManager(new LinearLayoutManager(CartActivity.this));
            cartAdapter = new CartAdapter(options, CartActivity.this);
            rvProducts.setAdapter(cartAdapter);
        }
        //String docId = sp.getString("product",null);
        else {
            // במקרה שאין מוצרים ברשימה או שהיא ריקה, תוכל להציג הודעה או לפעול אחרת
            Log.d("CartActivity", "No products to display.");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        cartAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        cartAdapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        cartAdapter.notifyDataSetChanged();
    }

    public List<String> getCartItems(){
        sp = getSharedPreferences("cart", 0);
        String json = sp.getString("productList", ""); // מקבל את ה-JSON
        Gson gson = new Gson();

        // המרת ה-JSON חזרה לרשימה
        Type type = new TypeToken<List<String>>(){}.getType();
        List<String> Ids = gson.fromJson(json, type);

        return Ids;
    }

}