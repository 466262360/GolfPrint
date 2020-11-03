package com.mashangyou.golfprint.adapter;

import android.view.ViewGroup;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mashangyou.golfprint.R;
import com.mashangyou.golfprint.bean.res.VerifyRes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

/**
 * Created by Administrator on 2020/9/10.
 * Des:
 */
public class OrderMemberAdapter extends BaseQuickAdapter<VerifyRes.Orders, BaseViewHolder> {

    private SimpleDateFormat format;

    public OrderMemberAdapter(int layoutResId, @Nullable List<VerifyRes.Orders> data) {
        super(layoutResId, data);
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    }



    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, VerifyRes.Orders consumeRes) {
        baseViewHolder.setText(R.id.tv_order_id, getContext().getString(R.string.code_result_13) + consumeRes.getOrderId())
                .setText(R.id.tv_people, getContext().getString(R.string.code_result_14) + consumeRes.getPeoples())
                .setText(R.id.tv_caves, getContext().getString(R.string.code_result_15) + consumeRes.getCaves())
                .setText(R.id.tv_date, getContext().getString(R.string.code_result_16) + getDate(consumeRes.getPlayTime()));
        if (consumeRes.isSel()) {
            baseViewHolder.setBackgroundResource(R.id.tv_date, R.drawable.shape_order_bottom_true)
                    .setBackgroundResource(R.id.iv_sel, R.drawable.order_sel_true)
                    .setTextColor(R.id.tv_date, ContextCompat.getColor(getContext(),R.color.color_text_5));
        } else {
            baseViewHolder.setBackgroundResource(R.id.tv_date, R.drawable.shape_order_bottom_false)
                    .setBackgroundResource(R.id.iv_sel, R.drawable.order_sel_false)
                    .setTextColor(R.id.tv_date, ContextCompat.getColor(getContext(),R.color.color_text_6));
        }
        ConstraintLayout view = baseViewHolder.getView(R.id.cl_item);
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = getRecyclerView().getMeasuredHeight() / 3;
        view.setLayoutParams(params);
    }

    private String getDate(String playTime) {
        Date date = TimeUtils.string2Date(playTime);
        return  format.format(date);


    }
}
