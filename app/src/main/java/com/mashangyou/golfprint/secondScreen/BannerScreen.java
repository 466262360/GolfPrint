package com.mashangyou.golfprint.secondScreen;

import android.app.Presentation;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;

import com.mashangyou.golfprint.R;
import com.mashangyou.golfprint.adapter.BannerAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import androidx.viewpager.widget.ViewPager;

/**
 * Created by Administrator on 2020/10/29.
 * Des:
 */
public class BannerScreen extends Presentation {
    private ViewPager mVp;
    private MyHandler mHandler;

    public BannerScreen(Context outerContext, Display display) {
        super(outerContext, display);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_banner);
        mVp =findViewById(R.id.vp_banner);
        initBanner();
        if (mHandler == null) {
            mHandler = new MyHandler();
        }
        mHandler.startAuto();
    }

    public class MyHandler extends Handler implements Runnable {

        @Override
        public void run() {
            mVp.setCurrentItem(mVp.getCurrentItem() + 1, false);
            postDelayed(this, 3000);
        }

        //    开启自动轮播
        public void startAuto() {
            //防止开启两次task
            removeCallbacks(this);
            postDelayed(this, 3000);
        }

        //    停止自动轮播
        public void stopAuto() {
            removeCallbacks(this);
        }
    }

    private void initBanner() {
        ArrayList<Integer> strings = new ArrayList<>();
        strings.add(R.drawable.bs_1);
        strings.add(R.drawable.bs_2);
        strings.add(R.drawable.bs_3);
        BannerAdapter bannerAdapter = new BannerAdapter();
        bannerAdapter.setData(strings);
        if (bannerAdapter.getDataSize() == 0) {
            return;
        }
        int m = (Integer.MAX_VALUE / 2) % bannerAdapter.getDataSize();
        int currentPosition = Integer.MAX_VALUE / 2 - m;
        mVp.setCurrentItem(currentPosition);
        mVp.setAdapter(bannerAdapter);
    }

    @Override
    public void show() {
        super.show();

    }

    /**  EventBus解注册  */
    @Override
    public void dismiss() {
        mHandler.stopAuto();
        super.dismiss();
    }
}
