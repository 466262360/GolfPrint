package com.mashangyou.golfprint.adapter;

import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mashangyou.golfprint.R;
import com.mashangyou.golfprint.bean.res.PublishRes;
import com.mashangyou.golfprint.widget.PublishDialog;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Administrator on 2020/9/10.
 * Des:
 */
public class PublishAdapter extends BaseQuickAdapter<PublishRes.Publish,BaseViewHolder> {

    private final SimpleDateFormat format;
    private PublishHorAdapter adapter;

    public PublishAdapter(int layoutResId, @Nullable List<PublishRes.Publish> data) {
        super(layoutResId, data);
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, PublishRes.Publish data) {
        RecyclerView rv = baseViewHolder.getView(R.id.rv_publish_hor);
       baseViewHolder.setText(R.id.tv_time,data.getTitle());
        if (data.getInfos()!=null){
            adapter = new PublishHorAdapter(R.layout.item_publish_hor, data.getInfos(),getRecyclerView().getWidth());
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            layoutManager.setOrientation(RecyclerView.HORIZONTAL);
            rv.setLayoutManager(layoutManager);
            rv.setAdapter(adapter);

            adapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                    PublishDialog dialog = new PublishDialog(getContext(),data.getInfos().get(position));
                    dialog.show();
                }
            });
        }

    }
}
