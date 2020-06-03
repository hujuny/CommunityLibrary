package com.yhj.communitylibrary.library.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yhj.communitylibrary.R;
import com.yhj.communitylibrary.library.adapter.BookAdapter;
import com.yhj.communitylibrary.manage.Book;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class SearchBookActivity extends AppCompatActivity implements BookAdapter.OnBookClickListener {

    @BindView(R.id.add_et_phone)
    EditText addEtPhone;
    @BindView(R.id.search_book_recycle)
    RecyclerView searchBookRecycle;
    @BindView(R.id.tv)
    TextView tv;

    private List<Book> bookList = new ArrayList<>();
    private BookAdapter bookAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_book);
        ButterKnife.bind(this);

        bookAdapter = new BookAdapter(bookList, this);


        addEtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //处理清除字符后界面布局不消失

                Log.e("yhj", "内容" + s);
                if (!TextUtils.isEmpty(s)) {
                    searchBook(s.toString().trim());//找书
                } else {
                    tv.setVisibility(View.GONE);
                    bookList.clear();
                    bookAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    private void searchBook(CharSequence s) {
        if (bookList.size()>0){
            bookList.clear();
            bookAdapter.notifyDataSetChanged();
        }
        BmobQuery bmobQuery = new BmobQuery<>();
        bmobQuery.addQueryKeys("title,book_cover,book_admin");
        bmobQuery.findObjects(new FindListener<Book>() {
            @Override
            public void done(List<Book> object, BmobException e) {
                if (e == null) {

                    for (int i = 0; i < object.size(); i++) {
                        if (object.get(i).getTitle().contains(s)) {
                            bookList.add(object.get(i));
                        }
                    }
                    if (bookList.size() <= 0) {
                        tv.setVisibility(View.VISIBLE);
                    } else {
                        tv.setVisibility(View.GONE);
                        GridLayoutManager layoutManager = new GridLayoutManager(SearchBookActivity.this, 3);
                        searchBookRecycle.setLayoutManager(layoutManager);
                        searchBookRecycle.setAdapter(bookAdapter);
                    }

                } else {
                    Toast.makeText(SearchBookActivity.this, "数据获取失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public void onItemClick(int pos, View view) {
        Intent intent = new Intent(SearchBookActivity.this, BookContentActivity.class);
        intent.putExtra("book_admin", bookList.get(pos).getBook_admin());
        intent.putExtra("title", bookList.get(pos).getTitle());

        startActivity(intent);
    }
}
