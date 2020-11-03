package com.mashangyou.golfprint.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mashangyou.golfprint.R;
import com.mashangyou.golfprint.bean.res.PublishInfoRes;
import com.mashangyou.golfprint.bean.res.PublishRes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Administrator on 2020/9/10.
 * Des:
 */
public class PublishHorAdapter extends BaseQuickAdapter<PublishInfoRes, BaseViewHolder> {

    private final SimpleDateFormat format;

    public PublishHorAdapter(int layoutResId, @Nullable List<PublishInfoRes> data) {
        super(layoutResId, data);
        format = new SimpleDateFormat("HH:mm");
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, PublishInfoRes data) {
        baseViewHolder.setText(R.id.tv_time, format.format(new Date(data.getOpentime())));
        switch (data.getSalestatus()) {
            case "0":
                baseViewHolder.setText(R.id.tv_bg, "")
                        .setTextColorRes(R.id.tv_time, R.color.color_text_6)
                        .setBackgroundResource(R.id.cl_bg, R.drawable.shape_publish_2);
                break;
            case "1":
                baseViewHolder.setText(R.id.tv_bg, getContext().getString(R.string.publish_6))
                        .setTextColor(R.id.tv_bg, R.color.color_stroke_1_alpha_24)
                        .setTextColorRes(R.id.tv_time, R.color.color_white)
                        .setBackgroundResource(R.id.cl_bg, R.drawable.shape_publish_1);
                break;
            case "2":
                baseViewHolder.setText(R.id.tv_bg, getContext().getString(R.string.publish_7))
                        .setTextColor(R.id.tv_bg, R.color.color_btn_alpha_24)
                        .setTextColorRes(R.id.tv_time, R.color.color_white)
                        .setBackgroundResource(R.id.cl_bg, R.drawable.shape_publish_3);
                break;
            case "3":
                baseViewHolder.setText(R.id.tv_bg, getContext().getString(R.string.publish_9))
                        .setTextColor(R.id.tv_bg, R.color.color_stroke_alpha_40)
                        .setTextColorRes(R.id.tv_time, R.color.color_stroke_4)
                        .setBackgroundResource(R.id.cl_bg, R.drawable.shape_publish_4);
                break;
        }


    }
}
