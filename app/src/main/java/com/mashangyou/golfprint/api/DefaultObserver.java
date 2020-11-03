package com.mashangyou.golfprint.api;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.mashangyou.golfprint.bean.res.ResponseBody;
import com.mashangyou.golfprint.ui.activity.LoginActivity;


import java.io.IOException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


public abstract class DefaultObserver<T> implements Observer<T> {

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(T response) {
        try {
            if (response instanceof ResponseBody) {
                if (0 == ((ResponseBody) response).getCode()) {
                    onSuccess(response);
                } else if (401 == ((ResponseBody) response).getCode()) {
                    ActivityUtils.finishAllActivities();
                    ActivityUtils.startActivity(LoginActivity.class);
                } else {
                    onFail(response);
                }
            } else {
                ToastUtils.showShort("ERROR");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(Throwable e) {
        ToastUtils.showShort(e.toString());
        onError(e.toString());
        if (e.toString().contains("401")) {
            ActivityUtils.finishAllActivities();
            ActivityUtils.startActivity(LoginActivity.class);
        }

    }

    @Override
    public void onComplete() {
    }

    /**
     * 请求成功
     *
     * @param response 服务器返回的数据
     */
    abstract public void onSuccess(T response) throws IOException;

    /**
     * 返回失败
     *
     * @param response 服务器返回的数据
     */
    abstract public void onFail(T response);

    abstract public void onError(String s);



}
