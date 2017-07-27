package com.zng.unionpayqr.pro.cashier_manage.activity;

import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zng.unionpayqr.R;
import com.zng.unionpayqr.base.BaseActivity;
import com.zng.unionpayqr.model.OrderRecordTable;
import com.zng.unionpayqr.utils.AppManager;
import com.zng.unionpayqr.utils.PayUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

@ContentView(R.layout.activity_order_refund_success)
public class OrderRefundSuccessActivity extends BaseActivity {

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

	@ViewInject(R.id.tv_order_type)
	private TextView tv_order_type;// 订单类型

	@ViewInject(R.id.tv_order_code)
	private TextView tv_order_code;// 订单号

	@ViewInject(R.id.tv_order_sign)
	private TextView tv_order_sign;// 凭证号

	@ViewInject(R.id.btn_print)
	private Button btn_print;// 打印

	private OrderRecordTable mOrderRecordTable;

//	private PrintPowerOnAndOffUtils mPrintPowerOnAndOffUtils;
//	private PrintUtils mPrintUtils;
	private boolean isPower;// 是否上电
	private boolean isFinishPrint = true;// 是否打印完成，默认为未打印状态
	private boolean isAgainPrint = false;// 是否重新打印
	private String order_code;// 订单号
	private String order_voucher_no;// 订单凭证号
	private String order_money;// 订单金额
	private String order_date;// 订单支付时间
	private String order_refund_date;// 退款时间
	private String pay_way;// 支付方式
	private String order_type;// 订单类型
	

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
			
			tv_money.setText(order_money + "元");
			tv_order_code.setText(order_code);
			tv_order_sign.setText(order_voucher_no);
			tv_order_time.setText(order_date);
			tv_refund_time.setText(order_refund_date);
			tv_order_type.setText(order_type);
			tv_refund_time.setText(mOrderRecordTable.getAll_NewDateTime());
			tv_order_state.setText("支付成功(退款)");
			rel_refund_time.setVisibility(View.VISIBLE);
			
			setActionBarTitle(order_type+"订单详情");
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
//		}
		printContent();
	}

	@Override
	protected void setListener() {
		title_back_bt.setOnClickListener(this);
		btn_print.setOnClickListener(this);
	}

	@Override
	protected void onClickEvent(View paramView) {

		switch (paramView.getId()) {
		case R.id.title_back_bt:
			if (isFinishPrint) {
				AppManager.getAppManager().finishActivity(this);

			} else {
				showToast(mContext, "打印中...请稍后重试!");
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

	private void printContent() {
		isFinishPrint = false;
//		isPower = mPrintPowerOnAndOffUtils.checkPowerStatus();
//		if (!isPower) {
//			mPrintPowerOnAndOffUtils.powerOn();
//		}
//		SystemClock.sleep(300);
//		new Thread() {
//			@Override
//			public void run() {
//				int ret = mPrintUtils.detectionPrinter();
//				if (ret == 0) {
//
//					mPrintUtils.startPrint();
//					mPrintUtils.printerSetLineAlign(1);
//					mPrintUtils.printText("POS 签购单");
//					mPrintUtils.printText("(退款单)");
//					mPrintUtils.printerSetLineAlign(0);
//					if (!TextUtils.isEmpty(order_money)) {
//						mPrintUtils.printText("退款金额：" + order_money + "元");
//					}
//					mPrintUtils.printText("交易状态：已退款");
//					mPrintUtils.printText("退款方式：银联二维码退款");
//					mPrintUtils.printText("交易时间：" + order_date);
//					mPrintUtils.printText("交易类型："+order_type);
//					mPrintUtils.printText("操 作 员："+loginId);
//					mPrintUtils.printText("订 单 号：");
//					mPrintUtils.printText("  " + order_code);
//					mPrintUtils.printText("凭 证 号：");
//					mPrintUtils.printText("  " + order_voucher_no);
////					mPrintUtils.printText("-------------------------------");
//					if (isAgainPrint) {
//						mPrintUtils.printText("备注：重打印凭证");
//					}
//					mPrintUtils.stopPrint();
//					SystemClock.sleep(150);
//				}
//				isFinishPrint = true;
//			}
//		}.start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
//		mPrintPowerOnAndOffUtils.powerOff();
	}
}
