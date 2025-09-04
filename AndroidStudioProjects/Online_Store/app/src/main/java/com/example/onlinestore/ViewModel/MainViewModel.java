package com.example.onlinestore.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.onlinestore.Domain.BannerModel;
import com.example.onlinestore.Domain.CategoryModel;
import com.example.onlinestore.Domain.ItemsModel;
import com.example.onlinestore.Repository.MainRepository;

import java.util.ArrayList;

public class MainViewModel extends ViewModel {
    private final MainRepository repository = new MainRepository();

    public LiveData<ArrayList<CategoryModel>> loadCategory(){
        return repository.loadCategory();
    }

    public LiveData<ArrayList<BannerModel>> loadBanner(){
        return repository.loadBanner();
    }

    public LiveData<ArrayList<ItemsModel>> loadPopular(){
        return repository.loadPopular();
    }

    public LiveData<ArrayList<ItemsModel>> loadFavourites() {
        return repository.loadFavourites();
    }
}
