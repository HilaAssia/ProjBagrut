package com.example.bagrutproject.views;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bagrutproject.R;
import com.example.bagrutproject.model.LogoutListener;
import com.example.bagrutproject.model.Product;
import com.example.bagrutproject.utils.FireStoreHelper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ForSaleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForSaleFragment extends Fragment implements FireStoreHelper.FBReply {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ProductsAdapter productsAdapter;
    RecyclerView rvSell;
    FireStoreHelper fireStoreHelper;
    Spinner spinner;
    Dialog dialog;

    public ForSaleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ForSaleFragment newInstance(String param1, String param2) {
        ForSaleFragment fragment = new ForSaleFragment();
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

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_for_sale, container, false);
        fireStoreHelper=new FireStoreHelper(this);
        rvSell= (RecyclerView) view.findViewById(R.id.rvSell);
        dialog=new Dialog(ForSaleFragment.this.getContext());
        spinner = (Spinner) view.findViewById(R.id.topNav); // מוצא את הספינר מהעיצוב
        setOptions();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // מאזין לבחירת קטגוריה
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedItem = parentView.getItemAtPosition(position).toString(); // מקבל את הקטגוריה שנבחרה
                Toast.makeText(ForSaleFragment.this.getContext(), "בחרת: " + selectedItem, Toast.LENGTH_SHORT).show(); // מציג הודעה
                if (selectedItem.equals("logout")) {
                    new AlertDialog.Builder(requireContext())
                            .setTitle("logout")
                            .setMessage("are you sure you want to logout?")
                            .setPositiveButton("yes", (dialog, which) -> {
                                if (getActivity() instanceof LogoutListener) {
                                    ((LogoutListener) getActivity()).logout();
                                }
                            })
                            .setNegativeButton("cancel", (dialog, which) -> {
                                dialog.dismiss(); // פשוט סוגר את הדיאלוג
                            })
                            .show();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // פעולה שלא מתבצעת כלום אם לא נבחר כלום
            }
        });

        // Inflate the layout for this fragment
        setupRecyclerView();

        return view;
    }

    private void setupRecyclerView(){
        Query query = FireStoreHelper.getCollectionRefProduct().whereEqualTo("forSale", true).orderBy("name",
                Query.Direction.DESCENDING);//מבצע סינון ומחזיר רק את המסמכים שבהם השדה "forSale" הוא true ומסדר אותם לפי השם
        FirestoreRecyclerOptions<Product> options=new FirestoreRecyclerOptions.Builder<Product>()
                .setQuery(query, Product.class).build();
        rvSell.setLayoutManager(new LinearLayoutManager(ForSaleFragment.this.getContext()));
        productsAdapter = new ProductsAdapter(options,ForSaleFragment.this.getContext(),false,fireStoreHelper);
        rvSell.setAdapter(productsAdapter);

        productsAdapter.setOnProductDeleteRequestListener(productId -> {
            showDeleteDialog(productId);
        });
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
    public void onProductsLoaded(ArrayList<Product> products) {

    }

    @Override
    public void onDeleteSuccess() {
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
        Toast.makeText(this.getContext(), "Product deleted", Toast.LENGTH_SHORT).show();
    }

    public void updateSpinner(ArrayList<String> items) { // עדכון הספינר עם רשימת קטגוריות
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter); // מחבר את הרשימה לספינר
    }

    public void setOptions() { // שליפת קטגוריות מ-Firestore
        ArrayList<String> categoriesList = new ArrayList<>();
        categoriesList.add(""); // קטגוריה ברירת מחדל

        categoriesList.add("logout");

        updateSpinner(categoriesList);
    }

    private void showDeleteDialog(String productId) {
        dialog.setContentView(R.layout.delete_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        Button btnDelete = dialog.findViewById(R.id.btnDELETE);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);

        btnDelete.setOnClickListener(v -> {
            productsAdapter.deleteProduct(productId);
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}