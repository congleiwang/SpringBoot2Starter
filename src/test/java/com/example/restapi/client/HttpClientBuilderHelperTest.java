package com.example.restapi.client;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Assert;
import org.junit.Test;

import javax.net.ssl.SSLContext;

import static org.junit.Assert.*;

public class HttpClientBuilderHelperTest {

    @Test
    public void withProxy() {
        Proxy proxy = new Proxy();
        proxy.setHostname("localhost");
        proxy.setPort(8888);
        proxy.setPassword("password");
        proxy.setUserName("user");
        HttpClientBuilderHelper httpClientBuilderHelper = new HttpClientBuilderHelper();
        CloseableHttpClient secureClientWithProxy = httpClientBuilderHelper.createSecureClientWithProxy();

        Assert.assertNotNull(secureClientWithProxy);

    }

    @Test
    public void createSecureClient() {
        HttpClientBuilderHelper httpClientBuilderHelper = new HttpClientBuilderHelper();
        HttpClient secureClient = httpClientBuilderHelper.createSecureClient();
        Assert.assertNotNull(secureClient);
    }

    @Test
    public void createSecureClientSimple() {
        HttpClientBuilderHelper httpClientBuilderHelper = new HttpClientBuilderHelper();
        CloseableHttpClient secureClient = httpClientBuilderHelper.createSecureClientSimple();
        Assert.assertNotNull(secureClient);
    }

    @Test
    public void createSecureClientWithProxy() {
        HttpClientBuilderHelper httpClientBuilderHelper = new HttpClientBuilderHelper();
        CloseableHttpClient secureClient = httpClientBuilderHelper.createSecureClientWithProxy();
        Assert.assertNotNull(secureClient);
    }

    @Test
    public void createSslContextTrusty() {
        HttpClientBuilderHelper httpClientBuilderHelper = new HttpClientBuilderHelper();
        SSLContext sslContextTrusty = httpClientBuilderHelper.createSslContextTrusty();
        Assert.assertNotNull(sslContextTrusty);
    }

    @Test
    public void createSslContextSimple() {
        HttpClientBuilderHelper httpClientBuilderHelper = new HttpClientBuilderHelper();
        SSLContext sslContextSimple = httpClientBuilderHelper.createSslContextSimple();
        Assert.assertNotNull(sslContextSimple);

    }

    @Test
    public void createSslContextWithKeyStore() {
    }

    @Test
    public void buildRequestConnfig() {
        HttpClientBuilderHelper httpClientBuilderHelper = new HttpClientBuilderHelper();
    }
}