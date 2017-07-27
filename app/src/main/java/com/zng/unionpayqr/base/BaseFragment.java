package com.zng.unionpayqr.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.zng.unionpayqr.utils.AppManager;
import com.zng.unionpayqr.utils.Constant;
import com.zng.unionpayqr.utils.Logger;
import com.zng.unionpayqr.utils.ToastUtil;
import com.zng.unionpayqr.view.MvpView;
import com.zng.unionpayqr.widget.ErrorHintView;
import com.zng.unionpayqr.widget.ErrorHintView.OperateListener;

import org.xutils.x;

/**
 * Created by JDD on 2016/4/22 0022.
 */
public abstract class BaseFragment extends Fragment implements MvpView,OnClickListener{

	public Context mContext;
	public Activity mActivity;
	// 进度弹窗
	protected ProgressDialog progressDialog = null;
	private boolean injected = false;
	private ErrorHintView hintView;
	private LinearLayout lyt_content;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContext = getActivity();
		mActivity = getActivity();
		injected = true;
		return x.view().inject(this, inflater, container);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (!injected) {
			x.view().inject(this, this.getView());
		}
		initContentView();
		setListener();
	}
	
	public void setErrorHintView(ErrorHintView hintView){
		this.hintView=hintView;
	}
	
	public void setLinearLayout(LinearLayout lyt_content){
		this.lyt_content=lyt_content;
	}
	
	public abstract void initContentView();
	
	/**
	 * 显示toast提示
	 * 
	 * @param context
	 * @param text
	 */
	public void showToast(Context context, String text) {
		if (!TextUtils.isEmpty(text)) {
			ToastUtil.showToast(context, text);
		}

	}

	public void openActivity(Activity context, Class<?> pClass) {
		openActivity(pClass, null, null, context);
	}

	public void openActivity(Activity context,Class<?> pClass, Bundle bundle) {
		openActivity(pClass, bundle, null, context);
	}

	/**
	 * 
	 * @Title:openActivity
	 * 
	 * @Description页面跳转的动画效果
	 */
	public void openActivity(Class<?> pClass, Bundle bundle, Uri uri, Activity context) {
		Intent intent = new Intent(context, pClass);
		if (bundle != null)
			intent.putExtras(bundle);
		if (uri != null)
			intent.setData(uri);
		// 增加动画
		context.startActivity(intent);
	}
	
	
	// 跳转并结束
	protected void openfinish(Class cls) {
		Intent intent = new Intent(mContext, cls);
		startActivity(intent);
		AppManager.getAppManager().finishActivity();
	}

	/**
	 * 展示加载进度条,无标题
	 * 
	 * @param stringResId
	 */
	public void showProgressDialog(int stringResId) {
		try {
			showProgressDialog(null, mContext.getResources().getString(stringResId));
		} catch (Exception e) {
			Logger.i("zqh","showProgressDialog(null, context.getResources().getString(stringResId));");
		}
	}

	/**
	 * 展示加载进度条,无标题
	 * 
	 * @param dialogMessage
	 */
	public void showProgressDialog(String dialogMessage) {
		showProgressDialog(null, dialogMessage);
	}

	/**
	 * 展示加载进度条
	 * 
	 * @param dialogTitle
	 *            标题
	 * @param dialogMessage
	 *            信息
	 */
	public void showProgressDialog(final String dialogTitle, final String dialogMessage) {

		if (progressDialog == null) {
			progressDialog = new ProgressDialog(mContext,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
		}
		if (progressDialog.isShowing() == true) {
			progressDialog.dismiss();
		}
		if (dialogTitle != null && !"".equals(dialogTitle.trim())) {
			progressDialog.setTitle(dialogTitle);
		}
		if (dialogMessage != null && !"".equals(dialogMessage.trim())) {
			progressDialog.setMessage(dialogMessage);
		}
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();

	}

	/**
	 * 隐藏加载进度
	 */
	public void dismissProgressDialog() {

		// 把判断写在runOnUiThread外面导致有时dismiss无效，可能不同线程判断progressDialog.isShowing()结果不一致
		if (progressDialog == null || progressDialog.isShowing() == false) {
			Logger.i("zqh", "dismissProgressDialog  progressDialog == null"
					+ " || progressDialog.isShowing() == false >> return;");
			return;
		}
		progressDialog.dismiss();

	}
	
	
	/***
	 * 数据请求显示状态
	 * 
	 * @param i
	 */
	public void showLoading(int i) {
		hintView.setVisibility(View.GONE);
		lyt_content.setVisibility(View.GONE);
		switch (i) {
		case 1:
			hintView.hideLoading();
			lyt_content.setVisibility(View.VISIBLE);
			break;

		case 2:
			hintView.hideLoading();
			hintView.netError(new OperateListener() {
				@Override
				public void operate() {
					showLoading(Constant.VIEW_LOADING);
					initData();
				}
			});
			break;

		case 3:
			hintView.hideLoading();
			hintView.loadFailure(new OperateListener() {
				@Override
				public void operate() {
					showLoading(Constant.VIEW_LOADING);
					initData();
				}
			});
			break;

		case 4:
			hintView.loadingData();
			break;

		case 5:
			hintView.hideLoading();
			hintView.noData();
			break;

		}
	}

	public void initData() {

	}

	/**
	 * 设置监听
	 */
	protected abstract void setListener();

	/**
	 * 
	 * @param paramView
	 *            监听实现
	 */
	protected abstract void onClickEvent(View paramView);

	@Override
	public void onClick(View v) {
		onClickEvent(v);

	}

	


	@Override
	public void showLoading(String msg) {

	}

	@Override
	public void hideLoading() {

	}

	@Override
	public void showError(String msg, View.OnClickListener onClickListener) {

	}

	@Override
	public void showEmpty(String msg, View.OnClickListener onClickListener) {

	}

	@Override
	public void showEmpty(String msg, View.OnClickListener onClickListener, int imageId) {

	}

	@Override
	public void showNetError(View.OnClickListener onClickListener) {

	}
    
}
