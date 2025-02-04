package com.example.bagrutproject.views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bagrutproject.R;
import com.example.bagrutproject.model.Product;
import com.example.bagrutproject.utils.FireStoreHelper;
import com.example.bagrutproject.utils.ImageUtils;

public class EditProductActivity extends AppCompatActivity {

    private ActivityResultLauncher<Void> mGetThumb;
    private ActivityResultLauncher<String> mGetContent;
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
                    saveProduct(ImageUtils.getBitmapFromImageView(ivImage), etName.getText().toString(), etPrice.getText().toString(), etDetails.getText().toString(), etQuantity.getInputType(), forSale.isChecked());// etCategory.getText().toString());
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
        if (etQuantity.getInputType()<=0){
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
        else
            fireStoreHelper.add(product);
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
}