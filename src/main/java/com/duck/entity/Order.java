package com.duck.entity;

import com.duck.listener.EntityListener;
import com.duck.utils.EOrderStatus;
import com.duck.utils.EPaymentType;
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
@Builder
@Entity
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
@EntityListeners(EntityListener.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String orderCode;

    String vnpTxnRef;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    EOrderStatus status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    EPaymentType paymentType;

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

    @OneToMany(mappedBy = "order", cascade = {
            CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE, CascadeType.DETACH
    })
    List<OrderItem> orderItems = new ArrayList<>();
}

