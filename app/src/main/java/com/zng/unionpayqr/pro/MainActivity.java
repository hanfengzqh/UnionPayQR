package com.zng.unionpayqr.pro;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zng.unionpayqr.R;
import com.zng.unionpayqr.base.BaseActivity;
import com.zng.unionpayqr.model.UnionPayResult;
import com.zng.unionpayqr.presenter.UnionPayPresenter;
import com.zng.unionpayqr.pro.cashier.fragment.SelectionAmountFragment;
import com.zng.unionpayqr.pro.cashier_manage.fragment.OrderManagerFragment;
import com.zng.unionpayqr.sdk.AcpService;
import com.zng.unionpayqr.sdk.LogUtil;
import com.zng.unionpayqr.utils.AppManager;
import com.zng.unionpayqr.utils.Constant;
import com.zng.unionpayqr.utils.NetworkUtil;
import com.zng.unionpayqr.utils.SPUtils;
import com.zng.unionpayqr.utils.UnionPayQRConstant;
import com.zng.unionpayqr.utils.Utils;
import com.zng.unionpayqr.view.ScanPayView;
import com.zng.unionpayqr.widget.Indicator;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity implements ScanPayView{
	@ViewInject(R.id.lyt_back)
	private LinearLayout lyt_back;

	@ViewInject(R.id.lyt_setting)
	private LinearLayout lyt_setting;

	@ViewInject(R.id.indicator)
	private Indicator indicator;

	@ViewInject(R.id.tv_pay_tab)
	private TextView tv_pay_tab;

	@ViewInject(R.id.tv_pay_order_tab)
	private TextView tv_pay_order_tab;

	@ViewInject(R.id.view_pager)
	private ViewPager view_pager;

	private FragmentPagerAdapter mAdapter;

	private List<Fragment> list;
	private UnionPayPresenter mPayRequestPresenter;

	@Override
	public void initContentView() {
		AppManager.getAppManager().addActivity(this);
		mPayRequestPresenter = new UnionPayPresenter();
		mPayRequestPresenter.attachView(this);
		checkHomeDataCacheIsOutTime();
		list = new ArrayList<Fragment>();
		list.add(new SelectionAmountFragment());//支付界面--Fragment
		list.add(new OrderManagerFragment());//支付订单---Fragment
		mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
			@Override
			public Fragment getItem(int position) {
				return list.get(position);
			}

			@Override
			public int getCount() {
				return list.size();
			}
		};

		view_pager.setAdapter(mAdapter);
	}

	@Override
	protected void setListener() {
		lyt_back.setOnClickListener(this);
		lyt_setting.setOnClickListener(this);
		tv_pay_tab.setOnClickListener(this);
		tv_pay_order_tab.setOnClickListener(this);
		view_pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				indicator.scroll(position, positionOffset);
			}

			@Override
			public void onPageSelected(int position) {
			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
	}

	@Override
	protected void onClickEvent(View paramView) {
		switch (paramView.getId()) {
		case R.id.lyt_back:
			finish();
			break;
		case R.id.lyt_setting:
			openActivity(SettingActivity.class);
			break;
		case R.id.tv_pay_tab:
			tv_pay_tab.setTextColor(Color.WHITE);
			view_pager.setCurrentItem(0);
			break;
		case R.id.tv_pay_order_tab:
			tv_pay_order_tab.setTextColor(Color.WHITE);
			view_pager.setCurrentItem(1);
			break;
		default:
			break;
		}
	}

	@Override
	public void getScanPayResult(UnionPayResult resultData) {
		
		int resultCode = AcpService.updateEncryptCert(resultData,
				"UTF-8");
		if (resultCode == 1) {
			LogUtil.writeLog("加密公钥更新成功");
		} else if (resultCode == 0) {
			LogUtil.writeLog("加密公钥无更新");
		} else {
			LogUtil.writeLog("加密公钥更新失败");
		}
	}

	@Override
	public void getQueryPayResult(UnionPayResult resultData) {
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mPayRequestPresenter.detachView();
	}

	/**
	 * 检测数据是否是在需求的时间之内
	 */
	private void checkHomeDataCacheIsOutTime(){
		try{
			long nowMillis = Utils.getCurrentMills();
			long homeCheckTime = SPUtils.getLong(mContext, Constant.KEY_HOME_CHECK_TIME, 0);
			boolean checkResult = (nowMillis - homeCheckTime) > Constant.CHECK_INTERVAL;
			Log.d("zqh","checkResult >> checkResult : "+checkResult);
			if((checkResult) && UnionPayQRConstant.isConfig(mContext)){
				if(NetworkUtil.checkNetwork(this)){
					SPUtils.putLong(mContext,Constant.KEY_HOME_CHECK_TIME,
							System.currentTimeMillis());
					mPayRequestPresenter.updatePublicKey(mContext,UnionPayQRConstant.SCAN_PAY_REQUEST);
				}
			}
		}catch(Exception e){
			Log.d("zqh","check home data cache is out time error.",e);
		}
	}
}
