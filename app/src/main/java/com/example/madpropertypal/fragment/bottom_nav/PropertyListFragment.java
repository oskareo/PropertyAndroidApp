package com.example.madpropertypal.fragment.bottom_nav;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.madpropertypal.R;
import com.example.madpropertypal.adapter.PropertyAdapter;
import com.example.madpropertypal.databinding.FragmentPropertyListBinding;
import com.example.madpropertypal.dialog.LoadingDialog;
import com.example.madpropertypal.model.Property;
import com.example.madpropertypal.model.api_response.PropertyResponse;
import com.example.madpropertypal.network.ApiClient;
import com.example.madpropertypal.network.ApiInterface;
import com.example.madpropertypal.util.AppDataBase;
import com.example.madpropertypal.util.MyPreferences;
import com.example.madpropertypal.util.Utils;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PropertyListFragment extends Fragment {

    public FragmentPropertyListBinding binding;
    private PropertyAdapter adapter;
    private Context context;
    private AppDataBase appDataBase;
    private LoadingDialog loadingDialog;
    public static final String TAG = "PropertyList";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPropertyListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    private void init() {
        context = getActivity();
        loadingDialog = new LoadingDialog(context);
        appDataBase = new AppDataBase(context);
        appDataBase.open();

        /*Here I am using a variable which is saved in saved preferences
         * The purpose of this key-value is that, when App is launched its value will be
         * true and if the value is true then API will be called to get all properties
         * And right after calling API I will set its value to false so as long as the app
         * will be running, it will not call API to get Properties. but from the SQLite
         * Don't worry, newly added property will be auto fetch from SQLIte as it will be added
         * when Property is added in the Online DB*/
        if (Utils.isNetworkAvailable(context) && new MyPreferences(context).getIsAppFirstTime()) {
            getDataFromOnlineDB(0);
        } else {
            getDataFromLocalDB();
        }

        setClickListener();
    }

    private void setClickListener() {
        binding.fabAddProperty.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_propertyListFragment_to_propertyAddFragment)
        );
    }

    private void getDataFromLocalDB() {
        if (appDataBase.getTotalProperties() > 0) {
            adapter = new PropertyAdapter(
                    context,
                    appDataBase.getPropertyList(),
                    model -> {
                        PropertyListFragmentDirections.ActionPropertyListFragmentToPropertyDetailFragment action =
                                PropertyListFragmentDirections.actionPropertyListFragmentToPropertyDetailFragment(model);
                        NavHostFragment.findNavController(this).navigate(action);
                    });
            binding.rclViewProperty.setAdapter(adapter);
            enableSearching();
            binding.ivNoData.setVisibility(View.GONE);
        }else {
            binding.ivNoData.setVisibility(View.VISIBLE);
        }
    }

    public void enableSearching() {
        binding.searchView.setQuery("", false);
        binding.searchView.clearFocus();
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                adapter.getFilter().filter(query);
                return false;
            }
        });
    }

    private void getDataFromOnlineDB(int userId) {
        loadingDialog.show("Get Properties in progress");
        try {
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<PropertyResponse> apiCall = apiInterface.getAllProperty(userId);

            apiCall.enqueue(new Callback<PropertyResponse>() {
                @Override
                public void onResponse(Call<PropertyResponse> call, Response<PropertyResponse> response) {
                    Log.d(TAG, "onResponse -> " + response.code());
                    if (response.isSuccessful() && response.code() == 200) {

                        /*Response is parsed*/
                        PropertyResponse apiResponse = response.body();
                        Log.d(TAG, "onResponse -> Successful -> " + apiResponse);

                        /*Check if response has data*/
                        if (apiResponse.getData().size() > 0) {
                            List<PropertyResponse.Data> allReports = apiResponse.getData();

                            /*This will loop through all data items get*/
                            for (int i = 0; i < allReports.size(); i++) {
                                PropertyResponse.Data model = allReports.get(i);

                                /*Check if current property is present is present in SQLite,
                                * If not, then property will be added to SQLite*/
                                if (!appDataBase.checkIfPropertyExists(Integer.parseInt(model.getId()))) {
                                    Property property = new Property(
                                            Integer.parseInt(model.getId()),
                                            model.getName(),
                                            model.getType(),
                                            model.getLeastype(),
                                            model.getLocation(),
                                            Long.parseLong(model.getBedrooms()),
                                            Long.parseLong(model.getBathrooms()),
                                            Long.parseLong(model.getSize()),
                                            Long.parseLong(model.getAsking_price()),
                                            model.getLocal_amenities(),
                                            model.getDescription(),
                                            Long.parseLong(model.getFloors()),
                                            null,
                                            Integer.parseInt(model.getUser_id()),
                                            Long.parseLong(model.getCreation_date()),
                                            model.getImage(),
                                            0,
                                            1
                                    );
                                    /*Here property is inserted in SQLite*/
                                    appDataBase.insertProperty(property);
                                }
                            }
                            loadingDialog.dismiss();

                            /*After loop missing data is added in SQLite now, data will be fetched from Synced SQLite*/
                            getDataFromLocalDB();

                            /*In Shared Pref, First time is set to false so as long as app will not destroy, it will
                            * not call API for latest data, But new data added will be auto synced*/
                            new MyPreferences(context).setFirstTime(false);
                        } else {
                            loadingDialog.dismiss();
                            Utils.showLongToast(context, "No Property Found");
                        }
                    } else {
                        loadingDialog.dismiss();
                        Log.d(TAG, "onResponse -> Unsuccessful ");
                        try {
                            Utils.showLongToast(context, "Response Error\n" + response.errorBody().string());
                        } catch (IOException e) {
                            Utils.showLongToast(context, "Response Exception\n" + e.getMessage());
                        }
                    }
                }

                @Override
                public void onFailure(Call<PropertyResponse> call, Throwable t) {
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
}