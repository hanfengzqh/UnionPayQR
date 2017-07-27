package com.zng.unionpayqr.model;

import com.zng.unionpayqr.utils.Constant;
import com.zng.unionpayqr.utils.Utils;

public class UnionPayResult {

	private String version;// 版本号
	private String encoding;// 编码方式
	private String signature;// 签名
	private String signMethod;// 签名方法

	private String txnType;// 交易类型
	private String txnSubType;// 交易子类
	private String bizType;// 产品类型
	private String accessType;// 接入类型
	private String merId;// 商户代码
	private String orderId;// 商户订单号
	private String txnTime;// 订单发送时间
	private String txnAmt;// 交易金额
	private String currencyCode;// 交易币种

	private String queryId;// 交易查询流水号
	private String respCode;// 响应码
	private String respMsg;// 应答信息
	private String signPubKeyCert;// 签名公钥证书
	private String qrCode;// 二维码
	private String origQryId;// 原始交易流水号
	private String origOrderId;// 原交易商户订单号
	private String origTxnTime;// 原交易商户发送交易时间
	private String certType;// 敏感信息加密公钥01
	private String encryptPubKeyCert;// 加密公钥证书
	private String origRespCode;// 原交易应答码--查询交易成功时返回
	private String traceTime;// 交易完成时间

	public String getTraceTime() {
		return traceTime;
	}

	public void setTraceTime(String traceTime) {
		this.traceTime = traceTime;
	}

	public String getOrigRespCode() {
		return origRespCode;
	}

	public void setOrigRespCode(String origRespCode) {
		this.origRespCode = origRespCode;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getSignMethod() {
		return signMethod;
	}

	public void setSignMethod(String signMethod) {
		this.signMethod = signMethod;
	}

	public String getTxnType() {
		return txnType;
	}

	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}

	public String getTxnSubType() {
		return txnSubType;
	}

	public void setTxnSubType(String txnSubType) {
		this.txnSubType = txnSubType;
	}

	public String getBizType() {
		return bizType;
	}

	public void setBizType(String bizType) {
		this.bizType = bizType;
	}

