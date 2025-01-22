package com.example.bagrutproject;

import android.widget.ImageView;

public class Product {

    //ImageView image;
    String name;
    String price;
    //String category;
    String details;
    boolean isForSale;

    public Product() {
        
    }
    public Product(String name, String price, String details) {
        this.name=name;
        this.price=price;
        //this.category=category;
        this.details=details;
        this.isForSale=false;
    }
    public Product(ImageView image, String name, String price, String category, String details) {
        //this.image=image;
        this.name=name;
        this.price=price;
        //this.category=category;
        this.details=details;
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

    public boolean isForSale() {
        return isForSale;
    }

    public void setForSale(boolean isChecked) {
        this.isForSale = isChecked;
    }


    //public String getCategory() {
        //return category;
    //}

    //public void setCategory(String category) {
        //this.category = category;
    //}
}
