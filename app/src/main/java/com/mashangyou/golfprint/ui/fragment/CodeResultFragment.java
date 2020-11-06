package com.mashangyou.golfprint.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.mashangyou.golfprint.R;
import com.mashangyou.golfprint.adapter.OrderMemberAdapter;
import com.mashangyou.golfprint.api.Contant;
import com.mashangyou.golfprint.api.DefaultObserver;
import com.mashangyou.golfprint.api.RetrofitManager;
import com.mashangyou.golfprint.bean.event.EventFragment;
import com.mashangyou.golfprint.bean.event.EventPrint;
import com.mashangyou.golfprint.bean.event.EventRvOrderId;
import com.mashangyou.golfprint.bean.event.EventRvOrderY;
import com.mashangyou.golfprint.bean.event.EventScreen;
import com.mashangyou.golfprint.bean.event.EventVerifyResult;
import com.mashangyou.golfprint.bean.res.ResponseBody;
import com.mashangyou.golfprint.bean.res.VerifyRes;
import com.mashangyou.golfprint.interfac.OnButtonClick;
import com.mashangyou.golfprint.ui.activity.MainActivity;
import com.mashangyou.golfprint.util.SerializableMap;
import com.mashangyou.golfprint.util.USBPrinter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2020/10/27.
 * Des:
 */
public class CodeResultFragment extends BaseFragment {
    @BindView(R.id.gp_none)
    Group gpNone;
    @BindView(R.id.gp_list)
    Group gpList;
    @BindView(R.id.rv_order)
    RecyclerView rvOrder;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_id)
    TextView tvId;
    @BindView(R.id.tv_card)
    TextView tvCard;
    @BindView(R.id.iv_member)
    ImageView ivMember;
    private VerifyRes userInfo;
    List<VerifyRes.Orders> ordersList;
    private VerifyRes.Orders currentOrders;
    private String orderId;
    private OnButtonClick onButtonClick;
    private SimpleDateFormat format;
    private OrderMemberAdapter orderAdapter;
    private boolean isUse;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_code_result;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if (getActivity() != null) {
            ((MainActivity) getActivity()).setTopTitle(getString(R.string.title_6));
        }
        EventBus.getDefault().post(new EventScreen(Contant.CODE_RESULT));
        isUse=true;
    }

    @OnClick({R.id.btn_cancel, R.id.btn_confirm, R.id.btn_back})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
            case R.id.btn_back:
                EventBus.getDefault().post(new EventFragment(Contant.F_ORDER));
                EventBus.getDefault().post(new EventScreen(Contant.BANNER));
                break;
            case R.id.btn_confirm:
                if (!TextUtils.isEmpty(orderId) && currentOrders != null&&isUse) {
                    use();
                } else {
                    ToastUtils.showShort(getString(R.string.code_result_17));
                }
                break;
        }
    }

    private void use() {
        isUse =false;
        showLoading();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("orderId", orderId);
        hashMap.put("token", SPUtils.getInstance().getString(Contant.ACCESS_TOKEN));
        RetrofitManager.getApi()
                .use(hashMap)
                .compose(this.bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody response) {
                        hideLoading();
                        isUse =true;
                        sendPrint();
                        EventBus.getDefault().postSticky(new EventVerifyResult(orderId, getDate(currentOrders.getPlayTime())));
                        EventBus.getDefault().post(new EventFragment(Contant.F_VERIFY));
                    }

                    @Override
                    public void onFail(ResponseBody response) {
                        hideLoading();
                        isUse =true;
                        EventBus.getDefault().postSticky(new EventVerifyResult(null, null));
                        EventBus.getDefault().post(new EventFragment(Contant.F_VERIFY));
                    }

                    @Override
                    public void onError(String s) {
                        hideLoading();
                        isUse =true;
                    }
                });
    }

    private void sendPrint() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Contant.PRINT_NAME, userInfo.getName());
        hashMap.put(Contant.PRINT_ID, userInfo.getTcode());
        hashMap.put(Contant.PRINT_MEMBER_NAME, userInfo.getMemberName());
        hashMap.put(Contant.PRINT_DATE, getDate(currentOrders.getPlayTime()));
        hashMap.put(Contant.PRINT_PEOPLE, currentOrders.getPeoples());
        hashMap.put(Contant.PRINT_CAVES, currentOrders.getCaves());
        hashMap.put(Contant.PRINT_ORDER, currentOrders.getOrderId());
        hashMap.put(Contant.PRINT_GOLFNAME, currentOrders.getGolfName());
        hashMap.put(Contant.PRINT_FREQUENCY, currentOrders.getFrequency());
        hashMap.put(Contant.PRINT_CURRENT_DATE, TimeUtils.getNowString(format));
//        SerializableMap map = new SerializableMap();
//        map.setMap(hashMap);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable(Contant.PRINT_MAP, map);
//        Intent intent = new Intent();
//        intent.setAction(Contant.PRINT_ACTION);
//        intent.putExtra(Contant.PRINT_MAP, bundle);
//        mContext.sendBroadcast(intent);
        EventBus.getDefault().post(new EventPrint(hashMap));
    }



    @Subscribe(threadMode = ThreadMode.POSTING, sticky = true)
    public void receive(VerifyRes data) {
        if (data != null) {
            ordersList=new ArrayList<>();
            userInfo = data;
            tvName.setText(getString(R.string.code_result_2) + userInfo.getName());
            tvId.setText(getString(R.string.code_result_3) + userInfo.getTcode());
            tvCard.setText(getString(R.string.code_result_4) + userInfo.getMemberName());
            Glide.with(this)
                    .load(userInfo.getImg())
                    .transform(new CenterCrop(), new RoundedCorners(ConvertUtils.dp2px(12)))
                    .into(ivMember);
            ordersList.clear();
            ordersList = userInfo.getOrders();
            if (ordersList != null && ordersList.size() > 0) {
                gpList.setVisibility(View.VISIBLE);
                gpNone.setVisibility(View.GONE);
                initRv();
            } else {
                gpList.setVisibility(View.GONE);
                gpNone.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initRv() {
        orderAdapter = new OrderMemberAdapter(R.layout.item_order_member, ordersList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        rvOrder.setLayoutManager(linearLayoutManager);
        rvOrder.setAdapter(orderAdapter);
        orderAdapter.setOnItemClickListener((adapter, view, position) -> {
            for (VerifyRes.Orders item : ordersList) {
                item.setSel(false);
            }
            ordersList.get(position).setSel(true);
            currentOrders = ordersList.get(position);
            orderId = ordersList.get(position).getOrderId();
            if (orderId != null) {
                EventBus.getDefault().post(new EventRvOrderId(orderId));
            }
            orderAdapter.notifyDataSetChanged();
        });

        rvOrder.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (recyclerView.getScrollState() != RecyclerView.SCROLL_STATE_IDLE) {
                    EventBus.getDefault().post(new EventRvOrderY(dy, rvOrder.getMeasuredHeight()));
                }
            }
        });

    }

    private String getDate(String playTime) {
        Date date = TimeUtils.string2Date(playTime);
        return format.format(date);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}
