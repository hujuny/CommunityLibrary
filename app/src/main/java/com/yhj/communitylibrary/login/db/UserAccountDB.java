package com.yhj.communitylibrary.login.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.yhj.communitylibrary.login.dao.UserAccountTable;

/**
 * author : yhj
 * date   : 2019/12/11
 * desc   :
 */
public class UserAccountDB extends SQLiteOpenHelper {

    // 构造
    public UserAccountDB(Context context) {
        super(context, "account.db", null, 1);
    }

    // 数据库创建的时候调用
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建数据库表的语句
        db.execSQL(UserAccountTable.CREATE_TAB);
    }

    // 数据库更新的时候调用
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
