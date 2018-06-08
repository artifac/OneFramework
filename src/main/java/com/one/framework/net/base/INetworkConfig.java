package com.one.framework.net.base;

import com.one.framework.net.NetInterceptor;
import java.util.List;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Created by ludexiang on 2018/6/8.
 */

public interface INetworkConfig {

  SSLSocketFactory getSslSocketFactory();

  X509TrustManager getTrustManager();

  HostnameVerifier getHostnameVerifier();

  List<NetInterceptor> getInterceptors();
}
