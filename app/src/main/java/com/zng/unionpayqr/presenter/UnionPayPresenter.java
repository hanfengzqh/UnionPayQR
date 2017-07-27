package com.zng.unionpayqr.presenter;

import java.util.Map;

import android.content.Context;
import android.text.TextUtils;

import com.zng.unionpayqr.http.OnHttpResultListener;
import com.zng.unionpayqr.http.impl.SystemHttpCommand;
import com.zng.unionpayqr.http.utils.HttpTask;
import com.zng.unionpayqr.model.UnionPayResult;
import com.zng.unionpayqr.sdk.AcpService;
import com.zng.unionpayqr.sdk.SDKConfig;
import com.zng.unionpayqr.utils.DemoBase;
import com.zng.unionpayqr.utils.FastJsonUtils;
import com.zng.unionpayqr.utils.PayParams;
import com.zng.unionpayqr.utils.PayUtils;
import com.zng.unionpayqr.utils.UnionPayQRConstant;
import com.zng.unionpayqr.utils.Utils;
import com.zng.unionpayqr.view.ScanPayView;

public class UnionPayPresenter extends BasePresenter<ScanPayView> {

	@Override
	public void attachView(ScanPayView mvpView) {
		super.attachView(mvpView);
	}

	@Override
	public void detachView() {
		super.detachView();
	}
	
	/**
	 * 初始化商户相关参数--验证商户参数的准确性
	 * @param mContext
	 * @param refund_fee 金额
	 */
	public void initPayData(Context mContext,String reqType,String refund_fee) {
		String reqNo = PayUtils.getOutTradeNo();
		Map<String, String> reqData = PayParams.scanPayRequest(mContext, reqNo, refund_fee);
		httpConnect(mContext, reqType ,reqData);
	}

	/**
	 *商户敏感密钥更新 
	 * @param mContext
	 */
	public void updatePublicKey(Context mContext,String reqType){
		String reqNo = PayUtils.getOutTradeNo();
		Map<String, String> requestParam = PayParams.publicKeyUpdate(mContext, reqNo);
		httpConnect(mContext, reqType ,requestParam);
		
	}
	
	/**
	 * 条码支付查询
	 * @param mContext
	 * @param ori_seq
	 */
	public void queryPayStatus(Context mContext,String reqType,String ori_seq){
		Map<String, String> requestParam = PayParams.queryPayRequest(mContext,ori_seq);
		httpConnect(mContext, reqType,requestParam);
	}
	
	
	/**
	 * 退货
	 * @param mContext
	 * @param reqType 交易类型
	 * @param refund_fee 退款金额
	 * @param queryId 银联交易订单号
	 */
	public void refundData(Context mContext,String reqType,String refund_fee,String queryId){
		String ori_seq = PayUtils.getOutTradeNo();
		Map<String, String> requestParam = PayParams.refundRequest(mContext, ori_seq, refund_fee, queryId);
		httpConnect(mContext, reqType, requestParam);
		
	}
	
	/**
	 * 消费撤销
	 * @param mContext
	 * @param reqType 交易类型
	 * @param refund_fee 退款金额
	 * @param queryId 银联交易订单号
	 */
	public void revokeData(Context mContext,String reqType,String refund_fee,String queryId){
		String ori_seq = PayUtils.getOutTradeNo();
		Map<String, String> requestParam = PayParams.revokeRequest(mContext, ori_seq,refund_fee,queryId);
		httpConnect(mContext, reqType, requestParam);
	}
	
	
	/**
	 * QR被扫
	 * @param mContext
	 * @param reqType 交易类型
	 * @param total_fee 金额
	 * @param C2BCode 银联条码
	 * 
	 */
	public void scanPayData(Context mContext,String reqType,String total_fee,String C2BCode){
		String reqNo = PayUtils.getOutTradeNo();
		Map<String, String> requestParam = PayParams.codePayRequest(mContext, reqNo, total_fee, C2BCode);
		httpConnect(mContext, reqType,requestParam);
	}
	
	
	/***
	 * 请求参数
	 * @param reqType 请求类型
	 * @param requestParam 请求参数
	 */
	@SuppressWarnings("unchecked")
	public void httpConnect(Context mContext, final String reqType,Map<String, String> requestParam) {
		
		String requestAppUrl = SDKConfig.getConfig().getBackRequestUrl();
		if (reqType.equals(UnionPayQRConstant.QUERY_PAY_REQUEST)) {
			requestAppUrl = SDKConfig.getConfig().getSingleQueryUrl();
		}
		
		// 请求地址,请求参数,网络请求实现类,请求结果处理
		HttpTask httpTask = new HttpTask(requestAppUrl, requestParam, new SystemHttpCommand(),
				new OnHttpResultListener() {
			
					@Override
					public void onResult(Map<String, String> rspData) {
//						Logger.d("zqh", "rspData = "+rspData);
						if(!rspData.isEmpty()){
							if(AcpService.validate(rspData, DemoBase.encoding)){
								try {
									UnionPayResult payResult = FastJsonUtils.json2pojo(FastJsonUtils.obj2JsonString(rspData), UnionPayResult.class);
									if (payResult != null) {
										String respCode = payResult.getRespCode();
										if (!TextUtils.isEmpty(respCode)) {
//											if(("00").equals(respCode)){
												//成功,获取银联二维码
												getChannelResult(reqType,payResult);
//											}else{
//												//其他应答码为失败请排查原因或做失败处理
//												if (Utils.isInteger(respCode)) {
//													UnionPayResult.getRespCode(Integer.parseInt(respCode));
//												}else{
//													UnionPayResult.getRespCode(100);
//												}
//												
//											}
											
										}else{//respCode == null
											if (isViewAttached()) {
												getMvpView().showError("响应码为空", null);
											}
										}
									}else{//payResult == null
										if (isViewAttached()) {
											getMvpView().showError("数据解析失败", null);
										}
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}else{
//								LogUtil.writeErrorLog("验证签名失败");
								//TODO 检查验证签名失败的原因
								if (isViewAttached()) {
									getMvpView().showError("验证签名失败", null);
								}
							}
						}else{
							//未返回正确的http状态
//							LogUtil.writeErrorLog("未获取到返回报文或返回http状态码非200");
							if (isViewAttached()) {
								getMvpView().showNetError(null);
							}
						}
					}
				});
		httpTask.execute();
	}
	
	/***
	 * @param mUnionPayPuResult 
	 * @param reqType 请求类型
	 */
	public void getChannelResult(String reqType,UnionPayResult mUnionPayPuResult) {
		if (reqType.equals(UnionPayQRConstant.SCAN_PAY_REQUEST)) {
			getMvpView().getScanPayResult(mUnionPayPuResult);
		} else if (reqType.equals(UnionPayQRConstant.QUERY_PAY_REQUEST)) {
			getMvpView().getQueryPayResult(mUnionPayPuResult);
		} 
	}
}
