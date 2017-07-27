package com.zng.unionpayqr.pro.cashier_manage.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.xutils.DbManager;
import org.xutils.x;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.zng.unionpayqr.R;
import com.zng.unionpayqr.base.BaseFragment;
import com.zng.unionpayqr.base.UnionPay;
import com.zng.unionpayqr.base.adapter.BGAOnRVItemClickListener;
import com.zng.unionpayqr.model.FilterData;
import com.zng.unionpayqr.model.FilterEntity;
import com.zng.unionpayqr.model.ModelUtil;
import com.zng.unionpayqr.model.OrderRecordTable;
import com.zng.unionpayqr.pro.cashier.adapter.OrderManagerAdapter;
import com.zng.unionpayqr.pro.cashier_manage.activity.OrderDetailsActivity;
import com.zng.unionpayqr.refreshlayout.utils.Divider;
import com.zng.unionpayqr.refreshlayout.view.SuperRefreshLayout;
import com.zng.unionpayqr.refreshlayout.view.SuperRefreshLayout.RefreshListener;
import com.zng.unionpayqr.utils.UnionPayQRConstant;
import com.zng.unionpayqr.widget.FilterView;

@ContentView(R.layout.activity_order_manager)
public class OrderManagerFragment extends BaseFragment implements 
				RefreshListener, BGAOnRVItemClickListener {

	@ViewInject(R.id.refresh_layout)
	private SuperRefreshLayout refresh_layout;

	@ViewInject(R.id.recyclerView)
	private RecyclerView recyclerView;

	@ViewInject(R.id.fv_top_filter)
	FilterView fvTopFilter;

	private OrderManagerAdapter mOrderManagerAdapter;

	private List<OrderRecordTable> orderRecordList = new ArrayList<OrderRecordTable>();

	private DbManager db;
	private FilterData filterData; // 筛选数据
	private String order_pay_way;

	@Override
	public void initContentView() {
		db = x.getDb(((UnionPay) mContext.getApplicationContext()).getDaoConfig());
		// 设置RecyclerView的布局管理
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		// 如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
		recyclerView.setHasFixedSize(true);
		// 添加我们自定义的分隔线
		recyclerView.addItemDecoration(new Divider(mContext));
		recyclerView.setItemAnimator(new DefaultItemAnimator());
		refresh_layout.setRefreshListener(this);
		//初始化item--Adapter
		mOrderManagerAdapter = new OrderManagerAdapter(recyclerView);
		recyclerView.setAdapter(mOrderManagerAdapter);
		//初始化筛选条件
		initData();
		initDBInfo(order_pay_way);
	}

	@Override
	public void initData() {
		// 筛选数据
		filterData = new FilterData();
		filterData.setFilters(ModelUtil.getFilterData());
		fvTopFilter.setFilterData(mActivity, filterData);
	}
	
	/**
	 * 
	 * @param order_pay_way 
	 * 		      支付方式 qr_code;qr_scan
	 * 
	 */
	public void initDBInfo(String order_pay_way) {
		try {
			if (orderRecordList != null && orderRecordList.size() > 0) {
				orderRecordList.clear();
			}
			if (TextUtils.isEmpty(order_pay_way) || order_pay_way.equals(UnionPayQRConstant.QRALL)) {
				orderRecordList = db.selector(OrderRecordTable.class).findAll();
			} else {
				orderRecordList = db.selector(OrderRecordTable.class).where("all_TradePayWay", "=", order_pay_way)
						.findAll();
			}
			
			if (orderRecordList != null) {
				Collections.reverse(orderRecordList);// 实现list集合逆序排列
				mOrderManagerAdapter.setData(orderRecordList);
				mOrderManagerAdapter.notifyDataSetChanged();
			}

		} catch (DbException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void setListener() {
		if (mOrderManagerAdapter != null) {
			mOrderManagerAdapter.setOnRVItemClickListener(this);
		}
		// (真正的)筛选视图点击
		fvTopFilter.setOnFilterClickListener(new FilterView.OnFilterClickListener() {
			@Override
			public void onFilterClick(int position) {
				fvTopFilter.showFilterLayout(position);
			}
		});

		// 分类Item点击
		fvTopFilter.setOnItemFilterClickListener(new FilterView.OnItemFilterClickListener() {

			@Override
			public void onItemFilterClick(FilterEntity entity) {
				order_pay_way = entity.getKey();
				initDBInfo(order_pay_way);
			}
		});

	}

	@Override
	protected void onClickEvent(View paramView) {
	}

	@Override
	public void onRefresh(SuperRefreshLayout superRefreshLayout) {

		initDBInfo(order_pay_way);
		refresh_layout.finishRefresh();
	}

	@Override
	public void onRefreshLoadMore(SuperRefreshLayout superRefreshLayout) {
		refresh_layout.finishLoadMore();
	}
	
	
	//item 点击事件
	@Override
	public void onRVItemClick(ViewGroup parent, View itemView, int position) {
		Bundle bundle = new Bundle();
		bundle.putParcelable("OrderRecordTable", mOrderManagerAdapter.getItem(position));
		openActivity(mActivity, OrderDetailsActivity.class, bundle);
	}
	
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		order_pay_way = null;
	}
}
