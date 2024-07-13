package com.amplifiers.pathfinder.utility;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/public/categories")
public class CategoryController {

    @GetMapping("/all")
    public List<String> getAllCategories() {
        List<String> categoryNames = Arrays.stream(Category.values())
                .map(Category::getDisplayName)
                .collect(Collectors.toList());
        return categoryNames;
    }
}
