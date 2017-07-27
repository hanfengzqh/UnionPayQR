package com.zng.unionpayqr.pro.cashier.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zng.unionpayqr.R;
import com.zng.unionpayqr.base.BaseFragment;
import com.zng.unionpayqr.model.UnionPayResult;
import com.zng.unionpayqr.presenter.UnionPayPresenter;
import com.zng.unionpayqr.pro.MainActivity;
import com.zng.unionpayqr.pro.cashier.activity.PaymentSuccessActivity;
import com.zng.unionpayqr.utils.AmountUtils;
import com.zng.unionpayqr.utils.CodeUtils;
import com.zng.unionpayqr.utils.Constant;
import com.zng.unionpayqr.utils.Logger;
import com.zng.unionpayqr.utils.PayUtils;
import com.zng.unionpayqr.utils.TimeUtils;
import com.zng.unionpayqr.utils.UnionPayQRConstant;
import com.zng.unionpayqr.view.ScanPayView;
import com.zng.unionpayqr.widget.CustomDialog;
import com.zng.unionpayqr.widget.ErrorHintView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

@ContentView(R.layout.fragment_pay)
public class UnionPayQRFragment extends BaseFragment implements ScanPayView{

	@ViewInject(R.id.title_back_bt)
	private Button title_back_bt;
	
	@ViewInject(R.id.title_cancle_bt)
	private Button title_cancle_bt;
	
	@ViewInject(R.id.title_main_tv)
	private TextView title_main_tv;
	
	@ViewInject(R.id.show_login_operation)
	private TextView show_login_operation;
	
	@ViewInject(R.id.rl_title_right)
	private RelativeLayout rl_title_right;

	@ViewInject(R.id.iv_right_title)
	private ImageView iv_right_title;

	@ViewInject(R.id.tv_right_title)
	private TextView tv_right_title;
	
	@ViewInject(R.id.lyt_content)
	private LinearLayout lyt_content;

	@ViewInject(R.id.hintView)
	private ErrorHintView hintView;

	@ViewInject(R.id.txt_amount)
	private TextView txt_amount;

	@ViewInject(R.id.img_code)
	private ImageView img_code;

	@ViewInject(R.id.txt_explain)
	private TextView txt_explain;
	
	@ViewInject(R.id.tv_count_down)
	private TextView tv_count_down;
	
