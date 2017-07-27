package com.zng.unionpayqr.pro.cashier.activity;

import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zng.unionpayqr.R;
import com.zng.unionpayqr.base.BaseActivity;
import com.zng.unionpayqr.base.UnionPay;
import com.zng.unionpayqr.model.OrderRecordTable;
import com.zng.unionpayqr.model.PayResultInfo;
import com.zng.unionpayqr.pro.MainActivity;
import com.zng.unionpayqr.utils.AppManager;
import com.zng.unionpayqr.utils.PayUtils;
import com.zng.unionpayqr.utils.SPUtils;
import com.zng.unionpayqr.utils.UnionPayQRConstant;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.activity_payment_success)
public class PaymentSuccessActivity extends BaseActivity {
	
	@ViewInject(R.id.tv_money)
	private TextView tv_money;

	@ViewInject(R.id.rel_order_code)
	private RelativeLayout rel_order_code;

	@ViewInject(R.id.tv_order_code)
	private TextView tv_order_code;

	@ViewInject(R.id.tv_order_sign)
	private TextView tv_order_sign;

	@ViewInject(R.id.tv_order_state)
	private TextView tv_order_state;

	@ViewInject(R.id.tv_order_time)
	private TextView tv_order_time;

	@ViewInject(R.id.tv_order_pay_way)
	private TextView tv_order_pay_way;

	@ViewInject(R.id.img_back_money)
	private ImageView img_back_money;

	@ViewInject(R.id.rel_money)
	private RelativeLayout rel_money;
	
	private PayResultInfo mPayResultInfo;

//	private PrintPowerOnAndOffUtils mPrintPowerOnAndOffUtils;
//	private PrintUtils mPrintUtils;
	private boolean isPower;// 是否上电
	private boolean isFinishPrint = false;// 是否打印完成，默认为未打印状态
	private boolean isAgainPrint = false;// 是否重新打印

	private String order_code;// 订单号（商户生成的交易流水号）
	private String order_money;// 订单金额
	private String order_date;// 订单支付时间
	private String order_date2;// 订单支付时间
	private String order_state;// 订单状态
	private String pay_way;// 支付类型
	private String order_queryId;// 银联支付订单号
	private boolean isOrderForce;// 是否强制完成
	private String order_merchantNmu;//商户号
	
	@Override
	public void initContentView() {
		AppManager.getAppManager().addActivity(this);
		showRigehtImage(R.drawable.title_reprint_img_bg, this);
		setRightTitle(getString(R.string.tv_reprint_text));
		addCancleBtn();
		
		mPayResultInfo = this.getIntent().getParcelableExtra("PayResultInfo");
		if (mPayResultInfo != null) {
			order_code = mPayResultInfo.getOrder_code();
			order_money = mPayResultInfo.getOrder_money();
			order_date = mPayResultInfo.getOrder_pay_date();
			order_date2 = mPayResultInfo.getTraceTime();
			order_queryId = mPayResultInfo.getOrder_query();
			isOrderForce = mPayResultInfo.getOrder_success_force();
			pay_way = mPayResultInfo.getPay_way();
			order_merchantNmu = SPUtils.getString(mContext,
					UnionPayQRConstant.PUBLIC_KEY, "");
			
			if (TextUtils.isEmpty(order_money)) {
				rel_money.setVisibility(View.GONE);
			} else {
				tv_money.setText(order_money + "元");
			}
			
			tv_order_code.setText(order_code);
			tv_order_sign.setText(order_queryId);
			
			if (!TextUtils.isEmpty(pay_way)) {
				order_state = PayUtils.getOrderState(pay_way);
			}
			tv_order_pay_way.setText(order_state);
			if (isOrderForce) {
				tv_order_state.setText("强制完成");
			} else {
				tv_order_state.setText("支付成功");
			}
			tv_order_time.setText(order_date);
			
			setActionBarTitle(order_state+"支付成功");
			show_login_operation.setText(operaterId+"已登陆");
			show_login_operation.setTextColor(getResources().getColor(R.color.white));
			
		}
		initData();
	}

