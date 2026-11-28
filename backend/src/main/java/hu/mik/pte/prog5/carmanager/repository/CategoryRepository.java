package hu.mik.pte.prog5.carmanager.repository;

import hu.mik.pte.prog5.carmanager.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
