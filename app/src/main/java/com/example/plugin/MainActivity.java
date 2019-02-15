package com.example.plugin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.module.Animal;
import com.example.plugin.manager.PluginManager;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PluginManager.init();

        IDemo service = PluginManager.getService(IDemo.class);

        service.demo();

        Animal animal = PluginManager.getService(Animal.class);

        animal.eat();

    }
}
