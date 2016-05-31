package com.example.chenzhe.eyerhyme.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Jay on 2016/5/14.
 */
public class MovieProductItem implements Serializable {
    private String movieName;
    private String theaterName;
    private String theaterHall;
    private String date;
    private String phone;
    private int[] seats;
    private int price;
    private int amount;
    private int discount;
    private int serviceFee;
    private int subtotal;
    public MovieProductItem(String t_movieName, String t_theaterName, String t_theaterHall,
                     String t_date, String t_phone, int[] t_seats, int t_price,
                     int t_amount, int t_discount, int t_serviceFee) {
        movieName = t_movieName;
        theaterName = t_theaterName;
        theaterHall = t_theaterHall;
        date = t_date;
        phone = t_phone;
        price = t_price;
        amount = t_amount;
        discount = t_discount;
        serviceFee = t_serviceFee;
        subtotal = price * amount + serviceFee * amount;
        seats = new int[amount];
        for (int i = 0; i < amount; i++)
            seats[i] = t_seats[i];
    }

    public String getMovieName() {
        return movieName;
    }

    public String getTheaterName() {
        return theaterName;
    }

    public String getTheaterHall() {
        return theaterHall;
    }

    public String getDate() {
        return date;
    }

    public String getPhone() {
        return phone;
    }

    public int[] getSeats() {
        return seats;
    }

    public int getPrice() {
        return price;
    }

    public int getAmount() {
        return amount;
    }

    public int getDiscount() {
        return discount;
    }

    public int getServiceFee() {
        return serviceFee;
    }

    public int getSubtotal() {
        return subtotal;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setServiceFee(int serviceFee) {
        this.serviceFee = serviceFee;
    }

    public void setSubtotal(int subtotal) {
        this.subtotal = subtotal;
    }

    public void setTheaterHall(String theaterHall) {
        this.theaterHall = theaterHall;
    }

    public void setTheaterName(String theaterName) {
        this.theaterName = theaterName;
    }
}
