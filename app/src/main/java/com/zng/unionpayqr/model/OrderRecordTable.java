package com.zng.unionpayqr.model;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import android.os.Parcel;
import android.os.Parcelable;

@Table(name = "order_record_tb")
public class OrderRecordTable implements Parcelable {
	@Column(name = "id", isId = true)
	private int id;

	@Column(name = "isRefund")
	private boolean isRefund;// 是否已经退款

	@Column(name = "isOrderForce")
	private boolean isOrderForce;// 订单是否强制完成

	@Column(name = "all_TradePayWay")
	private String all_TradePayWay;// 支付方式

	@Column(name = "all_TradeDateTime2")
	private String all_TradeDateTime2;// 支付时间

	@Column(name = "all_TradeMoney")
	private String all_TradeMoney;// 支付金额

	@Column(name = "all_NewMoney")
	private String all_NewMoney;// 退款金额

	@Column(name = "all_TradeDateTime")
	private String all_TradeDateTime;// 支付时间

	@Column(name = "all_NewDateTime")
	private String all_NewDateTime;// 退款时间

	@Column(name = "all_VoucherNumber")
	private String all_VoucherNumber;// 支付凭证(银联生成的流水号)

	@Column(name = "all_NewVoucherNumber")
	private String all_NewVoucherNumber;// 退款凭证(银联生成的流水号)

	@Column(name = "all_OrderNumber")
	private String all_OrderNumber;// 支付订单号(商户生成)

	@Column(name = "all_NewOrderNumber")
	private String all_NewOrderNumber;// 退款订单(商户退款生成的订单)

	@Column(name = "all_TradeUser")
	private String all_TradeUser;// 交易操作员

	@Column(name = "all_NewUser")
	private String all_NewUser;// 退款操作员

	@Column(name = "all_MerchantNum")
	private String all_MerchantNum;// 商户号
	
	public OrderRecordTable() {
		super();
	}

