package com.example.bagrutproject.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bagrutproject.R;
import com.example.bagrutproject.model.Product;
import com.example.bagrutproject.utils.FBAuthHelper;
import com.example.bagrutproject.utils.FireStoreHelper;
import com.example.bagrutproject.utils.ImageUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProductDetailsActivity extends AppCompatActivity implements FireStoreHelper.FBReply {

    FBAuthHelper fbAuthHelper;
    FireStoreHelper fireStoreHelper;
    SharedPreferences sp;
    ImageView ivImage;
    TextView tvName, tvPrice, tvDetails, tvOutOfStock;
    Button addToCartBtn, continueShoppingBtn;
    String docId;
    boolean isUser=false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        fbAuthHelper=new FBAuthHelper(this,null);
        fireStoreHelper=new FireStoreHelper(this);
        ivImage=findViewById(R.id.imageView);
        tvName=findViewById(R.id.tvName);
        tvPrice=findViewById(R.id.tvPrice);
        tvDetails=findViewById(R.id.tvDetails);
        tvOutOfStock=findViewById(R.id.outOfStock);

        docId = getIntent().getStringExtra("docId");
        if (docId == null || docId.isEmpty()){
            Toast.makeText(this, "a problem appeared while uploading this product", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(ProductDetailsActivity.this,UserActivity.class);
            startActivity(intent);
            finish();
        }
            //isEditMode = true; // מפעיל מצב עריכה

            //fireStoreHelper.getOne(docId);
            /*forSale.setChecked(getIntent().getBooleanExtra("forSale", false));
            etName.setText(getIntent().getStringExtra("name"));
            etPrice.setText(getIntent().getStringExtra("price"));
            etDetails.setText(getIntent().getStringExtra("details"));
            etQuantity.setText(Integer.toString(getIntent().getIntExtra("quantity", 0)));
            category = getIntent().getStringExtra("category");
            findViewById(R.id.btndelete).setVisibility(View.VISIBLE); // מציג כפתור מחיקה*/
        fireStoreHelper.getOne(docId);

        addToCartBtn=findViewById(R.id.btnAddToCart);
        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProduct(docId);
                Intent intent=new Intent(ProductDetailsActivity.this,CartActivity.class);
                startActivity(intent);
                finish();
            }
        });

        continueShoppingBtn=findViewById(R.id.btnContinueShopping);
        continueShoppingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ProductDetailsActivity.this,UserActivity.class);
                startActivity(intent);
                finish();
            }
        });

        if (isUser)
            findViewById(R.id.btnAddToCart).setVisibility(View.GONE);
    }

    public void addProduct(String newProduct){
        // יצירת SharedPreferences
        sp = getSharedPreferences(fbAuthHelper.getCurrentUser().getEmail(), 0);
        SharedPreferences.Editor editor = sp.edit();

        // שליפת מפת העגלה הקיימת
        Gson gson = new Gson();
        String json = sp.getString("cartMap", null);
        Type type = new TypeToken<Map<String, Integer>>() {}.getType();
        Map<String, Integer> cartMap = gson.fromJson(json, type);

        if (cartMap == null) {
            cartMap = new HashMap<>();
        }

        // עדכון הכמות למוצר הקיים או הוספת חדש
        String productId = newProduct;
        int newQuantity = 1;
        if (cartMap.containsKey(productId)) {
            cartMap.put(productId, cartMap.get(productId) + newQuantity);
        } else {
            cartMap.put(productId, newQuantity);
        }

        // המרה ל-JSON ושמירה ב-SharedPreferences
        String updatedJson = gson.toJson(cartMap);
        editor.putString("cartMap", updatedJson);
        editor.commit();
    }

    @Override
    public void getAllSuccess(ArrayList<Product> products) {

    }

    @Override
    public void getOneSuccess(Product product) {
        // ממלא את השדות הקיימים במידע שהגיע
        ivImage.setImageBitmap(ImageUtils.convertStringToBitmap(product.getImage()));
        //forSale.setChecked(product.getForSale());
        tvName.setText(product.getName());
        tvPrice.setText(product.getPrice()+"₪");
        tvDetails.setText(product.getDetails());
        if (product.getQuantity()<=0) {
            tvOutOfStock.setVisibility(View.VISIBLE);
            addToCartBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public void onProductsLoaded(ArrayList<Product> products) {

    }

    @Override
    public void onDeleteSuccess() {

    }
}