	private boolean isStart = true;
	private String amount;//订单金额
	private String ori_seq;//商户生成的交易流水号
	private String ori_queryId;//银联查询订单号
	private UnionPayPresenter mPayRequestPresenter;
	private CustomDialog mCustomDialog;
	private String traceTime;//交易完成时间
	private TimeCount mTimeCount;
	private boolean isTimeOut = false;
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			String codeInfo=(String) msg.obj;
			switch (msg.what) {
			case Constant.ORDER_SUCCESS:
				showToast(mContext, "支付成功");
				txt_explain.setText(codeInfo);
				isStart=false;
				mTimeCount.cancel();
				PayUtils.openSuccessActivity(mActivity, PaymentSuccessActivity.class, 
						amount, ori_seq, traceTime ,ori_queryId, false,UnionPayQRConstant.QRCODE);
				mHandler.removeCallbacksAndMessages(null);
				break;
			case Constant.ORDER_FAIL:
				txt_explain.setText("订单生成,正在等待买家支付");
				break;
			case Constant.ORDER_ABNORMAL:
				txt_explain.setText(codeInfo);
				isStart=false;
				break;
			case Constant.ORDER_TIMEOUT:
				txt_explain.setText("网络异常,请检查网络");
				isStart=false;
				break;
			case Constant.QUERY_START:
				Logger.d("zqh", "订单查询开始");
				initQueryOrder();
				break;
			case 7:
				mPayRequestPresenter.queryPayStatus(mContext, 
						UnionPayQRConstant.QUERY_PAY_REQUEST,ori_seq);
				mHandler.sendEmptyMessageDelayed(7, 2000);
				
				break;
			default:
				break;
			}

		}
	};
	
	
	@Override
	public void initContentView() {
		amount = this.getArguments().getString("amount");
		mPayRequestPresenter = new UnionPayPresenter();
		mPayRequestPresenter.attachView(this);
		operaterId = PayUtils.getLoginOperId(mContext);
		title_main_tv.setText("银联二维码支付");
		show_login_operation.setText(operaterId+"已登陆");
		show_login_operation.setTextColor(getResources().getColor(R.color.white));
		title_back_bt.setVisibility(View.INVISIBLE);
		title_cancle_bt.setVisibility(View.VISIBLE);
		mTimeCount = new TimeCount(TimeUtils.MIN, TimeUtils.SEC);
		txt_amount.setText("金额："+amount+"元");
		setLinearLayout(lyt_content);
		setErrorHintView(hintView);
		showLoading(Constant.VIEW_LOADING);
		initData();
	}

	@Override
	public void initData(){
		isStart=true;
		mPayRequestPresenter.initPayData(mContext,
				UnionPayQRConstant.SCAN_PAY_REQUEST ,AmountUtils.changeY2F(amount));
	}

	@Override
	protected void setListener() {
		title_cancle_bt.setOnClickListener(this);
		rl_title_right.setOnClickListener(this);
	}

	@Override
	protected void onClickEvent(View paramView) {
		switch (paramView.getId()) {
		case R.id.title_cancle_bt:
			openfinish(MainActivity.class);
			break;
		case R.id.rl_title_right:
			 if (TextUtils.isEmpty(ori_seq)) {
					showToast(mContext, "未生成订单，无法强制完成操作");
				} else {
					showConfirmDialog();
				}	 
			break;
		default:
			break;
		}

	}
	
	/***
	 * 强制完成
	 */
	public void showConfirmDialog() {
		mCustomDialog = new CustomDialog(mContext, "", "确认订单 " + ori_seq + " 是否生成", itemsOnClick);
		mCustomDialog.show();
	}

	// 为弹出窗口实现监听类
	public OnClickListener itemsOnClick = new OnClickListener() {

		public void onClick(View v) {
			Intent intent = null;
			switch (v.getId()) {
			case R.id.btn_cancel:// 取消
				mCustomDialog.cancel();
				if (isTimeOut) {
					intent = new Intent(getActivity(),MainActivity.class);
					startActivity(intent);
					getActivity().finish();
				}
				break;
			case R.id.btn_confirm:// 确定
				PayUtils.openSuccessActivity(mActivity, PaymentSuccessActivity.class,
						amount, ori_seq,"","1", true,UnionPayQRConstant.QRCODE);
				break;
			default:
				break;
			}
		}
	};

	private String operaterId;

	/**
	 * 查询订单
	 */
	public void initQueryOrder() {
		if (isStart) {
			mHandler.sendEmptyMessageDelayed(7, 3000);
		}
	}
	
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mHandler.removeCallbacksAndMessages(null);
		if (mTimeCount != null) {
			mTimeCount.cancel();
		}
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		isStart=false;
		mPayRequestPresenter.detachView();
		if (mTimeCount != null) {
			mTimeCount.cancel();
		}
	}
	
	

	@Override
	public void getQueryPayResult(UnionPayResult resultData) {
		if (resultData != null) {
			String respCode = resultData.getRespCode();
			if (respCode.equals("00")) {
				String respCd = resultData.getOrigRespCode();
				ori_queryId=resultData.getQueryId();
				traceTime = resultData.getTraceTime();
				PayUtils.checkPayResult(respCd,mHandler);
			}else{
				dismissProgressDialog();
				showToast(mContext, UnionPayResult.getRespCode(respCode));
			}
		}
		
	}
	
	
	@Override
	public void showNetError(OnClickListener onClickListener) {
		PayUtils.sendMsg(Constant.ORDER_TIMEOUT,mHandler);
		showLoading(Constant.VIEW_WIFIFAILUER);
	}
	
	@Override
	public void showError(String msg, OnClickListener onClickListener) {
		PayUtils.sendMsg(Constant.ORDER_ABNORMAL,mHandler);
		showLoading(Constant.VIEW_NODATA);
	}

	@Override
	public void getScanPayResult(UnionPayResult resultData) {
		if (resultData != null) {
			String respCode = resultData.getRespCode();
			if (respCode.equals("00")) {
				if (!TextUtils.isEmpty(resultData.getQrCode())) {
					showLoading(Constant.VIEW_SHOW);
					txt_explain.setText("生成二维码,正在等待买家支付");
					ori_seq=resultData.getOrderId();
					Bitmap bitmap = CodeUtils.create2Code(resultData.getQrCode(), img_code);
					Bitmap headBitmap = CodeUtils.getHeadBitmap(60, mActivity);
					if (bitmap != null && headBitmap != null) {
						PayUtils.sendMsg(Constant.ORDER_FAIL,mHandler);
						CodeUtils.createQRCodeBitmapWithPortrait(bitmap, headBitmap);
						mTimeCount.start();
						mHandler.sendEmptyMessageDelayed(Constant.QUERY_START, 2000);
					}
				} else {
					showLoading(Constant.VIEW_LOADFAILURE);
				}
			}else{
				dismissProgressDialog();
				showToast(mContext, UnionPayResult.getRespCode(respCode));
			}
		}
	}
	
	private class TimeCount extends CountDownTimer {

		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			long countDown = millisUntilFinished / 1000;
			if (countDown > 1) {
				tv_count_down.setText(millisUntilFinished / 1000 + "s");
			} else {
				tv_count_down.setText(null);
			}
		}

		@Override
		public void onFinish() {
			if (mHandler != null) {
				mHandler.removeCallbacksAndMessages(null);
			}
			isTimeOut = true;
			tv_count_down.setText(null);
			showConfirmDialog();
		}
	}
}
