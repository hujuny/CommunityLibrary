package com.yhj.communitylibrary.my.activity.bean;


/**
 * Created by yhj on 2019/8/23.
 */

public class TuLingMsg {

    public interface Callback{
        void run(String msg);
    }

    /**
     * 文本类型
     */
    public static class Text{
        private int code;
        private String text;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }


}
