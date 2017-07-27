package com.zng.unionpayqr;

import com.zng.unionpayqr.utils.SPUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class USBBroadCastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context mContext, Intent intent) {
		String action = intent.getAction();
		if (action.equals(Intent.ACTION_MEDIA_EJECT)) {
			// USB设备移除，更新UI
			Log.d("liujie", "USB设备移除，更新UI");
			SPUtils.putString(mContext, "USBpath","");
		} else if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
			// USB设备挂载，更新UI
			String devicePath = intent.getDataString().toLowerCase()
					.substring(7);
			if (devicePath.contains("usb")) {
				// usb路径
				Log.d("liujie", "USB设备挂载，更新UI devicePath = " + devicePath);
				SPUtils.putString(mContext, "USBpath", devicePath);
			}
		}
	}

}
