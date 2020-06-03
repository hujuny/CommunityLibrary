package com.yhj.communitylibrary.utils;


import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 该工具类用来进行get post请求
 * 返回json字符串进行二次处理
 * @author zmm
 */
public class DownJsonUtils {

    /**
     *   GET
     * @param url
     * @param listener
     */
    public static void downJsonOKHTTP_GET(String url,final OnsendDataListener listener) {
        final Handler handler = new Handler();
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .get()//默认就是GET请求，可以不写
                .build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("yhj", "错误"+e );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            listener.onSendData(json);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

    }

    /**
     *   POST
     * @param url
     * @param object
     * @param listener
     */
    public static void downJsonOKHTTP_POST(String url, Object object, final OnsendDataListener listener) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();

        Gson gson = new Gson();
        String json = gson.toJson(object);
        //MediaType  设置Content-Type 标头中包含的媒体类型值
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8")
                , json);

        Request request = new Request.Builder()
                .url(url)//请求的url
                .post(requestBody)
                .build();

        //创建/Call
        Call call = okHttpClient.newCall(request);
        //加入队列 异步操作
        call.enqueue(new Callback() {
            //请求错误回调方法
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("连接失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    listener.onSendData(response.body().string());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    /**
     * 重置用户密码
     * @param url
     * @param newPassword
     * @param token
     * @param listener
     */
    public static void downJsonOKHTTP_PUT(String url,Object newPassword,String token,final OnsendDataListener listener) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();

        Gson gson = new Gson();
        String json = gson.toJson(newPassword);
        //MediaType  设置Content-Type 标头中包含的媒体类型值
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8")
                , json);

        Request request = new Request.Builder()
                .url(url)//请求的url
                .addHeader("Authorization","Bearer "+token)
                .put(requestBody)
                .build();

        //创建/Call
        Call call = okHttpClient.newCall(request);
        //加入队列 异步操作
        call.enqueue(new Callback() {
            //请求错误回调方法
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("连接失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    listener.onSendData(response.code()+"");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }


    public interface OnsendDataListener {
        void onSendData(String json) throws JSONException;
    }


}
