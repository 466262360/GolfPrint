package com.mashangyou.golfprint.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;


import com.mashangyou.golfprint.R;

import androidx.annotation.NonNull;

/**
 * Created by Administrator on 2020/5/25.
 * Des:
 */
public class LoadingDialog extends Dialog {

    public LoadingDialog(@NonNull Context context) {
        super(context, R.style.CustomDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
    }
}
