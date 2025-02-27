package com.example.bagrutproject.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;
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

    private String previousQuery = ""; // שומר את החיפוש הקודם
    private Runnable searchRunnable;
    SharedPreferences sp;
    private FBAuthHelper fbAuthHelper;
    private FireStoreHelper fireStoreHelper;
    ImageButton cart, logout;
    RecyclerView rvProducts;
    ProductsAdapter productsAdapter;
    SearchView searchBar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        searchBar=findViewById(R.id.search_bar);
        sp=getSharedPreferences("user cart",0);
        fbAuthHelper=new FBAuthHelper(this, this);
        fireStoreHelper=new FireStoreHelper(null);

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
        rvProducts= findViewById(R.id.urvProducts);
        setupRecyclerView(false);

        search();
    }

    private void setupRecyclerView(boolean isSearching){
        Query query = FireStoreHelper.getCollectionRefProduct().whereEqualTo("forSale",true).orderBy("name",
                Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Product> options=new FirestoreRecyclerOptions.Builder<Product>()
                .setQuery(query, Product.class).build();
        if (!isSearching) {
            rvProducts.setLayoutManager(new LinearLayoutManager(this));
            productsAdapter = new ProductsAdapter(options, this, true);
            rvProducts.setAdapter(productsAdapter);
        }
        else
            // עדכון ה-adapter עם אפשרויות חדשות
            productsAdapter.updateOptions(options);
    }

    @Override
    public void onStart() {
        super.onStart();
        productsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        productsAdapter.stopListening();// עצור את ההאזנה
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

    public void search(){
        // הגדרת מאזין לשינוי הטקסט בשדה החיפוש
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // הגדרת ריצה חדשה שתתבצע לאחר DEBOUNCE_DELAY
                searchRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (!query.equals(previousQuery)) {
                            previousQuery = query;
                            performSearch(query);
                        }
                    }
                };

                // סינון ברגע שהמשתמש מקיש Enter
                performSearch(query);
                Toast.makeText(UserActivity.this,"חיפשת: "+query,Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.isEmpty())
                    // סינון בזמן שהמשתמש מקליד
                    performSearch(newText);
                else {
                    // הגדרת ריצה חדשה שתתבצע לאחר DEBOUNCE_DELAY
                    searchRunnable = new Runnable() {
                        @Override
                        public void run() {
                            if (!newText.equals(previousQuery)) {
                                previousQuery = newText;
                                performSearch(newText);
                            }
                        }
                    };

                    // סינון ברגע שהמשתמש מקיש Enter
                    performSearch(newText);
                }

                Toast.makeText(UserActivity.this,"שינית את החיפוש ל: "+newText,Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    // פונקציה שתבצע את החיפוש ב-Firestore
    private void performSearch(String query) {
        // אם אין חיפוש, הראה את כל המוצרים
        if (query.isEmpty()) {
            setupRecyclerView(true);
        }

        else {
            // יצירת שאילתה מ-Firestore שמסננת את המוצרים לפי שם המוצר
            Query searchQuery = FireStoreHelper.getCollectionRefProduct().whereEqualTo("forSale", true)
                    .whereGreaterThanOrEqualTo("name", query)
                    .whereLessThanOrEqualTo("name", query + "\uf8ff"); // חיפוש מותאם

            // עדכון ה-FirestoreRecyclerOptions עם השאילתה המפולטרת
            FirestoreRecyclerOptions<Product> newOptions = new FirestoreRecyclerOptions.Builder<Product>()
                    .setQuery(searchQuery, Product.class)
                    .build();

            // עדכון ה-adapter עם אפשרויות חדשות
            productsAdapter.updateOptions(newOptions);
        }

        // עדכן את המידע שהאדפטר מאזין לו
        productsAdapter.notifyDataSetChanged();
    }
}