package com.example.madpropertypal.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.madpropertypal.databinding.ActivityAuthBinding;

public class AuthActivity extends AppCompatActivity {

    /*This variable is for viewbinding, I am using this in the whole project
    * to access all ids of the current layout by just typing variable name
    * with .(dot) like: binding.etPassword */
    private ActivityAuthBinding binding;

    /*Also I am using NavGraph to launch fragments in the whole app
    * This AuthActivity also has a navgraph associated with it which can
    * be view in the layout file of this, Layout of each Activity or Fragment
    * can be seen by CTRL+LeftClick on Type of binding variable e.g. ActivityAuthBinding*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}