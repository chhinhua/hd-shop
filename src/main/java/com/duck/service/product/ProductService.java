package com.duck.service.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.duck.dto.product.ProductDTO;
import com.duck.dto.product.ProductResponse;
import com.duck.entity.Product;

import java.security.Principal;
import java.util.List;

/**
 * @author Chhin Hua
 * @date 7/10/2023
 */
public interface ProductService {

    /**
     * Finds a product by its ID.
     *
     * @param productId The ID of the product to find.
     * @return The product with the specified ID.
     */
    Product findById(final Long productId);

    /**
     * Creates a new product.
     *
     * @param product The product to be created.
     * @return The created product data transfer object.
     */
    ProductDTO create(final Product product);

    /**
     * Updates an existing product.
     *
     * @param productDTO The product data transfer object containing the updated details.
     * @param productId  The ID of the product to be updated.
     * @return The updated product data transfer object.
     */
    ProductDTO update(final ProductDTO productDTO, final Long productId);

    /**
     * Toggles the active status of a product.
     *
     * @param productId The ID of the product whose active status is to be toggled.
     * @return The updated product data transfer object.
     */
    ProductDTO toggleActive(final Long productId);

    /**
     * Toggles the selling status of a product.
     *
     * @param productId The ID of the product whose selling status is to be toggled.
     * @return The updated product data transfer object.
     */
    ProductDTO toggleSelling(final Long productId);


    /**
     * Retrieves a product by its ID.
     *
     * @param productId The ID of the product to be retrieved.
     * @param principal The principal of the logged-in user.
     * @return The product data transfer object.
     */
    ProductDTO getOne(final Long productId, final Principal principal);

    /**
     * Adds a specified quantity to a product.
     *
     * @param product_id The ID of the product to which the quantity is to be added.
     * @param quantity  The quantity to be added.
     * @return The updated product data transfer object.
     */
    // TODO update this implementation method
    ProductDTO addQuantity(final Long product_id, final Integer quantity);

    /**
     * Deletes a product by its ID.
     *
     * @param id The ID of the product to be deleted.
     */
    void delete(final Long id);

    /**
     * Performs analysis on a product.
     *
     * @param productId    The ID of the product to be analyzed.
     * @param analysisType The type of analysis to be performed.
     */
    void productAnalysis(final Long productId, final String analysisType);

    /**
     * Filters products based on provided criteria and returns a paginated response.
     *
     * @param sell         Boolean flag indicating whether to filter by sell status.
     * @param searchTerm   Keyword to filter the products.
     * @param cateName     List of category names to filter the products.
     * @param sortCriteria List of sorting criteria to order the products.
     * @param pageNo       The page number to retrieve.
     * @param pageSize     The number of products per page.
     * @return ProductResponse A paginated response containing the filtered products.
     * @throws JsonProcessingException If there is an error processing JSON data.
     */
    ProductResponse filter(
            Boolean sell,
            String searchTerm,
            List<String> cateName,
            List<String> sortCriteria,
            int pageNo,
            int pageSize
    ) throws JsonProcessingException;

    /**
     * Filter product for client has logedin account
     * @param sell
     * @param searchTerm
     * @param cateNames
     * @param sortCriteria
     * @param pageNo
     * @param pageSize
     * @param username
     * @return
     * @throws JsonProcessingException
     */
    ProductResponse filterProducts(
            Boolean sell,
            String searchTerm,
            List<String> cateNames,
            List<String> sortCriteria,
            int pageNo,
            int pageSize,
            String username
    ) throws JsonProcessingException;
}
