package com.example.bagrutproject.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bagrutproject.R;
import com.example.bagrutproject.model.LogoutListener;
import com.example.bagrutproject.model.Order;
import com.example.bagrutproject.utils.FireStoreHelper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrdersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrdersFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    OrdersAdapter ordersAdapter;
    RecyclerView rvOrders;
    Spinner spinner;

    public OrdersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrdersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrdersFragment newInstance(String param1, String param2) {
        OrdersFragment fragment = new OrdersFragment();
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
        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        rvOrders= (RecyclerView) view.findViewById(R.id.rvOrders);
        spinner = (Spinner) view.findViewById(R.id.topNav); // מוצא את הספינר מהעיצוב
        setOptions();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // מאזין לבחירת קטגוריה
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedItem = parentView.getItemAtPosition(position).toString(); // מקבל את הקטגוריה שנבחרה
                Toast.makeText(OrdersFragment.this.getContext(), "בחרת: " + selectedItem, Toast.LENGTH_SHORT).show(); // מציג הודעה
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
        Query query = FireStoreHelper.getCollectionRefOrder();
        FirestoreRecyclerOptions<Order> options=new FirestoreRecyclerOptions.Builder<Order>()
                .setQuery(query, Order.class).build();
        rvOrders.setLayoutManager(new LinearLayoutManager(OrdersFragment.this.getContext()));
        ordersAdapter = new OrdersAdapter(options,OrdersFragment.this.getContext());
        rvOrders.setAdapter(ordersAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        ordersAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        ordersAdapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        ordersAdapter.notifyDataSetChanged();
    }

    public void updateSpinner(ArrayList<String> items) { // עדכון הספינר עם רשימת קטגוריות
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter); // מחבר את הרשימה לספינר
    }

    public void setOptions() {
        ArrayList<String> list = new ArrayList<>();
        list.add(""); // ברירת מחדל

        list.add("logout");

        updateSpinner(list);
    }
}