package com.example.madpropertypal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.madpropertypal.R;
import com.example.madpropertypal.databinding.ItemPropertyBinding;
import com.example.madpropertypal.interfaces.PropertyClickListener;
import com.example.madpropertypal.model.LocationModel;
import com.example.madpropertypal.model.Property;
import com.example.madpropertypal.network.NetworkConstants;
import com.example.madpropertypal.util.AppDataBase;
import com.example.madpropertypal.util.Utils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;


public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.ViewHolder> implements Filterable {

    public Context context;
    public List<Property> data, filteredData;
    /*This Listener will be called when any property is clicked and send a callback to PropertyListFragment*/
    public PropertyClickListener listener;

    public PropertyAdapter(Context context, List<Property> data, PropertyClickListener listener) {
        this.context = context;
        this.data = data;
        filteredData = data;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemPropertyBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PropertyAdapter.ViewHolder holder, int position) {
        holder.bind(filteredData.get(position));
    }

    /*To avoid crash when user types something in the search bar and data gets null,
    * I am using Ternary operator which checks if filtered data is null, if it is null
    * then it returns 0 otherwise filteredData.size will be returned*/
    @Override
    public int getItemCount() {
        return filteredData == null ? 0 : filteredData.size();
    }

    /*This code is for Search view*/
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String query = charSequence.toString().toLowerCase();
                if (query.isEmpty()) {
                    filteredData = data;
                } else {
                    List<Property> filteredList = new ArrayList<>();
                    for (Property model : data) {
                       /*I am storing location model as string and when it is needed in the code
                       * I parse it to LocationModel*/
                        LocationModel locationModel = new Gson().fromJson(model.getLocation(), LocationModel.class);
                        /*Here the filter will be applied as per 5 different fields*/
                        if (
                                model.getName().toLowerCase().contains(query) ||
                                        locationModel.getAddress().toLowerCase().contains(query) ||
                                        model.getType().toLowerCase().contains(query) ||
                                        String.valueOf(model.getNumber()).equals(query) ||
                                        String.valueOf(model.getBedrooms()).equals(query)
                        ) {
                            filteredList.add(model);
                        }
                    }
                    filteredData = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredData;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredData = (List<Property>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ItemPropertyBinding binding;

        public ViewHolder(ItemPropertyBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Property model) {
            binding.tvName.setText(model.getName());
            binding.tvPrice.setText(model.getAskingPrice() + "");

            Glide.with(context).load(NetworkConstants.IMAGE_BASE_URL + model.getImageLink()).into(binding.ivPicture);

            /*Here Favourite icon is set as per the property is favourite or not*/
            if (model.getIsFavourite() == 1) {
                binding.fabFavourite.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart_filled));
            } else {
                binding.fabFavourite.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart_unfilled));
            }

            /*Here click listener of Favourite icon is handled*/
            binding.fabFavourite.setOnClickListener(v -> {
                if (model.getIsFavourite() == 1) {
                    binding.fabFavourite.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart_unfilled));
                    model.setIsFavourite(0);
                } else {
                    binding.fabFavourite.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart_filled));
                    model.setIsFavourite(1);
                }
                /*Above if-else modifies the model and we update the model in SQLite*/
                updateInLocalDb(model);
            });

            /*Here the global listener is called to send callback to fragment*/
            binding.getRoot().setOnClickListener(v -> {
                listener.propertyClicked(model);
            });
        }
    }

    private void updateInLocalDb(Property property) {
        AppDataBase appDatabase = new AppDataBase(context);
        appDatabase.open();
        long updatedId = appDatabase.updateProperty(property);
        if (updatedId > 0) {
            Utils.showShortToast(context, "Updated");
        } else {
            Utils.showShortToast(context, "Failed tp Update");
        }
        appDatabase.close();
    }
}
