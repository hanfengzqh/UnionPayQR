package com.zng.unionpayqr.base;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.zng.unionpayqr.R;
import com.zng.unionpayqr.utils.AppManager;
import com.zng.unionpayqr.utils.Constant;
import com.zng.unionpayqr.utils.Contacts;
import com.zng.unionpayqr.utils.Logger;
import com.zng.unionpayqr.utils.PayUtils;
import com.zng.unionpayqr.utils.ToastUtil;
import com.zng.unionpayqr.utils.Utils;
import com.zng.unionpayqr.view.MvpView;
import com.zng.unionpayqr.widget.ErrorHintView;
import com.zng.unionpayqr.widget.ErrorHintView.OperateListener;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by JDD on 2016/4/22 0022.
 */

public abstract class BaseActivity extends FragmentActivity implements MvpView,
		OnClickListener {

	@ViewInject(R.id.title_back_bt)
	public Button title_back_bt;
	
	@ViewInject(R.id.title_cancle_bt)
	public Button title_cancle_bt;
	
	@ViewInject(R.id.title_main_tv)
	public TextView title_main_tv;
	
	@ViewInject(R.id.show_login_operation)
	public TextView show_login_operation;
	
	@ViewInject(R.id.rl_title_right)
	public RelativeLayout rl_title_right;

	@ViewInject(R.id.iv_right_title)
	public ImageView iv_right_title;

	@ViewInject(R.id.tv_right_title)
	public TextView tv_right_title;
	
	protected Context mContext;
	private boolean isAlive = false;
	/**
	 * 进度弹窗
	 */
	protected ProgressDialog progressDialog = null;

	private ErrorHintView hintView;

	private LinearLayout lyt_content;

	protected String receipt_title;
	protected String remark;

	protected String loginId;//签到
	protected String operaterId;//获取当前登陆账号

	@Override
	protected void onCreate(Bundle arg0) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(arg0);
		x.view().inject(this);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		mContext = this;
		isAlive = true;
		receipt_title = Utils.getStringValuesIntoSettings(mContext,
				Contacts.PRINT_RECEIPT_TITLE);
		remark = Utils.getStringValuesIntoSettings(mContext,
				Contacts.PRINT_REMARK_INFO);
		operaterId = PayUtils.getLoginOperId(mContext);
		loginId = PayUtils.getSignInOperId(mContext);
		
		// 注册home键广播
		final IntentFilter homeFilter = new IntentFilter(
				Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		registerReceiver(homePressReceiver, homeFilter);
		initContentView();
		setListener();
	}

	public void setErrorHintView(ErrorHintView hintView) {
		this.hintView = hintView;
	}

	public void setLinearLayout(LinearLayout lyt_content) {
		this.lyt_content = lyt_content;
	}

	public void initData() {

	}

	public void openActivity(Class<?> pClass) {
		openActivity(pClass, null, null);
	}

	public void openActivity(Class<?> pClass, Bundle bundle) {
		openActivity(pClass, bundle, null);
	}

	/**
	 * @Title:openActivity
	 * 
	 */
	public void openActivity(Class<?> pClass, Bundle bundle, Uri uri) {
		Intent intent = new Intent(this, pClass);
		if (bundle != null)
			intent.putExtras(bundle);
		if (uri != null)
			intent.setData(uri);
		startActivity(intent);

	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(homePressReceiver);
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
			showProgressDialog(null,
					mContext.getResources().getString(stringResId));
		} catch (Exception e) {
			Logger.i(
					"zqh",
					"showProgressDialog showProgressDialog(null, context.getResources().getString(stringResId));");
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
	public void showProgressDialog(final String dialogTitle,
			final String dialogMessage) {
		runUiThread(new Runnable() {
			@Override
			public void run() {
				if (progressDialog == null) {
					progressDialog = new ProgressDialog(mContext,
							AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
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
		});
	}

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

	/**
	 * 隐藏加载进度
	 */
	public void dismissProgressDialog() {
		runUiThread(new Runnable() {
			@Override
			public void run() {
				// 把判断写在runOnUiThread外面导致有时dismiss无效，可能不同线程判断progressDialog.isShowing()结果不一致
				if (progressDialog == null
						|| progressDialog.isShowing() == false) {
					Logger.i(
							"zqh",
							"dismissProgressDialog  progressDialog == null"
									+ " || progressDialog.isShowing() == false >> return;");
					return;
				}
				progressDialog.dismiss();
			}
		});
	}

	/**
	 * 在UI线程中运行，建议用这个方法代替runOnUiThread
	 * 
	 * @param action
	 */
	public final void runUiThread(Runnable action) {
		if (isAlive() == false) {
			Logger.i("zqh", "runUiThread  isAlive() == false >> return;");
			return;
		}
		runOnUiThread(action);
	}

	// 运行线程 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

	public final boolean isAlive() {
		return isAlive && mContext != null;// & !
		// isFinishing();导致finish，onDestroy内runUiThread不可用
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

	public abstract void initContentView();

	/**
	 * 设置监听
	 */
	protected abstract void setListener();

	/**
	 * 设置正标题
	 * 
	 * @param titleName
	 */
	public void setActionBarTitle(String titleName) {
		title_main_tv.setText(titleName);
	}

	/**
	 * 添加返回按键
	 */
	public void addBackBtn() {
		title_back_bt.setVisibility(View.VISIBLE);
		title_cancle_bt.setVisibility(View.INVISIBLE);
	}

	/**
	 * 添加取消按键
	 */
	public void addCancleBtn() {
		title_back_bt.setVisibility(View.INVISIBLE);
		title_cancle_bt.setVisibility(View.VISIBLE);
	}

	/**
	 * 设置右侧标题文字
	 * 
	 * @param right_title
	 */
	public void setRightTitle(String right_title) {
		tv_right_title.setText(right_title);
	}

	/**
	 * 设置显示右侧图片
	 */
	public void showRigehtImage(int resId, OnClickListener onClickListener) {
		iv_right_title.setBackgroundResource(resId);
		rl_title_right.setVisibility(View.VISIBLE);
		rl_title_right.setOnClickListener(onClickListener);
	}

	/**
	 * 隐藏右侧图片
	 */
	public void hideRightImage() {
		rl_title_right.setVisibility(View.INVISIBLE);
	}
	
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

	// home键监听
	protected final BroadcastReceiver homePressReceiver = new BroadcastReceiver() {

		final String SYSTEM_DIALOG_REASON_KEY = "reason";

		final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
				String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
				if (reason != null
						&& reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {// 监听到home键
					finish();
			        android.os.Process.killProcess(android.os.Process.myPid());  
			        System.exit(0);
				}
			}
		}
	};

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
	public void showEmpty(String msg, View.OnClickListener onClickListener,
			int imageId) {

	}

	@Override
	public void showNetError(View.OnClickListener onClickListener) {

	}
}
