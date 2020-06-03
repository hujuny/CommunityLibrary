package com.yhj.communitylibrary.login.dao;

/**
 * Created by Administrator on 2016/9/24.
 */
// 联系人表的建表语句
public class ContactTable {
    public static final String TAB_NAME = "tab_contact";

    public static final String COL_HXID = "hxid";
    public static final String COL_NAME = "name";
    public static final String COL_NICK = "nick";
    public static final String COL_PHOTO = "photo";

    public static final String COL_IS_CONTACT = "is_contact";// 是否是联系人


    public static final String CREATE_TAB = "create table "
            + TAB_NAME + " ("
            + COL_NAME + " te0xt primary key,"
            + COL_HXID + " text,"
            + COL_NICK + " text,"
            + COL_PHOTO + " text,"
            + COL_IS_CONTACT + " integer);";


}
