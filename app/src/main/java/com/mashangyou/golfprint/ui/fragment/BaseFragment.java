package com.mashangyou.golfprint.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mashangyou.golfprint.widget.LoadingDialog;
import com.trello.rxlifecycle2.components.support.RxFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2020/10/26.
 * Des:
 */
public abstract class BaseFragment extends RxFragment {
    protected Context mContext;
    Unbinder unbinder;
    private LoadingDialog loadingDialog;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void showLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            return;
        }
        loadingDialog = new LoadingDialog(mContext);
        if (!((Activity)mContext).isFinishing()) loadingDialog.show();
    }

    public void hideLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    protected abstract int getLayoutId();

    protected abstract void initView();

    protected abstract void initData();


}
