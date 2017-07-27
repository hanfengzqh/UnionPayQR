package com.zng.unionpayqr.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.zng.unionpayqr.R;

public class CustomDialog extends Dialog {

	private Button btn_cancel, btn_confirm;
	private View mMenuView;

	public CustomDialog(Context context, String title, String content,
			android.view.View.OnClickListener itemsOnClick) {
		super(context, R.style.alert_dialog);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.dialog_custom, null);
		btn_cancel = (Button) mMenuView.findViewById(R.id.btn_cancel);
		btn_confirm = (Button) mMenuView.findViewById(R.id.btn_confirm);

		// 取消按钮
		btn_cancel
				.setOnClickListener((android.view.View.OnClickListener) itemsOnClick);

		// 确定按钮
		btn_confirm
				.setOnClickListener((android.view.View.OnClickListener) itemsOnClick);
		// 不可以用“返回键”取消
		this.setCancelable(false);
		// 设置布局
		this.setContentView(mMenuView, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));

	}
}
