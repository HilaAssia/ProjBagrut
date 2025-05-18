package com.example.bagrutproject.views;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bagrutproject.R;
import com.example.bagrutproject.model.Category;
import com.example.bagrutproject.model.Order;
import com.example.bagrutproject.model.Product;
import com.example.bagrutproject.utils.FireStoreHelper;
import com.example.bagrutproject.utils.ImageUtils;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class EditProductActivity extends AppCompatActivity implements FireStoreHelper.FBAddStat, FireStoreHelper.FBReply {

    private ActivityResultLauncher<Void> mGetThumb; // מפעיל מצלמה ולקיחת תמונה מוקטן
    private ActivityResultLauncher<String> mGetContent; // מפעיל בחירת תמונה מהגלריה
    Spinner spinner; // תפריט בחירה לקטגוריות
    FireStoreHelper fireStoreHelper; // עוזר לעבודה מול מסד נתונים Firestore
    ImageView ivImage; // תמונת המוצר
    Switch forSale; // מתג אם המוצר למכירה
    EditText etName, etPrice, etDetails, etQuantity; // שדות קלט לשם, מחיר, פרטים וכמות
    Boolean isEditMode = false; // משתנה שבודק אם אנחנו במצב עריכה
    Button saveBtn, deleteBtn, captureImageBtn, selectImageBtn; // כפתורים לשמירה, מחיקה, צילום ובחירת תמונה
    String docId, category, imageString; // מזהה המוצר והקטגוריה הנבחרת

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product); // מחבר את העיצוב של הפעילות

        // אתחול רכיבים גרפיים מהמסך
        ivImage = findViewById(R.id.imageView);
        etName = findViewById(R.id.etName);
        etPrice = findViewById(R.id.etPrice);
        etDetails = findViewById(R.id.etDetails);
        etQuantity = findViewById(R.id.etQuantity);
        forSale = findViewById(R.id.switchForSale);

        registerCameraLauncher(); // רושם את הפעולה לצילום תמונה
        registerForContentLauncher(); // רושם את הפעולה לבחירת תמונה מהגלריה

        fireStoreHelper = new FireStoreHelper(this, this); // יוצר עוזר למסד הנתונים

        spinner = findViewById(R.id.spinner); // מוצא את הספינר מהעיצוב
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // מאזין לבחירת קטגוריה
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedItem = parentView.getItemAtPosition(position).toString(); // מקבל את הקטגוריה שנבחרה
                Toast.makeText(EditProductActivity.this, "בחרת: " + selectedItem, Toast.LENGTH_SHORT).show(); // מציג הודעה
                category = selectedItem; // שומר את הקטגוריה שנבחרה
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // פעולה שלא מתבצעת כלום אם לא נבחר כלום
            }
        });

        // בודק אם קיבלנו מזהה מוצר לעריכה
        docId = getIntent().getStringExtra("docId");
        if (docId != null && !docId.isEmpty()) {
            isEditMode = true; // מפעיל מצב עריכה

            //fireStoreHelper.getOne(docId);
            /*forSale.setChecked(getIntent().getBooleanExtra("forSale", false));
            etName.setText(getIntent().getStringExtra("name"));
            etPrice.setText(getIntent().getStringExtra("price"));
            etDetails.setText(getIntent().getStringExtra("details"));
            etQuantity.setText(Integer.toString(getIntent().getIntExtra("quantity", 0)));
            category = getIntent().getStringExtra("category");
            findViewById(R.id.btndelete).setVisibility(View.VISIBLE); // מציג כפתור מחיקה*/
            fireStoreHelper.getOne(docId);
        }
        else
            setCategories(); // טוען קטגוריות לספינר

        captureImageBtn = findViewById(R.id.btnCaptureImage); // מוצא את כפתור הצילום
        captureImageBtn.setOnClickListener(new View.OnClickListener() { // מאזין ללחיצה על צילום
            @Override
            public void onClick(View v) {
                mGetThumb.launch(null); // מפעיל מצלמה לצילום
            }
        });

        selectImageBtn = findViewById(R.id.btnSelectImage); // מוצא את כפתור בחירת תמונה
        selectImageBtn.setOnClickListener(new View.OnClickListener() { // מאזין ללחיצה על בחירת תמונה
            @Override
            public void onClick(View v) {
                mGetContent.launch("image/*"); // פותח גלריה לבחירת תמונה
            }
        });

        saveBtn = findViewById(R.id.btnSave); // מוצא את כפתור השמירה
        saveBtn.setOnClickListener(new View.OnClickListener() { // מאזין ללחיצה על שמירה
            @Override
            public void onClick(View v) {
                if (validateInput()) { // אם המידע תקין
                    saveProduct(ImageUtils.getBitmapFromImageView(ivImage), // שומר מוצר חדש או מעודכן
                            etName.getText().toString(),
                            etPrice.getText().toString(),
                            etDetails.getText().toString(),
                            Integer.parseInt(etQuantity.getText().toString()),
                            forSale.isChecked(), category);
                    Intent intent = new Intent(EditProductActivity.this, ManagerActivity.class); // עובר חזרה למסך הניהול
                    startActivity(intent);
                    finish();
                }
            }
        });

        deleteBtn = findViewById(R.id.btndelete); // מוצא את כפתור המחיקה
        deleteBtn.setOnClickListener(new View.OnClickListener() { // מאזין ללחיצה על מחיקה
            @Override
            public void onClick(View v) {
                deleteProduct(); // מוחק את המוצר
            }
        });
    }

    private boolean validateInput() { // בדיקה אם השדות מולאו בצורה תקינה
        boolean isValid = true;
        if (etName.getText().toString().isEmpty()) {
            etName.setError("please enter some content");
            isValid = false;
        }
        if (etPrice.getText().toString().isEmpty()) {
            etPrice.setError("please enter some content");
            isValid = false;
        }
        if (etDetails.getText().toString().isEmpty()) {
            etDetails.setError("please enter some content");
            isValid = false;
        }
        if (!etQuantity.getText().toString().isEmpty()){
            if (Integer.parseInt(etQuantity.getText().toString()) <= 0) {
                etQuantity.setError("quantity has to be more than 0!!!");
                isValid = false;
            }
        }
        else {
            etQuantity.setError("please enter some content");
            isValid = false;
        }
        if (category.equals("category")) { // אם לא נבחרה קטגוריה
            // מציג דיאלוג ליצירת קטגוריה חדשה
            Dialog dialog = new Dialog(EditProductActivity.this);
            dialog.setContentView(R.layout.add_cat_dialog);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(false);
            EditText etCategory = dialog.findViewById(R.id.etCategory);
            Button buttonAdd = dialog.findViewById(R.id.btnAdd);
            buttonAdd.setOnClickListener(new View.OnClickListener() { // לחיצה על הוספת קטגוריה
                @Override
                public void onClick(View v) {
                    Category cat = new Category(etCategory.getText().toString()); // יוצר קטגוריה
                    if (cat.toString().equals(""))
                        dialog.dismiss();
                    fireStoreHelper.add(cat, EditProductActivity.this); // מוסיף למסד נתונים
                    setCategories(); // מרענן קטגוריות בספינר
                    dialog.dismiss(); // סוגר דיאלוג
                }
            });
            Button buttonCancel = dialog.findViewById(R.id.btnCancel); // כפתור ביטול
            buttonCancel.setVisibility(View.VISIBLE);
            buttonCancel.setOnClickListener(new View.OnClickListener() { // מאזין ללחיצה ביטול
                @Override
                public void onClick(View v) {
                    dialog.dismiss(); // סוגר דיאלוג
                }
            });
            dialog.show(); // מציג את הדיאלוג
            isValid = false;
        }
        return isValid;
    }

    private void saveProduct(Bitmap bitmap, String name, String price, String details, int quantity, boolean forSale, String category) { // שמירה של מוצר
        Product product = new Product(ImageUtils.convertBitmapToString(bitmap), name, price, details, quantity, forSale, category); // יוצר אובייקט מוצר
        if (isEditMode) { // אם במצב עריכה
            product.setId(docId);
            fireStoreHelper.update(docId, product); // מעדכן מוצר קיים
        }else
            fireStoreHelper.add(product); // מוסיף מוצר חדש
    }

    private void deleteProduct() { // מחיקת מוצר
        fireStoreHelper.deleteProduct(docId); // מוחק לפי מזהה
    }

    private void registerCameraLauncher() { // רישום פעולת צילום
        mGetThumb = registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), new ActivityResultCallback<Bitmap>() {
            @Override
            public void onActivityResult(Bitmap result) {
                ivImage.setImageBitmap(result); // מציג את התמונה שצולמה
            }
        });
    }

    private void registerForContentLauncher() { // רישום פעולת בחירת תמונה
        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri); // מקבל תמונה מהגלריה
                String imageStr = ImageUtils.convertBitmapToString(bitmap); // ממיר את התמונה למחרוזת
                if (imageStr.getBytes().length > 1048576) { // אם גודל המחרוזת גדול מ־1MB
                    Toast.makeText(this, "התמונה כבדה מדי, אנא בחר תמונה אחרת", Toast.LENGTH_LONG).show();
                } else {
                    ivImage.setImageBitmap(bitmap); // מציג את התמונה
                    // כאן תוכל גם לשמור אותה אם צריך
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "שגיאה בטעינת התמונה", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateSpinner(ArrayList<String> items) { // עדכון הספינר עם רשימת קטגוריות
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter); // מחבר את הרשימה לספינר
    }

    public void setCategories() { // שליפת קטגוריות מ-Firestore
        ArrayList<String> categoriesList = new ArrayList<>();
        categoriesList.add("category"); // קטגוריה ברירת מחדל

        FireStoreHelper.getCollectionRefCat().get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) { // עובר על כל מסמך
                            String categoryName = document.getString("category"); // מביא את שם הקטגוריה
                            categoriesList.add(categoryName); // מוסיף לרשימה
                        }
                        updateSpinner(categoriesList); // מעדכן את הספינר

                        // קוד חדש: רק אם במצב עריכה, נבחר את הקטגוריה המתאימה
                        if (isEditMode && category != null) {
                            int index = categoriesList.indexOf(category);
                            if (index != -1) {
                                spinner.setSelection(index);
                            }
                        }
                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException()); // מדווח על שגיאה
                    }
                });
    }

    @Override
    public void onAddProductSuccesses(String docId, Product product) {
        product.setId(docId);
        fireStoreHelper.update(docId, product);
    }

    @Override
    public void onAddOrderSuccesses(String id, Order order) {

    }

    @Override
    public void getAllSuccess(ArrayList<Product> products) {

    }

    @Override
    public void getOneSuccess(Product product) {
        // ממלא את השדות הקיימים במידע שהגיע
        ivImage.setImageBitmap(ImageUtils.convertStringToBitmap(product.getImage()));
        forSale.setChecked(product.getForSale());
        etName.setText(product.getName());
        etPrice.setText(product.getPrice());
        etDetails.setText(product.getDetails());
        etQuantity.setText(String.valueOf(product.getQuantity()));
        category = product.getCategory();
        setCategories(); // טוען קטגוריות לספינר

        findViewById(R.id.btndelete).setVisibility(View.VISIBLE); // מציג כפתור מחיקה
    }

    @Override
    public void onProductsLoaded(ArrayList<Product> products) {

    }

    @Override
    public void onDeleteSuccess() {
        Intent intent = new Intent(EditProductActivity.this, ManagerActivity.class); // עובר חזרה למסך הניהול
        startActivity(intent);
        finish();
    }
}