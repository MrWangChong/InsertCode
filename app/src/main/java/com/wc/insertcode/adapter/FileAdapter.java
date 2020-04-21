package com.wc.insertcode.adapter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.wc.insertcode.MainActivity;
import com.wc.insertcode.R;
import com.wc.insertcode.ShowImageActivity;
import com.wc.insertcode.ShowVideoActivity;
import com.wc.insertcode.base.BaseActivity;
import com.wc.insertcode.utils.GlideApp;
import com.wc.qiniu.FileListUtil;
import com.wc.qiniu.read.PictureConfig;
import com.wc.qiniu.read.PictureMimeType;

import java.util.ArrayList;
import java.util.List;


public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {
    private BaseActivity mContext;
    private LayoutInflater mInflater;
    private List<FileListUtil.FileBean> mData;
    private ImageView[] imageViews;
    private boolean isSelecting;

    public FileAdapter(BaseActivity context, List<FileListUtil.FileBean> data) {
        mContext = context;
        mData = data;
        mInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.item_file, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final FileListUtil.FileBean dataBean = mData.get(position);
        holder.checkbox.setVisibility(isSelecting ? View.VISIBLE : View.GONE);
        holder.checkbox.setChecked(dataBean.isSelected);
        if (imageViews == null || imageViews.length != mData.size()) {
            imageViews = new ImageView[mData.size()];
        }
        imageViews[position] = holder.iv_image;
        final boolean is_video = PictureMimeType.isPictureType(dataBean.mimeType) != PictureConfig.TYPE_IMAGE;
        if (is_video) {
            GlideApp.with(mContext).load(R.drawable.icon_video_paly).into(holder.iv_image);
        } else {
            GlideApp.with(mContext).load(MainActivity.BASE_URL + dataBean.key).into(holder.iv_image);
        }

        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dataBean.isSelected = isChecked;
            }
        });
        holder.item_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_video) {
                    Intent intent = new Intent(mContext, ShowVideoActivity.class);
                    intent.putExtra("videoUrl", MainActivity.BASE_URL + dataBean.key);
                    mContext.startActivity(intent);
                    return;
                }
                List<FileListUtil.FileBean> tmpList = new ArrayList<>();
                for (FileListUtil.FileBean fileBean : mData) {
                    if (PictureMimeType.isPictureType(dataBean.mimeType) == PictureConfig.TYPE_IMAGE) {
                        tmpList.add(fileBean);
                    }
                }
                String[] images = new String[tmpList.size()];
                for (int i = 0; i < images.length; i++) {
                    images[i] = MainActivity.BASE_URL + tmpList.get(i).key;
                }
                ShowImageActivity.startImageActivity(mContext, imageViews, images, position, false, holder.iv_image.getDrawable());
            }
        });
        holder.item_view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isSelecting = !isSelecting;
                mContext.onChange(isSelecting);
                notifyDataSetChanged();
                return true;
            }
        });
    }

    public void setSelecting(boolean selecting) {
        isSelecting = selecting;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_image;
        CheckBox checkbox;
        View item_view;

        ViewHolder(View itemView) {
            super(itemView);
            item_view = itemView;
            iv_image = itemView.findViewById(R.id.iv_image);
            checkbox = itemView.findViewById(R.id.checkbox);
        }
    }
}

