package com.yhj.communitylibrary.login.bean;

import cn.bmob.v3.BmobObject;

/**
 * author : yhj
 * date   : 2019/12/11
 * desc   :用户账号信息的bean类
 */


public class UserInfo extends BmobObject {

    private String name;// 用户名称
    private String hxid;// 密码
    private String nick;// 用户的昵称
    private String photo;// 头像



    public UserInfo(String name) {
        this.name = name;
        this.hxid = name;
        this.nick = name;
    }


    public UserInfo(String name, String hxid, String nick, String photo) {
        this.name = name;
        this.hxid = hxid;
        this.nick = nick;
        this.photo = photo;
    }

    public UserInfo() {
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHxid() {
        return hxid;
    }

    public void setHxid(String hxid) {
        this.hxid = hxid;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }


    @Override
    public String toString() {
        return "UserInfo{" +
                "name='" + name + '\'' +
                ", hxid='" + hxid + '\'' +
                ", nick='" + nick + '\'' +
                ", photo='" + photo + '\'' +
                '}';
    }
}
