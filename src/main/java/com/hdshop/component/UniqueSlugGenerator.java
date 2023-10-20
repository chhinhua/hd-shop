package com.hdshop.component;
import com.github.slugify.Slugify;
import com.hdshop.entity.BaseEntity;
import com.hdshop.entity.Category;
import com.hdshop.entity.product.Product;
import com.hdshop.repository.CategoryRepository;
import com.hdshop.repository.product.ProductRepository;
import org.springframework.stereotype.Component;

@Component
public class UniqueSlugGenerator {
    private final Slugify slugify;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    private UniqueSlugGenerator(Slugify slugify, CategoryRepository categoryRepository,
                               ProductRepository productRepository) {
        this.slugify = slugify;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    /**
     * Generate the Unique Slug for category
     * @param baseSlug
     * @return unique slug have been generated
     */
    public String generateUniqueCategorySlug(String baseSlug) {
        String generatedSlug = baseSlug;
        int counter = 1;

        while (categoryRepository.existsCategoryBySlug(generatedSlug)) {
            generatedSlug = baseSlug + "-" + counter;
            counter++;
        }

        return generatedSlug;
    }

    /**
     * Generate the Unique Slug for product
     * @param baseSlug
     * @return unique slug have been generated
     */
    private String generateUniqueProductSlug(String baseSlug) {
        String generatedSlug = baseSlug;
        int counter = 1;

        while (productRepository.existsProductBySlug(generatedSlug)) {
            generatedSlug = baseSlug + "-" + counter;
            counter++;
        }

        return generatedSlug;
    }


    public <T extends BaseEntity> String generateUniqueSlug(T entity, String name) {
        String baseSlug = slugify.slugify(name);
        String uniqueSlug;

        if (entity instanceof Product) {
            uniqueSlug = generateUniqueProductSlug(baseSlug);
        } else if (entity instanceof Category) {
            uniqueSlug = generateUniqueCategorySlug(baseSlug);
        } else {
            throw new IllegalArgumentException("Unsupported entity type");
        }

        return uniqueSlug;
    }
}
