package com.ferreteriahogar.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ferreteriahogar.api.model.InventoryProduct;
import com.ferreteriahogar.api.model.InventoryProductId;
import java.util.List;

@Repository
public interface InventoryProductRepository extends JpaRepository<InventoryProduct, InventoryProductId> {

    List<InventoryProduct> findByIdInventoryCode(String inventorCode);

    List<InventoryProduct> findByIdProductCode(String productCode);

    boolean existsById(InventoryProductId id);
}

