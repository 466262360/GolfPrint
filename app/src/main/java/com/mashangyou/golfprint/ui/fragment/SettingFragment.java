package com.mashangyou.golfprint.ui.fragment;

import android.view.View;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.mashangyou.golfprint.R;
import com.mashangyou.golfprint.api.Contant;
import com.mashangyou.golfprint.api.DefaultObserver;
import com.mashangyou.golfprint.api.RetrofitManager;
import com.mashangyou.golfprint.bean.res.ResponseBody;
import com.mashangyou.golfprint.ui.activity.LoginActivity;
import com.mashangyou.golfprint.ui.activity.MainActivity;

import java.util.HashMap;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2020/10/26.
 * Des:
 */
public class SettingFragment extends BaseFragment{
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_setting;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        if (getActivity()!=null){
            ((MainActivity)getActivity()).setTopTitle(getString(R.string.title_5));
        }
    }

    @OnClick({R.id.cl_1,R.id.btn_exit})
    void onClick(View view){
        switch (view.getId()){
            case R.id.cl_1:
                if (getActivity()!=null){
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.replace(R.id.fl_content,new PassWordFragment()).commit();
                }
                break;
            case R.id.btn_exit:
                quit();
                break;
        }
    }

    private void quit() {
        showLoading();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("token", SPUtils.getInstance().getString(Contant.ACCESS_TOKEN));
        RetrofitManager.getApi()
                .quit(hashMap)
                .compose(this.bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody response) {
                        hideLoading();
                        SPUtils.getInstance().put(Contant.ACCESS_TOKEN, "");
                        SPUtils.getInstance().put(Contant.USER_NAME, "");
                        SPUtils.getInstance().put(Contant.PASS_WORD, "");
                        ActivityUtils.startActivity(LoginActivity.class);
                        ActivityUtils.finishAllActivities();
                    }

                    @Override
                    public void onFail(ResponseBody response) {
                        hideLoading();
                        ToastUtils.showShort(response.getErrMsg());
                    }

                    @Override
                    public void onError(String s) {
                        hideLoading();
                    }
                });
    }
}
