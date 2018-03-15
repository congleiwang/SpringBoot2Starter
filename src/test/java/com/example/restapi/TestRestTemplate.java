package com.example.restapi;

import com.example.restapi.client.RestTemplateBuilder;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;

public class TestRestTemplate {
    @Test
    public void sslLoads() {
        RestTemplateBuilder templateHelper = new RestTemplateBuilder().throwError().withSsl();
        ResponseEntity<String> response = templateHelper.get("https://www.foaas.com/version");

        Assert.assertTrue(response.getBody() != null);
        Assert.assertTrue(response.getStatusCodeValue() == 200);
        Assert.assertTrue(response.getHeaders() != null);
    }

    @Test
    public void testStarWars() {
        RestTemplateBuilder templateHelper = new RestTemplateBuilder().throwError().withSsl();
        ResponseEntity<String> response = templateHelper.get("https://swapi.co/api/people/1");

        Assert.assertTrue(response.getBody() != null);
        Assert.assertTrue(response.getStatusCodeValue() == 200);
        Assert.assertTrue(response.getHeaders() != null);

        try {
            JsonNode nameNode = getJsonKey(response.getBody());
            String name = nameNode.path("name").textValue();
            Assert.assertEquals(name, "Luke Skywalker");
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test(expected = HttpClientErrorException.class)
    public void thisIsNotTheUrlYouAreLookingFor() {
        RestTemplateBuilder templateHelper = new RestTemplateBuilder().throwError().withSsl();
        ResponseEntity<String> response = templateHelper.get("https://swapi.co/people/1");
    }

    @Test
    public void thisIsNotTheUrlYouAreLookingForNoError() {
        RestTemplateBuilder templateHelper = new RestTemplateBuilder().withSsl();
        ResponseEntity<String> response = templateHelper.get("https://swapi.co/people/1");
        Assert.assertTrue(response.getStatusCodeValue() >= 400);
    }

    private JsonNode getJsonKey(String input) throws IOException {

        ObjectMapper mapper = newObjectMapper();
        JsonNode root = mapper.readTree(input);

        return root;
    }

    public static ObjectMapper newObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);

        return objectMapper;
    }
}
