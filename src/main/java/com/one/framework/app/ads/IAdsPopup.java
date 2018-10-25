package com.one.framework.app.ads;

import android.widget.FrameLayout;
import com.one.framework.app.widget.GiftView;
import java.util.List;

/**
 * Created by ludexiang on 2017/12/14.
 */

public interface IAdsPopup extends GiftView.IPopShowListener {
  void show(AdsBean model);
  void dismiss();
  void addSharePlat(List<FrameLayout> views);
//  ShareView.OnShareListener listener();
  void giftView(GiftView view);
  boolean isShowing();
  boolean giftShowing();
  void release();
}
