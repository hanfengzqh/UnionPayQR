package com.zng.unionpayqr.http;

import java.util.Map;

/**
 * 执行网络请求命令接口
 * @param <T>
 */
public interface IHttpCommand<T> {
	
	public Map<String, String> httpexecute(String url, IRequestParam<T> requestParam,String encoding);
	public Map<String, String> httpPost(String url, Map<String, String> requestParam,String encoding);	

}
