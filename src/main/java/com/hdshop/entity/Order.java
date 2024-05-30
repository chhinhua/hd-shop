package com.hdshop.entity;

import com.hdshop.utils.EnumOrderStatus;
import com.hdshop.utils.EnumPaymentType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Table(name = "orders")
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String orderCode;

    String vnpTxnRef;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    EnumOrderStatus status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    EnumPaymentType paymentType;

    Integer totalItems;

    BigDecimal subTotal;

    BigDecimal shippingFee;

    BigDecimal total;

    String note;

    Boolean isPaidBefore;

    Boolean isDeleted;

    @CreatedBy
    String createdBy;

    @LastModifiedBy
    String lastModifiedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    Address address;

    @OneToMany(mappedBy = "order")
    List<Review> reviews = new ArrayList<>();

    @OneToMany(
            mappedBy = "order",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE, CascadeType.DETACH}
    )
    List<OrderItem> orderItems = new ArrayList<>();
}
