package com.fitnest.fitnest.Controller;

import com.fitnest.fitnest.Model.SportCategory;
import com.fitnest.fitnest.Service.SportCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class SportCategoryController {

    private final SportCategoryService sportCategoryService;

    @Autowired
    public SportCategoryController(SportCategoryService sportCategoryService) {
        this.sportCategoryService = sportCategoryService;
    }

    @GetMapping("/getCategories")
    public List<SportCategory> getAllCategories() {
        return sportCategoryService.getAllCategories();
    }
}
