package com.zng.unionpayqr.refreshlayout.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * author: qiulie
 * created on: 2016/9/3 19:28
 * description:
 */

public abstract class DefaultHeadView extends FrameLayout {
    protected Context context;

    public DefaultHeadView(Context context) {
        this(context, null);
    }

    public DefaultHeadView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DefaultHeadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        addView(initView());
    }

    /**
     * 初始化下拉刷新的view
     *
     * @return
     */
    protected abstract View initView();

    /**
     * 设置下拉刷新的状态
     *
     * @param statePullRefresh
     */
    protected abstract void setPullState(int statePullRefresh);

    /**
     * 下拉的百分比
     *
     * @param offset
     */
    protected void updatePullOffset(float offset) {

    }

    protected void overRefresh(){

    };
}
