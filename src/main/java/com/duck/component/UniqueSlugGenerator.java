package com.duck.component;
import com.github.slugify.Slugify;
import com.duck.entity.BaseEntity;
import com.duck.entity.Category;
import com.duck.entity.Product;
import com.duck.repository.CategoryRepository;
import com.duck.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniqueSlugGenerator {
    private final Slugify slugify;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

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
    public String generateUniqueProductSlug(String baseSlug) {
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
