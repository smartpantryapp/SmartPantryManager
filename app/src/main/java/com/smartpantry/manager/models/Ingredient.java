package com.smartpantry.manager.models;

public class Ingredient {

    private int id;
    private String name;
    private double quantity;
    private String unit;
    private String expiryDate;
    private long createdAt;

    public Ingredient(
            int id,
            String name,
            double quantity,
            String unit,
            String expiryDate,
            long createdAt) {

        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
        this.createdAt = createdAt;
    }

    public Ingredient(
            String name,
            double quantity,
            String unit,
            String expiryDate) {

        this(0, name, quantity, unit, expiryDate,
                System.currentTimeMillis());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}