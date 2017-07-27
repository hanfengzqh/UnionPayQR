package com.zng.unionpayqr.view;

import com.zng.unionpayqr.model.UnionPayResult;

public interface ScanPayView extends MvpView {
	
	 void getScanPayResult(UnionPayResult resultData);
	
	 void getQueryPayResult(UnionPayResult resultData);
	 
}
