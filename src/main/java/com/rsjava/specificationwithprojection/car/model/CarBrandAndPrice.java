package com.rsjava.specificationwithprojection.car.model;

public interface CarBrandAndPrice {

    String getBrand();

    String getPrice();

    default String getBrandAndPrice() {
        return getBrand().concat(" ").concat(getPrice());
    }
}
