package com.zng.unionpayqr.http.utils;



import java.util.Map;

import com.zng.unionpayqr.http.IHttpCommand;
import com.zng.unionpayqr.http.IRequestParam;
import com.zng.unionpayqr.http.OnHttpResultListener;
import com.zng.unionpayqr.utils.DemoBase;

import android.os.AsyncTask;

/**
 * 异步任务执行网络请求类---公共类
 */
public class HttpPostTask extends AsyncTask<Map<String, String>, Void, Map<String, String>> {

	private String url;
	@SuppressWarnings("rawtypes")
	private IRequestParam requestParam;
	private OnHttpResultListener onHttpResultListener;
	@SuppressWarnings("rawtypes")
	private IHttpCommand httpCommand;

	@SuppressWarnings("rawtypes")
	public HttpPostTask(String url, IRequestParam requestParam,
			IHttpCommand httpCommand, OnHttpResultListener onHttpResultListener) {
		this.url = url;
		this.requestParam = requestParam;
		this.httpCommand = httpCommand;
		this.onHttpResultListener = onHttpResultListener;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Map<String, String> doInBackground(Map<String, String>... params) {
		try {
			return httpCommand.httpexecute(url, requestParam,DemoBase.encoding);
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
