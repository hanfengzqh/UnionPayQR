package com.zng.unionpayqr.pro;

import android.content.Intent;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.zng.unionpayqr.R;
import com.zng.unionpayqr.base.BaseActivity;
import com.zng.unionpayqr.model.UnionPayResult;
import com.zng.unionpayqr.presenter.UnionPayPresenter;
import com.zng.unionpayqr.sdk.SDKConfig;
import com.zng.unionpayqr.utils.AmountUtils;
import com.zng.unionpayqr.utils.AppManager;
import com.zng.unionpayqr.utils.FileUtils;
import com.zng.unionpayqr.utils.Logger;
import com.zng.unionpayqr.utils.SPUtils;
import com.zng.unionpayqr.utils.UnionPayQRConstant;
import com.zng.unionpayqr.view.ScanPayView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.File;
import java.io.IOException;

@ContentView(R.layout.activity_setting)
public class SettingActivity extends BaseActivity implements ScanPayView {

	@ViewInject(R.id.btn_import_secretkey)
	private Button btn_import_secretkey;// 密钥证书
	private UnionPayPresenter mPayRequestPresenter;

	@Override
	public void initContentView() {
		AppManager.getAppManager().addActivity(this);
		mPayRequestPresenter = new UnionPayPresenter();
		mPayRequestPresenter.attachView(this);
		addBackBtn();
		setActionBarTitle(getResources().getString(R.string.params_setting));
		rl_title_right.setVisibility(View.GONE);
	}

	@Override
	protected void setListener() {
		title_back_bt.setOnClickListener(this);
		btn_import_secretkey.setOnClickListener(this);
	}

	@Override
	public void onClickEvent(View v) {
		switch (v.getId()) {
		case R.id.title_back_bt:
			finish();
			break;
		case R.id.btn_import_secretkey:// 导入商户信息
			importParameterInformation();
			break;
		default:
			break;
		}

	}

