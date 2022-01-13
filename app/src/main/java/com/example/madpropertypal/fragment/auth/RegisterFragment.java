package com.example.madpropertypal.fragment.auth;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.madpropertypal.databinding.FragmentRegisterBinding;
import com.example.madpropertypal.dialog.LoadingDialog;
import com.example.madpropertypal.model.User;
import com.example.madpropertypal.model.api_response.UserResponse;
import com.example.madpropertypal.network.ApiClient;
import com.example.madpropertypal.network.ApiInterface;
import com.example.madpropertypal.util.AppDataBase;
import com.example.madpropertypal.util.Utils;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends Fragment {

    public FragmentRegisterBinding binding;
    private Context context;
    private AppDataBase appDataBase;
    private LoadingDialog loadingDialog;
    public static final String TAG = "RegisterFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        init();
        setClickListener();
        return binding.getRoot();
    }

    private void init() {
        context = getActivity();
        loadingDialog = new LoadingDialog(context);
    }

    private void setClickListener() {

        binding.ivBack.setOnClickListener(v -> getActivity().onBackPressed());

        binding.btnRegister.setOnClickListener(v -> {

            String name = binding.etName.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                Utils.showErrorAndGiveFocus(context, binding.etName, "Please enter valid name");
                return;
            }

            String email = binding.etEmail.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                Utils.showErrorAndGiveFocus(context, binding.etEmail, "Please enter valid email");
                return;
            }

            String password = binding.etPassword.getText().toString().trim();
            if (TextUtils.isEmpty(password) || password.length() < 4) {
                Utils.showErrorAndGiveFocus(context, binding.etPassword, "Please enter valid password of length minimum 4");
                return;
            }

            /*If network connection is available, Register api will be called*/
            if (Utils.isNetworkAvailable(context)) {
                User user = new User(0, name, email, password, System.currentTimeMillis());
                register(user);
            } else {
                Utils.showLongToast(context, "Network not available");
            }
        });
    }

    private void register(User user) {
        loadingDialog.show("Register user in progress");
        try {
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<UserResponse> apiCall = apiInterface.registerUser(
                    user.getName(), user.getEmail(), user.getPassword(), user.getCreationDate()
            );

            apiCall.enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    Log.d(TAG, "onResponse -> " + response.code());
                    loadingDialog.dismiss();
                    if (response.isSuccessful() && response.code() == 200) {
                        /*If response is successful I'll parse the response in our model
                        * This response is same as Login*/
                        UserResponse apiResponse = response.body();
                        Log.d(TAG, "onResponse -> Successful -> " + apiResponse);
                        if (apiResponse.getData().size() > 0) {
                            UserResponse.Data data = apiResponse.getData().get(0);
                            User user = new User(
                                    Integer.parseInt(data.getId()),
                                    data.getName(),
                                    data.getEmail(),
                                    data.getPassword(),
                                    Long.parseLong(data.getCreated_date()));

                            /*Save this user in SQLIte, so next time user can Login from SQLite as well*/
                            saveToLocalDb(user);
                        } else {
                            Utils.showLongToast(context, "User Already Exists");
                        }
                    } else {
                        Log.d(TAG, "onResponse -> Unsuccessful ");
                        try {
                            Utils.showLongToast(context, "Response Error\n" + response.errorBody().string());
                        } catch (IOException e) {
                            Utils.showLongToast(context, "Response Exception\n" + e.getMessage());
                        }
                    }
                }

                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {
                    Log.d(TAG, "onFailure -> " + t.getMessage());
                    loadingDialog.dismiss();
                    Utils.showShortToast(context, "Failure\n" + t.getMessage());
                }
            });
        } catch (Exception e) {
            loadingDialog.dismiss();
            Utils.showShortToast(context, "API Exception\n" + e.getMessage());
        }
    }

    private void saveToLocalDb(User user) {
        Log.d(TAG, "saveToLocalDb -> " + user.getId());
        appDataBase = new AppDataBase(context);
        appDataBase.open();
        long insertedId = appDataBase.insertUser(user);
        if (insertedId > -1) {
            Utils.showShortToast(context, "User Created Successfully");
            getActivity().onBackPressed();
        }
        appDataBase.close();
    }
}