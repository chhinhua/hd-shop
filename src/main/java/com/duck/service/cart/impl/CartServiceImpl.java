package com.duck.service.cart.impl;

import com.duck.dto.cart.CartItemDTO;
import com.duck.dto.cart.CartItemResponse;
import com.duck.dto.cart.CartResponse;
import com.duck.entity.*;
import com.duck.exception.ResourceNotFoundException;
import com.duck.repository.CartItemRepository;
import com.duck.repository.CartRepository;
import com.duck.repository.ProductRepository;
import com.duck.repository.UserRepository;
import com.duck.service.cart.CartService;
import com.duck.service.product.ProductSkuService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartServiceImpl implements CartService {
    CartRepository cartRepository;
    CartItemRepository cartItemRepository;
    UserRepository userRepository;
    ProductRepository productRepository;
    ModelMapper modelMapper;
    MessageSource messageSource;
    ProductSkuService skuService;

    /**
     * Retrieves the cart associated with the provided username.
     * If no cart is found, a new one is created for the user.
     *
     * @param username The username of the user.
     * @return The associated cart.
     */
    @Override
    public CartResponse getCartByUsername(String username) {
        Cart cart = getCartByUsernameOrElseCreateNew(username);
        return modelMapper.map(cart, CartResponse.class);
    }

    @Override
    public Cart findByUsername(String username) {
        return cartRepository.findByUser_Username(username).orElseThrow(
                () -> new ResourceNotFoundException(getMessage("cart-not-found"))
        );
    }

    @Override
    public CartResponse getCartById(Long cartId) {
        Cart cart = cartRepository
                .findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException(getMessage("cart-not-found")));

        return mapToResponse(cart);
    }

    /**
     * Adds an item to the cart identified by the given cart ID.
     * If the item has a SKU, it processes it with SKU, otherwise without SKU.
     *
     * @param username The username of current user
     * @param itemDTO  The item to be added to the cart.
     * @return The updated CartItemDTO.
     */
    @Override
    public CartItemResponse addToCart(String username, CartItemDTO itemDTO) {
        // TODO Handle vấn đề số lượng sản phẩm tồn tại trước khi thêm vào giỏ (quantityAvailable)
        Cart cart = getCartByUsernameOrElseCreateNew(username);
        Product product = getExistingProductById(itemDTO.getProductId());
        boolean hasSku = itemDTO.getValueNames() != null;
        if (hasSku) {
            return processCartItemWithSku(cart, product, itemDTO);
        } else {
            return processCartItemWithoutSku(cart, product, itemDTO);
        }
    }

    @Override
    @Transactional
    public CartResponse clearItems(String username) {
        Cart cart = getExistingCartByUsername(username);    // delete all items for of this cart
        cartItemRepository.deleteAll(cart.getCartItems());  // clear items
        cart.getCartItems().clear();    // save change
        Cart cartWithClearedItems = updateCartTotals(cart);
        return mapToResponse(cartWithClearedItems);
    }

    @Override
    @Transactional
    public CartResponse removeListItems(String username, List<Long> itemIds) {
        Cart cart = getExistingCartByUsername(username);
        List<CartItem> items = cartItemRepository.findByIdIn(itemIds);
        cartItemRepository.deleteAll(items);     // delete items
        cart.getCartItems().removeAll(items);       // clear items
        Cart cartWithRemovedItems = updateCartTotals(cart);     //save change
        return mapToResponse(cartWithRemovedItems);
    }

    @Override
    public Integer getTotalItems(Principal principal) {
        String username = principal.getName();
        Cart userCart = getExistingCartByUsername(username);
        int totalItems = userCart.getCartItems().size();
        return totalItems;
    }

    private Cart getExistingCartByUsername(String username) {
        return cartRepository.findByUser_Username(username)
                .orElseThrow(() -> new ResourceNotFoundException(getMessage("cart-not-found")));
    }

    private Cart getCartByUsernameOrElseCreateNew(String username) {
        Cart cart = cartRepository.findByUser_Username(username)
                .orElseGet(() -> {
                    User user = userRepository
                            .findByUsernameOrEmail(username, username)
                            .orElseThrow(() -> new ResourceNotFoundException(getMessage("user-not-found")));

                    Cart newCart = new Cart();
                    newCart.setTotalPrice(BigDecimal.valueOf(0));
                    newCart.setTotalItems(0);
                    newCart.setUser(user);

                    return cartRepository.save(newCart);
                });
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
        ProductSku sku = skuService.findByProductIdAndValueNames(product.getProductId(), itemDTO.getValueNames());
        Optional<CartItem> existingItem = getCartItemByCartProductAndSku(cart, product, sku);

        if (existingItem.isPresent()) {
            CartItem existingCartItem = existingItem.get();
            updateExistingCartItem(existingCartItem, sku, itemDTO.getQuantity());
            // update cart totals
            updateCartTotals(cart);
            return mapToItemResponse(cartItemRepository.save(existingCartItem));
        } else {
            CartItem newCartItem = createNewCartItemWithSku(cart, product, sku, itemDTO);
            // update cart totals
            updateCartTotals(cart);
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
            // update cart totals
            updateCartTotals(cart);
            return mapToItemResponse(cartItemRepository.save(existingCartItem));
        } else {
            CartItem newCartItem = createNewCartItemWithoutSku(cart, product, itemDTO);
            // update cart totals
            updateCartTotals(cart);
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
        cart.getCartItems().add(newCartItem);
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
        cart.getCartItems().add(newCartItem);
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

    @Override
    public Cart updateCartTotals(Cart cart) {
        int totalItems = cart.getCartItems().size();
        BigDecimal totalPrice = cart.getCartItems().stream()
                .map(CartItem::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalItems(totalItems);
        cart.setTotalPrice(totalPrice);
        return cartRepository.save(cart);
    }

    @Override
    public String getTotalPriceForYourCart(Principal principal) {
        String username = principal.getName();
        Cart cart = getExistingCartByUsername(username);
        BigDecimal totalPrice = cart.getTotalPrice();
        return totalPrice != null ? totalPrice.toString() : "0";
    }


    private Product getExistingProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(getMessage("product-not-found")));
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }

    private CartItemResponse mapToItemResponse(CartItem entity) {
        return modelMapper.map(entity, CartItemResponse.class);
    }

    private CartResponse mapToResponse(Cart entity) {
        return modelMapper.map(entity, CartResponse.class);
    }
}
