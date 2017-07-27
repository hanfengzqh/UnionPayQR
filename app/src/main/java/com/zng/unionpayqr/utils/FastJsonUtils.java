package com.zng.unionpayqr.utils;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class FastJsonUtils {

	/**
	 * 功能描述：把JSON数据转换成普通字符串列表
	 * 
	 * @param jsonData
	 *            JSON数据
	 * @return
	 * @throws Exception
	 * @author myclover
	 */
	public static List<String> json2List(String jsonData) throws Exception {
		return JSON.parseArray(jsonData, String.class);
	}

	/**
	 * 功能描述：把JSON数据转换成指定的java对象
	 * 
	 * @param jsonData
	 *            JSON数据
	 * @param clazz
	 *            指定的java对象
	 * @return
	 * @throws Exception
	 * @author myclover
	 */
	public static <T> T json2pojo(String jsonData, Class<T> clazz)
			throws Exception {
		return JSON.parseObject(jsonData, clazz);
	}

	/**
	 * 功能描述：把JSON数据转换成指定的java对象列表
	 * 
	 * @param jsonData
	 *            JSON数据
	 * @param clazz
	 *            指定的java对象
	 * @return
	 * @throws Exception
	 * @author myclover
	 */
	public static <T> List<T> getBeanList(String jsonData, Class<T> clazz)
			throws Exception {
		return JSON.parseArray(jsonData, clazz);
	}

	/**
	 * 功能描述：把JSON数据转换成较为复杂的java对象列表
	 * 
	 * @param jsonData
	 *            JSON数据
	 * @return
	 * @throws Exception
	 * @author myclover
	 */
	public static List<Map<String, Object>> getBeanMapList(String jsonData)
			throws Exception {
		return JSON.parseObject(jsonData,
				new TypeReference<List<Map<String, Object>>>() {
				});
	}

	public static Object getObject(String jsonData, Class object) {
		return JSON.parseObject(jsonData, object);
	}

	public static JSONObject getJsonObject(String jsonData) {

		return JSONObject.parseObject(jsonData);
	}

	/**
	 * 将网络请求下来的数据用fastjson处理空的情况，并将时间戳转化为标准时间格式
	 * 
	 * @param result
	 * @return
	 */
	public static String dealResponseResult(String result) {
		result = JSONObject.toJSONString(result,
				SerializerFeature.WriteClassName,
				SerializerFeature.WriteMapNullValue,
				SerializerFeature.WriteNullBooleanAsFalse,
				SerializerFeature.WriteNullListAsEmpty,
				SerializerFeature.WriteNullNumberAsZero,
				SerializerFeature.WriteNullStringAsEmpty,
				SerializerFeature.WriteDateUseDateFormat,
				SerializerFeature.WriteEnumUsingToString,
				SerializerFeature.WriteSlashAsSpecial,
				SerializerFeature.WriteTabAsSpecial);
		return result;
	}

	/**
	 * json转Map
	 * 
	 * @param json
	 * @return
	 */
	public static Map<String, Object> json2Map(String json) {
		return JSON.parseObject(json, Map.class);
	}

	public static String obj2JsonString(Object obj) {
		return JSON.toJSONString(obj);
	}
}
