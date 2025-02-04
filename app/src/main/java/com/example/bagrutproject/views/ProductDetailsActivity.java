package com.example.bagrutproject.views;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bagrutproject.R;
import com.example.bagrutproject.utils.FireStoreHelper;

public class ProductDetailsActivity extends AppCompatActivity {

    SharedPreferences sp;
    FireStoreHelper fireStoreHelper;
    ImageView ivImage;
    TextView tvName, tvPrice, tvDetails;
    Button addToCartBtn;
    String docId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        ivImage=findViewById(R.id.imageView);
        tvName=findViewById(R.id.tvName);
        tvPrice=findViewById(R.id.tvPrice);
        tvDetails=findViewById(R.id.tvDetails);

        docId = getIntent().getStringExtra("docId");
        if (docId != null && !docId.isEmpty()){
            tvName.setText(getIntent().getStringExtra("name"));
            tvPrice.setText(getIntent().getStringExtra("price"));
            tvDetails.setText(getIntent().getStringExtra("details"));
            //etCategory.setText(getIntent().getStringExtra("category"));
        }

        addToCartBtn=findViewById(R.id.btnAddToCart);
        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart(docId);
            }
        });
    }

    public void addToCart(String docId){
        sp=getSharedPreferences("user cart",0);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString("product", docId);
        editor.commit();
    }
}