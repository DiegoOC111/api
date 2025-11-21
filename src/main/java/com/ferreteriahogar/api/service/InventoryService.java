package com.ferreteriahogar.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ferreteriahogar.api.controller.dto.InventoryFullDTO;
import com.ferreteriahogar.api.controller.dto.ProductInInventoryDTO;
import com.ferreteriahogar.api.model.Inventory;
import com.ferreteriahogar.api.model.InventoryProduct;
import com.ferreteriahogar.api.model.User;
import com.ferreteriahogar.api.repository.InventoryProductRepository;
import com.ferreteriahogar.api.repository.InventoryRepository;
import com.ferreteriahogar.api.repository.UserRespository;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private InventoryProductRepository invProdRepository;

    @Autowired
    private UserRespository userRepository;

    public List<Inventory> getAllInventory(){
        return inventoryRepository.findAll();
    }

    public Inventory getInventoryById(String code){
        return inventoryRepository.findById(code)
                .orElseThrow(() -> new IllegalArgumentException("Inventario no encontrado"));
    }

    public InventoryFullDTO getInventoryFull(String inventoryCode) {

        Inventory inventory = inventoryRepository.findById(inventoryCode)
                .orElseThrow(() -> new IllegalArgumentException("Inventario no encontrado"));

        List<InventoryProduct> items = invProdRepository.findByIdInventoryCode(inventoryCode);

        int totalItems = items.size();

        int totalStock = items.stream()
                .mapToInt(InventoryProduct::getStock)
                .sum();

        List<ProductInInventoryDTO> productList = items.stream()
                .map(ip -> new ProductInInventoryDTO(
                        ip.getProduct().getCode(),
                        ip.getProduct().getName(),
                        ip.getStock(),
                        ip.getMinStock()
                ))
                .toList();

        return new InventoryFullDTO(
                new InventoryFullDTO.InventoryInfo(
                        inventory.getCode(),
                        inventory.getName(),
                        inventory.getStatus(),
                        inventory.getUser().getUsername()
                ),
                totalItems,
                totalStock,
                productList
        );
    }

    public Inventory saveInventory(Inventory i){
        validateInventory(i);
        return inventoryRepository.save(i);
    }

    public Inventory updateInventory(Inventory i){
        validateInventoryForUpdate(i);
        return inventoryRepository.save(i);
    }

    public void deleteInventory(String code){
        if(!inventoryRepository.existsById(code)){
            throw new IllegalArgumentException("Inventario no encontrado");
        }

        inventoryRepository.deleteById(code);
    }

    private void validateInventory(Inventory i){
        if (i == null){
            throw new IllegalArgumentException("El inventario no puede ser nulo.");
        }

        if (i.getName() == null || i.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del inventario es obligatorio.");
        }

        if (i.getStatus() == null || i.getStatus().trim().isEmpty()) {
            throw new IllegalArgumentException("El estado del inventario es obligatorio.");
        }

        if (i.getUser() == null || i.getUser().getUsername() == null) {
            throw new IllegalArgumentException("El inventario debe tener un usuario asignado.");
        }

        User user = userRepository.findByUsername(i.getUser().getUsername());
        if (user == null){
            throw new IllegalArgumentException("El usuario asignado no existe.");
        }

        i.setUser(user);
    }

    private void validateInventoryForUpdate(Inventory i){
        if (i.getCode() == null) {
            throw new IllegalArgumentException("El ID del inventario es obligatorio para actualizar.");
        }
        validateInventory(i);
    }
}
