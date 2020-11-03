package com.mashangyou.golfprint.ui.fragment;

import android.view.View;
import android.widget.RadioGroup;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mashangyou.golfprint.R;
import com.mashangyou.golfprint.adapter.ConsumeAdapter;
import com.mashangyou.golfprint.adapter.TotalRecordAdapter;
import com.mashangyou.golfprint.api.Contant;
import com.mashangyou.golfprint.api.DefaultObserver;
import com.mashangyou.golfprint.api.RetrofitManager;
import com.mashangyou.golfprint.bean.res.CountWriteRes;
import com.mashangyou.golfprint.bean.res.ResponseBody;
import com.mashangyou.golfprint.bean.res.SelectWriteRes;
import com.mashangyou.golfprint.ui.activity.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
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
public class DataFragment extends BaseFragment{
    @BindView(R.id.rv_consume)
    RecyclerView rvConsume;
    @BindView(R.id.rv_record)
    RecyclerView rvRecord;
    @BindView(R.id.rg)
    RadioGroup rg;
    private List<CountWriteRes.Content> currentMonthList=new ArrayList<>();
    private List<CountWriteRes.Content> sevendaysList=new ArrayList<>();
    private List<CountWriteRes.Content> yearsList=new ArrayList<>();
    private List<CountWriteRes.Content> countWriteList;
    private ConsumeAdapter consumeAdapter;
    private List<SelectWriteRes.Record> recordList;
    private TotalRecordAdapter totalRecordAdapter;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_data;
    }

    @Override
    protected void initView() {
        rg.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i) {
                case R.id.rb_week:
                    countWriteList.clear();
                    countWriteList.addAll(sevendaysList);
                    consumeAdapter.setList(countWriteList);
                    break;
                case R.id.rb_month:
                    countWriteList.clear();
                    countWriteList.addAll(currentMonthList);
                    consumeAdapter.setList(countWriteList);
                    break;
                case R.id.rb_year:
                    countWriteList.clear();
                    countWriteList.addAll(yearsList);
                    consumeAdapter.setList(countWriteList);
                    break;
            }
        });
        countWriteList = new ArrayList<>();
        consumeAdapter = new ConsumeAdapter(R.layout.item_consume, countWriteList);
        rvConsume.setLayoutManager(new LinearLayoutManager(mContext));
        rvConsume.setAdapter(consumeAdapter);

        recordList = new ArrayList<>();
        totalRecordAdapter = new TotalRecordAdapter(R.layout.item_total_record, recordList);
        rvRecord.setLayoutManager(new LinearLayoutManager(mContext));
        rvRecord.setAdapter(totalRecordAdapter);
    }

    @Override
    protected void initData() {
        if (getActivity()!=null){
            ((MainActivity)getActivity()).setTopTitle(getString(R.string.title_4));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        countWrite();
        selectWrite();
    }

    private void countWrite() {
        RetrofitManager.getApi()
                .countWrite(SPUtils.getInstance().getString(Contant.ACCESS_TOKEN))
                .compose(this.bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<ResponseBody<CountWriteRes>>() {
                    @Override
                    public void onSuccess(ResponseBody<CountWriteRes> response) {
                        sevendaysList = response.getData().getSevendays();
                        currentMonthList = response.getData().getCurrentMonth();
                        yearsList = response.getData().getThisYear();
                        countWriteList.clear();
                        countWriteList.addAll(sevendaysList);
                        consumeAdapter.setList(countWriteList);

                    }

                    @Override
                    public void onFail(ResponseBody<CountWriteRes> response) {
                        ToastUtils.showShort(response.getErrMsg());
                    }

                    @Override
                    public void onError(String s) {

                    }
                });
    }

    private void selectWrite() {
        RetrofitManager.getApi()
                .selectWrite(SPUtils.getInstance().getString(Contant.ACCESS_TOKEN))
                .compose(this.bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<ResponseBody<SelectWriteRes>>() {
                    @Override
                    public void onSuccess(ResponseBody<SelectWriteRes> response) {
                        recordList = response.getData().getRecord();
                        totalRecordAdapter.setList(recordList);
                    }

                    @Override
                    public void onFail(ResponseBody<SelectWriteRes> response) {
                        ToastUtils.showShort(response.getErrMsg());
                    }

                    @Override
                    public void onError(String s) {

                    }
                });
    }
}
