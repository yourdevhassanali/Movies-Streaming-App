package com.hassan.moviestreamclientapp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hassan.moviestreamclientapp.Model.SliderSide;
import com.hassan.moviestreamclientapp.MoviePlayerActivity;
import com.hassan.moviestreamclientapp.R;

import java.util.List;

public class SliderPagerAdapterNew extends PagerAdapter {

    private Context mContext ;
    private List<SliderSide> mList ;

    public SliderPagerAdapterNew(Context mContext, List<SliderSide> mList) {

        this.mContext = mContext;
        this.mList = mList;
    }
    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View slideLayout = inflater.inflate(R.layout.slide_item,null);
        ImageView slideImg = slideLayout.findViewById(R.id.slide_img);
        TextView slideText = slideLayout.findViewById(R.id.slide_title);
        FloatingActionButton floatingActionButton = slideLayout.findViewById(R.id.floatingActionButton);

        Glide.with(mContext).load(mList.get(position).getVideo_thumb()).into(slideImg);
        //slideImg.setImageResource(mList.get(position).getVideo_thumb());
       // slideText.setText(mList.get(position).getVideo_name());


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String video_url =  mList.get(position).getVideo_url();
                Intent intent = new Intent(mContext , MoviePlayerActivity.class);
                intent.putExtra("videoUri",video_url);
                mContext.startActivity(intent);

            }
        });
        container.addView(slideLayout);
        return slideLayout;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}

