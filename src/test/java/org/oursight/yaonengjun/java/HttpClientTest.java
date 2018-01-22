package org.oursight.yaonengjun.java;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.IOException;

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
      HttpHost target = new HttpHost("localhost", 443, "https");
      HttpHost proxy = new HttpHost("127.0.0.1", 8080, "http");

      RequestConfig config = RequestConfig.custom()
              .setProxy(proxy)
              .build();
      HttpGet request = new HttpGet("/");
      request.setConfig(config);

      System.out.println("Executing request " + request.getRequestLine() + " to " + target + " via " + proxy);

      CloseableHttpResponse response = httpclient.execute(target, request);
      try {
        System.out.println("----------------------------------------");
        System.out.println(response.getStatusLine());
        EntityUtils.consume(response.getEntity());
      } finally {
        response.close();
      }
    } finally {
      httpclient.close();
    }

  }
}