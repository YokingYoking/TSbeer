package com.example.tsbeer;

import android.app.Application;

public class MyApplication extends Application {

    private String username;
    private String nickname;

    @Override
    public void onCreate() {
        super.onCreate();
        setName(NAME); //初始化全局变量
        setNickname(NICKNAME); //初始化全局变量
    }

    public String getName() {
        return username;
    }

    public void setName(String name) {
        this.username = name;
    }

    private static final String NAME = "";

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String name) {
        this.nickname = name;
    }

    private static final String NICKNAME = "";
}
