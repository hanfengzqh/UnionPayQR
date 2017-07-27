package com.zng.unionpayqr.http.impl;

import java.util.Map;

import com.zng.unionpayqr.http.IHttpCommand;
import com.zng.unionpayqr.http.IRequestParam;
import com.zng.unionpayqr.sdk.AcpService;

/**
 * Created by Dream on 16/5/28.
 */
public class SystemHttpCommand implements IHttpCommand<Map<String,String>> {

	@Override
	public Map<String, String> httpexecute(String url, IRequestParam<Map<String, String>>requestParam,String encoding) {
		try {
			return AcpService.post(requestParam.getRequestParam(), url, encoding);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Map<String, String> httpPost(String url, Map<String, String> requestParam,String encoding) {
		try {
			return AcpService.post(requestParam, url, encoding);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


}
