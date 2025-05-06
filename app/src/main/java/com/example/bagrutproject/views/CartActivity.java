package com.example.bagrutproject.views;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity implements FireStoreHelper.FBReply {

    FireStoreHelper fireStoreHelper; // עוזר לביצוע פעולות על Firestore
    CartAdapter cartAdapter; // מתאם להצגת מוצרים בעגלה
    RecyclerView rvProducts; // רכיב להצגת המוצרים
    TextView tvTotalPrice; // תצוגה של המחיר הכולל
    Button buyBtn; // כפתור רכישה
    ArrayList<Product> order; // רשימת המוצרים בעגלה
    double totalPrice; // מחיר כולל של כל המוצרים
    static HashMap<String,Integer> productInventoryQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) { // פונקציה שמופעלת כשהמסך נוצר
        super.onCreate(savedInstanceState); // קריאה לפונקציית העל
        setContentView(R.layout.activity_cart); // קישור למסך הפעולה (activity_cart)

        // אתחול רכיבים
        fireStoreHelper = new FireStoreHelper(this);
        rvProducts = findViewById(R.id.crvProducts);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        buyBtn = findViewById(R.id.button);
        order = new ArrayList<>();
        productInventoryQuantity=new HashMap<>();

        // קבלת המוצרים מהעגלה והבאתם מהמסד
        getOrder(getCartItems(), CartActivity.this);

        // טיפול בלחיצה על כפתור הקנייה
        buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (order.isEmpty()) { // אם העגלה ריקה
                    Toast.makeText(CartActivity.this, "Cart is empty!", Toast.LENGTH_SHORT).show(); // הצגת הודעה
                    return; // יציאה מהפונקציה
                }
                // אם יש מוצרים בעגלה: יצירת אובייקט של הזמנה חדשה
                Order newOrder = new Order(
                        CartActivity.this.order, // העתקת המוצרים
                        fireStoreHelper.getCurrentUser().getUid(), // הוספת מזהה משתמש
                        String.valueOf(totalPrice), // הוספת מחיר סופי
                        fireStoreHelper.getCurrentUser().getEmail() // הוספת כתובת אימייל
                );
                if (updateInventoryQuantity(newOrder.getProducts())) { // עדכון כמויות מלאי
                    fireStoreHelper.add(newOrder, CartActivity.this); // הוספת ההזמנה למסד הנתונים
                    Toast.makeText(CartActivity.this, "You will get an email when your order is ready :) ", Toast.LENGTH_SHORT).show(); // הצגת הודעה למשתמש
                }
                else
                    Toast.makeText(CartActivity.this, "one or more of your order is out of stock!", Toast.LENGTH_SHORT).show();
            }
        });


        // הגדרת ה-RecyclerView להצגת מוצרים
        setupRecyclerView(getCartItems());
    }

    // שליפת רשימת מזהים של מוצרים מהזיכרון המקומי
    public List<String> getCartItems() {
        SharedPreferences sp = getSharedPreferences(fireStoreHelper.getCurrentUser().getEmail(), 0);
        String json = sp.getString("cartMap", null); // שליפת מפת העגלה
        Gson gson = new Gson();

        Type type = new TypeToken<Map<String, Integer>>() {}.getType();
        Map<String, Integer> cartMap = gson.fromJson(json, type);

        if (cartMap == null) {
            return new ArrayList<>(); // עגלה ריקה
        }

        return new ArrayList<>(cartMap.keySet()); // מחזיר את כל ה-productId ברשימה
    }

    public int getProductQuantity(String productId) {
        SharedPreferences sp = getSharedPreferences(fireStoreHelper.getCurrentUser().getEmail(), 0);
        String json = sp.getString("cartMap", null); // שליפת העגלה כ-JSON
        Gson gson = new Gson();

        Type type = new TypeToken<Map<String, Integer>>() {}.getType();
        Map<String, Integer> cartMap = gson.fromJson(json, type);

        if (cartMap != null && cartMap.containsKey(productId)) {
            return cartMap.get(productId);
        }

        return 0; // אם לא קיים – נחזיר 0
    }

    // טעינת פרטי המוצרים לפי מזהים מהמסד
    public void getOrder(List<String> ids, FireStoreHelper.FBReply listener) {
        ArrayList<Product> tempOrder = new ArrayList<>(); // יצירת רשימה זמנית של מוצרים
        if (ids == null || ids.isEmpty()) { // אם הרשימה ריקה
            listener.onProductsLoaded(tempOrder); // החזרת רשימה ריקה
            return;//סיום הפעולה
        }

        fireStoreHelper.getCollectionRefProduct().whereIn("id", ids) // חיפוש מוצרים לפי מזהים
                .get()
                .addOnCompleteListener(task -> { // האזנה להשלמת החיפוש
                    if (task.isSuccessful() && task.getResult() != null) { // אם החיפוש הצליח
                        for (QueryDocumentSnapshot document : task.getResult()) { // מעבר על כל תוצאה
                            Product product = document.toObject(Product.class); // המרת מסמך לאובייקט Product
                            productInventoryQuantity.put(product.getId(),product.getQuantity());
                            product.setQuantity(getProductQuantity(product.getId()));
                            tempOrder.add(product); // הוספת המוצר לרשימה
                        }
                    }
                    listener.onProductsLoaded(tempOrder); // קריאה חזרה עם הרשימה
                });
    }

    // חישוב ועדכון מחיר כולל
    public void setTotalPrice(ArrayList<Product> products) {
        totalPrice = 0; // איפוס מחיר
        for (Product product : products) { // מעבר על כל מוצר
            totalPrice += Double.parseDouble(product.getPrice())*product.getQuantity(); // חיבור מחיר המוצר לסכום הכללי
        }
        tvTotalPrice.setText("Total: " + totalPrice + "₪"); // הצגת המחיר הכולל במסך
    }

    // עדכון כמויות מוצרים במלאי אחרי רכישה
    public boolean updateInventoryQuantity(ArrayList<Product> order) {
        Product product;
        for (Product p : order) { // לולאה שעוברת על כל המוצרים בהזמנה
            product =new Product(p);
            if (productInventoryQuantity.get(p.getId()) - p.getQuantity() < 0) { // אם אין מספיק מלאי
                Toast.makeText(this, p.getName() + ": is out of stock!!!", Toast.LENGTH_LONG).show(); // הודעת חוסר מלאי
                return false;
            }
            else {// אם יש מספיק מלאי
                product.setQuantity(productInventoryQuantity.get(p.getId()) - p.getQuantity()); // הורדת הכמות במלאי
                fireStoreHelper.update(product.getId(), product); // עדכון המוצר במסד הנתונים
                Toast.makeText(this, p.getQuantity() + " items are in stock", Toast.LENGTH_LONG).show(); // הצגת הכמות החדשה
            }
        }
        return true;
    }

    // הגדרת RecyclerView להצגת המוצרים לפי המזהים
    private void setupRecyclerView(List<String> ids) {
        if (ids != null && !ids.isEmpty()) { // אם יש מוצרים להציג
            Query query = FireStoreHelper.getCollectionRefProduct()
                    .whereIn("id", ids) // יצירת שאילתה לפי מזהים
                    .orderBy("name", Query.Direction.DESCENDING); // סידור תוצאות לפי שם מוצר

            FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>()
                    .setQuery(query, Product.class) // הגדרת שאילתה והתוצאה כ-Product
                    .build();

            rvProducts.setLayoutManager(new LinearLayoutManager(CartActivity.this)); // קביעת סידור ליניארי של המוצרים
            cartAdapter = new CartAdapter(options, CartActivity.this, fireStoreHelper.getCurrentUser()); // יצירת מתאם לרשימה
            rvProducts.setAdapter(cartAdapter); // חיבור המתאם ל-RecyclerView
        } else {
            Log.d("CartActivity", "No products to display."); // הודעת דיבוג שאין מוצרים
        }
    }

    @Override
    public void onStart() { // כשהמסך מתחיל להופיע
        super.onStart();
        if (cartAdapter != null)
            cartAdapter.startListening(); // התחלת האזנה לשינויים במוצרים
    }

    @Override
    public void onStop() { // כשהמסך מפסיק להופיע
        super.onStop();
        if (cartAdapter != null)
            cartAdapter.stopListening(); // עצירת האזנה לשינויים
    }

    @Override
    public void onResume() { // כשהמשתמש חוזר למסך
        super.onResume();
        if (cartAdapter != null)
            cartAdapter.notifyDataSetChanged(); // רענון התצוגה
    }

    // מימוש ריק של קבלת כל המוצרים (נדרש מהממשק FBReply)
    @Override
    public void getAllSuccess(ArrayList<Product> products) {}

    // מימוש ריק של קבלת מוצר בודד (נדרש מהממשק FBReply)
    @Override
    public void getOneSuccess(Product product) {}

    // תגובה לטעינת המוצרים מהמסד
    @Override
    public void onProductsLoaded(ArrayList<Product> products) {
        this.order = products; // שמירת המוצרים ברשימת העגלה
        if (products.isEmpty()) { // אם אין מוצרים
            Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show(); // הודעה על עגלה ריקה
        } else {
            setTotalPrice(order); // חישוב ואיתחול מחיר כולל
            Toast.makeText(this, products.size() + " products loaded", Toast.LENGTH_SHORT).show(); // הודעה על מספר מוצרים שהועמסו
        }
    }

    @Override
    public void onDeleteSuccess() {

    }

    public static int getProductInventoryQuantity(String id){
        return productInventoryQuantity.get(id);
    }
}