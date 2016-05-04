package com.example.chenzhe.eyerhyme.model;

import java.io.Serializable;

/**
 * Created by Jay on 2016/5/3.
 * 电影院的基本信息：名字、地点、最低价格、评分、距离
 */
public class TheaterItem implements Serializable {
//    “theater_id”: integer, 该影院的标识
//    “name”: string, 影院的名称
//    “location”: string, 影院的文字地址
//    “lowest_price”: integer, 影院的最低价
//    “grade”: float, 影院的平均评分
//    “longitude”: float, 该影院的经度
//    “latitude”: float, 该影院的纬度
    private int theater_id;
    private String name;
    private String location;
    private int lowest_price;
    private float grade;
    private double longitude;
    private double latitude;
    public TheaterItem (int t_id, String t_name, String t_address, int t_lowestPrice,
                 float t_score, double t_longitude, double t_latitude) {
        this.theater_id = t_id;
        this.name = t_name;
        this.location = t_address;
        this.lowest_price = t_lowestPrice;
        this.grade = t_score;
        this.longitude = t_longitude;
        this.latitude = t_latitude;
    }

    public int getTheater_id() {
        return theater_id;
    }

    public void setTheater_id(int theater_id) {
        this.theater_id = theater_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getLowest_price() {
        return lowest_price;
    }

    public void setLowest_price(int lowest_price) {
        this.lowest_price = lowest_price;
    }

    public float getGrade() {
        return grade;
    }

    public void setGrade(float grade) {
        this.grade = grade;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }
}
