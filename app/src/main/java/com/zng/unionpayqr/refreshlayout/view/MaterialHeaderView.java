package com.zng.unionpayqr.refreshlayout.view;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.zng.unionpayqr.R;
import com.zng.unionpayqr.refreshlayout.utils.Util;

public class MaterialHeaderView extends DefaultHeadView{

    private final static String Tag = MaterialHeaderView.class.getSimpleName();

    private final static int DEFAULT_PROGRESS_SIZE = 50;
    private final static int PROGRESS_STOKE_WIDTH = 3;

    private CircleProgressBar circleProgressBar;
    private int waveColor;
    private int progressTextColor = Color.BLACK;
    private int[] progress_colors;
    private int progressStokeWidth;
    private boolean isShowArrow, isShowProgressBg;
    private int progressValue = 0, progressValueMax = 100,progressMax =100;
    private int textType = 1;
    private int progressBg;
    private int progressSize = DEFAULT_PROGRESS_SIZE;
    private static float density;

    public MaterialHeaderView(Context context) {
        super(context);
    }

    @Override
    protected View initView() {

        FrameLayout fl = new FrameLayout(context);

        density = getContext().getResources().getDisplayMetrics().density;
        circleProgressBar = new CircleProgressBar(getContext());
        LayoutParams layoutParams = new LayoutParams((int) density * progressSize, (int) density * progressSize);
        layoutParams.gravity = Gravity.CENTER;
        circleProgressBar.setPadding(50,50,50,50);
//        layoutParams.setMargins(Util.dip2px(context,20),Util.dip2px(context,20),Util.dip2px(context,20),Util.dip2px(context,20));
        circleProgressBar.setLayoutParams(layoutParams);
        circleProgressBar.setColorSchemeColors(progress_colors);
        circleProgressBar.setProgressStokeWidth(progressStokeWidth);
//        circleProgressBar.setShowArrow(isShowArrow);
        circleProgressBar.setShowProgressText(false);
        circleProgressBar.setTextColor(progressTextColor);
        circleProgressBar.setProgress(progressValue);
        circleProgressBar.setMax(progressValueMax);
        circleProgressBar.setCircleBackgroundEnabled(isShowProgressBg);
        circleProgressBar.setProgressBackGroundColor(progressBg);



        setProgressSize(DEFAULT_PROGRESS_SIZE);
        setProgressColors(context.getResources().getIntArray(R.array.material_colors));
        setProgressStokeWidth(PROGRESS_STOKE_WIDTH);
        setTextType(textType);
        setProgressTextColor(progressTextColor);
        setProgressValue(progressValue);
        setProgressValueMax(progressMax);
        setIsProgressBg(true);
        setProgressBg(CircleProgressBar.DEFAULT_CIRCLE_BG_LIGHT);
        setVisibility(View.INVISIBLE);

//        fl.addView(circleProgressBar);
//        FrameLayout.LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,Util.dip2px(context,96));
//        fl.setLayoutParams(lp);
//        rl.addView(circleProgressBar);
//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,Util.dip2px(context,96));
//        rl.setLayoutParams(lp);

        return circleProgressBar;
    }

    @Override
    protected void setPullState(int statePullRefresh) {
        switch (statePullRefresh){
            case SuperRefreshLayout.STATE_REFRESHING:
                onRefreshing();
                break;
            case SuperRefreshLayout.STATE_PULL_REFRESH:
                onComlete();
                onBegin();
                break;
            case SuperRefreshLayout.STATE_RELEASE_REFRESH:

                break;
        }

    }

    public void setProgressSize(int progressSize) {
        this.progressSize = progressSize;
        LayoutParams layoutParams = new LayoutParams((int) density * progressSize, (int) density * progressSize);
        layoutParams.gravity = Gravity.CENTER;
        if(circleProgressBar!=null)
        circleProgressBar.setLayoutParams(layoutParams);
    }

    public void setProgressBg(int progressBg) {
        this.progressBg = progressBg;
        if(circleProgressBar!=null)
        circleProgressBar.setProgressBackGroundColor(progressBg);
    }

    public void setIsProgressBg(boolean isShowProgressBg) {
        this.isShowProgressBg = isShowProgressBg;
        if(circleProgressBar!=null)
        circleProgressBar.setCircleBackgroundEnabled(isShowProgressBg);
    }

    public void setProgressTextColor(int textColor) {
        this.progressTextColor = textColor;
    }

    public void setProgressColors(int[] colors) {
        this.progress_colors = colors;
        if(circleProgressBar!=null)
        circleProgressBar.setColorSchemeColors(progress_colors);
    }

    public void setTextType(int textType) {
        this.textType = textType;
    }

    public void setProgressValue(int value) {
        this.progressValue = value;
        this.post(new Runnable() {
            @Override
            public void run() {
                if (circleProgressBar != null) {
                    circleProgressBar.setProgress(progressValue);
                }
            }
        });

    }

    public void setProgressValueMax(int value) {
        this.progressValueMax = value;
    }

    public void setProgressStokeWidth(int w) {
        this.progressStokeWidth = w;
        if(circleProgressBar!=null)
            circleProgressBar.setProgressStokeWidth(progressStokeWidth);
    }

    public void showProgressArrow(boolean isShowArrow) {
        this.isShowArrow = isShowArrow;
        if(circleProgressBar!=null)
        circleProgressBar.setShowArrow(isShowArrow);
    }

    public void onComlete() {
        if (circleProgressBar != null) {
            circleProgressBar.onComlete();
            ViewCompat.setTranslationY(circleProgressBar, 0);
            ViewCompat.setScaleX(circleProgressBar, 0);
            ViewCompat.setScaleY(circleProgressBar, 0);
        }

    }

    public void onBegin() {
        if (circleProgressBar != null) {
            ViewCompat.setScaleX(circleProgressBar, 0.001f);
            ViewCompat.setScaleY(circleProgressBar, 0.001f);
            circleProgressBar.onBegin();
        }
    }

    public void onPull(float fraction) {
        if (circleProgressBar != null) {
            circleProgressBar.onPull(fraction);
            float a = Util.limitValue(1, fraction);
            ViewCompat.setScaleX(circleProgressBar, a);
            ViewCompat.setScaleY(circleProgressBar, a);
            ViewCompat.setAlpha(circleProgressBar, a);
        }
    }

    public void onRefreshing() {
        if (circleProgressBar != null) {
            circleProgressBar.onRefreshing();
        }
    }

    @Override
    protected void updatePullOffset(float offset) {
//        if(offset > 1){
//            offset = 1;
//        }
        onPull(offset);
    }

    @Override
    protected void overRefresh() {
        circleProgressBar.onComlete();
    }
}
