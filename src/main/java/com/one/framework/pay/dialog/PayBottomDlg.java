package com.one.framework.pay.dialog;

import android.app.Activity;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.alipay.sdk.app.EnvUtils;
import com.alipay.sdk.app.EnvUtils.EnvEnum;
import com.one.framework.R;
import com.one.framework.app.widget.TripButton;
import com.one.framework.dialog.BottomSheetDialog;
import com.one.framework.pay.IPay;
import com.one.framework.pay.adapter.PayDlgListAdapter;
import com.one.framework.pay.wx.WxPay;
import com.one.framework.pay.zfb.AliPay;
import com.one.framework.utils.UIThreadHandler;
import com.one.pay.model.PayInfo;
import com.one.pay.model.PayList;
import com.one.pay.model.PayModel;

/**
 * Created by ludexiang on 2018/6/21.
 */

public class PayBottomDlg extends BottomSheetDialog implements OnClickListener, IPay {

  private ImageView mClose;
  private TextView mTotalFee;
  private LinearLayout mFeeDetail;
  private TextView mPayVoucher;
  private LinearLayout mVoucherLayout;
  private ListView mPayTypeList;
  private TripButton mPay;
  private PayModel mModel;
  private PayDlgListAdapter mAdapter;
  private int mPayPosition;
  private IPayCallback mPayListener;
  private HandlerThread mPayThread;
  private Handler mPayHandler;
  private Activity mActivity;
  private PayInfo mPayInfo;

  public PayBottomDlg(@NonNull final Activity activity, IPayCallback listener, PayModel model) {
    super(activity);
    mActivity = activity;

    mModel = model;
    mPayListener = listener;
    initView(activity, listener);
    setCanceledOnTouchOutside(false);
    mPayThread = new HandlerThread("PAY_THREAD");
    mPayThread.start();
    mPayHandler = new Handler(mPayThread.getLooper()) {
      @Override
      public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
          case PAY_WX: {
            WxPay wxPay = new WxPay(activity);
            wxPay.wxPay(mPayInfo);
            break;
          }
          case PAY_ZFB: {
            AliPay aliPay = new AliPay(mActivity);
            aliPay.aliPay(PayBottomDlg.this, mPayListener);
            break;
          }
          case PAY_ZSBANK: {
            break;
          }
        }
      }
    };
  }

  public PayBottomDlg(@NonNull final Activity activity, IPayCallback listener, final PayInfo info) {
    super(activity);
    mActivity = activity;

    mPayListener = listener;
    initView(activity, listener);
    setCanceledOnTouchOutside(false);
    mPayThread = new HandlerThread("PAY_THREAD");
    mPayThread.start();
    mPayHandler = new Handler(mPayThread.getLooper()) {
      @Override
      public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
          case PAY_WX: {
            WxPay wxPay = new WxPay(activity);
            wxPay.wxPay(info);
            break;
          }
          case PAY_ZFB: {
            AliPay aliPay = new AliPay(mActivity);
            aliPay.aliPay(PayBottomDlg.this, mPayListener);
            break;
          }
          case PAY_ZSBANK: {
            break;
          }
        }
      }
    };
  }

  private void initView(Activity activity, IPayCallback listener) {
    mAdapter = new PayDlgListAdapter(activity);
    View view = LayoutInflater.from(activity).inflate(R.layout.pay_dlg_layout, null);
    mClose = (ImageView) view.findViewById(R.id.pay_dlg_close);
    mTotalFee = (TextView) view.findViewById(R.id.pay_trip_fee);
    mFeeDetail = (LinearLayout) view.findViewById(R.id.pay_fee_detail_layout);
    mPayVoucher = (TextView) view.findViewById(R.id.pay_voucher);
    mVoucherLayout = (LinearLayout) view.findViewById(R.id.pay_voucher_choose);
    mPayTypeList = (ListView) view.findViewById(R.id.pay_type_list);
    mPay = (TripButton) view.findViewById(R.id.pay);

    if (mModel != null) {
      mAdapter.setListData(mModel.getPayList());
      mAdapter.setListener(listener);

      mTotalFee.setText(String.valueOf(mModel.getTotalFee()));
      mPay.setTripButtonText(String.format(getContext().getString(R.string.pay_dlg_pay_fee_confirm), String.valueOf(mModel.getTotalFee())));
    }
    mPayTypeList.setAdapter(mAdapter);
    mClose.setOnClickListener(this);
    mFeeDetail.setOnClickListener(this);
    mVoucherLayout.setOnClickListener(this);
    mPay.setOnClickListener(this);
    setContentView(view);


  }

  public void updatePayList(int position) {
    mPayPosition = position;
    mAdapter.updatePayList(position);
  }

  @Override
  public void onClick(View v) {
    int id = v.getId();
    if (id == R.id.pay_fee_detail_layout) {
      // h5
    } else if (id == R.id.pay_dlg_close) {
      dismiss();
      UIThreadHandler.post(new Runnable() {
        @Override
        public void run() {
          mPayListener.onPayFail();
        }
      });
    } else if (id == R.id.pay_voucher_choose) {
      // voucher choose list
    } else if (id == R.id.pay) {
      PayList payType = mModel.getPayList().get(mPayPosition);
      switch (payType.getPayItemType()) {
        case PAY_WX: {
          onWxPay();
          break;
        }
        case PAY_ZFB: {
          onAliPay();
          break;
        }
        case PAY_ZSBANK: {
          onZSBankPay();
          break;
        }
      }
    }
  }

  @Override
  public void onWxPay() {
    mPayHandler.sendEmptyMessage(PAY_WX);
  }

  @Override
  public void onAliPay() {
    EnvUtils.setEnv(EnvEnum.SANDBOX); // 测试环境
    mPayHandler.sendEmptyMessage(PAY_ZFB);
  }

  public void onZSBankPay() {

  }

  public interface IPayCallback {
    void onPayListSelect(int position);

    void onPaySuccess();

    void onPayFail();
  }
}
