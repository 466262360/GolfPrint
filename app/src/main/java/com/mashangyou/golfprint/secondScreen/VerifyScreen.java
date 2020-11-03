package com.mashangyou.golfprint.secondScreen;

import android.app.Presentation;
import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.mashangyou.golfprint.R;
import com.mashangyou.golfprint.bean.event.EventVerifyResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.constraintlayout.widget.Group;

/**
 * Created by Administrator on 2020/10/30.
 * Des:
 */
public class VerifyScreen extends Presentation {

    private final Context context;
    private Group gpSuccess;
    private Group gpFail;
    private TextView tvOrderId;
    private TextView tvDate;
    private TextView tvVerify;
    private ImageView ivVerify;
    private FrameLayout flBg;

    public VerifyScreen(Context outerContext, Display display) {
        super(outerContext, display);
        context =outerContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_verify);
        initView();
    }

    private void initView() {
        gpSuccess = findViewById(R.id.gp_success);
        gpFail = findViewById(R.id.gp_fail);
        tvOrderId = findViewById(R.id.tv_order_id);
        tvDate = findViewById(R.id.tv_time);
        ivVerify = findViewById(R.id.iv_verify);
        flBg = findViewById(R.id.fl_bg);
        tvVerify = findViewById(R.id.tv_verify);
    }

    @Subscribe(threadMode = ThreadMode.POSTING, sticky = true)
    public void receive(EventVerifyResult data) {
        if (data != null) {
            if (!TextUtils.isEmpty(data.getOrderId())){
                gpSuccess.setVisibility(View.VISIBLE);
                gpFail.setVisibility(View.GONE);
                tvOrderId.setText(context.getString(R.string.verify_1)+data.getOrderId());
                tvDate.setText(context.getString(R.string.verify_2)+data.getDate());
                setTextSuccess(tvVerify);
            }else{
                gpSuccess.setVisibility(View.GONE);
                gpFail.setVisibility(View.VISIBLE);
                flBg.setBackgroundResource(R.drawable.shape_verify_fail_bg);
                ivVerify.setImageResource(R.drawable.verify_fail);
                tvVerify.setText(context.getString(R.string.verify_7));
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
    @Override
    public void show() {
        super.show();
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
    }

    /**  EventBus解注册  */
    @Override
    public void dismiss() {
        EventBus.getDefault().unregister(this);
        super.dismiss();
    }
}
