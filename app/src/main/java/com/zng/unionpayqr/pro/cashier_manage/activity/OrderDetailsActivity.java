package com.zng.unionpayqr.pro.cashier_manage.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zng.unionpayqr.R;
import com.zng.unionpayqr.base.BaseActivity;
import com.zng.unionpayqr.base.UnionPay;
import com.zng.unionpayqr.model.OrderRecordTable;
import com.zng.unionpayqr.model.UnionPayResult;
import com.zng.unionpayqr.presenter.UnionPayPresenter;
import com.zng.unionpayqr.sdk.AcpService;
import com.zng.unionpayqr.sdk.LogUtil;
import com.zng.unionpayqr.sdk.SDKConfig;
import com.zng.unionpayqr.utils.AppManager;
import com.zng.unionpayqr.utils.DemoBase;
import com.zng.unionpayqr.utils.NetworkUtil;
import com.zng.unionpayqr.utils.PayParams;
import com.zng.unionpayqr.utils.PayUtils;
import com.zng.unionpayqr.utils.SPUtils;
import com.zng.unionpayqr.utils.UnionPayQRConstant;
import com.zng.unionpayqr.view.ScanPayView;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.Map;

@ContentView(R.layout.activity_order_details)
public class OrderDetailsActivity extends BaseActivity implements ScanPayView {

	@ViewInject(R.id.tv_money)
	private TextView tv_money;// 订单金额

	@ViewInject(R.id.tv_order_state)
	private TextView tv_order_state;// 订单状态

	@ViewInject(R.id.tv_order_time)
	private TextView tv_order_time;// 交易时间

	@ViewInject(R.id.rel_refund_time)
	private RelativeLayout rel_refund_time;// 退款时间显示

	@ViewInject(R.id.tv_refund_time)
	private TextView tv_refund_time;// 退款时间

	@ViewInject(R.id.tv_order_pay_way)
	private TextView tv_order_pay_way;// 支付方式

	@ViewInject(R.id.tv_order_type)
	private TextView tv_order_type;// 订单类型

	@ViewInject(R.id.tv_order_operator)
	private TextView tv_order_operator;// 操作员

	@ViewInject(R.id.rel_order_refund_operator)
	private RelativeLayout rel_order_refund_operator;// 退款操作员显示

	@ViewInject(R.id.tv_order_refund_operator)
	private TextView tv_order_refund_operator;// 退款操作员

	@ViewInject(R.id.tv_order_code)
	private TextView tv_order_code;// 订单号

	@ViewInject(R.id.tv_order_sign)
	private TextView tv_order_sign;// 凭证号

	@ViewInject(R.id.btn_refund)
	private Button btn_refund;// 退款

	@ViewInject(R.id.btn_refund_query)
	private Button btn_refund_query;// 退款查询

	@ViewInject(R.id.btn_print)
	private Button btn_print;// 打印

	private OrderRecordTable mOrderRecordTable;

//	private PrintPowerOnAndOffUtils mPrintPowerOnAndOffUtils;
//	private PrintUtils mPrintUtils;
	private boolean isPower;// 是否上电
	private boolean isFinishPrint = true;// 是否打印完成，默认为未打印状态
	private boolean isAgainPrint = false;// 是否重新打印

