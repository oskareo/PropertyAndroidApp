package com.example.madpropertypal.fragment.report;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.madpropertypal.adapter.ReportAdapter;
import com.example.madpropertypal.databinding.FragmentPropertyReportsBinding;
import com.example.madpropertypal.dialog.LoadingDialog;
import com.example.madpropertypal.fragment.property.PropertyDetailFragmentArgs;
import com.example.madpropertypal.model.Property;
import com.example.madpropertypal.model.Report;
import com.example.madpropertypal.model.api_response.ReportResponse;
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

public class PropertyReportFragment extends Fragment {

    private static final String TAG = "PropertyReport";
    public FragmentPropertyReportsBinding binding;
    private ReportAdapter adapter;
    private Context context;
    private Property property;
    private LoadingDialog loadingDialog;
    private AppDataBase appDataBase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPropertyReportsBinding.inflate(inflater, container, false);
        init();
        setClickListener();
        return binding.getRoot();
    }

    private void init() {
        context = getActivity();
        appDataBase = new AppDataBase(context);
        appDataBase.open();
        loadingDialog = new LoadingDialog(context);
        property = PropertyDetailFragmentArgs.fromBundle(getArguments()).getProperty();

        /*If network is available the it will get reports from Online Server*/
        if (Utils.isNetworkAvailable(context)) {
            getReportsFromServer(property.getNumber());
        } else {
            /*If there is no connection then reports that were previously fetch from server will be shown*/
            getReportsFromLocal();
        }

        if (property.getUserId() != new MyPreferences(context).getSavedUserId()) {
            binding.fabAddReport.setVisibility(View.GONE);
        }
    }

    private void setClickListener() {

        binding.ivBack.setOnClickListener(v -> getActivity().onBackPressed());

        binding.fabAddReport.setOnClickListener(v -> {
            if (Utils.isNetworkAvailable(context)) {
                PropertyReportFragmentDirections.ActionPropertyReportFragmentToReportAddFragment action =
                        PropertyReportFragmentDirections.actionPropertyReportFragmentToReportAddFragment(property);
                NavHostFragment.findNavController(this)
                        .navigate(action);
            } else {
                Utils.showLongToast(context, "No Network Connection");
            }
        });
    }

    private void getReportsFromLocal() {
        if (appDataBase.getTotalReports(property.getNumber()) > 0) {
            adapter = new ReportAdapter(context, appDataBase.getReports(property.getNumber()));
            binding.rclViewReport.setAdapter(adapter);
            binding.ivNoData.setVisibility(View.GONE);
        } else {
            Utils.showLongToast(context, "No Reports Found in Local DB");
            binding.ivNoData.setVisibility(View.VISIBLE);
        }
    }

    private void getReportsFromServer(int propertyId) {
        loadingDialog.show("Get Reports in progress");
        try {
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<ReportResponse> apiCall = apiInterface.getAllReports(propertyId);
            apiCall.enqueue(new Callback<ReportResponse>() {
                @Override
                public void onResponse(Call<ReportResponse> call, Response<ReportResponse> response) {
                    Log.d(TAG, "onResponse -> " + response.code());
                    if (response.isSuccessful() && response.code() == 200) {
                        ReportResponse apiResponse = response.body();
                        Log.d(TAG, "onResponse -> Successful -> " + apiResponse);
                        if (apiResponse.getData().size() > 0) {
                            List<ReportResponse.Data> allReports = apiResponse.getData();

                            /*Loop through all reports*/
                            for (int i = 0; i < allReports.size(); i++) {
                                ReportResponse.Data current = allReports.get(i);
                                /*Check if reports is present in SQLite, otherwise report will be added in SQLite*/
                                if (!appDataBase.checkIfReportExists(Integer.parseInt(current.getId()))) {
                                    Report report = new Report(
                                            Integer.parseInt(current.getId()),
                                            Integer.parseInt(current.getPropertyId()),
                                            Long.parseLong(current.getDateOfViewing()),
                                            current.getInterest(),
                                            Long.parseLong(current.getOfferPrice()),
                                            Long.parseLong(current.getOfferExpiryDate()),
                                            current.getConditionsOfOffer(),
                                            current.getViewingComments()
                                    );
                                    appDataBase.insertReport(report);
                                }
                            }
                            loadingDialog.dismiss();
                            getReportsFromLocal();
                        } else {
                            loadingDialog.dismiss();
                            Utils.showLongToast(context, "No Reports Found");
                            binding.ivNoData.setVisibility(View.VISIBLE);
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
                public void onFailure(Call<ReportResponse> call, Throwable t) {
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