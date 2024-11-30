package com.example.bagrutproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AddProductActivity extends AppCompatActivity {

    FireStoreHelper fireStoreHelper;
    EditText etName, etPrice, etDetails, etCategory;
    Boolean isEditMode=false;
    Button saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);


        fireStoreHelper=new FireStoreHelper(null);
        saveBtn =findViewById(R.id.btnSave);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateInput()==true) {
                    saveProduct(etName.getText().toString(), etPrice.getText().toString(), etDetails.getText().toString(), etCategory.getText().toString());

                }
                Intent intent=new Intent(AddProductActivity.this, ForSaleFragment.class);
                startActivity(intent);
            }
        });
    }

    private boolean validateInput(){
        boolean isValid = true;
        if (etName.getText().toString().isEmpty()) {
            etName.setError("please enter title");
            isValid = false;
        }
        if (etPrice.getText().toString().isEmpty()){
            etPrice.setError("please enter some content");
            isValid = false;
        }
        if (etDetails.getText().toString().isEmpty()){
            etDetails.setError("please enter some content");
            isValid = false;
        }
        if (etCategory.getText().toString().isEmpty()){
            etCategory.setError("please enter some content");
            isValid = false;
        }
        return isValid;
    }

    private void saveProduct(String name, String price, String details, String category){
        Product product=new Product(name,price,details,category);
        fireStoreHelper.add(product);
    }
}