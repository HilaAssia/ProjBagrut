package com.example.bagrutproject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Product {
    String name;
    String price;
    String category;
    String details;

    public Product() {
        
    }
    public Product(String name, String price) {
        
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
