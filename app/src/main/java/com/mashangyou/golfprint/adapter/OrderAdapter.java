package com.mashangyou.golfprint.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mashangyou.golfprint.R;
import com.mashangyou.golfprint.bean.res.OrderListRes;
import com.mashangyou.golfprint.bean.res.OrderRes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Administrator on 2020/9/10.
 * Des:
 */
public class OrderAdapter extends BaseQuickAdapter<OrderListRes.Order,BaseViewHolder> {

    private final SimpleDateFormat format;

    public OrderAdapter(int layoutResId, @Nullable List<OrderListRes.Order> data) {
        super(layoutResId, data);
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, OrderListRes.Order consumeRes) {
        baseViewHolder.setText(R.id.tv_order_id, consumeRes.getOrderId())
                .setText(R.id.tv_date, consumeRes.getDateTime())
                .setText(R.id.tv_people, consumeRes.getPeoples());
        View view = baseViewHolder.getView(R.id.ll_bg);
        if (baseViewHolder.getLayoutPosition()%2==1){
            view.setBackgroundResource(R.color.color_transparent);
        }else{
            view.setBackgroundResource(R.color.color_main_bg_alpha_20);
        }
    }
}
