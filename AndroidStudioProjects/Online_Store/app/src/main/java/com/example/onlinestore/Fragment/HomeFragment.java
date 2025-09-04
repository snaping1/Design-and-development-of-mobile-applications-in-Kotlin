package com.example.onlinestore.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.content.Intent;
import android.widget.Toast;

import com.example.onlinestore.Adapter.CategoryAdapter;
import com.example.onlinestore.Adapter.PopularAdapter;
import com.example.onlinestore.Adapter.SliderAdapter;
import com.example.onlinestore.Domain.BannerModel;
import com.example.onlinestore.Domain.ItemsModel;
import com.example.onlinestore.ViewModel.MainViewModel;
import com.example.onlinestore.databinding.FragmentHomeBinding;
import com.example.onlinestore.Activity.CartActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private MainViewModel viewModel;

    private ArrayList<ItemsModel> allPopularItems = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        requireActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        viewModel = new MainViewModel();

        binding.searchTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterPopularItems(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        initCategory();
        initSlider();
        initPopular();
        setupBottomNavigation();



        return binding.getRoot();
    }

    private void setupBottomNavigation() {
        binding.cartBtn.setOnClickListener(v -> {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                Toast.makeText(requireContext(), "Пожалуйста, зарегистрируйтесь, чтобы перейти в корзину", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(requireContext(), "Переходим в корзину", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(requireContext(), CartActivity.class));
        });
    }

    private void initPopular() {
        binding.progressBarPopular.setVisibility(View.VISIBLE);
        viewModel.loadPopular().observe(getViewLifecycleOwner(), itemsModels -> {
            if (itemsModels != null && !itemsModels.isEmpty()) {
                allPopularItems.clear();
                allPopularItems.addAll(itemsModels);

                binding.popularView.setLayoutManager(
                        new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
                binding.popularView.setAdapter(new PopularAdapter(new ArrayList<>(allPopularItems)));
                binding.popularView.setNestedScrollingEnabled(true);
            }
            binding.progressBarPopular.setVisibility(View.GONE);
        });
    }

    private void initSlider() {
        binding.progressBarSlider.setVisibility(View.VISIBLE);
        viewModel.loadBanner().observe(getViewLifecycleOwner(), bannerModels -> {
            if (bannerModels != null && !bannerModels.isEmpty()) {
                setupSlider(bannerModels);
                binding.progressBarSlider.setVisibility(View.GONE);
            }
        });
    }

    private void setupSlider(ArrayList<BannerModel> bannerModels) {
        binding.viewPagerSlider.setAdapter(new SliderAdapter(bannerModels, binding.viewPagerSlider));
        binding.viewPagerSlider.setClipToPadding(false);
        binding.viewPagerSlider.setClipChildren(false);
        binding.viewPagerSlider.setOffscreenPageLimit(3);
        binding.viewPagerSlider.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_ALWAYS);

        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(40));
        binding.viewPagerSlider.setPageTransformer(transformer);
    }

    private void initCategory() {
        binding.progressBarCategory.setVisibility(View.VISIBLE);
        viewModel.loadCategory().observe(getViewLifecycleOwner(), categoryModels -> {
            binding.categoryView.setLayoutManager(
                    new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
            binding.categoryView.setAdapter(new CategoryAdapter(categoryModels));
            binding.categoryView.setNestedScrollingEnabled(true);
            binding.progressBarCategory.setVisibility(View.GONE);
        });
    }

    private void filterPopularItems(String query) {
        ArrayList<ItemsModel> filteredItems = new ArrayList<>();
        for (ItemsModel item : allPopularItems) {
            if (item.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredItems.add(item);
            }
        }
        binding.popularView.setAdapter(new PopularAdapter(filteredItems));
    }
}