	public String getAccessType() {
		return accessType;
	}

	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}

	public String getMerId() {
		return merId;
	}

	public void setMerId(String merId) {
		this.merId = merId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getTxnTime() {
		return txnTime;
	}

	public void setTxnTime(String txnTime) {
		this.txnTime = txnTime;
	}

	public String getTxnAmt() {
		return txnAmt;
	}

	public void setTxnAmt(String txnAmt) {
		this.txnAmt = txnAmt;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getQueryId() {
		return queryId;
	}

	public void setQueryId(String queryId) {
		this.queryId = queryId;
	}

	public String getRespCode() {
		return respCode;
	}

	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}

	public String getRespMsg() {
		return respMsg;
	}

	public void setRespMsg(String respMsg) {
		this.respMsg = respMsg;
	}

	public String getSignPubKeyCert() {
		return signPubKeyCert;
	}

	public void setSignPubKeyCert(String signPubKeyCert) {
		this.signPubKeyCert = signPubKeyCert;
	}

	public String getQrCode() {
		return qrCode;
	}

	public void setQrCode(String qrCode) {
		this.qrCode = qrCode;
	}

	public String getOrigQryId() {
		return origQryId;
	}

	public void setOrigQryId(String origQryId) {
		this.origQryId = origQryId;
	}

	public String getOrigOrderId() {
		return origOrderId;
	}

	public void setOrigOrderId(String origOrderId) {
		this.origOrderId = origOrderId;
	}

	public String getOrigTxnTime() {
		return origTxnTime;
	}

	public void setOrigTxnTime(String origTxnTime) {
		this.origTxnTime = origTxnTime;
	}

	public UnionPayResult() {
		super();
	}

	public String getCertType() {
		return certType;
	}

	public void setCertType(String certType) {
		this.certType = certType;
	}

	public String getEncryptPubKeyCert() {
		return encryptPubKeyCert;
	}

	public void setEncryptPubKeyCert(String encryptPubKeyCert) {
		this.encryptPubKeyCert = encryptPubKeyCert;
	}

	public UnionPayResult(String version, String encoding, String signature,
			String signMethod, String txnType, String txnSubType,
			String bizType, String accessType, String merId, String orderId,
			String txnTime, String txnAmt, String currencyCode, String queryId,
			String respCode, String respMsg, String signPubKeyCert,
			String qrCode, String origQryId, String origTxnTime) {
		super();
		this.version = version;
		this.encoding = encoding;
		this.signature = signature;
		this.signMethod = signMethod;
		this.txnType = txnType;
		this.txnSubType = txnSubType;
		this.bizType = bizType;
		this.accessType = accessType;
		this.merId = merId;
		this.orderId = orderId;
		this.txnTime = txnTime;
		this.txnAmt = txnAmt;
		this.currencyCode = currencyCode;
		this.queryId = queryId;
		this.respCode = respCode;
		this.respMsg = respMsg;
		this.signPubKeyCert = signPubKeyCert;
		this.qrCode = qrCode;
		this.origQryId = origQryId;
		this.origTxnTime = origTxnTime;
	}

	/***
	 * 获取反应码--描述信息
	 * 
	 * @param respCode
	 * @return
	 */
	public static String getRespCode(String respCode) {
		String respCodeDesc = "交易失败";
		if (Utils.isInteger(respCode)) {

			switch (Integer.parseInt(respCode)) {
			case 1:
				respCodeDesc = "交易失败。详情请咨询95516";
				break;
			case 2:
				respCodeDesc = "系统未开放或暂时关闭，请稍后再试";
				break;
			case 3:
				respCodeDesc = "交易通讯超时，请发起查询交易";
				break;
			case 4:
				respCodeDesc = "交易状态未明，请查询对账结果";
				break;
			case 5:
				respCodeDesc = "交易已受理，请稍后查询交易结果";
				break;
			case 10:
				respCodeDesc = "报文格式错误";
				break;
			case 11:
				respCodeDesc = "验证签名失败";
				break;
			case 12:
				respCodeDesc = "重复交易";
				break;
			case 13:
				respCodeDesc = "报文交易要素缺失";
				break;
			case 14:
				respCodeDesc = "批量文件格式错误";
				break;
			case 30:
				respCodeDesc = "交易未通过，请尝试使用其他银联卡支付或联系95516";
				break;
			case 31:
				respCodeDesc = "商户状态不正确";
				break;
			case 32:
				respCodeDesc = "商户无此交易权限";
				break;
			case 33:
				respCodeDesc = "交易金额超限";
				break;
			case 34:
				respCodeDesc = "查无此交易";
				break;
			case 35:
				respCodeDesc = "原交易不存在或状态不正确";
				break;
			case 36:
				respCodeDesc = "与原交易信息不符";
				break;
			case 37:
				respCodeDesc = "已超过最大查询次数或操作过于频繁";
				break;
			case 38:
				respCodeDesc = "银联风险受限";
				break;
			case 39:
				respCodeDesc = "交易不在受理时间范围内";
				break;
			case 40:
				respCodeDesc = "绑定关系检查失败";
				break;
			case 41:
				respCodeDesc = "批量状态不正确，无法下载";
				break;
			case 42:
				respCodeDesc = "扣款成功但交易超过规定支付时间";
				break;
			case 45:
				respCodeDesc = "已被成功退货或已被成功撤销";
				break;
			case 60:
				respCodeDesc = "交易失败，详情请咨询您的发卡行";
				break;
			case 61:
				respCodeDesc = "输入的卡号无效，请确认后输入";
				break;
			case 62:
				respCodeDesc = "交易失败，发卡银行不支持该商户，请更换其他银行卡";
				break;
			case 63:
				respCodeDesc = "卡状态不正确";
				break;
			case 64:
				respCodeDesc = "卡上的余额不足";
				break;
			case 65:
				respCodeDesc = "输入的密码、有效期或CVN2有误，交易失败";
				break;
			case 66:
				respCodeDesc = "持卡人身份信息或手机号输入不正确，验证失败";
				break;
			case 67:
				respCodeDesc = "密码输入次数超限";
				break;
			case 68:
				respCodeDesc = "您的银行卡暂不支持该业务，请向您的银行或95516咨询";
				break;
			case 69:
				respCodeDesc = "您的输入超时，交易失败";
				break;
			case 70:
				respCodeDesc = "交易已跳转，等待持卡人输入";
				break;
			case 71:
				respCodeDesc = "动态口令或短信验证码校验失败";
				break;
			case 72:
				respCodeDesc = "您尚未在{}银行网点柜面或个人网银签约加办银联无卡支付业务，请去柜面或网银开通或拨打{}";
				break;
			case 73:
				respCodeDesc = "支付卡已超过有效期";
				break;
			case 74:
				respCodeDesc = "扣款成功，销账未知";
				break;
			case 75:
				respCodeDesc = "扣款成功，销账失败";
				break;
			case 76:
				respCodeDesc = "需要验密开通";
				break;
			case 77:
				respCodeDesc = "银行卡未开通认证支付";
				break;
			case 78:
				respCodeDesc = "发卡行交易权限受限，详情请咨询您的发卡行";
				break;
			case 79:
				respCodeDesc = "此卡可用，但发卡行暂不支持短信验证";
				break;
			case 80:
				respCodeDesc = "交易失败，Token 已过期";
				break;
			case 98:
				respCodeDesc = "文件不存在";
				break;
			case 99:
				respCodeDesc = "通用错误";
				break;
			case 100:
				respCodeDesc = "交易失败";
			default:
				break;
			}
		} else {
			if (respCode.equals("00A6")) {
				respCodeDesc = "有缺陷的成功";
			} else if (respCode.equals("PR05")) {
				respCodeDesc = "审核中";
			} else if (respCode.equals("PR99")) {
				respCodeDesc = "审核失败";
			}
		}
		return respCodeDesc;
	}
}
