package com.yhj.communitylibrary;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.EaseUI;
import com.yhj.communitylibrary.utils.SpeechUtils;

import java.util.Stack;

import cn.bmob.v3.Bmob;


/**
 * author : yhj
 * date   : 2019/12/11
 * desc   : application
 */
public class CLApplication extends Application {

    private static Context mContext;
    private static Stack<Activity> activityStack;//activity栈

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化EaseUI
        EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证,true:自动验证,false,手动验证
        options.setAcceptInvitationAlways(false);
        EaseUI.getInstance().init(this, options);


        //Bmob云数据库初始化
        //TODO bmob云数据库的appkey
        Bmob.initialize(this, "your appkey");

        Model.getInstance().init(this);


        // 初始化全局上下文对象
        mContext = this;

        SpeechUtils.initTTS(mContext);

    }


    // 获取全局上下文对象
    public static Context getGlobalApplication() {
        return mContext;
    }


    /**
     * 添加activity到堆栈
     *
     * @param activity
     */
    public static void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<>();
        }
        if (!activityStack.contains(activity)) {
            activityStack.add(activity);
        }
    }

    /**
     * 销毁所有activity
     */
    public static void finishAllActivity() {
        for (int i = 0; i < activityStack.size(); i++) {
            if (activityStack.get(i) != null) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }
}
