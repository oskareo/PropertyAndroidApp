package com.example.madpropertypal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.madpropertypal.R;
import com.example.madpropertypal.databinding.ItemReportBinding;
import com.example.madpropertypal.model.Report;
import com.example.madpropertypal.util.Utils;

import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {

    public Context context;
    public List<Report> data;

    public ReportAdapter(Context context, List<Report> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemReportBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReportAdapter.ViewHolder holder, int position) {
        holder.bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ItemReportBinding binding;

        public ViewHolder(ItemReportBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Report model) {
            /*I am saving date in the form of milliseconds and here it is formatted into a specific format
            * Format can be changed in Utils method*/
            binding.tvDateOfViewing.setText(context.getString(R.string.date_of_viewing) + ": " + Utils.getFormattedDate(model.getDateOfViewing()));
            binding.tvInterest.setText(context.getString(R.string.interest)+ ": "+model.getInterest());
            binding.tvOfferPrice.setText(context.getString(R.string.offer_price)+ ": "+model.getOfferPrice());
            binding.tvOfferExpiryDate.setText(context.getString(R.string.offer_expiry_date)+ ": "+Utils.getFormattedDate(model.getOfferExpiryDate()));
            binding.tvConditionsOfOffer.setText(context.getString(R.string.conditions_of_offer)+ ": "+model.getConditionsOfOffer());
            binding.tvViewingComments.setText(context.getString(R.string.viewing_comments)+ ": "+model.getViewingComments());
        }
    }
}
