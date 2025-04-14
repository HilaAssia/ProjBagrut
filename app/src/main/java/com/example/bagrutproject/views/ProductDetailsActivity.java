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
import java.util.ArrayList;
import java.util.List;

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
            tvPrice.setText(getIntent().getStringExtra("price"));
            tvDetails.setText(getIntent().getStringExtra("details"));
            isUser=getIntent().getBooleanExtra("isUser",false);
            id=getIntent().getStringExtra("id");
            //etCategory.setText(getIntent().getStringExtra("category"));
        }

        addToCartBtn=findViewById(R.id.btnAddToCart);
        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getCartItems()!=null)
                    editCart(getCartItems(), id);
                else
                    editCart(new ArrayList<String>(), id);
                Intent intent=new Intent(ProductDetailsActivity.this,UserActivity.class);
                startActivity(intent);
                finish();
            }
        });

        if (isUser)
            findViewById(R.id.btnAddToCart).setVisibility(View.GONE);
    }

    public void editCart(List<String> ids, String id){
        // יצירת SharedPreferences
        sp = getSharedPreferences(fbAuthHelper.getCurrentUser().getUid(), 0);
        SharedPreferences.Editor editor = sp.edit();
        ids.add(id);

        // המרת רשימת המוצרים ל-JSON
        Gson gson = new Gson();
        String json = gson.toJson(ids);

        // שמירה של הרשימה ב-SharedPreferences
        editor.putString("productList", json);
        editor.commit();
    }

    public List<String> getCartItems(){
        sp = getSharedPreferences(fbAuthHelper.getCurrentUser().getUid(), 0);
        String json = sp.getString("productList", ""); // מקבל את ה-JSON
        Gson gson = new Gson();

        // המרת ה-JSON חזרה לרשימה
        Type type = new TypeToken<List<String>>(){}.getType();
        List<String> ids = gson.fromJson(json, type);

        return ids;
    }
}