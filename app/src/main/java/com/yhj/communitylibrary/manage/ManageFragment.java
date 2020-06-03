package com.yhj.communitylibrary.manage;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.yhj.communitylibrary.R;
import com.yhj.communitylibrary.manage.activity.BarCodeActivity;
import com.yhj.communitylibrary.manage.activity.BookEntryActivity;
import com.yhj.communitylibrary.manage.adapter.ManagePagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class ManageFragment extends Fragment {


    Unbinder unbinder;
    @BindView(R.id.toolar)
    Toolbar toolar;
    @BindView(R.id.manage_tab)
    TabLayout manageTab;
    @BindView(R.id.manage_viewpager)
    ViewPager manageViewpager;

    private String[] titles = {"我的图书", "借阅历史", "逾期记录"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage, container, false);
        unbinder = ButterKnife.bind(this, view);

        toolar.setTitle("");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolar);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        manageViewpager.setAdapter(new ManagePagerAdapter(getActivity().getSupportFragmentManager(),titles,1));

        //实现TabLayout 与 ViewPager 联动
        manageTab.setupWithViewPager(manageViewpager);

        //默认选中某项 这里是选中推荐
        manageTab.getTabAt(0).select();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.manage_book, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.code_entry://扫码录入
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 1);
                } else {
                    startActivity(new Intent(getContext(), BarCodeActivity.class));
                }
                break;
            case R.id.manual_entry://手动录入
                startActivity(new Intent(getContext(), BookEntryActivity.class));
                break;
        }
        return true;
    }
}
