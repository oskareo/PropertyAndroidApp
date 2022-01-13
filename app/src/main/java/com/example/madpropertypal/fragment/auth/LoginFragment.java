package com.example.madpropertypal.fragment.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.madpropertypal.R;
import com.example.madpropertypal.activity.MainActivity;
import com.example.madpropertypal.databinding.FragmentLoginBinding;
import com.example.madpropertypal.dialog.LoadingDialog;
import com.example.madpropertypal.model.User;
import com.example.madpropertypal.model.api_response.UserResponse;
import com.example.madpropertypal.network.ApiClient;
import com.example.madpropertypal.network.ApiInterface;
import com.example.madpropertypal.util.AppDataBase;
import com.example.madpropertypal.util.MyPreferences;
import com.example.madpropertypal.util.Utils;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    public FragmentLoginBinding binding;
    private Context context;
    private LoadingDialog loadingDialog;
    public static final String TAG = "LoginFragment";
    private AppDataBase appDataBase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        init();
        setClickListener();
        return binding.getRoot();
    }

    private void init() {
        context = getActivity();
        loadingDialog = new LoadingDialog(context);
    }

    private void setClickListener() {
        binding.btnLogin.setOnClickListener(v -> {

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

            appDataBase = new AppDataBase(context);
            appDataBase.open();
            /*Here user is verified from SQLite*/
            User user = appDataBase.verifyLogin(email, password);
            if (user == null) {
                /*If the user is not present in the SQLIte, there is a possibility that
                 it will be present in Online Server*/
                Utils.showShortToast(context, "User Does not exists in Local Database");

                /*If network connection is available, Login api will be called*/
                if (Utils.isNetworkAvailable(context)) {
                    login(email, password);
                } else {
                    Utils.showLongToast(context, "Network not available");
                }
            } else {
                /*If user is present is SQLite then its id will be saved in Preferences
                 * Which I'll use everywhere in the app*/
                saveIdInPreferences(user.getId());
            }
        });

        binding.tvCreateAccount.setOnClickListener(v -> {
                    binding.etEmail.setText("");
                    binding.etPassword.setText("");
                    /*The below code launches Register Fragment by Navigation Controller*/
                    NavHostFragment.findNavController(this).navigate(R.id.action_loginFragment_to_registerFragment);
                }
        );
    }

    private void saveIdInPreferences(int id) {
        new MyPreferences(context).saveUserId(id);
        startActivity(new Intent(context, MainActivity.class));
        getActivity().finish();
    }

    private void login(String email, String password) {
        loadingDialog.show("Login user in progress");
        try {

            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<UserResponse> apiCall = apiInterface.loginUser(
                    email, password);

            apiCall.enqueue(new Callback<UserResponse>() {

                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    Log.d(TAG, "onResponse -> " + response.code());
                    loadingDialog.dismiss();
                    if (response.isSuccessful() && response.code() == 200) {
                        /*If response is successful I'll parse the response in our model*/
                        UserResponse apiResponse = response.body();
                        Log.d(TAG, "onResponse -> Successful -> " + apiResponse);
                        if (apiResponse.getData().size() > 0) {
                            /*API returns an array, though it has only one element, so I'll get first object*/
                            UserResponse.Data apiUser = apiResponse.getData().get(0);
                            /*Now I'll parse response object in our User model*/
                            User user = new User(
                                    Integer.parseInt(apiUser.getId()),
                                    apiUser.getName(),
                                    apiUser.getEmail(),
                                    apiUser.getPassword(),
                                    Long.parseLong(apiUser.getCreated_date()));

                            /*Save this user in SQLIte, so next time user can Login from SQLite as well*/
                            saveToLocalDb(user);
                        } else {
                            Utils.showLongToast(context, "Wrong Email or password");
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
        long insertedId = appDataBase.insertUser(user);
        if (insertedId > -1) {
            Utils.showShortToast(context, "User Login Successfully");
            saveIdInPreferences(user.getId());
        }
        appDataBase.close();
    }
}