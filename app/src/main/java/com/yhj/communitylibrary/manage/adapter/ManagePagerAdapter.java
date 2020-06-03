package com.yhj.communitylibrary.manage.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.yhj.communitylibrary.borrow.fragment.BorrowRecordsFragment;
import com.yhj.communitylibrary.borrow.fragment.OverdueRecordFragment;
import com.yhj.communitylibrary.borrow.fragment.ReturnRecordsFragment;
import com.yhj.communitylibrary.manage.fragment.BorrowHistoryFragment;
import com.yhj.communitylibrary.manage.fragment.MyBookFragment;
import com.yhj.communitylibrary.manage.fragment.OverdueRecordsFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * author : yhj
 * date   : 2020/3/17
 * desc   :TabViewPager适配器
 */
public class ManagePagerAdapter extends FragmentPagerAdapter {
    //Fragment的集合
    private List<Fragment> mBaseFragments;

    //标题的数组
    private String tabTitles[];
    private int sign;

    public ManagePagerAdapter(FragmentManager fm, String[] tabTitles,int sign) {
        super(fm);
        this.tabTitles = tabTitles;
        this.sign=sign;//标记borrow和manage||manage为1；borrow为2
        mBaseFragments = new ArrayList<>();


        if (sign==1) {
            mBaseFragments.add(new MyBookFragment());
            mBaseFragments.add(new BorrowHistoryFragment());
            mBaseFragments.add(new OverdueRecordsFragment());
        }else if (sign==2){
            mBaseFragments.add(new BorrowRecordsFragment());
            mBaseFragments.add(new ReturnRecordsFragment());
            mBaseFragments.add(new OverdueRecordFragment());
        }


    }

    @Override
    public int getCount() {
        return mBaseFragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return mBaseFragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