	private String order_code;// 商户生成订单号
	private String order_voucher_no;// 订单凭证号(银联查询订单号)
	private String order_money;// 订单金额
	private String order_date;// 订单支付时间
	private String order_refund_date;// 退款时间
	private String pay_way;// 支付方式
	private String order_type;// 订单类型
	private boolean isRefund;// 是否退款
	private boolean isOrderForce;//是否强制完成
	private UnionPayPresenter mQueryRefundPresenter;
	private String order_merchantNmu;//商户号
	private final int REFUND_ID = 0;
	private final int TRADEORDERNOTEXIST = 1;
	private final int NETWORK_ERROR = 2;
	private final int VALIDATEERROR = 3;
	
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REFUND_ID:
				dismissProgressDialog();
				Bundle bundle = new Bundle();
				bundle.putParcelable("OrderRecordTable", mOrderRecordTable);
				openActivity(OrderRefundActivity.class, bundle);
				finish();
				break;
			case TRADEORDERNOTEXIST:
				dismissProgressDialog();
				String resultCode = (String) msg.obj;
				if (!TextUtils.isEmpty(resultCode)&&
						resultCode.equals("05")) {
					showToast(mContext, "此交易不存在");
				}else{
					showToast(mContext, ""+UnionPayResult.getRespCode(resultCode));
				}
				break;
			case NETWORK_ERROR:
				dismissProgressDialog();
				showToast(mContext, "网络通讯异常");
				break;
			case VALIDATEERROR:
				dismissProgressDialog();
				showToast(mContext, "商户安全校验失败");
				break;
			default:
				break;
			}
			
		};
	};
	
	@Override
	public void initContentView() {
		AppManager.getAppManager().addActivity(this);
		addBackBtn();
		rl_title_right.setVisibility(View.GONE);
		
		mOrderRecordTable = this.getIntent().getParcelableExtra("OrderRecordTable");
		if (mOrderRecordTable != null) {
			order_code = mOrderRecordTable.getAll_OrderNumber();
			order_voucher_no = mOrderRecordTable.getAll_VoucherNumber();
			order_money = mOrderRecordTable.getAll_TradeMoney();
			order_date = mOrderRecordTable.getAll_TradeDateTime();
			order_refund_date = mOrderRecordTable.getAll_NewDateTime();
			pay_way = mOrderRecordTable.getAll_TradePayWay();
			order_type = PayUtils.getOrderState(pay_way);
			order_merchantNmu = SPUtils.getString(mContext,
					UnionPayQRConstant.PUBLIC_KEY, "");
			isRefund = mOrderRecordTable.getIsRefund();
			isOrderForce=mOrderRecordTable.getIsOrderForce();
			tv_money.setText(order_money + "元");
			tv_order_code.setText(order_code);
			tv_order_sign.setText(order_voucher_no);
			tv_order_time.setText(order_date);
			tv_refund_time.setText(order_refund_date);
			tv_refund_time.setText(mOrderRecordTable.getAll_NewDateTime());
			if (isOrderForce) {
				tv_order_type.setText(order_type+"(强制完成)");
			} else {
				tv_order_type.setText(order_type);
			}
			if (isRefund) {
				btn_refund.setText("退款查询");
				tv_order_state.setText("支付成功（退款）");
				rel_refund_time.setVisibility(View.VISIBLE);
				rel_order_refund_operator.setVisibility(View.GONE);
				tv_order_pay_way.setText(order_type);
			} else {
				btn_refund.setText("退款");
				tv_order_state.setText("支付成功");
				rel_refund_time.setVisibility(View.GONE);
				rel_order_refund_operator.setVisibility(View.GONE);
				tv_order_pay_way.setText(order_type);
			}
			
			setActionBarTitle(order_type+"订单详情");
			show_login_operation.setText(operaterId+"已登陆");
			show_login_operation.setTextColor(getResources().getColor(R.color.white));
			
		}
		initData();
	}

	@Override
	public void initData() {
		mQueryRefundPresenter = new UnionPayPresenter();
		mQueryRefundPresenter.attachView(this);
		
//		if (NetworkUtil.checkNetwork(mContext)) {
//			isForceAddData();
//		}
		
//		mPrintPowerOnAndOffUtils = new PrintPowerOnAndOffUtils(mContext);
//		mPrintUtils = new PrintUtils(mContext);
//		isPower = mPrintPowerOnAndOffUtils.checkPowerStatus();
//		if (!isPower) {
//			mPrintPowerOnAndOffUtils.powerOn();
//			SystemClock.sleep(Intergers.PRINTER_POWERON_DELAY_TIME);
//		}
	}
	
	

	@Override
	protected void setListener() {
		title_back_bt.setOnClickListener(this);
		btn_refund.setOnClickListener(this);
		btn_refund_query.setOnClickListener(this);
		btn_print.setOnClickListener(this);
	}

	@Override
	protected void onClickEvent(View paramView) {

		switch (paramView.getId()) {
		case R.id.title_back_bt:
			if (isFinishPrint) {
				finish();
			} else {
				showToast(mContext, "打印中...请稍后重试!");
			}
			break;
		case R.id.btn_refund:
			if (isFinishPrint) {
				if (NetworkUtil.checkNetwork(mContext)) {
					if (isRefund) {
						showProgressDialog("查询中，请稍后...");
						mQueryRefundPresenter.queryPayStatus(mContext, 
								UnionPayQRConstant.QUERY_PAY_REQUEST, order_code);
					}else{
						if (isOrderForce) {
							showProgressDialog("此订单为强制完成，请稍后...");
							isForceAddData();
						}else{
							mHandler.sendEmptyMessage(REFUND_ID);
						}
					}
				}else{
					showToast(mContext, "网络连接失败，请先配置网络");
				}
			} else {
				showToast(mContext, "打印中...请稍后重试!");
			}
			break;
		case R.id.btn_refund_query:
			if (NetworkUtil.checkNetwork(mContext)) {
				if (isRefund) {
					showProgressDialog("查询中，请稍后...");
					mQueryRefundPresenter.queryPayStatus(mContext, 
							UnionPayQRConstant.QUERY_PAY_REQUEST, order_code);
				}
			}else{
				showToast(mContext, "网络连接失败，请先配置网络");
			}
			break;
		case R.id.btn_print:
			if (isFinishPrint) {
				isAgainPrint = true;
				printContent();
			} else {
				showToast(mContext, "打印中...请稍后重试!");
			}
			break;

		default:
			break;
		}

	}

	public void checkPassword() {
		if (isFinishPrint) {
			if (!NetworkUtil.checkNetwork(mContext)) {
				showToast(mContext, "当前网络未开启,请开启网络");
				return;
			}
			if (loginId.equals("99")) {
				String error_system = "做退款交易需一般管理员登录!";
				showToast(mContext, error_system);
				return;
			} else if (loginId.equals("00")) {
				String error_system = "做退款交易需一般管理员登录!";
				showToast(mContext, error_system);
				return;
			}
			Bundle bundle = new Bundle();
			bundle.putString("intent_activity", this.getClass().getName());
			bundle.putParcelable("OrderRecordTable", mOrderRecordTable);
//			openActivity(InputSupervisorPasswordActivity.class, bundle);
//			mPrintPowerOnAndOffUtils.powerOff();
			return;
		} else {
			showToast(mContext, "打印中...请稍后重试!");
		}
	}

	private void printContent() {
		isFinishPrint = false;
//		isPower = mPrintPowerOnAndOffUtils.checkPowerStatus();
//		if (!isPower) {
//			mPrintPowerOnAndOffUtils.powerOn();
//			SystemClock.sleep(Intergers.PRINTER_POWERON_DELAY_TIME);
//		}

//		new Thread() {
//			@Override
//			public void run() {
//				int ret = mPrintUtils.detectionPrinter();
//				if (ret == 0) {
//					mPrintUtils.startPrint();
//					mPrintUtils.printerSetLineAlign(1);
//					mPrintUtils.printText("POS 签购单");
//					mPrintUtils.printText("(订单详情)");
//					mPrintUtils.printerSetLineAlign(0);
//					mPrintUtils.printText("商户编号："+order_merchantNmu);
//					if (!TextUtils.isEmpty(order_money)) {
//						mPrintUtils.printText("金 额：" + order_money + "元");
//					}
//					if (isRefund) {
//						mPrintUtils.printText("交易状态：支付成功(有退款)");
//					} else {
//						mPrintUtils.printText("交易状态：支付成功");
//					}
//					mPrintUtils.printText("交易时间：" + order_date);
//					if (!TextUtils.isEmpty(order_refund_date)) {
//						mPrintUtils.printText("退款时间：" + order_refund_date);
//					}
//					mPrintUtils.printText("交易方 式："+order_type);
//					mPrintUtils.printText("订 单 号：");
//					mPrintUtils.printText("  " + order_code);
//					mPrintUtils.printText("凭 证 号：");
//					mPrintUtils.printText("  " + order_voucher_no);
//					mPrintUtils.printText("操 作 员："+loginId);
//					mPrintUtils.stopPrint();
//					SystemClock.sleep(150);
//				}
				isFinishPrint = true;
//			}
//		}.start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
//		mPrintPowerOnAndOffUtils.powerOff();
	}

	@Override
	public void showNetError(OnClickListener onClickListener) {
		super.showNetError(onClickListener);
	}
	
	@Override
	public void getScanPayResult(UnionPayResult resultData) {
	}

	@Override
	public void getQueryPayResult(UnionPayResult resultData) {
		if (resultData != null) {
			String respCode = resultData.getRespCode();
			if (respCode.equals("00")) {
				String respCd = resultData.getOrigRespCode();
				showToast(mContext, UnionPayResult.getRespCode(respCd));
				if (!TextUtils.isEmpty(respCd)&&respCd.equals("00")) {
					showToast(mContext, "退款成功");
				}else{
					showToast(mContext, UnionPayResult.getRespCode(respCd));
				}
			}else{
				dismissProgressDialog();
				showToast(mContext, UnionPayResult.getRespCode(respCode));
			}
			dismissProgressDialog();
		}
	}
	
	/***
	 * 保存交易数据到数据库
	 */
	public void setSaveDBInfo(String order_voucher_no){
		DbManager db = x.getDb(((UnionPay)getApplicationContext()).getDaoConfig());
		mOrderRecordTable.setIsRefund(true);
		mOrderRecordTable.setAll_NewUser(loginId);
		mOrderRecordTable.setAll_VoucherNumber(order_voucher_no);
		try {
			db.saveOrUpdate(mOrderRecordTable);
			mHandler.sendEmptyMessage(REFUND_ID);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 若是强制完成则查询添加数据
	 */
	private void isForceAddData(){
		if (isOrderForce && !TextUtils.isEmpty(order_voucher_no) 
				&& order_voucher_no.equals("1")) {
			new Thread(new Runnable() {
				public void run() {
					Map<String, String> requestParam = PayParams.queryPayRequest(mContext,order_code);
					String url = SDKConfig.getConfig().getSingleQueryUrl();								//交易请求url从配置文件读取对应属性文件acp_sdk.properties中的 acpsdk.singleQueryUrl
					Map<String, String> rspData = AcpService.post(requestParam, url,DemoBase.encoding);
					Message msg = mHandler.obtainMessage();
					if(!rspData.isEmpty()){
						if(AcpService.validate(rspData, DemoBase.encoding)){
							LogUtil.writeLog("验证签名成功");
							String rspCode = rspData.get("respCode");
							if(("00").equals(rspCode)){//如果查询交易成功
								String origRespCode = rspData.get("origRespCode");
								if(("00").equals(origRespCode)){
									//交易成功，更新商户订单状态
									order_voucher_no = rspData.get("queryId");
									setSaveDBInfo(order_voucher_no);
								}else{
									msg.what = TRADEORDERNOTEXIST;
									msg.obj = origRespCode;
									mHandler.sendMessage(msg);
								}
							}else{
								msg.what = TRADEORDERNOTEXIST;
								msg.obj = rspCode;
								mHandler.sendMessage(msg);
							}
						}else{
							mHandler.sendEmptyMessage(VALIDATEERROR);
						}
					}else{
						mHandler.sendEmptyMessage(NETWORK_ERROR);
					}
				}
			}).start();
		}
	}
}
