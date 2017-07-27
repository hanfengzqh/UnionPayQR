package com.zng.unionpayqr.utils;

import android.content.Context;
import android.provider.Settings;
import android.text.format.Time;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Utils {

	/**
	 * 存放值 settings 数据库
	 * 
	 * @param mContext
	 *            上下文对象
	 * @param key
	 *            存放key
	 * @param values
	 *            存放值
	 */
	public static void putStringValuesIntoSettings(Context mContext, String key, String values) {
		Settings.System.putString(mContext.getContentResolver(), key, values);
	}

	/**
	 * 获取值 settings 数据库
	 * 
	 * @param mContext
	 *            上下文对象
	 * @param key
	 *            存放key
	 */
	public static String getStringValuesIntoSettings(Context mContext, String key) {
		return Settings.System.getString(mContext.getContentResolver(), key);
	}
	
	
	

	/** 获取本地包名 */
	public static String getLocalPackageName() {
		String inner_Ver = getInner_Ver();
		int lastIndexOf = inner_Ver.lastIndexOf("_V");
		String localPackageName = inner_Ver.substring(0, lastIndexOf);
		// Logger.d("zqh",
		// "LocalPackageName = " + localPackageName.replace('_', '.'));
		return localPackageName.replace('_', '.');
	}

	/** 获取本地版本号 */
	public static String getLocalVersionCode() {
		String inner_Ver = getInner_Ver();
		int length = inner_Ver.length();
		int lastIndexOf = inner_Ver.lastIndexOf("_V");
		String localVersionCode = inner_Ver.substring(lastIndexOf + 2, length);
		 Logger.d("zqh", "localVersionCode = " + localVersionCode);
		return localVersionCode;
	}

	/** 获取本地版本号 */
	public static String getCompileTime() {
		String ver = "";
		String compileTime = "";
		if (android.os.Build.DISPLAY
				.contains(android.os.Build.VERSION.INCREMENTAL)) {
			ver = android.os.Build.DISPLAY;
		} else {
			ver = android.os.Build.VERSION.INCREMENTAL;
		}

		int length = ver.length();
		if (length > 4) {
			int lastIndexOf = ver.lastIndexOf("ZNG.");
			compileTime = ver.substring(lastIndexOf + 4, length);
		}
		return compileTime;
	}
	
	
	/**
	 * INNER-VER 内部版本 return String
	 */

	public static String getInner_Ver() {
		String ver = "";

		if (android.os.Build.DISPLAY
				.contains(android.os.Build.VERSION.INCREMENTAL)) {
			ver = android.os.Build.DISPLAY;
		} else {
			ver = android.os.Build.VERSION.INCREMENTAL;
		}
		// Logger.d("zqh", "ver = " + ver + "");
		int lastIndexOf = ver.lastIndexOf("_R");
		String innerVer = ver.substring(0, lastIndexOf);
		// Logger.d("zqh", "innerVer = " + innerVer);
		return innerVer;
	}
	
	/**
	 * 判断是否为整型数字类型字符串
	 * 
	 * @param number
	 * @return
	 */
	public static final boolean isInteger(final String number) {
		boolean result = false;
		if (number != null) {
			Pattern pattern = Pattern.compile("^-?\\d+$");
			Matcher matcher = pattern.matcher(number.trim());
			result = matcher.matches();
		}
		return result;
	}
	
	/**
	 * 时间转化器 20170718144059---->2017-07-18 14:40:59
	 * @param time 2017-03-27
	 * @return 20170327
	 */
	public static String converterTime(String time){
		StringBuilder sb = new StringBuilder();
		if (time.length()>=14) {
			sb.append(time.substring(0, 4))
			.append("-").append(time.substring(4, 6))
			.append("-").append(time.substring(6, 8))
			.append(" ").append(time.substring(8, 10))
			.append(":").append(time.substring(10, 12))
			.append(":").append(time.substring(12, 14));
			return sb.toString();
		}else{
			return TimeUtils.getCurTimeString(TimeUtils.DEFAULT_SDF);
		}
	}
	
	public static final long getCurrentMills() {
        final Time now = new Time();
        now.setToNow();
        return now.toMillis(false);
    }
}
