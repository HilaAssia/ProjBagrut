package com.example.bagrutproject.views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bagrutproject.R;
import com.example.bagrutproject.model.Product;
import com.example.bagrutproject.utils.FireStoreHelper;
import com.example.bagrutproject.utils.ImageUtils;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class EditProductActivity extends AppCompatActivity {

    private ActivityResultLauncher<Void> mGetThumb;
    private ActivityResultLauncher<String> mGetContent;
    Spinner spinner;
    FireStoreHelper fireStoreHelper;
    ImageView ivImage;
    Switch forSale;
    EditText etName, etPrice, etDetails, etQuantity;
    Boolean isEditMode=false;
    Button saveBtn,deleteBtn,captureImageBtn,selectImageBtn;
    String docId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        spinner = findViewById(R.id.spinner);
        getCategory();
        ivImage=findViewById(R.id.imageView);
        etName=findViewById(R.id.etName);
        etPrice=findViewById(R.id.etPrice);
        etDetails=findViewById(R.id.etDetails);
        etQuantity=findViewById(R.id.etQuantity);
        forSale=findViewById(R.id.switchForSale);

        registerCameraLauncher();
        registerForContentLauncher();
        fireStoreHelper=new FireStoreHelper(null);
        captureImageBtn=findViewById(R.id.btnCaptureImage);
        captureImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetThumb.launch(null);
            }
        });
        selectImageBtn=findViewById(R.id.btnSelectImage);
        selectImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetContent.launch("image/*");
            }
        });
        saveBtn =findViewById(R.id.btnSave);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateInput()==true) {
                    saveProduct(ImageUtils.getBitmapFromImageView(ivImage),
                            etName.getText().toString(),
                            etPrice.getText().toString(),
                            etDetails.getText().toString(),
                            Integer.parseInt(etQuantity.getText().toString()),
                            forSale.isChecked());// etCategory.getText().toString());
                    Intent intent=new Intent(EditProductActivity.this, HomeActivity.class);
                    startActivity(intent);
                }

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
            forSale.setChecked(getIntent().getBooleanExtra("forSale", false));
            etName.setText(getIntent().getStringExtra("name"));
            etPrice.setText(getIntent().getStringExtra("price"));
            etDetails.setText(getIntent().getStringExtra("details"));
            etQuantity.setText(Integer.toString(getIntent().getIntExtra("quantity",0)));
            //etCategory.setText(getIntent().getStringExtra("category"));
            findViewById(R.id.btndelete).setVisibility(View.VISIBLE);
        }
    }

    private boolean validateInput(){
        boolean isValid = true;
        if (etName.getText().toString().isEmpty()) {
            etName.setError("please enter some content");
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
        if (Integer.parseInt(etQuantity.getText().toString())<=0){
            etQuantity.setError("please enter some content");
            isValid = false;
        }
        //if (etCategory.getText().toString().isEmpty()){
            //etCategory.setError("please enter some content");
            //isValid = false;
        //}
        return isValid;
    }

    private void saveProduct(Bitmap bitmap, String name, String price, String details, int quantity, boolean forSale){
        Product product=new Product(ImageUtils.convertBitmapToString(bitmap),name,price,details,quantity,forSale);
        if (isEditMode)
            fireStoreHelper.update(docId,product);
        else {
            fireStoreHelper.add(product);
        }
    }

    private void deleteProduct(){
        fireStoreHelper.delete(docId);
    }

    private void registerCameraLauncher(){
        mGetThumb=registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), new ActivityResultCallback<Bitmap>() {
            @Override
            public void onActivityResult(Bitmap result) {
                ivImage.setImageBitmap(result);
            }
        });
    }

    private void registerForContentLauncher(){
        mGetContent=registerForActivityResult(new ActivityResultContracts.GetContent(), uri->{
            try {
               Bitmap bitmap= MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
               ivImage.setImageBitmap(bitmap);
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    public void updateSpinner(ArrayList<String> items){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // מגדיר את התצוגה של רשימת האפשרויות
        spinner.setAdapter(adapter);
    }

    public void getCategory(){
        // יצירת רשימה של מיתרים שתכיל את שמות הקטגוריות
        ArrayList<String> categoriesList = new ArrayList<>();
        categoriesList.add("category");

        // שליפת הקטגוריות מ-Firestore
        fireStoreHelper.getCollectionRefCat().get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // הוספת שם הקטגוריה לרשימה
                            String categoryName = document.getString("category");
                            categoriesList.add(categoryName);
                        }
                        // אחרי שסיימנו לעדכן את הרשימה, תוכל להמיר אותה למערך (אם נדרש)
                        //String[] categoriesArray = categoriesList.toArray(new String[0]);
                        // עכשיו categoriesArray מכיל את כל הקטגוריות
                        // אפשר לעדכן את ה-Spinner או כל רכיב אחר
                        updateSpinner(categoriesList);
                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException());
                    }
                });
    }
}