package com.fitnest.fitnest.Service;

import com.fitnest.fitnest.Model.SportCategory;
import com.fitnest.fitnest.Repository.SportCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SportCategoryService {
    private final SportCategoryRepository sportCategoryRepository;

    @Autowired
    public SportCategoryService(SportCategoryRepository sportCategoryRepository) {
        this.sportCategoryRepository = sportCategoryRepository;
    }

    public List<SportCategory> getAllCategories() {
        return sportCategoryRepository.findAll();
    }
    public Optional<SportCategory> getCategoryById(Long id) {
        return sportCategoryRepository.findById(id);
    }
}
