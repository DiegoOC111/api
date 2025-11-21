package com.ferreteriahogar.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ferreteriahogar.api.model.Inventory;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, String> {

}
