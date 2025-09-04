package com.example.onlinestore.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.onlinestore.Fragment.CartFragment;
import com.example.onlinestore.Fragment.FavouriteFragment;
import com.example.onlinestore.Fragment.HomeFragment;
import com.example.onlinestore.Fragment.ProfileFragment;
import com.example.onlinestore.R;
import com.example.onlinestore.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Показываем стартовый фрагмент
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        setupBottomNavigation();

        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        boolean isDemo = prefs.getBoolean("isDemoUser", false);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "Вы используете демоверсию, пожалуйста войдите", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void setupBottomNavigation() {
        binding.bottomNavigation.setItemSelected(R.id.home, true);

        binding.bottomNavigation.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {
                Fragment fragment = null;

                if (id == R.id.home) {
                    fragment = new HomeFragment();

                } else if (id == R.id.favourites) {
                    if (isDemoUser()) {
                        showRegisterDialog();
                        return;
                    }
                    fragment = new FavouriteFragment();

                } else if (id == R.id.cart) {
                    if (isDemoUser()) {
                        showRegisterDialog();
                        return;
                    }
                    fragment = new CartFragment();

                } else if (id == R.id.profile) {
                    if (isDemoUser()) {
                        showRegisterDialog();
                        return;
                    }
                    fragment = new ProfileFragment();
                }

                if (fragment != null) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .commit();
                }
            }
        });

    }

    private boolean isDemoUser() {
        // Если пользователь не авторизован через Firebase, считаем его демо-пользователем
        return FirebaseAuth.getInstance().getCurrentUser() == null;
    }

    private void showRegisterDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Только для зарегистрированных")
                .setMessage("Пожалуйста, зарегистрируйтесь или войдите, чтобы использовать эту функцию.")
                .setPositiveButton("Зарегистрироваться", (dialog, which) -> {
                    startActivity(new Intent(this, RegisterActivity.class));
                })
                .setNegativeButton("Отмена", null)
                .show();
    }
}
