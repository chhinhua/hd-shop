package com.duck.init;

import com.github.slugify.Slugify;
import com.duck.entity.Category;
import com.duck.repository.CategoryRepository;
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
        parentCategory.setName("Giày");
        parentCategory.setDescription("Danh mục giày");
        parentCategory.setSlug(slugify.slugify(parentCategory.getName()));

        // Create child categories
        Category childCategory1 = new Category();
        childCategory1.setName("Giày nam");
        childCategory1.setDescription("Danh mục giày nam");
        childCategory1.setSlug(slugify.slugify(childCategory1.getName()));

        Category childCategory2 = new Category();
        childCategory2.setName("Giày nữ");
        childCategory2.setSlug(slugify.slugify(childCategory2.getName()));
        childCategory2.setDescription("Danh mục giày nữ");

        // Add child categories to parent category
        parentCategory.getChildren().add(childCategory1);
        parentCategory.getChildren().add(childCategory2);

        // Set parent category as parent of child categories
        childCategory1.setParent(parentCategory);
        childCategory2.setParent(parentCategory);

        // Save parent category, child categories to database
        categoryRepository.save(parentCategory);
    }
}
