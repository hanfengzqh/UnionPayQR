package com.zng.unionpayqr.utils;


public class Constant {
	
	public static final int ORDER_SUCCESS = 0;// 订单支付成功
	public static final int ORDER_FAIL = 1;// 订单未支付
	public static final int ORDER_ABNORMAL = 2;// 订单查询请求异常
	public static final int ORDER_TIMEOUT = 3;// 订单支付超时
	public static final int QUERY_START = 6;

	/** 显示 **/
	public static int VIEW_SHOW = 1;
	/** 显示断网 **/
	public static int VIEW_WIFIFAILUER = 2;
	/** 显示加载数据失败 **/
	public static int VIEW_LOADFAILURE = 3;
	/** 正在加载中 **/
	public static int VIEW_LOADING = 4;
	/** 没有数据 **/
	public static int VIEW_NODATA = 5;
	
	public static final String KEY_HOME_CHECK_TIME = "key_home_check_time";
	public static int CHECK_INTERVAL = Constant.Time.HOUR * 2;
	
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
	
	public static final class Time {
		public static final int SECOND = 1000;
		public static final int MINUTE = SECOND * 60;
		public static final int HOUR = MINUTE * 60;
		public static final int DAY = HOUR * 24;
	}
}
