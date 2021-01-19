package com.supercompany.subscriptionservice.service;

import com.supercompany.subscriptionservice.exception.ProductNotFoundException;
import com.supercompany.subscriptionservice.model.Product;
import com.supercompany.subscriptionservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * @throws ProductNotFoundException when the product with {id} missing.
     */
    public Product findById(final int productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }
}
