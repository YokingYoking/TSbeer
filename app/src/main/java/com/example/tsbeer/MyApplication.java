package com.example.tsbeer;

import android.app.Application;

public class MyApplication extends Application {

    private String username;

    @Override
    public void onCreate() {
        super.onCreate();
        setName(NAME); //初始化全局变量
    }

    public String getName() {
        return username;
    }

    public void setName(String name) {
        this.username = name;
    }

    private static final String NAME = "";
}
