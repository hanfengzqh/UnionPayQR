package com.zng.unionpayqr.utils;

import java.io.File;

import android.content.Context;
import android.text.TextUtils;

import com.zng.unionpayqr.sdk.SDKConfig;

public class UnionPayQRConstant {
	public final static String APPID = "DEFAULT";
	public final static String VERSION = "1.0";
	public final static String MCHID = "779430100420002";

	public final static String H5_CHANNEL = "01";
	public final static String ALIPAY_CHANNEL = "0";
	public final static String WECHAT_CHANNEL = "1";
	public final static String UNIONPAY_CHANNEL = "2";

	public final static String SCAN_PAY_REQUEST = "scan_pay_request";
	public final static String H5_PAY_REQUEST = "h5_pay_request";
	public final static String QUERY_PAY_REQUEST = "query_pay_request";
	public final static String CODE_PAY_REQUEST = "code_pay_request";
	public final static String REFUND_REQUEST = "refund_request";
	public final static String QUERY_REFUND_REQUEST = "query_refund_request";

	public final static String MERID = "merId";
	public final static String SERVER_URL = "server_url";
	public final static String PUBLIC_KEY = "merchantNum";
	public final static String PRIVATE_KEY = "signCertPwd";

	/**
	 * 二维码请求的type
	 */
	public static final String REQUEST_SCAN_TYPE = "type";
	/**
	 * 普通类型，扫完即关闭
	 */
	public static final int REQUEST_SCAN_TYPE_COMMON = 0;
	/**
	 * 服务商登记类型，扫描
	 */
	public static final int REQUEST_SCAN_TYPE_REGIST = 1;

	/**
	 * 扫描类型 条形码或者二维码：REQUEST_SCAN_MODE_ALL_MODE 条形码：
	 * REQUEST_SCAN_MODE_BARCODE_MODE 二维码：REQUEST_SCAN_MODE_QRCODE_MODE
	 *
	 */
	public static final String REQUEST_SCAN_MODE = "ScanMode";
	/**
	 * 条形码： REQUEST_SCAN_MODE_BARCODE_MODE
	 */
	public static final int REQUEST_SCAN_MODE_BARCODE_MODE = 0X100;
	/**
	 * 二维码：REQUEST_SCAN_MODE_ALL_MODE
	 */
	public static final int REQUEST_SCAN_MODE_QRCODE_MODE = 0X200;
	/**
	 * 条形码或者二维码：REQUEST_SCAN_MODE_ALL_MODE
	 */
	public static final int REQUEST_SCAN_MODE_ALL_MODE = 0X300;

	public static final String QRCODE = "qr_code";
	public static final String QRSCAN = "qr_scan";
	public static final String QRALL = "qr_all";
	/**
	 * 获取公钥信息
	 * 
	 * @param context
	 * @return
	 */
	public static final String getPublicKey(Context context) {
		String publicKey = SPUtils.getString(context, PUBLIC_KEY);
		return publicKey;
	}

	/**
	 * 获取私钥信息
	 * 
	 * @param context
	 * @return
	 */
	public static final String getPrivateKey(Context context) {
		String privateKey = SPUtils.getString(context, PRIVATE_KEY);
		return privateKey;
	}

	/**
	 * 获取商户平台号
	 * 
	 * @param context
	 * @return
	 */
	public static final String getMerId(Context context) {
		String merId = SPUtils.getString(context, MERID);
		if (TextUtils.isEmpty(merId)) {
			merId = MCHID;
		}
		return merId;
	}

	/**
	 * 获取服务器url
	 * 
	 * @param context
	 * @return
	 */
	public static final String getServerUrl(Context context) {
		String url = SPUtils.getString(context, SERVER_URL);
		return url;
	}

	/**
	 * 是否配置信息
	 * 
	 * @param context
	 * @param path
	 * @return
	 */
	public static boolean isConfig(Context context) {
		
		String acp_prodEncPath = SDKConfig.getConfig().getPackEnc();
		String acp_prodMiddlePath = SDKConfig.getConfig().getPackMiddle();
		String acp_prodRootPath = SDKConfig.getConfig().getPackRoot();
		String acp_prodSigndfxPath = SDKConfig.getConfig().getPackSign();
		
		File acp_prodEncFile = FileUtils.getFileByPath(acp_prodEncPath);
		File acp_prodMiddleFile = FileUtils.getFileByPath(acp_prodMiddlePath);
		File acp_prodRootFile = FileUtils.getFileByPath(acp_prodRootPath);
		File acp_prodSigndfxFile = FileUtils.getFileByPath(acp_prodSigndfxPath);
		
		return (!TextUtils.isEmpty(getPublicKey(context)) && !TextUtils.isEmpty(getPrivateKey(context)))
				&& (acp_prodEncFile.exists() && acp_prodMiddleFile.exists() && acp_prodRootFile.exists() 
						&& acp_prodSigndfxFile.exists());
	}

}
