package com.marketrow.ecommerce.repository;

import com.marketrow.ecommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategorySlug(String slug);
    List<Product> findByFeaturedTrue();
    List<Product> findByNameContainingIgnoreCase(String keyword);
}
