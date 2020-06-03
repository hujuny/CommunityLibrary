package com.yhj.communitylibrary.library.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * author : yhj
 * date   : 2019/12/4
 * desc   :spinner构造器
 */
public class SpinnerArrayAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private String[] mStringArray;

    private int mtextViewResourceId;
    private float msize;

    public SpinnerArrayAdapter(Context context, int textViewResourceId, String[] objects, float size) {
        super(context, textViewResourceId, objects);
        mContext = context;
        mStringArray = objects;
        mtextViewResourceId = textViewResourceId;
        msize = size;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 修改spinner选择后结果的字体颜色
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mtextViewResourceId, parent, false);
        }//此处Text1是spinner默认的用来显示文字的textview
        TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
        tv.setText(mStringArray[position]);
        tv.setTextSize(msize);
        tv.setTextColor(Color.BLACK);
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        // 修改Spinner展开后的字体
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mtextViewResourceId, parent, false);
        }//修改Text1是Spinner默认的用来显示文字的Textview
        TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
        tv.setText(mStringArray[position]);
        tv.setTextSize(msize);
        tv.setGravity(Gravity.CENTER);
        return convertView;
    }
}

