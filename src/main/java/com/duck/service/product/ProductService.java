package com.duck.service.product;

import com.duck.dto.product.AddInventoryRequest;
import com.duck.dto.product.ProductDTO;
import com.duck.dto.product.ProductResponse;
import com.duck.entity.Product;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.security.Principal;
import java.util.List;

/**
 * @author <a href="https://github.com/chhinhua">Chhin Hua</a>
 * @date 7/10/2023
 */
public interface ProductService {

    /**
     * Adds inventory based on the provided request.
     *
     * @param request the {@link AddInventoryRequest} containing the product ID and SKUs with their respective quantities to add
     * @return a {@link ProductDTO} representing the updated product
     * @throws com.duck.exception.BadCredentialsException if any {@link com.duck.entity.ProductSku} or {@link Product} is not found, or if any quantity is negative
     */
    ProductDTO addInventory(final AddInventoryRequest request);


    /**
     * Applies a discount to a {@link Product} and its associated {@link com.duck.entity.ProductSku} based on the given discount percentage.
     *
     * @param productId       the ID of the {@link Product} to which the discount will be applied
     * @param percentDiscount the percentage discount to be applied to the product's price
     * @throws com.duck.exception.BadCredentialsException if the percentDiscount is negative or greater than 100
     */
    void makeDiscount(final long productId, final int percentDiscount);


    /**
     * Finds a product by its ID.
     *
     * @param productId The ID of the product to find.
     * @return The product with the specified ID.
     */
    Product findById(final Long productId);


    /**
     * Creates a new {@link Product}.
     *
     * @param product The {@link Product} to be created.
     * @return The created {@link ProductDTO} data transfer object.
     */
    ProductDTO create(final Product product);


    /**
     * Updates an existing {@link Product}.
     *
     * @param productDTO The {@link ProductDTO} data transfer object containing the updated details.
     * @param productId  The ID of the product to be updated.
     * @return The updated {@link ProductDTO} data transfer object.
     */
    ProductDTO update(final ProductDTO productDTO, final Long productId);


    /**
     * Toggles the active status of a {@link Product}.
     *
     * @param productId The ID of the product whose active status is to be toggled.
     * @return The updated {@link ProductDTO} data transfer object.
     */
    ProductDTO toggleActive(final Long productId);


    /**
     * Toggles the selling status of a {@link Product}.
     *
     * @param productId The ID of the product whose selling status is to be toggled.
     * @return The updated {@link ProductDTO} data transfer object.
     */
    ProductDTO toggleSelling(final Long productId);


    /**
     * Retrieves a {@link Product} by its ID.
     *
     * @param productId The ID of the product to be retrieved.
     * @param principal The principal of the logged-in user.
     * @return The {@link ProductDTO} data transfer object.
     */
    ProductDTO getOne(final Long productId, final Principal principal);


    /**
     * Adds a specified quantity to a {@link Product}.
     *
     * @param product_id The ID of the product to which the quantity is to be added.
     * @param quantity  The quantity to be added.
     * @return The updated {@link ProductDTO} data transfer object.
     */
    ProductDTO addQuantity(final Long product_id, final Integer quantity);


    /**
     * Deletes a {@link Product} by its ID.
     *
     * @param id The ID of the product to be deleted.
     */
    void delete(final Long id);

    
    /**
     * Analyzes {@link Product} interactions and increments the appropriate field based on the analysis type.
     *
     * @param productId    the ID of the product to analyze
     * @param analysisType the type of analysis (e.g., CLICK, VIEW, ADD_CART)
     * @throws IllegalArgumentException if the analysis type is not recognized
     */
    void productAnalysis(final Long productId, final String analysisType);


    /**
     * Filters {@link Product} based on provided criteria and returns a paginated response.
     *
     * @param sell         Boolean flag indicating whether to filter by sell status.
     * @param searchTerm   Keyword to filter the products.
     * @param cateName     List of category names to filter the products.
     * @param sortCriteria List of sorting criteria to order the products.
     * @param pageNo       The page number to retrieve.
     * @param pageSize     The number of products per page.
     * @return {@link ProductResponse} A paginated response containing the filtered products.
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
     * Filter {@link Product} for client has logedin account
     * @param sell
     * @param searchTerm
     * @param cateNames
     * @param sortCriteria
     * @param pageNo
     * @param pageSize
     * @param username
     * @return {@link ProductResponse}
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
