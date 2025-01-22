package com.example.bagrutproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class EditProductActivity extends AppCompatActivity {

    FireStoreHelper fireStoreHelper;
    EditText etName, etPrice, etDetails;
    Boolean isEditMode=false;
    Button saveBtn,deleteBtn;
    String docId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        etName=findViewById(R.id.etName);
        etPrice=findViewById(R.id.etPrice);
        etDetails=findViewById(R.id.etDetails);

        fireStoreHelper=new FireStoreHelper(null);
        saveBtn =findViewById(R.id.btnSave);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateInput()==true) {
                    saveProduct(etName.getText().toString(), etPrice.getText().toString(), etDetails.getText().toString());// etCategory.getText().toString());

                }
                Intent intent=new Intent(EditProductActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
        deleteBtn =findViewById(R.id.btndelete);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProduct();
                Intent intent=new Intent(EditProductActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        docId = getIntent().getStringExtra("docId");
        if (docId != null && !docId.isEmpty()){
            isEditMode=true;
            etName.setText(getIntent().getStringExtra("name"));
            etPrice.setText(getIntent().getStringExtra("price"));
            etDetails.setText(getIntent().getStringExtra("details"));
            //etCategory.setText(getIntent().getStringExtra("category"));
            findViewById(R.id.btndelete).setVisibility(View.VISIBLE);
        }
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
        //if (etCategory.getText().toString().isEmpty()){
            //etCategory.setError("please enter some content");
            //isValid = false;
        //}
        return isValid;
    }

    private void saveProduct(String name, String price, String details){
        Product product=new Product(name,price,details);
        if (isEditMode)
            fireStoreHelper.update(docId,product);
        else
            fireStoreHelper.add(product);
    }

    private void deleteProduct(){
        fireStoreHelper.delete(docId);
    }
}