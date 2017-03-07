package com.pandaq.mvpdemo.adapter;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pandaq.mvpdemo.R;
import com.pandaq.mvpdemo.model.zhihu.ZhihuStory;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by PandaQ on 2016/10/19.
 * email : 767807368@qq.com
 */

public class ZhihuStoryAdapter extends RecyclerView.Adapter {

    private Activity mActivity;
    private ArrayList<ZhihuStory> mStories;

    public ZhihuStoryAdapter(Activity activity, ArrayList<ZhihuStory> stories) {
        mActivity = activity;
        mStories = stories;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.zhihudaily_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        holder.mNewsTitle.setText(mStories.get(position).getTitle());
        Glide.with(mActivity)
                .load(mStories.get(position).getImages().get(0))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.mNewsImage);
        if (mClicklistener != null) {
            holder.mCvZhihuItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClicklistener.onItemClick(mStories.get(position));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mStories.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.news_image)
        ImageView mNewsImage;
        @BindView(R.id.news_title)
        TextView mNewsTitle;
        @BindView(R.id.cv_zhihu_item)
        CardView mCvZhihuItem;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void addItem(ArrayList<ZhihuStory> stories) {
        int position = mStories.size();
        mStories.addAll(stories);
        notifyItemInserted(position);
    }

    public interface ItemClicklistener {
        void onItemClick(ZhihuStory story);
    }

    private ItemClicklistener mClicklistener;

    public void addOnItemClickListener(ItemClicklistener clicklistener) {
        mClicklistener = clicklistener;
    }
}
