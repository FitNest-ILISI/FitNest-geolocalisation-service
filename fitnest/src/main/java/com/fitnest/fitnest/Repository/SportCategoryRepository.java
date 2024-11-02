package com.fitnest.fitnest.Repository;

import com.fitnest.fitnest.Model.SportCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SportCategoryRepository extends JpaRepository<SportCategory, Long> {
    Optional<SportCategory> findByName(String name);
    Optional<SportCategory> findById(Long id);
}
