package com.hdshop.service.cart.impl;

import com.hdshop.dto.cart.CartItemDTO;
import com.hdshop.dto.cart.CartItemResponse;
import com.hdshop.entity.*;
import com.hdshop.exception.ResourceNotFoundException;
import com.hdshop.repository.*;
import com.hdshop.service.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductSkuRepository skuRepository;
    private final ModelMapper modelMapper;

    /**
     * Adds an item to the cart identified by the given cart ID.
     * If the item has a SKU, it processes it with SKU, otherwise without SKU.
     * @param cartId The ID of the cart.
     * @param itemDTO The item to be added to the cart.
     * @return The updated CartItemDTO.
     */
    @Override
    public CartItemResponse addToCart(Long cartId, CartItemDTO itemDTO) {
        // TODO Handle vấn đề số lượng sản phẩm tồn tại trước khi thêm vào giỏ (quantityAvailable)
        Cart cart = getCartById(cartId);
        Product product = getProductById(itemDTO.getProductId());

        boolean hasSku = itemDTO.getSkuId() != null;

        if (hasSku) {
            return processCartItemWithSku(cart, product, itemDTO);
        } else {
            return processCartItemWithoutSku(cart, product, itemDTO);
        }
    }

    /**
     * Retrieves the cart associated with the provided username.
     * If no cart is found, a new one is created for the user.
     * @param username The username of the user.
     * @return The associated cart.
     */
    @Override
    public Cart getCartByUsername(String username) {
        return cartRepository.findByUser_Username(username)
                .orElseGet(() -> {
                    User user = userRepository.findByUsernameOrEmail(username, username)
                            .orElseThrow(()-> new ResourceNotFoundException("User", "username", username));

                    Cart newCart = new Cart();
                    newCart.setUser(user);

                    return cartRepository.save(newCart);
                });
    }

    /**
     * Overrides the method to change the quantity of a CartItem.
     *
     * @param cartItemId The ID of the CartItem to be updated.
     * @param quantity The new quantity for the CartItem.
     * @return A CartItemDTO representing the updated CartItem.
     * @throws ResourceNotFoundException if a CartItem with the given ID is not found.
     */
    @Override
    public CartItemResponse changeQuantity(Long cartItemId, int quantity) {
        // check existing CartItem by id
        CartItem existingItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(()-> new ResourceNotFoundException("CartItem", "id", cartItemId));

        existingItem.setQuantity(quantity);
        existingItem.setSubTotal(existingItem.getPrice().multiply(BigDecimal.valueOf(quantity)));

        CartItem changeItemQuantity =  cartItemRepository.save(existingItem);

        return mapToResponse(changeItemQuantity);
    }

    /**
     * Processes a cart item with SKU information.
     * @param cart The cart to process the item for.
     * @param product The product associated with the item.
     * @param itemDTO The DTO containing the item information.
     * @return The updated CartItemDTO.
     */
    private CartItemResponse processCartItemWithSku(Cart cart, Product product, CartItemDTO itemDTO) {
        ProductSku sku = getSkuById(itemDTO.getSkuId());

        Optional<CartItem> existingItem = cartItemRepository
                .findByCart_IdAndProduct_ProductIdAndSku_SkuId(cart.getId(), product.getProductId(), sku.getSkuId());

        if (existingItem.isPresent()) {
            CartItem existingCartItem = existingItem.get();
            existingCartItem.setQuantity(existingCartItem.getQuantity() + itemDTO.getQuantity());
            existingCartItem.setPrice(sku.getPrice());
            existingCartItem.setSubTotal(existingCartItem.getPrice().multiply(BigDecimal.valueOf(existingCartItem.getQuantity())));

            return mapToResponse(cartItemRepository.save(existingCartItem));
        } else {
            CartItem newCartItem = createNewCartItemWithSku(cart, product, sku, itemDTO);

            return mapToResponse(cartItemRepository.save(newCartItem));
        }
    }

    /**
     * Processes a cart item without SKU information.
     * @param cart The cart to process the item for.
     * @param product The product associated with the item.
     * @param itemDTO The DTO containing the item information.
     * @return The updated CartItemDTO.
     */
    private CartItemResponse processCartItemWithoutSku(Cart cart, Product product, CartItemDTO itemDTO) {

        Optional<CartItem> existingItem = cartItemRepository
                .findByCart_IdAndProduct_ProductId(cart.getId(), product.getProductId());

        if (existingItem.isPresent()) {
            CartItem existingCartItem = existingItem.get();
            existingCartItem.setQuantity(existingCartItem.getQuantity() + itemDTO.getQuantity());
            existingCartItem.setPrice(product.getPrice());
            existingCartItem.setSubTotal(existingCartItem.getPrice().multiply(BigDecimal.valueOf(existingCartItem.getQuantity())));
            existingCartItem.setImageUrl(product.getListImages().get(0));

            return mapToResponse(cartItemRepository.save(existingCartItem));
        } else {
            CartItem newCartItem = createNewCartItemWithoutSku(cart, product, itemDTO);

            return mapToResponse(cartItemRepository.save(newCartItem));
        }
    }

    /**
     * Creates a new cart item with SKU information.
     * @param cart The cart to associate with the new item.
     * @param product The product associated with the item.
     * @param sku The SKU information.
     * @param itemDTO The DTO containing the item information.
     * @return The newly created CartItem.
     */
    private CartItem createNewCartItemWithSku(Cart cart, Product product, ProductSku sku, CartItemDTO itemDTO) {
        CartItem newCartItem = new CartItem();
        newCartItem.setPrice(sku.getPrice());
        newCartItem.setQuantity(itemDTO.getQuantity());
        newCartItem.setSubTotal(sku.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())));
        newCartItem.setImageUrl(findImageUrlFromOptionValues(sku));
        newCartItem.setCart(cart);
        newCartItem.setProduct(product);
        newCartItem.setSku(sku);
        return newCartItem;
    }

    /**
     * Creates a new cart item without SKU information.
     * @param cart The cart to associate with the new item.
     * @param product The product associated with the item.
     * @param itemDTO The DTO containing the item information.
     * @return The newly created CartItem.
     */
    private CartItem createNewCartItemWithoutSku(Cart cart, Product product, CartItemDTO itemDTO) {
        CartItem newCartItem = new CartItem();
        newCartItem.setPrice(product.getPrice());
        newCartItem.setQuantity(itemDTO.getQuantity());
        newCartItem.setSubTotal(product.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())));
        newCartItem.setImageUrl(product.getListImages().get(0));
        newCartItem.setCart(cart);
        newCartItem.setProduct(product);
        return newCartItem;
    }

    private Cart getCartById(Long cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "id", cartId));
    }

    private Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
    }

    private ProductSku getSkuById(Long skuId) {
        return skuRepository.findById(skuId)
                .orElseThrow(() -> new ResourceNotFoundException("ProductSku", "skuId", skuId));
    }

    private String findImageUrlFromOptionValues(ProductSku sku) {
        return sku.getOptionValues().stream()
                .filter(value -> value.getImageUrl() != null)
                .map(OptionValue::getImageUrl)
                .findFirst()
                .orElse(null);
    }

    private CartItemResponse mapToResponse(CartItem entity) {
        return modelMapper.map(entity, CartItemResponse.class);
    }
}
