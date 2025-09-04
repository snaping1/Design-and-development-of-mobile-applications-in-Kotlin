package com.example.onlinestore.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.onlinestore.R;

public class EditProfileFragment extends Fragment {

    private EditText nameInput, emailInput, pincodeInput, addressInput, cityInput, streetInput, countryInput;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        nameInput = root.findViewById(R.id.nameInput);
        emailInput = root.findViewById(R.id.emailInput);
        pincodeInput = root.findViewById(R.id.pincodeInput);
        addressInput = root.findViewById(R.id.addressInput);
        cityInput = root.findViewById(R.id.cityInput);
        streetInput = root.findViewById(R.id.streetInput);
        countryInput = root.findViewById(R.id.countryInput);

        root.findViewById(R.id.saveBtn).setOnClickListener(v -> saveProfile());

        loadProfileData();

        return root;
    }

    private void loadProfileData() {
        SharedPreferences prefs = requireContext().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        nameInput.setText(prefs.getString("userName", ""));
        emailInput.setText(prefs.getString("userEmail", ""));
        pincodeInput.setText(prefs.getString("pinCode", ""));
        addressInput.setText(prefs.getString("address", ""));
        cityInput.setText(prefs.getString("city", ""));
        streetInput.setText(prefs.getString("street", ""));
        countryInput.setText(prefs.getString("country", ""));
    }

    private void saveProfile() {
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String pincode = pincodeInput.getText().toString().trim();
        String address = addressInput.getText().toString().trim();
        String city = cityInput.getText().toString().trim();
        String street = streetInput.getText().toString().trim();
        String country = countryInput.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email)) {
            Toast.makeText(requireContext(), "Пожалуйста, заполните имя и email", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = requireContext().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("userName", name);
        editor.putString("userEmail", email);
        editor.putString("pinCode", pincode);
        editor.putString("address", address);
        editor.putString("city", city);
        editor.putString("street", street);
        editor.putString("country", country);
        editor.apply();

        Toast.makeText(requireContext(), "Профиль сохранён", Toast.LENGTH_SHORT).show();

        // Возврат к профилю
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}
