package com.mashangyou.golfprint.ui.fragment;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.mashangyou.golfprint.R;
import com.mashangyou.golfprint.api.Contant;
import com.mashangyou.golfprint.bean.event.EventFragment;
import com.mashangyou.golfprint.bean.event.EventScreen;
import com.mashangyou.golfprint.bean.event.EventVerifyResult;
import com.mashangyou.golfprint.ui.activity.MainActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.logging.Handler;

import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2020/10/27.
 * Des:
 */
public class VerifyResultFragment extends BaseFragment{
    @BindView(R.id.tv_order_id)
    TextView tvOrderId;
    @BindView(R.id.tv_time)
    TextView tvDate;
    @BindView(R.id.tv_verify)
    TextView tvVerify;
    @BindView(R.id.gp_success)
    Group gpSuccess;
    @BindView(R.id.gp_fail)
    Group gpFail;
    @BindView(R.id.fl_bg)
    FrameLayout flBg;
    @BindView(R.id.iv_verify)
    ImageView ivVerify;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_verify_result;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        if (getActivity()!=null){
            ((MainActivity)getActivity()).setTopTitle(getString(R.string.title_6));
        }
        EventBus.getDefault().post(new EventScreen(Contant.VERIFY));
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void receive(EventVerifyResult data) {
        if (data != null) {
            if (!TextUtils.isEmpty(data.getOrderId())){
                gpSuccess.setVisibility(View.VISIBLE);
                gpFail.setVisibility(View.GONE);
                tvOrderId.setText(getString(R.string.verify_1)+data.getOrderId());
                tvDate.setText(getString(R.string.verify_2)+data.getDate());
                setTextSuccess(tvVerify);
            }else{
                gpSuccess.setVisibility(View.GONE);
                gpFail.setVisibility(View.VISIBLE);
                flBg.setBackgroundResource(R.drawable.shape_verify_fail_bg);
                ivVerify.setImageResource(R.drawable.verify_fail);
                tvVerify.setText(getString(R.string.verify_7));
                setTextFail(tvVerify);
            }


        }
    }

    private void setTextSuccess(TextView textView) {
        LinearGradient mLinearGradient = new LinearGradient(0, 0, textView.getPaint().getTextSize()* textView.getText().length(), 0, Color.parseColor("#65D1A1"), Color.parseColor("#4CA9B7"), Shader.TileMode.CLAMP);
        textView.getPaint().setShader(mLinearGradient);
        textView.invalidate();
    }
    private void setTextFail(TextView textView) {
        LinearGradient mLinearGradient = new LinearGradient(0, 0, textView.getPaint().getTextSize()* textView.getText().length(), 0, Color.parseColor("#F78C45"), Color.parseColor("#E72C68"), Shader.TileMode.CLAMP);
        textView.getPaint().setShader(mLinearGradient);
        textView.invalidate();
    }

    @OnClick({R.id.btn_cancel,R.id.btn_contiune,R.id.btn_home,R.id.btn_contiune_fail})
    void onClick(View view){
       switch (view.getId()){
           case R.id.btn_cancel:
           case R.id.btn_home:
               EventBus.getDefault().post(new EventFragment(Contant.F_ORDER));
               break;
           case R.id.btn_contiune:
           case R.id.btn_contiune_fail:
               EventBus.getDefault().post(new EventFragment(Contant.F_SCANSHOW));
               break;
       }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}
