package com.yhj.communitylibrary.my.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.yhj.communitylibrary.R;
import com.yhj.communitylibrary.my.activity.bean.Msg;
import com.yhj.communitylibrary.my.activity.bean.TuLingMsg;
import com.yhj.communitylibrary.my.adapter.MsgAdapter;
import com.yhj.communitylibrary.utils.SpeechUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.yhj.communitylibrary.my.activity.bean.Msg.TYPE_RECIVED;

public class RobotActivity extends AppCompatActivity {

    private EditText inputText;
    private RecyclerView msgRecyclerView;
    private List<Msg> msgList = new ArrayList<>();
    private MsgAdapter adapter;
    private Button send;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot);



        //获取控件
        inputText = (EditText) findViewById(R.id.input_text);
        msgRecyclerView = (RecyclerView) findViewById(R.id.msg_recycler_view);

        //消息处理
        msgHandle();
        //初始化消息列表
        initMsgs();

    }



    private void msgHandle() {
        //创建属于主线程的handler
        handler = new Handler();
        msgRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MsgAdapter(msgList);
        msgRecyclerView.setAdapter(adapter);
        send = (Button) findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String content = inputText.getText().toString();

                if (!"".equals(content)) {
                    //显示我发送的消息
                    Msg msg = new Msg(content, Msg.TYPE_SEND);
                    msgList.add(msg);
                    adapter.notifyItemInserted(msgList.size() - 1);
                    msgRecyclerView.scrollToPosition(msgList.size() - 1);
                    inputText.setText("");

                    //随机回应消息
                    //getMsgTuLing(content);
                    new Thread() {
                        public void run() {
                            //图灵机器人接口
                            getMsgTuLing(content, new TuLingMsg.Callback() {

                                @Override
                                public void run(String msg) {
                                    SpeechUtils.startAuto(msg);
                                    //更新界面
                                    msgList.add(new Msg(msg, TYPE_RECIVED));
                                    adapter.notifyItemInserted(msgList.size() - 1);
                                    msgRecyclerView.scrollToPosition(msgList.size() - 1);
                                }
                            });

                        }
                    }.start();
                }
            }
        });
    }

    private void initMsgs() {
        SpeechUtils.startAuto("你好,我是您的智能服务助手，请问您需要什么帮助！");

        msgList.add(new Msg("你好,我是您的智能服务助手，请问您需要什么帮助！", TYPE_RECIVED));


    }

    /**
     * post请求 提交数据到服务器
     *
     * @param
     */
    public void getMsgTuLing(String msg, final TuLingMsg.Callback callback) {
        String json = "{\n" +
                "    \"key\":\"b596bc014659412aaa2f7defb14b2bb8\",\n" +
                "    \"info\":\"" + msg + "\",\n" +
                "    \"userid\":\"ewew223\"\n" +
                "}";


        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

        //图灵机器人接口
        String url = "http://www.tuling123.com/openapi/api";
        OkHttpClient client = new OkHttpClient();//创建okhttp实例
        RequestBody body = RequestBody.create(mediaType, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        client.newCall(request);
        OkHttpClient mOkHttpClient = new OkHttpClient();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    TuLingMsg.Text text = JSON.parseObject(result, TuLingMsg.Text.class);
                    final String str = text.getText();

                    //回应消息，必须这样写，请参考 http://blog.csdn.net/djx123456/article/details/6325983
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.run(str);
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpeechUtils.textToSpeech.stop();
    }
}
