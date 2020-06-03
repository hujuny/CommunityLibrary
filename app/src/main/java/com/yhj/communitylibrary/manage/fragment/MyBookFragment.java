package com.yhj.communitylibrary.manage.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.yhj.communitylibrary.HomeActivity;
import com.yhj.communitylibrary.R;
import com.yhj.communitylibrary.library.adapter.BookAdapter;
import com.yhj.communitylibrary.library.adapter.SpinnerArrayAdapter;
import com.yhj.communitylibrary.manage.Book;
import com.yhj.communitylibrary.manage.activity.MyBookContentActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SQLQueryListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyBookFragment extends Fragment implements BookAdapter.OnBookClickListener {


    Unbinder unbinder;
    @BindView(R.id.my_book_category)
    Spinner myBookCategory;
    @BindView(R.id.my_book_recycle)
    RecyclerView myBookRecycle;
    @BindView(R.id.my_book_swipe)
    SwipeRefreshLayout myBookSwipe;

    @BindView(R.id.iv)
    ImageView iv;

    private BookAdapter bookAdapter;

    private List<Book> bookList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_book, container, false);
        unbinder = ButterKnife.bind(this, view);



        String[] mStringsItem = getResources().getStringArray(R.array.sping_text);
        ArrayAdapter<String> adapter = new SpinnerArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, mStringsItem, 16f);
        myBookCategory.setAdapter(adapter);

        myBookCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        myBookSwipe.setColorSchemeResources(R.color.myblue);
        myBookSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String category = myBookCategory.getSelectedItem().toString();
                if (category.equals("全部分类")) {
                    initData();
                } else {
                    showBook(category);
                }
                bookAdapter.notifyDataSetChanged();
                myBookSwipe.setRefreshing(false);
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
        BmobQuery<Book> eq1 = new BmobQuery<Book>();
        eq1.addWhereEqualTo("book_admin",HomeActivity.name);
        BmobQuery<Book> eq2 = new BmobQuery<Book>();
        eq2.addWhereEqualTo("category", spinnerContent);
        List<BmobQuery<Book>> queries = new ArrayList<BmobQuery<Book>>();
        queries.add(eq1);
        queries.add(eq2);
        BmobQuery<Book> mainQuery = new BmobQuery<Book>();
        BmobQuery<Book> andQuery = mainQuery.and(queries);
        andQuery.addQueryKeys("title,book_cover");
        andQuery.findObjects(new FindListener<Book>() {
            @Override
            public void done(List<Book> object, BmobException e) {
                if (e == null) {
                    if (object.size() == 0) {
                        myBookRecycle.setVisibility(View.GONE);
                        iv.setVisibility(View.VISIBLE);
                        Glide.with(getContext()).asGif().load(R.drawable.oa_develop).into(iv);
                    } else {
                        myBookRecycle.setVisibility(View.VISIBLE);
                        iv.setVisibility(View.GONE);
                        bookList.clear();
                        bookList.addAll(object);
                        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
                        myBookRecycle.setLayoutManager(layoutManager);
                        myBookRecycle.setAdapter(bookAdapter);
                    }

                } else {

                    Toast.makeText(getActivity(), "数据获取失败" + e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    /**
     * 初始化全部图书
     */
    private void initData() {

        BmobQuery<Book> bookBmobQuery = new BmobQuery<Book>();
        String bql = "select title,book_cover,book_admin from Book where book_admin='" + HomeActivity.name + "' order by createdAt desc";
        bookBmobQuery.setSQL(bql);
        bookBmobQuery.doSQLQuery(new SQLQueryListener<Book>() {
            @Override
            public void done(BmobQueryResult<Book> t, BmobException e) {
                List<Book> list = t.getResults();
                if (e == null) {
                    if (list.size() == 0) {
                        myBookRecycle.setVisibility(View.GONE);
                        iv.setVisibility(View.VISIBLE);
                        Glide.with(getContext()).asGif().load(R.drawable.oa_develop).into(iv);
                    } else {
                        myBookRecycle.setVisibility(View.VISIBLE);
                        iv.setVisibility(View.GONE);
                        bookList.clear();
                        bookList.addAll(list);
                        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
                        myBookRecycle.setLayoutManager(layoutManager);
                        myBookRecycle.setAdapter(bookAdapter);
                    }

                } else {
                    if (e.getErrorCode() == 9016) {
                        Toast.makeText(getActivity(), "网络未连接，请检查网络再试！", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onItemClick(int pos, View view) {
        Intent intent = new Intent(getActivity(), MyBookContentActivity.class);
        intent.putExtra("book_admin", HomeActivity.name);
        intent.putExtra("book_cover", bookList.get(pos).getBook_cover());
        startActivity(intent);
    }
}
