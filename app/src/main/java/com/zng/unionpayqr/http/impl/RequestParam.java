package com.zng.unionpayqr.http.impl;



import java.util.HashMap;
import java.util.Map;

import com.zng.unionpayqr.http.IRequestParam;


public class RequestParam implements IRequestParam<Map<String,Object>> {

	private Map<String, Object> paramMap = new HashMap<String, Object>();

	@Override
	public void put(String key, Object value) {
		paramMap.put(key, value);
	}

	@Override
	public Object get(String key) {
		return paramMap.get(key);
	}

	@Override
	public int size() {
		return paramMap.size();
	}

	@Override
	public Map<String, Object> getRequestParam() {
		return paramMap;
	}
}
