package com.hdshop.init;

import com.github.slugify.Slugify;
import com.hdshop.entity.Category;
import com.hdshop.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class CategoryCommandLineRunnerImpl implements CommandLineRunner {
    private final CategoryRepository categoryRepository;
    private final Slugify slugify;

    public CategoryCommandLineRunnerImpl(CategoryRepository categoryRepository, Slugify slugify) {
        this.categoryRepository = categoryRepository;
        this.slugify = slugify;
    }

    /**
     * Create sample category (parent, child, grand child)
     * @author Chhin Hua
     * @date 05-10-2023
     * @return
     */
    @Override
    public void run(String... args) throws Exception {
        //createXampleCategories();
    }

    private void createXampleCategories() {
        Date date = new Date();
        // Create parent category
        Category parentCategory = new Category();
        parentCategory.setName("Parent Category");
        parentCategory.setDescription("This is the parent category");
        parentCategory.setSlug(slugify.slugify(parentCategory.getName()));

        // Create child categories
        Category childCategory1 = new Category();
        childCategory1.setName("Child Category 1");
        childCategory1.setDescription("This is the first child category");
        childCategory1.setSlug(slugify.slugify(childCategory1.getName()));

        Category childCategory2 = new Category();
        childCategory2.setName("Child Category 2");
        childCategory2.setSlug(slugify.slugify(childCategory2.getName()));
        childCategory2.setDescription("This is the second child category");

        // Create grandchildren categories
        Category grandchildren1 = new Category();
        grandchildren1.setName("Grand Child  1");
        grandchildren1.setSlug(slugify.slugify(grandchildren1.getName()));
        grandchildren1.setDescription("This is the first grand 1");

        Category grandchildren2 = new Category();
        grandchildren2.setName("Grand Child 2");
        grandchildren2.setSlug(slugify.slugify(grandchildren2.getName()));
        grandchildren2.setDescription("This is the second grand 2");

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
