package com.example.bagrutproject.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.bagrutproject.R;
import com.example.bagrutproject.databinding.ActivityHomeBinding;
import com.example.bagrutproject.utils.FBAuthHelper;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity implements FBAuthHelper.FBReply {

    private FBAuthHelper fbAuthHelper;
    ActivityHomeBinding binding;
    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new InventoryFragment());

        binding.bottomNavigationView.setOnItemReselectedListener(item -> {
            switch (item.getItemId()){
                case R.id.ordersItem:
                    replaceFragment(new OrdersFragment());
                    break;
                case R.id.productsItem:
                    replaceFragment(new ForSaleFragment());
                    break;
                case R.id.InventoryItem:
                    replaceFragment(new InventoryFragment());
                    break;
                case R.id.LogoutItem:
                    fbAuthHelper.logoutUser();
                    Intent intent = new Intent(HomeActivity.this, LogInActivity.class);
                    startActivity(intent);
                    break;
            }
        });

        fbAuthHelper=new FBAuthHelper(this,this);
    }
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void createUserSuccess(FirebaseUser user) {

    }

    @Override
    public void loginSuccess(FirebaseUser user) {

    }

    @Override
    public void logoutSuccess(FirebaseUser user) {
        Toast.makeText(this, "success",
                Toast.LENGTH_SHORT).show();
    }
}