package com.one.framework.download;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.one.framework.R;
import com.one.framework.app.widget.LoadingView;
import com.one.framework.app.widget.ShapeImageView;
import com.one.framework.app.widget.TripButton;
import com.one.framework.log.Logger;
import com.one.framework.utils.UIThreadHandler;
import com.one.framework.utils.UIUtils;

public class UpgradeDialog extends Dialog implements UpdateProgressListener {

  private ShapeImageView mShape;
  private TextView title;
  private TextView content;
  private LinearLayout mOptionsLayout;
  private TripButton cancel;
  private TripButton upgrade;
  private int margin;
  private View.OnClickListener cancelListener;
  private View.OnClickListener upgradeListener;
  private boolean isForceUpgrade;
  private LinearLayout mDownloadProgress;
  private LoadingView mProgress;
  private TextView mCurProgress;

  public UpgradeDialog(@NonNull Context context) {
    this(context, R.style.DialogTheme);
  }

  public UpgradeDialog(@NonNull Context context, int themeResId) {
    super(context, themeResId);
    margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 54,
        context.getResources().getDisplayMetrics());
    init();
  }

  protected void init() {
    //获取当前Activity所在的窗体
    Window dialogWindow = this.getWindow();
    //设置Dialog从窗体底部弹出
    dialogWindow.setGravity(Gravity.CENTER);
    //获得窗体的属性
    WindowManager.LayoutParams lp = dialogWindow.getAttributes();
    dialogWindow.getDecorView().setPadding(0, 0, 0, 0); //消除边距

    lp.width = UIUtils.getScreenWidth(getContext()) - margin * 2;   //设置宽度充满屏幕
    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
    lp.y = 0;//设置Dialog距离底部的距离
//      将属性设置给窗体
    dialogWindow.setAttributes(lp);

    setCanceledOnTouchOutside(true);

    initView();

//    UIThreadHandler.postDelayed(new Runnable() {
//      @Override
//      public void run() {
//        test();
//      }
//    }, 500);
  }

  private void initView() {
    View view = LayoutInflater.from(getContext()).inflate(R.layout.one_upgrade_layout, null);
    mShape = view.findViewById(R.id.oneDownloadShape);
    title = view.findViewById(R.id.one_upgrade_title);
    content = view.findViewById(R.id.one_upgrade_content);
    mCurProgress = view.findViewById(R.id.oneDownloadProgress);
    mProgress = view.findViewById(R.id.one_download_progressing);
    mOptionsLayout = view.findViewById(R.id.oneDownloadOptionsRoot);
    cancel = view.findViewById(R.id.one_upgrade_cancel);
    upgrade = view.findViewById(R.id.one_upgrade_upgrade);
    mDownloadProgress = view.findViewById(R.id.oneDownloadProgressLayout);

    cancel.setOnClickListener(v -> {
      dismiss();
      if (cancelListener != null) {
        cancelListener.onClick(v);
      }
    });

    upgrade.setOnClickListener(v -> {
      if (!isForceUpgrade) {
        dismiss();
      } else {
        // 强制升级

      }
      if (upgradeListener != null) {
        upgradeListener.onClick(v);
      }
    });
    setContentView(view);

    setOnKeyListener(new OnKeyListener() { // 屏蔽返回键
      @Override
      public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
          return true;
        }
        return false;
      }
    });
  }

  /**
   * 是否是强制升级
   * @param forceUpgrade
   */
  public UpgradeDialog forceUpgrade(boolean forceUpgrade) {
    isForceUpgrade = forceUpgrade;
    setCanceledOnTouchOutside(false); // 不能点击任何区域消失
    cancel.setVisibility(forceUpgrade ? View.GONE : View.VISIBLE);
    return this;
  }

  public UpgradeDialog updateUI(String title, String content) {
    this.title.setText(title);
    this.content.setText(content);
    return this;
  }

  public UpgradeDialog cancelListener(View.OnClickListener listener) {
    cancelListener = listener;
    return this;
  }

  public UpgradeDialog upgradeListener(View.OnClickListener listener) {
    upgradeListener = listener;
    return this;
  }

  @Override
  public void start() {
    title.setVisibility(View.GONE);
    content.setVisibility(View.GONE);
    mOptionsLayout.setVisibility(View.GONE);
    mDownloadProgress.setVisibility(View.VISIBLE);

  }

  @Override
  public void update(int progress) {
    float scale = (progress * 1f / 100);
    if (progress >= 5) { // 百分比表示指到小白点
      int curProgress = progress - 5;
      float newScale = (curProgress * 1f / 100);
      mCurProgress.setText(progress + "%");
      mCurProgress.setTranslationX(mProgress.getWidth() * newScale);
    }
    mProgress.setProgress(mProgress.getWidth() * scale);
  }

  @Override
  public void success() {
    dismiss();
  }

  @Override
  public void error() {
  }


//  private byte[] lock = new byte[0];
//  int i = 0;
//  private void test() {
//    new Thread(new Runnable() {
//      @Override
//      public void run() {
//
//        while (i <= 100) {
//          UIThreadHandler.post(new Runnable() {
//            @Override
//            public void run() {
//              update(i);
//            }
//          });
//
//          synchronized (lock) {
//            try {
//              lock.wait(500);
//              i++;
//            } catch (InterruptedException e) {
//              e.printStackTrace();
//            }
//          }
//        }
//      }
//    }).start();
//  }
}