	@Override
	public void initData() {
//		mPrintPowerOnAndOffUtils = new PrintPowerOnAndOffUtils(mContext);
//		mPrintUtils = new PrintUtils(mContext);
//		isPower = mPrintPowerOnAndOffUtils.checkPowerStatus();
//		if (!isPower) {
//			mPrintPowerOnAndOffUtils.powerOn();
//			SystemClock.sleep(Intergers.PRINTER_POWERON_DELAY_TIME);
//		}
		setSaveDBInfo();
		printContent();
	}

	/***
	 * 保存交易数据到数据库
	 */
	public void setSaveDBInfo() {
		DbManager db = x.getDb(((UnionPay) getApplicationContext()).getDaoConfig());
		OrderRecordTable mOrderRecordTable = new OrderRecordTable();
		mOrderRecordTable.setAll_OrderNumber(order_code);//商户订单号
		mOrderRecordTable.setAll_VoucherNumber(order_queryId);//银联查询订单号
		mOrderRecordTable.setAll_TradeMoney(order_money);//交易金额
		mOrderRecordTable.setAll_TradeDateTime(order_date);//交易时间
		mOrderRecordTable.setAll_TradePayWay(pay_way);//支付方式
		mOrderRecordTable.setAll_TradeDateTime2(order_date2);
		mOrderRecordTable.setIsOrderForce(isOrderForce);//是否是强制完成
		mOrderRecordTable.setAll_MerchantNum(order_merchantNmu);//设置商户号
		mOrderRecordTable.setAll_TradeUser(loginId);
		
		try {
			OrderRecordTable findFirst = db.selector(OrderRecordTable.class).where("all_OrderNumber", "=", order_code)
			.findFirst();
			if (findFirst == null) {
				db.save(mOrderRecordTable);
			}
		} catch (DbException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void setListener() {
		title_cancle_bt.setOnClickListener(this);
		img_back_money.setOnClickListener(this);
		rl_title_right.setOnClickListener(this);
	}

	@Override
	protected void onClickEvent(View paramView) {

		switch (paramView.getId()) {
		case R.id.title_cancle_bt:

			if (isFinishPrint) {
				openActivity(MainActivity.class);
				finish();
			} else {
				showToast(mContext, "打印中...请稍后重试!");
			}
			break;
		case R.id.img_back_money:
			if (isFinishPrint) {
				openActivity(MainActivity.class);
				finish();
			} else {
				showToast(mContext, "打印中...请稍后重试!");
			}
			break;
		case R.id.rl_title_right:
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

	private void printContent() {
		isFinishPrint = false;
//		isPower = mPrintPowerOnAndOffUtils.checkPowerStatus();
//		if (!isPower) {
//			mPrintPowerOnAndOffUtils.powerOn();
//			SystemClock.sleep(Intergers.PRINTER_POWERON_DELAY_TIME);
//		}
		
		new Thread() {
			@Override
			public void run() {
//				int ret = mPrintUtils.detectionPrinter();
//				Log.i("liujie", "ret" + ret);

//				if (ret == 0) {
//					mPrintUtils.startPrint();
//					mPrintUtils.printerSetLineAlign(1);
//					mPrintUtils.printText("POS 签购单");
//					mPrintUtils.printText("(消费单)");
//					mPrintUtils.printerSetLineAlign(0);
//					mPrintUtils.printText("商户编号："+order_merchantNmu);
//					mPrintUtils.printText("订 单 号：");
//					mPrintUtils.printText("  " + order_code);
//					mPrintUtils.printText("凭 证 号：");
//					mPrintUtils.printText("  " + order_queryId);
//					mPrintUtils.printText("操 作 员："+loginId);
//					mPrintUtils.printText("当前状态：支付成功");
//					mPrintUtils.printText("-----付 款 方 式-----");
//					mPrintUtils.printText(order_state);
//					if (!TextUtils.isEmpty(order_money)) {
//						mPrintUtils.printText("金 额：" + order_money + "元");
//					}
//					mPrintUtils.printText("交易时间：" + order_date);
//					if (isAgainPrint) {
//						mPrintUtils.printText("备注：重打印凭证");
//					}
//					mPrintUtils.stopPrint();
					SystemClock.sleep(150);
//				}
				isFinishPrint = true;
			}
		}.start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
//		mPrintPowerOnAndOffUtils.powerOff();
	}
}
