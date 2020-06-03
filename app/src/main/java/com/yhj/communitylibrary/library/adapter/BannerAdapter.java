package com.yhj.communitylibrary.library.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yhj.communitylibrary.R;

/**
 * author : yhj
 * date   : 2019/11/22
 * desc   :轮播图适配器
 */
public class BannerAdapter extends PagerAdapter {

    private Context context;
    private View.OnClickListener onBannerClickListener;
    private int[] banners = new int[]{
            R.mipmap.s1,
            R.mipmap.s2,
            R.mipmap.s3,
            R.mipmap.s4,
            R.mipmap.s5
    };


    public BannerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;//一直滑动
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        //position的值范围是0-2147483647，将这个值对图片长度求余之后，position的取值范围是0——banners.length-1
        position %= banners.length - 1;
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setTag(position);


        imageView.setImageResource(banners[position]);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onBannerClickListener != null) {
                    onBannerClickListener.onClick(view);
                }
            }
        });
        container.addView(imageView);
        return imageView;
    }

    public void setOnBannerClickListener(View.OnClickListener onBannerClickListener) {
        this.onBannerClickListener = onBannerClickListener;
    }

    public int[] getBanners() {
        return banners;
    }
}
