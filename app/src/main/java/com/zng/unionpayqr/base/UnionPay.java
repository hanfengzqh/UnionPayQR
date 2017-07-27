package com.zng.unionpayqr.base;

import org.xutils.BuildConfig;
import org.xutils.DbManager;
import org.xutils.x;

import android.app.Application;
import android.content.Context;

import com.zng.unionpayqr.sdk.SDKConfig;
import com.zng.unionpayqr.utils.UnionPayQRConstant;

public class UnionPay extends Application {

	private DbManager.DaoConfig daoConfig;
	private static UnionPay INSTANCE;
	public static Context mContext;
	public static String path;// 工程相对路径
	
	public DbManager.DaoConfig getDaoConfig() {
		return daoConfig;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		INSTANCE = this;
        mContext = getApplicationContext();
        path = getApplicationContext().getFilesDir().getAbsolutePath();
        if (UnionPayQRConstant.isConfig(mContext)) {
        	SDKConfig.getConfig().loadPropertiesFromSrc(this);
		}
        
		x.Ext.init(this);
		x.Ext.setDebug(BuildConfig.DEBUG); // 是否输出debug日志, 开启debug会影响性能
		daoConfig = new DbManager.DaoConfig().setDbName("order_manage_db")// 创建数据库的名称
				.setDbVersion(1)// 数据库版本号
				.setDbUpgradeListener(new DbManager.DbUpgradeListener() {
					@Override
					public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
						// TODO: ...
						// db.addColumn(...);
						// db.dropTable(...);
						// ...
					}
				});// 数据库更新操作
	}

	public static UnionPay getInstance() {
		return INSTANCE;
	}
    
    public static final Context getAppContext(){
    	return mContext;
    }
    
   public static final String getPath(){
	   return path;
   }
}
