package com.mashangyou.golfprint.widget;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blankj.utilcode.util.ConvertUtils;
import com.mashangyou.golfprint.R;


import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author lulei
 *         Created 2017/12/1 15:17
 * @copyright Copyright (c) 2007-2017 ShopNC Inc. All rights reserved.
 * @license http://www.shopnc.net
 * @link http://www.shopnc.net
 * <p>
 * 自定义仿IOS选择弹出窗
 */
public class UpdateProgressDialog extends Dialog {
    @BindView(R.id.pb_download)
    ProgressBar mPbDownload;
    @BindView(R.id.tv_progress)
    TextView mTvProgress;

    Context context;



    public UpdateProgressDialog(Context context) {
        super(context, R.style.CustomDialog);
        this.context = context;

    }

    public void setProgress(int progress) {
        if (progress==100){
            dismiss();
        }else{
            mPbDownload.setProgress(progress);
            mTvProgress.setText(progress+"%");
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_update_progress, null);
        setContentView(view, new LinearLayout.LayoutParams(
                ConvertUtils.dp2px(315), LinearLayout.LayoutParams.WRAP_CONTENT));
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        ButterKnife.bind(this);

    }



    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

}
