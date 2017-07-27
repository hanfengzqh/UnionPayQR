package com.zng.unionpayqr.refreshlayout.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.zng.unionpayqr.R;
import com.zng.unionpayqr.refreshlayout.utils.GeometryUtil;
import com.zng.unionpayqr.refreshlayout.utils.Util;

/**
 * author: qiulie
 * created on: 2016/9/12 9:50
 * description:粘连控件
 */

public class GooView extends View {
    public static final String Tag = GooView.class.getSimpleName();

    private static final int DEFAULT_VIEW_HEIGHT_DP = 48;

    private Context context;
    private Paint mPaint;
    private float tempStickRadius; //固定圆半径
    private float mDragRadius = 50; //拖拽圆半径
    private int mMaxHeight; //默认固定高度
    private PointF mStickCenter; //固定圆心坐标
    private PointF mDragCenter; //拖拽圆心坐标

    private PointF mControlPoint;

    private PointF[] mStickPoints;
    private PointF[] mDragPoints;
    private Paint mPaintArrow;

    private float startAngle = 0;
    private float sweepAngle = 90;
    private MyRunnable myRunnable;
    private int fill_color;
    private int progress_color;

    public GooView(Context context) {
        this(context, null);
    }

    public GooView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GooView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;

        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.GooView, defStyleAttr, 0);

        fill_color = t.getColor(R.styleable.GooView_fill_color, Color.RED);
        progress_color = t.getColor(R.styleable.GooView_progress_color, Color.WHITE);

        t.recycle();

        init();
    }

    private void init() {

        mMaxHeight = Util.dip2px(context, DEFAULT_VIEW_HEIGHT_DP);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(fill_color);

        mPaintArrow = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintArrow.setColor(progress_color);  //设置画笔颜色
        mPaintArrow.setStyle(Paint.Style.STROKE);//设置填充样式
        mPaintArrow.setStrokeWidth(15);//设置画笔宽度
        mPaintArrow.setStrokeCap(Paint.Cap.ROUND);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getMeasuredSize(widthMeasureSpec, 48),
                getMeasuredSize(heightMeasureSpec, 48));
    }

    private int getMeasuredSize(int measureSpecValue, int defaultValue) {
        int specMode = MeasureSpec.getMode(measureSpecValue);
        int specSize = MeasureSpec.getSize(measureSpecValue);
        int defaultSize = Util.dip2px(context, defaultValue);
        if (specMode == MeasureSpec.EXACTLY) {
            defaultSize = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            defaultSize = Math.min(specSize, defaultSize);
        }
        return defaultSize;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (h < mMaxHeight) {
            tempStickRadius = h / 2;
            mStickCenter = new PointF(w / 2, h / 2);
            mDragCenter = new PointF(w / 2, h / 2);
            mDragRadius = tempStickRadius;
        } else {
            tempStickRadius = mMaxHeight / 2;
            mDragRadius = tempStickRadius - (h - mMaxHeight) / 4;
            mDragRadius = Math.max(mDragRadius, 20);

            mStickCenter = new PointF(w / 2, mMaxHeight / 2);
            mDragCenter = new PointF(w / 2, h - mDragRadius);

        }


        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        // 通过几何图形工具获取交点坐标
        mDragPoints = new PointF[]{
                new PointF(mDragCenter.x - mDragRadius, mDragCenter.y),
                new PointF(mDragCenter.x + mDragRadius, mDragCenter.y)
        };

        mStickPoints = new PointF[]{
                new PointF(mStickCenter.x - tempStickRadius, mStickCenter.y),
                new PointF(mStickCenter.x + tempStickRadius, mStickCenter.y)
        };

        // 3. 获取控制点坐标
        mControlPoint = GeometryUtil.getMiddlePoint(mStickCenter, mDragCenter);

        // 3. 画连接部分
        Path path = new Path();
        // 跳到点1
        path.moveTo(mStickPoints[0].x, mStickPoints[0].y);
        // 画曲线1 -> 2
        path.quadTo(mControlPoint.x - mDragRadius, mControlPoint.y, mDragPoints[0].x, mDragPoints[0].y);
        // 画直线2 -> 3
        path.lineTo(mDragPoints[1].x, mDragPoints[1].y);
        // 画曲线3 -> 4
        path.quadTo(mControlPoint.x + mDragRadius, mControlPoint.y, mStickPoints[1].x, mStickPoints[1].y);
        path.close();
        canvas.drawPath(path, mPaint);

        // 2. 画固定圆
        canvas.drawCircle(mStickCenter.x, mStickCenter.y, tempStickRadius, mPaint);

        // 1. 画拖拽圆
        canvas.drawCircle(mDragCenter.x, mDragCenter.y, mDragRadius, mPaint);
        // 1. 画箭头
        float radius = tempStickRadius - 32;
        if (radius > 0) {
            float left = mStickCenter.x - radius;
            float top = mStickCenter.y - radius;
            float right = left + radius * 2;
            float bottom = top + radius * 2;
            RectF rect = new RectF(left, top, right, bottom);
            canvas.drawArc(rect, startAngle, sweepAngle, false, mPaintArrow);

        }
//        canvas.drawCircle(mDragCenter.x, mDragCenter.y, tempStickRadius, mPaintArrow);
    }

    public void refresh() {
        if(myRunnable == null)
            myRunnable = new MyRunnable();
        myRunnable.start();
    }

    public void removeCallbak() {
        if(myRunnable != null){
            myRunnable.stop();
        }
        startAngle = 0;
        sweepAngle = 90;
    }

    private class MyRunnable implements Runnable{

        @Override
        public void run() {
            startAngle += 15;
            sweepAngle += 15;

            if(startAngle > 360) startAngle -= 360;
            if(sweepAngle > 360) sweepAngle -= 360;

            invalidate();

            GooView.this.postDelayed(this, 50);

        }

        public void start() {
            stop();
            // 执行延时操作
            GooView.this.postDelayed(this, 50);
        }

        public void stop() {
            GooView.this.removeCallbacks(this);
        }
    }
}
