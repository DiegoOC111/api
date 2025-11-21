package com.ferreteriahogar.api.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ferreteriahogar.api.model.Inventory;
import com.ferreteriahogar.api.model.InventoryProduct;
import com.ferreteriahogar.api.model.InventoryProductId;
import com.ferreteriahogar.api.model.Product;
import com.ferreteriahogar.api.repository.InventoryProductRepository;
import com.ferreteriahogar.api.repository.InventoryRepository;
import com.ferreteriahogar.api.repository.ProductRepository;

@Service
public class InventoryProductService {

    @Autowired
    private InventoryProductRepository invProdRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ProductRepository productRepository;

    

    public List<InventoryProduct> getByInventory(String inventoryCode) {
        return invProdRepository.findByIdInventoryCode(inventoryCode);
    }

    public InventoryProduct getById(InventoryProductId id) {
        return invProdRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item de inventario no encontrado."));
    }

    // public InventoryProduct save(InventoryProduct ip) {
    //     validate(ip);
    
    //     // Si ya existe → actualizar stock sumando
    //     if (invProdRepository.existsById(ip.getId())) {
    //         InventoryProduct existing = invProdRepository.findById(ip.getId()).get();
    //         existing.setStock(existing.getStock() + ip.getStock());
    //         existing.setMinStock(ip.getMinStock());
    //         return invProdRepository.save(existing);
    //     }

    //     return invProdRepository.save(ip);
    // }

    public InventoryProduct save(InventoryProduct ip) {
        validate(ip);

        // Evita doble query
        InventoryProduct existing = invProdRepository.findById(ip.getId()).orElse(null);

        if (existing != null) {
            existing.setStock(existing.getStock() + ip.getStock());
            existing.setMinStock(ip.getMinStock());
            return invProdRepository.save(existing);
        }

        return invProdRepository.save(ip);
    }

    public InventoryProduct addProductByScan(String inventoryCode, String productCode, Integer qty) {

        Inventory inventory = inventoryRepository.findById(inventoryCode)
                .orElseThrow(() -> new IllegalArgumentException("Inventario no encontrado"));

        Product product = productRepository.findById(productCode)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        InventoryProductId id = new InventoryProductId(inventoryCode, productCode);

        InventoryProduct existing = invProdRepository.findById(id).orElse(null);

        if (existing != null) {
            existing.setStock(existing.getStock() + qty);
            return invProdRepository.save(existing);
        }

        InventoryProduct ip = new InventoryProduct();
        ip.setId(id);
        ip.setInventory(inventory);
        ip.setProduct(product);
        ip.setStock(qty);
        ip.setMinStock(0);

        return invProdRepository.save(ip);
    }

    public InventoryProduct update(InventoryProduct ip) {

        if (ip.getId() == null ||
            ip.getId().getInventoryCode() == null ||
            ip.getId().getProductCode() == null) {
            throw new IllegalArgumentException("El ID compuesto es obligatorio para actualizar.");
        }

        if (!invProdRepository.existsById(ip.getId())) {
            throw new IllegalArgumentException("No se puede actualizar: el item no existe.");
        }

        validate(ip);
        return invProdRepository.save(ip);
    }

    public void delete(InventoryProductId id) {
        if (!invProdRepository.existsById(id)) {
            throw new IllegalArgumentException("Item de inventario no encontrado.");
        }
        invProdRepository.deleteById(id);
    }

    private void validate(InventoryProduct ip) {

        if (ip == null) {
            throw new IllegalArgumentException("El item de inventario no puede ser nulo.");
        }

        if (ip.getId() == null ||
            ip.getId().getInventoryCode() == null ||
            ip.getId().getInventoryCode().trim().isEmpty() ||
            ip.getId().getProductCode() == null ||
            ip.getId().getProductCode().trim().isEmpty()) {

            throw new IllegalArgumentException("El código de inventario y producto son obligatorios.");
        }

        if (ip.getStock() == null || ip.getStock() < 0) {
            throw new IllegalArgumentException("El stock debe ser un número válido.");
        }

        if (ip.getMinStock() == null || ip.getMinStock() < 0) {
            throw new IllegalArgumentException("El stock mínimo debe ser un número válido.");
        }

        // Validar INVENTARIO
        Inventory inventory = inventoryRepository.findById(ip.getId().getInventoryCode())
                .orElseThrow(() -> new IllegalArgumentException("El inventario asignado no existe."));

        // Validar PRODUCTO
        Product product = productRepository.findById(ip.getId().getProductCode())
                .orElseThrow(() -> new IllegalArgumentException("El producto asignado no existe."));

        ip.setInventory(inventory);
        ip.setProduct(product);
    }
}
