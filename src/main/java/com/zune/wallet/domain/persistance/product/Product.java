package com.zune.wallet.domain.persistance.product;

import com.zune.wallet.domain.common.model.BaseTimeEntity;
import com.zune.wallet.domain.persistance.product.type.ProductType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "product", schema = "wallet")
public class Product extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductType productType;

    @Builder
    public Product(String name, Double price, ProductType productType) {
        this.name = name;
        this.price = price;
        this.productType = productType;
    }
}
