package com.one.framework.download;

public interface UpdateProgressListener {
  void start();
  void update(int progress);
  void success();
  void error();
}
