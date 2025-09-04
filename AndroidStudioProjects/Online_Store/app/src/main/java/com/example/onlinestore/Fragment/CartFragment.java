package com.example.onlinestore.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.onlinestore.Adapter.CartAdapter;
import com.example.onlinestore.Helper.ManagmentCart;
import com.example.onlinestore.databinding.FragmentCartBinding;

public class CartFragment extends Fragment {

    private FragmentCartBinding binding;
    private ManagmentCart managmentCart;
    private double tax;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
        managmentCart = new ManagmentCart(requireContext());

        calculatorCart();
        setVariable();
        initCartList();

        return binding.getRoot();
    }

    private void initCartList() {
        if (managmentCart.getListCart().isEmpty()) {
            binding.emptyTxt.setVisibility(View.VISIBLE);
            binding.scrollView.setVisibility(View.GONE);
        } else {
            binding.emptyTxt.setVisibility(View.GONE);
            binding.scrollView.setVisibility(View.VISIBLE);
        }

        binding.cartView.setLayoutManager(new LinearLayoutManager(
                requireContext(), LinearLayoutManager.VERTICAL, false));
        binding.cartView.setAdapter(new CartAdapter(
                managmentCart.getListCart(), requireContext(), this::calculatorCart));
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void calculatorCart() {
        double percentTax = 0.02;
        double delivery = 10;



    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
