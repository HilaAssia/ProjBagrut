package com.example.bagrutproject.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bagrutproject.R;
import com.example.bagrutproject.utils.FBAuthHelper;
import com.example.bagrutproject.utils.ImageUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ProductDetailsActivity extends AppCompatActivity {

    FBAuthHelper fbAuthHelper;
    SharedPreferences sp;
    ImageView ivImage;
    TextView tvName, tvPrice, tvDetails;
    Button addToCartBtn;
    String docId,id;
    boolean isUser=false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        fbAuthHelper=new FBAuthHelper(this,null);
        ivImage=findViewById(R.id.imageView);
        tvName=findViewById(R.id.tvName);
        tvPrice=findViewById(R.id.tvPrice);
        tvDetails=findViewById(R.id.tvDetails);

        docId = getIntent().getStringExtra("docId");
        if (docId != null && !docId.isEmpty()){
            ivImage.setImageBitmap(ImageUtils.convertStringToBitmap(getIntent().getStringExtra("image")));
            tvName.setText(getIntent().getStringExtra("name"));
            tvPrice.setText(getIntent().getStringExtra("price")+"₪");
            tvDetails.setText(getIntent().getStringExtra("details"));
            isUser=getIntent().getBooleanExtra("isUser",false);
            id=getIntent().getStringExtra("id");
            //etCategory.setText(getIntent().getStringExtra("category"));
        }

        addToCartBtn=findViewById(R.id.btnAddToCart);
        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProduct(id);
                Intent intent=new Intent(ProductDetailsActivity.this,CartActivity.class);
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
}