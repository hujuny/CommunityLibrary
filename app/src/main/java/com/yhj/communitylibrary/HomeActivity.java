package com.yhj.communitylibrary;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hacknife.immersive.Immersive;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.yhj.communitylibrary.borrow.BorrowFragment;
import com.yhj.communitylibrary.contacts.ContactsFragment;
import com.yhj.communitylibrary.library.LibraryFragment;
import com.yhj.communitylibrary.library.bean.Borrow;
import com.yhj.communitylibrary.login.bean.UserInfo;
import com.yhj.communitylibrary.login.dao.UserAccountDao;
import com.yhj.communitylibrary.manage.ManageFragment;
import com.yhj.communitylibrary.manage.activity.BarCodeActivity;
import com.yhj.communitylibrary.my.MyFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class HomeActivity extends AppCompatActivity {


    @BindView(R.id.navigation)
    BottomNavigationView navigation;
    @BindView(R.id.main_fragment)
    LinearLayout mainFragment;
    private Fragment[] fragments;

    private boolean isBack = true;
    private int lastfragment = 0;
    private UserInfo userInfo;
    public static String name;
    public static String nick;
    public static String imgpath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Immersive.setContentView(this, R.layout.activity_home, R.color.myblue, R.color.black, false, false);
        ButterKnife.bind(this);

        //注册一个监听连接状态的listener
        EMClient.getInstance().addConnectionListener(new MyConnectionListener());

        Intent intent = getIntent();

        UserAccountDao userAccountDao = new UserAccountDao(this);
        userInfo = userAccountDao.getAccountByHxId(EMClient.getInstance().getCurrentUser());

        try {
            name = userInfo.getName();
            nick = userInfo.getNick();
            imgpath = userInfo.getPhoto();
            myBookOverdue();
        } catch (Exception e) {
            e.printStackTrace();

            BmobQuery<UserInfo> categoryBmobQuery = new BmobQuery<>();
            categoryBmobQuery.addWhereEqualTo("name", intent.getStringExtra("name"));
            categoryBmobQuery.findObjects(new FindListener<UserInfo>() {
                @Override
                public void done(List<UserInfo> object, BmobException e) {
                    if (e == null) {
                        name = object.get(0).getName();
                        nick = object.get(0).getNick();
                        imgpath = object.get(0).getPhoto();
                        try {
                            myBookOverdue();//我的图书借阅逾期
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            });


        }

        initFragment();
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


    }

    private void myBookOverdue() throws ParseException {

        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String time = simpleDateFormat.format(date);
        Date date1 = simpleDateFormat.parse(time);
        BmobDate bmobDate = new BmobDate(date1);

        BmobQuery<Borrow> query1 = new BmobQuery<Borrow>();
        query1.addWhereEqualTo("borrow_name", name);
        Log.e("yhj", "名字" + name);
        BmobQuery<Borrow> query2 = new BmobQuery<Borrow>();
        query2.addWhereEqualTo("is_restore", 0);

        BmobQuery<Borrow> query3 = new BmobQuery<Borrow>();
        query3.addWhereLessThan("restoreAt", bmobDate);

        List<BmobQuery<Borrow>> queries = new ArrayList<BmobQuery<Borrow>>();
        queries.add(query1);
        queries.add(query2);
        queries.add(query3);

        BmobQuery<Borrow> mainQuery = new BmobQuery<Borrow>();
        mainQuery.and(queries);
        mainQuery.findObjects(new FindListener<Borrow>() {
            @Override
            public void done(List<Borrow> object, BmobException e) {
                Log.e("yhj", "长度" + object.size());
                if (e == null) {
                    if (object.size() > 0) {
                        Toast.makeText(HomeActivity.this, "你有图书逾期未归还！", Toast.LENGTH_SHORT).show();
                        for (int i = 0; i < object.size(); i++) {
                            String objectId = object.get(i).getObjectId();
                            Borrow borrow = new Borrow();
                            borrow.setIs_overdue(1);
                            borrow.update(objectId, new UpdateListener() {
                                @Override
                                public void done(BmobException e) {

                                }
                            });
                        }
                    }
                }else{
                    Toast.makeText(HomeActivity.this, "逾期更新失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void initFragment() {
        LibraryFragment libraryFragment = new LibraryFragment();
        ManageFragment manageFragment = new ManageFragment();
        ContactsFragment contactsFragment = new ContactsFragment();
        BorrowFragment borrowFragment = new BorrowFragment();
        MyFragment myFragment = new MyFragment();

        fragments = new Fragment[]{libraryFragment, manageFragment, contactsFragment, borrowFragment, myFragment};
        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, libraryFragment).show(libraryFragment).commit();

    }

    public void switchFragment(int lastFragment, int index) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(fragments[lastFragment]);
        if (!fragments[index].isAdded()) {
            transaction.add(R.id.main_fragment, fragments[index]);
        }
        transaction.show(fragments[index]).commitAllowingStateLoss();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            switch (menuItem.getItemId()) {
                case R.id.home_library:
                    if (lastfragment != 0) {
                        switchFragment(lastfragment, 0);
                        lastfragment = 0;
                    }
                    return true;
                case R.id.home_manage:
                    if (lastfragment != 1) {
                        switchFragment(lastfragment, 1);
                        lastfragment = 1;
                    }
                    return true;
                case R.id.home_contacts:
                    if (lastfragment != 2) {
                        switchFragment(lastfragment, 2);
                        lastfragment = 2;
                    }
                    return true;
                case R.id.home_borrow:
                    if (lastfragment != 3) {
                        switchFragment(lastfragment, 3);
                        lastfragment = 3;
                    }
                    return true;
                case R.id.home_my:
                    if (lastfragment != 4) {
                        switchFragment(lastfragment, 4);
                        lastfragment = 4;
                    }
                    return true;

            }
            return false;
        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(new Intent(this, BarCodeActivity.class));
                } else {
                    Toast.makeText(HomeActivity.this, "请授予该权限,负责会影响使用部分功能！", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isBack) {
                isBack = false;
                Toast.makeText(this,
                        "再次点击退出",
                        Toast.LENGTH_SHORT).show();
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        isBack = true;
                    }
                }.start();

                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    //实现ConnectionListener接口
    private class MyConnectionListener implements EMConnectionListener {
        @Override
        public void onConnected() {
        }

        @Override
        public void onDisconnected(final int error) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (error == EMError.USER_REMOVED) {
                        // 显示帐号已经被移除
                    } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                        // 显示帐号在其他设备登录
                        Toast.makeText(HomeActivity.this, "账号已在其它设备登录！", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }
}
