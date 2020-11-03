package com.mashangyou.golfprint.adapter;

import android.view.View;

import com.blankj.utilcode.util.TimeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mashangyou.golfprint.R;
import com.mashangyou.golfprint.bean.res.SelectWriteRes;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2020/9/10.
 * Des:
 */
public class TotalRecordAdapter extends BaseQuickAdapter<SelectWriteRes.Record,BaseViewHolder> {

    private final SimpleDateFormat format;

    public TotalRecordAdapter(int layoutResId, @Nullable List<SelectWriteRes.Record> data) {
        super(layoutResId, data);
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, SelectWriteRes.Record consumeRes) {
        baseViewHolder.setText(R.id.tv_date, getDate(consumeRes.getDate()))
                .setText(R.id.tv_member_name, consumeRes.getMemberName())
                .setText(R.id.tv_frequ, consumeRes.getFrequ()+"");
        View view = baseViewHolder.getView(R.id.ll_bg);
        if (baseViewHolder.getLayoutPosition()%2==1){
            view.setBackgroundResource(R.color.color_transparent);
        }else{
            view.setBackgroundResource(R.color.color_main_bg_alpha_20);
        }
    }
    private String getDate(String playTime) {
        Date date = TimeUtils.string2Date(playTime);
        return  format.format(date);
    }
}
