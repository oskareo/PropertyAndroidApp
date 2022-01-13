package com.example.madpropertypal.fragment.report;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.example.madpropertypal.databinding.FragmentReportAddBinding;
import com.example.madpropertypal.dialog.LoadingDialog;
import com.example.madpropertypal.fragment.property.PropertyDetailFragmentArgs;
import com.example.madpropertypal.model.Property;
import com.example.madpropertypal.model.Report;
import com.example.madpropertypal.model.api_response.ReportResponse;
import com.example.madpropertypal.network.ApiClient;
import com.example.madpropertypal.network.ApiInterface;
import com.example.madpropertypal.util.AppDataBase;
import com.example.madpropertypal.util.Utils;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportAddFragment extends Fragment {

    private static final String TAG = "ReportAdd";
    public FragmentReportAddBinding binding;
    private Context context;
    private Property property;
    /*This date picker dialog will be opened for Date of Viewing and for Expiry date*/
    DatePickerDialog datePickerDialog;
    long dateOfViewing = 0, expiryDate = 0;
    private LoadingDialog loadingDialog;
    private AppDataBase appDataBase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentReportAddBinding.inflate(inflater, container, false);
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
        binding.tvPropertyName.setText(property.getName());
        /*Initially date of viewing will be set for current time, but user can edit this*/
        dateOfViewing = System.currentTimeMillis();
        binding.etDateOfViewing.setText(Utils.getFormattedDate(dateOfViewing));
        /*To disable soft keyboard, as these are supposed to open date picker*/
        binding.etDateOfViewing.setShowSoftInputOnFocus(false);
        binding.etOfferExpiryDate.setShowSoftInputOnFocus(false);
    }

    private void setClickListener() {

        binding.ivBack.setOnClickListener(v -> getActivity().onBackPressed());

        binding.etDateOfViewing.setOnClickListener(v -> {
            callDatePicker(binding.etDateOfViewing);
        });

        binding.etOfferExpiryDate.setOnClickListener(v -> {
            callDatePicker(binding.etOfferExpiryDate);
        });

        binding.btnAdd.setOnClickListener(v -> {

            String interest = binding.etInterest.getText().toString().trim();
            if (TextUtils.isEmpty(interest)) {
                Utils.showErrorAndGiveFocus(context, binding.etInterest, "Please enter Interest");
                return;
            }

            String offerPrice = binding.etOfferPrice.getText().toString().trim();
            if (TextUtils.isEmpty(offerPrice)) {
                offerPrice = "0";
            }

            String conditionsOfOffer = binding.etConditionsOfOffer.getText().toString().trim();
            if (TextUtils.isEmpty(conditionsOfOffer)) {
                conditionsOfOffer = "";
            }

            String viewingComments = binding.etViewingComments.getText().toString().trim();
            if (TextUtils.isEmpty(viewingComments)) {
                viewingComments = "";
            }

            Report report = new Report(1, property.getNumber(), dateOfViewing, interest, Long.parseLong(offerPrice), expiryDate, conditionsOfOffer, viewingComments);
            if (Utils.isNetworkAvailable(context)) {
                uploadReportToServer(report);
            } else {
                Utils.showLongToast(context, "No Network Connection");
            }
        });
    }

    private void callDatePicker(EditText editText) {
        // calender class's instance and get current date , month and year from calender
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        // date picker dialog
        datePickerDialog = new DatePickerDialog(getActivity(),
                (view, year, monthOfYear, dayOfMonth) -> {

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, monthOfYear + 1, dayOfMonth, hour, minute);

                    editText.setText(Utils.getFormattedDate(calendar.getTimeInMillis()));

                    if (editText.getId() == binding.etDateOfViewing.getId()) {
                        dateOfViewing = calendar.getTimeInMillis();
                    } else {
                        expiryDate = calendar.getTimeInMillis();
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void uploadReportToServer(Report report) {
        loadingDialog.show("Add Report in progress");
        try {
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

            /*Params are set in the API*/
            Call<ReportResponse> apiCall = apiInterface.addReport(
                    report.getPropertyId(),
                    report.getDateOfViewing(),
                    report.getInterest(),
                    report.getOfferPrice(),
                    report.getOfferExpiryDate(),
                    report.getConditionsOfOffer(),
                    report.getViewingComments()
            );
            apiCall.enqueue(new Callback<ReportResponse>() {
                @Override
                public void onResponse(Call<ReportResponse> call, Response<ReportResponse> response) {
                    Log.d(TAG, "onResponse -> " + response.code());
                    if (response.isSuccessful() && response.code() == 200) {
                        ReportResponse apiResponse = response.body();
                        Log.d(TAG, "onResponse -> Successful -> " + apiResponse);
                        if (apiResponse.getData().size() > 0) {
                            List<ReportResponse.Data> allReports = apiResponse.getData();
                            /*API returns all reports of current property, but we will get last object of report
                             * which is the newly added report*/
                            ReportResponse.Data newReport = allReports.get(allReports.size() - 1);
                            Report report = new Report(
                                    Integer.parseInt(newReport.getId()),
                                    Integer.parseInt(newReport.getPropertyId()),
                                    Long.parseLong(newReport.getDateOfViewing()),
                                    newReport.getInterest(),
                                    Long.parseLong(newReport.getOfferPrice()),
                                    Long.parseLong(newReport.getOfferExpiryDate()),
                                    newReport.getConditionsOfOffer(),
                                    newReport.getViewingComments()
                            );
                            Log.d(TAG, "onResponse -> Inserted Report Id -> " + newReport.getId());
                            Utils.showShortToast(context, "Report Added");

                            /*Report is added to SQLite*/
                            appDataBase.insertReport(report);
                            loadingDialog.dismiss();
                            getActivity().onBackPressed();
                        } else {
                            loadingDialog.dismiss();
                            Utils.showLongToast(context, "No Reports Found");
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