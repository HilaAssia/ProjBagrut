package com.example.bagrutproject.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Spinner;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

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
    Spinner orderBySpinner, catgorySpinner;
    String orderBy, category;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        searchBar=findViewById(R.id.search_bar);
        sp=getSharedPreferences("user cart",0);
        fbAuthHelper=new FBAuthHelper(this, this);
        fireStoreHelper=new FireStoreHelper();

        orderBySpinner = findViewById(R.id.sOrderBy); // מוצא את הספינר מהעיצוב
        setOrderBySpinner(); // טוען קטגוריות לספינר
        orderBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // מאזין לבחירת קטגוריה
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedItem = parentView.getItemAtPosition(position).toString(); // מקבל את הקטגוריה שנבחרה
                Toast.makeText(UserActivity.this, "בחרת: " + selectedItem, Toast.LENGTH_SHORT).show(); // מציג הודעה
                orderBy = selectedItem; // שומר את הקטגוריה שנבחרה
                updateAdapter( orderBy, category);
                productsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // פעולה שלא מתבצעת כלום אם לא נבחר כלום
            }
        });

        catgorySpinner = findViewById(R.id.sCategory); // מוצא את הספינר מהעיצוב
        setCategories(); // טוען קטגוריות לספינר
        catgorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // מאזין לבחירת קטגוריה
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedItem = parentView.getItemAtPosition(position).toString(); // מקבל את הקטגוריה שנבחרה
                Toast.makeText(UserActivity.this, "בחרת: " + selectedItem, Toast.LENGTH_SHORT).show(); // מציג הודעה
                category = selectedItem;
                updateAdapter( orderBy,category); // שומר את הקטגוריה שנבחרה
                productsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // פעולה שלא מתבצעת כלום אם לא נבחר כלום
            }
        });

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
        setupRecyclerView();

        search();
    }

    private void setupRecyclerView(){
        Query query = FireStoreHelper.getCollectionRefProduct().whereEqualTo("forSale",true)
                .orderBy("name", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Product> options=new FirestoreRecyclerOptions.Builder<Product>()
                .setQuery(query, Product.class).build();
        rvProducts.setLayoutManager(new LinearLayoutManager(UserActivity.this));
        productsAdapter = new ProductsAdapter(options,UserActivity.this,true,fireStoreHelper);
        rvProducts.setAdapter(productsAdapter);
    }

    private void updateAdapter(String orderBy, String category) {
        Query query;

        if (category != null && !category.equals("categories")) {
            query = FireStoreHelper.getCollectionRefProduct()
                    .whereEqualTo("forSale", true)
                    .whereEqualTo("category", category);
        } else {
            query = FireStoreHelper.getCollectionRefProduct()
                    .whereEqualTo("forSale", true);
        }

        if (orderBy != null && !orderBy.equals("order by:")) {
            switch (orderBy) {
                case "highest price":
                    query = query.orderBy("price", Query.Direction.DESCENDING);
                    break;
                case "lowest price":
                    query = query.orderBy("price", Query.Direction.ASCENDING);
                    break;
                default:
                    query = query.orderBy("name", Query.Direction.DESCENDING);
            }
        }
        else {
            query = query.orderBy("name", Query.Direction.DESCENDING);
        }


        FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>()
                .setQuery(query, Product.class).build();

        productsAdapter.stopListening();
        productsAdapter.updateOptions(options);
        productsAdapter.startListening();
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
            updateAdapter( orderBy, category);
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

    public void updateCatSpinner(ArrayList<String> items) { // עדכון הספינר עם רשימת קטגוריות
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        catgorySpinner.setAdapter(adapter); // מחבר את הרשימה לספינר
    }

    public void updateOrderBySpinner(ArrayList<String> items) { // עדכון הספינר עם רשימת קטגוריות
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        orderBySpinner.setAdapter(adapter); // מחבר את הרשימה לספינר
    }

    public void setCategories() { // שליפת קטגוריות מ-Firestore
        ArrayList<String> categoriesList = new ArrayList<>();
        categoriesList.add("categories"); // קטגוריה ברירת מחדל

        FireStoreHelper.getCollectionRefCat().get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) { // עובר על כל מסמך
                            String categoryName = document.getString("category"); // מביא את שם הקטגוריה
                            categoriesList.add(categoryName); // מוסיף לרשימה
                        }
                        updateCatSpinner(categoriesList); // מעדכן את הספינר
                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException()); // מדווח על שגיאה
                    }
                });
    }

    public void setOrderBySpinner() { // שליפת קטגוריות מ-Firestore
        ArrayList<String> orderByList = new ArrayList<>();
        orderByList.add("order by:"); // קטגוריה ברירת מחדל

        orderByList.add("highest price");
        orderByList.add("lowest price");

        updateOrderBySpinner(orderByList); // מעדכן את הספינר
    }
}