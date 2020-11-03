package com.mashangyou.golfprint.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mashangyou.golfprint.R;
import com.mashangyou.golfprint.bean.res.CountWriteRes;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by Administrator on 2020/9/10.
 * Des:
 */
public class ConsumeAdapter extends BaseQuickAdapter<CountWriteRes.Content, BaseViewHolder> {

    public ConsumeAdapter(int layoutResId, @Nullable List<CountWriteRes.Content> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, CountWriteRes.Content consumeRes) {
        baseViewHolder.setText(R.id.tv_member_name, consumeRes.getMemberName())
                .setText(R.id.tv_peoples, consumeRes.getPeoples()+"")
                .setText(R.id.tv_frequ, consumeRes.getFrequ()+"");
        View view = baseViewHolder.getView(R.id.ll_bg);
        if (baseViewHolder.getLayoutPosition()%2==1){
            view.setBackgroundResource(R.color.color_transparent);
        }else{
            view.setBackgroundResource(R.color.color_main_bg_alpha_20);
        }
    }
}
