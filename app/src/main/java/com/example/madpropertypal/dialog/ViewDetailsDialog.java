package com.example.madpropertypal.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.madpropertypal.R;
import com.example.madpropertypal.databinding.DialogViewDetailsBinding;
import com.example.madpropertypal.interfaces.ConfirmationClickListener;
import com.example.madpropertypal.model.LocationModel;
import com.example.madpropertypal.model.Property;
import com.google.gson.Gson;

public class ViewDetailsDialog {

    private Context context;
    /*This interface has two methods which are called when user press yes or no,
    * and send a callback to fragment, where user I can determine which action
    * to be performed*/
    public ConfirmationClickListener listener;
    public DialogViewDetailsBinding binding;
    public Dialog dialog;
    public Property property;

    public ViewDetailsDialog(Context context, Property property, ConfirmationClickListener listener) {
        this.context = context;
        this.property = property;
        this.listener = listener;
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_view_details, null);
        binding = DialogViewDetailsBinding.bind(view);
        dialog = new Dialog(context);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        binding.fabNo.setOnClickListener(v -> {
            dialog.dismiss();
            listener.noClicked();
        });
        binding.fabYes.setOnClickListener(v -> {
            dialog.dismiss();
            listener.yesClicked();
        });

        setData();

        dialog.show();
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }

    private void setData() {
        LocationModel locationModel = new Gson().fromJson(property.getLocation(), LocationModel.class);
        binding.ivImage.setImageBitmap(BitmapFactory.decodeByteArray(property.getImage(), 0, property.getImage().length));
        binding.tvName.setText(property.getName());
        binding.tvType.setText(property.getType());
        binding.tvLeaseType.setText(property.getLeaseType());
        binding.tvLocation.setText(locationModel.getAddress());
        binding.tvBedrooms.setText(property.getBedrooms() + "");
        binding.tvBathrooms.setText(property.getBathrooms() + "");
        binding.tvSize.setText(property.getSize() + "");
        binding.tvAskingPrice.setText(property.getAskingPrice() + "");
        binding.tvLocalAmenities.setText(property.getLocalAmenities());
        binding.tvDescription.setText(property.getDescription());
        binding.tvFloors.setText(property.getFloors() + "");
    }
}
