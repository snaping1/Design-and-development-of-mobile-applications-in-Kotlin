package com.example.onlinestore.Activity;

import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.onlinestore.R;
import com.example.onlinestore.databinding.ActivitySplashBinding;


public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(binding.getRoot());

        // Задержка на 3 секунды перед переходом к основному Activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Переход к основному Activity
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);

                // Закрытие SplashActivity
                finish();
            }
        }, 3000); // 3000 миллисекунд = 3 секунды
    }
}