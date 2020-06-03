package com.yhj.communitylibrary.login.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hacknife.immersive.Immersive;
import com.yhj.communitylibrary.R;
import com.yhj.communitylibrary.login.bean.NewPassowrdBean;
import com.yhj.communitylibrary.login.bean.TokenBean;
import com.yhj.communitylibrary.utils.DownJsonUtils;
import com.yhj.communitylibrary.utils.UrlPath;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForgetPasswordActivity extends AppCompatActivity {

    @BindView(R.id.forget_phone)
    EditText forgetPhone;
    @BindView(R.id.forget_code)
    EditText forgetCode;
    @BindView(R.id.forget_new_password)
    EditText forgetNewPassword;
    @BindView(R.id.forget_get)
    TextView forgetGet;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Immersive.setContentView(this, R.layout.activity_forget_password, R.color.myblue, R.color.black, false, false);

        ButterKnife.bind(this);


    }

    /**
     * 获取环信app管理员token
     */
    private void getToken() {
        DownJsonUtils.downJsonOKHTTP_POST(UrlPath.path_2(), new TokenBean("client_credentials", "YXA6pkFwLvSNSCeWW6MwfbxppQ", "YXA6gkPxSOw39GMDq8l7UzTMzh_2CO8"), new DownJsonUtils.OnsendDataListener() {
            @Override
            public void onSendData(String json) throws JSONException {
                JSONObject jsonObject = new JSONObject(json);
                token = jsonObject.getString("access_token");
            }
        });
    }


    @OnClick({R.id.forget_get, R.id.forget_reset})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.forget_get:
                if (!TextUtils.isEmpty(forgetPhone.getText())) {
                    if (isMobile(forgetPhone.getText().toString())) {
                        getToken();
                        CountDownTimer timer = new CountDownTimer(60000, 1000) {
                            public void onTick(long millisUntilFinished) {
                                forgetGet.setText("重新发送(" + millisUntilFinished / 1000 + ")");
                                forgetGet.setEnabled(false);
                            }

                            public void onFinish() {
                                forgetGet.setText("点击获取验证码");
                                forgetGet.setEnabled(true);

                            }
                        };
                        timer.start();
                    } else {
                        Toast.makeText(this, "手机号格式有误，请检查再试！", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(this, "请输入找回密码的手机号！", Toast.LENGTH_SHORT).show();
                }


                break;
            case R.id.forget_reset:
                if (!TextUtils.isEmpty(forgetPhone.getText()) && !TextUtils.isEmpty(forgetCode.getText()) && !TextUtils.isEmpty(forgetNewPassword.getText())) {
                    DownJsonUtils.downJsonOKHTTP_PUT(UrlPath.path_3(forgetPhone.getText().toString()), new NewPassowrdBean(forgetNewPassword.getText().toString()), token, new DownJsonUtils.OnsendDataListener() {
                        @Override
                        public void onSendData(String json) throws JSONException {
                            if (json.trim().equals("200")) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ForgetPasswordActivity.this, "密码修改成功，请登录！", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
                                        intent.putExtra("phone", forgetPhone.getText().toString());
                                        startActivity(intent);
                                        finish();
                                    }
                                });

                            }
                        }
                    });
                } else {
                    Toast.makeText(this, "信息输入有误，请检查再试！", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * 效验手机号
     *
     * @param mobile
     * @return
     */
    public static boolean isMobile(String mobile) {
        String str = mobile;
        String pattern = "^(13[0-9]|15[012356789]|17[013678]|18[0-9]|14[57]|19[89]|166)[0-9]{8}";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(str);
        return m.matches();
    }
}
