package com.yhj.communitylibrary.my;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.yhj.communitylibrary.HomeActivity;
import com.yhj.communitylibrary.Model;
import com.yhj.communitylibrary.R;
import com.yhj.communitylibrary.login.activity.LoginActivity;
import com.yhj.communitylibrary.my.activity.RobotActivity;
import com.yhj.communitylibrary.utils.DownJsonUtils;
import com.yhj.communitylibrary.utils.UrlPath;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MyFragment extends Fragment {


    @BindView(R.id.my_bt_close)
    Button myBtClose;
    Unbinder unbinder;
    @BindView(R.id.my_ll_robot)
    LinearLayout myLlRobot;
    @BindView(R.id.iv_title)
    CircleImageView ivTitle;
    @BindView(R.id.tv_username)
    TextView tvUsername;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.my_update)
    TextView myUpdate;
    private String mVersionName;
    private String mDesc;
    private String downloadUrl;
    private NotificationCompat.Builder mBuilder;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        checkVersion();
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(HomeActivity.imgpath);
            ivTitle.setImageBitmap(bitmap);

            tvUsername.setText(HomeActivity.nick);
            tvPhone.setText(HomeActivity.name);

        } catch (NullPointerException e) {
            e.printStackTrace();
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.touxiang);
            ivTitle.setImageBitmap(bitmap);

            tvUsername.setText("");
            tvPhone.setText("");
        }

    }

    /**
     * 在线更新
     */
    private void checkVersion() {
        DownJsonUtils.downJsonOKHTTP_GET(UrlPath.path_4(), new DownJsonUtils.OnsendDataListener() {
            @Override
            public void onSendData(String json) throws JSONException {
                JSONObject jsonObject = new JSONObject(json);
                mVersionName = jsonObject.getString("versionName");
                mDesc = jsonObject.getString("description");
                downloadUrl = jsonObject.getString("downloadUrl");
                if (jsonObject.getInt("versionCode") > getVersionCode()) {
                    Drawable nav_up = getResources().getDrawable(android.R.drawable.ic_notification_overlay, null);
                    nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
                    myUpdate.setCompoundDrawables(null, null, nav_up, null);
                }
            }
        });
    }

    /**
     * 获取本地版本号
     *
     * @return
     */
    private int getVersionCode() {
        PackageManager packageManager = getActivity().getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getActivity().getPackageName(), 0);//获取包信息
            int versionCode = packageInfo.versionCode;

            return versionCode;
        } catch (PackageManager.NameNotFoundException e) {//没有包名会走此异常
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("最新版本V" + mVersionName);
        builder.setMessage("更新内容:\n" + mDesc);
        builder.setNegativeButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                download();
            }
        });

        builder.setPositiveButton("下次再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        /*
         * 设置取消侦听,用户点击返回键触发
         * */
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                String savePath = Environment.getExternalStorageDirectory().getAbsolutePath();

                Log.e("yhj", "" + savePath);

            }
        });
        builder.show();//显示对话框
    }

    /**
     * 下载新版本apk
     */
    private void download() {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("正在下载新版本...");
        progressDialog.setCancelable(false);
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(downloadUrl)
                .addHeader("Connection", "close")
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                // 储存下载文件的目录
                String savePath = Environment.getExternalStorageDirectory().getAbsolutePath();

                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.setMax((int) (total / 1024 / 1024));
                            progressDialog.show();
                        }
                    });

                    File file = new File(savePath, "communitylibrary.apk");
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        Log.e("yhj", "值为" + (int) (sum / 1024 / 1024));
                        long finalSum = sum;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.setProgress((int) (finalSum / 1024 / 1024));

                            }
                        });
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();

                        }
                    });
                    Intent intent = getInstallIntent();
                    startActivityForResult(intent, 0);
                    fos.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
    }


    private Intent getInstallIntent() {

        String fileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/communitylibrary.apk";
        Uri uri = null;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        try {
            if (Build.VERSION.SDK_INT >= 24) {//7.0 Android N
                uri = FileProvider.getUriForFile(getContext(), "com.yhj.communitylibrary.fileProvider", new File(fileName));
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//7.0以后，系统要求授予临时uri读取权限，安装完毕以后，系统会自动收回权限，该过程没有用户交互
            } else {//7.0以下
                uri = Uri.fromFile(new File(fileName));
            }
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            startActivity(intent);
            return intent;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return intent;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 0:
                if (resultCode==Activity.RESULT_OK){
                    Toast.makeText(getActivity(), "安装成功！", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getActivity(), "未安装该应用", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @OnClick({R.id.my_ll_robot, R.id.my_bt_close, R.id.my_share, R.id.my_advice, R.id.my_information, R.id.my_upgrade})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.my_ll_robot:
                startActivity(new Intent(getContext(), RobotActivity.class));
                break;

            case R.id.my_share:
                Intent textIntent = new Intent(Intent.ACTION_SEND);
                textIntent.setType("text/plain");
                textIntent.putExtra(Intent.EXTRA_TEXT, "分享一款非常棒的社区图书管理软件，功能强大，图书管理、借阅便捷，下载地址：http://yanghujun.com:8080/communitylibrary/communitylibrary.apk");
                startActivity(Intent.createChooser(textIntent, "分享"));
                break;
            case R.id.my_advice:
                // 必须明确使用mailto前缀来修饰邮件地址,如果使用   intent.putExtra(Intent.EXTRA_EMAIL, email)，结果将匹配不到任何应用
                Uri uri = Uri.parse("mailto:1364115532@qq.com");
                String[] email = {"1364115532@qq.com"};
                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                intent.putExtra(Intent.EXTRA_CC, email); // 抄送人
                intent.putExtra(Intent.EXTRA_SUBJECT, "请输入邮件主题！"); // 主题
                intent.putExtra(Intent.EXTRA_TEXT, "请输入问题描述！"); // 正文
                startActivity(Intent.createChooser(intent, "请选择邮件类应用"));
                break;
            case R.id.my_information:
                break;
            case R.id.my_upgrade:
                Drawable[] compoundDrawables = myUpdate.getCompoundDrawables();

                if (compoundDrawables[2]!=null) {
                    showUpdateDialog();
                } else {
                    Toast.makeText(getActivity(), "您的软件已是最新版本，无须更新！", Toast.LENGTH_SHORT).show();
                }
                break;


            case R.id.my_bt_close:
                Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        // 登录环信服务器退出登录
                        EMClient.getInstance().logout(false, new EMCallBack() {
                            @Override
                            public void onSuccess() {
                                // 关闭DBHelper
                                Model.getInstance().getDbManager().close();
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // 更新ui显示
                                        Toast.makeText(getActivity(), "退出成功", Toast.LENGTH_SHORT).show();
                                        // 回到登录页面
                                        Intent intent = new Intent(getActivity(), LoginActivity.class);

                                        try{
                                            intent.putExtra("pic",HomeActivity.imgpath);

                                        }catch(Exception e){
                                            e.printStackTrace();
                                        }
                                        startActivity(intent);
                                        getActivity().finish();
                                    }
                                });

                            }

                            @Override
                            public void onError(int i, final String s) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), "退出失败" + s, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onProgress(int i, String s) {
                            }
                        });
                    }
                });
                break;
        }
    }

}