	public OrderRecordTable(int id, boolean isRefund, boolean isOrderForce,
			String all_TradePayWay, String all_TradeDateTime2,
			String all_TradeMoney, String all_NewMoney,
			String all_TradeDateTime, String all_NewDateTime,
			String all_VoucherNumber, String all_NewVoucherNumber,
			String all_OrderNumber, String all_NewOrderNumber,
			String all_TradeUser, String all_NewUser,String all_MerchantNum) {
		super();
		this.id = id;
		this.isRefund = isRefund;
		this.isOrderForce = isOrderForce;
		this.all_TradePayWay = all_TradePayWay;
		this.all_TradeDateTime2 = all_TradeDateTime2;
		this.all_TradeMoney = all_TradeMoney;
		this.all_NewMoney = all_NewMoney;
		this.all_TradeDateTime = all_TradeDateTime;
		this.all_NewDateTime = all_NewDateTime;
		this.all_VoucherNumber = all_VoucherNumber;
		this.all_NewVoucherNumber = all_NewVoucherNumber;
		this.all_OrderNumber = all_OrderNumber;
		this.all_NewOrderNumber = all_NewOrderNumber;
		this.all_TradeUser = all_TradeUser;
		this.all_NewUser = all_NewUser;
		this.all_MerchantNum = all_MerchantNum;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getAll_MerchantNum() {
		return all_MerchantNum;
	}

	public void setAll_MerchantNum(String all_MerchantNum) {
		this.all_MerchantNum = all_MerchantNum;
	}

	public void setRefund(boolean isRefund) {
		this.isRefund = isRefund;
	}

	public void setOrderForce(boolean isOrderForce) {
		this.isOrderForce = isOrderForce;
	}

	public boolean getIsRefund() {
		return isRefund;
	}

	public void setIsRefund(boolean isRefund) {
		this.isRefund = isRefund;
	}

	public boolean getIsOrderForce() {
		return isOrderForce;
	}

	public void setIsOrderForce(boolean isOrderForce) {
		this.isOrderForce = isOrderForce;
	}

	public String getAll_TradeDateTime2() {
		return all_TradeDateTime2;
	}

	public void setAll_TradeDateTime2(String all_TradeDateTime2) {
		this.all_TradeDateTime2 = all_TradeDateTime2;
	}

	public String getAll_TradeMoney() {
		return all_TradeMoney;
	}

	public void setAll_TradeMoney(String all_TradeMoney) {
		this.all_TradeMoney = all_TradeMoney;
	}

	public String getAll_NewMoney() {
		return all_NewMoney;
	}

	public void setAll_NewMoney(String all_NewMoney) {
		this.all_NewMoney = all_NewMoney;
	}

	public String getAll_TradeDateTime() {
		return all_TradeDateTime;
	}

	public void setAll_TradeDateTime(String all_TradeDateTime) {
		this.all_TradeDateTime = all_TradeDateTime;
	}

	public String getAll_NewDateTime() {
		return all_NewDateTime;
	}

	public void setAll_NewDateTime(String all_NewDateTime) {
		this.all_NewDateTime = all_NewDateTime;
	}

	public String getAll_VoucherNumber() {
		return all_VoucherNumber;
	}

	public void setAll_VoucherNumber(String all_VoucherNumber) {
		this.all_VoucherNumber = all_VoucherNumber;
	}

	public String getAll_NewVoucherNumber() {
		return all_NewVoucherNumber;
	}

	public void setAll_NewVoucherNumber(String all_NewVoucherNumber) {
		this.all_NewVoucherNumber = all_NewVoucherNumber;
	}

	public String getAll_OrderNumber() {
		return all_OrderNumber;
	}

	public void setAll_OrderNumber(String all_OrderNumber) {
		this.all_OrderNumber = all_OrderNumber;
	}

	public String getAll_NewOrderNumber() {
		return all_NewOrderNumber;
	}

	public void setAll_NewOrderNumber(String all_NewOrderNumber) {
		this.all_NewOrderNumber = all_NewOrderNumber;
	}

	public String getAll_TradeUser() {
		return all_TradeUser;
	}

	public void setAll_TradeUser(String all_TradeUser) {
		this.all_TradeUser = all_TradeUser;
	}

	public String getAll_NewUser() {
		return all_NewUser;
	}

	public void setAll_NewUser(String all_NewUser) {
		this.all_NewUser = all_NewUser;
	}

	public String getAll_TradePayWay() {
		return all_TradePayWay;
	}

	public void setAll_TradePayWay(String all_TradePayWay) {
		this.all_TradePayWay = all_TradePayWay;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(id);
		out.writeByte((byte) (isRefund ? 1 : 0));
		out.writeByte((byte) (isOrderForce ? 1 : 0));
		out.writeString(all_TradePayWay);
		out.writeString(all_TradeDateTime2);
		out.writeString(all_TradeMoney);
		out.writeString(all_NewMoney);
		out.writeString(all_TradeDateTime);
		out.writeString(all_NewDateTime);
		out.writeString(all_VoucherNumber);
		out.writeString(all_NewVoucherNumber);
		out.writeString(all_OrderNumber);
		out.writeString(all_NewOrderNumber);
		out.writeString(all_TradeUser);
		out.writeString(all_NewUser);
		out.writeString(all_MerchantNum);
	}

	public OrderRecordTable(Parcel in) {
		id = in.readInt();
		isRefund = in.readByte() != 0;
		isOrderForce = in.readByte() != 0;
		all_TradePayWay = in.readString();
		all_TradeDateTime2 = in.readString();
		all_TradeMoney = in.readString();
		all_NewMoney = in.readString();
		all_TradeDateTime = in.readString();
		all_NewDateTime = in.readString();
		all_VoucherNumber = in.readString();
		all_NewVoucherNumber = in.readString();
		all_OrderNumber = in.readString();
		all_NewOrderNumber = in.readString();
		all_TradeUser = in.readString();
		all_NewUser = in.readString();
		all_MerchantNum = in.readString();
	}

	public static final Parcelable.Creator<OrderRecordTable> CREATOR = new Creator<OrderRecordTable>() {
		@Override
		public OrderRecordTable[] newArray(int size) {
			return new OrderRecordTable[size];
		}

		@Override
		public OrderRecordTable createFromParcel(Parcel in) {
			return new OrderRecordTable(in);
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}
}
