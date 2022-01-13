package com.example.madpropertypal.fragment.property;

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

import com.bumptech.glide.Glide;
import com.example.madpropertypal.activity.GooglemapActivity;
import com.example.madpropertypal.databinding.FragmentPropertyAddBinding;
import com.example.madpropertypal.dialog.LoadingDialog;
import com.example.madpropertypal.dialog.ViewDetailsDialog;
import com.example.madpropertypal.interfaces.ConfirmationClickListener;
import com.example.madpropertypal.model.LocationModel;
import com.example.madpropertypal.model.Property;
import com.example.madpropertypal.model.api_response.PropertyResponse;
import com.example.madpropertypal.network.ApiClient;
import com.example.madpropertypal.network.ApiInterface;
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

public class PropertyAddFragment extends Fragment {

    private static final String TAG = "PropertyAdd";
    public FragmentPropertyAddBinding binding;
    private Context context;
    private Uri imageUri = null;
    /*These are the result launchers to get content from Other activities, these perform same function as
    * onActivityResult(), but this deprecated, soi below is the new method*/
    private ActivityResultLauncher<Intent> locationResultLauncher, cropImageActivityResultLauncher;
    LocationModel locationModel = null;
    private LoadingDialog loadingDialog;
    private AppDataBase appDataBase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPropertyAddBinding.inflate(inflater, container, false);
        init();
        setClickListener();
        /*The results of launchers (2 are used in the app) are get in the below method*/
        setResultLauncher();
        return binding.getRoot();
    }

    private void init() {
        context = getActivity();
        loadingDialog = new LoadingDialog(context);
        /*This line will not allow soft keyboard on the edit text, as it is supposed to open GoogleMapActivity*/
        binding.etLocation.setShowSoftInputOnFocus(false);
        appDataBase = new AppDataBase(context);
        appDataBase.open();
    }

    private void setClickListener() {

        binding.ivBack.setOnClickListener(v ->
                getActivity().onBackPressed()
        );

        /*This will launch CropImage Activity to let user select image and then crop as per need*/
        binding.ivImage.setOnClickListener(v ->
                cropImageActivityResultLauncher.launch(CropImage.activity().getIntent(context))
        );

        /*This will launch Google map*/
        binding.etLocation.setOnClickListener(v ->
                locationResultLauncher.launch(new Intent(context, GooglemapActivity.class))
        );

        binding.btnAdd.setOnClickListener(v -> {
            if (imageUri == null) {
                Utils.showShortToast(context, "Please select Image");
                return;
            }

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

            /*Location model is converted to string*/
            location = new Gson().toJson(locationModel);

            Property property = new Property(0, name, type, leaseType, location, Long.parseLong(bedroom),
                    Long.parseLong(bathroom), Long.parseLong(size), Long.parseLong(askingPrice), localAmenities,
                    description, Long.parseLong(floors), Utils.getByteArrayFromUri(imageUri),
                    new MyPreferences(context).getSavedUserId(), System.currentTimeMillis(),
                    "", 0, 0);

            /*Details are shown for verification as a dialog, dialog has two listeners, for Yes and No*/
            new ViewDetailsDialog(context, property, new ConfirmationClickListener() {
                @Override
                public void noClicked() {

                }

                @Override
                public void yesClicked() {
                    /*If network is available then property will be added to server*/
                    if (Utils.isNetworkAvailable(context)) {
                        uploadPropertyToServer(property);
                    } else {
                        Utils.showShortToast(context, "No Network Connection");
                    }
                }
            });
        });
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
                        binding.etLocation.setError(null);
                    }
                });
    }

    private void uploadPropertyToServer(Property property) {
        loadingDialog.show("Upload Property in progress");
        try {
            File file = new File(Utils.getPath(imageUri, getActivity()));
            RequestBody requestFile = RequestBody.create(file, MediaType.parse("multipart/form-data"));
            MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
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
            MultipartBody.Part user_id = MultipartBody.Part.createFormData("user_id", String.valueOf(property.getUserId()));
            MultipartBody.Part creation_date = MultipartBody.Part.createFormData("creation_date", String.valueOf(property.getCreationDate()));
            MultipartBody.Part is_favourite = MultipartBody.Part.createFormData("is_favourite", String.valueOf(property.getIsFavourite()));

            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<PropertyResponse> apiCall = apiInterface.addProperty(
                    name, type, leastype, location, bedrooms, bathrooms, size, asking_price, local_amenities,
                    description, floors, user_id, creation_date, is_favourite, body
            );
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
                                    Integer.parseInt(model.getIs_favourite()),
                                    1
                            );

                            /*Insert this new property in SQLite*/
                            appDataBase.insertProperty(newProperty);
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
        } catch (Exception e) {
            loadingDialog.dismiss();
            Utils.showShortToast(context, "API Exception\n" + e.getMessage());
            Log.d(TAG, "Exception -> " + e.getMessage());
        }
    }
}