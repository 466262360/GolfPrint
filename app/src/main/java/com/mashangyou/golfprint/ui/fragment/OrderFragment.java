package com.mashangyou.golfprint.ui.fragment;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.mashangyou.golfprint.R;
import com.mashangyou.golfprint.adapter.OrderAdapter;
import com.mashangyou.golfprint.api.Contant;
import com.mashangyou.golfprint.api.DefaultObserver;
import com.mashangyou.golfprint.api.RetrofitManager;
import com.mashangyou.golfprint.bean.event.EventScreen;
import com.mashangyou.golfprint.bean.res.OrderListRes;
import com.mashangyou.golfprint.bean.res.ResponseBody;
import com.mashangyou.golfprint.ui.activity.MainActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2020/10/26.
 * Des:
 */
public class OrderFragment extends BaseFragment{
    @BindView(R.id.rv_order)
    RecyclerView rvOrder;
    private OrderAdapter orderAdapter;
    private List<OrderListRes.Order> orderList=new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_order;
    }

    @Override
    protected void initView() {
        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(R.layout.item_order, orderList);
        rvOrder.setLayoutManager(new LinearLayoutManager(getContext()));
        rvOrder.setAdapter(orderAdapter);
    }

    @Override
    protected void initData() {
        if (getActivity()!=null){
            ((MainActivity)getActivity()).setTopTitle(getString(R.string.title_1));
            //EventBus.getDefault().post(new EventScreen(Contant.BANNER));
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        getOrder();
    }

    private void getOrder() {
        RetrofitManager.getApi()
                .getOrder(SPUtils.getInstance().getString(Contant.ACCESS_TOKEN))
                .compose(this.bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<ResponseBody<OrderListRes>>() {
                    @Override
                    public void onSuccess(ResponseBody<OrderListRes> response) {
                        orderList = response.getData().getOrderList();
                        orderAdapter.setList(orderList);

                    }

                    @Override
                    public void onFail(ResponseBody<OrderListRes> response) {
                        ToastUtils.showShort(response.getErrMsg());
                    }

                    @Override
                    public void onError(String s) {

                    }
                });
    }

}
