package com.example.bagrutproject.model;

import java.io.Serializable;
import java.util.UUID;

public class Product implements Serializable {

    private String image;
    private String name;
    private String price;
    private String category;
    private String details;
    private String id;
    private int quantity;
    private boolean forSale;

    public Product() {
        
    }
    public Product(Product p) {
        this.image=p.getImage();
        this.name=p.getName();
        this.price=p.getPrice();
        this.category=p.getCategory();
        this.details=p.getDetails();
        this.quantity=p.getQuantity();
        this.forSale= p.forSale;
        this.id=p.getId();
    }
    public Product(String name, String price, String details, int quantity, boolean forSale) {
        this.name=name;
        this.price=price;
        this.details=details;
        this.quantity=quantity;
        this.forSale=forSale;
        UUID randomUUID = UUID.randomUUID();
        this.id=randomUUID.toString();
    }
    public Product(String image, String name, String price, String details, int quantity, boolean forSale, String category) {// כדי להוסיף קטגוריה
        this.image=image;
        this.name=name;
        this.price=price;
        this.category=category;
        this.details=details;
        this.quantity=quantity;
        this.forSale=forSale;
        UUID randomUUID = UUID.randomUUID();
        this.id=randomUUID.toString();
    }

    public String getImage() { return image;}

    public void setImage(String image) {
        this.image = image;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void quantityAdd(int n) {
        this.quantity += n;
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

    public Boolean getForSale() {
        return forSale;
    }

    public void setForSale(boolean forSale) {
        this.forSale = forSale;
    }


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void delete(){this.setQuantity(0);}
}
