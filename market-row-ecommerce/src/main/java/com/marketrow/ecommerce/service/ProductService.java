package com.marketrow.ecommerce.service;

import com.marketrow.ecommerce.model.Category;
import com.marketrow.ecommerce.model.Product;
import com.marketrow.ecommerce.repository.CategoryRepository;
import com.marketrow.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public ProductService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryBySlug(String slug) {
        return categoryRepository.findBySlug(slug);
    }

    public List<Product> getProductsByCategorySlug(String slug) {
        return productRepository.findByCategorySlug(slug);
    }

    public List<Product> getFeaturedProducts() {
        return productRepository.findByFeaturedTrue();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }
}
