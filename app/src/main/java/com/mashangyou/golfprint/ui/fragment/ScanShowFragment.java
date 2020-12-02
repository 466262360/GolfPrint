package com.mashangyou.golfprint.ui.fragment;

import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mashangyou.golfprint.R;
import com.mashangyou.golfprint.api.Contant;
import com.mashangyou.golfprint.bean.event.EventErrorCode;
import com.mashangyou.golfprint.bean.event.EventFragment;
import com.mashangyou.golfprint.bean.event.EventScreen;
import com.mashangyou.golfprint.interfac.OnButtonClick;
import com.mashangyou.golfprint.ui.activity.MainActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2020/10/27.
 * Des:
 */
public class ScanShowFragment extends BaseFragment {
    @BindView(R.id.iv_scan)
    ImageView ivScan;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_scan_show;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        if (getActivity()!=null){
            ((MainActivity)getActivity()).setTopTitle(getString(R.string.title_8));
        }
        Glide.with(this).load(R.drawable.scan_show).into(ivScan);
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
