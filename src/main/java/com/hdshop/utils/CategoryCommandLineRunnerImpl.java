package com.hdshop.utils;

import com.hdshop.entities.Category;
import com.hdshop.repositories.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class CategoryCommandLineRunnerImpl implements CommandLineRunner {
    private final CategoryRepository categoryRepository;

    public CategoryCommandLineRunnerImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Create parent category
        Category parentCategory = new Category();
        parentCategory.setName("Parent Category");
        parentCategory.setDescription("This is the parent category");
        parentCategory.setCreateAt(new Date());
        parentCategory.setUpdateAt(new Date());

        // Create child categories
        Category childCategory1 = new Category();
        childCategory1.setName("Child Category 1");
        childCategory1.setDescription("This is the first child category");
        childCategory1.setCreateAt(new Date());
        childCategory1.setUpdateAt(new Date());

        Category childCategory2 = new Category();
        childCategory2.setName("Child Category 2");
        childCategory2.setDescription("This is the second child category");
        childCategory2.setCreateAt(new Date());
        childCategory2.setUpdateAt(new Date());

        // Create grandchildren categories
        Category grandchildren1 = new Category();
        grandchildren1.setName("Grand Child  1");
        grandchildren1.setDescription("This is the first grand 1");
        grandchildren1.setCreateAt(new Date());
        grandchildren1.setUpdateAt(new Date());

        Category grandchildren2 = new Category();
        grandchildren2.setName("Grand Child 2");
        grandchildren2.setDescription("This is the second grand 2");
        grandchildren2.setCreateAt(new Date());
        grandchildren2.setUpdateAt(new Date());

        // Add child categories to parent category
        parentCategory.getChildren().add(childCategory1);
        parentCategory.getChildren().add(childCategory2);

        // Set parent category as parent of child categories
        childCategory1.setParent(parentCategory);
        childCategory2.setParent(parentCategory);

        // Add grand child categories to child category
        childCategory1.getChildren().add(grandchildren1);
        childCategory2.getChildren().add(grandchildren2);

        // Set parent category as parent of grand child categories
        grandchildren1.setParent(childCategory1);
        grandchildren2.setParent(childCategory2);

        // Save parent category, child and grand child categories to database
        categoryRepository.save(parentCategory);
    }
}
