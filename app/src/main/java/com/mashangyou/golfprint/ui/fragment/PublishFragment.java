package com.mashangyou.golfprint.ui.fragment;

import android.app.DatePickerDialog;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.mashangyou.golfprint.R;
import com.mashangyou.golfprint.adapter.PublishAdapter;
import com.mashangyou.golfprint.api.Contant;
import com.mashangyou.golfprint.api.DefaultObserver;
import com.mashangyou.golfprint.api.RetrofitManager;
import com.mashangyou.golfprint.bean.event.EventCode;
import com.mashangyou.golfprint.bean.event.EventScreen;
import com.mashangyou.golfprint.bean.res.PublishInfoRes;
import com.mashangyou.golfprint.bean.res.PublishListRes;
import com.mashangyou.golfprint.bean.res.PublishRes;
import com.mashangyou.golfprint.bean.res.ResponseBody;
import com.mashangyou.golfprint.scan.Code;
import com.mashangyou.golfprint.ui.activity.MainActivity;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2020/10/26.
 * Des:
 */
public class PublishFragment extends BaseFragment implements DatePickerDialog.OnDateSetListener {
    @BindView(R.id.rv_publish)
    RecyclerView rvPublish;
    @BindView(R.id.tv_year)
    TextView tvYear;
    @BindView(R.id.tv_month)
    TextView tvMonth;
    @BindView(R.id.tv_day)
    TextView tvDay;
    @BindView(R.id.btn_search)
    Button btnSearch;
    private int year;
    private int month;
    private int day;
    private DatePickerDialog pickerDialog;
    private PublishAdapter publishAdapter;
    private List<PublishRes.Publish> publishList;
    private SimpleDateFormat simpleDateFormat;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_publish;
    }

    @Override
    protected void initView() {
        publishList = new ArrayList<>();
        publishAdapter = new PublishAdapter(R.layout.item_publish, publishList);
        rvPublish.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPublish.setAdapter(publishAdapter);
    }

    @Override
    protected void initData() {
        if (getActivity() != null) {
            ((MainActivity) getActivity()).setTopTitle(getString(R.string.title_2));
        }
        simpleDateFormat = new SimpleDateFormat("HH");
        Calendar instance = Calendar.getInstance();
        year = instance.get(Calendar.YEAR);
        month = instance.get(Calendar.MONTH);
        day = instance.get(Calendar.DAY_OF_MONTH);
        setDate();
        query();
    }

    private void query() {
        showLoading();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("datatime", year + "-" + appendZero(month+1) + "-" + appendZero(day));
        hashMap.put("token", SPUtils.getInstance().getString(Contant.ACCESS_TOKEN));
        RetrofitManager.getApi()
                .query(hashMap)
                .compose(this.bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<ResponseBody<PublishListRes>>() {
                    @Override
                    public void onSuccess(ResponseBody<PublishListRes> response) {
                        hideLoading();
                        publishList.clear();
                        getList(response.getData().getList());
                    }

                    @Override
                    public void onFail(ResponseBody<PublishListRes> response) {
                        hideLoading();
                        ToastUtils.showShort(response.getErrMsg());
                    }

                    @Override
                    public void onError(String s) {
                        hideLoading();
                    }
                });

    }

    private void getList(List<PublishInfoRes> list) {
        Calendar instance = Calendar.getInstance();
        Map<String, List<PublishInfoRes>> skuIdMap = new HashMap<>();
        for (PublishInfoRes item : list) {
            instance.setTimeInMillis(item.getOpentime());
            List<PublishInfoRes> tempList = skuIdMap.get(simpleDateFormat.format(instance.getTimeInMillis()));
            if (tempList == null) {
                tempList = new ArrayList<>();
                tempList.add(item);
                skuIdMap.put(simpleDateFormat.format(instance.getTimeInMillis()), tempList);
            } else {
                tempList.add(item);
            }
        }
        Iterator<String> iterator = skuIdMap.keySet().iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            PublishRes.Publish item = new PublishRes.Publish();
            item.setTitle(next);
            List<PublishInfoRes> infoRes = skuIdMap.get(next);
            if (infoRes != null) {
                Collections.sort(infoRes);
            }
            item.setInfos(infoRes);
            publishList.add(item);
            Collections.sort(publishList);
        }
        publishAdapter.setList(publishList);
    }

    @OnClick({R.id.tv_year, R.id.tv_month, R.id.tv_day, R.id.btn_search})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_year:
            case R.id.tv_month:
            case R.id.tv_day:
                showDateDialog();
                break;
            case R.id.btn_search:
                query();
                break;
        }
    }

    private void showDateDialog() {
        pickerDialog = new DatePickerDialog(mContext, this, year, month, day);
        pickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        pickerDialog.dismiss();
        year = i;
        month = i1;
        day = i2;
        setDate();
        query();
    }

    private void setDate() {
        tvYear.setText(year + "");
        tvMonth.setText(appendZero(month+1));
        tvDay.setText(appendZero(day));
    }

    private String appendZero(int date) {
        if (date < 10) {
            return "0" + date;
        } else {
            return "" + date;
        }
    }
}
