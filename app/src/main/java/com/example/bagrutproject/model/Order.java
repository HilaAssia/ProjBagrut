package com.example.bagrutproject.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Order {
    private ArrayList<Product> products;
    private String uid;
    private String email;
    private String totalPrice;
    private String timestamp;
    public Order(){}
    public Order(ArrayList<Product> products, String uid, String totalPrice, String email){
        this.products=products;
        this.uid=uid;
        this.email=email;
        this.totalPrice=totalPrice;
        this.timestamp = new SimpleDateFormat("dd/MM/yy  HH:mm").format(new Date());
    }

    public ArrayList<String> getProductsIDs(ArrayList<String> ids) {
        for (Product p:this.products){
            ids.add(p.getId());
        }
        return ids;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}
