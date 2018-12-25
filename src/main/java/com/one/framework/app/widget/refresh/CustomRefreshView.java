package com.one.framework.app.widget.refresh;


import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.one.framework.R;
import com.one.framework.log.Logger;
import com.one.framework.utils.UIUtils;

public class CustomRefreshView extends FrameLayout {
  private static final String TAG = CustomRefreshView.class.getSimpleName();

  private ImageView mAnimView;
  private AnimationDrawable mAnimDrawable;

  public CustomRefreshView(Context context) {
    this(context, null);
  }

  public CustomRefreshView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public CustomRefreshView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    mAnimView = findViewById(R.id.one_custom_view);
    if (mAnimView != null) {
      mAnimDrawable = (AnimationDrawable) mAnimView.getDrawable();
    }
    Logger.e(TAG, "CustomRefreshView height >>> " + getRefreshHeight());
    setTranslationY(-getRefreshHeight());
  }

  public int getRefreshHeight() {
    return UIUtils.getViewHeight(this);
  }

  public void startAnim() {
    if (mAnimDrawable != null && !mAnimDrawable.isRunning()) {
      mAnimDrawable.start();
    }
  }

  public void releaseResources() {
    if (mAnimDrawable != null && mAnimDrawable.isRunning()) {
      mAnimDrawable.stop();
    }
    setTranslationY(-getRefreshHeight());
  }

  /**
   * x 方向平移
   */
  public void stopAnim(AnimationListener listener) {
    if (mAnimView != null) {
      Animation xTranslation = AnimationUtils.loadAnimation(getContext(), R.anim.one_x_trans_anim);
      xTranslation.setDuration(300);
      xTranslation.setInterpolator(new AnticipateInterpolator());
      xTranslation.setAnimationListener(listener);
      mAnimView.startAnimation(xTranslation);
    }
  }
}
