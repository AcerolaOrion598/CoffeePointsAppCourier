package com.djaphar.coffeepointsappcourier.ApiClasses;

import java.util.ArrayList;

public class UpdatableUser {
    private Boolean isActive, isCurrentlyNotHere;
    private String supervisor, hint;
    private Coordinates coordinates;
    private ArrayList<Product> productList;

    public UpdatableUser(Boolean isActive, Boolean isCurrentlyNotHere, String supervisor, String hint, Coordinates coordinates, ArrayList<Product> productList) {
        this.isActive = isActive;
        this.isCurrentlyNotHere = isCurrentlyNotHere;
        this.supervisor = supervisor;
        this.hint = hint;
        this.coordinates = coordinates;
        this.productList = productList;
    }

    public Boolean isActive() {
        return isActive;
    }

    public Boolean isCurrentlyNotHere() {
        return isCurrentlyNotHere;
    }

    public String getSupervisor() {
        return supervisor;
    }

    public String getHint() {
        return hint;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public ArrayList<Product> getProductList() {
        return productList;
    }

    public void setActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public void setCurrentlyNotHere(Boolean isCurrentlyNotHere) {
        this.isCurrentlyNotHere = isCurrentlyNotHere;
    }

    public void setSupervisor(String supervisor) {
        this.supervisor = supervisor;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public void setProductList(ArrayList<Product> productList) {
        this.productList = productList;
    }
}
