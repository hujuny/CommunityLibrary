package com.yhj.communitylibrary.login.bean;

/**
 * author : yhj
 * date   : 2020/5/4
 * desc   :
 */
public class NewPassowrdBean {

    /**
     * newpassword : 123456
     */

    public NewPassowrdBean(String newpassword) {
        this.newpassword = newpassword;
    }

    private String newpassword;

    public String getNewpassword() {
        return newpassword;
    }

    public void setNewpassword(String newpassword) {
        this.newpassword = newpassword;
    }
}
