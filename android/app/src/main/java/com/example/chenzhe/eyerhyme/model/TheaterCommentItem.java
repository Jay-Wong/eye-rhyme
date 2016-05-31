package com.example.chenzhe.eyerhyme.model;

import java.io.Serializable;

/**
 * Created by Jay on 2016/5/6.
 */
public class TheaterCommentItem implements Serializable{
//    “name”: string, 评分用户的用户名
//    “grade”: integer, 评分的分数
//    “content”: string, 评论的内容
//    “date_time”: string, 评论时间
    private String name;
    private int grade;
    private String content;
    private String date_time;
    private int user_id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}
