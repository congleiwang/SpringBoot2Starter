package com.example.restapi;

import com.example.restapi.model.Product;
import com.example.restapi.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RestapiApplication implements CommandLineRunner {

    @Autowired
    private ProductRepository productRepository;

    public static void main(String[] args) {
        SpringApplication.run(RestapiApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Product product = new Product();
        product.setName("Simple Product");
        product.setDescription("This is a tester Product");
        product.setType("CUSTOM");
        product.setCategory("SPECIAL");

        productRepository.save(product);
    }
}
