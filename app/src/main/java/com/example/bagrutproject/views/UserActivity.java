package com.example.bagrutproject.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bagrutproject.R;
import com.example.bagrutproject.model.Product;
import com.example.bagrutproject.utils.FBAuthHelper;
import com.example.bagrutproject.utils.FireStoreHelper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Query;

public class UserActivity extends AppCompatActivity implements FBAuthHelper.FBReply {

    SharedPreferences sp;
    private FBAuthHelper fbAuthHelper;
    ImageButton cart, logout;
    RecyclerView rvProducts;
    ProductsAdapter productsAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        sp=getSharedPreferences("user cart",0);
        fbAuthHelper=new FBAuthHelper(this, this);

        cart = findViewById(R.id.cartButton);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UserActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        logout = findViewById(R.id.logoutButton);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbAuthHelper.logoutUser();
                Intent intent = new Intent(UserActivity.this, LogInActivity.class);
                startActivity(intent);
            }
        });
        rvProducts= findViewById(R.id.rvProducts);
        setupRecyclerView();
    }

    private void setupRecyclerView(){
        Query query = FireStoreHelper.getCollectionRef().whereEqualTo("forSale",true).orderBy("name",
                Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Product> options=new FirestoreRecyclerOptions.Builder<Product>()
                .setQuery(query, Product.class).build();
        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        productsAdapter = new ProductsAdapter(options,this,true);
        rvProducts.setAdapter(productsAdapter);
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

    @Override
    public void createUserSuccess(FirebaseUser user) {

    }

    @Override
    public void loginSuccess(FirebaseUser user) {

    }

    @Override
    public void logoutSuccess(FirebaseUser user) {
        Toast.makeText(this, "success",
                Toast.LENGTH_SHORT).show();
    }

    public void addToCart(Product product){
        sp=getSharedPreferences("user cart",0);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString("product", product.toString(product.getImage(), product.getName(), product.getPrice(), product.getDetails(), product.getQuantity(), product.getForSale()));
        editor.commit();
    }
}