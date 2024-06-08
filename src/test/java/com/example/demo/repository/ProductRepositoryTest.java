package com.example.demo.repository;

import com.example.demo.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProductRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository = new ProductRepository(jdbcTemplate, jdbcTemplate.getDataSource());
    }

    @Test
    @Sql("/test-schema.sql")
    void testSaveAndFindById() {
        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(100.0);

        Product savedProduct = productRepository.save(product);

        Product foundProduct = productRepository.findById(savedProduct.getId());

        assertNotNull(foundProduct);
        assertEquals("Test Product", foundProduct.getName());
        assertEquals(100.0, foundProduct.getPrice());
    }

    @Test
    @Sql("/test-schema.sql")
    void testFindAll() {
        Product product1 = new Product();
        product1.setName("Product 1");
        product1.setPrice(50.0);
        productRepository.save(product1);

        Product product2 = new Product();
        product2.setName("Product 2");
        product2.setPrice(150.0);
        productRepository.save(product2);

        List<Product> products = productRepository.findAll();

        assertEquals(2, products.size());
    }

    @Test
    @Sql("/test-schema.sql")
    void testUpdate() {
        Product product = new Product();
        product.setName("Old Product");
        product.setPrice(80.0);
        Product savedProduct = productRepository.save(product);

        savedProduct.setName("Updated Product");
        savedProduct.setPrice(90.0);
        productRepository.update(savedProduct);

        Product updatedProduct = productRepository.findById(savedProduct.getId());

        assertEquals("Updated Product", updatedProduct.getName());
        assertEquals(90.0, updatedProduct.getPrice());
    }

    @Test
    @Sql("/test-schema.sql")
    void testDeleteById() {
        Product product = new Product();
        product.setName("Delete Product");
        product.setPrice(60.0);
        Product savedProduct = productRepository.save(product);

        productRepository.deleteById(savedProduct.getId());

        Product deletedProduct = productRepository.findById(savedProduct.getId());

        assertNull(deletedProduct);
    }
}
