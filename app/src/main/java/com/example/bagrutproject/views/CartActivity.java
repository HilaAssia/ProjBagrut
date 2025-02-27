package com.example.bagrutproject.views;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bagrutproject.R;
import com.example.bagrutproject.model.Order;
import com.example.bagrutproject.model.Product;
import com.example.bagrutproject.utils.FireStoreHelper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity implements FireStoreHelper.FBReply {

    FireStoreHelper fireStoreHelper;
    SharedPreferences sp;
    CartAdapter cartAdapter;
    RecyclerView rvProducts;
    Button buyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        fireStoreHelper=new FireStoreHelper(this);
        rvProducts=findViewById(R.id.crvProducts);
        buyBtn=findViewById(R.id.button);
        buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Product[] products=getOrder(getCartItems(),CartActivity.this);
                Order order=new Order(products);
                fireStoreHelper.add(order,CartActivity.this);
            }
        });

        setupRecyclerView(getCartItems());
    }

    private void setupRecyclerView(List<String> Ids){
        if (Ids!=null && !Ids.isEmpty()){
            // עכשיו נבצע את השאילתה עבור המוצרים עם ה-IDs האלו
            Query query = FireStoreHelper.getCollectionRefProduct()
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
        if (cartAdapter!=null)
            cartAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (cartAdapter!=null)
            cartAdapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (cartAdapter!=null)
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

    public Product[] getOrder(List<String> ids, FireStoreHelper.FBReply listener){
        // בדיקה אם הרשימה ריקה
        if (ids == null || ids.isEmpty()) {
            listener.onProductsLoaded(new Product[0]); // מחזיר מערך ריק אם אין IDs
        }

        fireStoreHelper.getCollectionRefOrder().whereIn("id", ids) // מחפש את כל המוצרים עם ה-IDs ברשימה
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Product> products = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Product product = document.toObject(Product.class); // המרה למסד הנתונים
                            products.add(product);
                        }
                        listener.onProductsLoaded(products.toArray(new Product[0]));
                    } else {
                        listener.onProductsLoaded(new Product[0]); // במקרה של שגיאה מחזירים מערך ריק
                    }
                });
        return new Product[0];
    }

    @Override
    public void getAllSuccess(ArrayList<Product> products) {

    }

    @Override
    public void getOneSuccess(Product product) {

    }

    @Override
    public Product[] onProductsLoaded(Product[] products) {
        return products;
    }
}