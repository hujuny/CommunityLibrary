package com.yhj.communitylibrary.manage.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import wangfei.scan2.Scan2Activity;

/**
 * author : yhj
 * date  :2019/12/3
 * desc   :二维码扫描
 */
public class BarCodeActivity extends Scan2Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = BarCodeActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    @Override
    public void handleResult(String s) {
//        Log.e("yhj", "地址："+s);
        Intent intent = new Intent(BarCodeActivity.this, BookEntryActivity.class);
        intent.putExtra("ISBN",s);
        startActivity(intent);
        finish();
    }

}
