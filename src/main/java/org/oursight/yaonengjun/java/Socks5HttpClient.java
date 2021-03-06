package org.oursight.yaonengjun.java;

import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;

import javax.net.ssl.SSLContext;

/**
 * Created by yaonengjun on 2018/1/22 下午5:01.
 */
public class Socks5HttpClient extends DefaultHttpClient {

  private CloseableHttpClient httpclient;


  public Socks5HttpClient() {
//    super();
    Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory>create()
            .register("http", PlainConnectionSocketFactory.INSTANCE)
            .register("https", new MyConnectionSocketFactory(SSLContexts.createSystemDefault()))
            .build();
    PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(reg);
    httpclient = HttpClients.custom()
            .setConnectionManager(cm)
            .build();

  }

  public CloseableHttpResponse execute(
          final HttpUriRequest request

  ) throws IOException, ClientProtocolException {
    return execute(request, (HttpContext) null);
  }

  public CloseableHttpResponse execute(
          final HttpUriRequest request,
          HttpContext context

  ) throws IOException, ClientProtocolException {

    if (context == null) {
      context = HttpClientContext.create();
    }

    HttpHost proxy = (HttpHost) request.getParams().getParameter(ConnRouteParams.DEFAULT_PROXY);

    if (proxy != null && "socks".equalsIgnoreCase(proxy.getSchemeName())) {
      InetSocketAddress localSocksAddress = new InetSocketAddress(proxy.getHostName(), proxy.getPort());
      context.setAttribute("socks.address", localSocksAddress);
      request.getParams().removeParameter(ConnRouteParams.DEFAULT_PROXY);
//      return super.execute(request, context);
    }
    return httpclient.execute(request, context);
  }


  CloseableHttpResponse execute(
          final HttpUriRequest request,
          HttpContext context,
          String socksServerIp,
          int socksPort

  ) throws IOException, ClientProtocolException {

    if (context == null) {
      context = HttpClientContext.create();
    }

    InetSocketAddress localSocksAddress = new InetSocketAddress(socksServerIp, socksPort);
    context.setAttribute("socks.address", localSocksAddress);
    return httpclient.execute(request, context);
  }

  static class MyConnectionSocketFactory extends SSLConnectionSocketFactory {

    public MyConnectionSocketFactory(final SSLContext sslContext) {
      super(sslContext);
    }

    @Override
    public Socket createSocket(final HttpContext context) throws IOException {
      InetSocketAddress socksaddr = (InetSocketAddress) context.getAttribute("socks.address");
//      if(socksaddr == null)
//        return PlainConnectionSocketFactory.INSTANCE.createSocket(context);

      Proxy proxy = new Proxy(Proxy.Type.SOCKS, socksaddr);
      return new Socket(proxy);
    }

  }
}
