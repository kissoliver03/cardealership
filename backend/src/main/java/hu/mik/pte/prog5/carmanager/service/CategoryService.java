package hu.mik.pte.prog5.carmanager.service;

import hu.mik.pte.prog5.carmanager.model.Category;
import hu.mik.pte.prog5.carmanager.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> list() {
        return categoryRepository.findAll();
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    @Transactional
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    @Transactional
    public void updateDailyFee(Long id, BigDecimal dailyFee) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category != null) {
            category.setDailyFee(dailyFee);
            categoryRepository.save(category);
        }
    }

    @Transactional
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }
}
