package com.yhj.communitylibrary.manage.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.yhj.communitylibrary.HomeActivity;
import com.yhj.communitylibrary.R;
import com.yhj.communitylibrary.library.bean.Borrow;
import com.yhj.communitylibrary.manage.adapter.BookBorrowAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class BorrowHistoryFragment extends Fragment implements BookBorrowAdapter.OnBookClickListener {


    Unbinder unbinder;
    @BindView(R.id.book_history_recycle)
    RecyclerView bookHistoryRecycle;
    @BindView(R.id.iv)
    ImageView iv;
    @BindView(R.id.book_history_swipe)
    SwipeRefreshLayout bookHistorySwipe;

    private List<Borrow> bookList = new ArrayList<>();
    private BookBorrowAdapter bookBorrowAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_borrow_history, container, false);
        unbinder = ButterKnife.bind(this, view);


        bookBorrowAdapter = new BookBorrowAdapter(bookList, this,1);

        initData();


        bookHistorySwipe.setColorSchemeResources(R.color.myblue);
        bookHistorySwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
                bookBorrowAdapter.notifyDataSetChanged();
                bookHistorySwipe.setRefreshing(false);
            }
        });


        return view;
    }

    private void initData() {


        String bql = "select * from Borrow where book_admin='" + HomeActivity.name + "' and is_restore=0 order by restoreAt desc";
        BmobQuery<Borrow> query = new BmobQuery<Borrow>();
        query.setSQL(bql);
        query.doSQLQuery(new SQLQueryListener<Borrow>() {

            @Override
            public void done(BmobQueryResult<Borrow> result, BmobException e) {
                if (e == null) {
                    List<Borrow> list = (List<Borrow>) result.getResults();
                    if (list != null && list.size() > 0) {
                        iv.setVisibility(View.GONE);
                        bookHistoryRecycle.setVisibility(View.VISIBLE);
                        bookList.clear();
                        bookList.addAll(list);
                        bookHistoryRecycle.setLayoutManager(new LinearLayoutManager(getContext()));
                        bookHistoryRecycle.setAdapter(bookBorrowAdapter);
                    } else {
                        bookHistoryRecycle.setVisibility(View.GONE);
                        iv.setVisibility(View.VISIBLE);
                        Glide.with(getContext()).asGif().load(R.drawable.oa_develop).into(iv);
                    }
                }else {
                    if (e.getErrorCode()==9016){
                        Toast.makeText(getActivity(), "网络未连接，请检查网络再试！", Toast.LENGTH_SHORT).show();
                        iv.setVisibility(View.VISIBLE);
                        Glide.with(getContext()).asGif().load(R.drawable.oa_develop).into(iv);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("还书");
        builder.setMessage("确认还书吗？");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String objectId = bookList.get(pos).getObjectId();

                Borrow borrow = new Borrow();
                borrow.setIs_restore(1);
                borrow.update(objectId, new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e==null){
                            dialog.dismiss();
                            Toast.makeText(getActivity(), "还书成功！", Toast.LENGTH_SHORT).show();
                            bookList.remove(pos);
                            bookBorrowAdapter.notifyDataSetChanged();
                            if (bookList.size()<=0){
                                iv.setVisibility(View.VISIBLE);
                                Glide.with(getContext()).asGif().load(R.drawable.oa_develop).into(iv);
                            }

                        }
                    }
                });
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
