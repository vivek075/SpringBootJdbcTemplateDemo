package com.example.demo.repository;

import com.example.demo.model.Product;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ProductRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    public ProductRepository(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("products")
                .usingGeneratedKeyColumns("id");
    }

    private static final class ProductMapper implements RowMapper<Product> {
        @Override
        public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
            Product product = new Product();
            product.setId(rs.getLong("id"));
            product.setName(rs.getString("name"));
            product.setPrice(rs.getDouble("price"));
            return product;
        }
    }
// The JdbcTemplate.update method does not automatically set the generated keys back to the entity. You need to use SimpleJdbcInsert to get the generated key.
    public Product save(Product product) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", product.getName());
        parameters.put("price", product.getPrice());
        Number newId = jdbcInsert.executeAndReturnKey(parameters);
        product.setId(newId.longValue());
        return product;
    }

    public Product findById(Long id) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM products WHERE id = ?",
                    new ProductMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Product> findAll() {
        return jdbcTemplate.query("SELECT * FROM products", new ProductMapper());
    }

    public int update(Product product) {
        return jdbcTemplate.update(
                "UPDATE products SET name = ?, price = ? WHERE id = ?",
                product.getName(), product.getPrice(), product.getId());
    }

    public int deleteById(Long id) {
        return jdbcTemplate.update("DELETE FROM products WHERE id = ?", id);
    }
}
