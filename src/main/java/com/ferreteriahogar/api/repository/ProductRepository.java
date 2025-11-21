package com.ferreteriahogar.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ferreteriahogar.api.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    List<Product> findAll();

    Product findByCode(String code);
}
