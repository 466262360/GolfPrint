package com.mashangyou.golfprint.widget;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ConvertUtils;
import com.mashangyou.golfprint.R;
import com.mashangyou.golfprint.bean.res.AppVersion;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author lulei
 *         Created 2017/12/1 15:17
 * @copyright Copyright (c) 2007-2017 ShopNC Inc. All rights reserved.
 * @license http://www.shopnc.net
 * @link http://www.shopnc.net
 * <p>
 * 自定义仿IOS选择弹出窗
 */
public class UpdateAppDialog extends Dialog {
    @BindView(R.id.tv1)
    TextView tv1;
    @BindView(R.id.tvVersion)
    TextView tvVersion;
    @BindView(R.id.tvContent)
    TextView tvContent;
    @BindView(R.id.ivDelete)
    ImageView ivDelete;

    private final AppVersion appVersion;
    Context context;
    OnDownClickListener onDownClickListener;
    OnCancelClickListener onCancelClickListener;


    public UpdateAppDialog(Context context, AppVersion appVersion) {
        super(context, R.style.CustomDialog);
        this.context = context;
        this.appVersion = appVersion;
    }

    public interface OnDownClickListener {
        void onDownClick();
    }
    public interface OnCancelClickListener {
        void onCancelClick();
    }

    public void setOnDownListener(OnDownClickListener onDownClickListener) {
        this.onDownClickListener = onDownClickListener;
    }

    public void setOnCancelListener(OnCancelClickListener onCancelClickListener) {
        this.onCancelClickListener = onCancelClickListener;
    }

    public void setContent(String text) {
        tvContent.setText(text);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_update_app, null);
        setContentView(view, new LinearLayout.LayoutParams(
                ConvertUtils.dp2px(315), LinearLayout.LayoutParams.WRAP_CONTENT));
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        ButterKnife.bind(this);
        if (appVersion.getIsUpdate()==1){
            tv1.setText(context.getResources().getString(R.string.dialog_update_app3));
            tvVersion.setVisibility(View.GONE);
            ivDelete.setVisibility(View.GONE);
            tvContent.setText(context.getResources().getString(R.string.dialog_update_app4));
        }else {
            tv1.setText(context.getResources().getString(R.string.dialog_update_app1));
            tvVersion.setVisibility(View.GONE);
            ivDelete.setVisibility(View.GONE);
            tvVersion.setText("(V"+appVersion.getVersion()+")");
            tvContent.setText(appVersion.getUpdateContent());
        }
    }

    @OnClick({R.id.ivDelete, R.id.tvDown})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvDown:
                if (onDownClickListener != null) {
                    onDownClickListener.onDownClick();
                }
                    dismiss();
                break;
            case R.id.ivDelete:
                if (onCancelClickListener != null) {
                    onCancelClickListener.onCancelClick();
                }
                dismiss();
                break;
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

}
