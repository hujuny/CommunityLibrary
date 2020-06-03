package com.yhj.communitylibrary.library;


import android.content.Intent;
import android.graphics.Outline;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.yhj.communitylibrary.R;
import com.yhj.communitylibrary.library.Activity.BookContentActivity;
import com.yhj.communitylibrary.library.Activity.SearchBookActivity;
import com.yhj.communitylibrary.library.adapter.BannerAdapter;
import com.yhj.communitylibrary.library.adapter.BookAdapter;
import com.yhj.communitylibrary.library.adapter.SpinnerArrayAdapter;
import com.yhj.communitylibrary.manage.Book;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SQLQueryListener;


public class LibraryFragment extends Fragment implements BookAdapter.OnBookClickListener {


    @BindView(R.id.vp_banner)
    ViewPager vpBanner;
    @BindView(R.id.viewGroup)
    LinearLayout viewGroup;
    Unbinder unbinder;

    public static final int CAROUSEL_TIME = 3000;//滚动间隔
    @BindView(R.id.library_banner)
    RelativeLayout libraryBanner;
    @BindView(R.id.library_category)
    Spinner libraryCategorySpinner;
    @BindView(R.id.library_search)
    ImageView librarySearch;
    @BindView(R.id.library_recycle)
    RecyclerView libraryRecycle;
    @BindView(R.id.library_swipe)
    SwipeRefreshLayout librarySwipe;

    private int currentItem = 0;
    private BannerAdapter bannerAdapter;
    private BookAdapter bookAdapter;

    private List<Book> bookList = new ArrayList<>();

    private Handler handler = new Handler();
    private final Runnable mTicker = new Runnable() {
        @Override
        public void run() {
            long now = SystemClock.uptimeMillis();
            long next = now + (CAROUSEL_TIME - now % CAROUSEL_TIME);
            handler.postAtTime(mTicker, next);
            currentItem++;
            vpBanner.setCurrentItem(currentItem);


        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);
        unbinder = ButterKnife.bind(this, view);
        initBanner();

        String[] mStringsItem = getResources().getStringArray(R.array.sping_text);
        ArrayAdapter<String> adapter = new SpinnerArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, mStringsItem, 16f);
        libraryCategorySpinner.setAdapter(adapter);

        libraryCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String spinnerContent = adapter.getItem(position);
                if (spinnerContent.equals("全部分类")) {
                    initData();
                } else {
                    showBook(spinnerContent);//展示不同类别图书
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        bookAdapter = new BookAdapter(bookList, this);
        librarySwipe.setColorSchemeResources(R.color.myblue);
        librarySwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String category = libraryCategorySpinner.getSelectedItem().toString();
                if (category.equals("全部分类")) {
                    initData();
                } else {
                    showBook(category);
                }
                bookAdapter.notifyDataSetChanged();
                librarySwipe.setRefreshing(false);
            }
        });

        return view;

    }


    /**
     * 展示不同类别图书
     *
     * @param spinnerContent
     */
    private void showBook(String spinnerContent) {
        BmobQuery<Book> bookBmobQuery = new BmobQuery<Book>();
        String bql = "select title,book_cover,book_admin from Book where category='" + spinnerContent + "' order by createdAt desc";
        bookBmobQuery.setSQL(bql);
        bookBmobQuery.doSQLQuery(new SQLQueryListener<Book>() {
            @Override
            public void done(BmobQueryResult<Book> t, BmobException e) {
                if (e == null) {
                    List<Book> list = (List<Book>) t.getResults();
                    bookList.clear();
                    bookList.addAll(list);
                    GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
                    libraryRecycle.setLayoutManager(layoutManager);
                    libraryRecycle.setAdapter(bookAdapter);
                } else {
                    if (e.getErrorCode() == 9016) {
                        Toast.makeText(getActivity(), "网络未连接，请检查网络再试", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

    }


    /**
     * 初始化全部图书
     */
    private void initData() {

        String bql = "select title,book_cover,book_admin from Book order by createdAt desc";
        BmobQuery<Book> query = new BmobQuery<>();

        query.setSQL(bql);
        query.doSQLQuery(new SQLQueryListener<Book>() {
            @Override
            public void done(BmobQueryResult<Book> t, BmobException e) {
                if (e == null) {
                    List<Book> list = (List<Book>) t.getResults();
                    bookList.clear();
                    bookList.addAll(list);
                    GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
                    libraryRecycle.setLayoutManager(layoutManager);
                    libraryRecycle.setAdapter(bookAdapter);
                } else {
                    if (e.getErrorCode() == 9016) {
                        Toast.makeText(getActivity(), "网络未连接，请检查网络再试", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

    }

    private void initBanner() {
        //给banner图设置圆角
        libraryBanner.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), 30);
            }
        });
        libraryBanner.setClipToOutline(true);

        bannerAdapter = new BannerAdapter(getActivity());
        bannerAdapter.setOnBannerClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (Integer) view.getTag();
            }
        });
        vpBanner.setOffscreenPageLimit(2);//缓存页数
        vpBanner.setAdapter(bannerAdapter);
        vpBanner.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                currentItem = i;
                setImageBackground(i %= bannerAdapter.getBanners().length);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        for (int i = 0; i < bannerAdapter.getBanners().length; i++) {
            ImageView imageView = new ImageView(getActivity());
            imageView.setLayoutParams(new ViewGroup.LayoutParams(1, 1));
            if (i == 0) {
                imageView.setBackgroundResource(R.mipmap.yuandian);
            } else {
                imageView.setBackgroundResource(R.mipmap.yuandian1);
            }
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 10;
            layoutParams.rightMargin = 10;
            viewGroup.addView(imageView, layoutParams);
        }
        currentItem = bannerAdapter.getBanners().length * 50;
        vpBanner.setCurrentItem(currentItem);

        handler.postDelayed(mTicker, CAROUSEL_TIME);
    }

    private void setImageBackground(int selectItems) {
        for (int i = 0; i < bannerAdapter.getBanners().length; i++) {
            ImageView imageVew = (ImageView) viewGroup.getChildAt(i);
            imageVew.setBackground(getResources().getDrawable(R.drawable.login_button_shape));
            if (i == selectItems) {
                imageVew.setBackgroundResource(R.mipmap.yuandian);
            } else {
                imageVew.setBackgroundResource(R.mipmap.yuandian1);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(mTicker);
        unbinder.unbind();
    }


    @OnClick(R.id.library_search)
    public void onClick() {
        startActivity(new Intent(getContext(), SearchBookActivity.class));

    }

    @Override
    public void onItemClick(int pos, View view) {
        Intent intent = new Intent(getActivity(), BookContentActivity.class);
        intent.putExtra("book_admin", bookList.get(pos).getBook_admin());
        intent.putExtra("title", bookList.get(pos).getTitle());
        startActivity(intent);

    }


}


