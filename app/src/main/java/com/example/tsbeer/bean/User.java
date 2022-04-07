package com.example.tsbeer.bean;

import java.io.Serializable;

/**
 * 用户
 */
public class User implements Serializable {
    private Integer id;//用户ID
    private String account;//账号
    private String password;//密码
    private String name;//昵称
    private String sex;//性别
    private String phone;//手机号
    private String address;//地址
    private String photo;//头像
    private String identityCard;//身份证号

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getIdentityCard() {
        return identityCard;
    }

    public void setIdentityCard(String identityCard) {
        this.identityCard = identityCard;
    }

    public User(Integer id, String account, String password, String name, String sex, String phone, String address, String photo, String identityCard) {
        this.id = id;
        this.account = account;
        this.password = password;
        this.name = name;
        this.sex = sex;
        this.phone = phone;
        this.address = address;
        this.photo = photo;
        this.identityCard = identityCard;
    }
}
