package com.zng.unionpayqr.pro.cashier_manage.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zng.unionpayqr.R;
import com.zng.unionpayqr.base.BaseActivity;
import com.zng.unionpayqr.base.UnionPay;
import com.zng.unionpayqr.model.OrderRecordTable;
import com.zng.unionpayqr.model.UnionPayResult;
import com.zng.unionpayqr.presenter.UnionPayPresenter;
import com.zng.unionpayqr.utils.AmountUtils;
import com.zng.unionpayqr.utils.AppManager;
import com.zng.unionpayqr.utils.Constant;
import com.zng.unionpayqr.utils.DemoBase;
import com.zng.unionpayqr.utils.Logger;
import com.zng.unionpayqr.utils.PayUtils;
import com.zng.unionpayqr.utils.TimeUtils;
import com.zng.unionpayqr.utils.UnionPayQRConstant;
import com.zng.unionpayqr.view.ScanPayView;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.activity_order_refund)
public class OrderRefundActivity extends BaseActivity implements ScanPayView{

	@ViewInject(R.id.lyt_back)
	private LinearLayout lyt_back;

	@ViewInject(R.id.tv_title)
	private TextView tv_title;

	@ViewInject(R.id.tv_request_result)
	private TextView tv_request_result;
	
	
	private String voucher_number;//银联订单号
	private String voucher_numbernew;//银联新订单号
	private String ori_seq;//订单号
	private String ori_seqnew;//新订单号
	private String refund_fee;//退款金额
	private OrderRecordTable mOrderRecordTable;
	private UnionPayPresenter mRefundPresenter;
	private String tradeDate;//交易时间
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			String codeInfo=(String) msg.obj;
			Logger.e("zqh", "codeInfo = "+codeInfo);
			Logger.e("zqh", "msg.what = "+msg.what);
			
			switch (msg.what) {
			case Constant.ORDER_SUCCESS:
				tv_request_result.setText("退款申请成功");
				setSaveDBInfo();
				Bundle bundle=new Bundle();
				bundle.putParcelable("OrderRecordTable", mOrderRecordTable);
				openActivity(OrderRefundSuccessActivity.class, bundle);
				OrderRefundActivity.this.finish();
				break;
			case Constant.ORDER_FAIL:
				tv_request_result.setText("退款申请失败");
				mHandler.sendEmptyMessageDelayed(7, 1500);
				break;
			case Constant.ORDER_ABNORMAL:
				tv_request_result.setText("该订单无法申请退款...");
				finish();
				break;
			case Constant.ORDER_TIMEOUT:
				tv_request_result.setText("网络异常,请检查网络");
				finish();
				break;
			case 7:
				tv_request_result.setText(""+codeInfo);
				finish();
			default:
				break;
			}

		}
	};
	
	
	@Override
	public void initContentView() {
		AppManager.getAppManager().addActivity(this);
		mRefundPresenter = new UnionPayPresenter();
		mRefundPresenter.attachView(this);
		setActionBarTitle("退款");
		rl_title_right.setVisibility(View.GONE);
		mOrderRecordTable = this.getIntent().getParcelableExtra("OrderRecordTable");
		if (mOrderRecordTable != null) {
			voucher_number=mOrderRecordTable.getAll_VoucherNumber();
			ori_seq=mOrderRecordTable.getAll_OrderNumber();
			refund_fee=mOrderRecordTable.getAll_TradeMoney();
			tradeDate = mOrderRecordTable.getAll_TradeDateTime2();
		}
		initData();
	}

	@Override
	public void initData() {
		tv_request_result.setText("退款中,请稍后...");
		String currentTime = DemoBase.getCurrentTime();
		if (!TextUtils.isEmpty(tradeDate) && tradeDate.length()>=8) {
			if (tradeDate.subSequence(0, 8).equals(currentTime.subSequence(0, 8))) {
				mRefundPresenter.revokeData(mContext, UnionPayQRConstant.SCAN_PAY_REQUEST, AmountUtils.changeY2F(refund_fee),voucher_number);
			}else{
				mRefundPresenter.refundData(mContext, UnionPayQRConstant.SCAN_PAY_REQUEST, AmountUtils.changeY2F(refund_fee),voucher_number);
			}
		}else{
			mRefundPresenter.refundData(mContext, UnionPayQRConstant.SCAN_PAY_REQUEST, AmountUtils.changeY2F(refund_fee),voucher_number);
		}
	}
	
	

	@Override
	protected void setListener() {
		title_back_bt.setOnClickListener(this);
	}

	@Override
	protected void onClickEvent(View paramView) {

		switch (paramView.getId()) {
		case R.id.title_back_bt:
			finish();
			break;
		default:
			break;
		}

	}
	
	
	/***
	 * 保存交易数据到数据库
	 */
	public void setSaveDBInfo(){
		String order_pay_date=TimeUtils.getCurTimeString(TimeUtils.DEFAULT_SDF);
		DbManager db = x.getDb(((UnionPay)getApplicationContext()).getDaoConfig());
		mOrderRecordTable.setIsRefund(true);
		mOrderRecordTable.setAll_NewDateTime(order_pay_date);
		mOrderRecordTable.setAll_NewUser(loginId);
		mOrderRecordTable.setAll_NewMoney(refund_fee);
		mOrderRecordTable.setAll_NewOrderNumber(ori_seqnew);
		mOrderRecordTable.setAll_NewVoucherNumber(voucher_numbernew);
		try {
			db.saveOrUpdate(mOrderRecordTable);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void showEmpty(String msg, OnClickListener onClickListener) {
	}
	
	@Override
	public void showNetError(OnClickListener onClickListener) {
		super.showNetError(onClickListener);
		mHandler.sendEmptyMessage(Constant.ORDER_TIMEOUT);	
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mRefundPresenter.detachView();
	}

	@Override
	public void getScanPayResult(UnionPayResult resultData) {
		if (resultData != null) {
			String respCd = resultData.getRespCode();
			if (respCd.equals("00")) {
				voucher_numbernew = resultData.getQueryId();
				ori_seqnew = resultData.getOrderId();
			}else{
				dismissProgressDialog();
				showToast(mContext, UnionPayResult.getRespCode(respCd));
				finish();
			}
			PayUtils.checkPayResult(respCd,mHandler);
		}
	}

	@Override
	public void getQueryPayResult(UnionPayResult resultData) {
	}
}
