package com.hdshop.service.cart.impl;

import com.hdshop.dto.cart.CartItemDTO;
import com.hdshop.dto.cart.CartItemResponse;
import com.hdshop.dto.cart.CartResponse;
import com.hdshop.entity.*;
import com.hdshop.exception.ResourceNotFoundException;
import com.hdshop.repository.*;
import com.hdshop.service.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
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
     * Retrieves the cart associated with the provided username.
     * If no cart is found, a new one is created for the user.
     *
     * @param username The username of the user.
     * @return The associated cart.
     */
    @Override
    public Cart getCartByUsername(String username) {
        return cartRepository.findByUser_Username(username)
                .orElseGet(() -> {
                    User user = userRepository
                            .findByUsernameOrEmail(username, username)
                            .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

                    Cart newCart = new Cart();
                    newCart.setUser(user);

                    return cartRepository.save(newCart);
                });
    }

    @Override
    public CartResponse getCartById(Long cartId) {
        Cart cart = cartRepository
                .findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "id", cartId));

        return getResponse(cart);
    }

    /**
     * Adds an item to the cart identified by the given cart ID.
     * If the item has a SKU, it processes it with SKU, otherwise without SKU.
     *
     * @param cartId  The ID of the cart.
     * @param itemDTO The item to be added to the cart.
     * @return The updated CartItemDTO.
     */
    @Override
    public CartItemResponse addToCart(Long cartId, CartItemDTO itemDTO) {
        // TODO Handle vấn đề số lượng sản phẩm tồn tại trước khi thêm vào giỏ (quantityAvailable)
        Cart cart = getExistingCartById(cartId);
        Product product = getExistingProductById(itemDTO.getProductId());

        boolean hasSku = itemDTO.getSkuId() != null;

        if (hasSku) {
            return processCartItemWithSku(cart, product, itemDTO);
        } else {
            return processCartItemWithoutSku(cart, product, itemDTO);
        }
    }

    @Override
    @Transactional
    public CartResponse clearItems(String username) {
        Cart cart = getExistingCartByUsername(username);

        // delete all items for of this cart
        cartItemRepository.deleteAll(cart.getCartItems());

        // clear items
        cart.getCartItems().clear();

        // save change
        Cart cartWithClearedItems = cartRepository.save(cart);

        return getResponse(cartWithClearedItems);
    }

    @Override
    @Transactional
    public CartResponse removeListItems(String username, List<Long> itemIds) {
        Cart cart = getExistingCartByUsername(username);

        List<CartItem> items = cartItemRepository.findByIdIn(itemIds);

        // delete items
        cartItemRepository.deleteAll(items);

        // clear items
        cart.getCartItems().removeAll(items);

        // save change
        Cart cartWithRemovedItems = cartRepository.save(cart);

        return getResponse(cartWithRemovedItems);
    }

    private Cart getExistingCartByUsername(String username) {
        Cart cart = cartRepository.findByUser_Username(username)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "user have username", username));
        return cart;
    }

    /**
     * Processes a cart item with SKU information.
     *
     * @param cart    The cart to process the item for.
     * @param product The product associated with the item.
     * @param itemDTO The DTO containing the item information.
     * @return The updated CartItemDTO.
     */
    private CartItemResponse processCartItemWithSku(Cart cart, Product product, CartItemDTO itemDTO) {
        ProductSku sku = getExistingSkuById(itemDTO.getSkuId());

        Optional<CartItem> existingItem = getCartItemByCartProductAndSku(cart, product, sku);

        if (existingItem.isPresent()) {
            CartItem existingCartItem = existingItem.get();
            updateExistingCartItem(existingCartItem, sku, itemDTO.getQuantity());
            return mapToItemResponse(cartItemRepository.save(existingCartItem));
        } else {
            CartItem newCartItem = createNewCartItemWithSku(cart, product, sku, itemDTO);
            return mapToItemResponse(cartItemRepository.save(newCartItem));
        }
    }

    /**
     * Processes a cart item without SKU information.
     *
     * @param cart    The cart to process the item for.
     * @param product The product associated with the item.
     * @param itemDTO The DTO containing the item information.
     * @return The updated CartItemDTO.
     */
    private CartItemResponse processCartItemWithoutSku(Cart cart, Product product, CartItemDTO itemDTO) {
        Optional<CartItem> existingItem = getCartItemByCartAndProduct(cart, product);

        if (existingItem.isPresent()) {
            CartItem existingCartItem = existingItem.get();
            updateExistingCartItem(existingCartItem, product, itemDTO.getQuantity());
            return mapToItemResponse(cartItemRepository.save(existingCartItem));
        } else {
            CartItem newCartItem = createNewCartItemWithoutSku(cart, product, itemDTO);
            return mapToItemResponse(cartItemRepository.save(newCartItem));
        }
    }

    private Optional<CartItem> getCartItemByCartAndProduct(Cart cart, Product product) {
        return cartItemRepository.findByCart_IdAndProduct_ProductId(cart.getId(), product.getProductId());
    }

    private Optional<CartItem> getCartItemByCartProductAndSku(Cart cart, Product product, ProductSku sku) {
        return cartItemRepository.findByCart_IdAndProduct_ProductIdAndSku_SkuId(cart.getId(), product.getProductId(), sku.getSkuId());
    }

    private void updateExistingCartItem(CartItem existingCartItem, ProductSku sku, int quantity) {
        existingCartItem.setQuantity(existingCartItem.getQuantity() + quantity);
        existingCartItem.setPrice(sku.getPrice());
        existingCartItem.setSubTotal(existingCartItem.getPrice().multiply(BigDecimal.valueOf(existingCartItem.getQuantity())));
    }

    private void updateExistingCartItem(CartItem existingCartItem, Product product, int quantity) {
        existingCartItem.setQuantity(existingCartItem.getQuantity() + quantity);
        existingCartItem.setPrice(product.getPrice());
        existingCartItem.setSubTotal(existingCartItem.getPrice().multiply(BigDecimal.valueOf(existingCartItem.getQuantity())));
        existingCartItem.setImageUrl(product.getListImages().get(0));
    }

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

    private String findImageUrlFromOptionValues(ProductSku sku) {
        return sku.getOptionValues().stream()
                .map(OptionValue::getImageUrl)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(sku
                        .getProduct()
                        .getListImages()
                        .get(0)
                );
    }

    private BigDecimal calculateTotalPrice(List<CartItem> cartItems) {
        return cartItems.stream().map(CartItem::getSubTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private CartResponse getResponse(Cart cart) {
        CartResponse response = modelMapper.map(cart, CartResponse.class);
        response.setTotalItems(cart.getCartItems().size());
        response.setTotalPrice(calculateTotalPrice(cart.getCartItems()));

        return response;
    }

    private Cart getExistingCartById(Long cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "id", cartId));
    }

    private Product getExistingProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
    }

    private ProductSku getExistingSkuById(Long skuId) {
        return skuRepository.findById(skuId)
                .orElseThrow(() -> new ResourceNotFoundException("ProductSku", "skuId", skuId));
    }

    private CartItemResponse mapToItemResponse(CartItem entity) {
        return modelMapper.map(entity, CartItemResponse.class);
    }
}
