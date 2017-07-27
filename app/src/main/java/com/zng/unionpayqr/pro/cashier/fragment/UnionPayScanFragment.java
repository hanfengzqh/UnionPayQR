package com.zng.unionpayqr.pro.cashier.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.Result;
import com.zng.unionpayqr.R;
import com.zng.unionpayqr.base.BaseFragment;
import com.zng.unionpayqr.model.UnionPayResult;
import com.zng.unionpayqr.presenter.UnionPayPresenter;
import com.zng.unionpayqr.pro.cashier.activity.PaymentSuccessActivity;
import com.zng.unionpayqr.utils.AmountUtils;
import com.zng.unionpayqr.utils.Constant;
import com.zng.unionpayqr.utils.PayUtils;
import com.zng.unionpayqr.utils.UnionPayQRConstant;
import com.zng.unionpayqr.view.ScanPayView;
import com.zng.unionpayqr.widget.CustomDialog;
import com.zng.unionpayqr.zxing.ScanListener;
import com.zng.unionpayqr.zxing.ScanManager;
import com.zng.unionpayqr.zxing.decode.DecodeThread;
import com.zng.unionpayqr.zxing.decode.ZxingUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

@ContentView(R.layout.fragment_scan_code)
public class UnionPayScanFragment extends BaseFragment implements ScanPayView, ScanListener {

	@ViewInject(R.id.capture_preview)
	private SurfaceView scanPreview;
	@ViewInject(R.id.capture_container)
	View scanContainer;
	@ViewInject(R.id.capture_crop_view)
	View scanCropView;
	@ViewInject(R.id.capture_scan_line)
	ImageView scanLine;
	@ViewInject(R.id.service_register_rescan)
	Button rescan;
	@ViewInject(R.id.scan_image)
	ImageView scan_image;
	@ViewInject(R.id.lyt_back)
	LinearLayout lyt_back;
	@ViewInject(R.id.tv_scan_result)
	TextView tv_scan_result;
	@ViewInject(R.id.scan_hint)
	TextView scan_hint;
	
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

