package com.example.bagrutproject.model;

public class Order {
    Product[] products;
    String totalPrice;
    public Order(){}
    public Order(Product[] products){
        this.products=products;
    }
    public Order(Product[] products, String totalPrice){
        this.products=products;
        this.totalPrice=totalPrice;
    }

    public Product[] getProducts() {
        return products;
    }

    public void setProducts(Product[] products) {
        this.products = products;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }
}
