package com.ferreteriahogar.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ferreteriahogar.api.model.Product;
import com.ferreteriahogar.api.repository.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public Product getById(String code) {
        return productRepository.findById(code)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado."));
    }
  
    public Product create(Product p) {

        validateForCreate(p);

        return productRepository.save(p);
    }

    public Product update(Product p) {

        if (p.getCode() == null || p.getCode().isBlank()) {
            throw new IllegalArgumentException("El código es obligatorio para actualizar.");
        }

        Product existing = getById(p.getCode());

        if (p.getName() != null && !p.getName().isBlank()) {
            existing.setName(p.getName());
        }

        if (p.getDescription() != null) {
            existing.setDescription(p.getDescription());
        }

        return productRepository.save(existing);
    }

    public void delete(String code) {
        if (!productRepository.existsById(code)) {
            throw new IllegalArgumentException("Producto no encontrado.");
        }
        productRepository.deleteById(code);
    }

    private void validateForCreate(Product p) {

        if (p == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo.");
        }

        if (p.getCode() == null || p.getCode().isBlank()) {
            throw new IllegalArgumentException("El código es obligatorio.");
        }

        if (productRepository.existsById(p.getCode())) {
            throw new IllegalArgumentException("Ya existe un producto con ese código.");
        }

        if (p.getName() == null || p.getName().isBlank()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        }
    }
}
