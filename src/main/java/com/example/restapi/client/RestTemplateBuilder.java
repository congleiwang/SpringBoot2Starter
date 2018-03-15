package com.example.restapi.client;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * Class: RestTemplateHelper.java
 *
 * @author: Springzen
 * @since: Nov 23, 2015
 * @update: Mar 14. 2018
 * @version: 1.0
 *
 * Copyright (c) 2015-2018 Springzen - Governed by MIT License
 */
public class RestTemplateBuilder {
    private boolean throwOnException;
    private boolean enableSsl;

    private int connectTimeout;
    private int readTimeout;

    public RestTemplateBuilder throwError() {
        this.throwOnException = true;
        return this;
    }

    public RestTemplateBuilder withSsl() {
        this.enableSsl = true;
        return this;
    }

    public RestTemplateBuilder withConnectionTimeout(int connectionTimeout) {
        this.connectTimeout = connectionTimeout;

        return this;
    }

    public RestTemplateBuilder withReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;

        return this;
    }

    public ResponseEntity<String> get(String uri, Map<String, String> params) {
        Map<String, String> safeParams = Optional.ofNullable(params).orElse(Collections.emptyMap());
        return exchange(null, uri, HttpMethod.GET, safeParams, null, MediaType.APPLICATION_JSON);
    }

    public ResponseEntity<String> get(String uri) {
        return exchange(null, uri, HttpMethod.GET, null, null, MediaType.APPLICATION_JSON);
    }

    /**
     * Post JSON for uri with json body
     *
     * @param body
     * @param uri
     * @return
     */
    public ResponseEntity<String> postForJson(Object body, String uri) {
        return exchange(body, uri, HttpMethod.POST, null, null, MediaType.APPLICATION_JSON);
    }

    /**
     * Post JSON for uri with json body and headers
     *
     * @param body
     * @param uri
     * @param headers
     * @return
     */
    public ResponseEntity<String> postForJson(Object body, String uri, Map<String, String> headers) {
        return exchange(body, uri, HttpMethod.POST, null, headers, MediaType.APPLICATION_JSON);
    }

    /**
     * Post JSON for uri with json body, quary params and headers
     *
     * @param body
     * @param uri
     * @param params
     * @param headers
     * @return
     */
    public ResponseEntity<String> postForJson(Object body, String uri, Map<String, String> params,
                                              Map<String, String> headers) {
        return exchange(body, uri, HttpMethod.POST, params, headers, MediaType.APPLICATION_JSON);
    }

    /**
     * Create an exchange call for String based restful calls.
     *
     * @param body
     * @param uri
     * @param method
     * @param params
     * @param headers
     * @param mediaType
     * @return
     */
    public ResponseEntity<String> exchange(final Object body, final String uri, final HttpMethod method,
                                           final Map<String, String> params, final Map<String, String> headers, final MediaType mediaType) {
        RestTemplate restTemplate = buildRestTemplate();

        /**
         * Query Params
         */
        MultiValueMap<String, String> qParams = new LinkedMultiValueMap<String, String>();
        emptyMap(params).forEach((k, v) -> {
            qParams.add(k, v);
        });

        /**
         * Build the URL for the exchange
         */
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(uri).queryParams(qParams);
        URI url = uriComponentsBuilder.build().toUri();

        /**
         * Headers + MediaType
         */
        HttpHeaders httpHeaders = new HttpHeaders();
        emptyMap(headers).forEach((k, v) -> {
            httpHeaders.add(v, v);
        });
        httpHeaders.setContentType(mediaType);

        /**
         * Set the entity
         */
        HttpEntity<Object> entity;
        if (body != null) {
            entity = new HttpEntity<Object>(body, httpHeaders);
        } else {
            entity = new HttpEntity<Object>(httpHeaders);
        }

        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = restTemplate.exchange(url, method, entity, String.class);
        } catch (HttpClientErrorException e) {
            HttpStatus httpStatus = e.getStatusCode();

            /**
             * We failed to get the HTTP Status - a tea pot is the only assumption we can make
             */
            if (httpStatus == null) {
                httpStatus = HttpStatus.I_AM_A_TEAPOT;
            }

            String responseBodyAsString = e.getResponseBodyAsString();
            if (StringUtils.isNotBlank(responseBodyAsString)) {
                responseEntity = new ResponseEntity<String>(responseBodyAsString, httpStatus);
            } else {
                responseEntity = new ResponseEntity<String>(httpStatus);
            }

            if (throwOnException) {
                throw e;
            }
        } catch (ResourceAccessException e) {
            if (throwOnException) {
                throw e;
            }
        } catch (Exception e) {
            if (throwOnException) {
                throw new RestClientException("General exception in RestClient call.", e);
            }
        } finally {
            if (responseEntity == null) {
                responseEntity = new ResponseEntity<String>(HttpStatus.EXPECTATION_FAILED);
            }
        }

        return responseEntity;
    }

    private RestTemplate buildRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        if (enableSsl) {
            /**
             * @todo - add support for SSL
             */
            HttpClientBuilderHelper httpClientBuilderHelper = new HttpClientBuilderHelper();
            HttpClient client = httpClientBuilderHelper.createSecureClient();
            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(client);

            /**
             * Timeout - 20 seconds
             */
            requestFactory.setConnectTimeout(this.connectTimeout > 0 ? this.connectTimeout : 20000);
            requestFactory.setReadTimeout(this.readTimeout > 0 ? this.readTimeout : 20000);
            restTemplate = new RestTemplate(requestFactory);
        }

        return restTemplate;
    }

    /**
     * Create an empty map if the current map is null
     *
     * @param map
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> Map<K, V> emptyMap(Map<K, V> map) {
        return map == null ? Collections.<K, V>emptyMap() : map;
    }
}

