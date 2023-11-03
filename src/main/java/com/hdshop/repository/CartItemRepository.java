package com.hdshop.repository;

import com.hdshop.entity.Cart;
import com.hdshop.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCart(Cart cart);

    Optional<CartItem> findByCart_IdAndProduct_ProductId(Long cartId, Long productId);

    Optional<CartItem> findByCart_IdAndProduct_ProductIdAndSku_SkuId(Long cartId, Long productId, Long skuId);
}
