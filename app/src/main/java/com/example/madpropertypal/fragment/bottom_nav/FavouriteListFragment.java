package com.example.madpropertypal.fragment.bottom_nav;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.madpropertypal.R;
import com.example.madpropertypal.adapter.PropertyAdapter;
import com.example.madpropertypal.databinding.FragmentFavouriteListBinding;
import com.example.madpropertypal.util.AppDataBase;
import com.example.madpropertypal.util.MyPreferences;

public class FavouriteListFragment extends Fragment {

    public FragmentFavouriteListBinding binding;
    private PropertyAdapter adapter;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFavouriteListBinding.inflate(inflater, container, false);
        init();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        getAndSetData();
        setClickListener();
    }

    private void init() {
        context = getActivity();
    }

    private void setClickListener() {
        binding.fabAddProperty.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_favouriteListFragment_to_propertyAddFragment)
        );
    }

    private void getAndSetData() {
        AppDataBase appDataBase = new AppDataBase(context);
        appDataBase.open();
        /*First I'll check if there are favourite Properties in SQLite*/
        if (appDataBase.getTotalFavouriteProperties(new MyPreferences(context).getSavedUserId()) > 0) {
            /*If Favourite Properties count is > 0, then adapter will be set*/
            /*Adapter has a callback listener which is called when any property is clicked*/
            adapter = new PropertyAdapter(
                    context,
                    appDataBase.getFavouritePropertyList(new MyPreferences(context).getSavedUserId()),
                    model -> {
                        /*model is Property which is clicked and I;ll pass this model to detail Fragment
                         * as argument*/
                        FavouriteListFragmentDirections.ActionFavouriteListFragmentToPropertyDetailFragment action =
                                FavouriteListFragmentDirections.actionFavouriteListFragmentToPropertyDetailFragment(model);
                        NavHostFragment.findNavController(this).navigate(action);
                    });
            binding.rclViewProperty.setAdapter(adapter);
            /*If there is data then searching will be enabled*/
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
}