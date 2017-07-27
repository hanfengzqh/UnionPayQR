package com.zng.unionpayqr.refreshlayout.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.zng.unionpayqr.R;
import com.zng.unionpayqr.refreshlayout.holder.BaseHolder;
import com.zng.unionpayqr.refreshlayout.holder.LoadMoreHolder;
import com.zng.unionpayqr.refreshlayout.utils.Util;

/**
 * author: qiulie
 * created on: 2016/9/2 18:34
 * description: 支持listview与Recyclerview下拉刷新控件
 */

public class SuperRefreshLayout extends RelativeLayout {
    public static final String Tag = SuperRefreshLayout.class.getSimpleName();

    public static final int STATE_PULL_REFRESH = 0;// 下拉刷新
    public static final int STATE_RELEASE_REFRESH = 1;// 松开刷新
    public static final int STATE_REFRESHING = 2;// 正在刷新
    public static final int STATE_LOADMOREING = 3;// 正在加载更多

    // 初始化参数
    private boolean isPullEndless; // 下拉无尽模式
    private boolean isOverlay; // 覆盖模式
    private int showType; // 显示类型 0：文字模式 1：官方模式

    // 运行中参数
    private int mCurrrentState = STATE_PULL_REFRESH;// 当前状态
    private Context context;
    private View mChildView;
    private DefaultHeadView mHeadView;
    private int mHeadViewHeight;
    private BaseHolder mFootView;
    private int mFootViewHeight;
    private float mTouchY;
    private float mLastY;
    private boolean isInControl = false;
    private boolean isLoadMore = true; // 是否有加载更多
    private boolean isLoadMoreing = false; //加载更多中
    private Scroller mScroller;

    private RefreshListener mRefreshListener;

    public SuperRefreshLayout(Context context) {
        this(context, null);
    }

    public SuperRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SuperRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        this.context = context;
        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.SuperRefreshLayout, defStyleAttr, 0);

        isPullEndless = t.getBoolean(R.styleable.SuperRefreshLayout_pull_endless, false);
        isOverlay = t.getBoolean(R.styleable.SuperRefreshLayout_overlay, false);
        showType = t.getInt(R.styleable.SuperRefreshLayout_show_type, 0);

        t.recycle();

        mScroller = new Scroller(context);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        Log.i(Tag, "onAttachedToWindow");

        Context context = getContext();

        mChildView = getChildAt(0);
        switch (showType) {
            case 0:
                mHeadView = new TextHeadView(context);
                break;
            case 1:
                mHeadView = new MaterialHeaderView(context);
                break;
            case 2:
                mHeadView = new GooHeadView(context);
                break;
        }

        if (mChildView instanceof ListView) {
            ((ListView) mChildView).setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                    if (scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_FLING) {

                        if (((ListView) mChildView).getLastVisiblePosition() == ((ListView) mChildView).getCount() - 1) {// 滑动到最后
//                            Log.e(Tag, "滑动到底部了...");
                            if(isLoadMore)
                                toggleFootView(true);
                        } else {

                        }
                    }

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                }
            });
        }

        if(mChildView instanceof RecyclerView){
            ((RecyclerView)mChildView).setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                    int type;
                    int lastVisiblePosition = 0;
                    RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
                    if(manager instanceof LinearLayoutManager){
                        lastVisiblePosition = ((LinearLayoutManager)manager).findLastVisibleItemPosition();
                    }else if(manager instanceof GridLayoutManager){
                        lastVisiblePosition = ((GridLayoutManager)manager).findLastVisibleItemPosition();
                    }else if(manager instanceof StaggeredGridLayoutManager){
                        int[] positions = ((StaggeredGridLayoutManager) manager).findLastVisibleItemPositions(null);
                        for (int a : positions) {
                            lastVisiblePosition = Math.max(a,lastVisiblePosition);
                        }
                    }
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        if(lastVisiblePosition >= manager.getItemCount() - 1){// 滑动到最后
//                            Log.e(Tag, "滑动到底部了...");
                            if(isLoadMore)
                                toggleFootView(true);
                        } else {

                        }
                    }
                }
            });
        }


        mHeadView.measure(0, 0);
        mHeadViewHeight = mHeadView.getMeasuredHeight();
        if (showType == 1) {
            mHeadViewHeight += Util.dip2px(context, 10);
        }

        mFootView = new LoadMoreHolder(context);
        mFootView.getRootView().measure(0, 0);
        mFootViewHeight = mFootView.getRootView().getMeasuredHeight();
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, mFootViewHeight);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lp.setMargins(0, -mFootViewHeight, 0, -mFootViewHeight);
        mFootView.getRootView().setLayoutParams(lp);


//        Log.e(Tag, "mHeadViewHeight=" + mHeadView.getMeasuredHeight());
//        Log.e(Tag, "mFootViewHeight=" + mFootView.getMeasuredHeight());

