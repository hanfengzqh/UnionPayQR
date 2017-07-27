package com.zng.unionpayqr.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.zng.unionpayqr.R;

public class MyDialog extends Dialog implements
		android.view.View.OnClickListener {
	private DialogClickListener listener;
	Context context;
	private TextView tv_restinfo_pop_tel_content;
	private TextView dialog_textViewID;
	private TextView dialog_textViewID1;
	private String leftBtnText;
	private String rightBtnText;
	private String content;

	public MyDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	/**
	 * �Զ���dialog
	 * 
	 * @param context
	 * @param theme
	 *            ����
	 * @param content
	 *            ��������
	 * @param leftBtnText
	 *            ��ť���֣���Ϊ""������
	 * @param rightBtnText
	 *            �Ұ�ť���֣���Ϊ""������
	 * @param listener
	 *            �ص��ӿ�
	 */
	public MyDialog(Context context, int theme, String content,
			String leftBtnText, String rightBtnText,
			DialogClickListener listener) {
		super(context, theme);
		this.context = context;
		this.content = content;
		this.leftBtnText = leftBtnText;
		this.rightBtnText = rightBtnText;
		this.listener = listener;
	}

	public void setTextSize(int size) {
		dialog_textViewID.setTextSize(size);
		dialog_textViewID1.setTextSize(size);
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dialog);
		tv_restinfo_pop_tel_content = (TextView) findViewById(R.id.tv_restinfo_pop_tel_content);
		dialog_textViewID1 = (TextView) findViewById(R.id.dialog_textViewID1);
		dialog_textViewID = (TextView) findViewById(R.id.dialog_textViewID);
		dialog_textViewID.setOnClickListener(this);
		dialog_textViewID1.setOnClickListener(this);
		initView();
		initDialog(context);
	}

	/**
	 * ����dialog�Ŀ�Ϊ��Ļ��3��֮1
	 * 
	 * @param context
	 */
	private void initDialog(Context context) {
		setCanceledOnTouchOutside(false);
		setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK
						&& event.getRepeatCount() == 0) {
					return true;
				} else {
					return false;
				}
			}
		});
		WindowManager windowManager = this.getWindow().getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = this.getWindow().getAttributes();
		lp.width = (int) (display.getWidth() / 6 * 5); //// ���ÿ��
		this.getWindow().setAttributes(lp);
	}

	private void initView() {
		//tv_restinfo_pop_tel_content.setTextIsSelectable(true);//主要就是这句话起到复制粘贴的作用
		tv_restinfo_pop_tel_content.setText(content);
		if (leftBtnText.equals(""))
			dialog_textViewID.setVisibility(View.GONE);
		else
			dialog_textViewID.setText(leftBtnText);
		if (rightBtnText.equals(""))
			dialog_textViewID1.setVisibility(View.GONE);
		else
			dialog_textViewID1.setText(rightBtnText);
	}

	public interface DialogClickListener {
		void onLeftBtnClick(Dialog dialog,String textContent);

		void onRightBtnClick(Dialog dialog);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialog_textViewID:
			String textContent=tv_restinfo_pop_tel_content.getText().toString();
			listener.onLeftBtnClick(this,textContent);
			break;
		case R.id.dialog_textViewID1:
			
			listener.onRightBtnClick(this);
			break;
		default:
			break;
		}
	}
}