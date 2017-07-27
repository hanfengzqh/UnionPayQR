package com.zng.unionpayqr.pro.cashier_manage.adapter;

import java.util.List;

import org.xutils.x;
import org.xutils.view.annotation.ViewInject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zng.unionpayqr.R;
import com.zng.unionpayqr.base.adapter.BaseListAdapter;
import com.zng.unionpayqr.model.FilterEntity;
import com.zng.unionpayqr.utils.PayUtils;

/**
 * Created by sunfusheng on 16/4/23.
 */
public class FilterOneAdapter extends BaseListAdapter<FilterEntity> {

    private FilterEntity selectedEntity;

    public FilterOneAdapter(Context context) {
        super(context);
    }

    public FilterOneAdapter(Context context, List<FilterEntity> list) {
        super(context, list);
    }

    public void setSelectedEntity(FilterEntity filterEntity) {
        this.selectedEntity = filterEntity;
        for (FilterEntity entity : getData()) {
            entity.setSelected(entity.getKey().equals(selectedEntity.getKey()));
        }
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_filter_one, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        FilterEntity entity = getItem(position);

        holder.tvTitle.setText(PayUtils.getOrderState(entity.getKey()));
        
        if (entity.isSelected()) {
            holder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.orange));
        } else {
            holder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.font_black_3));
        }

        return convertView;
    }

    static class ViewHolder {
        @ViewInject(R.id.iv_image)
        ImageView ivImage;
        @ViewInject(R.id.tv_title)
        TextView tvTitle;

        ViewHolder(View view) {
          x.view().inject(this, view);
        }
    }
}
