package com.one.framework.app.ads;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import com.one.framework.R;
import com.one.framework.app.widget.GiftView;
import com.one.framework.app.widget.ShapeImageView;
import com.one.framework.utils.UIUtils;
import java.lang.ref.WeakReference;
import java.util.List;


public class AdsPopup implements IAdsPopup, ShapeImageView.IClickListener {

  private PopupWindow mPop;
  private RelativeLayout mParent;
  private ImageView mClose;
  private LinearLayout mShareLayout;
  private ShapeImageView mDisplayImg;
//  private ShareListener mListener;
  private WeakReference<Activity> mWeak;
  private LinearLayout mShareParentLayout;
//  private GiftView mGiftView;
  private int mScreenHeight, mScreenWidth;
  private RelativeLayout mContent;
  private int mCloseY;

  public AdsPopup(Activity context, OnDismissListener onDismissListener) {
    mWeak = new WeakReference<>(context);
    Activity a = mWeak.get();
    if (a != null) {
      mScreenHeight = a.getResources().getDisplayMetrics().heightPixels;
      mScreenWidth = a.getResources().getDisplayMetrics().widthPixels;

      View contentView = LayoutInflater.from(context)
          .inflate(R.layout.ads_popup_window_layout, null);
      mPop = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.MATCH_PARENT);
      mPop.setOutsideTouchable(false);
      mPop.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#50000000")));
      mPop.update(0, 0, mScreenWidth, mScreenHeight);
      mPop.setOnDismissListener(onDismissListener);
//      mListener = new ShareListener();
      initView(contentView);
      int y = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 130, a.getResources().getDisplayMetrics());
      mCloseY = mScreenHeight - y;
    }
  }

  private void initView(View view) {
    mParent = (RelativeLayout) view.findViewById(R.id.ads_popup_layout);
    mContent = (RelativeLayout) view.findViewById(R.id.pop_content);
    mDisplayImg = (ShapeImageView) view.findViewById(R.id.ads_pop_display);
    mClose = (ImageView) view.findViewById(R.id.pop_close);
    mShareLayout = (LinearLayout) view.findViewById(R.id.ads_pop_share_channel);
    mShareParentLayout = (LinearLayout) view.findViewById(R.id.ad_share_parent_layout);
    mClose.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        playAnim(false);
      }
    });
    mDisplayImg.setListener(this);
    Activity a = mWeak.get();
    if (a != null) {
      mClose.setBackgroundDrawable(UIUtils.rippleDrawableRounded(Color.TRANSPARENT, Color.LTGRAY, UIUtils.dip2pxInt(a, 36f)));
    }

  }

  @Override
  public void show(AdsBean model) {
    mDisplayImg.loadImageByUrl(mParent, model.getImageUrl(), model.getRedirectUrl());
      RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
      if (model.getType() != 2) {
        mShareParentLayout.setVisibility(View.GONE);
      } else {
        mShareParentLayout.setVisibility(View.VISIBLE);
        params.addRule(RelativeLayout.ABOVE, R.id.ad_share_parent_layout);
        mDisplayImg.setSpecialRadius(false);
      }
      mDisplayImg.setLayoutParams(params);
    if (mPop != null && !mPop.isShowing()) {
      mPop.showAtLocation(mParent, Gravity.BOTTOM, 0, 0);
      playAnim(true);
    }
  }

  @Override
  public void addSharePlat(List<FrameLayout> views) {
    if (views != null && views.size() > 0) {
      for (FrameLayout view : views) {
        mShareLayout.addView(view);
      }
    }
  }

  @Override
  public void dismiss() {
    if (mPop != null && mPop.isShowing()) {
      mPop.dismiss();
    }
  }


//  @Override
//  public ShareView.OnShareListener listener() {
//    return mListener;
//  }

