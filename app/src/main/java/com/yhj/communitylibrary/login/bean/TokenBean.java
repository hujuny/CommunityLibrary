package com.yhj.communitylibrary.login.bean;

/**
 * author : yhj
 * date   : 2020/5/4
 * desc   :tokenBean
 */

public class TokenBean {
    /**
     * grant_type : client_credentials
     * client_id : YXA6pkFwLvSNSCeWW6MwfbxppQ
     * client_secret : YXA6gkPxSOw39GMDq8l7UzTMzh_2CO8
     */

    private String grant_type;
    private String client_id;

    public TokenBean(String grant_type, String client_id, String client_secret) {
        this.grant_type = grant_type;
        this.client_id = client_id;
        this.client_secret = client_secret;
    }

    private String client_secret;

    public String getGrant_type() {
        return grant_type;
    }

    public void setGrant_type(String grant_type) {
        this.grant_type = grant_type;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getClient_secret() {
        return client_secret;
    }

    public void setClient_secret(String client_secret) {
        this.client_secret = client_secret;
    }
}
