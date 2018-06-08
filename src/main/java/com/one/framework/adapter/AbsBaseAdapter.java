package com.one.framework.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @param <T> binder data
 * @param <H> ListView Holder
 */
public abstract class AbsBaseAdapter<T, H> extends BaseAdapter {
  protected LayoutInflater mInflater;
  protected List<T> mListData = new ArrayList<>();
  protected Context mContext;
  protected final int ROUND_RADIUS;
  
  public AbsBaseAdapter(Context context) {
    mContext = context;
    mInflater = LayoutInflater.from(mContext);
    ROUND_RADIUS = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,4, mContext.getResources().getDisplayMetrics());
  }
  
  public void setListData(List<T> lists) {
    if (lists != null) {
      mListData.addAll(lists);
    }
    notifyDataSetChanged();
  }
  
  public void refreshData(List<T> lists) {
    if (lists != null) {
      mListData.clear();
      mListData.addAll(lists);
    }
    notifyDataSetChanged();
  }

  public void clear() {
    mListData.clear();
    notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    return mListData.size();
  }
  
  @Override
  public T getItem(int position) {
    return mListData.get(position);
  }
  
  @Override
  public long getItemId(int position) {
    return position;
  }
  
  @Override
  public View getView(int position, View convertView, ViewGroup viewGroup) {
    H holder;
    if (convertView == null) {
      holder = createHolder();
      convertView = createView();
      initView(convertView, holder);
      convertView.setTag(holder);
    } else {
      holder = (H) convertView.getTag();
    }
    bindData(mListData.get(position), holder, position);
    return convertView;
  }
  
  protected abstract H createHolder();
  protected abstract void initView(View view, H holder);
  protected abstract void bindData(T model, H holder, int position);
  protected abstract View createView();
  
}
