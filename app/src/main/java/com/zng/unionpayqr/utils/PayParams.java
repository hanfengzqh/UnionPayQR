package com.zng.unionpayqr.utils;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.zng.unionpayqr.sdk.AcpService;
import com.zng.unionpayqr.sdk.SDKConfig;
import com.zng.unionpayqr.sdk.SDKConstants;

public class PayParams {
	
	/***
	 * QR被扫订单
	 * @param reqNo   请求流水单号
	 * @param total_fee 支付金额
	 * @param C2BCode 支付授权码
	 * @return
	 */
	public static Map<String, String> codePayRequest(Context mContext, String reqNo,String total_fee,String C2BCode) {
		Map<String, String> contentData = new HashMap<String, String>();
		commonParams(contentData);
		/***银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改***/
		contentData.put(SDKConstants.param_txnType, "01");     //交易类型 01:消费
		contentData.put(SDKConstants.param_txnSubType, "06");  //交易子类 07：申请消费二维码
		contentData.put(SDKConstants.param_channelType, "08"); //渠道类型 08手机
		
		/***商户接入参数***/
		contentData.put(SDKConstants.param_qrNo, C2BCode);
		contentData.put(SDKConstants.param_orderId, reqNo); 	//商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则	
		contentData.put(SDKConstants.param_txnAmt, total_fee);	//交易金额 单位为分，不能带小数点
		
		/**对请求参数进行签名并发送http post请求，接收同步应答报文**/
		Map<String, String> reqData = AcpService.sign(contentData,DemoBase.encoding);
		return reqData;
	}
	
	
	/***
	 * QR主扫
	 * @param reqNo   请求流水
	 * @param total_fee 支付金额
	 * @return
	 */
	public static Map<String, String> scanPayRequest(Context mContext, 
			String reqNo,String total_fee) {
		Map<String, String> contentData = new HashMap<String, String>();
		
		commonParams(contentData);
		/***银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改***/
		contentData.put(SDKConstants.param_txnType, "01");      //交易类型 01:消费
		contentData.put(SDKConstants.param_txnSubType, "07");   //交易子类 07：申请消费二维码
		contentData.put(SDKConstants.param_channelType, "08");  //渠道类型 08手机
		/***商户接入参数***/
		contentData.put(SDKConstants.param_orderId, reqNo); 	//商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则	
		contentData.put(SDKConstants.param_txnAmt, total_fee);	//交易金额 单位为分，不能带小数点
		/**对请求参数进行签名并发送http post请求，接收同步应答报文**/
		Map<String, String> reqData = AcpService.sign(contentData,DemoBase.encoding);			 //报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
		return reqData;
	}
	
	/**组织公共参数*/
	public static void commonParams(Map<String, String> contentData){
		contentData.put(SDKConstants.param_version, DemoBase.version);       //版本号 全渠道默认值
		contentData.put(SDKConstants.param_encoding, DemoBase.encoding);     //字符集编码 可以使用UTF-8,GBK两种方式
		contentData.put(SDKConstants.param_signMethod, SDKConfig.getConfig().getSignMethod()); //签名方法
		contentData.put(SDKConstants.param_bizType, "000000");          		 	//填写000000
		contentData.put(SDKConstants.param_merId, SDKConfig.getConfig().getMerchantNum());
		contentData.put(SDKConstants.param_accessType, "0"); 
		contentData.put(SDKConstants.param_txnTime, DemoBase.getCurrentTime());
		contentData.put(SDKConstants.param_currencyCode, "156");
		contentData.put(SDKConstants.param_backUrl, DemoBase.backUrl);
	}
	
