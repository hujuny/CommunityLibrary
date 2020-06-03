package com.yhj.communitylibrary.manage;

import java.util.Date;

import cn.bmob.v3.BmobObject;

/**
 * author : yhj
 * date   : 2020/4/27
 * desc   :图书信息
 */
public class Book extends BmobObject {

    private String title;//书名
    private String author;//作者
    private String isbn;//ISBN
    private String publisher;//出版社
    private int pages;//页数
    private double price;//价格
    private Date publication_date;//出版日期
    private String category ;//类别
    private String introduction;//简介
    private String book_cover;//书刊封面
    private String book_admin;//图书管理者


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getPublication_date() {
        return publication_date;
    }

    public void setPublication_date(Date publication_date) {
        this.publication_date = publication_date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getBook_cover() {
        return book_cover;
    }

    public void setBook_cover(String book_cover) {
        this.book_cover = book_cover;
    }

    public String getBook_admin() {
        return book_admin;
    }

    public void setBook_admin(String book_admin) {
        this.book_admin = book_admin;
    }


}
