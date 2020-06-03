package com.yhj.communitylibrary.library.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yhj.communitylibrary.R;
import com.yhj.communitylibrary.manage.Book;

import java.util.List;

/**
 * author : yhj
 * date   : 2020/4/26
 * desc   :图书适配器
 */
public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {

    private Context mContext;
    private List<Book> mBookList;
    private OnBookClickListener listener;


    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView fruitImage;
        TextView fruitName;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            fruitImage = (ImageView) view.findViewById(R.id.book_image);
            fruitName = (TextView) view.findViewById(R.id.book_name);
        }
    }

    public BookAdapter(List<Book> bookList, OnBookClickListener listener) {
        this.listener = listener;
        mBookList = bookList;
    }


    @Override
    public BookAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (mContext == null) {
            mContext = viewGroup.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_book, viewGroup, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.fruitImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(holder.getAdapterPosition(), v);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull BookAdapter.ViewHolder viewHolder, int i) {
        Book fruit = mBookList.get(i);
        viewHolder.fruitName.setText(fruit.getTitle());
        //解决上传图书封面文件后url为https无法加载
        if (fruit.getBook_cover().contains("https")){
           fruit.setBook_cover(fruit.getBook_cover().replace("https","http"));
        }
        Log.e("yhj", "下载显示"+fruit.getBook_cover());
        Glide.with(mContext).load(fruit.getBook_cover()).into(viewHolder.fruitImage);


    }

    @Override
    public int getItemCount() {
        return mBookList.size();
    }

    public interface OnBookClickListener {
        void onItemClick(int pos, View view);
    }


}
