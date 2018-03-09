package com.example.restapi.controller;

import com.example.restapi.model.Product;
import com.example.restapi.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/products")
@Api(value = "ProductsControllerAPI", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductsController {

    private Logger LOGGER = LoggerFactory.getLogger(ProductsController.class);

    @Autowired
    ProductService productService;

    @GetMapping(path = "{id}")
    @ApiOperation("Gets the product with specific id")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = Product.class)})
    public ResponseEntity<Product> getProduct(@PathVariable(name = "id") String id) {
        Optional<Product> optionalProduct = productService.getProduct(id);

        if (optionalProduct.isPresent()) {
            return new ResponseEntity<>(optionalProduct.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @ApiOperation("Insert a product")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = Product.class)})
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Product saveProduct(@Valid @RequestBody Product product) {
        return productService.save(product);
    }

    @ApiOperation("Insert a product")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = Product.class)})
    @PutMapping(path = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Product> updsateProduct(@RequestBody Product product,
                                                  @PathVariable(name = "id") String id) {
        Optional<Product> productOptional = productService.findById(id);

        if (productOptional.isPresent()) {
            Product save = productService.save(product);
            return new ResponseEntity<>(save, HttpStatus.OK);
        } else {
            LOGGER.info("No product found with given id.");
            return new ResponseEntity<>(product, HttpStatus.NO_CONTENT);
        }
    }

    @DeleteMapping(path = "{id}")
    public void deleteProduct(@PathVariable(name = "id") String id) {
        productService.deleteById(id);
    }
}
