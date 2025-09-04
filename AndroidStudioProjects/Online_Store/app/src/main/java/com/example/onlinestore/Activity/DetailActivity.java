package com.example.onlinestore.Activity;

import android.graphics.Paint;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.onlinestore.Adapter.ColorAdapter;
import com.example.onlinestore.Adapter.PicListAdapter;
import com.example.onlinestore.Adapter.SizeAdapter;
import com.example.onlinestore.Domain.ItemsModel;
import com.example.onlinestore.Helper.ManagmentCart;
import com.example.onlinestore.databinding.ActivityDetailBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    private ActivityDetailBinding binding;
    private ItemsModel object;
    private int numberOder = 1;
    private ManagmentCart managmentCart;
    private boolean isUserLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        managmentCart = new ManagmentCart(this);

        // Проверяем, залогинен ли пользователь
        isUserLoggedIn = FirebaseAuth.getInstance().getCurrentUser() != null;

        getBundles();
        initPicList();
        initSize();
        initColor();

        setupButtons();
    }

    private void initColor() {
        binding.recyclerColor.setAdapter(new ColorAdapter(object.getColor()));
        binding.recyclerColor.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void initSize() {
        binding.recyclerSize.setAdapter(new SizeAdapter(object.getSize()));
        binding.recyclerSize.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void initPicList() {
        ArrayList<String> picList = new ArrayList<>(object.getPicUrl());

        Glide.with(this)
                .load(picList.get(0))
                .into(binding.pic);

        binding.picList.setAdapter(new PicListAdapter(picList, binding.pic));
        binding.picList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void getBundles() {
        object = (ItemsModel) getIntent().getSerializableExtra("object");
        binding.titleTxt.setText(object.getTitle());
        binding.priceTxt.setText("$" + object.getPrice());
        binding.oldPriceTxt.setText("$" + object.getOldPrice());
        binding.oldPriceTxt.setPaintFlags(binding.oldPriceTxt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        binding.descriprionTxt.setText(object.getDescription());

        // Кнопка "Назад"
        binding.backBtn.setOnClickListener(v -> finish());
    }

    private void setupButtons() {
        // Добавить в корзину
        binding.addToCartBtn.setOnClickListener(v -> {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                Toast.makeText(this, "Пожалуйста, войдите в аккаунт", Toast.LENGTH_SHORT).show();
                return;
            }
            object.setNumberingCart(numberOder);
            managmentCart.insertItem(object);
        });

        binding.favBtn.setOnClickListener(v -> {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                Toast.makeText(this, "Пожалуйста, войдите в аккаунт", Toast.LENGTH_SHORT).show();
                return;
            }

            String uid = FirebaseAuth.getInstance().getUid();
            DatabaseReference favRef = FirebaseDatabase.getInstance()
                    .getReference("Users").child(uid).child("Favourites");
            String key = object.getTitle();
            favRef.child(key).setValue(object)
                    .addOnSuccessListener(unused -> Toast.makeText(this, "Добавлено в избранное", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Ошибка при добавлении", Toast.LENGTH_SHORT).show());
        });
    }
}
