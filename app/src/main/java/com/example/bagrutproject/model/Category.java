package com.example.bagrutproject.model;

public class Category {
    private String name;
    private static String category="";
    public Category(String name){
        if (name.toString().equals(category.toString())){
            this.name=name;
        }

    }
}
