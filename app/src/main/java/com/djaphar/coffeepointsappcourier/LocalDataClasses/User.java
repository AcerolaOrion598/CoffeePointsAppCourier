package com.djaphar.coffeepointsappcourier.LocalDataClasses;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_table")
public class User {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "_id")
    private String _id;

    @NonNull
    @ColumnInfo(name = "token")
    private String token;

    @NonNull
    @ColumnInfo(name = "role")
    private String role;

    @NonNull
    @ColumnInfo(name = "phone_number")
    private String phoneNumber;

    @ColumnInfo(name = "supervisor")
    private String supervisor;

    @ColumnInfo(name = "user_hash")
    private Integer userHash;

    public User(@NonNull String _id, @NonNull String token, @NonNull String role, @NonNull String phoneNumber, String supervisor) {
        this._id = _id;
        this.token = token;
        this.role = role;
        this.phoneNumber = phoneNumber;
        this.supervisor = supervisor;
    }

    @NonNull
    public String get_id() {
        return _id;
    }

    public void set_id(@NonNull String _id) {
        this._id = _id;
    }

    @NonNull
    public String getToken() {
        return token;
    }

    public void setToken(@NonNull String token) {
        this.token = token;
    }

    @NonNull
    public String getRole() {
        return role;
    }

    public void setRole(@NonNull String role) {
        this.role = role;
    }

    @NonNull
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(@NonNull String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(String supervisor) {
        this.supervisor = supervisor;
    }

    public Integer getUserHash() {
        return userHash;
    }

    public void setUserHash(Integer userHash) {
        this.userHash = userHash;
    }

    public Integer determineHash() {
        String data = get_id() + getPhoneNumber() + getRole() + getToken();
        if (getSupervisor() != null) {
            data += getSupervisor();
        }
        return data.hashCode();
    }
}
