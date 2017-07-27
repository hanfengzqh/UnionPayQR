package com.zng.unionpayqr.pro.cashier.adapter;
import android.support.v7.widget.RecyclerView;

import com.zng.unionpayqr.R;
import com.zng.unionpayqr.base.adapter.BGARecyclerViewAdapter;
import com.zng.unionpayqr.base.adapter.BGAViewHolderHelper;
import com.zng.unionpayqr.model.OrderRecordTable;
import com.zng.unionpayqr.utils.PayUtils;

/**
 * Created by JDD on 2016/4/23 0023.
 */
public class OrderManagerAdapter extends BGARecyclerViewAdapter<OrderRecordTable>{

	public OrderManagerAdapter(RecyclerView recyclerView) {
		super(recyclerView, R.layout.item_order_manager);
	}

	@Override
	protected void fillData(BGAViewHolderHelper viewHolderHelper, int position, OrderRecordTable model) {
		viewHolderHelper.setText(R.id.tv_voucher, model.getAll_VoucherNumber());
		viewHolderHelper.setText(R.id.tv_money, model.getAll_TradeMoney()+"元");
		viewHolderHelper.setText(R.id.tv_order_type, PayUtils.getOrderState(model.getAll_TradePayWay()));
	}
}
