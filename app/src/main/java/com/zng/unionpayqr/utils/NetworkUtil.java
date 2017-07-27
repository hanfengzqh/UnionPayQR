package com.zng.unionpayqr.utils;

import java.util.List;

import com.zng.unionpayqr.base.UnionPay;

import android.content.Context;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

public class NetworkUtil {
    private NetworkUtil() {

    }

    /**
     * 网络是否可用
     * 
     * @param Context
     * @return true--网络可用;false--网络不可用
     */
    public static final boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Gps是否打开
     * 
     * @param context
     * @return true--GPS已打开;false--GPS未打开
     */
    public static final boolean isGpsEnabled(Context context) {
        LocationManager locationManager = ((LocationManager) context.getSystemService(Context.LOCATION_SERVICE));
        List<String> accessibleProviders = locationManager.getProviders(true);
        return accessibleProviders != null && accessibleProviders.size() > 0;
    }

    /**
     * wifi是否可用
     * @return 
     */
    public static final boolean isWifiEnabled(Context context) {
        ConnectivityManager mgrConn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager mgrTel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return ((mgrConn.getActiveNetworkInfo() != null && mgrConn.getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || mgrTel
                .getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);
    }

    /**
     * 判断当前网络是否是wifi网络 
     * if(activeNetInfo.getType()==ConnectivityManager.TYPE_MOBILE) { //判断3G网
     * 
     * @param context
     * @return boolean  true--是wifi网络;false--移动网络
     */
    public static final boolean isWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if ((activeNetInfo != null) && (activeNetInfo.isConnected())
                && (activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI)) {
            return true;
        }
        return false;
    }

    /**
     * 判断当前网络是否是3G网络
     * 
     * @param context
     * @return boolean true--3G网络;false--不是3G网络
     * 
     */
    public static final boolean is3G(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager == null) return false;
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            return true;
        }
        return false;
    }

    /**
     * 另外还有两个方法判断网络是否可用：
     */

    public static final boolean isNetworkAvailable_0(Context context) {
        ConnectivityManager cm = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        if (cm != null) {
            NetworkInfo info = cm.getActiveNetworkInfo();
            if (info != null && info.isConnectedOrConnecting()) {
                return true;
            }
        }
        return false;
    }

    public static final boolean isNetworkAvailable_1(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = cm.getActiveNetworkInfo();
        if (network != null) {
            return network.isAvailable();
        }
        return false;
    }

    /**
     * 更加严谨的写法：判断网络是否可用
     * @return true--网络通常;false--网络连接失败;
     * 
     */
    public static final boolean checkNetwork(Context context) {
    	if(context==null){
    		return false;
    	}
    	try {
    		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
		} catch (Exception e) {
		}
        
        return false;
    }

    public static final boolean hasHttpConnectionBug() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO;
    }

    public static final void disableConnectionReuseIfNecessary() {
        // HTTP connection reuse which was buggy pre-froyo
        if (hasHttpConnectionBug()) {
            System.setProperty("http.keepAlive", "false");
        }
    }
    
    
    public static final String HOTSPOT = "HOSPOT";
    public static final String WLAN = "WLAN";
    public static final String MOBILE = "MOBILE";
    public static final String NOCONNECT = "NOCONNECT";

    public static final String getNetworkStatus(Context context){
        String status = NOCONNECT;
        if(is3G(context)){
            status = MOBILE;
        }else if(isWifiEnabled(context)){
            if(isWifiHotspot(context)){
                status = HOTSPOT;
            }else if(isWifi(context)){
                status = WLAN;
            }
        }
        return status;
    }
    
    
    public static final String getNetworkType(){
    	Context context = UnionPay.getInstance().getApplicationContext();
    	String type = "offline";
    	
    	 if(isWifiEnabled(context) || isWifi(context)){
    		type = "wifi";
    	}else if(isWap(context)){
    		     type = "wap";
    	}else if(is3G(context)){
    		type="mobile";
    	}
    	 return type;
    }
    
    private static final boolean isWap(Context context){
    	ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    	
    	NetworkInfo mobileNetInfo = manager.getActiveNetworkInfo();
    	
    	if(mobileNetInfo!=null  && mobileNetInfo.isAvailable()){
    		int netType = mobileNetInfo.getType();
    		
    		if(netType == ConnectivityManager.TYPE_MOBILE){
    			
    			Uri uri = Uri.parse("content://telephony/carriers/preferapn");
    			String CTWAP = "ctwap";
    			String CMWAP = "cmwap";
    			String WAP3G = "3gwap";
    			String UNIWAP = "uniwap";
    			
    			Cursor c = context.getContentResolver().query(uri, null, null, null, null);
    			
    			if(c!=null && c.moveToFirst()){
    				String user = c.getString(c.getColumnIndex("user"));
    				if(!TextUtils.isEmpty(user)){
    					if(user.startsWith(CTWAP)){
    						return true;
    					}
    				}
    			}
    			
    			String netMode = mobileNetInfo.getExtraInfo();
    			
    			if(!TextUtils.isEmpty(netMode)){
    				netMode = netMode.toLowerCase();
    				
    				if(netMode.equals(CMWAP) 
    						|| netMode.equals(WAP3G)
    						|| netMode.equals(UNIWAP)){
    					return true;
    				}
    			}
    			
    		}
    	}
    	return false;
    }
    
    public static final boolean isWifiHotspot(Context context) {
        final String HOTSPOT_IP = "192.168.43.";
        final String HOTSPOT_IP2 = "172.20.10.";
        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);  
        if (!wifiManager.isWifiEnabled()) {  
            return false;
        }  
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();     
        if(wifiInfo == null) {
            return false;
        }
        String ip = android.text.format.Formatter.formatIpAddress(wifiInfo.getIpAddress());   
        Log.d("gw", "ip="+ip);
        if(ip != null) {
            if(ip.startsWith(HOTSPOT_IP) || ip.startsWith(HOTSPOT_IP2)) {
                return true;
            }
        }

        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo(); 
        if(dhcpInfo == null) {
            return false;
        }
        String dhcp_ip = android.text.format.Formatter.formatIpAddress(dhcpInfo.ipAddress);   
        Log.d("gw", "dhcp_ip="+dhcp_ip);
        if(dhcp_ip != null) {
            if(dhcp_ip.startsWith(HOTSPOT_IP) || dhcp_ip.startsWith(HOTSPOT_IP2)) {
                return true;
            }
        }

        String dhcp_gateway = android.text.format.Formatter.formatIpAddress(dhcpInfo.gateway);   
        Log.d("gw", "dhcp_gateway="+dhcp_gateway);
        if(dhcp_gateway != null) {
            if(dhcp_gateway.startsWith(HOTSPOT_IP) || dhcp_gateway.startsWith(HOTSPOT_IP2)) {
                return true;
            }
        }

        return false;
    }
}
