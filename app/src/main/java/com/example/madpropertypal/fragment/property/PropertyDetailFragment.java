package com.example.madpropertypal.fragment.property;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.madpropertypal.activity.GooglemapActivity;
import com.example.madpropertypal.databinding.FragmentPropertyDetailBinding;
import com.example.madpropertypal.dialog.LoadingDialog;
import com.example.madpropertypal.model.LocationModel;
import com.example.madpropertypal.model.Property;
import com.example.madpropertypal.model.api_response.PropertyResponse;
import com.example.madpropertypal.network.ApiClient;
import com.example.madpropertypal.network.ApiInterface;
import com.example.madpropertypal.network.NetworkConstants;
import com.example.madpropertypal.util.AppDataBase;
import com.example.madpropertypal.util.Constants;
import com.example.madpropertypal.util.MyPreferences;
import com.example.madpropertypal.util.Utils;
import com.google.gson.Gson;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class PropertyDetailFragment extends Fragment {

    private static final String TAG = "PropertyDetail";
    public FragmentPropertyDetailBinding binding;
    private Context context;
    private Property property;
    private Uri imageUri = null;
    private ActivityResultLauncher<Intent> locationResultLauncher, cropImageActivityResultLauncher;
    LocationModel locationModel = null;
    private LoadingDialog loadingDialog;
    private AppDataBase appDataBase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPropertyDetailBinding.inflate(inflater, container, false);
        init();
        setClickListener();
        setResultLauncher();
        return binding.getRoot();
    }

    private void init() {
        context = getActivity();
        property = PropertyDetailFragmentArgs.fromBundle(getArguments()).getProperty();
        binding.etLocation.setShowSoftInputOnFocus(false);
        locationModel = new Gson().fromJson(property.getLocation(), LocationModel.class);
        appDataBase = new AppDataBase(context);
        appDataBase.open();
        loadingDialog = new LoadingDialog(context);
        setData();
    }

    private void setClickListener() {

        binding.ivBack.setOnClickListener(v -> getActivity().onBackPressed());

        binding.fabVisit.setOnClickListener(v -> {
            Uri gmmIntentUri = Uri.parse("geo:" + locationModel.getLat() + "," + locationModel.getLng());
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                startActivity(mapIntent);
            }
        });

        if (property.getUserId() == new MyPreferences(context).getSavedUserId()) {
            binding.ivImage.setOnClickListener(v ->
                    cropImageActivityResultLauncher.launch(CropImage.activity().getIntent(context))
            );

            binding.etLocation.setOnClickListener(v ->
                    locationResultLauncher.launch(new Intent(context, GooglemapActivity.class))
            );

            /*Delete Button  to delete property*/
            binding.ivDelete.setOnClickListener(v -> {
                if (Utils.isNetworkAvailable(context)) {
                    deleteProperty(property.getNumber(), property.getUserId());
                } else {
                    Utils.showLongToast(context, "No Network Connection");
                }
            });

            binding.btnUpdate.setOnClickListener(v -> {

                String name = binding.etName.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    Utils.showErrorAndGiveFocus(context, binding.etName, "Please enter a valid Name");
                    return;
                }

                String type = binding.etType.getText().toString().trim();
                if (TextUtils.isEmpty(type)) {
                    Utils.showErrorAndGiveFocus(context, binding.etType, "Please enter a valid Type");
                    return;
                }

                String leaseType = binding.etType.getText().toString().trim();
                if (TextUtils.isEmpty(leaseType)) {
                    Utils.showErrorAndGiveFocus(context, binding.etLeaseType, "Please enter a valid Lease Type");
                    return;
                }

                String location = binding.etLocation.getText().toString().trim();
                if (TextUtils.isEmpty(location)) {
                    Utils.showErrorAndGiveFocus(context, binding.etLocation, "Please enter a valid Location");
                    binding.scrollView.smoothScrollTo(binding.etLocation.getScrollX(), binding.etLocation.getScrollY());
                    return;
                }

                String bedroom = binding.etBedrooms.getText().toString().trim();
                if (TextUtils.isEmpty(bedroom)) {
                    Utils.showErrorAndGiveFocus(context, binding.etBedrooms, "Please enter valid number of Bedrooms");
                    return;
                }

                String bathroom = binding.etBathrooms.getText().toString().trim();
                if (TextUtils.isEmpty(bathroom)) {
                    Utils.showErrorAndGiveFocus(context, binding.etBathrooms, "Please enter valid number of Bathrooms");
                    return;
                }

                String size = binding.etSize.getText().toString().trim();
                if (TextUtils.isEmpty(size)) {
                    Utils.showErrorAndGiveFocus(context, binding.etSize, "Please enter a valid Size");
                    return;
                }

                String askingPrice = binding.etAskingPrice.getText().toString().trim();
                if (TextUtils.isEmpty(askingPrice)) {
                    Utils.showErrorAndGiveFocus(context, binding.etAskingPrice, "Please enter valid Asking Price");
                    return;
                }

                String localAmenities = binding.etLocalAmenities.getText().toString().trim();
                if (TextUtils.isEmpty(localAmenities)) {
                    localAmenities = "";
                }

                String description = binding.etDescription.getText().toString().trim();
                if (TextUtils.isEmpty(description)) {
                    description = "";
                }

                String floors = binding.etFloors.getText().toString().trim();
                if (TextUtils.isEmpty(floors)) {
                    Utils.showErrorAndGiveFocus(context, binding.etFloors, "Please enter valid number of Floors");
                    return;
                }

                location = new Gson().toJson(locationModel);
                Property property = new Property(this.property.getNumber(), name, type, leaseType, location, Long.parseLong(bedroom),
                        Long.parseLong(bathroom), Long.parseLong(size), Long.parseLong(askingPrice), localAmenities, description,
                        Long.parseLong(floors), this.property.getImage(), new MyPreferences(context).getSavedUserId(),
                        this.property.getCreationDate(), this.property.getImageLink(), this.property.getIsFavourite(),
                        this.property.getIsUploadedToServer());

                if (Utils.isNetworkAvailable(context)) {
                    updatePropertyOnServer(property);
                } else {
                    Utils.showLongToast(context, "No Network Connection");
                }
            });
        } else {
            binding.etName.setShowSoftInputOnFocus(false);
            binding.etName.setEnabled(false);
            binding.etType.setShowSoftInputOnFocus(false);
            binding.etType.setEnabled(false);
            binding.etLeaseType.setShowSoftInputOnFocus(false);
            binding.etLeaseType.setEnabled(false);
            binding.etLocation.setShowSoftInputOnFocus(false);
            binding.etLocation.setEnabled(false);
            binding.etBedrooms.setShowSoftInputOnFocus(false);
            binding.etBedrooms.setEnabled(false);
            binding.etBathrooms.setShowSoftInputOnFocus(false);
            binding.etBathrooms.setEnabled(false);
            binding.etSize.setShowSoftInputOnFocus(false);
            binding.etSize.setEnabled(false);
            binding.etAskingPrice.setShowSoftInputOnFocus(false);
            binding.etAskingPrice.setEnabled(false);
            binding.etLocalAmenities.setShowSoftInputOnFocus(false);
            binding.etLocalAmenities.setEnabled(false);
            binding.etDescription.setShowSoftInputOnFocus(false);
            binding.etDescription.setEnabled(false);
            binding.etFloors.setShowSoftInputOnFocus(false);
            binding.etFloors.setEnabled(false);
            binding.ivDelete.setVisibility(View.GONE);
            binding.btnUpdate.setVisibility(View.GONE);
        }

        /*To view all reports of current property*/
        binding.tvPropertyReport.setOnClickListener(v -> {
            PropertyDetailFragmentDirections.ActionPropertyDetailFragmentToPropertyReportFragment action =
                    PropertyDetailFragmentDirections.actionPropertyDetailFragmentToPropertyReportFragment(property);
            NavHostFragment.findNavController(this).navigate(action);
        });
    }

    @SuppressLint("SetTextI18n")
    private void setData() {
        Glide.with(context).load(NetworkConstants.IMAGE_BASE_URL + property.getImageLink()).into(binding.ivImage);
        binding.etName.setText(property.getName());
        binding.etType.setText(property.getType());
        binding.etLeaseType.setText(property.getLeaseType());
        binding.etLocation.setText(locationModel.getAddress());
        binding.etBedrooms.setText(property.getBedrooms() + "");
        binding.etBathrooms.setText(property.getBathrooms() + "");
        binding.etSize.setText(property.getSize() + "");
        binding.etAskingPrice.setText(property.getAskingPrice() + "");
        binding.etLocalAmenities.setText(property.getLocalAmenities());
        binding.etDescription.setText(property.getDescription());
        binding.etFloors.setText(property.getFloors() + "");
    }

    private void setResultLauncher() {
        cropImageActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        CropImage.ActivityResult cropImageResult = CropImage.getActivityResult(data);
                        imageUri = cropImageResult.getUri();
                        Glide.with(context).load(imageUri).into(binding.ivImage);
                    }
                });

        locationResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getData() != null) {
                        locationModel = (LocationModel) result.getData().getSerializableExtra(Constants.LOCATION_DATA);
                        binding.etLocation.setText(locationModel.getAddress());
                    }
                });
    }

    private void updatePropertyOnServer(Property property) {
        Log.d(TAG, "updatePropertyOnServer: " + property.getNumber());
        loadingDialog.show("Update Property in progress");
        try {

            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

            MultipartBody.Part name = MultipartBody.Part.createFormData("name", property.getName());
            MultipartBody.Part type = MultipartBody.Part.createFormData("type", property.getType());
            MultipartBody.Part leastype = MultipartBody.Part.createFormData("leastype", property.getLeaseType());
            MultipartBody.Part location = MultipartBody.Part.createFormData("location", property.getLocation());
            MultipartBody.Part bedrooms = MultipartBody.Part.createFormData("bedrooms", String.valueOf(property.getBedrooms()));
            MultipartBody.Part bathrooms = MultipartBody.Part.createFormData("bathrooms", String.valueOf(property.getBathrooms()));
            MultipartBody.Part size = MultipartBody.Part.createFormData("size", String.valueOf(property.getSize()));
            MultipartBody.Part asking_price = MultipartBody.Part.createFormData("asking_price", String.valueOf(property.getAskingPrice()));
            MultipartBody.Part local_amenities = MultipartBody.Part.createFormData("local_amenities", property.getLocalAmenities());
            MultipartBody.Part description = MultipartBody.Part.createFormData("description", property.getDescription());
            MultipartBody.Part floors = MultipartBody.Part.createFormData("floors", String.valueOf(property.getFloors()));
            MultipartBody.Part property_id = MultipartBody.Part.createFormData("property_id", String.valueOf(property.getNumber()));

            MultipartBody.Part body;

            /*If image is selected then image will be added, otherwise a separate API will be called which
             * does not have image param*/
            if (imageUri != null) {
                File file = new File(Utils.getPath(imageUri, getActivity()));
                RequestBody requestFile = RequestBody.create(file, MediaType.parse("multipart/form-data"));
                body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

                Call<PropertyResponse> apiCall = apiInterface.updateProperty(
                        name, type, leastype, location, bedrooms, bathrooms, size, asking_price, local_amenities,
                        description, floors, property_id, body
                );
                /*API is called here*/
                callApi(apiCall);
            } else {
                Call<PropertyResponse> apiCall = apiInterface.updatePropertyNoImage(
                        name, type, leastype, location, bedrooms, bathrooms, size, asking_price, local_amenities,
                        description, floors, property_id
                );
                /*API is called here*/
                callApi(apiCall);
            }
        } catch (Exception e) {
            loadingDialog.dismiss();
            Utils.showShortToast(context, "API Exception\n" + e.getMessage());
            Log.d(TAG, "Exception -> " + e.getMessage());
        }
    }

    private void callApi(Call<PropertyResponse> apiCall) {
        apiCall.enqueue(new Callback<PropertyResponse>() {
            @Override
            public void onResponse(Call<PropertyResponse> call, Response<PropertyResponse> response) {
                Log.d(TAG, "onResponse -> " + response.code());
                if (response.isSuccessful() && response.code() == 200) {
                    Log.d(TAG, "onResponse -> Successful");
                    PropertyResponse apiResponse = response.body();
                    if (apiResponse.getData().size() > 0) {
                        List<PropertyResponse.Data> data = apiResponse.getData();

                        /*API returns all properties but I'll get most recent as this is the newly added property*/
                        PropertyResponse.Data model = data.get(data.size() - 1);
                        Property newProperty = new Property(
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
                                property.getImage(),
                                Integer.parseInt(model.getUser_id()),
                                Long.parseLong(model.getCreation_date()),
                                model.getImage(),
                                property.getIsFavourite(),
                                1
                        );
                        Log.d(TAG, "onResponse -> Updated Property Id -> " + newProperty.getNumber());

                        /*Update this new property in SQLite*/
                        appDataBase.updateProperty(newProperty);
                        loadingDialog.dismiss();
                        getActivity().onBackPressed();
                    } else {
                        Log.d(TAG, "onResponse -> Data is null");
                        loadingDialog.dismiss();
                        Utils.showLongToast(context, "Data is null\nFailed To Upload Data on Server");
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
    }

    private void deleteProperty(int propertyId, int userId) {
        loadingDialog.show("Delete Property in progress");
        try {
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<PropertyResponse> apiCall = apiInterface.deleteProperty(
                    propertyId, userId);
            apiCall.enqueue(new Callback<PropertyResponse>() {
                @Override
                public void onResponse(Call<PropertyResponse> call, Response<PropertyResponse> response) {
                    if (response.isSuccessful() && response.code() == 200) {
                        Log.d(TAG, "onResponse -> Successful");
                        /*On success, property will also be deleted from SQLite*/
                        appDataBase.deleteProperty(propertyId);
                        loadingDialog.dismiss();
                        getActivity().onBackPressed();
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