package com.yhj.communitylibrary.login.activity;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hacknife.immersive.Immersive;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.yhj.communitylibrary.Model;
import com.yhj.communitylibrary.R;
import com.yhj.communitylibrary.login.bean.UserInfo;
import com.yhj.communitylibrary.utils.DownJsonUtils;
import com.yhj.communitylibrary.utils.UrlPath;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {


    @BindView(R.id.register_iv)
    CircleImageView registerIv;
    @BindView(R.id.register_et_name)
    EditText registerEtName;
    @BindView(R.id.register_et_phone)
    EditText registerEtPhone;
    @BindView(R.id.et_vercode)
    EditText etVercode;
    @BindView(R.id.iv_vercode)
    ImageView ivVercode;
    @BindView(R.id.register_et_password)
    EditText registerEtPassword;
    @BindView(R.id.bt_register)
    Button btRegister;
    private String text;

    String imagePath = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Immersive.setContentView(this, R.layout.activity_register, R.color.myblue, R.color.black, false, false);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        //显示二维码
        DownJsonUtils.downJsonOKHTTP_GET(UrlPath.path_1(), new DownJsonUtils.OnsendDataListener() {
            @Override
            public void onSendData(String json) throws JSONException {
                JSONObject jsonObject = new JSONObject(json);
                int showapi_res_code = jsonObject.getInt("showapi_res_code");
                if (showapi_res_code == 0) {
                    JSONObject showapi_res_body = jsonObject.getJSONObject("showapi_res_body");
                    String img_path = showapi_res_body.getString("img_path");
                    text = showapi_res_body.getString("text");

                    RequestOptions requestOptions = new RequestOptions()
                            .error(R.drawable.android);
                    Glide.with(RegisterActivity.this).load(img_path).apply(requestOptions).into(ivVercode);
                }
            }
        });
    }

    @OnClick({R.id.register_iv, R.id.bt_register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_iv:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    openAlbum();
                }
                break;

            case R.id.bt_register:
                if (!TextUtils.isEmpty(registerEtName.getText().toString()) && !TextUtils.isEmpty(registerEtPhone.getText().toString()) && !TextUtils.isEmpty(etVercode.getText().toString()) && !TextUtils.isEmpty(registerEtPassword.getText().toString()) && !TextUtils.isEmpty(imagePath)) {

                    if (!isMobile(registerEtPhone.getText().toString().trim())) {
                        Toast.makeText(this, "手机号码格式有误，请再次输入", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    if (!etVercode.getText().toString().trim().equals(text)) {
                        initData();
                        Toast.makeText(this, "验证码输入有误", Toast.LENGTH_SHORT).show();
                        break;
                    }


                    Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {

                            //环信注册
                            try {
                                EMClient.getInstance().createAccount(registerEtPhone.getText().toString().trim(), registerEtPassword.getText().toString().trim());//同步方法
                                // 保存用户账号信息到本地数据库
                                Model.getInstance().getUserAccountDao().addAccount(new UserInfo(registerEtPhone.getText().toString(), registerEtPassword.getText().toString(), registerEtName.getText().toString(), imagePath));

                            } catch (HyphenateException e) {
                                e.printStackTrace();
                            }


                            UserInfo userInfo = new UserInfo();
                            userInfo.setName(registerEtPhone.getText().toString().trim());
                            userInfo.setHxid(registerEtPassword.getText().toString().trim());
                            userInfo.setNick(registerEtName.getText().toString().trim());
                            userInfo.setPhoto(imagePath);
                            userInfo.save(new SaveListener<String>() {
                                @Override
                                public void done(String s, BmobException e) {
                                    if (e == null) {
                                        Toast.makeText(RegisterActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });


                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    intent.putExtra("phone", registerEtPhone.getText().toString().trim());
                    SharedPreferences mPref = getSharedPreferences("img", MODE_PRIVATE);
                    mPref.edit().putString("imgpath", imagePath).apply();
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "注册信息不能为空", Toast.LENGTH_SHORT).show();
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

    public void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    public String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            registerIv.setImageBitmap(bitmap);

//            SharedPreferences mPref = getSharedPreferences("img", MODE_PRIVATE);
//            mPref.edit().putString("imgs", imagePath).apply();
        } else {
            Toast.makeText(this, "无法显示当前图片，请换一张！", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            default:
                break;
            case 1:
                if (resultCode == RESULT_OK) {


                    if (Build.VERSION.SDK_INT >= 19) {
                        Uri uri = data.getData();
                        if (DocumentsContract.isDocumentUri(this, uri)) {

                            String documentId = DocumentsContract.getDocumentId(uri);
                            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                                String id = documentId.split(":")[1];//解析出数字格式的id
                                String selection = MediaStore.Images.Media._ID + "=" + id;
                                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
                            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(documentId));
                                imagePath = getImagePath(contentUri, null);
                            }
                        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
                            imagePath = getImagePath(uri, null);
                        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                            imagePath = uri.getPath();
                        }
                        displayImage(imagePath);
                    } else {
                        Uri uri = data.getData();
                        imagePath = getImagePath(uri, null);
                        displayImage(imagePath);
                    }
                }

                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(RegisterActivity.this, "请授予该权限,负责会影响使用部分功能！", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }


}
