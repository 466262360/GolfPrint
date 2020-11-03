package com.mashangyou.golfprint.ui.fragment;

import com.mashangyou.golfprint.R;
import com.mashangyou.golfprint.ui.activity.MainActivity;

/**
 * Created by Administrator on 2020/10/26.
 * Des:
 */
public class SellFragment extends BaseFragment{
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sell;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        if (getActivity()!=null){
            ((MainActivity)getActivity()).setTopTitle(getString(R.string.title_3));
        }
    }

}
