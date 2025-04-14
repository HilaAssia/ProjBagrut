package com.example.bagrutproject.views;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
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
    TextView tvTotalPrice;
    Button buyBtn;
    ArrayList<Product> order;
    double totalPrice;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        fireStoreHelper=new FireStoreHelper(this);
        rvProducts=findViewById(R.id.crvProducts);
        tvTotalPrice=findViewById(R.id.tvTotalPrice);
        order=new ArrayList<>();
        getOrder(getCartItems(),CartActivity.this);
        buyBtn=findViewById(R.id.button);
        buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Order order=new Order(CartActivity.this.order,fireStoreHelper.getCurrentUser().getUid(),String.valueOf(totalPrice),fireStoreHelper.getCurrentUser().getEmail());
                fireStoreHelper.add(order,CartActivity.this);
                updateProductQuantity(order.getProducts(),1);
                Toast.makeText(CartActivity.this, "you will get an email when your order is ready :)", Toast.LENGTH_SHORT).show();
            }
        });

        setupRecyclerView(getCartItems());
    }

    private void setupRecyclerView(List<String> ids){
        if (ids!=null && !ids.isEmpty()){
            // עכשיו נבצע את השאילתה עבור המוצרים עם ה-IDs האלו
            Query query = FireStoreHelper.getCollectionRefProduct()
                    .whereIn("id", ids)  // מחפש רק את המוצרים עם מזהים ברשימה
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

    public void setTotalPrice(ArrayList<Product> products){
        totalPrice=0;
        for (Product product:products){
            totalPrice+=Integer.parseInt(product.getPrice());
        }
        tvTotalPrice.setText(tvTotalPrice.getText()+String.valueOf(totalPrice));
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
        sp = getSharedPreferences(fireStoreHelper.getCurrentUser().getUid(), 0);
        String json = sp.getString("productList", ""); // מקבל את ה-JSON
        Gson gson = new Gson();

        // המרת ה-JSON חזרה לרשימה
        Type type = new TypeToken<List<String>>(){}.getType();
        List<String> Ids = gson.fromJson(json, type);

        return Ids;
    }

    public void getOrder(List<String> ids, FireStoreHelper.FBReply listener){
        ArrayList<Product> o=new ArrayList<>();
        // בדיקה אם הרשימה ריקה
        if (ids == null || ids.isEmpty()) {
            listener.onProductsLoaded(o); // מחזיר מערך ריק אם אין IDs
            return;
        }
        fireStoreHelper.getCollectionRefProduct().whereIn("id", ids) // מחפש את כל המוצרים עם ה-IDs ברשימה
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Product product = document.toObject(Product.class); // המרה למסד הנתונים
                            o.add(product);
                        }
                    }
                    listener.onProductsLoaded(o); // במקרה של שגיאה מחזירים מערך ריק
                });

        Toast.makeText(this, this.order.size()+" מוצרים בעגלה", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void getAllSuccess(ArrayList<Product> products) {

    }

    @Override
    public void getOneSuccess(Product product) {

    }

    @Override
    public void onProductsLoaded(ArrayList<Product> products) {
        order=products;
        setTotalPrice(order);
        Toast.makeText(this, products.size()+"products loaded", Toast.LENGTH_SHORT).show();
    }

    public void updateProductQuantity(ArrayList<Product> order, int purchasedQuantity) {
        for (Product p:order){
            if (p.getQuantity()-purchasedQuantity<=0)
                Toast.makeText(this, "this product is out of stock!!!", Toast.LENGTH_LONG).show();
            else {
                p.setQuantity(p.getQuantity()-purchasedQuantity);
                fireStoreHelper.update(p.getId(),p);
                Toast.makeText(this, p.getQuantity()+" items are in stock", Toast.LENGTH_LONG).show();
            }
        }
    }

}