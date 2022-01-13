package com.example.madpropertypal.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.madpropertypal.R;
import com.example.madpropertypal.databinding.ActivityMainBinding;
import com.example.madpropertypal.util.MyPreferences;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /*Here Bottom nav is associated with navgraph of MainActivity */
        NavController navController = Navigation.findNavController(this, R.id.mainNavHostFragment);
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*Here I am using a variable which is saved in saved preferences
        * The purpose of this key-value is that, when App is launched its value will be
        * true and if the value is true then API will be called to get all properties
        * And right after calling API I will set its value to false so as long as the app
        * will be running, it will not call API to get Properties. but from the SQLite
        * Don't worry, newly added property will be auto fetch from SQLIte as it will be added
        * when Property is added in the Online DB*/
        new MyPreferences(MainActivity.this).setFirstTime(true);
    }
}