	/**
	 * 导入参数文件信息
	 */
	public void importParameterInformation() {
		showProgressDialog("请稍后，正在信息导入...");
		String usbPath = (String) SPUtils.getString(mContext, "USBpath");
		if (!TextUtils.isEmpty(usbPath)) {
			String merchantPath = usbPath + File.separator + "unionpay.txt";
			String acp_prodEncPath = usbPath + File.separator
					+ "acp_prod_enc.cer";
			String acp_prodMiddlePath = usbPath + File.separator
					+ "acp_prod_middle.cer";
			String acp_prodRootPath = usbPath + File.separator
					+ "acp_prod_root.cer";
			String acp_prodSigndfxPath = usbPath + File.separator
					+ "acp_prod_sign.pfx";

			File merchantFile = FileUtils.getFileByPath(merchantPath);
			File acp_prodEncFile = FileUtils.getFileByPath(acp_prodEncPath);
			File acp_prodMiddleFile = FileUtils
					.getFileByPath(acp_prodMiddlePath);
			File acp_prodRootFile = FileUtils.getFileByPath(acp_prodRootPath);
			
			File acp_prodSigndfxFile = FileUtils
					.getFileByPath(acp_prodSigndfxPath);
			
			// 导入商户私有证书
			if (acp_prodSigndfxFile.exists()) {
				FileUtils.copyFile(acp_prodSigndfxPath, SDKConfig.getConfig().getPackSign());
			} else {
//				showToast(mContext, "商户私钥证书导入错误");
			}
			
			// 导入商户号以及商户密码
			importMerchant(merchantFile);
			// 导入敏感信息密钥
			if (acp_prodEncFile.exists()) {
				FileUtils.copyFile(acp_prodEncPath, SDKConfig.getConfig().getPackEnc());
			}else{
//				acp_prodEncPath = "file:///android_asset/acp_prod_enc.cer";
				try {
					FileUtils.copyFile(getAssets().open("acp_prod_enc.cer"), SDKConfig.getConfig().getPackEnc());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// 导入签名公钥证书
			if (acp_prodMiddleFile.exists()) {
				FileUtils.copyFile(acp_prodMiddlePath, SDKConfig.getConfig().getPackMiddle());
			}else{
//				acp_prodMiddlePath = "file:///android_asset/acp_prod_middle.cer";
				try {
					FileUtils.copyFile(getAssets().open("acp_prod_middle.cer"), SDKConfig.getConfig().getPackMiddle());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			// 导入根证书
			if (acp_prodRootFile.exists()) {
				FileUtils.copyFile(acp_prodRootPath, SDKConfig.getConfig().getPackRoot());
			}else{
				
//				acp_prodRootPath = "file:///android_asset/acp_prod_root.cer";
				try {
					FileUtils.copyFile(getAssets().open("acp_prod_root.cer"), SDKConfig.getConfig().getPackRoot());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// 信息验证
			dismissProgressDialog();
			verfyInfor();

		} else {
			showToast(mContext, "请检查挂载设备，无法获取挂载路径");
			dismissProgressDialog();
		}
	}
	
	/**信息验证*/
	private void verfyInfor(){
		if (UnionPayQRConstant.isConfig(mContext)) {
			showProgressDialog("请稍后，正在验证...");
			new Thread(new Runnable() {
				public void run() {
					SDKConfig.getConfig().loadPropertiesFromSrc(SettingActivity.this);
					SystemClock.sleep(5000);
					mPayRequestPresenter.initPayData(mContext,UnionPayQRConstant.SCAN_PAY_REQUEST,AmountUtils.changeY2F("0.01"));
				}
			}).start();
		}else{
			showToast(mContext, "请配置完成全部参数......");
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mPayRequestPresenter.detachView();
		SPUtils.putString(mContext, "USBpath","");
	}

	@Override
	public void getScanPayResult(UnionPayResult resultData) {
		if (!TextUtils.isEmpty(resultData.getQrCode())) {
			showToast(mContext, "参数验证成功");
			startActivity(new Intent(SettingActivity.this, MainActivity.class));
			AppManager.getAppManager().finishActivity(this);
		} else {
			showToast(mContext, resultData.getRespMsg());
		}
		dismissProgressDialog();
	}

	@Override
	public void getQueryPayResult(UnionPayResult resultData) {
	}

	@Override
	public void showNetError(OnClickListener onClickListener) {
		dismissProgressDialog();
		showToast(mContext, "参数验证失败，请检查网络或参数...");
	}
	
	@Override
	public void showError(String msg, OnClickListener onClickListener) {
		super.showError(msg, onClickListener);
		dismissProgressDialog();
		showToast(mContext, msg);
	}
	
	

	/**
	 * 导入商户号及商户密码
	 * 
	 * @param merchantFile
	 */

	private void importMerchant(File merchantFile) {
		if (merchantFile.exists()) {
			final String merchant_key = FileUtils.readFile(merchantFile,
					"utf-8").toString();
			Logger.d("liujie", "merchant_key = " + merchant_key);
			if (!TextUtils.isEmpty(merchant_key)) {
				String[] splitParams = merchant_key.split(";");
				if (splitParams != null && splitParams.length > 1) {
					String merchantNum = splitParams[0];
					String signCertPwd = splitParams[1];
					// 商户号
					if (!TextUtils.isEmpty(merchantNum)) {
						SPUtils.putString(mContext,
								UnionPayQRConstant.PUBLIC_KEY, merchantNum.trim());
					} else {
						showToast(mContext, "商户号有误");
					}
					// 密码
					if (!TextUtils.isEmpty(signCertPwd)) {
						SPUtils.putString(mContext,
								UnionPayQRConstant.PRIVATE_KEY, signCertPwd.trim());
					} else {
						showToast(mContext, "敏感信息密钥有误");
					}
				} else {
					showToast(mContext, "商户号或敏感信息密钥未按规定写入文件");
				}
			} else {
				showToast(mContext, "商户号或敏感信息密钥信息有误");
			}
		} else {
			showToast(mContext, "无法获取挂载设备中指定文件");
		}
	}
}
