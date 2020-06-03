package com.yhj.communitylibrary.login.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hacknife.immersive.Immersive;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.yhj.communitylibrary.CLApplication;
import com.yhj.communitylibrary.HomeActivity;
import com.yhj.communitylibrary.Model;
import com.yhj.communitylibrary.R;
import com.yhj.communitylibrary.login.bean.UserInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class LoginActivity extends AppCompatActivity {


    @BindView(R.id.et_username)
    EditText etUsername;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.bt_login)
    Button btLogin;
    @BindView(R.id.tv_register)
    TextView tvRegister;
    @BindView(R.id.tv_forget_password)
    TextView tvForgetPassword;
    @BindView(R.id.login_iv)
    CircleImageView loginIv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Immersive.setContentView(this, R.layout.activity_login, R.color.myblue, R.color.black, false, false);

        ButterKnife.bind(this);

        CLApplication.addActivity(this);


        Intent intent = getIntent();
        etUsername.setText(intent.getStringExtra("phone"));
        try{
            String pic = intent.getStringExtra("pic");
            Bitmap bitmap = BitmapFactory.decodeFile(pic);
            loginIv.setImageBitmap(bitmap);
        }catch(Exception e){
            e.printStackTrace();
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.touxiang);
            loginIv.setImageBitmap(bitmap);
        }
        String imgPath = getSharedPreferences("img", MODE_PRIVATE).getString("imgpath", "");
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.touxiang);
        loginIv.setImageBitmap(bitmap);


    }

    @OnClick({R.id.tv_register, R.id.tv_forget_password, R.id.bt_login})
    public void ViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_register:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;

            case R.id.tv_forget_password:
                startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
                break;

            case R.id.bt_login:
                if (!TextUtils.isEmpty(etUsername.getText().toString().trim()) && !TextUtils.isEmpty(etPassword.getText().toString().trim())) {

                    Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            //环信登录
                            EMClient.getInstance().login(etUsername.getText().toString().trim(), etPassword.getText().toString().trim(), new EMCallBack() {
                                @Override
                                public void onSuccess() {

                                    // 对模型层数据的处理
                                    Model.getInstance().loginSuccess(new UserInfo(etUsername.getText().toString()));
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // 提示登录成功
                                            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();


                                            // 跳转到主页面
                                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                            intent.putExtra("name", etUsername.getText().toString());

                                            startActivity(intent);

                                            finish();
                                        }
                                    });
                                }

                                @Override
                                public void onError(int i, final String s) {
                                    // 提示登录失败
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(LoginActivity.this, "登录失败" + s, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void onProgress(int i, String s) {

                                }
                            });

                        }
                    });

                } else {
                    Toast.makeText(this, "账号或密码不能为空", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        CLApplication.finishAllActivity();
    }
}
