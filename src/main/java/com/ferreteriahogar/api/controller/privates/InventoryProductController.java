package com.ferreteriahogar.api.controller.privates;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ferreteriahogar.api.model.InventoryProduct;
import com.ferreteriahogar.api.model.InventoryProductId;
import com.ferreteriahogar.api.service.InventoryProductService;

@RestController
@RequestMapping("/api/inventory-product")
public class InventoryProductController {

    @Autowired
    private InventoryProductService inventoryProductService;



    @GetMapping("/inventory/{inventoryCode}")
    public ResponseEntity<?> getItemsByInventory(@PathVariable String inventoryCode) {
        try {
            return ResponseEntity.ok(inventoryProductService.getByInventory(inventoryCode));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{inventoryCode}/{productCode}")
    public ResponseEntity<?> getOne(
            @PathVariable String inventoryCode,
            @PathVariable String productCode) {

        try {
            InventoryProductId id = new InventoryProductId(inventoryCode, productCode);
            return ResponseEntity.ok(inventoryProductService.getById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody InventoryProduct ip) {
        try {
            return ResponseEntity.ok(inventoryProductService.save(ip));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{inventoryCode}/scan/{productCode}/{qty}")
    public ResponseEntity<?> addByScan(
            @PathVariable String inventoryCode,
            @PathVariable String productCode,
            @PathVariable Integer qty) {

        try {
            return ResponseEntity.ok(
                inventoryProductService.addProductByScan(inventoryCode, productCode, qty)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PutMapping
    public ResponseEntity<?> update(@RequestBody InventoryProduct ip) {
        try {
            return ResponseEntity.ok(inventoryProductService.update(ip));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @DeleteMapping("/{inventoryCode}/{productCode}")
    public ResponseEntity<?> delete(
            @PathVariable String inventoryCode,
            @PathVariable String productCode) {

        try {
            InventoryProductId id = new InventoryProductId(inventoryCode, productCode);
            inventoryProductService.delete(id);
            return ResponseEntity.ok("Item eliminado correctamente.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
