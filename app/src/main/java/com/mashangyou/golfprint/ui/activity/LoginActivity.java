package com.mashangyou.golfprint.ui.activity;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.mashangyou.golfprint.R;
import com.mashangyou.golfprint.api.Contant;
import com.mashangyou.golfprint.api.DefaultObserver;
import com.mashangyou.golfprint.api.RetrofitManager;
import com.mashangyou.golfprint.bean.res.LoginRes;
import com.mashangyou.golfprint.bean.res.ResponseBody;
import java.util.HashMap;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2020/10/10.
 * Des:
 */
public class LoginActivity extends BaseActivity{
    @BindView(R.id.et_user)
    EditText etUser;
    @BindView(R.id.et_pass_word)
    EditText etPassWord;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.iv_left_bg)
    ImageView leftBg;
    private String mUser;
    private String mPassWord;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initToobar() {

    }

    @Override
    protected void initView() {
        btnLogin.setOnClickListener(view -> {
            if (check())
            login();
        });
        etPassWord.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i== EditorInfo.IME_ACTION_DONE){
                    if (check())
                        login();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void initData() {
        Glide.with(this)
                .load(R.drawable.login_bg_1)
                .transform(new CenterCrop(), new RoundedCorners(ConvertUtils.dp2px(11)))
                .into(leftBg);
        initSpan();
        etUser.setText(SPUtils.getInstance().getString(Contant.USER_NAME));
        etPassWord.setText(SPUtils.getInstance().getString(Contant.PASS_WORD));
        if (!TextUtils.isEmpty(etUser.getText().toString())){
            etUser.setSelection(etUser.getText().toString().length());
        }
    }

    private boolean check(){
        mUser = etUser.getText().toString().trim();
        mPassWord = etPassWord.getText().toString().trim();
        if (TextUtils.isEmpty(mUser)){
            ToastUtils.showShort(getString(R.string.login_4));
            return false;
        }
        if (TextUtils.isEmpty(mPassWord)){
            ToastUtils.showShort(getString(R.string.login_5));
            return false;
        }
        return true;
    }

    private void initSpan() {
        String left = getString(R.string.login_1);
        String right = getString(R.string.login_2);
        SpannableString spannableString = new SpannableString( left+right);
        spannableString.setSpan(new UnderlineSpan(),left.length(),left.length()+right.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this,R.color.color_login_bg)),left.length(),left.length()+right.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvPhone.setText(spannableString);
    }

    private void login() {
        showLoading();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("username",mUser);
        hashMap.put("password",mPassWord);
        RetrofitManager.getApi()
                .login(hashMap)
                .compose(this.bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<ResponseBody<LoginRes>>() {
                    @Override
                    public void onSuccess(ResponseBody<LoginRes> response) {
                        hideLoading();
                        SPUtils.getInstance().put(Contant.ACCESS_TOKEN, response.getData().getToken());
                        SPUtils.getInstance().put(Contant.USER_NAME, mUser);
                        SPUtils.getInstance().put(Contant.PASS_WORD, mPassWord);
                        ActivityUtils.startActivity(MainActivity.class);
                        finish();
                    }

                    @Override
                    public void onFail(ResponseBody<LoginRes> response) {
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
