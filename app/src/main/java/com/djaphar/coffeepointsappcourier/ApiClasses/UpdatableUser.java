package com.djaphar.coffeepointsappcourier.ApiClasses;

import java.util.ArrayList;

public class UpdatableUser {

    private Boolean isActive, isAway;
    private String supervisor, hint;
    private ArrayList<Double> coordinates;
    private ArrayList<Product> productList;

    public UpdatableUser(Boolean isActive, Boolean isAway, String supervisor, String hint, ArrayList<Double> coordinates, ArrayList<Product> productList) {
        this.isActive = isActive;
        this.isAway = isAway;
        this.supervisor = supervisor;
        this.hint = hint;
        this.coordinates = coordinates;
        this.productList = productList;
    }

    public Boolean isActive() {
        return isActive;
    }

    public Boolean isAway() {
        return isAway;
    }

    public String getSupervisor() {
        return supervisor;
    }

    public String getHint() {
        return hint;
    }

    public ArrayList<Double> getCoordinates() {
        return coordinates;
    }

    public ArrayList<Product> getProductList() {
        return productList;
    }

    public void setActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public void setAway(Boolean isAway) {
        this.isAway = isAway;
    }

    public void setSupervisor(String supervisor) {
        this.supervisor = supervisor;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public void setCoordinates(ArrayList<Double> coordinates) {
        this.coordinates = coordinates;
    }

    public void setProductList(ArrayList<Product> productList) {
        this.productList = productList;
    }
}
