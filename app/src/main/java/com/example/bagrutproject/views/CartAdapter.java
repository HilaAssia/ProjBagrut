package com.example.bagrutproject.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bagrutproject.R;
import com.example.bagrutproject.model.Product;
import com.example.bagrutproject.utils.ImageUtils;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class CartAdapter extends FirestoreRecyclerAdapter<Product, CartAdapter.CartViewHolder> {

    private Context context; // משתנה לשמירת הקשר למסך הנוכחי
    private FirebaseUser user;

    // בנאי של המתאם, מקבל את האפשרויות לטעינה ואת הקונטקסט
    public CartAdapter(FirestoreRecyclerOptions<Product> options, Context context, FirebaseUser user) {
        super(options); // קריאה לבנאי של FirestoreRecyclerAdapter
        this.context = context; // שמירת הקשר שהתקבל
        this.user = user;
    }

    // פונקציה שמקשרת בין אובייקט מוצר לבין תצוגת האייטם בעגלה
    @Override
    protected void onBindViewHolder(@NonNull CartAdapter.CartViewHolder holder, int position, @NonNull Product product) {
        // אם למוצר יש תמונה, המרה ממחרוזת לביטמאפ והצגה בתמונה
        if (product.getImage() != null)
            holder.ivImage.setImageBitmap(ImageUtils.convertStringToBitmap(product.getImage()));

        // הצגת שם המוצר
        holder.tvName.setText(product.getName());

        // הצגת מחיר המוצר
        holder.tvPrice.setText(product.getPrice() + "₪");

        // (שורות שמורות במידה ורוצים להציג פרטים נוספים)
        // holder.tvDetails.setText(product.getDetails());
        int quantity = getProductQuantityFromCart(product.getId());
        holder.tvQuantity.setText(String.valueOf(quantity));
        holder.ibIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseQuantity(product.getId());
                // עדכון תצוגה
                int quantity = getProductQuantityFromCart(product.getId());
                holder.tvQuantity.setText(String.valueOf(quantity));
            }
        });

        holder.ibDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO הוספה של 1 לכמות
                decreaseQuantity(product.getId());
                // עדכון תצוגה
                int quantity = getProductQuantityFromCart(product.getId());
                holder.tvQuantity.setText(String.valueOf(quantity));
            }
        });

        holder.ibDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO הוספה של 1 לכמות
                deleteProduct(product.getId());
                // עדכון תצוגה
                int quantity = getProductQuantityFromCart(product.getId());
                holder.tvQuantity.setText(String.valueOf(quantity));
            }
        });

        // הגדרת פעולה בלחיצה על פריט - פתיחת מסך לצפייה בפרטי המוצר
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailsActivity.class); // יצירת אינטנט לפתיחת מסך פרטי מוצר
            intent.putExtra("isUser", true); // העברת מידע שהמשתמש הוא קונה

            // שליחת פרטי המוצר למסך הבא
            intent.putExtra("image", product.getImage());
            intent.putExtra("name", product.getName());
            intent.putExtra("price", product.getPrice()+"₪");
            intent.putExtra("details", product.getDetails());
            intent.putExtra("quantity", product.getQuantity());

            // שליחת מזהה המסמך מה-Database
            String docId = this.getSnapshots().getSnapshot(position).getId();
            intent.putExtra("docId", docId);

            context.startActivity(intent); // התחלת המסך החדש
        });
    }

    // יצירת מופע של ViewHolder לכל פריט ברשימה
    @NonNull
    @Override
    public CartAdapter.CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // יצירה של פריט לפי עיצוב פריט עגלה מה-XML
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new CartAdapter.CartViewHolder(view); // החזרת ה-ViewHolder שנוצר
    }

    // מחלקה פנימית שמייצגת את תצוגת פריט אחד בעגלה
    public class CartViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvQuantity; // טקסט להצגת שם ומחיר המוצר
        ImageView ivImage; // תצוגת תמונה של המוצר
        ImageButton ibIncrease, ibDecrease, ibDelete; // כפתורי מחיקה, הוספה והפחתה

        // בנאי של ViewHolder - קישור רכיבי תצוגה מתוך ה-XML
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            // אתחול רכיבים
            ivImage = itemView.findViewById(R.id.imgIcon);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            // tvDetails = itemView.findViewById(R.id.tvDetails); // (שמורים לפרטים נוספים)
            tvQuantity = itemView.findViewById(R.id.tvQuantity);

            ibIncrease = itemView.findViewById(R.id.btnIncrease);
            ibDecrease = itemView.findViewById(R.id.btnDecrease);
            ibDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    public int getProductQuantityFromCart(String productId) {
        SharedPreferences sp = context.getSharedPreferences(user.getEmail(), 0);
        String json = sp.getString("cartMap", null);

        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, Integer>>() {}.getType();
            Map<String, Integer> cartMap = gson.fromJson(json, type);

            if (cartMap != null && cartMap.containsKey(productId)) {
                return cartMap.get(productId);
            }
        }
        return 0; // אם לא קיים, נחזיר 0
    }

    public void increaseQuantity(String productId){
        SharedPreferences sp = context.getSharedPreferences(user.getEmail(), 0);
        SharedPreferences.Editor editor = sp.edit();

        String json = sp.getString("cartMap", null);
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Integer>>() {}.getType();
        Map<String, Integer> cartMap = gson.fromJson(json, type);

        if (cartMap != null && cartMap.containsKey(productId)){
            int currentQuantity = cartMap.get(productId);
            if (CartActivity.getProductInventoryQuantity(productId)>currentQuantity)
                cartMap.put(productId, currentQuantity + 1);
            else
                Toast.makeText(context, "This product is not available in higher quantities at the moment.", Toast.LENGTH_SHORT).show();
        }

        else
            cartMap = new HashMap<>();

        // שמירה
        String updatedJson = gson.toJson(cartMap);
        editor.putString("cartMap", updatedJson);
        editor.apply();
    }

    public void decreaseQuantity(String productId){
        SharedPreferences sp = context.getSharedPreferences(user.getEmail(), 0);
        SharedPreferences.Editor editor = sp.edit();

        String json = sp.getString("cartMap", null);
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Integer>>() {}.getType();
        Map<String, Integer> cartMap = gson.fromJson(json, type);

        if (cartMap != null && cartMap.containsKey(productId)){
            int currentQuantity = cartMap.get(productId);
            if (currentQuantity > 1) {
                cartMap.put(productId, currentQuantity - 1);
            } else {
                cartMap.remove(productId); // הסרה אם הכמות היא 1
            }
        }

        else
            cartMap = new HashMap<>();

        // שמירה
        String updatedJson = gson.toJson(cartMap);
        editor.putString("cartMap", updatedJson);
        editor.apply();

        this.notifyDataSetChanged(); // רענון התצוגה

        // ריסטארט של CartActivity
        if (context instanceof Activity) {
            ((Activity) context).recreate();
        }
    }

    public void deleteProduct(String productId){
        SharedPreferences sp = context.getSharedPreferences(user.getEmail(), 0);
        SharedPreferences.Editor editor = sp.edit();

        String json = sp.getString("cartMap", null);
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Integer>>() {}.getType();
        Map<String, Integer> cartMap = gson.fromJson(json, type);

        if (cartMap != null && cartMap.containsKey(productId)){
            cartMap.remove(productId);
        }

        else
            cartMap = new HashMap<>();

        // שמירה
        String updatedJson = gson.toJson(cartMap);
        editor.putString("cartMap", updatedJson);
        editor.apply();

        this.notifyDataSetChanged(); // רענון התצוגה

        // ריסטארט של CartActivity
        if (context instanceof Activity) {
            ((Activity) context).recreate();
        }
    }
}