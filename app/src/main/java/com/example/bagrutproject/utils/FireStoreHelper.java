package com.example.bagrutproject.utils;

import android.util.Log;

import com.example.bagrutproject.model.Category;
import com.example.bagrutproject.model.Manager;
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
    private static CollectionReference collectionRef = db.collection("products");
    private static CollectionReference collectionRefCat = db.collection("categories");
    private static CollectionReference collectionRefManager = db.collection("managers");
    private FireStoreHelper.FBReply fbReply;

    public interface FBReply {
        void getAllSuccess(ArrayList<Product> products);
        void getOneSuccess(Product product);
    }

    public FireStoreHelper(FireStoreHelper.FBReply fbReply) {
        this.fbReply = fbReply;
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
        collectionRef.add(product).addOnSuccessListener(documentReference -> {
            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
        }).addOnFailureListener(e -> {
            Log.w(TAG, "Error adding document", e);
        });

    }

    public void update(String id, Product product) {
        //"category", product.getCategory()
        collectionRef.document(id).update("image", product.getImage(),
                "name", product.getName(), "price",
                product.getPrice(), "details", product.getDetails(),
                "quantity", product.getQuantity(), "forSale",
                product.getForSale(), "id", id).addOnSuccessListener(aVoid -> {
            Log.d(TAG, "DocumentSnapshot updated with ID: " + id);
        }).addOnFailureListener(e -> {
            Log.w(TAG, "Error updating document", e);
        });
    }
    public void delete(String id) {
        collectionRef.document(id).delete().addOnSuccessListener(aVoid -> {
            Log.d(TAG, "DocumentSnapshot deleted with ID: " + id);
        }).addOnFailureListener(e -> {
            Log.w(TAG, "Error deleting document", e);
        });
    }
    public void getAll() {
        collectionRef.get().addOnCompleteListener(task -> {
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
        collectionRef.document(id).get().addOnCompleteListener(task -> {
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

    public static CollectionReference getCollectionRef() {
        return collectionRef;
    }
    public static CollectionReference getCollectionRefManager() {
        return collectionRefManager;
    }
}
