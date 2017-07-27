package com.zng.unionpayqr.pro.cashier.fragment;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.zng.unionpayqr.R;
import com.zng.unionpayqr.base.BaseFragment;
import com.zng.unionpayqr.pro.cashier.activity.PayMainActivity;
import com.zng.unionpayqr.utils.AmountUtils;
import com.zng.unionpayqr.utils.AppManager;
import com.zng.unionpayqr.utils.NetworkUtil;
import com.zng.unionpayqr.utils.UnionPayQRConstant;
import com.zng.unionpayqr.widget.KeyboardUtil;
import com.zng.unionpayqr.widget.KeyboardUtil.OnOkClick;
import com.zng.unionpayqr.widget.KeyboardUtil.onCancelClick;

@ContentView(R.layout.activity_selection_amount)
public class SelectionAmountFragment extends BaseFragment {

	@ViewInject(R.id.tv_sign)
	private TextView tv_sign;

	@ViewInject(R.id.tv_pay_amount)
	private EditText tv_pay_amount;
	@ViewInject(R.id.keyboard_view)
	private KeyboardView keyboard_view;

	@Override
	public void initContentView() {
		KeyboardUtil keyboardUtil = new KeyboardUtil(mActivity, mContext);
		keyboardUtil.attachTo(tv_pay_amount);
		keyboardUtil.setOnOkClick(new OnOkClick() {

			@Override
			public void onOkClick() {
				String pay_amount = tv_pay_amount.getText().toString();
				if (NetworkUtil.checkNetwork(mContext)) {
					verificationAmount(pay_amount);
				}else{
					showToast(mContext, "网络连接失败，请先配置网络");
				}
			}
		});
		keyboardUtil.setOnCancelClick(new onCancelClick() {

			@Override
			public void onCancellClick() {
				keyboard_view.setVisibility(View.GONE);

			}
		});
		AmountUtils.setPricePoint(tv_pay_amount);
	}

	public void verificationAmount(String amount) {
		if (TextUtils.isEmpty(amount)) {
			showToast(mContext, "付款金额不能为空");
		} else {
			double money = Double.parseDouble(amount);
			if (money > 0) {
				if (UnionPayQRConstant.isConfig(mContext)) {
					Bundle bundle = new Bundle();
					bundle.putString("pay_amount", amount);
					openActivity(mActivity, PayMainActivity.class, bundle);
					
				} else {
					showToast(mContext, "请先配置支付参数...");
				}

			} else {
				showToast(mContext, "付款金额必须大于0元");
			}
		}
	}

	@Override
	protected void setListener() {
		tv_pay_amount.setOnClickListener(this);
		tv_sign.setOnClickListener(this);
	}

	@Override
	protected void onClickEvent(View paramView) {

		switch (paramView.getId()) {
		case R.id.tv_pay_amount:
			keyboard_view.setVisibility(View.VISIBLE);
			break;

		case R.id.tv_sign:
			if (UnionPayQRConstant.isConfig(mContext)) {
				AppManager.getAppManager().finishActivity(mActivity);
			} else {
				showToast(mContext, "请先配置支付参数...");
			}
			break;
		default:
			break;
		}
	}

}
