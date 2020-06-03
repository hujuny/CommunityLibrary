package com.yhj.communitylibrary.manage.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.yhj.communitylibrary.R;
import com.yhj.communitylibrary.library.bean.Borrow;

import java.util.List;

/**
 * author : yhj
 * date   : 2020/4/29
 * desc   :借阅历史适配器
 */
public class BookBorrowAdapter extends RecyclerView.Adapter<BookBorrowAdapter.ViewHolder> {

    private Context mContext;

    private List<Borrow> mBookList;

    private OnBookClickListener listener;
    private int flag;//1.借阅历史；2.管理-逾期记录；3.借还-逾期记录；4，借还-（借书记录，还书记录）


    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView borrowTitle;
        TextView borrowTime;
        TextView borrowName;
        ImageView borrowIcon;
        LinearLayout borrowLinear;

        public ViewHolder(View view) {
            super(view);
            borrowTitle = (TextView) view.findViewById(R.id.borrow_history_title);
            borrowTime = (TextView) view.findViewById(R.id.borrow_history_time);
            borrowName = (TextView) view.findViewById(R.id.borrow_history_name);
            borrowIcon = (ImageView) view.findViewById(R.id.borrow_history_icon);
            borrowLinear = (LinearLayout) view.findViewById(R.id.book_history_linear);
        }
    }

    public BookBorrowAdapter(List<Borrow> bookList, OnBookClickListener mListener, int flag) {
        mBookList = bookList;
        listener = mListener;
        this.flag = flag;
    }


    @NonNull
    @Override
    public BookBorrowAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (mContext == null) {
            mContext = viewGroup.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_history_borrow, viewGroup, false);
        final BookBorrowAdapter.ViewHolder holder = new BookBorrowAdapter.ViewHolder(view);

        holder.borrowLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listener.onItemClick(holder.getAdapterPosition(), v);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull BookBorrowAdapter.ViewHolder viewHolder, int i) {
        Borrow borrow = mBookList.get(i);
        viewHolder.borrowTitle.setText(borrow.getTitle());
        viewHolder.borrowTime.setText(borrow.getRestoreAt().getDate().substring(0, 10));
        if (flag == 1) {
            viewHolder.borrowName.setText("借阅人：" + borrow.getBorrow_name());
        } else if (flag == 3) {
            viewHolder.borrowName.setText("图书管理者：" + borrow.getBook_admin());
            viewHolder.borrowTime.setTextColor(Color.RED);
        } else if (flag == 2) {
            viewHolder.borrowName.setText("借阅人：" + borrow.getBorrow_name());
            viewHolder.borrowTime.setTextColor(Color.RED);

        } else if (flag == 4) {
            viewHolder.borrowName.setText("图书管理者：" + borrow.getBook_admin());
        }
        if (borrow.getBook_icon().contains("https")){
            borrow.setBook_icon(borrow.getBook_icon().replace("https","http"));
        }
        RequestOptions requestOptions = new RequestOptions();
        Glide.with(mContext).load(borrow.getBook_icon()).apply(requestOptions).into(viewHolder.borrowIcon);

    }

    @Override
    public int getItemCount() {
        return mBookList.size();
    }

    public interface OnBookClickListener {
        void onItemClick(int pos, View view);
    }
}
