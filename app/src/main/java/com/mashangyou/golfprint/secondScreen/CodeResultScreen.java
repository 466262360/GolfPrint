package com.mashangyou.golfprint.secondScreen;

import android.app.Presentation;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.LogUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.mashangyou.golfprint.R;
import com.mashangyou.golfprint.adapter.OrderMemberAdapter;
import com.mashangyou.golfprint.bean.event.EventRvOrderId;
import com.mashangyou.golfprint.bean.event.EventRvOrderY;
import com.mashangyou.golfprint.bean.res.VerifyRes;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Administrator on 2020/10/29.
 * Des:
 */
public class CodeResultScreen extends Presentation {
    private final Context context;
    private VerifyRes userInfo;
    private VerifyRes.Orders currentOrders;
    private String orderId;
    List<VerifyRes.Orders> ordersList = new ArrayList<>();
    private RecyclerView rvOrder;
    private TextView tvName;
    private TextView tvId;
    private TextView tvCard;
    private TextView tvNone;
    private ImageView ivMember;
    private OrderMemberAdapter orderAdapter;

    public CodeResultScreen(Context outerContext, Display display) {
        super(outerContext, display);
        context =outerContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_code_result);
        initView();
        initData();
    }

    private void initView() {
        rvOrder = findViewById(R.id.rv_order);
        tvName = findViewById(R.id.tv_name);
        tvId = findViewById(R.id.tv_id);
        tvCard = findViewById(R.id.tv_card);
        ivMember = findViewById(R.id.iv_member);
        tvNone = findViewById(R.id.tv_none);
    }

    private void initData() {

    }

    private void initRv() {
        orderAdapter = new OrderMemberAdapter(R.layout.item_order_member, ordersList);
        rvOrder.setLayoutManager(new LinearLayoutManager(getContext()));
        rvOrder.setAdapter(orderAdapter);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void receiveEvent(EventRvOrderY eventRvOrder){
        int y=rvOrder.getMeasuredHeight()*eventRvOrder.getY()/eventRvOrder.getH();
        rvOrder.scrollBy(0,y);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void receiveEvent(EventRvOrderId eventRvOrderId){
        if (!TextUtils.isEmpty(eventRvOrderId.getId())){
            for (VerifyRes.Orders item : ordersList) {
                if (item.getOrderId().equals(eventRvOrderId.getId())){
                    item.setSel(true);
                }else{
                    item.setSel(false);
                }

            }
            orderAdapter.notifyDataSetChanged();
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING, sticky = true)
    public void receive(VerifyRes data) {
        if (data != null) {
            userInfo = data;
            tvName.setText(context.getString(R.string.code_result_2) + userInfo.getName());
            tvId.setText(context.getString(R.string.code_result_3) + userInfo.getTcode());
            tvCard.setText(context.getString(R.string.code_result_4) + userInfo.getMemberName());
            Glide.with(context)
                    .load(userInfo.getImg())
                    .transform(new CenterCrop(), new RoundedCorners(ConvertUtils.dp2px(12)))
                    .into(ivMember);

            ordersList = userInfo.getOrders();
            if (ordersList != null && ordersList.size() > 0) {
                rvOrder.setVisibility(View.VISIBLE);
                tvNone.setVisibility(View.GONE);
                initRv();
            } else {
                rvOrder.setVisibility(View.GONE);
                tvNone.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void show() {
        super.show();
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
    }

    /**  EventBus解注册  */
    @Override
    public void dismiss() {
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
        super.dismiss();
    }
}
