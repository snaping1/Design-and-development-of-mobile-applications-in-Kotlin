package com.example.onlinestore.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.request.RequestOptions;
import com.example.onlinestore.Activity.DetailActivity;
import com.example.onlinestore.Domain.ItemsModel;
import com.example.onlinestore.databinding.ViewholderFavouriteBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.Viewholder> {

    ArrayList<ItemsModel> items;
    Context context;

    public FavouriteAdapter (ArrayList<ItemsModel> itemsModels) {
        this.items = itemsModels;
    }

    @NonNull
    @Override
    public FavouriteAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewholderFavouriteBinding binding = ViewholderFavouriteBinding
                .inflate(LayoutInflater.from(context), parent, false);

        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouriteAdapter.Viewholder holder, int position) {
        holder.binding.titleTxt.setText(items.get(position).getTitle());
        holder.binding.priceTxt.setText("$" + items.get(position).getPrice());
        holder.binding.ratingTxt.setText("(" + items.get(position).getRating() + ")");
        holder.binding.offPercentTxt.setText(items.get(position).getOffPercent() + " Off");
        holder.binding.oldPriceTxt.setText("$" + items.get(position).getOldPrice());
        holder.binding.oldPriceTxt.setPaintFlags(holder.binding.oldPriceTxt.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);

        RequestOptions options = new RequestOptions();
        options = options.transform(new CenterInside());

        Glide.with(context)
                .load(items.get(position).getPicUrl().get(0))
                .apply(options)
                .into(holder.binding.pic);
        // 👉 Обработчик нажатия на сам товар
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("object", items.get(position));
                context.startActivity(intent);
            }
        });

        // 👉 Обработчик нажатия на кнопку удаления
        holder.binding.removeBtn.setOnClickListener(v -> {
            ItemsModel item = items.get(position);
            removeFromFavourites(item, position);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        ViewholderFavouriteBinding binding;

        public Viewholder(ViewholderFavouriteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private void removeFromFavourites(ItemsModel item, int position) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference("Users")
                .child(userId)
                .child("Favourites")
                .child(item.getId()) // Убедись, что у ItemsModel есть getId()
                .removeValue()
                .addOnSuccessListener(unused -> {
                    items.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, items.size());
                    Toast.makeText(context, "Удалено из избранного", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Ошибка при удалении", Toast.LENGTH_SHORT).show();
                });
    }

}
