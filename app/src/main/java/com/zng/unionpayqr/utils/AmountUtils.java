package com.zng.unionpayqr.utils;

import java.math.BigDecimal;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

public class AmountUtils {

	private static String numberStr = null;

	/** 金额为分的格式 */
	public static final String CURRENCY_FEN_REGEX = "\\-?[0-9]+";

	/**
	 * 将分为单位的转换为元并返回金额格式的字符串 （除100）
	 * 
	 * @param amount
	 * @return
	 * @throws Exception
	 */
	public static String changeF2Y(Long amount) throws Exception {
		if (!amount.toString().matches(CURRENCY_FEN_REGEX)) {
			throw new Exception("金额格式有误");
		}

		int flag = 0;
		String amString = amount.toString();
		if (amString.charAt(0) == '-') {
			flag = 1;
			amString = amString.substring(1);
		}
		StringBuffer result = new StringBuffer();
		if (amString.length() == 1) {
			result.append("0.0").append(amString);
		} else if (amString.length() == 2) {
			result.append("0.").append(amString);
		} else {
			String intString = amString.substring(0, amString.length() - 2);
			for (int i = 1; i <= intString.length(); i++) {
				if ((i - 1) % 3 == 0 && i != 1) {
					result.append(",");
				}
				result.append(intString.substring(intString.length() - i, intString.length() - i + 1));
			}
			result.reverse().append(".").append(amString.substring(amString.length() - 2));
		}
		if (flag == 1) {
			return "-" + result.toString();
		} else {
			return result.toString();
		}
	}

	/**
	 * 将分为单位的转换为元 （除100）
	 * 
	 * @param amount
	 * @return
	 * @throws Exception
	 */
	public static String changeF2Y(String amount) throws Exception {
		if (!amount.matches(CURRENCY_FEN_REGEX)) {
			throw new Exception("金额格式有误");
		}
		return BigDecimal.valueOf(Long.valueOf(amount)).divide(new BigDecimal(100)).toString();
	}

	/**
	 * 将元为单位的转换为分 （乘100）
	 * 
	 * @param amount
	 * @return
	 */
	public static String changeY2F(Long amount) {
		return BigDecimal.valueOf(amount).multiply(new BigDecimal(100)).toString();
	}

	/**
	 * 将元为单位的转换为分 替换小数点，支持以逗号区分的金额
	 * 
	 * @param amount
	 * @return
	 */
	public static String changeY2F(String amount) {
		String currency = amount.replaceAll("\\$|\\￥|\\,", ""); // 处理包含, ￥
																// 或者$的金额
		int index = currency.indexOf(".");
		int length = currency.length();
		Long amLong = 0l;
		if (index == -1) {
			amLong = Long.valueOf(currency + "00");
		} else if (length - index >= 3) {
			amLong = Long.valueOf((currency.substring(0, index + 3)).replace(".", ""));
		} else if (length - index == 2) {
			amLong = Long.valueOf((currency.substring(0, index + 2)).replace(".", "") + 0);
		} else {
			amLong = Long.valueOf((currency.substring(0, index + 1)).replace(".", "") + "00");
		}
		return amLong.toString();
	}

	public static void setPricePoint(final EditText editText) {

		editText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (!TextUtils.isEmpty(s)) {
					int lenght = s.length();
					Log.e("Sun", s + "====" + start + "=======" + before + "======" + count);
					double number = 0.00; // 初始金额
					// 第一次输入初始化 金额值
					if (lenght <= 1) {
						number = Double.parseDouble(s.toString());
						number = number / 100;// 第一次 长度等于
						numberStr = number + "";
					} else {
						// 之后的输入带入算法后将值设置给 金额值
						if (s.toString().contains(".")) {
							numberStr = getMoneyString(s.toString()); // 这个方法看第三步
						}
					}
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				String aa = editText.getText().toString();

				// 在此判断输入框的值是否等于金额的值，如果不相同则赋值，如果不判断监听器将会出现死循环
				if (!TextUtils.isEmpty(editText.getText().toString())
						&& !editText.getText().toString().equals(numberStr)) {

					editText.setText(numberStr); // 赋值到editText上
					editText.setSelection(numberStr.length()); // 将光标定位到结尾
				}
			}
		});
	}

	// 定义一个处理字符串的方法
	private static String getMoneyString(String money) {
		String overMoney = "";// 结果
		String[] pointBoth = money.split("\\.");// 分隔点前点后
		String beginOne = pointBoth[0].substring(pointBoth[0].length() - 1);// 前一位
		String endOne = pointBoth[1].substring(0, 1);// 后一位
		// 小数点前一位前面的字符串，小数点后一位后面
		String beginPoint = pointBoth[0].substring(0, pointBoth[0].length() - 1);
		String endPoint = pointBoth[1].substring(1);
		Log.e("Sun", pointBoth[0] + "===" + pointBoth[1] + "====" + beginOne + "=======" + endOne + "===>" + beginPoint
				+ "==" + endPoint);
		// 根据输入输出拼点
		if (pointBoth[1].length() > 2) {// 说明输入，小数点要往右移
			overMoney = pointBoth[0] + endOne + "." + endPoint;// 拼接实现右移动
		} else if (pointBoth[1].length() < 2) {// 说明回退,小数点左移
			overMoney = beginPoint + "." + beginOne + pointBoth[1];// 拼接实现左移
		} else {
			overMoney = money;
		}
		// 去除点前面的0 或者补 0
		String overLeft = overMoney.substring(0, overMoney.indexOf("."));// 得到前面的字符串
		Log.e("Sun", "左邊:" + overLeft + "===去零前" + overMoney);
		if (overLeft == null || overLeft == "" || overLeft.length() < 1) {// 如果没有就补零
			overMoney = "0" + overMoney;
		} else if (overLeft.length() > 1 && "0".equals(overLeft.subSequence(0, 1))) {// 如果前面有俩个零
			overMoney = overMoney.substring(1);// 去第一个0
		}
		Log.e("Sun", "結果:" + overMoney);
		return overMoney;
	}
}
