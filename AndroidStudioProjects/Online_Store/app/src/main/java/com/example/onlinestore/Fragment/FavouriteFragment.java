package com.example.onlinestore.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.onlinestore.Activity.CartActivity;
import com.example.onlinestore.Adapter.FavouriteAdapter;
import com.example.onlinestore.Domain.ItemsModel;
import com.example.onlinestore.ViewModel.MainViewModel;
import com.example.onlinestore.databinding.FragmentFavouriteBinding;


import java.util.ArrayList;

public class FavouriteFragment extends Fragment {
    private FragmentFavouriteBinding binding;
    private MainViewModel viewModel;

    private ArrayList<ItemsModel> allFavouriteItems = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentFavouriteBinding.inflate(inflater, container, false);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        binding.searchTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterFavouriteItems(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        initFavourite();
        setupBottomNavigation();

        return binding.getRoot();
    }

    private void setupBottomNavigation() {
        binding.cartBtn.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), CartActivity.class)));
    }

    private void initFavourite() {
        binding.progressBarPopular.setVisibility(View.VISIBLE);

        viewModel.loadFavourites().observe(getViewLifecycleOwner(), favouriteItems -> {
            if (favouriteItems != null && !favouriteItems.isEmpty()) {
                allFavouriteItems.clear();
                allFavouriteItems.addAll(favouriteItems);

                binding.favouriteView.setLayoutManager(
                        new GridLayoutManager(requireContext(), 2)
                );
                binding.favouriteView.setAdapter(new FavouriteAdapter(new ArrayList<>(allFavouriteItems)));
                binding.favouriteView.setNestedScrollingEnabled(true);
            } else {
                allFavouriteItems.clear();
                binding.favouriteView.setAdapter(new FavouriteAdapter(new ArrayList<>()));
            }
            binding.progressBarPopular.setVisibility(View.GONE);
        });
    }

    private void filterFavouriteItems(String query) {
        ArrayList<ItemsModel> filteredItems = new ArrayList<>();
        for (ItemsModel item : allFavouriteItems) {
            if (item.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredItems.add(item);
            }
        }
        binding.favouriteView.setAdapter(new FavouriteAdapter(filteredItems));
    }
}