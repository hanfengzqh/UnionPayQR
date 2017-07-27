package com.zng.unionpayqr.http.utils;
import java.util.Map;

import android.os.AsyncTask;

import com.zng.unionpayqr.http.IHttpCommand;
import com.zng.unionpayqr.http.OnHttpResultListener;
import com.zng.unionpayqr.utils.DemoBase;

/**
 * 异步任务执行网络请求
 */
public class HttpTask extends AsyncTask<Map<String, String>, Void, Map<String, String>> {

	private Map<String, String> requestParam;
	private OnHttpResultListener onHttpResultListener;
	@SuppressWarnings("rawtypes")
	private IHttpCommand httpCommand;
    private String url;
    
    /**
     * 异步请求构造方法
     * @param url 请求地址
     * @param requestParam 请求参数
     * @param httpCommand 网络请求实现
     * @param onHttpResultListener 网络请求参数返回结果处理
     */
	@SuppressWarnings("rawtypes")
	public HttpTask(String url,Map<String, String> requestParam,
			IHttpCommand httpCommand, OnHttpResultListener onHttpResultListener) {
		this.url=url;
		this.requestParam = requestParam;
		this.httpCommand = httpCommand;
		this.onHttpResultListener = onHttpResultListener;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Map<String, String> doInBackground(Map<String, String>... params) {
		try {
			return httpCommand.httpPost(url, requestParam,DemoBase.encoding);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(Map<String, String> result) {
		if (this.onHttpResultListener != null) {
			this.onHttpResultListener.onResult(result);
		}
	}

}
