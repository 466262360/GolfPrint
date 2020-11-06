package com.mashangyou.golfprint.ui.fragment;

import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.mashangyou.golfprint.R;
import com.mashangyou.golfprint.api.Contant;
import com.mashangyou.golfprint.bean.event.EventErrorCode;
import com.mashangyou.golfprint.bean.event.EventFragment;
import com.mashangyou.golfprint.interfac.OnButtonClick;
import com.mashangyou.golfprint.ui.activity.MainActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2020/10/27.
 * Des:
 */
public class ScanErrorFragment extends BaseFragment {
    @BindView(R.id.tv_message)
    TextView tvMessage;
    @BindView(R.id.iv_member)
    ImageView ivInfo;
    private OnButtonClick onButtonClick;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_error;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        if (getActivity()!=null){
            ((MainActivity)getActivity()).setTopTitle(getString(R.string.title_6));
        }
        EventBus.getDefault().register(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void receive(EventErrorCode eventErrorCode) {
        if (eventErrorCode.getCode()==10002){
            tvMessage.setText(getString(R.string.scan_error_2));
            ivInfo.setImageResource(R.drawable.code_error);
        }else if(eventErrorCode.getCode()==10001){
            tvMessage.setText(getString(R.string.scan_error_1));
            ivInfo.setImageResource(R.drawable.member_fail);
        }
    }

    @OnClick(R.id.btn_back)
    void onClick(){
        EventBus.getDefault().post(new EventFragment(Contant.F_ORDER));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

}
