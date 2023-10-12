package com.hdshop.utils;
import com.github.slugify.Slugify;
import com.hdshop.repositories.CategoryRepository;
import com.hdshop.repositories.ProductRepository;
import org.springframework.stereotype.Component;

@Component
public class UniqueSlugGenerator {

    private final Slugify slugify;

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public UniqueSlugGenerator(Slugify slugify, CategoryRepository categoryRepository,
                               ProductRepository productRepository) {
        this.slugify = slugify;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    public String generateUniqueCategorySlug(String baseSlug) {
        String generatedSlug = baseSlug;
        int counter = 1;

        while (categoryRepository.existsCategoryBySlug(generatedSlug)) {
            generatedSlug = baseSlug + "-" + counter;
            counter++;
        }

        return generatedSlug;
    }

    public String generateUniqueProductSlug(String baseSlug) {
        String generatedSlug = baseSlug;
        int counter = 1;

        while (productRepository.existsProductBySlug(generatedSlug)) {
            generatedSlug = baseSlug + "-" + counter;
            counter++;
        }

        return generatedSlug;
    }
}