//        mHeadViewHeight = Util.dip2px(context, 48);

        mHeadView.setVisibility(View.GONE);
        mFootView.getRootView().setVisibility(View.VISIBLE);
        addView(mHeadView, LayoutParams.MATCH_PARENT, mHeadViewHeight);
        addView(mFootView.getRootView());


        if (mChildView == null) {
            return;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        int action = ev.getAction();
        float y = ev.getY();


        switch (action) {
            case MotionEvent.ACTION_DOWN:
//                Log.e(Tag, "dispatchTouchEvent === ACTION_DOWN");
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
//                Log.e(Tag, "dispatchTouchEvent === ACTION_MOVE");
                float dy = y - mLastY;
                if (!isInControl && mCurrrentState != STATE_REFRESHING
                        && ((!canPullDown() && dy > 0) || (getScrollY() != 0) || (isLoadMore && !canPullUp() && dy < 0))) {
//                    Log.e(Tag, "事件分发处理...");
                    isInControl = true;
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                    MotionEvent ev2 = MotionEvent.obtain(ev);
                    dispatchTouchEvent(ev);
                    ev2.setAction(MotionEvent.ACTION_DOWN);
                    return dispatchTouchEvent(ev2);
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mCurrrentState == STATE_REFRESHING) return true;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                Log.e(Tag, "onInterceptTouchEvent === ACTION_DOWN");
                mTouchY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
//                Log.e(Tag, "onInterceptTouchEvent === ACTION_MOVE");
                float curY = ev.getY();
                float dy = curY - mTouchY;
                if (getScrollY() != 0) {
                    return true;
                }
                if (mCurrrentState != STATE_REFRESHING && dy > 0 && !canPullDown()) {
                    return true;
                }
                if (isLoadMore && mCurrrentState != STATE_REFRESHING && dy < 0 && !canPullUp()) {
                    return true;
                }
                isInControl = false;
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mCurrrentState == STATE_REFRESHING) return super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
//                Log.e(Tag, "TouchEvent === ACTION_MOVE");
                float curY = event.getY();
                float dy = curY - mTouchY;

                int scrollY = getScrollY();

                //加载更多有显示的情况
                if (scrollY != 0) {
                    scrollY = Math.max(0, scrollY - (int) dy);
                    if (scrollY > mFootViewHeight) scrollY = mFootViewHeight;
                    scrollTo(0, scrollY);
                    mTouchY = curY;

                    //让listview去处理事件
                    if (scrollY == 0) {
                        event.setAction(MotionEvent.ACTION_DOWN);
                        dispatchTouchEvent(event);
                        isInControl = false;
                    }
                    return true;
                }
                if (!canPullDown()) dealHeadView(event, dy);
                else if (!canPullUp() && isLoadMore) dealFootView(event, dy);

                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mCurrrentState == STATE_RELEASE_REFRESH) {
                    startAnimator(mChildView, mHeadViewHeight, mHeadView);
                } else if (mCurrrentState == STATE_PULL_REFRESH) {
                    startAnimator(mChildView, 0, mHeadView);
                }
                //判断是否加载更多
                int y = getScrollY();
                if(y != 0 && isLoadMore) {
                    if (y == mFootViewHeight) {
                        toggleFootView(true);
                    } else {
                        if(mCurrrentState == STATE_LOADMOREING)
                            toggleFootView(true);
                        else
                            toggleFootView(false);
                    }
                }
                break;
        }

        return true;
    }

    private void dealFootView(MotionEvent event, float dy) {
        //让listview去处理事件
        if (dy > 0) {
            event.setAction(MotionEvent.ACTION_DOWN);
            dispatchTouchEvent(event);
            isInControl = false;
            dy = 0;
        }

        dy = Math.abs(dy);

        if (dy > mFootViewHeight) dy = mFootViewHeight;
        scrollTo(0, (int) dy);
//        mFootView.setVisibility(View.VISIBLE);
    }

    private void dealHeadView(MotionEvent event, float dy) {
        if (dy < 0) {
            event.setAction(MotionEvent.ACTION_DOWN);
            dispatchTouchEvent(event);
            isInControl = false;
            dy = 0;
        }
        if (dy / 2 > mHeadViewHeight) {
            if (!isPullEndless)
                dy = mHeadViewHeight * 2;
            else
                dy = (((dy / 2 - mHeadViewHeight) / 2) + mHeadViewHeight) * 2;

            if (mCurrrentState != STATE_RELEASE_REFRESH) {
                mCurrrentState = STATE_RELEASE_REFRESH;
                refreshState();
            }
        } else {
            if (mCurrrentState != STATE_PULL_REFRESH) {
                mCurrrentState = STATE_PULL_REFRESH;
                refreshState();
            }

        }
//        Log.e(Tag, "dy=" + dy);
        mHeadView.setVisibility(View.VISIBLE);
        mHeadView.getLayoutParams().height = (int) (dy / 2);
        mHeadView.requestLayout();

        mHeadView.updatePullOffset(dy / 2 / mHeadViewHeight);
        if (!isOverlay) {
            LayoutParams layoutParams = (LayoutParams) mChildView.getLayoutParams();
            layoutParams.setMargins(0, (int) (dy / 2), 0, 0);
            mChildView.setLayoutParams(layoutParams);
        }
    }

    private void toggleFootView(boolean isOpen) {
        int scrollY = getScrollY();
        mScroller.abortAnimation();

        if(isOpen) {
//            Log.e(Tag,"isOpen...");
            int dy = mFootViewHeight - scrollY;
            mScroller.startScroll(0, scrollY, 0, dy, 250);
            if(mCurrrentState == STATE_LOADMOREING) return;
            mCurrrentState = STATE_LOADMOREING;
            refreshState();
        }
        else {
//            Log.e(Tag,"isClose...");
            if(scrollY == 0) return;
            mScroller.startScroll(0, scrollY, 0, -scrollY, 250);
            mCurrrentState = STATE_PULL_REFRESH;
            refreshState();
        }
        invalidate();

//        scrollTo(0, mFootViewHeight);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }else{
//            Log.e(Tag,"computeScroll="+getScrollY());
        }
    }

    private void refreshState() {
        switch (mCurrrentState) {
            case STATE_PULL_REFRESH:
                mHeadView.setPullState(STATE_PULL_REFRESH);
                break;
            case STATE_RELEASE_REFRESH:
                mHeadView.setPullState(STATE_RELEASE_REFRESH);
                break;
            case STATE_REFRESHING:
                mHeadView.setPullState(STATE_REFRESHING);
                if (mRefreshListener != null) mRefreshListener.onRefresh(this);
                break;
            case STATE_LOADMOREING:
                if(mRefreshListener != null) mRefreshListener.onRefreshLoadMore(this);
                break;

            default:
                break;
        }
    }

    private boolean canPullDown() {
        if (mChildView == null) return false;
        return ViewCompat.canScrollVertically(mChildView, -1);
    }

    private boolean canPullUp() {
        if (mChildView == null) return false;
        return ViewCompat.canScrollVertically(mChildView, 1);
    }

    public void finishRefresh() {

        this.post(new Runnable() {
            @Override
            public void run() {
                if (mHeadView != null)
                    mHeadView.overRefresh();
                startAnimator(mChildView, 0, mHeadView);

            }
        });


    }

    public void finishLoadMore() {

        this.post(new Runnable() {
            @Override
            public void run() {
                if (mFootView != null)
                    toggleFootView(false);

            }
        });


    }

    public void autoRefresh() {
        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mCurrrentState != STATE_REFRESHING) {
                    if (mHeadView != null) {

                        startAnimator(mChildView, mHeadViewHeight, mHeadView);
                    }


                }
            }
        }, 50);


    }

    @SuppressLint("NewApi")
	private void startAnimator(final View v, final int endH, final FrameLayout fl) {

        final RelativeLayout.LayoutParams childParams = (LayoutParams) mChildView.getLayoutParams();
        int startH = childParams.topMargin;

        if (isOverlay)
            startH = fl.getLayoutParams().height;
        ValueAnimator valueAnimator = ValueAnimator.ofInt(startH, endH);
        valueAnimator.setDuration(250);
        valueAnimator.start();


        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int height = (Integer) valueAnimator.getAnimatedValue();
                if (fl.getVisibility() != View.VISIBLE) fl.setVisibility(View.VISIBLE);
                fl.getLayoutParams().height = height;
                fl.requestLayout();
                mHeadView.setVisibility(View.VISIBLE);

                if (!isOverlay) {
                    childParams.setMargins(0, height, 0, 0);
                    mChildView.setLayoutParams(childParams);
                }

            }
        });

        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (endH == 0) {
                    mCurrrentState = STATE_PULL_REFRESH;
                    refreshState();
                } else {
                    mCurrrentState = STATE_REFRESHING;
                    refreshState();
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    public <T extends DefaultHeadView> void setHeadView(T v) {
        if (mHeadView != null) removeView(mHeadView);
        mHeadView = v;
        v.measure(0, 0);
        mHeadViewHeight = v.getMeasuredHeight();
        addView(mHeadView, LayoutParams.MATCH_PARENT, mHeadViewHeight);
    }

    /**
     * 监听回调接口
     */
    public interface RefreshListener {
        public void onRefresh(SuperRefreshLayout superRefreshLayout);

        public void onRefreshLoadMore(SuperRefreshLayout superRefreshLayout);
    }

    public void setRefreshListener(RefreshListener refreshListener) {
        this.mRefreshListener = refreshListener;
    }

    public boolean isLoadMore() {
        return isLoadMore;
    }

    public void setLoadMore(boolean loadMore) {
        isLoadMore = loadMore;
    }

    public boolean isOverlay() {
        return isOverlay;
    }

    public void setOverlay(boolean overlay) {
        isOverlay = overlay;
    }

    public boolean isPullEndless() {
        return isPullEndless;
    }

    public void setPullEndless(boolean pullEndless) {
        isPullEndless = pullEndless;
    }

}
