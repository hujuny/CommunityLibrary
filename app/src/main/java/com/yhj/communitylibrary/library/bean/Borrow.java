package com.yhj.communitylibrary.library.bean;


import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

/**
 * author : yhj
 * date   : 2020/4/28
 * desc   :图书借阅
 */
public class Borrow extends BmobObject {

    private String borrow_name;//借阅人
    private String book_admin;//图书管理者
    private String title;//书名
    private BmobDate restoreAt;//归还时间
    private int is_restore;//是否归还 1归还；0未归还
    private int is_overdue;//是否逾期 1逾期；0未逾期
    private String book_icon;//图书icon


    public String getBook_icon() {
        return book_icon;
    }

    public void setBook_icon(String book_icon) {
        this.book_icon = book_icon;
    }


    public BmobDate getRestoreAt() {
        return restoreAt;
    }

    public void setRestoreAt(BmobDate restoreAt) {
        this.restoreAt = restoreAt;
    }


    public String getBorrow_name() {
        return borrow_name;
    }

    public void setBorrow_name(String borrow_name) {
        this.borrow_name = borrow_name;
    }

    public String getBook_admin() {
        return book_admin;
    }

    public void setBook_admin(String book_admin) {
        this.book_admin = book_admin;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public int getIs_restore() {
        return is_restore;
    }

    public void setIs_restore(int is_restore) {
        this.is_restore = is_restore;
    }

    public int getIs_overdue() {
        return is_overdue;
    }

    public void setIs_overdue(int is_overdue) {
        this.is_overdue = is_overdue;
    }
}
