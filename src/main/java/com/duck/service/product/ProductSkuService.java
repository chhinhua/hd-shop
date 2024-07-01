package com.duck.service.product;

import com.duck.dto.product.ProductSkuDTO;
import com.duck.entity.Product;
import com.duck.entity.ProductSku;
import com.duck.exception.InvalidException;
import com.duck.exception.ResourceNotFoundException;

import java.util.List;

/**
 * @author Chhin Hua
 * @date 20/10/2023
 */
public interface ProductSkuService {

    /**
     * Saves or updates a list of ProductSku instances for a specific product.
     *
     * @param productId The ID of the product to update SKUs for
     * @param skus      The list of ProductSku instances to save or update
     * @throws ResourceNotFoundException if the product with the given ID is not found
     */
    void saveOrUpdateListSkus(final Long productId, final List<ProductSku> skus);

    /**
     * Saves the list of {@link ProductSku} instances associated with a {@link Product} when creating a product.
     * <p>
     * This method iterates through the skus of the provided {@link Product}, assigns
     * the product reference, retrieves option values, sets percent discount if available,
     * and saves each SKU to the repository. The saved list of skus is returned.
     *
     * @param product The {@link Product} containing the skus to be saved.
     */
    void saveSkusProductCreation(final Product product);

    /**
     * Finds a {@link ProductSku} based on the {@link Product} ID and a list of option value names.
     *
     * @param productId The ID of the product to find the SKU for
     * @param valueNames The list of option value names to identify the specific SKU
     * @return The found ProductSku instance
     * @throws ResourceNotFoundException if no matching SKU is found
     * @throws InvalidException if productId is null or valueNames is empty
     */
    ProductSku findByProductIdAndValueNames(final Long productId, final List<String> valueNames);

    ProductSku findById(final Long skuId);

    /**
     * Finds a {@link ProductSkuDTO} by generating an SKU based on the provided productId and valueNames.
     * If the SKU is not found using the original valueNames order, the method attempts to find it
     * using the reversed valueNames order.
     *
     * @param productId the ID of the product
     * @param valueNames the list of values used to generate the SKU
     * @return the found ProductSkuDTO
     * @throws ResourceNotFoundException if the ProductSku is not found
     */
    ProductSkuDTO findBySku(final Long productId, final List<String> valueNames);

    /**
     * Generates an SKU based on the provided productId and valueNames.
     * The SKU generation logic is defined within this method.
     *
     * @param productId the ID of the product
     * @param valueNames the list of values used to generate the SKU
     * @return the generated SKU as a String
     */
    String generateSku(final Long productId, final List<String> valueNames);
}
