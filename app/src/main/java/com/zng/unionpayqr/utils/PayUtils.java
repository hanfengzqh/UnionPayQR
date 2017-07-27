package com.zng.unionpayqr.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;

import com.zng.unionpayqr.model.PayResultInfo;
import com.zng.unionpayqr.model.UnionPayResult;
import com.zng.unionpayqr.pro.cashier.activity.PaymentSuccessActivity;

import java.text.SimpleDateFormat;
import java.util.Random;

public class PayUtils {

	private static String returnInfo;
	public static final String ALLCHAR = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

	/**
	 * 获取当前登陆操作员号
	 */
	public static String getLoginOperId(Context mContext) {
		return Settings.System.getString(mContext.getContentResolver(), "persist.sys.zng.operatorid");
	}

	/**
	 * 获取当前签到操作员号
	 */
	public static String getSignInOperId(Context mContext) {
		return Settings.System.getString(mContext.getContentResolver(), Contacts.SIGN_ID);
	}
	
	/**
	 * 返回一个定长的随机字符串(只包含大小写字母、数字)
	 * 
	 * @param length
	 *            随机字符串长度
	 * @return 随机字符串
	 */
	public static String generateString(int length) {
		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			sb.append(ALLCHAR.charAt(random.nextInt(ALLCHAR.length())));
		}
		return sb.toString();
	}
	
	// 获取系统当前年月日时分秒时间
	public static String getSystemTime() {
		SimpleDateFormat systemDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
		String systemDate = systemDateFormat.format(new java.util.Date());
		return systemDate;
	}

	// 生成商户交易订单号
	public static String getOutTradeNo() {
		String outTradeNo = getSystemTime() + generateString(8);
		return outTradeNo;
	}

	public static String getVersionName(Context mContext) {
		// 获取packagemanager的实例
		PackageManager packageManager = mContext.getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo;
		String version = null;
		try {
			packInfo = packageManager.getPackageInfo(mContext.getPackageName(), 0);
			version = packInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return version;
	}

	/**
	 * 发送消息
	 * 
	 * @param what
	 */
	public static void sendMsg(int what, Handler mHandler) {
		if (mHandler == null)
			return;
		Message msg = mHandler.obtainMessage();
		msg.what = what;
		msg.obj = returnInfo;
		mHandler.sendMessage(msg);
	}

	public static void checkPayResult(String respCode, Handler mHandler) {
		Logger.d("zqh", "查询编码 = "+respCode);
		
		//其他应答码为失败请排查原因或做失败处理
		if (Utils.isInteger(respCode)) {
			if (respCode.equals("00")) {
				returnInfo = "支付成功";
				sendMsg(Constant.ORDER_SUCCESS, mHandler);
			}else{
				returnInfo = UnionPayResult.getRespCode(respCode);
				sendMsg(Constant.ORDER_FAIL, mHandler);
			}
		}else{
			if (respCode.equals("00A6")) {
				returnInfo = "有缺陷的成功";
				sendMsg(Constant.ORDER_ABNORMAL, mHandler);
			} else if (respCode.equals("PR05")) {
				returnInfo = "审核中";
				sendMsg(Constant.ORDER_ABNORMAL, mHandler);
			} else if (respCode.equals("PR99")) {
				returnInfo = "审核失败";
				sendMsg(Constant.ORDER_ABNORMAL, mHandler);
			}
		}
	}

	/**
	 * 跳转成功界面
	 * @param mActivity 上下文
	 * @param clazz 成功界面
	 * @param order_money 金额
	 * @param order_code 商户生成的交易订单号
	 * @param traceTime 交易完成时间
	 * @param order_queryId 银联生成的查询流水号
	 * @param order_success_force 是否是强制完成 false-非强制完成;true--强制完成
	 * @param pay_way 条码支付/扫码支付
	 */
	public static void openSuccessActivity(Activity mActivity, Class<?> clazz, String order_money, String order_code,
			String traceTime,String order_queryId, boolean order_success_force,String pay_way) {
		String order_pay_date = "";
		
		if (order_success_force) {
			order_pay_date = TimeUtils.getCurTimeString(TimeUtils.DEFAULT_SDF);
			traceTime = DemoBase.getCurrentTime();
			
		}else{
			traceTime = TimeUtils.getCurTimeString().substring(0, 4)+traceTime;
			order_pay_date = Utils.converterTime(traceTime);
		}
		
		PayResultInfo mPayResultInfo = new PayResultInfo("Success", order_money, order_code,
				order_pay_date,traceTime,order_queryId, order_success_force,pay_way);
		Bundle bundle = new Bundle();
		bundle.putParcelable("PayResultInfo", mPayResultInfo);
		openActivity(mActivity, PaymentSuccessActivity.class, bundle);
		mActivity.finish();
	}

	/**
	 * 
	 * @Title:openActivity
	 * 
	 * @Description页面跳转的动画效果
	 */
	public static void openActivity(Activity context, Class<?> pClass, Bundle bundle) {
		Intent intent = new Intent(context, pClass);
		if (bundle != null)
			intent.putExtras(bundle);
		context.startActivity(intent);
	}

	/***
	 * 获取订单支付方式
	 * 
	 * @param code
	 * @return
	 */
	public static String getOrderState(String code) {
		String state = null;
		if (code.equals(UnionPayQRConstant.QRCODE)) {
			state = "银联条码";
		} else if (code.equals(UnionPayQRConstant.QRSCAN)) {
			state = "银联扫码";
		}else if (code.equals(UnionPayQRConstant.H5_CHANNEL)) {
			state = "微信/支付宝";
		}else if (code.equals(UnionPayQRConstant.WECHAT_CHANNEL)) {
			state = "微信";
		}else if(code.equals(UnionPayQRConstant.QRALL)){
			state = "全部";
		}
		return state;

	}
}
