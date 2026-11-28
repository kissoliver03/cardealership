package hu.mik.pte.prog5.carmanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @NotBlank(message = "A kategória neve nem lehet üres!")
    @Column(name = "category_name", nullable = false, length = 100)
    private String categoryName;

    @NotNull(message = "A napi díj megadása kötelező!")
    @Positive(message = "A napi díjnak pozitívnak kell lennie!")
    @Column(name = "daily_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal dailyFee;

    public Category() {
    }

    public Category(String categoryName, BigDecimal dailyFee) {
        this.categoryName = categoryName;
        this.dailyFee = dailyFee;
    }

    public Category(Long categoryId, String categoryName, BigDecimal dailyFee) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.dailyFee = dailyFee;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public BigDecimal getDailyFee() {
        return dailyFee;
    }

    public void setDailyFee(BigDecimal dailyFee) {
        this.dailyFee = dailyFee;
    }

    @Override
    public String toString() {
        return categoryName + " (" + dailyFee + " Ft/nap)";
    }
}
