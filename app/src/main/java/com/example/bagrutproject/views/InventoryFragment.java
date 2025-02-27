package com.example.bagrutproject.views;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bagrutproject.model.Category;
import com.example.bagrutproject.utils.FireStoreHelper;
import com.example.bagrutproject.R;
import com.example.bagrutproject.model.Product;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InventoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InventoryFragment extends Fragment implements FireStoreHelper.FBReply {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ProductsAdapter productsAdapter;
    RecyclerView rvProducts;
    FireStoreHelper fireStoreHelper;

    public InventoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InventoryFragment newInstance(String param1, String param2) {
        InventoryFragment fragment = new InventoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inventory, container, false);
        rvProducts= (RecyclerView) view.findViewById(R.id.rvProducts);
        fireStoreHelper= new FireStoreHelper(this);
        Dialog dialog = new Dialog(InventoryFragment.this.getContext());
        Button add = (Button) view.findViewById(R.id.btnAdd);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( InventoryFragment.this.getContext(), EditProductActivity.class);
                startActivity(intent);
            }
        });
        Button newCat = (Button) view.findViewById(R.id.newCategory);
        newCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setContentView(R.layout.dialog);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(false);
                EditText etCategory=dialog.findViewById(R.id.etCategory);
                Button buttonAdd=dialog.findViewById(R.id.btnAdd);
                buttonAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Category cat=new Category(etCategory.getText().toString());
                        fireStoreHelper.add(cat);
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        setupRecyclerView();

        return view;
    }

    private void setupRecyclerView(){
        Query query = FireStoreHelper.getCollectionRefProduct().orderBy("name",
                Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Product> options=new FirestoreRecyclerOptions.Builder<Product>()
                .setQuery(query, Product.class).build();
        rvProducts.setLayoutManager(new LinearLayoutManager(InventoryFragment.this.getContext()));
        productsAdapter = new ProductsAdapter(options,InventoryFragment.this.getContext(),false);
        rvProducts.setAdapter(productsAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        productsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        productsAdapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        productsAdapter.notifyDataSetChanged();
    }

    @Override
    public void getAllSuccess(ArrayList<Product> products) {

    }

    @Override
    public void getOneSuccess(Product product) {

    }

    @Override
    public Product[] onProductsLoaded(Product[] products) {
        return products;
    }

}