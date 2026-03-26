package service;

import controller.GlobalExceptionHandler;
import entity.ProductEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import repository.ProductRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public ProductEntity getProductOrFail(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new GlobalExceptionHandler.ResourceNotFoundException("Product not found with id: " + productId));
    }
}