	ScanManager scanManager;
	final int PHOTOREQUESTCODE = 1111;
	private UnionPayPresenter mUnionPayPresenter;
	private String amount;// 金额
	private String ori_seq;// 查询订单号
	private boolean isStart=true;
	private String order_amount;//订单金额
	private String traceTime;//交易完成时间
	private String queryId;//银联订单号
	private CustomDialog mCustomDialog;
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			String codeInfo = (String) msg.obj;
			switch (msg.what) {
			case Constant.ORDER_SUCCESS://0
				dismissProgressDialog();
				mHandler.removeCallbacksAndMessages(null);
				showToast(mContext, "支付成功");
				scan_hint.setText(codeInfo);
				isStart = false;
				PayUtils.openSuccessActivity(mActivity, PaymentSuccessActivity.class, amount,ori_seq,
						 traceTime,queryId, false,UnionPayQRConstant.QRSCAN);
				break;
			case Constant.ORDER_FAIL://1
				scan_hint.setText("订单生成,正在等待买家支付");
				showToast(mContext, codeInfo+"");
				dismissProgressDialog();
				mHandler.removeCallbacksAndMessages(null);
				startScan();
				break;
			case Constant.ORDER_ABNORMAL://2
				dismissProgressDialog();
				scan_hint.setText(codeInfo);
				isStart = false;
				startScan();
				break;
			case Constant.ORDER_TIMEOUT://3
				dismissProgressDialog();
				scan_hint.setText("网络异常,请检查网络");
				isStart = false;
				break;
			case Constant.QUERY_START://6
				initQueryOrder();
				break;
			case 7:
				mUnionPayPresenter.queryPayStatus(mContext, 
						UnionPayQRConstant.QUERY_PAY_REQUEST, ori_seq);
				mHandler.sendEmptyMessageDelayed(7, 2000);
				break;
			default:
				break;
			}

		}
	};
	
	@Override
	public void initContentView() {
		mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		operaterId = PayUtils.getLoginOperId(mContext);
		title_main_tv.setText("银联扫码支付");
		show_login_operation.setText(operaterId+"已登陆");
		show_login_operation.setTextColor(getResources().getColor(R.color.white));
		title_back_bt.setVisibility(View.INVISIBLE);
		title_cancle_bt.setVisibility(View.VISIBLE);
		
		amount = this.getArguments().getString("amount");
		mUnionPayPresenter = new UnionPayPresenter();
		mUnionPayPresenter.attachView(this);
		// 构造出扫描管理器
		scanManager = new ScanManager(mActivity, scanPreview, scanContainer, scanCropView, scanLine,
				UnionPayQRConstant.REQUEST_SCAN_MODE_ALL_MODE, this);
		if (!TextUtils.isEmpty(amount)) {
			tv_scan_result.setText("金额:" + amount);
			order_amount = AmountUtils.changeY2F(amount);
		}
	}

	/**
	 * 生成被扫订单
	 * @param auth_code
	 *            授权码
	 */
	public void initCodePay(String auth_code) {
		showProgressDialog("支付中...");
		mUnionPayPresenter.scanPayData(mContext, UnionPayQRConstant.SCAN_PAY_REQUEST, order_amount, auth_code);
	}

	/**
	 * 查询订单
	 */
	public void initQueryOrder() {
		if (isStart) {
			mHandler.sendEmptyMessageDelayed(7, 3000);
		}
	}

	@Override
	protected void setListener() {
		title_cancle_bt.setOnClickListener(this);
		rl_title_right.setOnClickListener(this);
	}

	@Override
	protected void onClickEvent(View paramView) {
		switch (paramView.getId()) {
		case R.id.service_register_rescan:// 再次开启扫描
			startScan();
			mHandler.removeCallbacksAndMessages(null);
			break;
		case R.id.title_cancle_bt:
			mActivity.finish();
			mHandler.removeCallbacksAndMessages(null);
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
	
	@Override
	public void getScanPayResult(UnionPayResult resultData) {
		if (resultData != null) {
			String respCode = resultData.getRespCode();
			if (respCode.equals("00")) {
				ori_seq = resultData.getOrderId();
				mHandler.sendEmptyMessageDelayed(Constant.QUERY_START, 2000);
			}else{
				dismissProgressDialog();
				showToast(mContext, UnionPayResult.getRespCode(respCode));
				mHandler.removeCallbacksAndMessages(null);
				startScan();
			}
		}
	}

	@Override
	public void getQueryPayResult(UnionPayResult resultData) {
		if (resultData != null) {
			String respCode = resultData.getRespCode();
			if (respCode.equals("00")) {
				String respCd = resultData.getOrigRespCode();
				traceTime = resultData.getTraceTime();
				queryId = resultData.getQueryId();
				PayUtils.checkPayResult(respCd, mHandler);
			}else{
				dismissProgressDialog();
				showToast(mContext, UnionPayResult.getRespCode(respCode));
				mHandler.removeCallbacksAndMessages(null);
			}
		}
	}

	@Override
	public void showNetError(OnClickListener onClickListener) {
		PayUtils.sendMsg(Constant.ORDER_TIMEOUT, mHandler);
		dismissProgressDialog();
	}

	@Override
	public void showError(String msg, OnClickListener onClickListener) {
		PayUtils.sendMsg(Constant.ORDER_ABNORMAL, mHandler);
		dismissProgressDialog();
	}

	public void scanResult(Result rawResult, Bundle bundle) {
		// 扫描成功后，扫描器不会再连续扫描，如需连续扫描，调用reScan()方法。
		// scanManager.reScan();
		if (!scanManager.isScanning()) { // 如果当前不是在扫描状态
			// 设置再次扫描按钮出现
//			rescan.setVisibility(View.VISIBLE);
//			scan_image.setVisibility(View.VISIBLE);
			Bitmap barcode = null;
			byte[] compressedBitmap = bundle.getByteArray(DecodeThread.BARCODE_BITMAP);
			if (compressedBitmap != null) {
				barcode = BitmapFactory.decodeByteArray(compressedBitmap, 0, compressedBitmap.length, null);
				barcode = barcode.copy(Bitmap.Config.ARGB_8888, true);
			}
			scan_image.setImageBitmap(barcode);
		}
//		rescan.setVisibility(View.VISIBLE);
//		scan_image.setVisibility(View.VISIBLE);
		if (TextUtils.isEmpty(rawResult.getText())) {
			showToast(mContext, "扫描失败");
			startScan();
		} else {
			initCodePay(rawResult.getText());
		}

	}

	void startScan() {
//		if (rescan.getVisibility() == View.VISIBLE) {
//			rescan.setVisibility(View.INVISIBLE);
//			scan_image.setVisibility(View.GONE);
			scanManager.reScan();
//		}
	}

	@Override
	public void scanError(Exception e) {
		showToast(mContext, e.getMessage());
		// 相机扫描出错时
		if (e.getMessage() != null && e.getMessage().startsWith("相机")) {
			scanPreview.setVisibility(View.INVISIBLE);
		}
	}

	public void showPictures(int requestCode) {
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		startActivityForResult(intent, requestCode);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mHandler.removeCallbacksAndMessages(null);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		String photo_path;
		if (resultCode == mActivity.RESULT_OK) {
			switch (requestCode) {
			case PHOTOREQUESTCODE:
				String[] proj = { MediaStore.Images.Media.DATA };
				Cursor cursor = mActivity.getContentResolver().query(data.getData(), proj, null, null, null);
				if (cursor.moveToFirst()) {
					int colum_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
					photo_path = cursor.getString(colum_index);
					if (photo_path == null) {
						photo_path = ZxingUtils.getPath(mActivity.getApplicationContext(), data.getData());
					}
					scanManager.scanningImage(photo_path);
				}
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		scanManager.onResume();
		rescan.setVisibility(View.INVISIBLE);
		scan_image.setVisibility(View.GONE);
	}

	@Override
	public void onPause() {
		super.onPause();
		scanManager.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mUnionPayPresenter.detachView();
		scanManager.onDestroy();
		isStart = false;
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
				break;
			case R.id.btn_confirm:// 确定
				PayUtils.openSuccessActivity(mActivity, PaymentSuccessActivity.class,
						amount, ori_seq,"","1", true,UnionPayQRConstant.QRSCAN);
				break;
			default:
				break;
			}
		}
	};
	private String operaterId;
}
