package com.zng.unionpayqr.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PayResultInfo implements Parcelable {

	private String order_state;
	private String order_money;
	private String order_code;
	private String order_pay_date;
	private String order_query;
	private boolean order_success_force;
	private String pay_way;
	private String traceTime;

	public PayResultInfo() {
		super();
	}

	public PayResultInfo(String order_state, String order_money,
			String order_code, String order_pay_date, String traceTime,
			String order_query, boolean order_success_force, String pay_way) {
		super();
		this.order_state = order_state;
		this.order_money = order_money;
		this.order_code = order_code;
		this.order_pay_date = order_pay_date;
		this.traceTime = traceTime;
		this.order_query = order_query;
		this.order_success_force = order_success_force;
		this.pay_way = pay_way;
	}

	public String getPay_way() {
		return pay_way;
	}

	public void setPay_way(String pay_way) {
		this.pay_way = pay_way;
	}

	public String getOrder_state() {
		return order_state;
	}

	public void setOrder_state(String order_state) {
		this.order_state = order_state;
	}

	public String getOrder_money() {
		return order_money;
	}

	public void setOrder_money(String order_money) {
		this.order_money = order_money;
	}

	public String getOrder_code() {
		return order_code;
	}

	public void setOrder_code(String order_code) {
		this.order_code = order_code;
	}

	public String getOrder_pay_date() {
		return order_pay_date;
	}

	public void setOrder_pay_date(String order_pay_date) {
		this.order_pay_date = order_pay_date;
	}

	public String getOrder_query() {
		return order_query;
	}

	public void setOrder_query(String order_query) {
		this.order_query = order_query;
	}

	public boolean getOrder_success_force() {
		return order_success_force;
	}

	public void setOrder_success_force(boolean order_success_force) {
		this.order_success_force = order_success_force;
	}
	
	public String getTraceTime() {
		return traceTime;
	}

	public void setTraceTime(String traceTime) {
		this.traceTime = traceTime;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(order_state);
		out.writeString(order_money);
		out.writeString(order_code);
		out.writeString(order_pay_date);
		out.writeString(traceTime);
		out.writeString(order_query);
		out.writeByte((byte) (order_success_force ? 1 : 0));
		out.writeString(pay_way);
	}

	public PayResultInfo(Parcel in) {
		order_state = in.readString();
		order_money = in.readString();
		order_code = in.readString();
		order_pay_date = in.readString();
		traceTime = in.readString();
		order_query = in.readString();
		order_success_force = in.readByte() != 0;
		pay_way = in.readString();
	}

	public static final Parcelable.Creator<PayResultInfo> CREATOR = new Creator<PayResultInfo>() {
		@Override
		public PayResultInfo[] newArray(int size) {
			return new PayResultInfo[size];
		}

		@Override
		public PayResultInfo createFromParcel(Parcel in) {
			return new PayResultInfo(in);
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

}
