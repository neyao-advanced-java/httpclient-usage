package org.oursight.yaonengjun.java;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;

import javax.net.ssl.SSLContext;

/**
 * Created by yaonengjun on 2018/1/18 下午4:10.
 */
public class HttpClientTest {

  @Test
  public void testGet() throws IOException {
    CloseableHttpClient httpclient = HttpClients.createDefault();
    try {
      String url = "http://httpbin.org/ip";
      RequestConfig config = RequestConfig.custom().build();

      HttpGet httpGet = new HttpGet(url);
      httpGet.setConfig(config);
//      httpGet.set

      System.out.println("Executing request " + httpGet.getRequestLine() + " to " + url);

      // Create a custom response handler
      ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

        public String handleResponse(
                final HttpResponse response) throws ClientProtocolException, IOException {
          int status = response.getStatusLine().getStatusCode();
          System.out.println("status: " + status);
          if (status >= 200 && status < 300) {
            HttpEntity entity = response.getEntity();
            return entity != null ? EntityUtils.toString(entity) : null;
          } else {
            throw new ClientProtocolException("Unexpected response status: " + status);
          }
        }

      };


      try {
          String response = httpclient.execute(httpGet, responseHandler);
        System.out.println(response);
//        System.out.println("----------------------------------------");
//        System.out.println(response.getStatusLine());
//        EntityUtils.consume(response.getEntity());
      } finally {
//        response.close();
      }
    } finally {
      httpclient.close();
    }

  }

  @Test
  public void testPost() throws IOException {
    CloseableHttpClient httpclient = HttpClients.createDefault();
    try {
//      HttpHost target = new HttpHost("localhost", 443, "https");
//      HttpHost proxy = new HttpHost("127.0.0.1", 8080, "http");

//      RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
      RequestConfig config = RequestConfig.custom().build();
      HttpPost httpPost = new HttpPost("http://httpbin.org/post");
      httpPost.setConfig(config);

//      System.out.println("Executing request " + request.getRequestLine() + " to " + target + " via " + proxy);

      CloseableHttpResponse response = httpclient.execute(httpPost);
      try {
        System.out.println("----------------------------------------");
        System.out.println(response.getStatusLine());
        System.out.println(EntityUtils.toString(response.getEntity()));
      } finally {
        response.close();
      }
    } finally {
      httpclient.close();
    }

  }


  @Test
  public void testPostWithProxy() throws IOException {
    CloseableHttpClient httpclient = HttpClients.createDefault();
    try {
      HttpHost proxy = new HttpHost("157.185.128.251", 61000, "http");

      RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
//      RequestConfig config = RequestConfig.custom().build();
      HttpPost httpPost = new HttpPost("http://httpbin.org/post");
      httpPost.setConfig(config);

//      System.out.println("Executing request " + request.getRequestLine() + " to " + target + " via " + proxy);

      CloseableHttpResponse response = httpclient.execute(httpPost);
      try {
        System.out.println("----------------------------------------");
        System.out.println(response.getStatusLine());
        System.out.println(EntityUtils.toString(response.getEntity()));
      } finally {
        response.close();
      }
    } finally {
      httpclient.close();
    }

  }

  @Test
  public void useSimpleHttpProxyViaSocksHttpClient() throws IOException {
//    HttpHost host = new HttpHost("127.0.0.1", 10086, "socks");
//    "socks"host.getSchemeName();

    HttpHost proxy = new HttpHost("157.185.128.251", 61000, "http");

    String url = "http://httpbin.org/ip";
    RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
    HttpGet httpGet = new HttpGet(url);
    httpGet.setConfig(config);

    Socks5HttpClient socks5HttpClient = new Socks5HttpClient();
//    CloseableHttpResponse  response = httpClient.execute(httpGet, null, "127.0.0.1", 1086);
    CloseableHttpResponse response = socks5HttpClient.execute(httpGet);

    try {
      System.out.println("----------------------------------------");
      System.out.println(response.getStatusLine());
      System.out.println( EntityUtils.toString(response.getEntity()) );
    } finally {
      response.close();
    }

  }

  @Test
  public void testUseSocks5AsProxy() throws IOException {
//    HttpHost host = new HttpHost("127.0.0.1", 10086, "socks");
//    "socks"host.getSchemeName();

    String url = "https://httpbin.org/ip";
    RequestConfig config = RequestConfig.custom().build();
    HttpGet httpGet = new HttpGet(url);
    httpGet.setConfig(config);

      Socks5HttpClient httpClient = new Socks5HttpClient();
      CloseableHttpResponse  response = httpClient.execute(httpGet, null, "127.0.0.1", 1086);


    try {
      System.out.println("----------------------------------------");
      System.out.println(response.getStatusLine());
      System.out.println( EntityUtils.toString(response.getEntity()) );
    } finally {
      response.close();
    }

  }


  @Test
  public void testUseSocks5AsProxy2() throws IOException {
    HttpHost proxy = new HttpHost("127.0.0.1", 1086, "socks");
//    "socks"host.getSchemeName();

    String url = "https://httpbin.org/ip";
//    RequestConfig config = RequestConfig.custom().build();
    RequestConfig config = RequestConfig.custom().build();
    HttpGet httpGet = new HttpGet(url);
    httpGet.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY, proxy);
    httpGet.setConfig(config);

    Socks5HttpClient httpClient = new Socks5HttpClient();
    CloseableHttpResponse  response = httpClient.execute(httpGet, (HttpContext) null);
//    CloseableHttpResponse  response = httpClient.execute(httpGet, null, "127.0.0.1", 1086);


    try {
      System.out.println("----------------------------------------");
      System.out.println(response.getStatusLine());
      System.out.println( EntityUtils.toString(response.getEntity()) );
    } finally {
      response.close();
    }

  }

}