//  protected void performShare(SHARE_MEDIA platform) {
//    Activity a = mWeak.get();
//    if (a == null) {
//      return;
//    }
//    if (!ShareUtils.isInstallApp(a, platform)) {
//      ToastsKt.toast(R.string.app_not_install);
//      return;
//    }
//    if (mModel == null || mModel.getShareAds() == null || mModel.getShareInfo() == null) {
//      return;
//    }
//
//    AdsBean bean = mModel.getShareAds();
//    ShareInfo shareInfo = mModel.getShareInfo();
//    ShareAction action = new ShareAction(a);
//    UMImage thumb = new UMImage(a, shareInfo.getShareIcon());//网络图片
//    UMWeb web = new UMWeb(bean.getRedirectUrl());
//    web.setTitle(platform == SHARE_MEDIA.WEIXIN ? shareInfo.getShareTitle()
//        : shareInfo.getShareMomentsContent());
//    web.setThumb(thumb);
//    web.setDescription(
//        platform == SHARE_MEDIA.WEIXIN ? shareInfo.getShareChatContent()
//            : shareInfo.getShareMomentsContent());
//    action.setPlatform(platform).setCallback(listener)
//        .withMedia(web)
//        .share();
//
//  }


  @Override
  public void onClick(String schema) {

  }

  @Override
  public void giftView(GiftView view) {
//    mGiftView = view;
//    if (mGiftView != null) {
//      mGiftView.setIPopShow(this);
//    }
  }

  private void playAnim(boolean isReverse, long duration) {
    float from = !isReverse ? 0f : 1f;
    float to = !isReverse ? 1f : 0f;
    AnimatorSet set = new AnimatorSet();
    ValueAnimator anim = createAnim("translationY", from, to);
    ValueAnimator alpha = createAnim("alpha", from, to);
//    ValueAnimator translation = createAnim("translation", from, to);
//    ValueAnimator scale = createAnim("scale", from, to);

    anim.setInterpolator(new LinearInterpolator());
    anim.addUpdateListener(new AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {

        float transX = (float) animation.getAnimatedValue();
        mPop.getContentView().setTranslationY(mContent.getY() * transX);
//        float value = (float) animation.getAnimatedValue();
//        float y = (mScreenHeight - mCloseY) * value;
//
//        mPop.getContentView().setTranslationY(value);
//        mClose.setTranslationY(y);
      }
    });

    alpha.addUpdateListener(new AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        float alpha1 = (float) animation.getAnimatedValue();
//        if (mGiftView != null) {
//          mGiftView.setAlpha(alpha1);
//        }
        mContent.setAlpha(1f - alpha1);
      }
    });

//    translation.setInterpolator(new AccelerateInterpolator());
//    translation.addUpdateListener(new AnimatorUpdateListener() {
//      @Override
//      public void onAnimationUpdate(ValueAnimator animation) {
//        float transX = (float) animation.getAnimatedValue();
//        float centerX = mScreenWidth - mGiftView.getViewWidth();
//        float centerY = mGiftView.getY();
//        mPop.getContentView()
//            .setTranslationX((centerX - mContent.getX() + mGiftView.getViewWidth() / 2) * transX);
//        mPop.getContentView()
//            .setTranslationY((centerY - mContent.getY() - mGiftView.getViewWidth() / 2) * transX);
//      }
//    });

//    scale.setInterpolator(new AccelerateInterpolator());
//    scale.addUpdateListener(new AnimatorUpdateListener() {
//      @Override
//      public void onAnimationUpdate(ValueAnimator animation) {
//        float scale1 = (float) animation.getAnimatedValue();
//        mPop.getContentView().setScaleX(1f - scale1);
//        mPop.getContentView().setScaleY(1f - scale1);
//      }
//    });

    set.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationStart(Animator animation) {
        super.onAnimationStart(animation);
        if (!isReverse) {
//          mGiftView.setVisibility(View.VISIBLE);
//          mGiftView.setTimeOut(0);
        }
      }

      @Override
      public void onAnimationEnd(Animator animation) {
        super.onAnimationEnd(animation);
        if (isReverse) {
//          mGiftView.setVisibility(View.INVISIBLE);
        } else {
          dismiss();
        }
      }
    });
    set.playTogether(anim, alpha/*, scale, translation*/);
    set.setDuration(duration);
    set.start();
  }

  private void playAnim(final boolean isReverse) {
//    if (mGiftView != null) {
      playAnim(isReverse, 300);
//    } else {
//      dismiss();
//    }
  }

  private ValueAnimator createAnim(String property, float... values) {
    PropertyValuesHolder holder = PropertyValuesHolder.ofFloat(property, values);
    ValueAnimator anim = ValueAnimator.ofPropertyValuesHolder(holder);
    return anim;
  }

  @Override
  public void onPopShow(boolean isReverse) {
    playAnim(true);
  }

  @Override
  public boolean isShowing() {
    return (mPop != null && mPop.isShowing());
  }

  @Override
  public boolean giftShowing() {
    return false;//mGiftView != null && mGiftView.getVisibility() == View.VISIBLE;
  }

  @Override
  public void release() {
    if (mPop != null) {
      mPop = null;
    }
  }
}
