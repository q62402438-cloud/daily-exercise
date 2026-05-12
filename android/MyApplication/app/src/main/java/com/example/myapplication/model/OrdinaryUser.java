package com.example.myapplication.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OrdinaryUser extends User implements Serializable {

    private static final long serialVersionUID = 1L;

    @SerializedName("userMailbox")
    private String userMailbox;

    @SerializedName("gender")
    private String gender;

    @SerializedName("birthday")
    private String birthday;

    @SerializedName("age")
    private Integer age;

    @SerializedName("weight")
    private Float weight;

    /* ===== 关键：构造函数 ===== */

    public OrdinaryUser() {
        super();
    }

    // ✅ 从 User 构造 OrdinaryUser
    public OrdinaryUser(User user) {
        super();
        if (user != null) {
            this.setUserID(user.getUserID());
            this.setUserName(user.getUserName());
            this.setPhoneNumber(user.getPhoneNumber());
            this.setUserPassword(user.getUserPassword());
            this.setUserType(user.getUserType());
        }
    }

    /* ===== getter / setter ===== */

    public String getUserMailbox() {
        return userMailbox;
    }
    public void setUserMailbox(String userMailbox) {
        this.userMailbox = userMailbox;
    }

    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public Integer getAge() {
        return age;
    }
    public void setAge(Integer age) {
        this.age = age;
    }

    public Float getWeight() {
        return weight;
    }
    public void setWeight(Float weight) {
        this.weight = weight;
    }
}