	/***
	 * QR查询
	 * @param ori_seq 查询交易流水
	 * @return
	 */
	public static Map<String, String> queryPayRequest(Context mContext,String ori_seq) {
		
		Map<String, String> data = new HashMap<String, String>();
		commonParams(data);
		/***银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改***/
		data.put(SDKConstants.param_txnType, "00");                             //交易类型 00-默认
		data.put(SDKConstants.param_txnSubType, "00");                          //交易子类型  默认00
		data.put(SDKConstants.param_bizType, "000000");                         //业务类型 
		
		/***要调通交易以下字段必须修改***/
		data.put(SDKConstants.param_orderId, ori_seq);                 			//****商户订单号，每次发交易测试需修改为被查询的交易的订单号
		/**请求参数设置完毕，以下对请求参数进行签名并发送http post请求，接收同步应答报文------------->**/
		Map<String, String> reqData = AcpService.sign(data,DemoBase.encoding);	
		return reqData;
	}
	

	
	/***
	 * 退货
	 * @param reqNo   请求流水(新生成的)
	 * @param queryId 原交易流水号
	 * @param refund_fee 退款金额
	 * @return
	 */
	public static Map<String, String> refundRequest(Context mContext, 
			String reqNo,String refund_fee,String queryId) {
		Map<String, String> data = new HashMap<String, String>();
		
		commonParams(data);
		/*** 银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改 ***/
		data.put(SDKConstants.param_txnType, "04"); // 交易类型 04-退货
		data.put(SDKConstants.param_txnSubType, "00"); // 交易子类型 默认00
		data.put(SDKConstants.param_channelType, "08"); // 渠道类型，07-PC，08-手机

		/*** 商户接入参数 ***/
		data.put(SDKConstants.param_orderId, reqNo); // 商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则，重新产生，不同于原消费
		data.put(SDKConstants.param_txnAmt, refund_fee); // ****退货金额，单位分，不要带小数点。退货金额小于等于原消费金额，当小于的时候可以多次退货至退货累计金额等于原消费金额

		/*** 要调通交易以下字段必须修改 ***/
		data.put(SDKConstants.param_origQryId, queryId); // ****原消费交易返回的的queryId，可以从消费交易后台通知接口中或者交易状态查询接口中获取
		
		/**请求参数设置完毕，以下对请求参数进行签名并发送http post请求，接收同步应答报文------------->**/
		Map<String, String> reqData  = AcpService.sign(data,DemoBase.encoding);	
		
		return reqData;
	}
	
	
	/***
	 * 消费撤销
	 * @param reqNo   请求流水
	 * @param refund_fee 撤销金额
	 * @param queryid 原交易流水号
	 * @return
	 */
	public static Map<String, String> revokeRequest(Context mContext,
			 String reqNo,String refund_fee,String queryid) {
		Map<String, String> data = new HashMap<String, String>();
		commonParams(data);
		/*** 银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改 ***/
		data.put(SDKConstants.param_txnType, "31"); // 交易类型 31-消费撤销
		data.put(SDKConstants.param_txnSubType, "00"); // 交易子类型 默认00
		data.put(SDKConstants.param_channelType, "08"); // 渠道类型，07-PC，08-手机
		
		/***商户接入参数***/
		data.put(SDKConstants.param_orderId, reqNo);       //商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则，重新产生，不同于原消费		
		data.put(SDKConstants.param_txnAmt, refund_fee);                          //【撤销金额】，消费撤销时必须和原消费金额相同	
		
		/***要调通交易以下字段必须修改***/
		data.put(SDKConstants.param_origQryId, queryid);
		
		/**请求参数设置完毕，以下对请求参数进行签名并发送http post请求，接收同步应答报文**/
		Map<String, String> reqData  = AcpService.sign(data,DemoBase.encoding);
		
		return reqData;
	}
	
	/**
	 * 签名公钥更新
	 * @param reqNo 交易流水号
	 * @return
	 */
	public static Map<String, String> publicKeyUpdate(Context mContext,String reqNo){
		Map<String, String> contentData = new HashMap<String, String>();
		commonParams(contentData);
		contentData.put(SDKConstants.param_txnType, "95"); // 交易类型 95-银联加密公钥更新查询
		contentData.put(SDKConstants.param_txnSubType, "00"); // 交易子类型 默认00
		contentData.put(SDKConstants.param_channelType, "07"); // 渠道类型

		contentData.put(SDKConstants.param_certType, "01"); // 01：敏感信息加密公钥(只有01可用)
		contentData.put(SDKConstants.param_orderId, DemoBase.getOrderId()); // 商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则

		Map<String, String> reqData = AcpService.sign(contentData,
				DemoBase.encoding); 
		
		return reqData;
	}
	
}
