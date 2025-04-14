package com.example.bagrutproject.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.bagrutproject.model.Category;
import com.example.bagrutproject.model.Manager;
import com.example.bagrutproject.model.Order;
import com.example.bagrutproject.model.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class FireStoreHelper {
    private static final String TAG = "FireStoreHelper Tag";
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private static CollectionReference collectionRefProduct = db.collection("products");
    private static CollectionReference collectionRefCat = db.collection("categories");
    private static CollectionReference collectionRefManager = db.collection("managers");
    private static CollectionReference collectionRefOrder = db.collection("orders");
    private FireStoreHelper.FBReply fbReply;

    public interface FBReply {
        void getAllSuccess(ArrayList<Product> products);
        void getOneSuccess(Product product);
        void onProductsLoaded(ArrayList<Product> products);
    }

    public FireStoreHelper(FireStoreHelper.FBReply fbReply) {
        this.fbReply = fbReply;
    }

    public void add(Order order, Context context) {
        collectionRefOrder.add(order).addOnSuccessListener(documentReference -> {
            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
            Toast.makeText(context, "your order was sent", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Log.w(TAG, "Error adding document", e);
        });
    }

    public void add(Manager manager) {
        collectionRefManager.document(FBAuthHelper.getCurrentUser().getUid()).set(manager).addOnSuccessListener(documentReference -> {
            Log.d(TAG, "DocumentSnapshot added with ID: " + FBAuthHelper.getCurrentUser().getUid());
        }).addOnFailureListener(e -> {
            Log.w(TAG, "Error adding document", e);
        });
    }

    public void add(Category category) {
        collectionRefCat.add(category).addOnSuccessListener(documentReference -> {
            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
        }).addOnFailureListener(e -> {
            Log.w(TAG, "Error adding document", e);
        });
    }

    public void add(Product product) {
        collectionRefProduct.add(product).addOnSuccessListener(documentReference -> {
            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
        }).addOnFailureListener(e -> {
            Log.w(TAG, "Error adding document", e);
        });

    }

    public void update(String id, Product product) {
        collectionRefProduct.document(id).update("image", product.getImage(),
                "name", product.getName(), "price",
                product.getPrice(), "details", product.getDetails(),
                "quantity", product.getQuantity(), "forSale",
                product.getForSale(),"category", product.getCategory(), "id", id).addOnSuccessListener(aVoid -> {
            Log.d(TAG, "DocumentSnapshot updated with ID: " + id);
        }).addOnFailureListener(e -> {
            Log.w(TAG, "Error updating document", e);
        });
    }
    public void delete(String id) {
        collectionRefProduct.document(id).delete().addOnSuccessListener(aVoid -> {
            Log.d(TAG, "DocumentSnapshot deleted with ID: " + id);
        }).addOnFailureListener(e -> {
            Log.w(TAG, "Error deleting document", e);
        });
    }
    public void getAll() {
        collectionRefProduct.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<Product> products = new ArrayList<>();
                for (com.google.firebase.firestore.DocumentSnapshot document : task.getResult()) {
                    Log.d(TAG, document.getId() + " => " + document.getData());
                    Product product = document.toObject(Product.class);
                    products.add(product);
                }
                fbReply.getAllSuccess(products);
            } else {
                Log.w(TAG, "Error getting documents.", task.getException());
            }
        });

    }
    public void getOne(String id) {
        collectionRefProduct.document(id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                com.google.firebase.firestore.DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    Product product = document.toObject(Product.class);
                    fbReply.getOneSuccess(product);
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    public static CollectionReference getCollectionRefProduct() {
        return collectionRefProduct;
    }
    public static CollectionReference getCollectionRefManager() {
        return collectionRefManager;
    }
    public static CollectionReference getCollectionRefCat() {
        return collectionRefCat;
    }
    public static CollectionReference getCollectionRefOrder(){return collectionRefOrder;}
    public static FirebaseUser getCurrentUser(){
        return currentUser;
    }

    public static void setCurrentUser(FirebaseUser currentUser) {
        FireStoreHelper.currentUser = currentUser;
    }
}
