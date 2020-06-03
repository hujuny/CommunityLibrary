package com.yhj.communitylibrary.borrow;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yhj.communitylibrary.R;
import com.yhj.communitylibrary.manage.adapter.ManagePagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * author : yhj
 * date  :2019/12/3
 * desc   :借还管理
 */
public class BorrowFragment extends Fragment {

    @BindView(R.id.borrow_tab)
    TabLayout borrowTab;
    @BindView(R.id.borrow_viewpager)
    ViewPager borrowViewpager;
    Unbinder unbinder;

    private String[] titles = {"借书记录", "还书记录", "逾期记录"};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_borrow, container, false);

        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        borrowViewpager.setAdapter(new ManagePagerAdapter(getActivity().getSupportFragmentManager(), titles, 2));

        //实现TabLayout 与 ViewPager 联动
        borrowTab.setupWithViewPager(borrowViewpager);

        //默认选中某项 这里是选中推荐
        borrowTab.getTabAt(0).select();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
