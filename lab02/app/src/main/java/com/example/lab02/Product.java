package com.example.lab02;

import java.io.Serializable;
import java.util.Date;

public class Product implements  Serializable{
    private String name;
    private Date receiptDate;
    private int quantity;
    private float price;

    public Product(String name, Date receiptDate, int quantity, float price){
        this.name = name;
        this.receiptDate = receiptDate;
        this.quantity = quantity;
        this.price = price;
    }

    public Product(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(Date receiptDate) {
        this.receiptDate = receiptDate;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

}
