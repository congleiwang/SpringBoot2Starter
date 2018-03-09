package com.example.restapi.service;

import com.example.restapi.model.Product;
import com.example.restapi.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductService {

    private Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepository productRepository;


    public Optional<Product> getProduct(String id) {
        LOGGER.debug("Tracing id -> " + id);
        return productRepository.findById(id);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public Optional<Product> findById(String id) {
        LOGGER.debug("Tracing id -> " + id);
        return productRepository.findById(id);
    }

    public void deleteById(String id) {
        LOGGER.debug("Tracing id -> " + id);
        productRepository.deleteById(id);
    }
}
