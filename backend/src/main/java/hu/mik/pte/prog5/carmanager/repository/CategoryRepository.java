package hu.mik.pte.prog5.carmanager.repository;

import hu.mik.pte.prog5.carmanager.model.Category;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CategoryRepository {

    private final JdbcClient jdbcClient;

    private final RowMapper<Category> categoryRowMapper = (rs, rowNum) -> new Category(
            rs.getLong("category_id"),
            rs.getString("category_name"),
            rs.getBigDecimal("daily_fee")
    );

    public CategoryRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<Category> findAll() {
        return jdbcClient.sql("SELECT category_id, category_name, daily_fee FROM category ORDER BY category_id")
                .query(categoryRowMapper)
                .list();
    }

    public Optional<Category> findById(Long id) {
        return jdbcClient.sql("SELECT category_id, category_name, daily_fee FROM category WHERE category_id = :id")
                .param("id", id)
                .query(categoryRowMapper)
                .optional();
    }

    public Category save(Category category) {
        if (category.getCategoryId() == null) {
            Long generatedId = jdbcClient.sql("""
                    INSERT INTO category (category_name, daily_fee)
                    VALUES (:name, :fee)
                    RETURNING category_id
                    """)
                    .param("name", category.getCategoryName())
                    .param("fee", category.getDailyFee())
                    .query(Long.class)
                    .single();
            category.setCategoryId(generatedId);
        } else {
            jdbcClient.sql("""
                    UPDATE category
                    SET category_name = :name, daily_fee = :fee
                    WHERE category_id = :id
                    """)
                    .param("name", category.getCategoryName())
                    .param("fee", category.getDailyFee())
                    .param("id", category.getCategoryId())
                    .update();
        }
        return category;
    }

    public void deleteById(Long id) {
        jdbcClient.sql("DELETE FROM category WHERE category_id = :id")
                .param("id", id)
                .update();
    }
}
