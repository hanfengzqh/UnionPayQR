package com.zng.unionpayqr.pro;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.zng.unionpayqr.R;
import com.zng.unionpayqr.base.BaseActivity;
import com.zng.unionpayqr.utils.AppManager;
import com.zng.unionpayqr.utils.Logger;

import org.xutils.view.annotation.ContentView;

@ContentView(R.layout.activity_splash)
public class SplashActivity extends BaseActivity {

	private static final int DETECTION_PUBLISHER_ID_RES = 141;// 发行商id

	@SuppressLint("HandlerLeak")
	private Handler commuHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {

			case DETECTION_PUBLISHER_ID_RES:
				String vidCode = (String) msg.obj;// 发行商id
				Logger.i("zqh","版本号"+vidCode);
//				if (TextUtils.isEmpty(vidCode)) {
//					showToast(mContext, "未检测到支付组件");
//					finish();
//				}else{
					openfinish(MainActivity.class);
//					String code=vidCode.substring(0, 3);
//					Logger.i("zqh","智能果标识"+code);
//					if(code.equals("ZNG")){
//						openfinish(MainActivity.class);
//						
//					}else{
//						showToast(mContext, "请在pos端进行支付交易...");
//					}
//				}
				break;
			}
		}
	};

//	private POSFunctionUtils mPOSFunctionUtils;

	@Override
	public void initContentView() {
		AppManager.getAppManager().addActivity(this);
//		mPOSFunctionUtils = new POSFunctionUtils(this);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				
//				String softwareVersion = mPOSFunctionUtils.getPosSn();
				Message msg = new Message();
				msg.what = DETECTION_PUBLISHER_ID_RES;
//				msg.obj = softwareVersion;
				commuHandler.sendMessage(msg);

			}
		}, 1000);

	}

	@Override
	protected void setListener() {

	}

	@Override
	protected void onClickEvent(View paramView) {

	}

}
