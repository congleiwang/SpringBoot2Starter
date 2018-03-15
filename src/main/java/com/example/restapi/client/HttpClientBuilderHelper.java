package com.example.restapi.client;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.util.ResourceUtils;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

/**
 * Class: HttpClientBuilderHelper.java - help create HttpClients
 *
 * @author: Springzen
 * @since: Feb 8, 2016
 * @updated: Mar 14, 2018
 * @version: 1.0
 * <p>
 * Copyright (c) 2015-2018 Springzen - Governed by MIT License
 */
public class HttpClientBuilderHelper {

    private CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    private Proxy proxy;


    public HttpClientBuilderHelper withProxy(Proxy proxy) {

        this.proxy = proxy;

        return this;
    }

    public HttpClient createSecureClient() {
        try {
            if (this.proxy != null) {
                return createSecureClientWithProxy();
            } else {
                return createSecureClientSimple();
            }
        } catch (Exception e) {
            return HttpClientBuilder.create().setDefaultRequestConfig(buildRequestConnfig()).build();
        }
    }

    public CloseableHttpClient createSecureClientSimple() {
        CloseableHttpClient client = HttpClients.custom()
                .setSSLContext(createSslContextSimple())
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .build();

        return client;
    }

    public CloseableHttpClient createSecureClientWithProxy() {

        if (proxy != null) {
            if (proxy.hasCredentials()) {
                credentialsProvider.setCredentials(new AuthScope(proxy.getHostname(), proxy.resolvePort()),
                        new UsernamePasswordCredentials(proxy.getHostname(), proxy.getPassword()));
            }
        }

        CloseableHttpClient client = HttpClients.custom()
                .setSSLContext(createSslContextSimple())
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .build();

        return client;
    }

    public SSLContext createSslContextTrusty() {
        SSLContext sslContext = null;
        try {
            sslContext = new SSLContextBuilder()
                    .loadTrustMaterial(null, new TrustAllStrategy()).build();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException | KeyStoreException e) {
            // log this
        } catch (Exception e) {
            // log this
        }

        return sslContext;
    }

    public SSLContext createSslContextSimple() {
        SSLContext sslContext = null;
        try {
            sslContext = new SSLContextBuilder()
                    .loadTrustMaterial(null, (certificate, authType) -> true).build();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException | KeyStoreException e) {
            // log this
        } catch (Exception e) {
            // log this
        }

        return sslContext;
    }

    /**
     * @param keystorePassword - password required to unlock keystore
     * @param pfxFile          - Personal Information Exchange Format file -> client.pfx"
     * @param truststoreFile   - the actual keystore - PKCS12 format -> truststore.jks
     * @return
     */
    public SSLContext createSslContextWithKeyStore(String keystorePassword, String pfxFile, String truststoreFile) {

        char[] password = Optional.ofNullable(keystorePassword).orElse("").toCharArray();
        SSLContext sslContext = null;
        try {
            sslContext = SSLContextBuilder
                    .create()
                    .loadKeyMaterial(loadPfxFile("classpath:" + pfxFile, password), password)
                    .loadTrustMaterial(ResourceUtils.getFile("classpath:" + truststoreFile), password)
                    .build();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException | KeyStoreException e) {
            // log this
        } catch (Exception e) {
            // log this
        }

        return sslContext;
    }

    /**
     * Original method @ https://github.com/steve-oakey/spring-boot-sample-clientauth
     * @param file
     * @param password
     * @return
     * @throws Exception
     */
    private KeyStore loadPfxFile(String file, char[] password) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        File key = ResourceUtils.getFile(file);
        try (InputStream in = new FileInputStream(key)) {
            keyStore.load(in, password);
        }
        return keyStore;
    }

    public RequestConfig buildRequestConnfig() {
        RequestConfig.Builder requestBuilder = RequestConfig.custom().setConnectTimeout(5000).setSocketTimeout(5000);

        if (proxy != null) {

            if (proxy.isValid()) {
                requestBuilder.setProxy(new HttpHost(proxy.getHostname(), proxy.resolvePort()));
            }
        }

        return requestBuilder.build();
    }
}

class Proxy {
    private String hostname;
    private int port;
    private String userName;
    private String password;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isValid() {
        return StringUtils.isNotBlank(hostname);
    }

    public boolean hasCredentials() {
        return StringUtils.isNotBlank(hostname) && StringUtils.isNotBlank(userName);
    }


    public int resolvePort() {
        return port <= 0 ? 80 : port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Proxy proxy = (Proxy) o;

        return new EqualsBuilder()
                .append(port, proxy.port)
                .append(hostname, proxy.hostname)
                .append(userName, proxy.userName)
                .append(password, proxy.password)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(hostname)
                .append(port)
                .append(userName)
                .append(password)
                .toHashCode();
    }
}
