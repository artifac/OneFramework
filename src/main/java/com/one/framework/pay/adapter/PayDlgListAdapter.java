package com.one.framework.pay.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.one.framework.R;
import com.one.framework.adapter.AbsBaseAdapter;
import com.one.framework.pay.dialog.PayBottomDlg.IPayCallback;
import com.one.pay.model.PayList;

/**
 * Created by ludexiang on 2018/6/21.
 */

public class PayDlgListAdapter extends AbsBaseAdapter<PayList, PayDlgListAdapter.PayListHolder> {

  private IPayCallback mListener;

  public PayDlgListAdapter(Context context) {
    super(context);
  }

  @Override
  protected PayListHolder createHolder() {
    return new PayListHolder();
  }

  public void setListener(IPayCallback listener) {
    mListener = listener;
  }

  @Override
  protected void initView(View view, PayListHolder holder) {
    holder.payLayout = (RelativeLayout) view.findViewById(R.id.pay_list_switch_layout);
    holder.payImage = (ImageView) view.findViewById(R.id.pay_icon);
    holder.payTitle = (TextView) view.findViewById(R.id.pay_title);
    holder.payRadio = (ImageView) view.findViewById(R.id.pay_radio);
    holder.payProgress = (ImageView) view.findViewById(R.id.pay_progress);
  }

  @Override
  protected void bindData(final PayList model, final PayListHolder holder, final int position) {
    holder.payRadio.setVisibility(View.VISIBLE);
    holder.payProgress.setVisibility(View.GONE);
    holder.payProgress.clearAnimation();
    holder.payImage.setImageResource(model.getPayItemIconRes());
    holder.payTitle.setText(model.getPayItemTitle());
    holder.payRadio.setImageResource(
        model.getPayItemSelected() ? R.drawable.pay_list_item_radio_select
            : R.drawable.pay_list_item_radio);
    holder.payLayout.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (model.getPayItemSelected()) {
          return;
        }
        holder.payRadio.setVisibility(View.INVISIBLE);
        holder.payProgress.setVisibility(View.VISIBLE);
        Animation rotate = AnimationUtils.loadAnimation(mContext, R.anim.pay_list_progress_anim);
        rotate.setInterpolator(new LinearInterpolator());
        holder.payProgress.startAnimation(rotate);
        if (mListener != null) {
          mListener.onPayListSelect(position);
        }
      }
    });
  }

  public void updatePayList(int position) {
    for (int i = 0; i < mListData.size(); i++) {
      if (i == position) {
        mListData.get(i).setSelected(true);
      } else {
        mListData.get(i).setSelected(false);
      }
    }
    notifyDataSetChanged();
  }

  @Override
  protected View createView() {
    return mInflater.inflate(R.layout.pay_dlg_pay_type_list_layout, null);
  }

  class PayListHolder {

    RelativeLayout payLayout;
    ImageView payImage;
    TextView payTitle;
    ImageView payRadio;
    ImageView payProgress;
  }
}
