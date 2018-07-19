package com.one.framework.app.navigation.impl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.BackStackEntry;
import android.support.v4.app.FragmentTransaction;
import com.one.framework.MainActivity;
import com.one.framework.R;
import com.one.framework.app.model.IBusinessContext;
import com.one.framework.app.navigation.INavigator;
import com.one.framework.log.Logger;
import com.one.framework.manager.FragmentDelegateManager;
import com.onecore.core.anim.FragmentAnimator;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by ludexiang on 2018/3/28.
 * 加载Fragment 动画逻辑
 * 界面路由导航
 */

public final class Navigator implements INavigator {

  private SoftReference<Context> mSoftReference;
  private FragmentDelegateManager mPageManager;
  private FragmentManager mFragmentManager;
  private PauseHandler mPauseHandler;

  public Navigator(Context context, FragmentManager manager) {
    mSoftReference = new SoftReference<Context>(context);
    mFragmentManager = manager;
    mPageManager = FragmentDelegateManager.getInstance();
    mPauseHandler = new PauseHandler(Looper.getMainLooper(), mFragmentManager);
  }

  @Override
  public void onResume() {
    mPauseHandler.resume();
  }

  @Override
  public void onPause() {
    mPauseHandler.pause();
  }

  private Fragment getFragment(Intent intent, IBusinessContext businessContext) {
    return mPageManager.getFragment(intent, businessContext);
  }

  @Override
  public Fragment startFragment(Intent intent, IBusinessContext businessContext, FragmentAnimator animator) {
    final Fragment fragment = getFragment(intent, businessContext);
    if (fragment != null) {
      final FragmentTransaction transaction = mFragmentManager.beginTransaction();
      if (animator != null) {
        transaction.setCustomAnimations(animator.getEnter(), animator.getExit(), animator.getPopEnter(), animator.getPopExit());
      }
      String fragmentTag = getFragmentTag(fragment.getClass());
      boolean isAddToBackStack = intent.getBooleanExtra(BUNDLE_ADD_TO_BACK_STACK, true);
      if (isAddToBackStack) {
        transaction.addToBackStack(fragmentTag);
      }
      boolean isAdd = intent.getBooleanExtra(BUNDLE_FORWARD_FRAGMENT_STYLE, false);
      if (isAdd) {
        transaction.add(R.id.content_view_container, fragment, fragmentTag);
      } else {
        transaction.replace(R.id.content_view_container, fragment, fragmentTag);
      }
      Bundle bundle = intent.getExtras();
      if (bundle != null) {
        fragment.setArguments(bundle);
      }
      safePost(new Runnable() {
        @Override
        public void run() {
          transaction.commitAllowingStateLoss();
          mFragmentManager.executePendingTransactions();
          fragment.setUserVisibleHint(true);
        }
      });
    } else {
      // 跳转Activity
      if (mSoftReference != null && mSoftReference.get() != null) {
        if (!(mSoftReference.get() instanceof Activity)) {
          intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        mSoftReference.get().startActivity(intent);
      }
    }
    return fragment;
  }

  /**
   * 若为0表示在首页
   */
  @Override
  public Fragment getCurrentFragment() {
    int backStackCount = mFragmentManager.getBackStackEntryCount();
    if (backStackCount != 0) { // 返回栈中存在Fragment
      BackStackEntry stackEntry = mFragmentManager.getBackStackEntryAt(backStackCount - 1);
      String fragmentTag = stackEntry.getName();
      return mFragmentManager.findFragmentByTag(fragmentTag);
    }
    return null;
  }

  @Override
  public Fragment getPreFragment(Fragment fragment) {
    return null;
  }

  private String getFragmentTag(Class<? extends Fragment> fragment) {
    String canonicalName = fragment.getCanonicalName();
    return canonicalName + "@" + System.identityHashCode(fragment);
  }

  @Override
  public void lockDrawerLayout(boolean lock) {
    if (mSoftReference.get() != null && mSoftReference.get() instanceof MainActivity) {
      ((MainActivity) mSoftReference.get()).lockDrawerLayout(lock);
    }
  }

  @Override
  public void backToRoot() {
    safePost(new Runnable() {
      @Override
      public void run() {
        int entryCount = mFragmentManager.getBackStackEntryCount();
        if (entryCount > 0) {
          final String tag = mFragmentManager.getBackStackEntryAt(0).getName();
          mFragmentManager.popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
      }
    });
  }

  /**
   * 安全的处理 Fragment的状态变更操作
   * 使用Handler Message的方式 避免stateLoss问题
   *
   * @param runnable
   */
  @Override
  public void safePost(Runnable runnable) {
    // 这里如果给message设置了callback 则handler就不会调用handleMessage方法
    // 而会直接调用message的callback去处理了
    Message transitionMsg = Message.obtain(mPauseHandler, 0, runnable);
    mPauseHandler.sendMessage(transitionMsg);
  }

  /**
   * Message Handler class that supports buffering up of messages when the
   * activity is isPaused i.e. in the background.
   */
  public static class PauseHandler extends Handler {

    /**
     * Message Queue Buffer
     */
    private final Vector<Message> mBuffer = new Vector<Message>();

    /**
     * Flag indicating the pause state
     */
    private volatile boolean isPaused;

    private FragmentManager manager;

    /**
     * Use the provided {@link Looper} instead of the default one.
     *
     * @param looper The looper, must not be null.
     */
    public PauseHandler(Looper looper, FragmentManager manager) {
      super(looper);
      this.manager = manager;
    }

    /**
     * Resume the handler
     */
    final void resume() {
      Logger.i("ldx", "resume and consume");

      isPaused = false;
      //处理 MainActivity#onResume ----> MainActivity#onStop 空档期 isPaused置为true
      while ((mBuffer.size() > 0) && !isPaused) {
        final List<Message> messageList = new ArrayList<>(mBuffer);
        mBuffer.clear();

        post(new Runnable() {
          @Override
          public void run() {
            if (!isPaused){
              for (Message message : messageList) {
                if (!isPaused) {
                  processMessage(message);
                  message.recycle();
                }else {
                  mBuffer.add(message);
                }
              }
              manager.executePendingTransactions();
            }else {
              mBuffer.addAll(messageList);
            }
          }
        });
      }
    }

    /**
     * Pause the handler
     */
    final void pause() {
      Logger.e("ldx", "pause");
      isPaused = true;
    }

    public boolean isPaused() {
      return isPaused;
    }

    /**
     * Notification message to be processed. This will either be directly from
     * handleMessage or played back from a saved message when the activity was
     * isPaused.
     *
     * @param message the message to be handled
     */
    protected void processMessage(Message message) {
      Logger.i("ldx", "processMessage");

      // 这里如果给message设置了callback 则handler就不会调用handleMessage方法
      // 而会直接调用message的callback去处理了
      Runnable runnable = (Runnable) message.obj;
      runnable.run();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void handleMessage(Message msg) {
      if (isPaused) {
        Message msgCopy = Message.obtain(msg);
        mBuffer.add(msgCopy);
      } else {
        processMessage(msg);
      }
    }
  }
}
