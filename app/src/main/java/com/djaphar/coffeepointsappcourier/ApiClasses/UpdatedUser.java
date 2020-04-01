package com.djaphar.coffeepointsappcourier.ApiClasses;

public class UpdatedUser {
    private boolean isActive, isCurrentlyNotHere;
    private String supervisor;
    private Coordinates coordinates;

    public UpdatedUser(boolean isActive, boolean isCurrentlyNotHere, String supervisor, Coordinates coordinates) {
        this.isActive = isActive;
        this.isCurrentlyNotHere = isCurrentlyNotHere;
        this.supervisor = supervisor;
        this.coordinates = coordinates;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isCurrentlyNotHere() {
        return isCurrentlyNotHere;
    }

    public String getSupervisor() {
        return supervisor;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setCurrentlyNotHere(boolean currentlyNotHere) {
        isCurrentlyNotHere = currentlyNotHere;
    }

    public void setSupervisor(String supervisor) {
        this.supervisor = supervisor;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }
}
