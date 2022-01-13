package com.example.madpropertypal.dialog;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.madpropertypal.R;
import com.example.madpropertypal.databinding.DialogLoadingBinding;

public class LoadingDialog {
    public DialogLoadingBinding binding;
    private final Dialog dialog;

    public LoadingDialog(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
        binding = DialogLoadingBinding.bind(view);
        dialog = new Dialog(context);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(binding.getRoot());
        setCancelable(false);
    }

    public void show() {
        dialog.show();
    }

    public void show(String description) {
        dialog.show();
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        binding.tvDescription.setText(description);
    }

    public void dismiss() {
        dialog.dismiss();
    }

    public void setCancelable(boolean flag) {
        dialog.setCancelable(flag);
        dialog.setCanceledOnTouchOutside(flag);
    }

    public void setDescription(String description) {
        binding.tvDescription.setText(description);
    }
}
