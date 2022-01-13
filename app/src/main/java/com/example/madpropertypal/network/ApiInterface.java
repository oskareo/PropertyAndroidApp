
package com.example.madpropertypal.network;

import com.example.madpropertypal.model.api_response.PropertyResponse;
import com.example.madpropertypal.model.api_response.ReportResponse;
import com.example.madpropertypal.model.api_response.UserResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {

    @FormUrlEncoded
    @POST(NetworkConstants.REGISTER_USER)
    Call<UserResponse> registerUser(
            @Field("name") String name,
            @Field("email") String email,
            @Field("password") String password,
            @Field("created_date") Long creationDate
    );

    @FormUrlEncoded
    @POST(NetworkConstants.LOGIN_USER)
    Call<UserResponse> loginUser(
            @Field("email") String email,
            @Field("password") String password
    );

    @Multipart
    @POST(NetworkConstants.ADD_PROPERTY)
    Call<PropertyResponse> addProperty(
            @Part MultipartBody.Part name,
            @Part MultipartBody.Part type,
            @Part MultipartBody.Part leastype,
            @Part MultipartBody.Part location,
            @Part MultipartBody.Part bedrooms,
            @Part MultipartBody.Part bathrooms,
            @Part MultipartBody.Part size,
            @Part MultipartBody.Part asking_price,
            @Part MultipartBody.Part local_amenities,
            @Part MultipartBody.Part description,
            @Part MultipartBody.Part floors,
            @Part MultipartBody.Part user_id,
            @Part MultipartBody.Part creation_date,
            @Part MultipartBody.Part is_favourite,
            @Part MultipartBody.Part image
    );

    @Multipart
    @POST(NetworkConstants.UPDATE_PROPERTY)
    Call<PropertyResponse> updateProperty(
            @Part MultipartBody.Part name,
            @Part MultipartBody.Part type,
            @Part MultipartBody.Part leastype,
            @Part MultipartBody.Part location,
            @Part MultipartBody.Part bedrooms,
            @Part MultipartBody.Part bathrooms,
            @Part MultipartBody.Part size,
            @Part MultipartBody.Part asking_price,
            @Part MultipartBody.Part local_amenities,
            @Part MultipartBody.Part description,
            @Part MultipartBody.Part floors,
            @Part MultipartBody.Part property_id,
            @Part MultipartBody.Part image
    );

    @Multipart
    @POST(NetworkConstants.UPDATE_PROPERTY_NO_IMAGE)
    Call<PropertyResponse> updatePropertyNoImage(
            @Part MultipartBody.Part name,
            @Part MultipartBody.Part type,
            @Part MultipartBody.Part leastype,
            @Part MultipartBody.Part location,
            @Part MultipartBody.Part bedrooms,
            @Part MultipartBody.Part bathrooms,
            @Part MultipartBody.Part size,
            @Part MultipartBody.Part asking_price,
            @Part MultipartBody.Part local_amenities,
            @Part MultipartBody.Part description,
            @Part MultipartBody.Part floors,
            @Part MultipartBody.Part property_id
    );

    @FormUrlEncoded
    @POST(NetworkConstants.ADD_REPORT)
    Call<ReportResponse> addReport(
            @Field("propertyId") int propertyId,
            @Field("dateOfViewing") long dateOfViewing,
            @Field("interest") String interest,
            @Field("offerPrice") long offerPrice,
            @Field("offerExpiryDate") long offerExpiryDate,
            @Field("conditionsOfOffer") String conditionsOfOffer,
            @Field("viewingComments") String viewingComments
    );

    @FormUrlEncoded
    @POST(NetworkConstants.DELETE_PROPERTY)
    Call<PropertyResponse> deleteProperty(
            @Field("property_id") int property_id,
            @Field("user_id") int user_id
    );

    @FormUrlEncoded
    @POST(NetworkConstants.GET_ALL_REPORTS)
    Call<ReportResponse> getAllReports(
            @Field("propertyId") int propertyId
    );

    @FormUrlEncoded
    @POST(NetworkConstants.GET_ALL_PROPERTY)
    Call<PropertyResponse> getAllProperty(
            @Field("user_id") int user_id
    );
}