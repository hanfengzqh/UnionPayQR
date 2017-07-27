package com.zng.unionpayqr.refreshlayout.holder;

import android.content.Context;
import android.view.View;

public abstract class BaseHolder<T>
{
	protected View	mRootView;
	protected Context	context;
	protected T		mData;

	public BaseHolder(Context context) {
		this.context = context;
		mRootView = initView();

		// 打标记
		mRootView.setTag(this);
	}

	protected abstract View initView();

	protected abstract void refreshUI(T data);

	public void setData(T data)
	{
		this.mData = data;

		// UI刷新
		refreshUI(mData);
	}

	public View getRootView()
	{
		return mRootView;
	}
}
