package com.zng.unionpayqr.refreshlayout.view;

import android.content.Context;
import android.view.View;

import com.zng.unionpayqr.R;
import com.zng.unionpayqr.refreshlayout.widget.GooView;

/**
 * author: qiulie
 * created on: 2016/9/12 10:34
 * description:
 */

public class GooHeadView extends DefaultHeadView {

    private GooView gooView;

    public GooHeadView(Context context) {
        super(context);
    }

    @Override
    protected View initView() {
        View view = View.inflate(context, R.layout.item_gooview_refresh, null);
        gooView = (GooView) view.findViewById(R.id.gv_head);
        return view;
    }

    @Override
    protected void setPullState(int statePullRefresh) {

        switch (statePullRefresh) {
            case SuperRefreshLayout.STATE_PULL_REFRESH:
                gooView.removeCallbak();

                break;
            case SuperRefreshLayout.STATE_REFRESHING:
                gooView.refresh();
                break;
        }

    }


}
