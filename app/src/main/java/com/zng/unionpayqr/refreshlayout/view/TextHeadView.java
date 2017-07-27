package com.zng.unionpayqr.refreshlayout.view;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.zng.unionpayqr.R;

/**
 * author: qiulie
 * created on: 2016/9/3 20:39
 * description:
 */

public class TextHeadView extends DefaultHeadView {	
    private TextView mTextView;
    private String mLastUpDate;
    private boolean mRefresh = false;
    private ImageView iv_arr;
    private ImageView iv_progress;
    private TextView tv_title;
    private TextView tv_time;
    //箭头动画
    private RotateAnimation animUp;
    private RotateAnimation animDown;

    public TextHeadView(Context context) {
        super(context);
    }

    @Override
    protected View initView() {
        View view = View.inflate(context, R.layout.item_def_refresh, null);
        iv_arr = (ImageView) view.findViewById(R.id.iv_arr);
        iv_progress = (ImageView) view.findViewById(R.id.iv_progress);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_time = (TextView) view.findViewById(R.id.tv_time);
        tv_time.setVisibility(View.GONE);

        initArrowAnim();
        setPullState(SuperRefreshLayout.STATE_PULL_REFRESH);

        return view;
    }

    @Override
    protected void setPullState(int statePullRefresh) {
        switch (statePullRefresh) {
            case SuperRefreshLayout.STATE_PULL_REFRESH:

                if (mRefresh) {
                    mRefresh = false;
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    mLastUpDate = format.format(new Date());
                    tv_time.setText("最近更新:" + mLastUpDate);
                    tv_time.setVisibility(View.VISIBLE);

                }

                if (mLastUpDate != null) {
                    tv_time.setText("最近更新:" + mLastUpDate);
                    tv_time.setVisibility(View.VISIBLE);
                } else {
                    tv_time.setVisibility(View.GONE);
                }
                tv_title.setText("下拉刷新...");
                iv_arr.setVisibility(View.VISIBLE);
                iv_progress.setVisibility(View.INVISIBLE);
                iv_arr.clearAnimation();
                iv_arr.startAnimation(animDown);
                ((AnimationDrawable) iv_progress.getDrawable()).stop();

                break;
            case SuperRefreshLayout.STATE_RELEASE_REFRESH:
                tv_title.setText("松开刷新...");
                iv_arr.setVisibility(View.VISIBLE);
                iv_progress.setVisibility(View.INVISIBLE);
                iv_arr.clearAnimation();
                iv_arr.startAnimation(animUp);
                break;
            case SuperRefreshLayout.STATE_REFRESHING:
                tv_title.setText("刷新中...");
                iv_arr.clearAnimation();
                iv_arr.setVisibility(View.GONE);
                iv_progress.setVisibility(View.VISIBLE);
                ((AnimationDrawable) iv_progress.getDrawable()).start();
                mRefresh = true;
                break;
        }

    }

    /**
     * 初始化箭头动画
     */
    private void initArrowAnim() {
        // 箭头向上动画
        animUp = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        animUp.setDuration(200);
        animUp.setFillAfter(true);

        // 箭头向下动画
        animDown = new RotateAnimation(-180, 0, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animDown.setDuration(200);
        animDown.setFillAfter(true);

    }
}
