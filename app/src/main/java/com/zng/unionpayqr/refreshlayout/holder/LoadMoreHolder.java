package com.zng.unionpayqr.refreshlayout.holder;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.ImageView;

import com.zng.unionpayqr.R;

public class LoadMoreHolder extends BaseHolder<Integer> {
    public static final int STATE_LOADING = 0;    // 加载中的状态
    public static final int STATE_ERROR = 1;    // 加载失败的状态
    public static final int STATE_EMPTY = 2;    // 没有更多数据的状态

    private View mErrorView;

    private View mLoadingView;
    private int mCurrentSate;
    private ImageView iv_progress;

    public LoadMoreHolder(Context context) {
        super(context);
    }

    @Override
    protected View initView() {
        View view = View.inflate(context, R.layout.item_load_more, null);
        iv_progress = (ImageView) view.findViewById(R.id.iv_progress);

        refreshUI(STATE_LOADING);
        ((AnimationDrawable) iv_progress.getDrawable()).start();

        return view;
    }

    @Override
    protected void refreshUI(Integer data) {
        this.mCurrentSate = data;

        switch (data) {
            case STATE_EMPTY:
//                mErrorView.setVisibility(View.GONE);
//                mLoadingView.setVisibility(View.GONE);
                break;
            case STATE_ERROR:
//                mErrorView.setVisibility(View.VISIBLE);
//                mLoadingView.setVisibility(View.GONE);
                break;
            case STATE_LOADING:
                ((AnimationDrawable) iv_progress.getDrawable()).start();
//                mErrorView.setVisibility(View.GONE);
//                mLoadingView.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    public int getCurrentState() {
        return mCurrentSate;
    }

}
