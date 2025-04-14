package com.example.bagrutproject.model;

import java.util.UUID;

public class Product {

    String image;
    String name;
    String price;
    String category;
    String details;
    String id;
    int quantity;
    boolean forSale;

    public Product() {
        
    }
    public Product(String name, String price, String details, int quantity, boolean forSale) {
        this.name=name;
        this.price=price;
        //this.category=category;
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
    public String toString(){
        return "imageaddress: "+image+"/nname: "+name+"/n price: "+price+"/n details: "+details+"/n quantity: "+quantity+"/n category:"+category+"/n id:"+id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
