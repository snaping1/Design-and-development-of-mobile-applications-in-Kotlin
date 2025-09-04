package com.example.onlinestore.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.onlinestore.R;
import com.example.onlinestore.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.*;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    private TextInputEditText emailEt, passEt;
    private AppCompatButton loginBtn, googleLoginBtn, demoBtn;
    private FirebaseAuth mAuth;

    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onStart() {
        super.onStart();

        // Если пользователь уже вошел — перейти в MainActivity
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            goToHome();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(binding.getRoot());

        emailEt = findViewById(com.example.onlinestore.R.id.emailEt);
        passEt = findViewById(com.example.onlinestore.R.id.passEt);
        loginBtn = findViewById(com.example.onlinestore.R.id.login_btn);
        googleLoginBtn = findViewById(com.example.onlinestore.R.id.google_login);
        demoBtn = findViewById(R.id.demoBtn);
        TextView fromLoginToRegister = findViewById(com.example.onlinestore.R.id.from_login_to_register);

        mAuth = FirebaseAuth.getInstance();

        // Настройка входа через Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(com.example.onlinestore.R.string.client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // === Email/Password вход ===
        loginBtn.setOnClickListener(v -> {
            String email = emailEt.getText().toString().trim();
            String password = passEt.getText().toString().trim();
            loginWithEmailPassword(email, password);
        });

        // === Google вход ===
        googleLoginBtn.setOnClickListener(v -> loginWithGoogle());

        demoBtn.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
            prefs.edit().putBoolean("isDemoUser", true).apply();

            Toast.makeText(this, "Вы вошли в демо-режиме", Toast.LENGTH_SHORT).show();
            goToHome();
        });


        // Переход к регистрации
        fromLoginToRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            overridePendingTransition(com.example.onlinestore.R.anim.slide_in, R.anim.slide_out);
            finish();
        });
    }

    // === Обработка результата входа через Google ===
    private final ActivityResultLauncher<Intent> googleLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    handleGoogleSignInResult(result.getData());
                }
            });

    // === ФУНКЦИЯ: Вход через Email и Пароль ===
    private void loginWithEmailPassword(String email, String password) {
        // Проверка: пустой email
        if (email.isEmpty()) {
            emailEt.setError("Введите email");
            emailEt.requestFocus();
            return;
        }

        // Проверка: email по шаблону
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEt.setError("Некорректный email");
            emailEt.requestFocus();
            return;
        }

        // Проверка: пустой пароль
        if (password.isEmpty()) {
            passEt.setError("Введите пароль");
            passEt.requestFocus();
            return;
        }

        // Проверка: минимальная длина пароля
        if (password.length() < 6) {
            passEt.setError("Пароль должен быть не менее 6 символов");
            passEt.requestFocus();
            return;
        }

        // Попытка входа через Firebase
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
                    prefs.edit().putString("userEmail", email).apply();
                    Toast.makeText(this, "Успешный вход", Toast.LENGTH_SHORT).show();
                    goToHome();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Ошибка входа: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // === ФУНКЦИЯ: Запуск входа через Google ===
    private void loginWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        googleLauncher.launch(signInIntent);
    }

    // === ФУНКЦИЯ: Обработка результата Google входа ===
    private void handleGoogleSignInResult(Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            firebaseAuthWithGoogle(account.getIdToken());
        } catch (ApiException e) {
            Toast.makeText(this, "Ошибка Google входа: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // === ФУНКЦИЯ: Аутентификация через Firebase с Google учеткой ===
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    Toast.makeText(this, "Успешный вход через Google", Toast.LENGTH_SHORT).show();
                    goToHome();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Ошибка Google входа: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // === Переход на домашнюю страницу ===
    private void goToHome() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
